package edu.miu.mongodemo.repository;

import edu.miu.mongodemo.model.News;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends MongoRepository<News, String> {
    List<News> findByCategory(String category);
    List<News> findByReporter(String reporter);
    List<News> findByPublishedDateBetween(LocalDateTime start, LocalDateTime end);
    List<News> findByPriorityGreaterThanEqual(Integer priority);
    List<News> findByHeadlineContainingIgnoreCase(String headline);
}

