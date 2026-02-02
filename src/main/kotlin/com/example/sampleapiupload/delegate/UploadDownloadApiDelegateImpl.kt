package com.example.sampleapiupload.delegate

import com.example.sampleapiupload.api.UploadDownloadApiDelegate
import com.example.sampleapiupload.model.FileUploadResponse
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class UploadDownloadApiDelegateImpl : UploadDownloadApiDelegate {

    // 실제 파일 저장 경로 (임시로 프로젝트 루트에 'uploads' 폴더 사용)
    private val UPLOAD_DIR = Paths.get("./uploads").toAbsolutePath().normalize()

    init {
        // uploads 디렉토리가 없으면 생성
        if (!Files.exists(UPLOAD_DIR)) {
            Files.createDirectories(UPLOAD_DIR)
        }
    }

    override fun uploadFile(file: Resource?): ResponseEntity<FileUploadResponse> {
        if (file == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        try {
            val originalFileName = file.filename ?: "unknown_file"
            val fileName = UUID.randomUUID().toString() + "_" + originalFileName
            val targetLocation = UPLOAD_DIR.resolve(fileName)

            // Resource의 InputStream을 사용하여 파일 저장
            file.inputStream.use { input ->
                Files.copy(input, targetLocation)
            }

            val fileSize = Files.size(targetLocation)
            val response = FileUploadResponse(fileName = fileName, fileSize = fileSize)
            return ResponseEntity.ok(response)
        } catch (ex: Exception) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    override fun downloadFile(fileName: String): ResponseEntity<Resource> {
        try {
            val filePath = UPLOAD_DIR.resolve(fileName).normalize()
            if (!Files.exists(filePath)) {
                return ResponseEntity(HttpStatus.NOT_FOUND)
            }

            val resource: Resource = org.springframework.core.io.UrlResource(filePath.toUri())

            if (resource.exists() && resource.isReadable) {
                val contentType = Files.probeContentType(filePath) ?: "application/octet-stream"

                // ★★★ 한글 파일 이름 인코딩 문제 해결 ★★★
                // 참고: DB 연동 없이는 원본 파일명을 알 수 없으므로, 저장된 파일명(UUID 포함)으로 다운로드됩니다.
                val contentDisposition = ContentDisposition.builder("attachment")
                    .filename(resource.filename!!, StandardCharsets.UTF_8)
                    .build()

                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .body(resource)
            } else {
                return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        } catch (ex: Exception) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
