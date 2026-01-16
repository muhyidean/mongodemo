package edu.miu.mongodemo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "news")
public class News {
    @Id
    private String id;
    private String headline;
    private String summary;
    private String fullText;
    private String reporter;
    private String category; // POLITICS, SPORTS, TECHNOLOGY, BUSINESS, etc.
    private LocalDateTime publishedDate;
    private LocalDateTime expiryDate;
    private String imageUrl;
    private List<String> relatedNewsIds;
    private Integer priority; // 1-10, 10 being highest

    public News() {
        this.relatedNewsIds = new ArrayList<>();
        this.publishedDate = LocalDateTime.now();
        this.priority = 5;
    }

    public News(String headline, String summary, String fullText, String reporter, String category) {
        this();
        this.headline = headline;
        this.summary = summary;
        this.fullText = fullText;
        this.reporter = reporter;
        this.category = category;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getRelatedNewsIds() {
        return relatedNewsIds;
    }

    public void setRelatedNewsIds(List<String> relatedNewsIds) {
        this.relatedNewsIds = relatedNewsIds;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}

