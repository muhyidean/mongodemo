package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.model.Article;
import edu.miu.mongodemo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * WebSocket Controller for Real-Time Article Updates
 * 
 * This controller demonstrates different WebSocket messaging patterns:
 * 
 * 1. Broadcasting Updates: When an article is published, all subscribers are notified
 * 2. Status Changes: Real-time updates when article status changes
 * 3. View Count: Live view count updates as users read articles
 * 
 * Use Cases:
 * - Live blog platform where readers see new articles as they're published
 * - Collaborative editing where multiple users see changes in real-time
 * - Analytics dashboard showing live metrics
 * - Notification system for content creators
 */
@Controller
public class WebSocketArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Handles article status changes (DRAFT -> PUBLISHED -> ARCHIVED)
     * 
     * This demonstrates:
     * - Receiving messages from clients
     * - Processing business logic
     * - Broadcasting to multiple topics
     * 
     * @param statusDTO Contains articleId and new status
     * 
     * Message Flow:
     * 1. Admin changes article status via WebSocket
     * 2. Server updates database
     * 3. Server broadcasts to:
     *    - /topic/articles/all: All subscribers (for article list updates)
     *    - /topic/article/{id}: Specific article subscribers (for detail page updates)
     */
    @MessageMapping("/article/status")
    public void updateArticleStatus(@Payload ArticleStatusDTO statusDTO) {
        // Get article from database
        Article article = articleService.getArticleById(statusDTO.getArticleId())
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        // Update status
        article.setStatus(statusDTO.getStatus());
        Article updatedArticle = articleService.updateArticle(article.getId(), article);
        
        // Create response DTO
        ArticleStatusResponseDTO response = new ArticleStatusResponseDTO();
        response.setArticleId(updatedArticle.getId());
        response.setStatus(updatedArticle.getStatus());
        response.setTitle(updatedArticle.getTitle());
        response.setUpdatedAt(LocalDateTime.now());
        
        // Broadcast to all subscribers of the article list
        // Useful for admin dashboards or article listing pages
        messagingTemplate.convertAndSend("/topic/articles/all", response);
        
        // Also broadcast to subscribers of this specific article
        // Useful for article detail pages that show status
        messagingTemplate.convertAndSend("/topic/article/" + updatedArticle.getId(), response);
    }

    /**
     * Handles new article publications
     * 
     * When an article is published, this broadcasts it to all subscribers
     * This enables a "live feed" experience where new articles appear instantly
     * 
     * @param articleDTO The article data to publish
     * @return ArticleDTO that gets broadcast to /topic/articles/new
     * 
     * @SendTo automatically broadcasts the return value
     * All clients subscribed to /topic/articles/new will receive this article
     */
    @MessageMapping("/article/publish")
    @SendTo("/topic/articles/new")
    public ArticleDTO publishArticle(@Payload ArticleDTO articleDTO) {
        // Create Article entity
        Article article = new Article();
        article.setTitle(articleDTO.getTitle());
        article.setContent(articleDTO.getContent());
        article.setAuthor(articleDTO.getAuthor());
        article.setStatus("PUBLISHED");
        article.setTags(articleDTO.getTags());
        
        // Save to database
        Article savedArticle = articleService.createArticle(article);
        
        // Convert to DTO for broadcasting
        ArticleDTO responseDTO = new ArticleDTO();
        responseDTO.setId(savedArticle.getId());
        responseDTO.setTitle(savedArticle.getTitle());
        responseDTO.setAuthor(savedArticle.getAuthor());
        responseDTO.setPublishedDate(savedArticle.getPublishedDate());
        responseDTO.setStatus(savedArticle.getStatus());
        responseDTO.setTags(savedArticle.getTags());
        
        // @SendTo will broadcast this to /topic/articles/new
        return responseDTO;
    }

    /**
     * Broadcasts article view count increment
     * 
     * This method can be called from ArticleService when someone views an article
     * Demonstrates sending messages from service layer
     * 
     * @param articleId The article being viewed
     * @param newViewCount The updated view count
     */
    public void broadcastViewCount(String articleId, Integer newViewCount) {
        ViewCountUpdateDTO dto = new ViewCountUpdateDTO();
        dto.setArticleId(articleId);
        dto.setViewCount(newViewCount);
        dto.setTimestamp(LocalDateTime.now());
        
        // Broadcast to subscribers of this article's view count topic
        // Analytics dashboards or article pages can subscribe to see live metrics
        messagingTemplate.convertAndSend("/topic/article/" + articleId + "/views", dto);
    }

    /**
     * Broadcasts article update (title, content, etc.)
     * 
     * Useful for collaborative editing or content management systems
     * where multiple users need to see updates in real-time
     * 
     * @param updateDTO Contains article updates
     */
    @MessageMapping("/article/update")
    @SendTo("/topic/article/{articleId}/updates")
    public ArticleUpdateDTO updateArticleContent(@Payload ArticleUpdateDTO updateDTO) {
        // Get existing article
        Article article = articleService.getArticleById(updateDTO.getArticleId())
                .orElseThrow(() -> new RuntimeException("Article not found"));
        
        // Update fields
        if (updateDTO.getTitle() != null) {
            article.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getContent() != null) {
            article.setContent(updateDTO.getContent());
        }
        if (updateDTO.getTags() != null) {
            article.setTags(updateDTO.getTags());
        }
        
        // Save to database
        Article updatedArticle = articleService.updateArticle(article.getId(), article);
        
        // Create response
        ArticleUpdateDTO response = new ArticleUpdateDTO();
        response.setArticleId(updatedArticle.getId());
        response.setTitle(updatedArticle.getTitle());
        response.setContent(updatedArticle.getContent());
        response.setTags(updatedArticle.getTags());
        response.setUpdatedAt(LocalDateTime.now());
        
        // Broadcast to all subscribers of this article's updates topic
        return response;
    }

    // ========== DTO Classes ==========

    /**
     * DTO for article data in WebSocket messages
     */
    public static class ArticleDTO {
        private String id;
        private String title;
        private String content;
        private String author;
        private LocalDateTime publishedDate;
        private java.util.List<String> tags;
        private String status;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public LocalDateTime getPublishedDate() { return publishedDate; }
        public void setPublishedDate(LocalDateTime publishedDate) { this.publishedDate = publishedDate; }

        public java.util.List<String> getTags() { return tags; }
        public void setTags(java.util.List<String> tags) { this.tags = tags; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * DTO for article status updates
     */
    public static class ArticleStatusDTO {
        private String articleId;
        private String status;

        public String getArticleId() { return articleId; }
        public void setArticleId(String articleId) { this.articleId = articleId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * DTO for article status response
     */
    public static class ArticleStatusResponseDTO {
        private String articleId;
        private String status;
        private String title;
        private LocalDateTime updatedAt;

        public String getArticleId() { return articleId; }
        public void setArticleId(String articleId) { this.articleId = articleId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    /**
     * DTO for view count updates
     */
    public static class ViewCountUpdateDTO {
        private String articleId;
        private Integer viewCount;
        private LocalDateTime timestamp;

        public String getArticleId() { return articleId; }
        public void setArticleId(String articleId) { this.articleId = articleId; }

        public Integer getViewCount() { return viewCount; }
        public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    /**
     * DTO for article content updates
     */
    public static class ArticleUpdateDTO {
        private String articleId;
        private String title;
        private String content;
        private java.util.List<String> tags;
        private LocalDateTime updatedAt;

        public String getArticleId() { return articleId; }
        public void setArticleId(String articleId) { this.articleId = articleId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public java.util.List<String> getTags() { return tags; }
        public void setTags(java.util.List<String> tags) { this.tags = tags; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}

