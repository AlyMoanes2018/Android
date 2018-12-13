package com.android.agzakhanty.sprints.two.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.one.views.InterestsActivity;
import com.android.agzakhanty.sprints.two.models.api_responses.CircleResponseModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Aly on 27/04/2018.
 */

public class CirclePharmaciesAdapter extends ArrayAdapter<PharmacyDistance> {

    ArrayList<PharmacyDistance> pharmacies;
    Context context;
    TextView noNearby;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView address;
        TextView distance;
        TextView status;
        TextView addToCircle;

    }

    public CirclePharmaciesAdapter(ArrayList<PharmacyDistance> pharmacies, Context context, TextView no) {
        super(context, R.layout.circle_paharmacy_list_item, pharmacies);
        this.context = context;
        this.pharmacies = pharmacies;
        noNearby = no;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final PharmacyDistance pharmacy = pharmacies.get(i);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.circle_paharmacy_list_item, viewGroup, false);
        if (pharmacy.getIsCircle().equalsIgnoreCase("false")) {
            viewHolder = new ViewHolder();

            viewHolder.name = (TextView) view.findViewById(R.id.nameTV);
            viewHolder.address = (TextView) view.findViewById(R.id.addressTV);
            viewHolder.distance = (TextView) view.findViewById(R.id.distanceTV);
            viewHolder.addToCircle = (TextView) view.findViewById(R.id.add);
            viewHolder.status = (TextView) view.findViewById(R.id.statusTV);
            Log.d("TEST_NULL3", pharmacy.getPharmacy().getId());
            viewHolder.addToCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    goToAddWS(viewHolder, pharmacy.getPharmacy().getId(), i);
                }
            });

            result = view;

            view.setTag(viewHolder);


            viewHolder.name.setText(pharmacy.getPharmacy().getName());
            viewHolder.address.setText(pharmacy.getPharmacy().getAddress());
            viewHolder.distance.setText(pharmacy.getDistanceResult());
            if (pharmacy.getPharmacy().getActive().equalsIgnoreCase("P") || pharmacy.getPharmacy().getActive().equalsIgnoreCase("N")) {
                viewHolder.status.setText(context.getResources().getString(R.string.activeCircle));
                viewHolder.status.setTextColor(Color.parseColor("#CB2E25"));
            } else {
                viewHolder.status.setText(context.getResources().getString(R.string.activeCircle));
                viewHolder.status.setTextColor(Color.parseColor("#449D44"));
            }
        }else {
            pharmacies.remove(pharmacy);
            Log.d("TEST_SIZE", pharmacies.size()+"");
            notifyDataSetChanged();
            if (pharmacies.size() > 0)
                noNearby.setVisibility(View.GONE);
            else noNearby.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void goToAddWS(final ViewHolder viewHolder, String pharmacyID, final int index) {
        final ProgressDialog dialog = DialogCreator.getInstance(context);
        dialog.setMessage(context.getResources().getString(R.string.addPcyCircle));
        dialog.show();
        String custJSON = PrefManager.getInstance(context).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        Log.d("TEST_UPDATE", new Gson().toJson(customer));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CircleResponseModel> call = apiService.addPharmacyToCircle(customer.getId(), pharmacyID);
        call.enqueue(new Callback<CircleResponseModel>() {
            @Override
            public void onResponse(Call<CircleResponseModel> call, Response<CircleResponseModel> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        Toast.makeText(context, context.getResources().getString(R.string.addToCirclesDone), Toast.LENGTH_LONG).show();
                        pharmacies.remove(index);
                        notifyDataSetChanged();

                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.wrongCode), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");

            }

            @Override
            public void onFailure(Call<CircleResponseModel> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }
}
