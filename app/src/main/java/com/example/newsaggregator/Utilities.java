package com.example.newsaggregator;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Utilities {
    public static boolean isNetworkConnectionAvailable(Activity activity) {
        ConnectivityManager connectivityManager = activity.getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public static Map<String, Map<String, String>> getMenuOptions(Activity activity, List<NewsSource> newsSources) {
        Map<String, Map<String, String>> menuOptions = new HashMap<>();
        Map<String, String> langCodeNameMap = createCodeNameMap(activity, "languages", R.raw.language_codes);
        Map<String, String> countryCodeNameMap = createCodeNameMap(activity, "countries", R.raw.country_codes);
        menuOptions.put(activity.getString(R.string.topics), new TreeMap<>());
        menuOptions.put(activity.getString(R.string.languages), new TreeMap<>());
        menuOptions.put(activity.getString(R.string.countries), new TreeMap<>());

        for(NewsSource newsSource : newsSources) {
            String category = newsSource.getCategory();
            String language = newsSource.getLanguageCode().toUpperCase();
            String country = newsSource.getCountryCode().toUpperCase();

            menuOptions.get(activity.getString(R.string.topics)).put(category, category);
            menuOptions.get(activity.getString(R.string.languages)).put(language, langCodeNameMap.get(language));
            menuOptions.get(activity.getString(R.string.countries)).put(country, countryCodeNameMap.get(country));
        }
        return menuOptions;
    }

    public static void updateNewsSourceColorCode(List<NewsSource> newsSources, Map<String, String> topicsMap) {
        Map<String, String> topicNameColorMap = new HashMap<>();
        List<String> colorCodes = getListOfColors();

        int i = 0;
        for(String topic: topicsMap.keySet()) {
            topicNameColorMap.put(topic, colorCodes.get(i++));
        }

        for(NewsSource newsSource : newsSources) {
            String topic = newsSource.getCategory();
            newsSource.setColorCode(topicNameColorMap.get(topic));
        }
    }

    public static List<NewsSource> updateCountryNameAndLanguageName(Activity activity, List<NewsSource> newsSources) {
        Map<String, String> langCodeNameMap = createCodeNameMap(activity, "languages", R.raw.language_codes);
        Map<String, String> countryCodeNameMap = createCodeNameMap(activity, "countries", R.raw.country_codes);


        for(NewsSource newsSource : newsSources) {
            String language = newsSource.getLanguageCode().toUpperCase();
            String country = newsSource.getCountryCode().toUpperCase();


            newsSource.setLanguageName(langCodeNameMap.get(language));
            newsSource.setCountryName(countryCodeNameMap.get(country));
        }
        return newsSources;
    }


    public static Map<String, String> createCodeNameMap(Activity activity, String resourceName, int resourceId) {
        Map<String, String> codeNameMap = new HashMap<>();
        try {
            InputStream is = activity.getApplicationContext().getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray countriesJsonArray = jsonObject.getJSONArray(resourceName);
            for (int i = 0; i < countriesJsonArray.length(); i++) {
                JSONObject countryJsonObject = countriesJsonArray.getJSONObject(i);
                String code = countryJsonObject.getString("code").toUpperCase();
                String name = countryJsonObject.getString("name");
                codeNameMap.put(code, name);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeNameMap;
    }

    public static Map<String, String> createNameCodeMap(Map<String, String> codeNameMap) {
        Map<String, String> nameCodeMap = new HashMap<>();
        for (String code : codeNameMap.keySet()) {
            String name = codeNameMap.get(code);
            nameCodeMap.put(name, code);
        }
        return nameCodeMap;
    }

    public static List<NewsSource> filterNewsSources(List<NewsSource> newsSources, Map<String, String> filters, Activity activity) {
        List<NewsSource> filteredNewsSources = new ArrayList<>();
        for(NewsSource newsSource: newsSources) {
            String topics = filters.get(activity.getString(R.string.topics));
            String languages = filters.get(activity.getString(R.string.languages));
            String countries = filters.get(activity.getString(R.string.countries));

            if(topics != null && topics != "all" && !newsSource.getCategory().equalsIgnoreCase(topics)) {
                continue;
            }
            if(languages != null && languages != "all" && !newsSource.getLanguageName().equalsIgnoreCase(languages)) {
                continue;
            }
            if(countries != null && countries != "all" && !newsSource.getCountryName().equalsIgnoreCase(countries)) {
                continue;
            }
            filteredNewsSources.add(newsSource);
        }
        return filteredNewsSources;
    }

    public static List<String> getListOfColors() {
        String[] colorArray = {
                "#0000FF", "#8A2BE2", "#A52A2A", "#DEB887", "#5F9EA0", "#7FFF00",
                "#D2691E", "#FF7F50", "#6495ED", "#DC143C", "#00008B", "#008B8B"
        };
        List<String> colors = Arrays.asList(colorArray);
        return colors;
    }
}
