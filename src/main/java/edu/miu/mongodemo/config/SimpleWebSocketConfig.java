package edu.miu.mongodemo.config;

import edu.miu.mongodemo.handler.SocketTextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Simple WebSocket Configuration (Non-STOMP)
 * 
 * This configuration registers a simple WebSocket endpoint without STOMP.
 * It works alongside the STOMP configuration, providing an alternative
 * WebSocket connection method.
 * 
 * Endpoint: /simple-ws
 * Handler: SocketTextHandler
 * 
 * This is separate from the STOMP WebSocket at /ws
 */
@Configuration
@EnableWebSocket
public class SimpleWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private SocketTextHandler socketTextHandler;

    /**
     * Registers WebSocket handlers
     * 
     * @param registry WebSocketHandlerRegistry to register handlers
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register the simple WebSocket handler at /simple-ws
        // withSockJS() enables SockJS fallback for browsers that don't support WebSocket
        // This allows the connection to work even if WebSocket is blocked by firewalls/proxies
        registry.addHandler(socketTextHandler, "/simple-ws")
                .setAllowedOriginPatterns("*") // Allow all origins (configure properly for production)
                .withSockJS(); // Enable SockJS fallback for better browser compatibility
    }
}

