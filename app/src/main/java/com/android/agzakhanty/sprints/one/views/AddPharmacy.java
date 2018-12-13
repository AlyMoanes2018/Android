package com.android.agzakhanty.sprints.one.views;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.sprints.one.views.fragments.PharmaciesListFragment;
import com.android.agzakhanty.sprints.one.views.fragments.PharmaciesMapFragment;
import com.android.agzakhanty.sprints.three.views.NewConsultation;
import com.android.agzakhanty.sprints.two.views.CirclesFull;
import com.android.agzakhanty.sprints.two.views.CustomTabLayout;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.two.views.NearbyCircleSearch;
import com.android.agzakhanty.sprints.two.views.NewOrder;
import com.android.agzakhanty.sprints.two.views.SearchCirclePharmacyByName;
import com.android.agzakhanty.sprints.two.views.SearchPharmacyByName;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddPharmacy extends AppCompatActivity {

    @BindView(R.id.toolbar)
     Toolbar toolbar;
    @BindView(R.id.tabs)
    CustomTabLayout tabLayout;
    @BindView(R.id.viewpager)
     ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pharmacy);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PharmaciesListFragment(), getResources().getString(R.string.list));
        adapter.addFragment(new PharmaciesMapFragment(), getResources().getString(R.string.map));
        viewPager.setAdapter(adapter);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("dash"))
                intent = new Intent(AddPharmacy.this, Dashboard.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("fav"))
                intent = new Intent(AddPharmacy.this, FavouritePharmacy.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("cf"))
                intent = new Intent(AddPharmacy.this, CirclesFull.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("cons"))
                intent = new Intent(AddPharmacy.this, NewConsultation.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("NO"))
                intent = new Intent(AddPharmacy.this, NewOrder.class);
            else
                intent = new Intent(AddPharmacy.this, Dashboard.class);
        } else intent = new Intent(AddPharmacy.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
