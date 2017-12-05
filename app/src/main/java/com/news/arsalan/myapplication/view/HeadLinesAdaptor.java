package com.news.arsalan.myapplication.view;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.news.arsalan.myapplication.R;
import com.news.arsalan.myapplication.model.Article;
import com.news.arsalan.myapplication.utility.StringUtil;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Arsalan Bahojb
 * on 2017-12-03.
 */

public class HeadLinesAdaptor extends RecyclerView.Adapter<HeadLinesAdaptor.ViewHolder> {
    private List<Article> articleList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    HeadLinesAdaptor(List<Article> articleListNew, Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        if (articleListNew != null) {
            this.articleList = articleListNew;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.article_summery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Article article = articleList.get(position);
        String sourceAndTitle = String.format("<b>%s</b> %s", article.source.name, article.title);
        holder.articleSummeryText.setText(Html.fromHtml(sourceAndTitle));

        String date = StringUtil.getDate(article.articlePublishedDate);


        holder.articleDate.setText(date);

        Picasso.Builder builder = new Picasso.Builder(context.getApplicationContext());
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                ViewGroup.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 10f);
                holder.layout.setLayoutParams(params);
            }
        });

        if (article.articleImageUrl != null && !article.articleImageUrl.isEmpty()) {
            builder.build().load(article.articleImageUrl).fit().into(holder.articleImage);
        } else {
            ViewGroup.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 10f);
            holder.layout.setLayoutParams(params);
        }
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
        holder.layout.setAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return articleList != null ? articleList.size() : 0;
    }

    void updateArticleSummeryList(List<Article> summeryList) {
        if (articleList != null) {
            this.articleList.clear();
            this.articleList.addAll(summeryList);
        } else {
            this.articleList = summeryList;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout;
        ImageView articleImage;
        TextView articleSummeryText;
        TextView articleDate;

        ViewHolder(View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.article_image);
            articleSummeryText = itemView.findViewById(R.id.article_summery_text);
            articleDate = itemView.findViewById(R.id.article_summery_date);
            layout = itemView.findViewById(R.id.article_data_layout);
        }

    }
}
