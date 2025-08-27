package com.kp18.GameBackend.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @Column(nullable = false, unique = true)
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email must be valid")
    @field:Size(max = 255, message = "Email must not exceed 255 characters")
    val email: String,

    @Column(nullable = false, name = "in_game_name")
    @field:NotBlank(message = "In-game name cannot be blank")
    @field:Size(min = 2, max = 50, message = "In-game name must be between 2 and 50 characters")
    val inGameName: String,

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
