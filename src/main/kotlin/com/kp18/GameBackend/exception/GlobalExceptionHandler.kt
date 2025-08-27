package com.kp18.GameBackend.exception

import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(
        ex: EntityNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Entity not found: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "Entity not found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid argument: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid argument provided",
            path = request.getDescription(false).removePrefix("uri=")
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Validation failed: {}", ex.message)
        
        val fieldErrors = ex.bindingResult.fieldErrors
        val errorMessage = if (fieldErrors.isNotEmpty()) {
            fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        } else {
            "Validation failed"
        }
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Failed",
            message = errorMessage,
            path = request.getDescription(false).removePrefix("uri=")
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Constraint violation: {}", ex.message)
        
        val errorMessage = ex.constraintViolations
            .joinToString(", ") { "${it.propertyPath}: ${it.message}" }
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Constraint Violation",
            message = errorMessage,
            path = request.getDescription(false).removePrefix("uri=")
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error occurred. Please try again later.",
            path = request.getDescription(false).removePrefix("uri=")
        )
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
