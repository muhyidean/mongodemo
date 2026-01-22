package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.model.Article;
import edu.miu.mongodemo.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    
    @Autowired
    private ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        logger.info("Fetching all articles");
        long startTime = System.currentTimeMillis();
        try {
            List<Article> articles = articleService.getAllArticles();
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Successfully retrieved {} articles in {}ms", articles.size(), duration);
            MDC.put("article_count", String.valueOf(articles.size()));
            MDC.put("response_time_ms", String.valueOf(duration));
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching all articles", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable String id) {
        logger.info("Fetching article with id: {}", id);
        MDC.put("article_id", id);
        long startTime = System.currentTimeMillis();
        try {
            Optional<Article> article = articleService.getArticleById(id);
            long duration = System.currentTimeMillis() - startTime;
            if (article.isPresent()) {
                logger.info("Article found: {} in {}ms", id, duration);
                MDC.put("response_time_ms", String.valueOf(duration));
                return ResponseEntity.ok(article.get());
            } else {
                logger.warn("Article not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching article with id: {}", id, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        logger.info("Creating new article with title: {}", article.getTitle());
        MDC.put("article_title", article.getTitle());
        MDC.put("article_author", article.getAuthor());
        long startTime = System.currentTimeMillis();
        try {
            Article createdArticle = articleService.createArticle(article);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Article created successfully with id: {} in {}ms", createdArticle.getId(), duration);
            MDC.put("article_id", createdArticle.getId());
            MDC.put("response_time_ms", String.valueOf(duration));
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
        } catch (Exception e) {
            logger.error("Error creating article with title: {}", article.getTitle(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable String id, @RequestBody Article article) {
        logger.info("Updating article with id: {}", id);
        MDC.put("article_id", id);
        long startTime = System.currentTimeMillis();
        try {
            Article updatedArticle = articleService.updateArticle(id, article);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Article updated successfully: {} in {}ms", id, duration);
            MDC.put("response_time_ms", String.valueOf(duration));
            return ResponseEntity.ok(updatedArticle);
        } catch (Exception e) {
            logger.error("Error updating article with id: {}", id, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
        logger.info("Deleting article with id: {}", id);
        MDC.put("article_id", id);
        long startTime = System.currentTimeMillis();
        try {
            articleService.deleteArticle(id);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Article deleted successfully: {} in {}ms", id, duration);
            MDC.put("response_time_ms", String.valueOf(duration));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting article with id: {}", id, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Article>> getArticlesByAuthor(@PathVariable String author) {
        logger.info("Fetching articles by author: {}", author);
        MDC.put("author", author);
        long startTime = System.currentTimeMillis();
        try {
            List<Article> articles = articleService.getArticlesByAuthor(author);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Found {} articles by author: {} in {}ms", articles.size(), author, duration);
            MDC.put("article_count", String.valueOf(articles.size()));
            MDC.put("response_time_ms", String.valueOf(duration));
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching articles by author: {}", author, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Article>> getArticlesByStatus(@PathVariable String status) {
        logger.info("Fetching articles with status: {}", status);
        MDC.put("status", status);
        long startTime = System.currentTimeMillis();
        try {
            List<Article> articles = articleService.getArticlesByStatus(status);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Found {} articles with status: {} in {}ms", articles.size(), status, duration);
            MDC.put("article_count", String.valueOf(articles.size()));
            MDC.put("response_time_ms", String.valueOf(duration));
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching articles with status: {}", status, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticlesByTitle(@RequestParam String title) {
        logger.info("Searching articles by title: {}", title);
        MDC.put("search_term", title);
        long startTime = System.currentTimeMillis();
        try {
            List<Article> articles = articleService.searchArticlesByTitle(title);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Search completed: found {} articles matching '{}' in {}ms", articles.size(), title, duration);
            MDC.put("article_count", String.valueOf(articles.size()));
            MDC.put("response_time_ms", String.valueOf(duration));
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error searching articles by title: {}", title, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Article>> getArticlesByTag(@PathVariable String tag) {
        logger.info("Fetching articles with tag: {}", tag);
        MDC.put("tag", tag);
        long startTime = System.currentTimeMillis();
        try {
            List<Article> articles = articleService.getArticlesByTag(tag);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Found {} articles with tag: {} in {}ms", articles.size(), tag, duration);
            MDC.put("article_count", String.valueOf(articles.size()));
            MDC.put("response_time_ms", String.valueOf(duration));
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error fetching articles with tag: {}", tag, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}

