package com.kp18.GameBackend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping("/ping")
    fun ping(): Map<String, String> {
        return mapOf(
            "message" to "pong",
            "status" to "OK",
            "timestamp" to java.time.LocalDateTime.now().toString()
        )
    }
}
