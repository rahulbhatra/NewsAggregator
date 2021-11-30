package com.example.newsaggregator;

import android.view.Menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RotateData implements Serializable {
    private Map<String, String> menuSubMenuFilterMap = new HashMap<>();
    private List<NewsSource> newsSources = new ArrayList<>();
    private NewsSource currentNewsSource = new NewsSource();
    private List<NewsArticle> currentNewsArticles = new ArrayList<>();
    private int articlePosition = 0;

    public RotateData(Map<String, String> menuSubMenuFilterMap, List<NewsSource> newsSources,
                      NewsSource currentNewsSource,
                      List<NewsArticle> currentNewsArticles, int articlePosition) {
        this.menuSubMenuFilterMap = menuSubMenuFilterMap;
        this.newsSources = newsSources;
        this.currentNewsSource = currentNewsSource;
        this.currentNewsArticles = currentNewsArticles;
        this.articlePosition = articlePosition;
    }

    public Map<String, String> getMenuSubMenuFilterMap() {
        return menuSubMenuFilterMap;
    }

    public void setMenuSubMenuFilterMap(Map<String, String> menuSubMenuFilterMap) {
        this.menuSubMenuFilterMap = menuSubMenuFilterMap;
    }

    public List<NewsSource> getNewsSources() {
        return newsSources;
    }

    public void setNewsSources(List<NewsSource> newsSources) {
        this.newsSources = newsSources;
    }

    public NewsSource getCurrentNewsSource() {
        return currentNewsSource;
    }

    public void setCurrentNewsSource(NewsSource currentNewsSource) {
        this.currentNewsSource = currentNewsSource;
    }

    public List<NewsArticle> getCurrentNewsArticles() {
        return currentNewsArticles;
    }

    public void setCurrentNewsArticles(List<NewsArticle> currentNewsArticles) {
        this.currentNewsArticles = currentNewsArticles;
    }

    public int getArticlePosition() {
        return articlePosition;
    }

    public void setArticlePosition(int articlePosition) {
        this.articlePosition = articlePosition;
    }
}
