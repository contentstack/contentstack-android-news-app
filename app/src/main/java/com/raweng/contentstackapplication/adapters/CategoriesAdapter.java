package com.raweng.contentstackapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.builtio.contentstack.Entry;
import com.raweng.contentstackapplication.R;
import com.raweng.contentstackapplication.viewholder.CategoriesViewHolder;

import java.util.ArrayList;

/**
 * Created by built.io.
 */
public class CategoriesAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private ArrayList<Entry> categories;
    private String[] list;
    private LayoutInflater layoutInflater;
    public int selectedPosition = 0;

    public CategoriesAdapter(Context context, int resource, ArrayList<Entry> categories, String[] list) {
        super(context, resource);

        this.context        = context;
        this.resource       = resource;
        this.categories     = categories;
        this.list           = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CategoriesViewHolder categoriesViewHolder = null;

        if(convertView == null){
            convertView = layoutInflater.inflate(resource, parent, false);
            categoriesViewHolder = new CategoriesViewHolder(context);
            categoriesViewHolder.categoriesTextView = (TextView) convertView.findViewById(R.id.categories_text);
            categoriesViewHolder.populateView(position, categories, list);
        }else{
            convertView.setTag(categoriesViewHolder);
        }

        if(selectedPosition == position){
            convertView.findViewById(R.id.categories_text).setBackgroundColor(context.getResources().getColor(R.color.menu_text_color));
            ((TextView)convertView.findViewById(R.id.categories_text)).setTextColor(context.getResources().getColor(android.R.color.white));
        }else{
            convertView.findViewById(R.id.categories_text).setBackgroundColor(context.getResources().getColor(android.R.color.white));
            ((TextView)convertView.findViewById(R.id.categories_text)).setTextColor(context.getResources().getColor(R.color.menu_text_color));
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return list.length;
    }
}
