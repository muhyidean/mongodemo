package edu.miu.mongodemo.service;

import edu.miu.mongodemo.model.Article;
import edu.miu.mongodemo.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Optional<Article> getArticleById(String id) {
        return articleRepository.findById(id);
    }

    public Article createArticle(Article article) {
        return articleRepository.save(article);
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
}

