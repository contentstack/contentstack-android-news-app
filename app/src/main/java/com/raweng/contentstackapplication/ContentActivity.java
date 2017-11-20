package com.raweng.contentstackapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.builtio.contentstack.Entry;
import com.builtio.contentstack.ContentType;
import com.builtio.contentstack.Query;
import com.builtio.contentstack.QueryResult;
import com.builtio.contentstack.QueryResultsCallBack;
import com.builtio.contentstack.ResponseType;
import com.builtio.contentstack.Stack;
import com.raweng.contentstackapplication.utils.AppSharedPreferences;

import java.util.ArrayList;
import java.util.Locale;

/***
 * Created by built.io.
 */
public class ContentActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Context context                      = null;
    private CharSequence mTitle                  = null;
    public String[] categoriesList               = new String[]{};
    private boolean isMenusFetched               = false;
    private EnvironmentFragment                    environmentFragment;
    private ImageView languageImageView          = null;
    private TextView  upcaretTextView            = null;
    private Toolbar toolbar                      = null;
    private ArrayList<Entry> entries             = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        if(AppSharedPreferences.getLocale(ContentActivity.this) == null){
            AppSharedPreferences.setLocale(ContentActivity.this,
            com.builtio.contentstack.Language.ENGLISH_UNITED_STATES.name());
        }

        initialize();

        if(!isMenusFetched) {
            showMenu(true);
        }

        languageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(upcaretTextView.getVisibility() == View.VISIBLE){
                    upcaretTextView.setVisibility(View.GONE);
                }else if(upcaretTextView.getVisibility() == View.GONE){
                    upcaretTextView.setVisibility(View.VISIBLE);
                }

                showLanguageSelection(v);
            }
        });

    }

    private void initialize() {
        context           = ContentActivity.this;
        toolbar           = (Toolbar) findViewById(R.id.toolbar);
        languageImageView = (ImageView) toolbar.findViewById(R.id.toolbar_lang);
        upcaretTextView   = (TextView) toolbar.findViewById(R.id.upcaretTextView);

        AssetManager am = ContentActivity.this.getAssets();
        Typeface font   = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "fontawesome-webfont.ttf"));

        upcaretTextView.setTypeface(font);
        setSupportActionBar(toolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar, entries, categoriesList);

        Window window = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primaryDark));
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        environmentFragment = new EnvironmentFragment();
        Bundle args         = new Bundle();

        args.putInt("section_number", position + 1);
        args.putBoolean("menuFetch", isMenusFetched);

        if(isMenusFetched) {
            args.putString("category_uid", categoriesList[position]);
        }

        environmentFragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.container, environmentFragment).commit();
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.content, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void showMenu(boolean isMenuFetchNeeded){
        if(isMenuFetchNeeded) {
            toolbar.setClickable(false);
            Query query = ContentApplication.getStackInstance().contentType("category").query();

            if (AppSharedPreferences.getLocale(ContentActivity.this).equals(com.builtio.contentstack.Language.ENGLISH_UNITED_STATES.name())) {
                query.language(com.builtio.contentstack.Language.ENGLISH_UNITED_STATES);
            } else {
                query.language(com.builtio.contentstack.Language.HINDI_INDIA);
            }

            query.find(new QueryResultsCallBack() {
                @Override
                public void onCompletion(ResponseType responseType, QueryResult queryResult, com.builtio.contentstack.Error error) {

                    if (error == null) {
                        entries = (ArrayList<Entry>) queryResult.getResultObjects();
                        categoriesList = new String[entries.size() + 1];

                        categoriesList[0] = (AppSharedPreferences.getLocale(ContentActivity.this).equals(com.builtio.contentstack.Language.ENGLISH_UNITED_STATES.name())) ? context.getResources().getString(R.string.top_news) : context.getResources().getString(R.string.top_news_hindi);

                        for (int i = 0; i < entries.size(); i++) {
                            categoriesList[i + 1] = entries.get(i).getUid();
                            Log.e("UID: ", entries.get(i).getUid());
                        }

                        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar, entries, categoriesList);
                        mNavigationDrawerFragment.closeDrawer();

                        toolbar.setClickable(true);

                        if (!isMenusFetched) {
                            environmentFragment.makeCallForNEWS(categoriesList, 0);
                        }

                    } else {
                        Toast.makeText(ContentActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }

                    isMenusFetched = true;
                }
            });
        }else{
            environmentFragment.makeCallForNEWS(categoriesList, 0);
        }
    }

    private void showLanguageSelection(View view) {

        final PopupWindow popup = new PopupWindow(ContentActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.popup_lang, null);

        if(AppSharedPreferences.getLocale(ContentActivity.this).equals(com.builtio.contentstack.Language.ENGLISH_UNITED_STATES.name())) {
            ((TextView) layout.findViewById(R.id.englishTextView)).setTextColor(getResources().getColor(android.R.color.black));
            ((TextView) layout.findViewById(R.id.hindiTextView)).setTextColor(getResources().getColor(android.R.color.darker_gray));
        }else{
            ((TextView) layout.findViewById(R.id.hindiTextView)).setTextColor(getResources().getColor(android.R.color.black));
            ((TextView) layout.findViewById(R.id.englishTextView)).setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        popup.setContentView(layout);

        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        ((TextView) layout.findViewById(R.id.englishTextView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSharedPreferences.setLocale(ContentActivity.this, com.builtio.contentstack.Language.ENGLISH_UNITED_STATES.name());
                popup.dismiss();
                environmentFragment.refreshAfterLanguageChange(true);
            }
        });

        ((TextView) layout.findViewById(R.id.hindiTextView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSharedPreferences.setLocale(ContentActivity.this, com.builtio.contentstack.Language.HINDI_INDIA.name());
                popup.dismiss();
                environmentFragment.refreshAfterLanguageChange(true);
            }
        });

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                upcaretTextView.setVisibility(View.GONE);
            }
        });

        popup.showAsDropDown(view);
    }
}
