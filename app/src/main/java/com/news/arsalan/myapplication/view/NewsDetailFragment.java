package com.news.arsalan.myapplication.view;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.news.arsalan.myapplication.R;
import com.news.arsalan.myapplication.utility.StringUtil;

import static com.news.arsalan.myapplication.Constant.NEWS_DETAIL_URI;


public class NewsDetailFragment extends Fragment {

    private static String TAG = NewsDetailFragment.class.getSimpleName();
    private String newsLink;
    private WebView newsDetailWebView;
    private ProgressDialog progressDialog;
    private OnNewsDetailViewStateListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String uriString = this.getArguments().getString(NEWS_DETAIL_URI);
        newsLink = StringUtil.isNotEmpty(uriString) ? uriString : "";
        View rootView = inflater.inflate(R.layout.fragment_news_deatil, container, false);
        newsDetailWebView = rootView.findViewById(R.id.news_details_web_view);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        startWebView(newsLink);
        listener.onNewsDetailViewState(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewsDetailViewStateListener) {
            listener = (NewsDetailFragment.OnNewsDetailViewStateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnNewsDetailViewStateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void startWebView(String url) {

        WebSettings settings = newsDetailWebView.getSettings();

        settings.setJavaScriptEnabled(true);
        newsDetailWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        newsDetailWebView.getSettings().setBuiltInZoomControls(true);
        newsDetailWebView.getSettings().setUseWideViewPort(true);
        newsDetailWebView.getSettings().setLoadWithOverviewMode(true);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        newsDetailWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dismissProgressDialog();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                dismissProgressDialog();
                Log.e(TAG, String.format("Some problem with downloading the link:%s, Error Code:%d", description, errorCode));
            }
        });
        newsDetailWebView.loadUrl(url);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgressDialog();
        listener.onNewsDetailViewState(false);
    }

    public void updateUrl(Uri uri) {
        startWebView(uri.toString());
    }

    private void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public interface OnNewsDetailViewStateListener {
        void onNewsDetailViewState(boolean state);
    }
}
