package com.news.arsalan.myapplication.model;

import android.content.Context;

import java.util.List;

/**
 * Created by Arsalan Bahojb
 * on 2017-12-04.
 */

public interface ArticleListRepository {

    void updateArticleList(Context context,  List<Article> result);

    void uploadArticlesFromRepository(Context context);

    List<Article> getArticleList();

    boolean isEmpty();
}
