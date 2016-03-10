package com.raweng.contentstackapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.builtio.contentstack.Entry;
import com.raweng.contentstackapplication.ContentApplication;
import com.raweng.contentstackapplication.NewsDetailActivity;
import com.raweng.contentstackapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/***
 * Created by built.io.
 */
public class NewsBannerPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<Entry> categoriesEntries;

    public NewsBannerPagerAdapter(Context context, ArrayList<Entry> categoriesEntries) {

        this.context = context;
        this.categoriesEntries = categoriesEntries;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_top_news_banner, container, false);

        ProgressBar progressBar      = (ProgressBar) view.findViewById(R.id.topNewsProgress);
        ImageView   imageView        = (ImageView) view.findViewById(R.id.topNewsBanner);
        TextView    textView         = (TextView) view.findViewById(R.id.topNewsTitle);
        TextView    categoryTimeText = (TextView) view.findViewById(R.id.topNewsCategoryTimeText);

        progressBar.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.primaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);

        String categoryName = categoriesEntries.get(position).getJSONArray("category").optJSONObject(0).optString("title");

        final SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        categoryTimeText.setText(categoryName + " | " + format.format(categoriesEntries.get(position).getCreateAt().getTime()));

        if(categoriesEntries.get(position).getJSONObject("banner") != null) {
            BitmapAjaxCallback ajaxCallback = new BitmapAjaxCallback() {

                @Override
                protected void showProgress(boolean show) {
                    super.showProgress(true);
                }

                @Override
                protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    super.callback(url, iv, bm, status);
                }
            };

            ajaxCallback.header("site_api_key", ContentApplication.CONTENTSTACK_API_KEY);
            ajaxCallback.header("Authtoken", ContentApplication.CONTENTSTACK_ACCESS_TOKEN);


            ajaxCallback.url(categoriesEntries.get(position).getJSONObject("banner").optString("url"));

            ajaxCallback.memCache(true);
            ajaxCallback.fileCache(true);
            ajaxCallback.targetWidth(300);

            ajaxCallback.progress(progressBar);

            AQuery aQuery = new AQuery((Activity) context);
            aQuery.id(imageView).image(ajaxCallback);
        }else{
            progressBar.setVisibility(View.GONE);
        }

        textView.setText(categoriesEntries.get(position).getString("title"));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newsDetailsIntent = new Intent(context, NewsDetailActivity.class);
                newsDetailsIntent.putExtra("title", categoriesEntries.get(position).getString("title"));
                newsDetailsIntent.putExtra("categoryName", categoriesEntries.get(position).getJSONArray("category").optJSONObject(0).optString("title"));
                newsDetailsIntent.putExtra("description", categoriesEntries.get(position).getString("body"));
                newsDetailsIntent.putExtra("creationTime", format.format(categoriesEntries.get(position).getCreateAt().getTime()));

                if (categoriesEntries.get(position).getJSONObject("banner").optString("url") != null) {
                    newsDetailsIntent.putExtra("url", categoriesEntries.get(position).getJSONObject("banner").optString("url"));
                }
                context.startActivity(newsDetailsIntent);

            }
        });

        ((ViewPager) container).addView(view, 0);

        return view;
    }

    @Override
    public int getCount() {
        return categoriesEntries.size();
    }


    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);

    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
