package com.kp18.GameBackend.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // Enable a simple memory-based message broker to carry messages back to the client
        config.enableSimpleBroker("/topic", "/queue")
        
        // Set prefix for messages bound for @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app")
        
        // Set prefix for user-specific destinations
        config.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // Register STOMP endpoint with SockJS fallback
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*") // Configure CORS as needed
            .withSockJS()
    }
}
