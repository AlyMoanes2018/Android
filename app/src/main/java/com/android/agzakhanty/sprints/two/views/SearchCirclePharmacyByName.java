package com.android.agzakhanty.sprints.two.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
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

public class SearchCirclePharmacyByName extends AppCompatActivity implements LocationListener {
    @BindView(R.id.searchET)
    AppCompatEditText searchET;
    @BindView(R.id.results)
    ListView pharmaciesLV;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.name)
    RadioButton name;
    @BindView(R.id.code)
    RadioButton code;
    @BindView(R.id.phone)
    RadioButton phone;
    @BindView(R.id.noNearby)
    TextView noNearby;

    String searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_NAME;
    private LocationManager locationManager;
    Boolean mLocationPermissionGranted = false;
    Location mLastKnownLocation = new Location(NETWORK_STATS_SERVICE);
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 100;

    @OnClick(R.id.doneButton)
    public void done() {
        Intent intent = new Intent(this, CirclesFull.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "searchPcyByName");
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_circle_pharmacy_by_name);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                searchET.setText("");
                noNearby.setVisibility(View.GONE);
                if (name.isChecked()) {
                    searchET.setHint(getResources().getString(R.string.nameHint));
                    searchET.setInputType(InputType.TYPE_CLASS_TEXT);
                    searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_NAME;
                } else if (code.isChecked()) {
                    searchET.setHint(getResources().getString(R.string.codeHint));
                    searchET.setInputType(InputType.TYPE_CLASS_NUMBER);
                    searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_CODE;
                } else if (phone.isChecked()) {
                    searchET.setHint(getResources().getString(R.string.phoneCircleHint));
                    searchET.setInputType(InputType.TYPE_CLASS_PHONE);
                    searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_MOBILE;
                }
            }
        });

        code.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                searchET.setText("");
                noNearby.setVisibility(View.GONE);
                if (name.isChecked()) {
                    searchET.setHint(getResources().getString(R.string.nameHint));
                    searchET.setInputType(InputType.TYPE_CLASS_TEXT);
                    searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_NAME;
                } else if (code.isChecked()) {
                    searchET.setHint(getResources().getString(R.string.codeHint));
                    searchET.setInputType(InputType.TYPE_CLASS_NUMBER);
                    searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_CODE;
                } else if (phone.isChecked()) {
                    searchET.setHint(getResources().getString(R.string.phoneCircleHint));
                    searchET.setInputType(InputType.TYPE_CLASS_PHONE);
                    searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_MOBILE;
                }
            }
        });

        phone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                searchET.setText("");
                noNearby.setVisibility(View.GONE);
                if (name.isChecked()) {
                    searchET.setHint(getResources().getString(R.string.nameHint));
                    searchET.setInputType(InputType.TYPE_CLASS_TEXT);
                    searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_NAME;
                } else if (code.isChecked()) {
                    searchET.setHint(getResources().getString(R.string.codeHint));
                    searchET.setInputType(InputType.TYPE_CLASS_NUMBER);
                    searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_CODE;
                } else if (phone.isChecked()) {
                    searchET.setHint(getResources().getString(R.string.phoneCircleHint));
                    searchET.setInputType(InputType.TYPE_CLASS_PHONE);
                    searchType = Constants.SERVICE_SEARCH_BY_PHARMACY_MOBILE;
                }
            }
        });

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
            pharmaciesLV.setVisibility(View.GONE);
            noNearby.setVisibility(View.GONE);
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
                customer.getId(),
                searchType,
                searchET.getText().toString());
        call.enqueue(new Callback<ArrayList<PharmacyDistance>>() {
            @Override
            public void onResponse(Call<ArrayList<PharmacyDistance>> call, Response<ArrayList<PharmacyDistance>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    ArrayList<PharmacyDistance> nearby = response.body();
                    CirclePharmaciesAdapter adapter = new CirclePharmaciesAdapter(nearby, SearchCirclePharmacyByName.this, noNearby);
                    pharmaciesLV.setAdapter(adapter);
                    pharmaciesLV.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    noNearby.setVisibility(View.GONE);

                } else {
                    pharmaciesLV.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SearchCirclePharmacyByName.this, getResources().getString(R.string.noNearby), Toast.LENGTH_LONG).show();
                    Log.d("TEST_CERT13", response.code() + "");
                    noNearby.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PharmacyDistance>> call, Throwable t) {
                Toast.makeText(SearchCirclePharmacyByName.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                pharmaciesLV.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                noNearby.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("circles"))
                intent = new Intent(SearchCirclePharmacyByName.this, Circles.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("circlesFull"))
                intent = new Intent(SearchCirclePharmacyByName.this, CirclesFull.class);
            else
                intent = new Intent(SearchCirclePharmacyByName.this, Dashboard.class);
        } else intent = new Intent(SearchCirclePharmacyByName.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
