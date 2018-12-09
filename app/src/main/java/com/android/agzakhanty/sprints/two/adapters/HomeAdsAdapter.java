package com.android.agzakhanty.sprints.two.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.sprints.two.models.Ad;
import com.android.agzakhanty.sprints.two.models.UserRatings;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.android.agzakhanty.sprints.two.views.AdDetails;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by a.moanes on 24/05/2018.
 */

public class HomeAdsAdapter extends RecyclerView.Adapter<MyViewHolder2> {
    private ArrayList<AdResponseModel> list;
    Context context;

    public HomeAdsAdapter(ArrayList<AdResponseModel> Data, Context ctx) {
        list = Data;
        context = ctx;
    }

    @Override
    public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_ads_list_item, parent, false);
        MyViewHolder2 holder = new MyViewHolder2(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder2 holder, int position) {


        if (list.get(position).getPcyImage() != null &&
                !list.get(position).getPcyImage().equalsIgnoreCase("null") &&
                !list.get(position).getPcyImage().isEmpty()) {
            Glide
                    .with(context)
                    .load(Constants.BASE_URL + list.get(position).getPcyImage())
                    .centerCrop()
                    .into(holder.pharmacyPhoto);
        } else {
            Glide
                    .with(context)
                    .load(Constants.NO_IMG_FOUND_URL)
                    .centerCrop()
                    .into(holder.pharmacyPhoto);
        }

        if (list.get(position).getAdvImage() != null && !list.get(position).getAdvImage().equalsIgnoreCase("null") &&
                !list.get(position).getAdvImage().isEmpty()) {
            Glide
                    .with(context)
                    .load(Constants.BASE_URL + list.get(position).getAdvImage())
                    .centerCrop()
                    .into(holder.adPhoto);
        } else {
            Glide
                    .with(context)
                    .load(Constants.NO_IMG_FOUND_URL)
                    .centerCrop()
                    .into(holder.adPhoto);
        }

        holder.adDescription.setText(list.get(position).getAdvSubject());
        holder.adPCY.setText(list.get(position).getPcyName());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}

class MyViewHolder2 extends RecyclerView.ViewHolder {
    public ImageView pharmacyPhoto;
    public ImageView adPhoto;
    public TextView adDescription;
    public TextView adPCY;

    public MyViewHolder2(View v) {
        super(v);
        pharmacyPhoto = (ImageView) v.findViewById(R.id.pharmacyLogoIV);
        adPhoto = (ImageView) v.findViewById(R.id.adImg);
        adDescription = (TextView) v.findViewById(R.id.adDescription);
        adPCY = (TextView) v.findViewById(R.id.adPCY);
    }
}