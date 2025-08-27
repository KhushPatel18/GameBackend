package com.kp18.GameBackend.dto

import java.time.LocalDateTime

data class GameStateDto(
    val gameId: String,
    val currentPlayer: String,
    val gamePhase: String,
    val playerCount: Int,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)

data class GameStateRequest(
    val gameId: String
)

data class GameStateResponse(
    val success: Boolean,
    val gameState: GameStateDto?,
    val message: String
)
