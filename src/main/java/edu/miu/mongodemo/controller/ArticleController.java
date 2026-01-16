package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.model.Article;
import edu.miu.mongodemo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable String id) {
        Optional<Article> article = articleService.getArticleById(id);
        return article.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        Article createdArticle = articleService.createArticle(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable String id, @RequestBody Article article) {
        Article updatedArticle = articleService.updateArticle(id, article);
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Article>> getArticlesByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(articleService.getArticlesByAuthor(author));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Article>> getArticlesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(articleService.getArticlesByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticlesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(articleService.searchArticlesByTitle(title));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Article>> getArticlesByTag(@PathVariable String tag) {
        return ResponseEntity.ok(articleService.getArticlesByTag(tag));
    }
}

