package com.raweng.contentstackapplication.viewholder;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.builtio.contentstack.Entry;
import com.builtio.contentstack.Entry;
import com.raweng.contentstackapplication.R;
import com.raweng.contentstackapplication.utils.AppSharedPreferences;

import org.json.JSONObject;

import java.util.ArrayList;

/***
 * Created by built.io.
 */
public class CategoriesViewHolder {

    public TextView categoriesTextView;
    public Context context;

    public CategoriesViewHolder(Context context) {
        this.context = context;
    }

    public void populateView(int position, Entry category) {

    }

    public void populateView(int position, ArrayList<Entry> categories, String[] list) {
        String categoryName = null;

        if(position == 0) {
            categoryName = (AppSharedPreferences.getLocale(context).equals(com.builtio.contentstack.Language.ENGLISH_UNITED_STATES.name())) ? context.getResources().getString(R.string.top_news) : context.getResources().getString(R.string.top_news_hindi);
        }else{
            categoryName = categories.get(position-1).getString("title");
        }

        categoriesTextView.setText(Html.fromHtml(categoryName));
    }
}
