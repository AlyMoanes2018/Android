package com.android.agzakhanty.sprints.one.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.android.agzakhanty.sprints.one.models.Pharmacy;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.one.views.CustomerLocationSelector;
import com.android.agzakhanty.sprints.one.views.FavouritePharmacy;
import com.android.agzakhanty.sprints.one.views.InterestsActivity;
import com.android.agzakhanty.sprints.two.views.Circles;
import com.android.agzakhanty.sprints.two.views.CirclesFull;
import com.android.agzakhanty.sprints.two.views.NewOrder;
import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.android.agzakhanty.R.id.parent;

/**
 * Created by Aly on 27/04/2018.
 */

public class FavouritePharmaciesAdapter extends ArrayAdapter<PharmacyDistance> {

    ArrayList<PharmacyDistance> pharmacies;
    Context context;
    String next, fromEdit;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView address;
        TextView distance;
        ImageView logo;
        Button favorite;
        RelativeLayout mainLayout;
    }

    public FavouritePharmaciesAdapter(ArrayList<PharmacyDistance> pharmacies, Context context, String next, String fromEdit) {
        super(context, R.layout.favourite_paharmacy_list_item, pharmacies);
        this.context = context;
        this.pharmacies = pharmacies;
        this.next = next;
        this.fromEdit = fromEdit;
    }

    @Override
    public int getCount() {
        return pharmacies.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final PharmacyDistance pharmacy = pharmacies.get(i);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.favourite_paharmacy_list_item, viewGroup, false);
            viewHolder.name = (TextView) view.findViewById(R.id.nameTV);
            viewHolder.address = (TextView) view.findViewById(R.id.addressTV);
            viewHolder.distance = (TextView) view.findViewById(R.id.distanceTV);
            viewHolder.logo = (ImageView) view.findViewById(R.id.logo);
            viewHolder.favorite = (Button) view.findViewById(R.id.favourite);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
            if (((Activity) context).getCallingActivity() != null) {
                viewHolder.favorite.setVisibility(View.GONE);
                viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // select a pcy
                        Intent intent = new Intent();
                        intent.putExtra("pcyName", pharmacy.getPharmacy().getName());
                        intent.putExtra("pcyID", pharmacy.getPharmacy().getId());
                        ((Activity) context).setResult(RESULT_OK, intent);
                        //close this Activity...
                        ((Activity) context).finish();
                    }
                });
            }
            viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToUpdateWS(viewHolder, pharmacy.getPharmacy().getId());
                }
            });

            result = view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }

        viewHolder.name.setText(pharmacy.getPharmacy().getName());
        viewHolder.address.setText(pharmacy.getPharmacy().getAddress());
        viewHolder.distance.setText(pharmacy.getDistanceResult());
        if (pharmacy.getPharmacy().getLogoURL() != null && !pharmacy.getPharmacy().getLogoURL().isEmpty()) {
            Glide
                    .with(context)
                    .load(Constants.BASE_URL + pharmacy.getPharmacy().getLogoURL())
                    .centerCrop()
                    .into(viewHolder.logo);
        } else {
            Glide
                    .with(context)
                    .load(Constants.NO_IMG_FOUND_URL)
                    .centerCrop()
                    .into(viewHolder.logo);
        }

        return view;
    }

    private void goToUpdateWS(final ViewHolder viewHolder, String pharmacyID) {
        final ProgressDialog dialog = DialogCreator.getInstance(context);
        dialog.setMessage(context.getResources().getString(R.string.updating));
        dialog.show();
        String custJSON = PrefManager.getInstance(context).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        customer.setFavPcy(pharmacyID);
        customer.setRegId(PrefManager.getInstance(context).read(Constants.REGISTRATION_ID));
        customer.setFileName();
        Log.d("TEST_UPDATE", new Gson().toJson(customer));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.updateCustomerInfo(customer.getId(), customer);
        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        viewHolder.favorite.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_selected));
                        PrefManager.getInstance(context).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(response.body().getCstmr()));
                        Log.d("TEST_REG", response.body().getCstmr().getRegId() + " E");
                        decideNextView();
                    } else {
                        decideNextView();
                        Toast.makeText(context, context.getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");

            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
                decideNextView();
            }
        });


    }

    public void decideNextView() {
        if (next.equalsIgnoreCase(Constants.FAVOURITE_PHARMACY_NEXT_REGISTER)) {
            Intent intent = new Intent(context, InterestsActivity.class);
            if (fromEdit != null)
                intent.putExtra("fromEdit", "y");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            ((Activity) context).finish();
        } else if (next.equalsIgnoreCase(Constants.FAVOURITE_PHARMACY_NEXT_NEW_ORDER)) {
            Intent intent = new Intent(context, NewOrder.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
            ((Activity) context).finish();
        } else if (next.equalsIgnoreCase(Constants.FAVOURITE_PHARMACY_NEXT_CIRCLES)) {
            Intent intent = new Intent(context, Circles.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            ((Activity) context).finish();
        } else if (next.equalsIgnoreCase(Constants.FAVOURITE_PHARMACY_NEXT_CIRCLES_FULL)) {
            Intent intent = new Intent(context, CirclesFull.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }

}
