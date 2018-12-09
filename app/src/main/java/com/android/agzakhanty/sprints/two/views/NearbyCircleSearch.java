package com.android.agzakhanty.sprints.two.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.two.adapters.CirclePharmaciesAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NearbyCircleSearch extends AppCompatActivity implements LocationListener {

    @BindView(R.id.pharmaciesList)
    ListView pharmaciesList;
    @BindView(R.id.noNearby)
    TextView noNearby;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private LocationManager locationManager;
    Boolean mLocationPermissionGranted = false;
    Location mLastKnownLocation;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 100;

    @OnClick(R.id.doneButton)
    public void done() {
        Intent intent = new Intent(this, CirclesFull.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "nearbyCircleSearch");
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_circle_search);
        ButterKnife.bind(this);
        Log.d("TEST_LOC", "VISIBLE");
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else getLocationPermission();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocationPermissionGranted) {
            Log.d("TEST_LOC", "Granted");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        } else {
            Log.d("TEST_LOC", "Denied");
            getLocationPermission();
        }

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
                } else {
                    mLocationPermissionGranted = false;
                }
            }
        }
    }

    private void goToNearbyWS() {
        Log.d("TEST_WS", "WEB SERVICE");
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<PharmacyDistance>> call = apiService.getAllNearbyPharmacies(mLastKnownLocation.getLatitude() + "",
                mLastKnownLocation.getLongitude() + "",
                customer.getId(),
                "10");
        call.enqueue(new Callback<ArrayList<PharmacyDistance>>() {
            @Override
            public void onResponse(Call<ArrayList<PharmacyDistance>> call, Response<ArrayList<PharmacyDistance>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    ArrayList<PharmacyDistance> nearby = response.body();
                    CirclePharmaciesAdapter adapter = new CirclePharmaciesAdapter(nearby, NearbyCircleSearch.this, noNearby);
                    pharmaciesList.setAdapter(adapter);
                    pharmaciesList.setVisibility(View.VISIBLE);
                    noNearby.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                } else {
                    pharmaciesList.setVisibility(View.GONE);
                    noNearby.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PharmacyDistance>> call, Throwable t) {
                Toast.makeText(NearbyCircleSearch.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
//                Log.d("TEST_CERT", t.getMessage());
                pharmaciesList.setVisibility(View.GONE);
                noNearby.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("circles"))
                intent = new Intent(NearbyCircleSearch.this, Circles.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("circlesFull"))
                intent = new Intent(NearbyCircleSearch.this, CirclesFull.class);
            else
                intent = new Intent(NearbyCircleSearch.this, Dashboard.class);
        } else intent = new Intent(NearbyCircleSearch.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
