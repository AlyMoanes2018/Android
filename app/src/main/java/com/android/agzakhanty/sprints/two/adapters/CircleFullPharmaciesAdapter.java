package com.android.agzakhanty.sprints.two.adapters;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.android.agzakhanty.sprints.two.interfaces.UiUpdaterCallback;
import com.android.agzakhanty.sprints.two.models.api_responses.UpdatePcyStatusResponseModel;
import com.android.agzakhanty.sprints.two.views.CirclesFull;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Aly on 27/04/2018.
 */

public class CircleFullPharmaciesAdapter extends ArrayAdapter<PharmacyDistance> {

    ArrayList<PharmacyDistance> pharmacies;
    Context context;
    ProgressDialog dialog;
    UiUpdaterCallback callback;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView address;
        TextView distance;
        TextView call;
        TextView toggle;
    }

    public CircleFullPharmaciesAdapter(ArrayList<PharmacyDistance> pharmacies, Context context, UiUpdaterCallback callback) {
        super(context, R.layout.circle_full_paharmacy_list_item, pharmacies);
        this.context = context;
        this.pharmacies = pharmacies;
        this.callback = callback;
        dialog = DialogCreator.getInstance(context);
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
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.circle_full_paharmacy_list_item, viewGroup, false);
        Log.d("TEST_CIRCLE", pharmacy.getIsCircle());
        //if (pharmacy.getIsCircle().equalsIgnoreCase("false")) {
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        viewHolder = new ViewHolder();
        viewHolder.name = (TextView) view.findViewById(R.id.nameTV);
        viewHolder.address = (TextView) view.findViewById(R.id.addressTV);
        viewHolder.distance = (TextView) view.findViewById(R.id.distanceTV);
        viewHolder.call = (TextView) view.findViewById(R.id.call);
        viewHolder.toggle = (TextView) view.findViewById(R.id.togglePcyActivityButton);
        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isPermissionGranted = PrefManager.getInstance(context).read(Constants.SP_CALL_PERMISSION_GRANTED).equals("true");
                Log.d("TEST_CALL", isPermissionGranted + "");
                if (pharmacy.getPharmacy().getMobile() != null && !pharmacy.getPharmacy().getMobile().isEmpty() && isPermissionGranted) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + pharmacy.getPharmacy().getMobile()));
                    context.startActivity(intent);
                }
            }
        });
        viewHolder.toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage("جاري تحديث بيانات الصيدلية..");
                dialog.show();
                goToUpdateActiveWS(i);
            }
        });

        viewHolder.name.setText(pharmacy.getPharmacy().getName());
        viewHolder.address.setText(pharmacy.getPharmacy().getAddress());
        viewHolder.distance.setText(pharmacy.getDistanceResult());
        if (pharmacy.getIsCircle().equalsIgnoreCase("true"))
            viewHolder.toggle.setText("غير نشطة");
        else
            viewHolder.toggle.setText("تنشيط");
        //}
        /*else {
            pharmacies.remove(pharmacy);
            notifyDataSetChanged();
        }*/
        return view;
    }

    private void goToUpdateActiveWS(final int index) {
        String custJSON = PrefManager.getInstance(context).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        final Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        Log.d("TEST_UPDATE", new Gson().toJson(customer));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UpdatePcyStatusResponseModel> call = apiService.updateCirclePcyStatus(pharmacies.get(index).getPharmacy().getId(), customer.getId());
        call.enqueue(new Callback<UpdatePcyStatusResponseModel>() {
            @Override
            public void onResponse(Call<UpdatePcyStatusResponseModel> call, Response<UpdatePcyStatusResponseModel> response) {
                if (response.body() != null && response.isSuccessful() && response.body().isUpdated().equalsIgnoreCase("true")) {
                    String activeResult = response.body().isActive();
                    String allCirclePcysJSON = PrefManager.getInstance(context).read(Constants.CIRCLE_ALL_PHARMACIES_LIST);
                    ArrayList<PharmacyDistance> allCirclePcys = new Gson().fromJson(allCirclePcysJSON, new TypeToken<ArrayList<PharmacyDistance>>() {
                    }.getType());
                    for (int i = 0; i < allCirclePcys.size(); i++) {
                        if (allCirclePcys.get(i).getPharmacy().getId().equalsIgnoreCase(pharmacies.get(index).getPharmacy().getId())) {
                            allCirclePcys.get(i).setIsCircle(activeResult);
                            break;
                        }
                    }
                    PrefManager.getInstance(context).write(Constants.CIRCLE_ALL_PHARMACIES_LIST, new Gson().toJson(allCirclePcys));
                    notifyDataSetChanged();
                    callback.onUiUpdateNeeded(activeResult);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<UpdatePcyStatusResponseModel> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }


}
