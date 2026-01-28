package edu.miu.mongodemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ollama Configuration
 * 
 * Configures the ChatClient bean for interacting with Ollama models.
 * Spring AI auto-configures the ChatModel from application.properties,
 * but this allows for additional customization if needed.
 */
@Configuration
public class OllamaConfig {

    /**
     * ChatClient bean for interacting with Ollama
     * 
     * The ChatModel is auto-configured by Spring AI based on:
     * - spring.ai.ollama.base-url
     * - spring.ai.ollama.chat.options.model
     * - spring.ai.ollama.chat.options.temperature
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                // Add any customizations here if needed
                .build();
    }
}
