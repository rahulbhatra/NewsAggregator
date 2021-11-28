package com.example.newsaggregator;

import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsArticleViewHolder extends RecyclerView.ViewHolder {

    TextView articleHeadline;
    TextView articleDate;
    TextView articleAuthors;
    ImageView articleImage;
    TextView articleText;
    TextView articleCount;

    public NewsArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        this.articleHeadline = itemView.findViewById(R.id.articleHeadline);
        this.articleDate = itemView.findViewById(R.id.articleDate);
        this.articleAuthors = itemView.findViewById(R.id.articleAuthors);
        this.articleImage = itemView.findViewById(R.id.articleImage);
        this.articleText = itemView.findViewById(R.id.articleText);
        this.articleText.setMovementMethod(new ScrollingMovementMethod());
        this.articleCount = itemView.findViewById(R.id.articleCount);
    }
}
