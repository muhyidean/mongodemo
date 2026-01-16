package edu.miu.mongodemo.service;

import edu.miu.mongodemo.model.News;
import edu.miu.mongodemo.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NewsService {
    
    @Autowired
    private NewsRepository newsRepository;

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public Optional<News> getNewsById(String id) {
        return newsRepository.findById(id);
    }

    public News createNews(News news) {
        return newsRepository.save(news);
    }

    public News updateNews(String id, News news) {
        news.setId(id);
        return newsRepository.save(news);
    }

    public void deleteNews(String id) {
        newsRepository.deleteById(id);
    }

    public List<News> getNewsByCategory(String category) {
        return newsRepository.findByCategory(category);
    }

    public List<News> getNewsByReporter(String reporter) {
        return newsRepository.findByReporter(reporter);
    }

    public List<News> getNewsByDateRange(LocalDateTime start, LocalDateTime end) {
        return newsRepository.findByPublishedDateBetween(start, end);
    }

    public List<News> getHighPriorityNews(Integer priority) {
        return newsRepository.findByPriorityGreaterThanEqual(priority);
    }

    public List<News> searchNewsByHeadline(String headline) {
        return newsRepository.findByHeadlineContainingIgnoreCase(headline);
    }
}

