package edu.miu.mongodemo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "articles")
public class Article {
    @Id
    private String id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime publishedDate;
    private List<String> tags;
    private Integer viewCount;
    private String status; // DRAFT, PUBLISHED, ARCHIVED
    @DBRef
    private Person person;
    public Article() {
        this.tags = new ArrayList<>();
        this.viewCount = 0;
        this.publishedDate = LocalDateTime.now();
        this.status = "DRAFT";
    }

    public Article(String title, String content, String author) {
        this();
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

