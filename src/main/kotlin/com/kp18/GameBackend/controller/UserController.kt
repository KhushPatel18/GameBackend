package com.kp18.GameBackend.controller

import com.kp18.GameBackend.dto.CreateUserDto
import com.kp18.GameBackend.dto.UserDto
import com.kp18.GameBackend.service.UserService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDto> {
        logger.info("GET request for user with id: {}", id)
        val user = userService.getUserById(id)
        return ResponseEntity.ok(user)
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        logger.info("GET request for all users")
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @PostMapping
    fun createUser(@Valid @RequestBody createUserDto: CreateUserDto): ResponseEntity<UserDto> {
        logger.info("POST request to create user with email: {}", createUserDto.email)
        val user = userService.createUser(createUserDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info("DELETE request for user with id: {}", id)
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
