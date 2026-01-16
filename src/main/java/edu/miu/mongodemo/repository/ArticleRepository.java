package edu.miu.mongodemo.repository;

import edu.miu.mongodemo.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
    List<Article> findByAuthor(String author);
    List<Article> findByStatus(String status);
    List<Article> findByTitleContainingIgnoreCase(String title);
    List<Article> findByTagsContaining(String tag);
}

