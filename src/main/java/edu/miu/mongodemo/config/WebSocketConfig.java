package edu.miu.mongodemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration Class
 * 
 * This class configures WebSocket support using STOMP (Simple Text Oriented Messaging Protocol)
 * over WebSockets. STOMP provides a frame-based protocol for messaging.
 * 
 * Key Concepts:
 * 1. WebSocket: A full-duplex communication protocol that allows bidirectional communication
 *    between client and server over a single TCP connection, without the overhead of HTTP headers.
 * 
 * 2. STOMP: A messaging protocol that runs on top of WebSocket. It provides:
 *    - Message routing (like REST endpoints but for real-time messaging)
 *    - Pub/Sub pattern support
 *    - Message acknowledgment
 * 
 * 3. Message Broker: Acts as an intermediary that routes messages from producers to consumers.
 *    - In-memory broker: Simple, good for development (used here)
 *    - External broker: RabbitMQ, ActiveMQ for production scalability
 * 
 * 4. Endpoints: WebSocket connection points that clients connect to
 * 
 * 5. Destinations: Topics or queues where messages are sent/received
 *    - /topic: For pub/sub (broadcasting to multiple subscribers)
 *    - /queue: For point-to-point messaging
 *    - /app: Prefix for application destinations (messages sent TO the server)
 */
@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling, backed by a message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker
     * 
     * This method sets up how messages are routed and distributed:
     * 
     * @param config MessageBrokerRegistry to configure broker settings
     * 
     * enableSimpleBroker("/topic", "/queue"):
     *   - Enables an in-memory message broker
     *   - "/topic" prefix: Used for broadcasting messages to all subscribers (pub/sub)
     *   - "/queue" prefix: Used for point-to-point messaging
     *   - When a message is sent to /topic/comments, all clients subscribed to that topic receive it
     * 
     * setApplicationDestinationPrefixes("/app"):
     *   - Messages sent from clients with "/app" prefix are routed to @MessageMapping methods
     *   - Example: Client sends to "/app/article/update" -> handled by @MessageMapping("/article/update")
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker to carry messages back to the client
        // on destinations prefixed with "/topic" and "/queue"
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefix for messages that are bound to @MessageMapping methods
        // Messages sent from client to "/app/*" will be routed to message-handling methods
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers STOMP endpoints
     * 
     * This method defines the WebSocket endpoint that clients will connect to:
     * 
     * @param registry StompEndpointRegistry to register endpoints
     * 
     * addEndpoint("/ws"):
     *   - Creates a WebSocket endpoint at "/ws"
     *   - Clients connect to: ws://localhost:8080/ws
     *   - This is the initial handshake endpoint
     * 
     * withSockJS():
     *   - Enables SockJS fallback options
     *   - SockJS provides WebSocket-like object with fallback transports
     *   - If WebSocket is not available, it falls back to HTTP polling, streaming, etc.
     *   - Makes the application work even if WebSocket is blocked by firewalls/proxies
     *   - Client connects to: http://localhost:8080/ws (SockJS handles the protocol)
     * 
     * setAllowedOriginPatterns("*"):
     *   - Allows connections from any origin (for development)
     *   - Uses pattern matching instead of exact origins (avoids CORS credential conflicts)
     *   - In production, specify exact patterns: setAllowedOriginPatterns("https://*.yourdomain.com")
     *   - Prevents CORS (Cross-Origin Resource Sharing) issues
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register "/ws" endpoint, enabling SockJS fallback options
        // SockJS provides WebSocket emulation with fallback transports
        registry.addEndpoint("/ws")
                // Use allowedOriginPatterns instead of allowedOrigins("*") to avoid CORS conflicts
                // This allows all origins without the wildcard restriction
                .setAllowedOriginPatterns("*") // Allow all origins (configure properly for production)
                .withSockJS(); // Enable SockJS fallback for browsers that don't support WebSocket
    }
}

