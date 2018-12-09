package com.android.agzakhanty.sprints.three.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.three.models.Measurement;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by a.moanes on 24/05/2018.
 */

public class NewMeasurementRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder2> {
    private ArrayList<Measurement> list;
    Context context;

    public NewMeasurementRecyclerAdapter(ArrayList<Measurement> Data, Context ctx) {
        list = Data;
        context = ctx;
    }

    @Override
    public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_measurement_recycler_list_item, parent, false);
        MyViewHolder2 holder = new MyViewHolder2(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder2 holder, int position) {

        if (PrefManager.getInstance(context).readInt(Constants.SP_LANGUAGE_KEY) == 0)
            holder.name.setText(list.get(position).getNameAr());
        else if (PrefManager.getInstance(context).readInt(Constants.SP_LANGUAGE_KEY) == 1)
            holder.name.setText(list.get(position).getNameEn());

        Random r = new Random();
        Drawable d;
        int rand = r.nextInt((4 - 1) + 1) + 1;
        if (rand == 1)
            d = ContextCompat.getDrawable(context, R.drawable.ic_add_shopping_cart);
        else if (rand == 2)
            d = ContextCompat.getDrawable(context, R.drawable.ic_cam);
        else if (rand == 3)
            d = ContextCompat.getDrawable(context, R.drawable.ic_weight);

        else d = ContextCompat.getDrawable(context, R.drawable.ic_repeat);

        holder.icon.setBackground(d);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class MyViewHolder2 extends RecyclerView.ViewHolder {
    public ImageView icon;
    public TextView name;

    public MyViewHolder2(View v) {
        super(v);
        icon = (ImageView) v.findViewById(R.id.icon);
        name = (TextView) v.findViewById(R.id.name);
    }
}

