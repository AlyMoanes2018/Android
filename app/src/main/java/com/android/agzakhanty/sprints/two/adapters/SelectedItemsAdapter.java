package com.android.agzakhanty.sprints.two.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.android.agzakhanty.sprints.two.views.AddOrderByItemsSelection;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aly on 27/04/2018.
 */

public class SelectedItemsAdapter extends ArrayAdapter<ItemsResponseModel> {

    ArrayList<ItemsResponseModel> models;
    Context context;
    PrefManager prefManager;
    boolean isView;
    String orderStatusID;
    boolean isAd;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        EditText price;
        EditText quantity;
        TextView availability;
        ImageView deleteItem;
    }

    public SelectedItemsAdapter(ArrayList<ItemsResponseModel> models, Context context, boolean isView, String sttsID,
                                boolean isAd) {
        super(context, R.layout.selected_items_list_item, models);
        this.context = context;
        this.models = models;
        this.isView = isView;
        this.isAd = isAd;
        this.orderStatusID = sttsID;
        prefManager = PrefManager.getInstance(context);
        //prefManager.write(new Gson().toJson(models), Constants.ORDER_SELECTED_ITMES);
    }

    @Override
    public int getCount() {
        return models.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final ItemsResponseModel item = models.get(i);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder = new ViewHolder(); // view lookup cache stored in tag
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.selected_items_list_item, viewGroup, false);


        viewHolder.name = (TextView) view.findViewById(R.id.itemNameTV);
        viewHolder.price = (EditText) view.findViewById(R.id.priceET);
        viewHolder.quantity = (EditText) view.findViewById(R.id.quantityET);
        viewHolder.availability = (TextView) view.findViewById(R.id.availableOrNot);
        viewHolder.deleteItem = (ImageView) view.findViewById(R.id.deleteItem);


        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                models.remove(i);
                prefManager.write(new Gson().toJson(models), Constants.ORDER_SELECTED_ITMES);
                notifyDataSetChanged();
                ((AddOrderByItemsSelection) context).onDialogDoneButtonClicked(models);
            }
        });

        viewHolder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newPrice = "";
                if (!isView && !isAd) {
                    if (!viewHolder.quantity.getText().toString().isEmpty()) {
                        if (Float.parseFloat(viewHolder.quantity.getText().toString()) != 0) {
                            models.get(i).setQty(viewHolder.quantity.getText().toString());
                            newPrice = (Float.parseFloat(models.get(i).getQty()))
                                    * (Float.parseFloat(models.get(i).getPrice())) + "";
                            Log.d("TEST_FLOAT", Float.parseFloat(models.get(i).getQty()) +
                                    " *" + Float.parseFloat(models.get(i).getPrice()) + " = " + newPrice);
                            viewHolder.price.setText(newPrice);
                            ((AddOrderByItemsSelection) context).updateTotalPrice();
                        } else {
                            models.get(i).setQty("1");
                            newPrice = models.get(i).getPrice();
                            viewHolder.price.setText(newPrice);
                            viewHolder.quantity.setText("1");
                            ((AddOrderByItemsSelection) context).updateTotalPrice();
                        }
                    }
                }
            }
        });

        if (prefManager.readInt(Constants.SP_LANGUAGE_KEY) == 0)
            viewHolder.name.setText(item.getNameAr());
        else if (prefManager.readInt(Constants.SP_LANGUAGE_KEY) == 1)
            viewHolder.name.setText(item.getNameEn());
        if (!isView && !isAd)
            item.setQty("1");
        //Log.d("TEST_ITEMS_DIALOG", new Gson().toJson(models) + "    EE");
        viewHolder.quantity.setText(item.getQty());
        viewHolder.price.setText(item.getPrice());
        if (isView) {
            viewHolder.deleteItem.setVisibility(View.GONE);
            viewHolder.price.setEnabled(false);
            viewHolder.quantity.setEnabled(false);
            if (orderStatusID.equals("2") || orderStatusID.equals("3")) {
                //set availability text here
                if (item.getStatus() != null && !item.getStatus().isEmpty()) {
                    boolean x = item.getStatus().equalsIgnoreCase("n");
                    Log.d("TEST_ALT", item.getStatus() + "  " + x);
                    if (item.getStatus().equalsIgnoreCase("N"))
                        viewHolder.availability.setText(context.getResources().getString(R.string.notAvailable));
                    else if (item.getStatus().equalsIgnoreCase("y")) {
                        if (item.getRefId() != null &&
                                (item.getBasicItemHint() != null ||
                                        !item.getBasicItemHint().isEmpty() ||
                                        !item.getBasicItemHint().equalsIgnoreCase("null")))
                            viewHolder.availability.setText(context.getResources().getString(R.string.notAvailable2));
                        else
                            viewHolder.availability.setText(context.getResources().getString(R.string.available));
                    }
                }


                viewHolder.availability.setVisibility(View.VISIBLE);
            } else viewHolder.availability.setVisibility(View.GONE);
        } else {
            if (isAd) {
                viewHolder.deleteItem.setVisibility(View.GONE);
                viewHolder.price.setEnabled(false);

                viewHolder.quantity.setEnabled(false);
                viewHolder.quantity.setText(item.getQty());
                viewHolder.availability.setVisibility(View.GONE);
            } else {
                viewHolder.availability.setVisibility(View.GONE);
                viewHolder.deleteItem.setVisibility(View.VISIBLE);
                viewHolder.price.setEnabled(false);
                viewHolder.quantity.setEnabled(true);
            }
        }


        return view;
    }

}
