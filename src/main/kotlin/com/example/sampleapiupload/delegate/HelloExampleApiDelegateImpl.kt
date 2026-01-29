package com.example.sampleapiupload.delegate

import com.example.sampleapiupload.api.HelloExampleApiDelegate
import com.example.sampleapiupload.model.Hello
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class HelloApiDelegateImpl : HelloExampleApiDelegate {

    override fun getHello(): ResponseEntity<Hello> {
        val response = Hello(message = "Hello, World!")
        return ResponseEntity.ok(response)
    }
}
