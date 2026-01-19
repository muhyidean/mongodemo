package edu.miu.mongodemo.service;

import edu.miu.mongodemo.controller.WebSocketArticleController;
import edu.miu.mongodemo.model.Article;
import edu.miu.mongodemo.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Article Service with WebSocket Integration
 * 
 * This service demonstrates how to integrate WebSocket broadcasting
 * from the service layer, not just controllers.
 */
@Service
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;

    /**
     * WebSocket controller for broadcasting updates
     * 
     * @Lazy annotation breaks the circular dependency:
     * - ArticleService needs WebSocketArticleController
     * - WebSocketArticleController needs ArticleService
     * - @Lazy delays initialization of WebSocketArticleController until it's actually used
     * 
     * Alternative approach: Use ApplicationEventPublisher for better decoupling
     * (publish events from service, listen in controller)
     */
    @Autowired
    @Lazy
    private WebSocketArticleController webSocketController;

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Optional<Article> getArticleById(String id) {
        return articleRepository.findById(id);
    }

    /**
     * Creates a new article and optionally broadcasts view count update
     * 
     * This demonstrates how WebSocket can be integrated into service methods
     * to provide real-time updates when data changes
     */
    public Article createArticle(Article article) {
        Article savedArticle = articleRepository.save(article);
        
        // Broadcast the creation via WebSocket
        // This enables real-time updates in admin dashboards or article lists
        // @Lazy ensures webSocketController is initialized when needed
        if (savedArticle.getViewCount() != null) {
            webSocketController.broadcastViewCount(savedArticle.getId(), savedArticle.getViewCount());
        }
        
        return savedArticle;
    }

    public Article updateArticle(String id, Article article) {
        article.setId(id);
        return articleRepository.save(article);
    }

    public void deleteArticle(String id) {
        articleRepository.deleteById(id);
    }

    public List<Article> getArticlesByAuthor(String author) {
        return articleRepository.findByAuthor(author);
    }

    public List<Article> getArticlesByStatus(String status) {
        return articleRepository.findByStatus(status);
    }

    public List<Article> searchArticlesByTitle(String title) {
        return articleRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Article> getArticlesByTag(String tag) {
        return articleRepository.findByTagsContaining(tag);
    }

    /**
     * Increments view count and broadcasts update via WebSocket
     * 
     * This method demonstrates real-time analytics:
     * - When someone views an article, increment the counter
     * - Broadcast the new count to all subscribers
     * - Enables live view count displays on article pages
     * 
     * @param id Article ID
     * @return Updated article with new view count
     */
    public Optional<Article> incrementViewCount(String id) {
        Optional<Article> articleOpt = articleRepository.findById(id);
        
        if (articleOpt.isPresent()) {
            Article article = articleOpt.get();
            article.setViewCount(article.getViewCount() + 1);
            Article updatedArticle = articleRepository.save(article);
            
            // Broadcast view count update via WebSocket
            // All clients subscribed to /topic/article/{id}/views will receive this
            // @Lazy ensures webSocketController is initialized when needed
            webSocketController.broadcastViewCount(updatedArticle.getId(), updatedArticle.getViewCount());
            
            return Optional.of(updatedArticle);
        }
        
        return Optional.empty();
    }
}

