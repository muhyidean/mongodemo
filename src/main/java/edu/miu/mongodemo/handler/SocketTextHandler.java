package edu.miu.mongodemo.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Simple WebSocket Handler (Non-STOMP)
 * 
 * This handler provides a simple WebSocket connection without STOMP protocol.
 * It's a lower-level approach compared to STOMP, giving you direct control
 * over WebSocket messages.
 * 
 * Differences from STOMP:
 * - No message broker needed
 * - Direct WebSocket connection
 * - Manual message handling
 * - Simpler for basic use cases
 * 
 * Use cases:
 * - Simple chat applications
 * - Real-time notifications
 * - Custom protocol implementations
 * - When you don't need pub/sub patterns
 */
@Component
public class SocketTextHandler extends TextWebSocketHandler {

    /**
     * Called when we receive a message from the client
     * 
     * @param session The WebSocket session
     * @param message The text message received from client
     * @throws Exception
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Got message: " + message.getPayload());
        
        // Echo back to the client with a response
        String response = "Hi " + message.getPayload() + " how may we help you?";
        session.sendMessage(new TextMessage(response));
    }

    /**
     * Called when a new WebSocket connection is established
     * 
     * @param session The WebSocket session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println("Connected: " + session.getId());
        
        // Send welcome message back to the client
        session.sendMessage(new TextMessage("Connected!"));
        
        // Start a background thread to send periodic messages (optional)
        // MyThread myThread = new MyThread(session);
        // Thread t = new Thread(myThread);
        // t.start();
    }

    /**
     * Called when the WebSocket connection is closed
     * 
     * @param session The WebSocket session
     * @param status The close status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        System.out.println("Connection closed: " + session.getId() + " - " + status);
    }

    /**
     * Called when an error occurs during WebSocket communication
     * 
     * @param session The WebSocket session
     * @param exception The exception that occurred
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        System.out.println("Transport error for session " + session.getId() + ": " + exception.getMessage());
    }
}

