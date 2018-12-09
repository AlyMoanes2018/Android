package com.android.agzakhanty.sprints.two.adapters;

import android.media.Rating;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.agzakhanty.R;

/**
 * Created by a.moanes on 24/05/2018.
 */



public class MyViewHolder extends RecyclerView.ViewHolder{
    public TextView usernameTV;
    public TextView comment;
    public RatingBar rate;
    public MyViewHolder(View v) {
        super(v);
        usernameTV = (TextView) v.findViewById(R.id.pharmacyNameTV);
        comment = (TextView) v.findViewById(R.id.commentTV);
        rate = (RatingBar) v.findViewById(R.id.ratingBar);
    }
}
