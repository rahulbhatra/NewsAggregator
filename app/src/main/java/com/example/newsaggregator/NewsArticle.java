package com.example.newsaggregator;

import java.io.Serializable;

public class NewsArticle implements Serializable {

    private String id;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;


    public NewsArticle(String id, String author, String title, String description, String url, String urlToImage, String publishedAt, String content) {
        this.id = id;
        this.author = "null".equalsIgnoreCase(author) ? "" : author;
        this.title = "null".equalsIgnoreCase(title) ? "" : title;
        this.description = "null".equalsIgnoreCase(description) ? "" : description;
        this.url = "null".equalsIgnoreCase(url) ? "" : url;
        this.urlToImage = "null".equalsIgnoreCase(urlToImage) ? null : urlToImage;
        this.publishedAt = "null".equalsIgnoreCase(publishedAt) ? "" : publishedAt;
        this.content = "null".equalsIgnoreCase(content) ? "" : content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
