package com.raweng.contentstackapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.builtio.contentstack.Contentstack;
import com.builtio.contentstack.Entry;
import com.builtio.contentstack.EntryResultCallBack;
import com.builtio.contentstack.Error;
import com.builtio.contentstack.Query;
import com.builtio.contentstack.QueryResult;
import com.builtio.contentstack.QueryResultsCallBack;
import com.builtio.contentstack.ResponseType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;


/***
 * Created by built.io.
 */
public class NewsDetailActivity extends AppCompatActivity {

    private String         newsHeadline = "";
    private String         categoryName= "";
    private String         description= "";
    private String         url= "";
    private String         uid= "";

    private ImageView      newsBanner;
    private TextView       newsHeadlineTextView;
    private TextView       newsDescription;
    private Toolbar        toolbar;
    private ImageView      back;

    private ProgressBar    progress;
    private TextView       newsCategoryTime;
    private String         creationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        back = (ImageView) toolbar.findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

            if (uid!=null && !uid.equalsIgnoreCase("")){
                Log.e("uid-->", uid.trim());
                final Entry entry = ContentApplication.getStackInstance().contentType("news").entry(uid);
                Log.e("Entry object: ",entry.toString());

                entry.fetch(new EntryResultCallBack() {
                    @Override
                    public void onCompletion(ResponseType responseType, Error error) {

                        if (error == null) {

                            Log.e("category_uid", entry.toJSON().toString());

                            JSONObject objectEntry = entry.toJSON();
                            if (objectEntry.has("title")){
                                newsHeadline=  objectEntry.optString("title");
                                Log.e("newsHeadline", newsHeadline);
                            }
                            if (objectEntry.has("categoryName")){
                                categoryName =  objectEntry.optString("categoryName");
                                categoryName = "Technology";
                                Log.e("categoryName", categoryName);
                            }
                            if (objectEntry.has("body")){
                                description =  objectEntry.optString("body");
                                Log.e("description", description);
                            }
                            if (objectEntry.has("featured_image")){
                                try {
                                    url =  objectEntry.getJSONObject("featured_image").optString("url");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.e("url", url);
                            }
                            if (objectEntry.has("created_at")){
                                String[] categoryTime =  objectEntry.optString("created_at").split("T");
                                creationTime = categoryTime[0];
                                Log.e("creationTime", creationTime);
                            }

                            init();

                        } else {
                            Toast.makeText(NewsDetailActivity.this, "Could not load current uid", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }else {

                Bundle bundle = intent.getExtras();

                if(intent != null && intent.getExtras() != null){
                    newsHeadline = bundle.getString("title");
                    categoryName = bundle.getString("categoryName");
                    description  = bundle.getString("description");
                    url          = bundle.getString("url");
                    creationTime = bundle.getString("creationTime");


                    init();
                }
            }

    }



    private void init() {

        newsBanner = (ImageView) findViewById(R.id.newsBanner);
        newsHeadlineTextView = (TextView) findViewById(R.id.newsTitle);
        newsDescription = (TextView) findViewById(R.id.newsDetails);
        progress        = (ProgressBar) findViewById(R.id.newsDetailsProgress);
        newsCategoryTime = (TextView) findViewById(R.id.newsCategoryTimeText);


        newsCategoryTime.setText(categoryName + " | " + creationTime);
        newsHeadlineTextView.setText(newsHeadline);
        newsDescription.setText(Html.fromHtml(description));
        Glide.with(getApplicationContext()).load(url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(newsBanner);



        progress.getIndeterminateDrawable().setColorFilter(NewsDetailActivity.this.getResources().getColor(R.color.primaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);

        Window window = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primaryDark));
        }

        //newsDescription.loadDataWithBaseURL(null, description, "text/html", "utf-8", null);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
