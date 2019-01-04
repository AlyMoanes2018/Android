package com.android.agzakhanty.sprints.two.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.views.fragments.OrdersFragment;
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

public class MyOrders extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    CustomTabLayout tabLayout;
    @BindView(R.id.progress_bar2)
    ProgressBar progressBar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.noOrders)
    TextView noOrders;
    @BindView(R.id.addButton)
    ImageView addButton;
    @BindView(R.id.searchOrdersET)
    EditText searchOrdersET;
    Customer customer;

    ViewPager.OnPageChangeListener onPageChangeListener;
    ArrayList<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        ButterKnife.bind(this);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());

        if (customer.getFavPcy() == null || customer.getFavPcy().isEmpty())
            addButton.setVisibility(View.GONE);
        else addButton.setVisibility(View.VISIBLE);

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
                    ((OrdersFragment) page).loadActiveOrders(query);
                } else if (viewPager.getCurrentItem() == 1 && page != null) {
                    ((OrdersFragment) page).loadInactiveOrders(query);
                }
            }
        });
        setSupportActionBar(toolbar);
        if (getCallingActivity() != null) {
            Toast.makeText(this, getResources().getString(R.string.ordersForResult), Toast.LENGTH_LONG).show();
            addButton.setVisibility(View.GONE);
        } else {
            CommonTasks.addLeftMenu(this, toolbar);
            addButton.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_dehaze_black_24dp));
        }
        orders = new ArrayList<>();
        CommonTasks.setUpTranslucentStatusBar(this);
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                if (viewPager.getCurrentItem() == 0 && page != null) {
                    ((OrdersFragment) page).loadActiveOrders(searchOrdersET.getText().toString());
                } else if (viewPager.getCurrentItem() == 1 && page != null) {
                    ((OrdersFragment) page).loadInactiveOrders(searchOrdersET.getText().toString());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.addOnPageChangeListener(onPageChangeListener);


        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        if (customer.getFavPcy() != null && !customer.getFavPcy().isEmpty()) {
            Log.d("TEST_HIDE", "IF");
            addButton.setVisibility(View.VISIBLE);
        } else {
            Log.d("TEST_HIDE", "ELSE");
            addButton.setVisibility(View.GONE);
        }
        goToGetOrdersWS();
    }

    private void goToGetOrdersWS() {

        Log.d("TEST_UPDATE", new Gson().toJson(customer));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Order>> call = apiService.getAllCustomerOrders(customer.getId());

        call.enqueue(new Callback<ArrayList<Order>>() {
                         @Override
                         public void onResponse(Call<ArrayList<Order>> call, Response<ArrayList<Order>> response) {
                             progressBar.setVisibility(View.GONE);
                             Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                             if (response.body() != null && response.isSuccessful()) {
                                 orders = response.body();

                                 if (orders.size() > 0) {

                                     noOrders.setVisibility(View.GONE);
                                     PrefManager.getInstance(MyOrders.this).write(Constants.ORDER_ALL_CUSTOMER_ORDER, new Gson().toJson(orders));
                                     if (viewPager.getCurrentItem() == 0 && page != null) {
                                         ((OrdersFragment) page).loadActiveOrders(searchOrdersET.getText().toString());
                                     }

                                 } else {
                                     Log.d("TEST_ORDERS", "WS call succeeded");
                                     ((OrdersFragment) page).updateUIForNoOrdersFound();
                                 }

                             } else {

                                 ((OrdersFragment) page).updateUIForNoOrdersFound();
                             }

                         }

                         @Override
                         public void onFailure(Call<ArrayList<Order>> call, Throwable t) {
                             Toast.makeText(MyOrders.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                             Log.d("TEST_CERT", t.getMessage());
                             noOrders.setVisibility(View.VISIBLE);
                             Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                             ((OrdersFragment) page).updateUIForNoOrdersFound();
                         }
                     }

        );
    }


    private void setupViewPager(ViewPager viewPager) {
        MyOrders.ViewPagerAdapter adapter = new MyOrders.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OrdersFragment(), getResources().getString(R.string.active));
        adapter.addFragment(new OrdersFragment(), getResources().getString(R.string.inactive));
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

    @OnClick(R.id.addButton)
    public void onAddClicked() {
        Intent intent = new Intent(MyOrders.this, NewOrder.class);
        intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "myOrders");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }

    public void setTabCustomFont() {


        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabCount = vg.getChildCount();
        for (int j = 0; j < tabCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bold.otf"));
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("dash"))
                intent = new Intent(MyOrders.this, Dashboard.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("newOrder"))
                intent = new Intent(MyOrders.this, NewOrder.class);
            else
                intent = new Intent(MyOrders.this, Dashboard.class);
        } else intent = new Intent(MyOrders.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
