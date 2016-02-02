package com.raweng.contentstackapplication.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raweng.contentstackapplication.R;

/***
 * Created by built.io.
 */
public class EnviromentViewHolder extends RecyclerView.ViewHolder {

    public TextView newsHeadLines;
    public TextView newsCategory;
    public TextView newsDate;
    public CardView cardView;
    public ImageView newsImageView;
    public ProgressBar progressBar;

    public EnviromentViewHolder(View itemView) {
        super(itemView);
        newsImageView = (ImageView) itemView.findViewById(R.id.newsPic);
        newsHeadLines = (TextView) itemView.findViewById(R.id.newsHeadLine);
        newsCategory  = (TextView) itemView.findViewById(R.id.newsCategoryText);
        cardView      = (CardView) itemView.findViewById(R.id.cardview);
        progressBar   = (ProgressBar) itemView.findViewById(R.id.newsPicProgress);
        progressBar.setVisibility(View.GONE);
        newsDate      = (TextView) itemView.findViewById(R.id.newsDate);
    }

    public TextView getNewsHeadLines() {
        return newsHeadLines;
    }

    public TextView getNewsCategory() {
        return newsCategory;
    }

    public CardView getCardView() {
        return cardView;
    }

    public ImageView getNewsImageView() {
        return newsImageView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getNewsDate() {
        return newsDate;
    }
}
