package com.android.agzakhanty.sprints.three.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.android.agzakhanty.general.application.LocalDBHelper;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.three.models.Reminder;
import com.android.agzakhanty.sprints.three.views.fragments.RemindersFragment;
import com.android.agzakhanty.sprints.two.views.CustomTabLayout;
import com.android.agzakhanty.sprints.two.views.Dashboard;
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

public class Reminders extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
   /* @BindView(R.id.progress_bar2)
    ProgressBar progressBar;*/
    @BindView(R.id.tabs)
    CustomTabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.addButton)
    ImageView addButton;
    @BindView(R.id.searchRemindersET)
    EditText searchRemindersET;
    ViewPager.OnPageChangeListener onPageChangeListener;
    List<Reminder> reminders;
    private LocalDBHelper db;
    Fragment page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        ButterKnife.bind(this);
        db = new LocalDBHelper(this);
        searchRemindersET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = searchRemindersET.getText().toString();
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                if (viewPager.getCurrentItem() == 0 && page != null) {
                    ((RemindersFragment) page).loadActiveReminders(query);
                } else if (viewPager.getCurrentItem() == 1 && page != null) {
                    ((RemindersFragment) page).loadCompletedReminders(query);
                }
            }
        });
        setSupportActionBar(toolbar);
        reminders = new ArrayList<>();
        CommonTasks.setUpTranslucentStatusBar(this);
        CommonTasks.addLeftMenu(this, toolbar);
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TEST_REM", "I'M CALLED");
                if (reminders.size() > 0) {
                    page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                    if (viewPager.getCurrentItem() == 0 && page != null) {
                        ((RemindersFragment) page).loadActiveReminders(searchRemindersET.getText().toString());
                    } else if (viewPager.getCurrentItem() == 1 && page != null) {
                        ((RemindersFragment) page).loadCompletedReminders(searchRemindersET.getText().toString());
                    }
                } else {
                    if (page != null)
                        ((RemindersFragment) page).updateUIForNoRemindersFound();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.addOnPageChangeListener(onPageChangeListener);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 0);
        reminders = db.getAllReminders();
        goToGetRemindersWS();
    }

    private void goToGetRemindersWS() {




            PrefManager.getInstance(Reminders.this).write(Constants.REMINDER_ALL_CUSTOMER_REMINDER, new Gson().toJson(reminders));
            viewPager.post(new Runnable()
            {
                @Override
                public void run()
                {
                    onPageChangeListener .onPageSelected(viewPager.getCurrentItem());
                }
            });

    }


    private void setupViewPager(ViewPager viewPager) {
        Reminders.ViewPagerAdapter adapter = new Reminders.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RemindersFragment(), getResources().getString(R.string.active));
        adapter.addFragment(new RemindersFragment(), getResources().getString(R.string.doneReminders));
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
        Intent intent = new Intent(Reminders.this, NewReminder.class);
        intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "reminders");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Reminders.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }


}
