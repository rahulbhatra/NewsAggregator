package com.example.newsaggregator;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsSource implements Serializable {
    private String id;
    private String name = "";
    private String description;
    private String url;
    private String category;
    private String languageCode;
    private String languageName;
    private String countryCode;
    private String countryName;
    private String colorCode;
    private List<NewsArticle> newsArticles = new ArrayList<>();

    public NewsSource() { }

    public NewsSource(String id, String name, String description, String url, String category,
                      String languageCode, String countryCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.category = category;
        this.languageCode = languageCode;
        this.countryCode = countryCode;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public List<NewsArticle> getNewsArticles() {
        return newsArticles;
    }

    public void setNewsArticles(List<NewsArticle> newsArticles) {
        this.newsArticles = newsArticles;
    }

    @NonNull
    public String toString() {
        return name;
    }
}
