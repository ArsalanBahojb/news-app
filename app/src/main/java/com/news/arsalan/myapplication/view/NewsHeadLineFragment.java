package com.news.arsalan.myapplication.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.news.arsalan.myapplication.Constant;
import com.news.arsalan.myapplication.R;
import com.news.arsalan.myapplication.model.Article;
import com.news.arsalan.myapplication.model.ArticleListRepository;
import com.news.arsalan.myapplication.model.ArticleListRepositoryManger;
import com.news.arsalan.myapplication.utility.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.sort;


public class NewsHeadLineFragment extends Fragment {
    private static final long UPDATE_DELAY = 10 * 60;
    private static final String TAG = NewsDetailFragment.class.getSimpleName();
    private static long lastUpdateTimeStamp;
    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, error.toString());
        }
    };
    private Gson gson;
    private List<Article> articleList;
    private ArticleListRepository repository;
    private OnFragmentInteractionListener mListener;
    private HeadLinesAdaptor adaptor;
    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.i(TAG, response);

            try {
                JSONObject responseJson = new JSONObject(response);
                String articles = responseJson.getString("articles");
                articleList = new ArrayList<>(Arrays.asList(gson.fromJson(articles, Article[].class)));
                sort(articleList);

                if (adaptor != null) {
                    adaptor.updateArticleSummeryList(articleList);
                    adaptor.notifyDataSetChanged();
                    repository.updateArticleList(getContext(), articleList);
                }
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Error", e);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to get a correct response", e);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateFromRepositoryIfNeeded(getContext());
    }

    private void fetchArticles() {
        long currentTime = System.currentTimeMillis() / 1000;
        if (((currentTime - UPDATE_DELAY) > lastUpdateTimeStamp) || (articleList == null) || articleList.isEmpty()) {
            lastUpdateTimeStamp = currentTime;
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
            StringRequest request = new StringRequest(Request.Method.GET, Constant.EVERY_THING_URL, onPostsLoaded, onPostsError);
            requestQueue.add(request);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_head_line, container, false);

        RecyclerView articleView = rootView.findViewById(R.id.articles_recycler_view);
        adaptor = new HeadLinesAdaptor(articleList, getContext());
        articleView.setAdapter(adaptor);
        articleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        articleView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext().getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        try {
                            URL newsSource = new URL(articleList.get(position).sourceUrl);
                            onArticleSelection(Uri.parse(newsSource.toURI().toString()));
                        } catch (MalformedURLException e) {
                            Log.e(TAG, "News doesn't have a valid URL", e);
                        } catch (URISyntaxException e) {
                            Log.e(TAG, "Failed to get Uri News doesn't have a valid URL", e);
                        }
                    }
                })
        );
        Log.i(TAG, " View done");

        fetchArticles();

        return rootView;
    }

    public void onArticleSelection(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateFromRepositoryIfNeeded(Context context) {
        repository = new ArticleListRepositoryManger();
        repository.uploadArticlesFromRepository(context);
        Log.e(TAG, "Get from repository size:" + repository.getArticleList().size() + "  , current:" + (articleList == null));
        if ((articleList == null || articleList.isEmpty()) && !repository.isEmpty()) {
            Log.e(TAG, "Get from repository");
            articleList = repository.getArticleList();
        }
    }

    public boolean filterData(Context context, String filter) {
        boolean result = false;
        updateFromRepositoryIfNeeded(context);
        List<Article> filterArticleList = new ArrayList<>();
        if (StringUtil.isNotEmpty(filter)) {
            if (articleList != null && articleList.size() > 0) {
                for (Article article : articleList) {
                    if (StringUtil.isNotEmpty(article.title) && article.title.toLowerCase().contains(filter)) {
                        filterArticleList.add(article);
                    }
                }
            }
        }

        if (!filterArticleList.isEmpty() && (adaptor != null)) {
            adaptor.updateArticleSummeryList(filterArticleList);
            adaptor.notifyDataSetChanged();
            result = true;
        }
        return result;
    }

    public void resetToOriginalList() {
        if (adaptor != null) {
            adaptor.updateArticleSummeryList(articleList);
            adaptor.notifyDataSetChanged();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
