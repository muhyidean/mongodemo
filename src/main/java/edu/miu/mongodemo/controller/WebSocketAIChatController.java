package edu.miu.mongodemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket Controller for AI Chat
 * 
 * This controller handles real-time chat messages via WebSocket and responds
 * with AI-generated content using Ollama.
 */
@Controller
public class WebSocketAIChatController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAIChatController.class);

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Handles chat messages from clients and responds with AI-generated content
     * 
     * Message Flow:
     * 1. Client sends message to /app/chat/message
     * 2. Server processes message with AI (Ollama)
     * 3. Server broadcasts response to /topic/chat/messages
     * 
     * @param chatMessage The chat message from the client
     * @return ChatResponseDTO containing the AI response
     */
    @MessageMapping("/chat/message")
    @SendTo("/topic/chat/messages")
    public ChatResponseDTO handleChatMessage(@Payload ChatMessageDTO chatMessage) {
        logger.info("Received chat message from user: {}", chatMessage.getUser());
        
        try {
            // Get AI response using ChatClient
            String aiResponse = chatClient.prompt()
                    .user(chatMessage.getMessage())
                    .call()
                    .content();
            
            logger.info("Generated AI response for user: {}", chatMessage.getUser());
            
            // Create response DTO
            ChatResponseDTO response = new ChatResponseDTO();
            response.setUser("AI Assistant");
            response.setMessage(aiResponse);
            response.setTimestamp(java.time.LocalDateTime.now());
            response.setType("ai");
            
            return response;
        } catch (Exception e) {
            logger.error("Error generating AI response", e);
            
            // Send error response
            ChatResponseDTO errorResponse = new ChatResponseDTO();
            errorResponse.setUser("AI Assistant");
            errorResponse.setMessage("Sorry, I encountered an error: " + e.getMessage());
            errorResponse.setTimestamp(java.time.LocalDateTime.now());
            errorResponse.setType("error");
            
            return errorResponse;
        }
    }

    /**
     * Sends a user message to the chat (echoes user message back)
     * This allows the UI to display user messages immediately
     * 
     * @param chatMessage The user's message
     * @return ChatResponseDTO containing the user's message
     */
    @MessageMapping("/chat/user")
    @SendTo("/topic/chat/messages")
    public ChatResponseDTO handleUserMessage(@Payload ChatMessageDTO chatMessage) {
        logger.debug("Echoing user message: {}", chatMessage.getMessage());
        
        ChatResponseDTO response = new ChatResponseDTO();
        response.setUser(chatMessage.getUser());
        response.setMessage(chatMessage.getMessage());
        response.setTimestamp(java.time.LocalDateTime.now());
        response.setType("user");
        
        return response;
    }

    // ========== DTO Classes ==========

    /**
     * DTO for incoming chat messages
     */
    public static class ChatMessageDTO {
        private String user;
        private String message;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * DTO for chat responses (both user and AI messages)
     */
    public static class ChatResponseDTO {
        private String user;
        private String message;
        private java.time.LocalDateTime timestamp;
        private String type; // "user", "ai", or "error"

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public java.time.LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(java.time.LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
