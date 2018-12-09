package com.android.agzakhanty.sprints.two.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.two.models.Ad;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.android.agzakhanty.sprints.two.views.fragments.AdsFragment;
import com.android.agzakhanty.sprints.two.views.fragments.CirclesFullFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Ads extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar appBar;
    @BindView(R.id.tabs)
    CustomTabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.searchOrdersET)
    EditText searchOrdersET;
    ArrayList<AdResponseModel> models;
    ViewPager.OnPageChangeListener onPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(appBar);
        CommonTasks.addLeftMenu(this, appBar);
        searchOrdersET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = searchOrdersET.getText().toString();
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                if (viewPager.getCurrentItem() == 0 && page != null) {
                    ((AdsFragment) page).loadActiveAds(query);
                } else if (viewPager.getCurrentItem() == 1 && page != null) {
                    ((AdsFragment) page).loadInactiveAds(query);
                }
            }
        });
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                if (viewPager.getCurrentItem() == 0 && page != null) {
                    ((AdsFragment) page).loadActiveAds(searchOrdersET.getText().toString());
                } else if (viewPager.getCurrentItem() == 1 && page != null) {
                    ((AdsFragment) page).loadInactiveAds(searchOrdersET.getText().toString());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.addOnPageChangeListener(onPageChangeListener);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("TEST_TIME", Calendar.getInstance().getTime() + "");
        goToGetAdsWS();

    }

    private void goToGetAdsWS() {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        final Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        Log.d("TEST_UPDATE", new Gson().toJson(customer));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<AdResponseModel>> call = apiService.getCircleAds(customer.getId());
        call.enqueue(new Callback<ArrayList<AdResponseModel>>() {
            @Override
            public void onResponse(Call<ArrayList<AdResponseModel>> call, Response<ArrayList<AdResponseModel>> response) {
                if (response.body() != null) {

                    models = response.body();
                    Log.d("TEST_Full", new Gson().toJson(customer));
                    viewPager.setVisibility(View.VISIBLE);
                    PrefManager.getInstance(Ads.this).write(Constants.CIRCLE_ALL_ADS_LIST, new Gson().toJson(models));
                    Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                    if (viewPager.getCurrentItem() == 0 && page != null) {
                        ((AdsFragment) page).loadActiveAds(searchOrdersET.getText().toString());
                    } else if (viewPager.getCurrentItem() == 1 && page != null) {
                        ((AdsFragment) page).loadInactiveAds(searchOrdersET.getText().toString());
                    }

                    Log.d("TEST_TIME", Calendar.getInstance().getTime() + "");
                } else {
                    viewPager.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<ArrayList<AdResponseModel>> call, Throwable t) {
                Toast.makeText(Ads.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                viewPager.setVisibility(View.GONE);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        Ads.ViewPagerAdapter adapter = new Ads.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AdsFragment(), getResources().getString(R.string.active));
        adapter.addFragment(new AdsFragment(), getResources().getString(R.string.inactive));
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

    //OnBack single
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Ads.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }

}
