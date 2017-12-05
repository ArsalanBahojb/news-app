package com.news.arsalan.myapplication.view;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.news.arsalan.myapplication.R;
import com.news.arsalan.myapplication.utility.StringUtil;

import static com.news.arsalan.myapplication.Constant.NEWS_DETAIL_URI;

public class NewsActivity extends AppCompatActivity implements NewsHeadLineFragment.OnFragmentInteractionListener, NewsDetailFragment.OnNewsDetailViewStateListener {

    private static String TAG = NewsActivity.class.getSimpleName();
    private final NewsDetailFragment newsDetailFragment = new NewsDetailFragment();
    private final NewsHeadLineFragment headLinesFragment = new NewsHeadLineFragment();
    private android.support.v7.app.ActionBar actionBar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        context = this;
        addActionBar();
        if (findViewById(R.id.main_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            headLinesFragment.setRetainInstance(true);
            getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, headLinesFragment).commit();
        }

    }

    private void addActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_view);
            final EditText search = actionBar.getCustomView().findViewById(R.id.search_field);
            final ImageView clean_search = actionBar.getCustomView().findViewById(R.id.search_field_clean);
            ImageView back_button = actionBar.getCustomView().findViewById(R.id.back_arrow);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (search.getText().length() > 0) {
                        clean_search.setVisibility(View.VISIBLE);
                    } else {
                        clean_search.setVisibility(View.INVISIBLE);
                    }
                }
            });
            search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId,
                                              KeyEvent event) {
                    String filterText = search.getText().toString();
                    if (StringUtil.isNotEmpty(filterText)) {
                        if (!headLinesFragment.filterData(context, filterText.toLowerCase())) {
                            Toast.makeText(getApplicationContext(), String.format(getString(R.string.search_key_not_found), filterText), Toast.LENGTH_LONG).show();
                        }
                    }
                    return false;
                }
            });
            clean_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    search.setText("");
                    closeKeyBoard();
                    headLinesFragment.resetToOriginalList();
                }
            });
            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        closeKeyBoard();
        if (findViewById(R.id.news_detail_fragment_container) == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (newsDetailFragment.isAdded()) {
                transaction.remove(newsDetailFragment);
            }
            transaction.commit();
            fragmentManager.executePendingTransactions();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        if (findViewById(R.id.news_detail_fragment_container) == null) {
            Bundle bundle = new Bundle();
            bundle.putString(NEWS_DETAIL_URI, uri.toString());
            newsDetailFragment.setArguments(bundle);
            newsDetailFragment.setRetainInstance(false);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, newsDetailFragment).addToBackStack(null).commit();
            getSupportFragmentManager().executePendingTransactions();
        } else {
            if (!newsDetailFragment.isAdded() && (findViewById(R.id.news_detail_fragment_container) != null)) {
                Bundle bundle = new Bundle();
                bundle.putString(NEWS_DETAIL_URI, uri.toString());
                newsDetailFragment.setArguments(bundle);
                newsDetailFragment.setRetainInstance(false);
                FrameLayout mainFrame = findViewById(R.id.main_fragment_container);
                FrameLayout detailFrame = findViewById(R.id.news_detail_fragment_container);
                mainFrame.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                detailFrame.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2f));
                getSupportFragmentManager().beginTransaction().add(R.id.news_detail_fragment_container, newsDetailFragment).commit();
                getSupportFragmentManager().executePendingTransactions();
            } else {
                newsDetailFragment.updateUrl(uri);
            }

        }
    }

    @Override
    public void onNewsDetailViewState(boolean state) {
        final EditText search = actionBar.getCustomView().findViewById(R.id.search_field);
        final ImageView clean_search = actionBar.getCustomView().findViewById(R.id.search_field_clean);
        if (state) {
            if (findViewById(R.id.news_detail_fragment_container) == null) {
                search.setVisibility(View.INVISIBLE);
                clean_search.setVisibility(View.INVISIBLE);
            }
        } else {
            search.setVisibility(View.VISIBLE);
            if (StringUtil.isNotEmpty(search.getText().toString())) {
                clean_search.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void closeKeyBoard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            Log.e(TAG, "Force to close KeyBoard error", e);
        }

    }

}