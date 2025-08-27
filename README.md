# Card Game Backend - Spring Boot + Kotlin

A comprehensive Spring Boot backend for a card game application with WebSocket support, Redis caching, and PostgreSQL database.

## Features

## Key Skills to go to next level
- Debugging
- Reading code
- Readability of code
- Full stack knowledge
- Focus on Fundamentals
- **Talk well non-technically**
- Command line scripting (productivity hacks)
- Technical documentation
- GIT


- **User Management**: REST API for user CRUD operations
- **WebSocket Support**: Real-time game state updates using STOMP
- **Redis Caching**: Game state storage with TTL
- **PostgreSQL Database**: Persistent user data storage
- **Health Monitoring**: Custom health indicators for database and Redis
- **Global Exception Handling**: Consistent error responses
- **Sample Data**: Automatic initialization of test data

## Tech Stack

- **Spring Boot 3.5.5**
- **Kotlin 1.9.25**
- **PostgreSQL** (Database)
- **Redis** (Cache)
- **WebSocket + STOMP** (Real-time communication)
- **Spring Data JPA** (ORM)
- **Spring Boot Actuator** (Monitoring)

## Project Structure

```
src/main/kotlin/com/kp18/GameBackend/
├── GameBackendApplication.kt
├── config/
│   ├── DataInitializer.kt      # Sample data creation
│   ├── HealthConfig.kt         # Custom health indicators
│   ├── RedisConfig.kt          # Redis configuration
│   └── WebSocketConfig.kt      # WebSocket configuration
├── controller/
│   ├── TestController.kt       # Test endpoints
│   ├── UserController.kt       # User REST API
│   └── WebSocketController.kt  # WebSocket handlers
├── dto/
│   ├── GameStateDto.kt         # Game state DTOs
│   └── UserDto.kt              # User DTOs
├── entity/
│   └── User.kt                 # User JPA entity
├── exception/
│   └── GlobalExceptionHandler.kt # Global error handling
├── repository/
│   └── UserRepository.kt       # User repository
└── service/
    ├── GameStateService.kt     # Redis game state service
    └── UserService.kt          # User business logic
```

## Prerequisites

- Java 21
- PostgreSQL
- Redis
- Maven

## Environment Variables

Set the following environment variables or use the defaults:

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/cardgame
DATABASE_USERNAME=cardgame_user
DATABASE_PASSWORD=cardgame_pass

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Logging
LOG_LEVEL=INFO
SHOW_SQL=false
```

## Setup Instructions

### 1. Database Setup
```sql
-- Create database and user
CREATE DATABASE cardgame;
CREATE USER cardgame_user WITH PASSWORD 'cardgame_pass';
GRANT ALL PRIVILEGES ON DATABASE cardgame TO cardgame_user;
```

### 2. Redis Setup
```bash
# Start Redis (macOS with Homebrew)
brew services start redis

# Or start manually
redis-server
```

### 3. Run Application
```bash
# Build and run
./mvnw spring-boot:run

# Or build JAR and run
./mvnw clean package
java -jar target/GameBackend-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### REST API

#### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `DELETE /api/users/{id}` - Delete user

#### Test
- `GET /api/test/ping` - Health check endpoint

#### Health
- `GET /actuator/health` - Application health status

### WebSocket Endpoints

Connect to: `ws://localhost:8080/ws`

#### Message Mappings
- `/app/get_state` - Get game state
- `/app/join_game` - Join a game
- `/app/leave_game` - Leave a game

#### Subscriptions
- `/user/queue/game_state` - Personal game state updates
- `/topic/game_updates` - Broadcast game updates

## Sample Requests

### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "inGameName": "TestPlayer"
  }'
```

### Get User
```bash
curl http://localhost:8080/api/users/1
```

### WebSocket (JavaScript Example)
```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to game updates
    stompClient.subscribe('/user/queue/game_state', function(message) {
        console.log('Game state:', JSON.parse(message.body));
    });
    
    // Request game state
    stompClient.send('/app/get_state', {}, JSON.stringify({
        gameId: 'game-001'
    }));
});
```

## Sample Data

The application automatically creates sample data on startup:
- 4 sample users
- 3 sample game states

## Monitoring

- Health endpoint: `http://localhost:8080/actuator/health`
- Application info: `http://localhost:8080/actuator/info`
- Metrics: `http://localhost:8080/actuator/metrics`

## Error Handling

The application provides consistent error responses:

```json
{
  "timestamp": "2025-08-26T21:17:38",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/api/users/999"
}
```

## Development Notes

- Uses Kotlin data classes for DTOs
- Implements proper validation with Bean Validation
- Follows Spring Boot best practices
- Includes comprehensive logging
- Uses environment variables for configuration
- Implements graceful error handling
