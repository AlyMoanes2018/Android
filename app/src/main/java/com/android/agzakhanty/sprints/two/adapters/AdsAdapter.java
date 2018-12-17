package com.android.agzakhanty.sprints.two.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.two.models.Ad;
import com.android.agzakhanty.sprints.two.models.AdDetails;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Aly on 27/04/2018.
 */

public class AdsAdapter extends ArrayAdapter<AdResponseModel> {

    ArrayList<AdResponseModel> models;
    Context context;
    PrefManager prefManager;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView item;
        TextView expiryDate;
        TextView ignore;
        TextView startDateHeader;
        CircleImageView pharmacyLogo;
        RelativeLayout mainLayout;
    }

    public AdsAdapter(ArrayList<AdResponseModel> models, Context context) {
        super(context, R.layout.ads_list_item, models);
        this.context = context;
        this.models = models;
        prefManager = PrefManager.getInstance(context);
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
        final AdResponseModel ad = models.get(i);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.ads_list_item, viewGroup, false);
            viewHolder.name = (TextView) view.findViewById(R.id.nameTV);
            viewHolder.item = (TextView) view.findViewById(R.id.itemTV);
            viewHolder.expiryDate = (TextView) view.findViewById(R.id.expiryDateTV);
            viewHolder.startDateHeader = (TextView) view.findViewById(R.id.dateHeaderTV);
            viewHolder.ignore = (TextView) view.findViewById(R.id.ignore);
            viewHolder.pharmacyLogo = (CircleImageView) view.findViewById(R.id.logo);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
            viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TEST_AD", "IN");
                    Intent intent = new Intent(getContext(), com.android.agzakhanty.sprints.two.views.AdDetails.class);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "ads");
                    PrefManager.getInstance(context).write(Constants.AD_DETAILS_JSON_OBJECT, new Gson().toJson(models.get(i)));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                }
            });
            viewHolder.ignore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    models.remove(i);
                    notifyDataSetChanged();
                }
            });

            result = view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }

        viewHolder.name.setText(models.get(i).getPcyName());
        viewHolder.item.setText(models.get(i).getAdvSubject());
        viewHolder.expiryDate.setText(context.getResources().getString(R.string.adExpiry) +
                " " + models.get(i).getAdvExpireDate().split("T")[0]);
        viewHolder.startDateHeader.setText(models.get(i).getDay());
        if (ad.getPcyImage() != null && !ad.getPcyImage().isEmpty()) {
            Glide
                    .with(context)
                    .load(Constants.BASE_URL + ad.getPcyImage())
                    .centerCrop()
                    .into(viewHolder.pharmacyLogo);
        } else {
            Glide
                    .with(context)
                    .load(Constants.NO_IMG_FOUND_URL)
                    .centerCrop()
                    .into(viewHolder.pharmacyLogo);
        }
        Log.d("TEST_DATE2", models.get(i).getDay());
        if (i > 0) {
            String previousAdDate = models.get(i - 1).getAdvStartDate().split("T")[0];
            if (models.get(i).getAdvStartDate().split("T")[0].equalsIgnoreCase(previousAdDate))
                viewHolder.startDateHeader.setVisibility(View.GONE);
            else
                viewHolder.startDateHeader.setVisibility(View.VISIBLE);
        }

        return view;
    }

}
