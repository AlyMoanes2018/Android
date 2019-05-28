package com.android.agzakhanty.sprints.one.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.TagsAndStatus;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Aly on 27/04/2018.
 */

public class InterestsAdapter extends ArrayAdapter<TagsAndStatus> {

    private ArrayList<TagsAndStatus> allTags;
    private Context context;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        CheckBox check;
        ImageView logo;
        RelativeLayout mainLayout;
    }

    public InterestsAdapter(ArrayList<TagsAndStatus> interests, Context context) {
        super(context, R.layout.interests_list_item, interests);
        this.context = context;
        this.allTags = interests;
    }

    @Override
    public int getCount() {
        return allTags.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final TagsAndStatus interest = allTags.get(i);
        Log.d("TEST_INTEREST", new Gson().toJson(interest));
        interest.getIfCheckedObject().setTagId(interest.getTag().getId());
        String custJSON = PrefManager.getInstance(context).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        interest.getIfCheckedObject().setCstId(customer.getId());
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.interests_list_item, viewGroup, false);
        viewHolder.name = (TextView) view.findViewById(R.id.nameTV);
        viewHolder.check = (CheckBox) view.findViewById(R.id.check);
        viewHolder.logo = (ImageView) view.findViewById(R.id.logo);
        viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
        result = view;

        view.setTag(viewHolder);


        // load language from sp
        PrefManager prefManager = PrefManager.getInstance(context);
        Log.d("TEST_INTER", PrefManager.getInstance(context).readInt(Constants.SP_LANGUAGE_KEY) + "  E");
        if (PrefManager.getInstance(context).readInt(Constants.SP_LANGUAGE_KEY) == 0)
            viewHolder.name.setText(interest.getTag().getTagAr());
        else if (PrefManager.getInstance(context).readInt(Constants.SP_LANGUAGE_KEY) == 1)
            viewHolder.name.setText(interest.getTag().getTagEn());
        if (interest.getStatus() != null && interest.getStatus().equalsIgnoreCase("true")) {
            viewHolder.check.setChecked(true);
        } else viewHolder.check.setChecked(false);
        viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    interest.setStatus("true");
                else interest.setStatus("false");
            }
        });
        if (interest.getTag().getTagUrl() != null && !interest.getTag().getTagUrl().isEmpty()) {
            Log.d("TEST_INTERESTS", Constants.BASE_URL + interest.getTag().getTagUrl());
            Glide
                    .with(context)
                    .load(Constants.BASE_URL + interest.getTag().getTagUrl())
                    .centerCrop()
                    .into(viewHolder.logo);
        } else {
            Glide
                    .with(context)
                    .load(Constants.NO_IMG_FOUND_URL)
                    .centerCrop()
                    .into(viewHolder.logo);
        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.check.isChecked())
                    viewHolder.check.setChecked(false);
                else viewHolder.check.setChecked(true);
            }
        });

        return view;
    }


}
