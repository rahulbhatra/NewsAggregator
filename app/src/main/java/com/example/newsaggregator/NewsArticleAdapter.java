package com.example.newsaggregator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleViewHolder> {

    private final MainActivity mainActivity;
    private final List<NewsArticle> newsArticles;

    public NewsArticleAdapter(MainActivity mainActivity, List<NewsArticle> newsArticles) {
        this.mainActivity = mainActivity;
        this.newsArticles = newsArticles;
    }

    @NonNull
    @Override
    public NewsArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsArticleViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.news_article, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NewsArticleViewHolder holder, int position) {
        NewsArticle newsArticle = newsArticles.get(position);
        holder.articleHeadline.setText(newsArticle.getTitle());
        holder.articleHeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewsIntent(newsArticle.getUrl());
            }
        });

        String dategot = getFormattedDateFromString(newsArticle.getPublishedAt());
        holder.articleDate.setText(dategot);

        holder.articleAuthors.setText(newsArticle.getAuthor());

        if(newsArticle.getUrlToImage() == null) {
            holder.articleImage.setImageResource(R.drawable.noimage);
        } else {
            Picasso picasso = Picasso.with(mainActivity);
            picasso.setLoggingEnabled(true);
            picasso.load(newsArticle.getUrlToImage())
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.loading)
                    .into(holder.articleImage);
        }
        holder.articleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewsIntent(newsArticle.getUrl());
            }
        });
        holder.articleText.setText(newsArticle.getDescription());
        holder.articleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewsIntent(newsArticle.getUrl());
            }
        });
        holder.articleCount.setText(String.format("%d of %d", (position + 1), newsArticles.size()));

    }

    public void startNewsIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mainActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return newsArticles.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getFormattedDateFromString(String publishedAt) {
        String dategot = "";
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_INSTANT;
            TemporalAccessor accessor = timeFormatter.parse(publishedAt);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");
            LocalDateTime localDateTime =
                    LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());
            dategot = localDateTime.format(dateTimeFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dategot.isEmpty()) {
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                TemporalAccessor accessor = timeFormatter.parse(publishedAt);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");
                LocalDateTime localDateTime =
                        LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());
                dategot = localDateTime.format(dateTimeFormatter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dategot;
    }
}
