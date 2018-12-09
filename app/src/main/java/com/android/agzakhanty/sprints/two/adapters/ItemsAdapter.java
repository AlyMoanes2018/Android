package com.android.agzakhanty.sprints.two.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Base64;
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
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by a.moanes on 05/07/2018.
 */

public class ItemsAdapter extends ArrayAdapter<ItemsResponseModel> {


    // View lookup cache
    private static class ViewHolder {
        TextView name;
        CheckBox ifInListCheck;
        ImageView reminderItemSelectedIV;
        RelativeLayout mainLayout;
    }

    private ArrayList<ItemsResponseModel> items;
    private ArrayList<ItemsResponseModel> selectedItems;
    private Context context;
    private String searchType;
    private PrefManager prefManager;
    private Boolean isFromNewOrder;
    private int selectedItemIndex;

    public ItemsAdapter(ArrayList<ItemsResponseModel> models, ArrayList<ItemsResponseModel> selectedItems, Context ctx, String searchType, Boolean isOrder) {
        super(ctx, R.layout.items_list_item, models);
        this.context = ctx;
        this.items = models;
        this.searchType = searchType;
        this.selectedItems = selectedItems;
        prefManager = PrefManager.getInstance(context);
        isFromNewOrder = isOrder;
        selectedItemIndex = -1;
    }


    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final ItemsResponseModel item = items.get(i);
        // Check if an existing view is being reused, otherwise inflate the view
        final ItemsAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        viewHolder = new ItemsAdapter.ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.items_list_item, viewGroup, false);
        viewHolder.name = (TextView) view.findViewById(R.id.nameTV);
        viewHolder.ifInListCheck = (CheckBox) view.findViewById(R.id.ifInListCheck);
        viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
        viewHolder.reminderItemSelectedIV = (ImageView) view.findViewById(R.id.reminderItemSelectedIV);

        if (isFromNewOrder) {
            for (int j = 0; j < selectedItems.size(); j++) {
                Log.d("TEST_ADAPTER", selectedItems.get(j).getId() + " " + item.getId());
                Log.d("TEST_ADAPTER", selectedItems.get(j).getId().equalsIgnoreCase(item.getId()) + "");
                if (selectedItems.get(j).getId().equalsIgnoreCase(item.getId())) {
                    viewHolder.ifInListCheck.setChecked(true);
                    break;
                } else
                    viewHolder.ifInListCheck.setChecked(false);
            }
            viewHolder.ifInListCheck.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ifInListCheck.setVisibility(View.GONE);
            if (selectedItemIndex == i)
                viewHolder.reminderItemSelectedIV.setVisibility(View.VISIBLE);
            else viewHolder.reminderItemSelectedIV.setVisibility(View.GONE);

        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFromNewOrder) {
                    selectedItemIndex = i;
                    selectedItems.clear();
                    selectedItems.add(item);
                    viewHolder.reminderItemSelectedIV.setVisibility(View.VISIBLE);
                    prefManager.write(Constants.ORDER_SELECTED_ITMES, new Gson().toJson(selectedItems));
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.ifInListCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Log.d("TEST_ITEMS_LISTENER", "IN TRUE");
                    selectedItems.add(item);
                } else {
                    Log.d("TEST_ITEMS_LISTENER", "IN FALSE");
                    for (int j = 0; j < selectedItems.size(); j++) {
                        if (selectedItems.get(j).getId().equalsIgnoreCase(item.getId())) {
                            selectedItems.remove(j);
                        }
                    }
                }
                prefManager.write(Constants.ORDER_SELECTED_ITMES, new Gson().toJson(selectedItems));
            }
        });

        view.setTag(viewHolder);


        if (searchType.equalsIgnoreCase("ar"))
            viewHolder.name.setText(item.getNameAr());
        else if (searchType.equalsIgnoreCase("en"))
            viewHolder.name.setText(item.getNameEn());

        return view;
    }

}
