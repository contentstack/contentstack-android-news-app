package com.raweng.contentstackapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.text.SimpleDateFormat;
import java.util.Locale;

/***
 * Created by built.io.
 */
public class NewsDetailActivity extends AppCompatActivity {

    private String newsHeadline;
    private String categoryName;
    private String description;
    private String url;

    ImageView newsBanner;
    TextView  newsHeadlineTextView;
    WebView   newsDescription;
    Toolbar   toolbar;

    AQuery aQuery;
    BitmapAjaxCallback ajaxCallback;
    private ProgressBar progress;
    private TextView newsCategoryTime;
    private String creationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView back = (ImageView) toolbar.findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null){
            Bundle bundle = intent.getExtras();
            newsHeadline = bundle.getString("title");
            categoryName = bundle.getString("categoryName");
            description  = bundle.getString("description");
            url          = bundle.getString("url");
            creationTime = bundle.getString("creationTime");
        }

        init();

        newsCategoryTime.setText(categoryName + " | " + creationTime);

        if(url != null) {
            ajaxCallback = new BitmapAjaxCallback() {
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

            ajaxCallback.url(url);
            ajaxCallback.memCache(true);
            ajaxCallback.fileCache(true);

            ajaxCallback.progress(progress);

            aQuery.id(newsBanner).image(ajaxCallback);
        }else{
            progress.setVisibility(View.GONE);
        }

        newsHeadlineTextView.setText(newsHeadline);
        //newsDescription.setText(Html.fromHtml(description));

        newsDescription.loadDataWithBaseURL(null, description, "text/html", "utf-8", null);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void init() {
        newsBanner = (ImageView) findViewById(R.id.newsBanner);
        newsHeadlineTextView = (TextView) findViewById(R.id.newsTitle);
        newsDescription = (WebView) findViewById(R.id.newsDetails);
        progress        = (ProgressBar) findViewById(R.id.newsDetailsProgress);
        newsCategoryTime = (TextView) findViewById(R.id.newsCategoryTimeText);

        progress.getIndeterminateDrawable().setColorFilter(NewsDetailActivity.this.getResources().getColor(R.color.primaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);

        aQuery = new AQuery(NewsDetailActivity.this);

        Window window = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primaryDark));
        }
    }
}
