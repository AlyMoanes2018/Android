package com.android.agzakhanty.sprints.two.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.agzakhanty.R;
import com.android.agzakhanty.sprints.two.models.UserRatings;

import java.util.ArrayList;

/**
 * Created by a.moanes on 24/05/2018.
 */

public class UserRatingsAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private ArrayList<UserRatings> list;
    Context context;
    public UserRatingsAdapter(ArrayList<UserRatings> Data, Context ctx) {
        list = Data;
        context = ctx;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_rating_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.usernameTV.setText(list.get(position).getUserName());
        holder.rate.setRating(list.get(position).getRating());
        holder.rate.setEnabled(false);
        holder.comment.setText(list.get(position).getComment());

        

    }
    @Override
    public int getItemCount() {
        return list.size();
    }
        }