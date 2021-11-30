package com.example.newsaggregator;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsArticlesApiRunnable implements Runnable {

    private static final String TAG = "NewsApiRunnable";
    private MainActivity mainActivity;
    private static String NEWS_API_KEY = "a9d28d61c1fa4d44aa4a47bc6814b4d9";
    private static String newSourcesUrl = "https://newsapi.org/v2/top-headlines";
    private String source = "";
    private String sourceName = "";

    public NewsArticlesApiRunnable(String source, String sourceName, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.source = source;
        this.sourceName = sourceName;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(newSourcesUrl).buildUpon();
        uriBuilder.appendQueryParameter("sources", source);
        uriBuilder.appendQueryParameter("apiKey", NEWS_API_KEY);

        String newSourceUrl = uriBuilder.build().toString();
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(newSourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent", "");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode() + " , " +conn.getResponseMessage());
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            reader.close();
            is.close();

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        handleResults(sb.toString());
    }

    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        final List<NewsArticle> newsArticles = parseJSON(s);
        mainActivity.runOnUiThread(() -> {
            if (newsArticles.size() != 0) {
                mainActivity.updateNewsArticles(sourceName, newsArticles);
            }
        });
    }

    private List<NewsArticle> parseJSON(String s) {
        List<NewsArticle> newsArticles = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("articles")) {
                JSONArray sources = jsonObject.getJSONArray("articles");
                for(int i = 0; i < sources.length(); i++) {
                    JSONObject jsonSource = sources.getJSONObject(i);
                    NewsArticle newsArticle = new NewsArticle(
                            this.source,
                            jsonSource.getString("author"),
                            jsonSource.getString("title"),
                            jsonSource.getString("description"),
                            jsonSource.getString("url"),
                            jsonSource.getString("urlToImage"),
                            jsonSource.getString("publishedAt"),
                            jsonSource.getString("content")
                    );
                    newsArticles.add(newsArticle);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsArticles;
    }
}
