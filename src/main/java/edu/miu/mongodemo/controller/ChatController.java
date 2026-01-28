package edu.miu.mongodemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Chat Controller
 * 
 * REST controller for interacting with Ollama via Spring AI ChatClient
 */
@RestController
@RequestMapping("/ai")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatClient chatClient;

    /**
     * Get AI response based on a subject
     * 
     * Example: GET /ai?subject=programming
     * 
     * @param subject The subject for the AI to generate content about
     * @return AI-generated response as a string
     */
    @GetMapping
    public ResponseEntity<?> getResponse(@RequestParam(required = false, defaultValue = "technology") String subject) {
        try {
            logger.info("Received request for subject: {}", subject);
            String template = "Tell me a joke about {subject}";
            PromptTemplate promptTemplate = new PromptTemplate(template);
            String prompt = promptTemplate.render(Map.of("subject", subject));
            
            logger.debug("Sending prompt to Ollama: {}", prompt);
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            
            logger.info("Successfully received response from Ollama");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error calling Ollama", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get AI response");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("cause", e.getCause() != null ? e.getCause().getMessage() : "Unknown");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get AI response with a custom prompt
     * 
     * Example: GET /ai/custom?prompt=Explain quantum computing
     * 
     * @param prompt The custom prompt to send to the AI
     * @return AI-generated response as a string
     */
    @GetMapping("/custom")
    public ResponseEntity<?> getCustomResponse(@RequestParam String prompt) {
        try {
            logger.info("Received custom prompt request: {}", prompt);
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            
            logger.info("Successfully received response from Ollama");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error calling Ollama with custom prompt", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get AI response");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("cause", e.getCause() != null ? e.getCause().getMessage() : "Unknown");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
