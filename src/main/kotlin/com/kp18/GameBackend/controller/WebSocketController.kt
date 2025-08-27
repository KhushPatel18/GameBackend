package com.kp18.GameBackend.controller

import com.kp18.GameBackend.dto.GameStateDto
import com.kp18.GameBackend.dto.GameStateRequest
import com.kp18.GameBackend.dto.GameStateResponse
import com.kp18.GameBackend.service.GameStateService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class WebSocketController(
    private val gameStateService: GameStateService,
    private val messagingTemplate: SimpMessagingTemplate
) {
    private val logger = LoggerFactory.getLogger(WebSocketController::class.java)

    @MessageMapping("/get_state")
    @SendToUser("/queue/game_state")
    fun handleGetState(@Payload request: GameStateRequest, principal: Principal?): GameStateResponse {
        logger.info("Received get_state request for gameId: {}", request.gameId)
        
        return try {
            var gameState = gameStateService.getGameState(request.gameId)
            
            // If no game state exists, create a dummy one
            if (gameState == null) {
                logger.info("No existing game state found for gameId: {}, creating dummy state", request.gameId)
                gameState = gameStateService.createDummyGameState(request.gameId)
            }
            
            GameStateResponse(
                success = true,
                gameState = gameState,
                message = "Game state retrieved successfully"
            )
        } catch (e: Exception) {
            logger.error("Error retrieving game state for gameId: {}", request.gameId, e)
            GameStateResponse(
                success = false,
                gameState = null,
                message = "Failed to retrieve game state: ${e.message}"
            )
        }
    }

    @MessageMapping("/join_game")
    @SendTo("/topic/game_updates")
    fun handleJoinGame(@Payload request: GameStateRequest, principal: Principal?): GameStateResponse {
        logger.info("Player joining game: {}", request.gameId)
        
        return try {
            val updatedState = gameStateService.updateGameState(request.gameId) { currentState ->
                val playerCount = (currentState?.playerCount ?: 0) + 1
                val gamePhase = if (playerCount >= 2) "READY_TO_START" else "WAITING_FOR_PLAYERS"
                
                GameStateDto(
                    gameId = request.gameId,
                    currentPlayer = currentState?.currentPlayer ?: "player_1",
                    gamePhase = gamePhase,
                    playerCount = playerCount
                )
            }
            
            if (updatedState != null) {
                GameStateResponse(
                    success = true,
                    gameState = updatedState,
                    message = "Successfully joined game"
                )
            } else {
                GameStateResponse(
                    success = false,
                    gameState = null,
                    message = "Failed to join game"
                )
            }
        } catch (e: Exception) {
            logger.error("Error joining game: {}", request.gameId, e)
            GameStateResponse(
                success = false,
                gameState = null,
                message = "Failed to join game: ${e.message}"
            )
        }
    }

    @MessageMapping("/leave_game")
    @SendTo("/topic/game_updates")
    fun handleLeaveGame(@Payload request: GameStateRequest, principal: Principal?): GameStateResponse {
        logger.info("Player leaving game: {}", request.gameId)
        
        return try {
            val updatedState = gameStateService.updateGameState(request.gameId) { currentState ->
                val playerCount = maxOf(0, (currentState?.playerCount ?: 1) - 1)
                val gamePhase = if (playerCount == 0) "ENDED" else "WAITING_FOR_PLAYERS"
                
                GameStateDto(
                    gameId = request.gameId,
                    currentPlayer = currentState?.currentPlayer ?: "player_1",
                    gamePhase = gamePhase,
                    playerCount = playerCount
                )
            }
            
            if (updatedState != null) {
                GameStateResponse(
                    success = true,
                    gameState = updatedState,
                    message = "Successfully left game"
                )
            } else {
                GameStateResponse(
                    success = false,
                    gameState = null,
                    message = "Failed to leave game"
                )
            }
        } catch (e: Exception) {
            logger.error("Error leaving game: {}", request.gameId, e)
            GameStateResponse(
                success = false,
                gameState = null,
                message = "Failed to leave game: ${e.message}"
            )
        }
    }

    // Utility method to broadcast game state updates to all subscribers
    fun broadcastGameStateUpdate(gameId: String, gameState: GameStateDto) {
        logger.info("Broadcasting game state update for gameId: {}", gameId)
        messagingTemplate.convertAndSend("/topic/game_updates", GameStateResponse(
            success = true,
            gameState = gameState,
            message = "Game state updated"
        ))
    }
}
