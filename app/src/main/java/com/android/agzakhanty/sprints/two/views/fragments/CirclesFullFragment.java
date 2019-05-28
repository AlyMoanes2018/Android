package com.android.agzakhanty.sprints.two.views.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.Pharmacy;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.two.adapters.CircleFullPharmaciesAdapter;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.views.CirclesFull;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CirclesFullFragment extends Fragment {

    @BindView(R.id.pharmaciesList)
    ListView pharmaciesList;
    @BindView(R.id.noNearby)
    TextView noNearby;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    Boolean mPhoneCallPermissionGranted = false;

    public CirclesFullFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_circle_full_list, container, false);
        ButterKnife.bind(this, view);

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            mPhoneCallPermissionGranted = true;
        } else getPhoneCallPermission();

        return view;
    }

    private void getPhoneCallPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            mPhoneCallPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    0);
        }
    }



    public void loadActivePharmacies() {
        progressBar.setVisibility(View.GONE);
        String custJSON = PrefManager.getInstance(getContext()).read(Constants.CIRCLE_ALL_PHARMACIES_LIST);
        ArrayList<PharmacyDistance> copy = new Gson().fromJson(custJSON, new TypeToken<ArrayList<PharmacyDistance>>() {
        }.getType());
        ArrayList<PharmacyDistance> activePharmacies = new ArrayList<>();
        for (int i = 0; i < copy.size(); i++) {
            //Log.d("TEST_PAGE", copy.get(i).getName() + " " + copy.get(i).getActive());
            if (copy.get(i).getPharmacy().getActive() == null ||
                    copy.get(i).getPharmacy().getActive().equalsIgnoreCase("y")) {
                //Log.d("TEST_PAGE", copy.get(i).getName() + " removed");
                activePharmacies.add(copy.get(i));
            }
        }
        CircleFullPharmaciesAdapter adapter = new CircleFullPharmaciesAdapter(activePharmacies, getContext());
        Log.d("TEST_PAGE4_active", new Gson().toJson(activePharmacies));
        if (activePharmacies.size() > 0) {
            pharmaciesList.setAdapter(adapter);
            noNearby.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            pharmaciesList.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        } else {
            noNearby.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            pharmaciesList.setVisibility(View.GONE);
        }

    }

    public void loadInactivePharmacies() {
        progressBar.setVisibility(View.GONE);
        String custJSON = PrefManager.getInstance(getContext()).read(Constants.CIRCLE_ALL_PHARMACIES_LIST);
        ArrayList<PharmacyDistance> copy = new Gson().fromJson(custJSON, new TypeToken<ArrayList<PharmacyDistance>>() {
        }.getType());
        ArrayList<PharmacyDistance> inActivePharmacies = new ArrayList<>();
        for (int i = 0; i < copy.size(); i++) {
            //Log.d("TEST_PAGE2", copy.get(i).getName() + " " + copy.get(i).getActive());
            if (copy.get(i).getPharmacy().getActive().equalsIgnoreCase("n") ||
                    copy.get(i).getPharmacy().getActive().equalsIgnoreCase("p")) {
                //Log.d("TEST_PAGE2", copy.get(i).getName() + " removed");
                inActivePharmacies.add(copy.get(i));
            }
        }
        CircleFullPharmaciesAdapter adapter = new CircleFullPharmaciesAdapter(inActivePharmacies, getContext());
        Log.d("TEST_PAGE4", new Gson().toJson(inActivePharmacies));
        if (inActivePharmacies.size() > 0) {
            pharmaciesList.setAdapter(adapter);
            noNearby.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            pharmaciesList.setVisibility(View.VISIBLE);
        } else {
            noNearby.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            pharmaciesList.setVisibility(View.GONE);
        }
    }

}
