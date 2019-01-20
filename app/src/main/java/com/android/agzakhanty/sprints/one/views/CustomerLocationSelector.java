package com.android.agzakhanty.sprints.one.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CustomerLocationSelector extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap map;
    private LatLng location = null;
    Boolean mLocationPermissionGranted = false;
    ProgressDialog dialog;
    Marker marker;
    @BindView(R.id.toolbar)
    Toolbar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_location_selector);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = DialogCreator.getInstance(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else getLocationPermission();


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15);
                map.animateCamera(cameraUpdate);
                location = place.getLatLng();
                marker.setPosition(place.getLatLng());
                marker.showInfoWindow();
            }

            @Override
            public void onError(Status status) {

            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.NextButton)
    public void onNextClicked() {
        if (marker != null) {
            Log.d("TEST_LOC2", marker.getPosition().latitude + " " + marker.getPosition().longitude);

           goToUpdateWS();
        }
    }

    @OnClick(R.id.skip)
    public void onSkipClicked(){
        Intent intent = new Intent(CustomerLocationSelector.this, FavouritePharmacy.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setPadding(0, 1000, 0, 0);
        location = new LatLng(30.044420, 31.235712);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15);
        map.animateCamera(cameraUpdate);
         marker = map.addMarker(new MarkerOptions()
                 .title(getResources().getString(R.string.mapMarkerTitle))
                .position(new LatLng(location.latitude, location.longitude))
                .draggable(true));
         marker.showInfoWindow();
        if (mLocationPermissionGranted) {
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(this);
        } else getLocationPermission();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        location = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
        marker.setPosition(location);
        marker.showInfoWindow();
        return false;
    }


    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
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
                    map.setMyLocationEnabled(true);
                    map.setOnMyLocationButtonClickListener(this);
                } else {
                    mLocationPermissionGranted = false;
                }
            }
        }
    }

    private void goToUpdateWS() {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        final Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        customer.setLatitude(marker.getPosition().latitude+"");
        customer.setLongitude(marker.getPosition().longitude+"");
        customer.setRegId(PrefManager.getInstance(CustomerLocationSelector.this).read(Constants.REGISTRATION_ID));
        Log.d("TEST_LOCATION_BEFORE", new Gson().toJson(customer));
        customer.setFileName();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.updateCustomerInfo(customer.getId(), customer);
        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        response.body().getCstmr().setProfilePhotoImgUrl(customer.getProfilePhotoImgUrl());
                        PrefManager.getInstance(CustomerLocationSelector.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(response.body().getCstmr()));
                        Log.d("TEST_LOCATION_AFTER", new Gson().toJson(response.body().getCstmr()));
                        Intent intent = new Intent(CustomerLocationSelector.this, FavouritePharmacy.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CustomerLocationSelector.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(CustomerLocationSelector.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, ProfilePhotoSetter.class);
        startActivity(i);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
    }
}
