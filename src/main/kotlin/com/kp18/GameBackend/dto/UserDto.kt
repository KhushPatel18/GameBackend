package com.kp18.GameBackend.dto

import com.kp18.GameBackend.entity.User
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val inGameName: String,
    val createdAt: LocalDateTime
)

data class CreateUserDto(
    val name: String,
    val email: String,
    val inGameName: String
)

// Extension function to convert User entity to UserDto
fun User.toDto(): UserDto = UserDto(
    id = this.id,
    name = this.name,
    email = this.email,
    inGameName = this.inGameName,
    createdAt = this.createdAt
)

// Extension function to convert CreateUserDto to User entity
fun CreateUserDto.toEntity(): User = User(
    name = this.name,
    email = this.email,
    inGameName = this.inGameName
)
