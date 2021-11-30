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

public class NewsSourcesApiRunnable implements Runnable {
    private static final String TAG = "NewsApiRunnable";
    private MainActivity mainActivity;
    private static String NEWS_API_KEY = "a9d28d61c1fa4d44aa4a47bc6814b4d9";
    private static String newSourcesUrl = "https://newsapi.org/v2/top-headlines/sources";

    public NewsSourcesApiRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(newSourcesUrl).buildUpon();
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

        final List<NewsSource> newsSources = parseJSON(s);
        mainActivity.runOnUiThread(() -> {
            if (newsSources.size() != 0) {
                mainActivity.updateNewsResourcesAndCreateMenu(newsSources);
            }
        });
    }

    private List<NewsSource> parseJSON(String s) {
        List<NewsSource> newsSources = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("sources")) {
                JSONArray sources = jsonObject.getJSONArray("sources");
                for(int i = 0; i < sources.length(); i++) {
                    JSONObject jsonSource = sources.getJSONObject(i);
                    NewsSource source = new NewsSource(
                            jsonSource.getString("id"),
                            jsonSource.getString("name"),
                            jsonSource.getString("description"),
                            jsonSource.getString("url"),
                            jsonSource.getString("category"),
                            jsonSource.getString("language"),
                            jsonSource.getString("country")
                    );
                    newsSources.add(source);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsSources;
    }
}
