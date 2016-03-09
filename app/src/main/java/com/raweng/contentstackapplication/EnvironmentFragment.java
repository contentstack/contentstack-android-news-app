package com.raweng.contentstackapplication;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.builtio.contentstack.Language;
import com.builtio.contentstack.Query;
import com.builtio.contentstack.QueryResult;
import com.builtio.contentstack.QueryResultsCallBack;
import com.builtio.contentstack.ResponseType;
import com.builtio.contentstack.*;
import com.raweng.contentstackapplication.adapters.EnvironmentRecyclerAdapter;
import com.raweng.contentstackapplication.adapters.NewsBannerPagerAdapter;
import com.raweng.contentstackapplication.thirdpartyutils.FixedSpeedScroller;
import com.raweng.contentstackapplication.thirdpartyutils.ProgressBarCircularIndeterminate;
import com.raweng.contentstackapplication.utils.AppSharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/***
 * Created by built.io.
 */
public class EnvironmentFragment extends Fragment {

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    ViewPager viewPager;
    TextView topNewsText;

    NewsBannerPagerAdapter newsBannerPagerAdapter = null;
    EnvironmentRecyclerAdapter recyclerAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private int scrollPosition    = 0;

    Handler handler = new Handler();
    int currentPage = 0;
    private Timer swipeTimer;
    private Runnable Update;
    private String category = null;
    private String categoryUid = null;
    private View sepretorView;

    private int topNewsSize = 0;
    private ProgressBarCircularIndeterminate indeterminate;
    private boolean isMenuFetch;
    ArrayList<Entry> topEntries = new ArrayList<Entry>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_enviroment, container, false);

        init(rootView);

        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);

        Bundle bundle = getArguments();
        isMenuFetch = bundle.getBoolean("menuFetch");
        if(isMenuFetch) {
            categoryUid = bundle.getString("category_uid");
            refreshAfterLanguageChange(false);
        }

        pagerAutoScroll();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAfterLanguageChange(true);
            }
        });

        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void init(View rootView){
        recyclerView   = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        viewPager      = (ViewPager) rootView.findViewById(R.id.topNewsViewPager);
        topNewsText    = (TextView) rootView.findViewById(R.id.top_news_text);
        refreshLayout  = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeView);
        sepretorView   = (View) rootView.findViewById(R.id.top_news_separator);
        indeterminate  = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.progress);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        if(!isMenuFetch){
            indeterminate.setVisibility(View.VISIBLE);
        }

        sepretorView.setVisibility(View.INVISIBLE);
        topNewsText.setVisibility(View.INVISIBLE);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.primaryDark));
    }


    private void fetchEntries(final String categoryUid, boolean isMenuChangeNeeded) {

        if(!isMenuChangeNeeded && indeterminate.getVisibility() == View.GONE){
            indeterminate.setVisibility(View.VISIBLE);
        }else{
            refreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
        }

        lockScreenOrientation();
        Query query = ContentApplication.getStackInstance().contentType("news").query();

        query = AppSharedPreferences.getLocale(getActivity()).equals(Language.ENGLISH_UNITED_STATES.name()) ? query.language(Language.ENGLISH_UNITED_STATES) : query.language(Language.HINDI_INDIA);
        query = (categoryUid == null || categoryUid.equalsIgnoreCase(getString(R.string.top_news)) || categoryUid.equalsIgnoreCase(getString(R.string.top_news_hindi))) ? query.where("topnews", true) : query.where("category", categoryUid);

        query.includeReference("category");
        query.ascending("updated_at");
        query.find(new QueryResultsCallBack() {
            @Override
            public void onCompletion(ResponseType responseType, QueryResult queryResult, com.builtio.contentstack.Error error) {
                unlockScreenOrientation();
                if (error == null) {
                    ArrayList<Entry> entries = (ArrayList<Entry>) queryResult.getResultObjects();
                    ArrayList<Entry> categoriesEntries = new ArrayList<Entry>();

                    for (Entry entry : entries) {

                        JSONArray jsonArray = entry.getJSONArray("category");

                        if (jsonArray != null && jsonArray.length() > 0) {
                            JSONObject uidJSON = jsonArray.optJSONObject(0);

                            if ((categoryUid == null || categoryUid.equalsIgnoreCase(getString(R.string.top_news)) || categoryUid.equalsIgnoreCase(getString(R.string.top_news_hindi))) && entry.getBoolean("top_news")) {
                                topEntries.add(entry);
                                categoriesEntries.add(entry);

                            } else if (categoryUid != null) {

                                if (category == null) {
                                    category = uidJSON.optJSONObject("_metadata").optString("title");
                                }

                                if (categoryUid.equalsIgnoreCase(uidJSON.optJSONObject("_metadata").optString("uid"))) {
                                    categoriesEntries.add(entry);
                                }

                                if (categoryUid.equalsIgnoreCase(uidJSON.optJSONObject("_metadata").optString("uid")) && entry.getBoolean("topnews")) {
                                    topEntries.add(entry);
                                }
                            }
                        }
                    }

                    String lang = AppSharedPreferences.getLocale(getActivity()).equals(Language.ENGLISH_UNITED_STATES.name()) ? getActivity().getResources().getString(R.string.top_news) : getActivity().getResources().getString(R.string.top_news_hindi);
                    lang = categoryUid == null ? lang : category;

                    topNewsText.setText(lang);

                    topNewsText.setVisibility(View.VISIBLE);
                    sepretorView.setVisibility(View.VISIBLE);

                    recyclerAdapter = new EnvironmentRecyclerAdapter(getActivity(), categoriesEntries, category);
                    recyclerView.setAdapter(recyclerAdapter);

                    newsBannerPagerAdapter = new NewsBannerPagerAdapter(getActivity(), topEntries);
                    viewPager.setAdapter(newsBannerPagerAdapter);

                    topNewsSize = topEntries.size();
                    if (categoriesEntries.size() > 1) {
                        try {
                            Field mScroller;
                            mScroller = ViewPager.class.getDeclaredField("mScroller");
                            mScroller.setAccessible(true);
                            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext());
                            mScroller.set(viewPager, scroller);
                        } catch (Exception e) {
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }

                if (indeterminate.getVisibility() == View.VISIBLE) {
                    indeterminate.setVisibility(View.GONE);
                }
            }
        });
    }

    public void makeCallForNEWS(String[] categoriesList, int position) {

        if(position == 0){
            fetchEntries(null, false);
        }else{
            fetchEntries(categoriesList[position], false);
        }
    }

    public void refreshAfterLanguageChange(boolean isMenuChangeNeeded){

        ((ContentActivity)getActivity()).showMenu(isMenuChangeNeeded);
        fetchEntries(categoryUid, isMenuChangeNeeded);
    }

    private void pagerAutoScroll(){
        currentPage = 0;
        Update = new Runnable() {
            public void run() {
                if (currentPage == topNewsSize) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if(getActivity() != null) {
            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    private void unlockScreenOrientation() {
        if(getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
}
