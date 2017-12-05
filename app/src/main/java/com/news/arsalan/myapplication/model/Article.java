package com.news.arsalan.myapplication.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Arsalan Bahojb
 * on 2017-12-01.
 */

public class Article implements Comparable {

    @SerializedName("source")
    public Source source;

    @SerializedName("author")
    public String author;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("url")
    public String sourceUrl;

    @SerializedName("urlToImage")
    public String articleImageUrl;

    @SerializedName("publishedAt")
    public String articlePublishedDate;

    @NotNull
    private String getArticleDateForComparator() {
        return articlePublishedDate != null ? articlePublishedDate : "";
    }

    @Override
    public int compareTo(@NonNull Object object) {
        int result;
        if (object instanceof Article) {
            Article otherArticle = (Article) object;
            result = otherArticle.getArticleDateForComparator().compareTo(this.getArticleDateForComparator());
        } else {
            result = -1;
        }

        return result;
    }
}
