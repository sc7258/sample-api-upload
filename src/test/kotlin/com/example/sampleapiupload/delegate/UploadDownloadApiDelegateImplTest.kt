package com.example.sampleapiupload.delegate

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ContentDisposition
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@SpringBootTest
@AutoConfigureMockMvc
class UploadDownloadApiDelegateImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        private val UPLOAD_DIR: Path = Paths.get("./uploads").toAbsolutePath().normalize()

        @BeforeAll
        @JvmStatic
        fun setup() {
            // 테스트 시작 전 uploads 폴더 생성
            if (!Files.exists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR)
            }
        }

        @AfterAll
        @JvmStatic
        fun cleanup() {
            // 모든 테스트 종료 후 uploads 폴더 및 내용 삭제
            try {
                Files.walk(UPLOAD_DIR)
                    .sorted(Comparator.reverseOrder())
                    .forEach(Files::delete)
            } catch (e: Exception) {
                // 테스트 환경에서 폴더가 이미 삭제되었거나 할 수 있으므로 예외 처리
            }
        }
    }

    @Test
    fun `파일 업로드 성공 케이스`() {
        val mockFile = MockMultipartFile(
            "file",
            "test-file.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".toByteArray()
        )

        val result = mockMvc.perform(multipart("/upload").file(mockFile))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.fileName").exists())
            .andExpect(jsonPath("$.fileSize").value(mockFile.size.toLong()))
            .andReturn()

        val responseString = result.response.contentAsString
        val fileName = responseString.substringAfter("\"fileName\":\"").substringBefore("\"")

        // 파일이 실제로 uploads 폴더에 저장되었는지 확인
        val storedFile = UPLOAD_DIR.resolve(fileName)
        assertTrue(Files.exists(storedFile))
        assertTrue(Files.size(storedFile) > 0)
    }

    @Test
    fun `파일 없이 업로드 요청 시 400 에러`() {
        mockMvc.perform(multipart("/upload")) // 파일 없이 요청
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `파일 다운로드 성공 케이스`() {
        // 먼저 테스트용 파일을 업로드
        val originalFileName = "test-file.txt" // 한글이 아닌 파일명으로 변경하여 인코딩 문제를 단순화
        val mockFile = MockMultipartFile("file", originalFileName, MediaType.TEXT_PLAIN_VALUE, "테스트 내용".toByteArray())
        val uploadResult = mockMvc.perform(multipart("/upload").file(mockFile)).andReturn()
        val storedFileName = uploadResult.response.contentAsString.substringAfter("\"fileName\":\"").substringBefore("\"")

        // 예상되는 Content-Disposition 헤더 값을 서버와 동일한 방식으로 생성
        val expectedContentDisposition = ContentDisposition.builder("attachment")
            .filename(storedFileName, StandardCharsets.UTF_8)
            .build()
            .toString()

        // 업로드된 파일 다운로드
        mockMvc.perform(get("/download/{fileName}", storedFileName))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Disposition", expectedContentDisposition))
            .andExpect(content().bytes("테스트 내용".toByteArray()))
    }

    @Test
    fun `존재하지 않는 파일 다운로드 시 404 에러`() {
        mockMvc.perform(get("/download/{fileName}", "non-existent-file.txt"))
            .andExpect(status().isNotFound)
    }
    
    @Test
    fun `경로 조작 문자로 다운로드 요청 시 404 에러`() {
        // 경로 조작 시도는 보통 서비스 로직에서 파일명을 찾지 못해 404로 귀결됨
        mockMvc.perform(get("/download/{fileName}", "../../../etc/passwd"))
            .andExpect(status().isNotFound)
    }
}
