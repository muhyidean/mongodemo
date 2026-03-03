package edu.miu.mongodemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ollama Configuration
 * 
 * Configures the ChatClient bean for interacting with Ollama models with chat memory.
 * Spring AI auto-configures the ChatModel from application.properties,
 * but this allows for additional customization including conversation memory.
 */
@Configuration
public class OllamaConfig {

    @Autowired(required = false)
    private ChatMemory chatMemory;

    /**
     * ChatClient bean for interacting with Ollama with chat memory enabled
     * 
     * The ChatModel is auto-configured by Spring AI based on:
     * - spring.ai.ollama.base-url
     * - spring.ai.ollama.chat.options.model
     * - spring.ai.ollama.chat.options.temperature
     * 
     * Chat Memory:
     * - Spring AI auto-configures a ChatMemory bean (MessageWindowChatMemory by default)
     * - MessageChatMemoryAdvisor automatically includes conversation history in AI context
     * - Default window size is 20 messages
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        
        // Add memory advisor if ChatMemory is available
        if (chatMemory != null) {
            MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                    .build();
            builder.defaultAdvisors(memoryAdvisor);
        }
        
        return builder.build();
    }
}
