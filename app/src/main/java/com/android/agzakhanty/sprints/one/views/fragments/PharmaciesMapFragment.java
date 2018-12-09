package com.android.agzakhanty.sprints.one.views.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.agzakhanty.sprints.one.views.CustomerLocationSelector;
import com.android.agzakhanty.sprints.one.views.FavouritePharmacy;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PharmaciesMapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap map;
    private LocationManager locationManager;
    Boolean mLocationPermissionGranted = false;
    Location mLastKnownLocation;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 100;
    ProgressDialog dialog;
    private Marker marker;

    public PharmaciesMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pharmacies_map, container, false);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        dialog = DialogCreator.getInstance(getContext());
        dialog.setMessage(getContext().getString(R.string.detecting));
        dialog.show();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // put markers here
    }

    @Override
    public void onLocationChanged(Location location) {
        //29.9796143, 31.1495216  Home
        //31.3461807, 30.0340685 Work
        Log.d("TEST_LOC", location.getLongitude() + " " + location.getLatitude());
        mLastKnownLocation = location;
        LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        map.animateCamera(cameraUpdate);
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
        //dialog.show();
        dialog.setMessage(getResources().getString(R.string.nearby));
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
                    //prepare markers
                    for (int i = 0; i < nearby.size(); i++) {
                        Log.d("TEST_NEARBY", "INSIDE LOOP");
                        nearby.get(i).getPharmacy().setDistance(nearby.get(i).getDistanceResult());
                        LatLng latLng = new LatLng(Double.parseDouble(nearby.get(i).getPharmacy().getLatitude()),
                                Double.parseDouble(nearby.get(i).getPharmacy().getLongitude()));
                        marker = map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(nearby.get(i).getPharmacy().getName()));
                        marker.setTag(i);
                    }

                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.noNearby), Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ArrayList<PharmacyDistance>> call, Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }

}
