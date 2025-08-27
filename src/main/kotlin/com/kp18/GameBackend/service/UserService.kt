package com.kp18.GameBackend.service

import com.kp18.GameBackend.dto.CreateUserDto
import com.kp18.GameBackend.dto.UserDto
import com.kp18.GameBackend.dto.toDto
import com.kp18.GameBackend.dto.toEntity
import com.kp18.GameBackend.entity.User
import com.kp18.GameBackend.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserDto {
        logger.info("Fetching user with id: {}", id)
        val user = userRepository.findById(id)
            .orElseThrow { 
                logger.warn("User not found with id: {}", id)
                EntityNotFoundException("User not found with id: $id") 
            }
        return user.toDto()
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserDto> {
        logger.info("Fetching all users")
        return userRepository.findAll().map { it.toDto() }
    }

    fun createUser(createUserDto: CreateUserDto): UserDto {
        logger.info("Creating new user with email: {}", createUserDto.email)
        
        // Check if email already exists
        if (userRepository.existsByEmail(createUserDto.email)) {
            logger.warn("User with email {} already exists", createUserDto.email)
            throw IllegalArgumentException("User with email ${createUserDto.email} already exists")
        }
        
        // Check if in-game name already exists
        if (userRepository.existsByInGameName(createUserDto.inGameName)) {
            logger.warn("User with in-game name {} already exists", createUserDto.inGameName)
            throw IllegalArgumentException("User with in-game name ${createUserDto.inGameName} already exists")
        }

        val user = createUserDto.toEntity()
        val savedUser = userRepository.save(user)
        logger.info("Successfully created user with id: {}", savedUser.id)
        return savedUser.toDto()
    }

    fun deleteUser(id: Long) {
        logger.info("Deleting user with id: {}", id)
        if (!userRepository.existsById(id)) {
            logger.warn("User not found with id: {}", id)
            throw EntityNotFoundException("User not found with id: $id")
        }
        userRepository.deleteById(id)
        logger.info("Successfully deleted user with id: {}", id)
    }
}
