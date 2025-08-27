package com.kp18.GameBackend.repository

import com.kp18.GameBackend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun findByInGameName(inGameName: String): Optional<User>
    fun existsByEmail(email: String): Boolean
    fun existsByInGameName(inGameName: String): Boolean
}
