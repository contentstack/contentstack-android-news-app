package com.raweng.contentstackapplication.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.builtio.contentstack.Entry;
import com.raweng.contentstackapplication.NewsDetailActivity;
import com.raweng.contentstackapplication.R;
import com.raweng.contentstackapplication.viewholder.EnviromentViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/***
 * Created by built.io.
 */
public class EnvironmentRecyclerAdapter extends RecyclerView.Adapter<EnviromentViewHolder> {

    ArrayList<Entry> entries;
    Activity activity;
    String categoryName;

    public EnvironmentRecyclerAdapter(Activity activity, ArrayList<Entry> entries, String categoryName) {
        this.entries = entries;
        this.activity = activity;
        this.categoryName = categoryName;
    }

    @Override
    public EnviromentViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_enviroment_row, parent, false);
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_enviroment_row, parent, false);
        return new EnviromentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EnviromentViewHolder enviromentViewHolder, int i) {
        final int position = i;

        enviromentViewHolder.getNewsHeadLines().setText(entries.get(i).getString("title"));

        categoryName = entries.get(i).getJSONArray("category").optJSONObject(0).optString("title");
        enviromentViewHolder.getNewsCategory().setText(categoryName);

        final SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        final String creationTime     = format.format(entries.get(i).getCreateAt().getTime());
        enviromentViewHolder.getNewsDate().setText(creationTime);

        setNewsPic(enviromentViewHolder, position);

        enviromentViewHolder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newsDetailsIntent = new Intent(activity, NewsDetailActivity.class);
                newsDetailsIntent.putExtra("title", entries.get(position).getString("title"));
                newsDetailsIntent.putExtra("categoryName", entries.get(position).getJSONArray("category").optJSONObject(0).optString("title"));
                newsDetailsIntent.putExtra("description", entries.get(position).getString("body"));
                newsDetailsIntent.putExtra("creationTime", format.format(entries.get(position).getCreateAt().getTime()));
                if (entries.get(position).getJSONObject("featured_image") != null && entries.get(position).getJSONObject("featured_image").optString("url") != null) {
                    newsDetailsIntent.putExtra("url", entries.get(position).getJSONObject("featured_image").optString("url"));
                }

                activity.startActivity(newsDetailsIntent);
            }
        });
    }

    private void setNewsPic(EnviromentViewHolder enviromentViewHolder, int position) {
        enviromentViewHolder.getNewsImageView().setImageResource(R.drawable.ic_default_news);
        BitmapAjaxCallback ajaxCallback = new BitmapAjaxCallback() {

            @Override
            protected void showProgress(boolean show) {
                super.showProgress(false);

            }

            @Override
            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                super.callback(url, iv, bm, status);
            }
        };

        ajaxCallback.header("site_api_key", "***REMOVED***");
        ajaxCallback.header("Authtoken", "***REMOVED***");

        ajaxCallback.url(entries.get(position).getJSONObject("thumbnail").optString("url"));
        ajaxCallback.memCache(true);
        ajaxCallback.fileCache(true);

        ajaxCallback.targetWidth(150);
        ajaxCallback.fallback(R.drawable.ic_default_news);
       // ajaxCallback.progress(enviromentViewHolder.getProgressBar());

        AQuery aQuery = new AQuery(activity);
        aQuery.id(enviromentViewHolder.getNewsImageView()).image(ajaxCallback);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }


}
