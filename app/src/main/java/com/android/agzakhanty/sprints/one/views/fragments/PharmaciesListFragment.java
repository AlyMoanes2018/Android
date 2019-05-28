package com.android.agzakhanty.sprints.one.views.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
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
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.adapters.FavouritePharmaciesAdapter;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PharmaciesListFragment extends Fragment implements LocationListener {

    @BindView(R.id.pharmaciesList)
    ListView pharmaciesList;
    @BindView(R.id.noNearby)
    TextView noNearby;
    private LocationManager locationManager;
    Boolean mLocationPermissionGranted = false;
    Location mLastKnownLocation;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 100;

    public PharmaciesListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pharmacies_list, container, false);
        ButterKnife.bind(this, view);
        Log.d("TEST_LOC", "VISIBLE");
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else getLocationPermission();
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (mLocationPermissionGranted) {
            Log.d("TEST_LOC", "Granted");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        } else {
            Log.d("TEST_LOC", "Denied");
            getLocationPermission();
        }


        return view;
    }


    @Override
    public void onLocationChanged(Location location) {
//29.9796143, 31.1495216  Home
        //31.3461807, 30.0340685 Work
        Log.d("TEST_LOC", location.getLongitude() + " " + location.getLatitude());
        mLastKnownLocation = location;
        goToNearbyWS();
        if (mLocationPermissionGranted)
            locationManager.removeUpdates(this);
        else
            getLocationPermission();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {
                    mLocationPermissionGranted = false;
                }
            }
        }
    }

    private void goToNearbyWS() {
        Log.d("TEST_WS", "WEB SERVICE");
        String custJSON = PrefManager.getInstance(getContext()).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<PharmacyDistance>> call = apiService.getAllNearbyPharmacies(mLastKnownLocation.getLatitude() + "",
                mLastKnownLocation.getLongitude() + "",
                null,
                "10");
        call.enqueue(new Callback<ArrayList<PharmacyDistance>>() {
            @Override
            public void onResponse(Call<ArrayList<PharmacyDistance>> call, Response<ArrayList<PharmacyDistance>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    ArrayList<PharmacyDistance> nearby = response.body();
                    FavouritePharmaciesAdapter adapter = new FavouritePharmaciesAdapter(nearby, getContext(),
                            getActivity().getIntent().getStringExtra("next"), null);
                    pharmaciesList.setAdapter(adapter);
                    pharmaciesList.setVisibility(View.VISIBLE);
                    noNearby.setVisibility(View.GONE);

                } else {
                    pharmaciesList.setVisibility(View.GONE);
                    noNearby.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PharmacyDistance>> call, Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                pharmaciesList.setVisibility(View.GONE);
                noNearby.setVisibility(View.VISIBLE);
            }
        });
    }
}
