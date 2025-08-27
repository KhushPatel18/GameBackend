package com.kp18.GameBackend.config

import com.kp18.GameBackend.entity.User
import com.kp18.GameBackend.repository.UserRepository
import com.kp18.GameBackend.service.GameStateService
import com.kp18.GameBackend.dto.GameStateDto
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val gameStateService: GameStateService
) : ApplicationRunner {
    
    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(args: ApplicationArguments?) {
        logger.info("Initializing sample data...")
        
        createSampleUsers()
        createSampleGameStates()
        
        logger.info("Sample data initialization completed")
    }

    private fun createSampleUsers() {
        if (userRepository.count() == 0L) {
            logger.info("Creating sample users...")
            
            val sampleUsers = listOf(
                User(
                    name = "John Doe",
                    email = "john.doe@example.com",
                    inGameName = "JohnTheCardMaster"
                ),
                User(
                    name = "Jane Smith",
                    email = "jane.smith@example.com",
                    inGameName = "JaneAce"
                ),
                User(
                    name = "Bob Johnson",
                    email = "bob.johnson@example.com",
                    inGameName = "BobTheBuilder"
                ),
                User(
                    name = "Alice Williams",
                    email = "alice.williams@example.com",
                    inGameName = "AliceInWonderland"
                )
            )
            
            userRepository.saveAll(sampleUsers)
            logger.info("Created {} sample users", sampleUsers.size)
        } else {
            logger.info("Users already exist, skipping sample user creation")
        }
    }

    private fun createSampleGameStates() {
        logger.info("Creating sample game states...")
        
        val sampleGameStates = listOf(
            GameStateDto(
                gameId = "game-001",
                currentPlayer = "JohnTheCardMaster",
                gamePhase = "IN_PROGRESS",
                playerCount = 2
            ),
            GameStateDto(
                gameId = "game-002",
                currentPlayer = "JaneAce",
                gamePhase = "WAITING_FOR_PLAYERS",
                playerCount = 1
            ),
            GameStateDto(
                gameId = "game-003",
                currentPlayer = "BobTheBuilder",
                gamePhase = "READY_TO_START",
                playerCount = 4
            )
        )
        
        sampleGameStates.forEach { gameState ->
            gameStateService.saveGameState(gameState)
            logger.info("Created sample game state for gameId: {}", gameState.gameId)
        }
    }
}
