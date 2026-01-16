package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.model.News;
import edu.miu.mongodemo.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    
    @Autowired
    private NewsService newsService;

    @GetMapping
    public ResponseEntity<List<News>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable String id) {
        Optional<News> news = newsService.getNewsById(id);
        return news.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<News> createNews(@RequestBody News news) {
        News createdNews = newsService.createNews(news);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNews);
    }

    @PutMapping("/{id}")
    public ResponseEntity<News> updateNews(@PathVariable String id, @RequestBody News news) {
        News updatedNews = newsService.updateNews(id, news);
        return ResponseEntity.ok(updatedNews);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable String id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<News>> getNewsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(newsService.getNewsByCategory(category));
    }

    @GetMapping("/reporter/{reporter}")
    public ResponseEntity<List<News>> getNewsByReporter(@PathVariable String reporter) {
        return ResponseEntity.ok(newsService.getNewsByReporter(reporter));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<News>> getNewsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(newsService.getNewsByDateRange(start, end));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<News>> getHighPriorityNews(@PathVariable Integer priority) {
        return ResponseEntity.ok(newsService.getHighPriorityNews(priority));
    }

    @GetMapping("/search")
    public ResponseEntity<List<News>> searchNewsByHeadline(@RequestParam String headline) {
        return ResponseEntity.ok(newsService.searchNewsByHeadline(headline));
    }
}

