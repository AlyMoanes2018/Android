package com.android.agzakhanty.sprints.two.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.Pharmacy;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.one.views.AddPharmacy;
import com.android.agzakhanty.sprints.two.views.fragments.CirclesFullFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CirclesFull extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar appBar;
    @BindView(R.id.tabs)
    CustomTabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.favPharmData)
    TextView favPharmDataTV;
    @BindView(R.id.editButton)
    Button editFavPharmacyButton;
    @BindView(R.id.callButton)
    ImageView callFavPharmacy;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.noCirclesLayout)
    RelativeLayout noPharmciesInCircleRL;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.addButton)
    ImageView addButton;
    ArrayList<PharmacyDistance> circlePharmacies;
    Customer customer;
    ViewPager.OnPageChangeListener onPageChangeListener;
    Boolean mPhoneCallPermissionGranted;

    @OnClick(R.id.selectPharamacy)
    public void addPharmacyToCircle() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioSelect);
        final RadioButton data = (RadioButton) view.findViewById(R.id.radioData);
        final RadioButton surrounding = (RadioButton) view.findViewById(R.id.radioSurr);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (data.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(CirclesFull.this, SearchCirclePharmacyByName.class);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "circlesFull");
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                } else if (surrounding.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(CirclesFull.this, NearbyCircleSearch.class);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "circlesFull");
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                }
            }
        });
        dialog.show();
    }

    @OnClick(R.id.addButton)
    public void addPharmacyToCircle2() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioSelect);
        final RadioButton data = (RadioButton) view.findViewById(R.id.radioData);
        final RadioButton surrounding = (RadioButton) view.findViewById(R.id.radioSurr);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (data.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(CirclesFull.this, SearchCirclePharmacyByName.class);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "circlesFull");
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                } else if (surrounding.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(CirclesFull.this, NearbyCircleSearch.class);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "circlesFull");
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                }
            }
        });
        dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circles_full);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(appBar);
        CommonTasks.addLeftMenu(this, appBar);
        circlePharmacies = new ArrayList<>();
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        mPhoneCallPermissionGranted = false;
        if (customer.getFavPcy() != null && !customer.getFavPcy().isEmpty()) {
            callFavPharmacy.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            favPharmDataTV.setVisibility(View.GONE);
            editFavPharmacyButton.setVisibility(View.GONE);
            goToWS(customer.getFavPcy());
        } else {
            favPharmDataTV.setText(getResources().getString(R.string.noFavP));
            editFavPharmacyButton.setText(getResources().getString(R.string.addP));
            editFavPharmacyButton.setVisibility(View.VISIBLE);
            callFavPharmacy.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                if (viewPager.getCurrentItem() == 0 && page != null) {
                    ((CirclesFullFragment) page).loadActivePharmacies();
                } else if (viewPager.getCurrentItem() == 1 && page != null) {
                    ((CirclesFullFragment) page).loadInactivePharmacies();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.addOnPageChangeListener(onPageChangeListener);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        goToGetCirclesWS();
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
                if (response.body() != null) {
                    circlePharmacies = response.body();

                    if (circlePharmacies.size() > 0) {
                        Log.d("TEST_Full", new Gson().toJson(customer));
                        mainLayout.setBackgroundColor(Color.parseColor("#E9F0F1"));
                        viewPager.setVisibility(View.VISIBLE);
                        addButton.setVisibility(View.VISIBLE);
                        tabLayout.setVisibility(View.VISIBLE);
                        noPharmciesInCircleRL.setVisibility(View.GONE);
                        PrefManager.getInstance(CirclesFull.this).write(Constants.CIRCLE_ALL_PHARMACIES_LIST, new Gson().toJson(circlePharmacies));
                        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                        if (viewPager.getCurrentItem() == 0 && page != null) {
                            ((CirclesFullFragment) page).loadActivePharmacies();
                        } else if (viewPager.getCurrentItem() == 1 && page != null) {
                            ((CirclesFullFragment) page).loadInactivePharmacies();
                        }

                    } else {
                        mainLayout.setBackgroundColor(Color.WHITE);
                        viewPager.setVisibility(View.GONE);
                        addButton.setVisibility(View.GONE);
                        tabLayout.setVisibility(View.GONE);
                        noPharmciesInCircleRL.setVisibility(View.VISIBLE);
                    }
                } else {
                    mainLayout.setBackgroundColor(Color.WHITE);
                    viewPager.setVisibility(View.GONE);
                    addButton.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    noPharmciesInCircleRL.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PharmacyDistance>> call, Throwable t) {
                Toast.makeText(CirclesFull.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                mainLayout.setBackgroundColor(Color.WHITE);
                viewPager.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                noPharmciesInCircleRL.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        CirclesFull.ViewPagerAdapter adapter = new CirclesFull.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CirclesFullFragment(), getResources().getString(R.string.active));
        adapter.addFragment(new CirclesFullFragment(), getResources().getString(R.string.inactive));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void goToWS(String id) {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PharmacyDistance> call = apiService.getCustomerFavouritePharmacy(id, customer.getId(), null, null);
        call.enqueue(new Callback<PharmacyDistance>() {
            @Override
            public void onResponse(Call<PharmacyDistance> call, Response<PharmacyDistance> response) {
                if (response.body() != null) {
                    PharmacyDistance model = response.body();
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        favPharmDataTV.setText(model.getPharmacy().getName() + ":\n\n" +
                                model.getPharmacy().getAddress());
                        editFavPharmacyButton.setText(getResources().getString(R.string.edit));
                        callFavPharmacy.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        favPharmDataTV.setVisibility(View.VISIBLE);
                        editFavPharmacyButton.setVisibility(View.VISIBLE);
                    } else {
                        favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                        editFavPharmacyButton.setText(getResources().getString(R.string.addP));
                        editFavPharmacyButton.setVisibility(View.VISIBLE);
                        callFavPharmacy.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CirclesFull.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<PharmacyDistance> call, Throwable t) {
                favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                editFavPharmacyButton.setText(getResources().getString(R.string.addP));
                editFavPharmacyButton.setVisibility(View.VISIBLE);
                callFavPharmacy.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CirclesFull.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.editButton)
    public void nextClicked() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioSelect);
        final RadioButton data = (RadioButton) view.findViewById(R.id.radioData);
        final RadioButton surrounding = (RadioButton) view.findViewById(R.id.radioSurr);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (data.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(CirclesFull.this, SearchPharmacyByName.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_CIRCLES_FULL);
                    startActivity(intent);
                    finish();
                } else if (surrounding.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(CirclesFull.this, AddPharmacy.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_CIRCLES_FULL);
                    startActivity(intent);
                    finish();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("dash"))
                intent = new Intent(CirclesFull.this, Dashboard.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("searchPcyByName"))
                intent = new Intent(CirclesFull.this, SearchCirclePharmacyByName.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("nearbyCircleSearch"))
                intent = new Intent(CirclesFull.this, NearbyCircleSearch.class);
            else
                intent = new Intent(CirclesFull.this, Dashboard.class);
        } else intent = new Intent(CirclesFull.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mPhoneCallPermissionGranted = false;
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhoneCallPermissionGranted = true;
                    Log.d("TEST_CALL2", mPhoneCallPermissionGranted + "");
                    PrefManager.getInstance(this).write(Constants.SP_CALL_PERMISSION_GRANTED, mPhoneCallPermissionGranted + "");
                } else {
                    mPhoneCallPermissionGranted = false;
                    Log.d("TEST_CALL2", mPhoneCallPermissionGranted + "");
                    PrefManager.getInstance(this).write(Constants.SP_CALL_PERMISSION_GRANTED, mPhoneCallPermissionGranted + "");
                }
                break;
            }
            default: {
                mPhoneCallPermissionGranted = false;
                PrefManager.getInstance(this).write(Constants.SP_CALL_PERMISSION_GRANTED, mPhoneCallPermissionGranted + "");
                break;
            }
        }
    }
}
