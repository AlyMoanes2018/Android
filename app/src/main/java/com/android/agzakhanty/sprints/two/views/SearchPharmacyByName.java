package com.android.agzakhanty.sprints.two.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DataValidator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.adapters.FavouritePharmaciesAdapter;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.one.views.AddPharmacy;
import com.android.agzakhanty.sprints.one.views.CustomerLocationSelector;
import com.android.agzakhanty.sprints.one.views.FavouritePharmacy;
import com.android.agzakhanty.sprints.one.views.InterestsActivity;
import com.android.agzakhanty.sprints.three.views.NewConsultation;
import com.android.agzakhanty.sprints.three.views.ReportViolation;
import com.android.agzakhanty.sprints.two.views.fragments.CirclesFullFragment;
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

public class SearchPharmacyByName extends AppCompatActivity implements LocationListener {
    @BindView(R.id.searchET)
    AppCompatEditText searchET;
    @BindView(R.id.results)
    ListView pharmaciesLV;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private LocationManager locationManager;
    Boolean mLocationPermissionGranted = false;
    Location mLastKnownLocation = new Location(NETWORK_STATS_SERVICE);
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pharmacy_by_name);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(toolbar);
        if (getCallingActivity() != null) {
            Toast.makeText(this, getResources().getString(R.string.pcyForResult), Toast.LENGTH_LONG).show();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLastKnownLocation.setLatitude(30.0340685);
        mLastKnownLocation.setLongitude(31.3461807);
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
        goToGetCirclesWS();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.searchButton)
    public void searchOnClick() {
        if (DataValidator.isStringEmpty(searchET.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.searchError), Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            goToNearbyWS();
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        //29.9796143, 31.1495216  Home
        //31.3461807, 30.0340685 Work
        Log.d("TEST_LOC", location.getLongitude() + " " + location.getLatitude());
        mLastKnownLocation = location;
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
        Call<ArrayList<PharmacyDistance>> call = apiService.getAllNearbyPharmaciesByName(mLastKnownLocation.getLatitude() + "",
                mLastKnownLocation.getLongitude() + "",
                null,
                Constants.SERVICE_SEARCH_BY_PHARMACY_NAME,
                searchET.getText().toString());
        call.enqueue(new Callback<ArrayList<PharmacyDistance>>() {
            @Override
            public void onResponse(Call<ArrayList<PharmacyDistance>> call, Response<ArrayList<PharmacyDistance>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    ArrayList<PharmacyDistance> nearby = response.body();
                    String fromEdit = getIntent().getStringExtra("fromEdit") != null ? "y" : null;
                    FavouritePharmaciesAdapter adapter = new FavouritePharmaciesAdapter(nearby, SearchPharmacyByName.this,
                            getIntent().getStringExtra("next"), fromEdit);
                    pharmaciesLV.setAdapter(adapter);
                    pharmaciesLV.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    //noNearby.setVisibility(View.GONE);

                } else {
                    pharmaciesLV.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Log.d("TEST_CERT", response.code() + "");
                    // noNearby.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PharmacyDistance>> call, Throwable t) {
                Toast.makeText(SearchPharmacyByName.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                pharmaciesLV.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                //noNearby.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("dash"))
                intent = new Intent(SearchPharmacyByName.this, Dashboard.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("fav"))
                intent = new Intent(SearchPharmacyByName.this, FavouritePharmacy.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("cf"))
                intent = new Intent(SearchPharmacyByName.this, CirclesFull.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("cons"))
                intent = new Intent(SearchPharmacyByName.this, NewConsultation.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("vio"))
                intent = new Intent(SearchPharmacyByName.this, ReportViolation.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("NO"))
                intent = new Intent(SearchPharmacyByName.this, NewOrder.class);
            else
                intent = new Intent(SearchPharmacyByName.this, Dashboard.class);
        } else intent = new Intent(SearchPharmacyByName.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }

    private void goToGetCirclesWS() {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        final Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        Log.d("TEST_UPDATE", new Gson().toJson(customer));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<PharmacyDistance>> call = apiService.getallCustomerCirclePharmacies(customer.getId(),
                customer.getLatitude() + "",
                customer.getLongitude() + "");
        call.enqueue(new Callback<ArrayList<PharmacyDistance>>() {
            @Override
            public void onResponse(Call<ArrayList<PharmacyDistance>> call, Response<ArrayList<PharmacyDistance>> response) {
                if (response.body() != null && response.isSuccessful() && response.body().size() > 0) {
                    String fromEdit = getIntent().getStringExtra("fromEdit") != null ? "y" : null;
                    FavouritePharmaciesAdapter adapter = new FavouritePharmaciesAdapter(response.body(), SearchPharmacyByName.this,
                            getIntent().getStringExtra("next"), fromEdit);
                    pharmaciesLV.setAdapter(adapter);
                    pharmaciesLV.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Log.d("TEST_Full", new Gson().toJson(customer));

                }

            }

            @Override
            public void onFailure(Call<ArrayList<PharmacyDistance>> call, Throwable t) {
                Toast.makeText(SearchPharmacyByName.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());

            }
        });

    }
}
