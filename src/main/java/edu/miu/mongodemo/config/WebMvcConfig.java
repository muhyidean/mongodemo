package edu.miu.mongodemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * 
 * This configuration ensures CORS is properly configured for WebSocket connections
 * and static resource serving.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configure CORS (Cross-Origin Resource Sharing)
     * 
     * This allows the HTML client to connect to the WebSocket endpoint
     * from the same origin (localhost:8080)
     * 
     * Note: WebSocket CORS is also configured in WebSocketConfig
     * This is for regular HTTP requests
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Use allowedOriginPatterns instead of allowedOrigins("*") to avoid CORS conflicts
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}

