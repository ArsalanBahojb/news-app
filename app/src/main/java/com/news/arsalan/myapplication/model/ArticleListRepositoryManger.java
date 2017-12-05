package com.news.arsalan.myapplication.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Arsalan Bahojb
 * on 2017-12-04.
 */

public class ArticleListRepositoryManger implements ArticleListRepository {


    private static final String PREFERENCE_NAME = "currency_list";
    private static final String PREFERENCE_KEY = "data";

    private ArrayList<Article> articleArrayList = new ArrayList<>();

    @Override
    public void updateArticleList(Context context, List<Article> result) {
        articleArrayList.clear();
        articleArrayList.addAll(result);
        saveData(context);
    }

    void saveData(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String articleStringList = gson.toJson(articleArrayList);
        prefsEditor.putString(PREFERENCE_KEY, articleStringList);
        prefsEditor.apply();
    }

    @Override
    public void uploadArticlesFromRepository(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(PREFERENCE_KEY, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<Article>>() {
            }.getType();
            articleArrayList = gson.fromJson(json, type);
        }
    }

    @Override
    public List<Article> getArticleList() {
        return articleArrayList;
    }

    @Override
    public boolean isEmpty() {
        return (articleArrayList == null) || articleArrayList.isEmpty();
    }
}
