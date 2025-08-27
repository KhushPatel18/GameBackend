package com.kp18.GameBackend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.SerializationFeature
import com.kp18.GameBackend.dto.GameStateDto
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class GameStateService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(GameStateService::class.java)

    // Configure ObjectMapper with JSR310 module for LocalDateTime support
    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    companion object {
        private const val GAME_STATE_PREFIX = "game:state:"
        private val DEFAULT_TTL = Duration.ofHours(2) // 2 hours TTL
    }

    fun saveGameState(gameState: GameStateDto): Boolean {
        return try {
            val key = GAME_STATE_PREFIX + gameState.gameId
            logger.info("Saving game state for gameId: {}", gameState.gameId)

            // Serialize to JSON string to avoid LocalDateTime serialization issues
            val gameStateJson = objectMapper.writeValueAsString(gameState)
            redisTemplate.opsForValue().set(key, gameStateJson, DEFAULT_TTL)
            logger.info("Successfully saved game state for gameId: {}", gameState.gameId)
            true
        } catch (e: Exception) {
            logger.error("Failed to save game state for gameId: {}", gameState.gameId, e)
            false
        }
    }

    fun getGameState(gameId: String): GameStateDto? {
        return try {
            val key = GAME_STATE_PREFIX + gameId
            logger.info("Retrieving game state for gameId: {}", gameId)

            val gameStateJson = redisTemplate.opsForValue().get(key) as? String
            if (gameStateJson != null) {
                val gameState = objectMapper.readValue(gameStateJson, GameStateDto::class.java)
                logger.info("Successfully retrieved game state for gameId: {}", gameId)
                gameState
            } else {
                logger.warn("No game state found for gameId: {}", gameId)
                null
            }
        } catch (e: Exception) {
            logger.error("Failed to retrieve game state for gameId: {}", gameId, e)
            null
        }
    }

    fun deleteGameState(gameId: String): Boolean {
        return try {
            val key = GAME_STATE_PREFIX + gameId
            logger.info("Deleting game state for gameId: {}", gameId)

            val deleted = redisTemplate.delete(key)
            if (deleted) {
                logger.info("Successfully deleted game state for gameId: {}", gameId)
            } else {
                logger.warn("No game state found to delete for gameId: {}", gameId)
            }
            deleted
        } catch (e: Exception) {
            logger.error("Failed to delete game state for gameId: {}", gameId, e)
            false
        }
    }

    fun createDummyGameState(gameId: String): GameStateDto {
        logger.info("Creating dummy game state for gameId: {}", gameId)
        val dummyState = GameStateDto(
            gameId = gameId,
            currentPlayer = "player_1",
            gamePhase = "WAITING_FOR_PLAYERS",
            playerCount = 1,
            lastUpdated = LocalDateTime.now()
        )
        saveGameState(dummyState)
        return dummyState
    }

    fun updateGameState(gameId: String, updater: (GameStateDto?) -> GameStateDto): GameStateDto? {
        return try {
            val currentState = getGameState(gameId)
            val newState = updater(currentState)

            if (saveGameState(newState)) {
                logger.info("Successfully updated game state for gameId: {}", gameId)
                newState
            } else {
                logger.error("Failed to save updated game state for gameId: {}", gameId)
                null
            }
        } catch (e: Exception) {
            logger.error("Failed to update game state for gameId: {}", gameId, e)
            null
        }
    }

    fun isRedisAvailable(): Boolean {
        return try {
            redisTemplate.connectionFactory?.connection?.ping()
            true
        } catch (e: Exception) {
            logger.error("Redis health check failed", e)
            false
        }
    }
}