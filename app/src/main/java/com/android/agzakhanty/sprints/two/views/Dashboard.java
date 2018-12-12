package com.android.agzakhanty.sprints.two.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.Agzakhanty;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.one.views.AddPharmacy;
import com.android.agzakhanty.sprints.three.models.ShakeDetector;
import com.android.agzakhanty.sprints.three.views.NewConsultation;
import com.android.agzakhanty.sprints.three.views.NewMeasurement;
import com.android.agzakhanty.sprints.three.views.NewReminder;
import com.android.agzakhanty.sprints.two.adapters.HomeAdsAdapter;
import com.android.agzakhanty.sprints.two.broadcast_recievers.AlarmReceiver;
import com.android.agzakhanty.sprints.two.interfaces.ClickListener;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.models.RecyclerTouchListener;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Dashboard extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.firstTimelineCircle)
    ImageView firstCircle;
    @BindView(R.id.secondTimelineCircle)
    ImageView secondCircle;
    @BindView(R.id.thirdTimelineCircle)
    ImageView thirdCircle;
    @BindView(R.id.firstTimeLineLine)
    View firstLine;
    @BindView(R.id.secondTimeLineLine)
    View secondLine;
    @BindView(R.id.statusOneNameTV)
    TextView statusOneNameTV;
    @BindView(R.id.statusTwoNameTV)
    TextView statusTwoNameTV;
    @BindView(R.id.statusThreeNameTV)
    TextView statusThreeNameTV;
    @BindView(R.id.menu_yellow)
    FloatingActionMenu menuRed;
    @BindView(R.id.fab1)
    FloatingActionButton fab1;
    @BindView(R.id.fab2)
    FloatingActionButton fab2;
    @BindView(R.id.fab3)
    FloatingActionButton fab3;
    @BindView(R.id.fab4)
    FloatingActionButton fab4;
    @BindView(R.id.fab5)
    FloatingActionButton fab5;
    @BindView(R.id.favPharmData)
    TextView favPharmDataTV;
    @BindView(R.id.callButton)
    ImageView callFavPharmacy;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.pharmacyLogo)
    CircleImageView pharmacyLogo;
    @BindView(R.id.twentyFourTV)
    TextView twentyFourTV;
    @BindView(R.id.distanceTV)
    TextView distanceTV;
    @BindView(R.id.deliveryButton)
    ImageView deliveryButton;
    @BindView(R.id.phoneButton)
    ImageView phoneButton;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.ordersTotalValueTV)
    TextView ordersTotalValueTV;
    @BindView(R.id.lastMeasureValueTV)
    TextView lastMeasureValueTV;
    @BindView(R.id.progress_bar2)
    ProgressBar progressBar2;
    @BindView(R.id.noActiveOrder)
    TextView noActiveOrder;
    @BindView(R.id.timelineLayout)
    LinearLayout timelineLayout;
    @BindView(R.id.timelineText)
    RelativeLayout timelineText;
    @BindView(R.id.deliveryImg)
    ImageView deliveryImg;
    @BindView(R.id.timeRemaining)
    LinearLayout timeRemaining;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.ads_progress_bar)
    ProgressBar ads_progress_bar;
    @BindView(R.id.numberOfActiveOffersTV)
    TextView numberOfActiveOffersTV;
    @BindView(R.id.editButton)
    Button editFavPharmacyButton;
    @BindView(R.id.timeRemainingTV2)
    TextView timeRemainingTV2;
    @BindView(R.id.deliverdLayout)
    LinearLayout deliverdLayout;
    String tempRemainingTime = "1";
    int seconds = 0;
    CountDownTimer timer;
    Order activeOrder;
    ArrayList<AdResponseModel> models;
    private PendingIntent pendingIntent;
    private Intent alarmIntent;
    private AlarmManager manager;
    PharmacyDistance model;
    Boolean doubleBackToExitPressedOnce;
    ArrayList<AdResponseModel> activeAds;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @OnClick(R.id.allOffersTV)
    public void watchAllAds() {
        Intent intent = new Intent(Dashboard.this, Ads.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }


    @OnClick(R.id.callButton)
    public void callPCY() {
        Boolean isPermissionGranted = PrefManager.getInstance(this).read(Constants.SP_CALL_PERMISSION_GRANTED).equals("true");
        Log.d("TEST_CALL", isPermissionGranted + "");
        if (model.getPharmacy().getMobile() != null && !model.getPharmacy().getMobile().isEmpty() && isPermissionGranted) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + model.getPharmacy().getMobile()));
            startActivity(intent);
        } else {
            getPhoneCallPermission();
        }
    }

    private void getPhoneCallPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + model.getPharmacy().getMobile()));
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    220);
        }
    }


    @OnClick(R.id.timelineLayout)
    public void getActiveOrderDetails() {
        if (activeOrder.getOrderId() != null) {
            //view order details
            Intent intent = new Intent(this, ViewOrderDetails.class);
            intent.putExtra("order", activeOrder.getOrderId());
            startActivity(intent);
            overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
            finish();
        }

    }

    @OnClick(R.id.deliveredButton)
    public void orderDelivered() {
        Intent intent = new Intent(Agzakhanty.getContext(), AddAdvertismentRate.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "dash");
        Log.d("TEST_NUM_DASH", activeOrder.getOrderId() + " E");
        intent.putExtra("orderNum", activeOrder.getOrderId());
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(toolbar);
        Log.d("TEST_OS_ID", PrefManager.getInstance(this).read(Constants.REGISTRATION_ID) + " E");
        Log.d("TEST_TIMER", PrefManager.getInstance(Dashboard.this).read(Constants.TIMER_IS_STARTED) + " E");
        activeOrder = new Order();
        doubleBackToExitPressedOnce = false;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                Intent intent = new Intent(Dashboard.this, NewOrder.class);
                intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "dash");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                finish();
            }
        });


        // Retrieve a PendingIntent that will perform a broadcast
        alarmIntent = new Intent(this, AlarmReceiver.class);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Log.d("TEST_TOKEND", FirebaseInstanceId.getInstance().getToken() + " E");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_dehaze_black_24dp));
        CommonTasks.addLeftMenu(this, toolbar);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        Log.d("TEST_REG", customer.getRegId() + " E");

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
            Log.d("TEST_UPDATE2", "Called from on create");
            goToActiveOrderWS(true);
        }
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, NewOrder.class);
                intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "dash");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                finish();
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, NewReminder.class);
                intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "dash");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                finish();
            }
        });
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, NewMeasurement.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                finish();
            }
        });
        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, NewConsultation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                finish();
            }
        });
        setTimelineStep(2);
        recycler.addOnItemTouchListener(new RecyclerTouchListener(Dashboard.this, recycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (activeAds.get(position) != null) {
                    Log.d("TEST_AD", "IN");
                    Intent intent = new Intent(Dashboard.this, com.android.agzakhanty.sprints.two.views.AdDetails.class);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "dash");
                    PrefManager.getInstance(Dashboard.this).write(Constants.AD_DETAILS_JSON_OBJECT, new Gson().toJson(activeAds.get(position)));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    public void goToActiveOrderWS(final Boolean isAdsNeeded) {
        progressBar2.setVisibility(View.VISIBLE);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        final Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        Log.d("TEST_UPDATE", new Gson().toJson(customer));
        Log.d("TEST_UPDATE2", "I'm Called");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Order> call = apiService.getLastActiveOrder(customer.getId());
        call.enqueue(new Callback<Order>() {
                         @Override
                         public void onResponse(Call<Order> call, Response<Order> response) {
                             progressBar.setVisibility(View.GONE);
                             if (response.body() != null && response.isSuccessful()) {
                                 activeOrder = response.body();
                                 Log.d("TEST_NUM_ORD", activeOrder.getOrderId() + " E");
                                 progressBar2.setVisibility(View.GONE);
                                 noActiveOrder.setVisibility(View.GONE);
                                 timelineLayout.setVisibility(View.VISIBLE);
                                 timelineText.setVisibility(View.VISIBLE);
                                 deliveryImg.setVisibility(View.VISIBLE);
                                 timeRemaining.setVisibility(View.VISIBLE);
                                 setTimelineStep(Integer.parseInt(activeOrder.getStatusId()));
                                 // set remaining time
                                 if (activeOrder.getStatusId().equalsIgnoreCase("1") ||
                                         activeOrder.getStatusId().equalsIgnoreCase("2") ||
                                         activeOrder.getStatusId().equalsIgnoreCase("3") ||
                                         activeOrder.getStatusId().equalsIgnoreCase("4")) {
                                     timeRemainingTV2.setTextSize(12f);
                                     timeRemainingTV2.setText(R.string.NA);
                                 } else if ((activeOrder.getStatusId().equals("5"))) {

                                     if (activeOrder.getRemainingTime().equalsIgnoreCase("0")) {
                                         timelineLayout.setVisibility(View.GONE);
                                         deliverdLayout.setVisibility(View.VISIBLE);
                                     } else {
                                         int minutes = Integer.parseInt(activeOrder.getRemainingTime());
                                         try {
                                             seconds = Integer.parseInt(activeOrder.getRemainingTimeSecond());
                                         } catch (Exception e) {
                                             seconds = 0;
                                         }
                                         Log.d("TEST_SECONDS", seconds + "");
                                         int timerLimitInMilliS = minutes * 60 * 1000;
                                         Log.d("TEST_TIMER", timerLimitInMilliS + "");
                                         timeRemainingTV2.setTextSize(20f);
                                         timeRemainingTV2.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                                         alarmIntent.putExtra("orderNum", activeOrder.getOrderId());
                                         Log.d("TEST_NUM", activeOrder.getOrderId());

                                         if (PrefManager.getInstance(Dashboard.this).read(Constants.TIMER_IS_STARTED).isEmpty()) {
                                             timer = new CountDownTimer(timerLimitInMilliS, 1000) {
                                                 @Override
                                                 public void onTick(long l) {
                                                     int updatedSeconds = (int) (l / 1000) % 60;
                                                     int updatedMinutes = (int) ((l / (1000 * 60)) % 60);
                                                     timeRemainingTV2.setText(String.format("%02d", updatedMinutes) + ":" + String.format("%02d", updatedSeconds));

                                                 }

                                                 @Override
                                                 public void onFinish() {
                                                     timeRemainingTV2.setText("00:00");
                                                     timelineLayout.setVisibility(View.GONE);
                                                     deliverdLayout.setVisibility(View.VISIBLE);
                                                     PrefManager.getInstance(Dashboard.this).write(Constants.TIMER_IS_STARTED, "");
                                                 }
                                             }.start();
                                             PrefManager.getInstance(Dashboard.this).write(Constants.TIMER_IS_STARTED, "t");
                                         }
                                     }
                                 }


                                 if (isAdsNeeded)
                                     goToGetAdsWS();
                             } else

                             {

                                 progressBar2.setVisibility(View.GONE);
                                 noActiveOrder.setVisibility(View.VISIBLE);
                                 timelineLayout.setVisibility(View.INVISIBLE);
                                 timelineText.setVisibility(View.INVISIBLE);
                                 deliveryImg.setVisibility(View.INVISIBLE);
                                 timeRemaining.setVisibility(View.INVISIBLE);
                                 if (isAdsNeeded)
                                     goToGetAdsWS();
                             }

                         }

                         @Override
                         public void onFailure(Call<Order> call, Throwable t) {
                             Toast.makeText(Dashboard.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                             Log.d("TEST_CERT", t.getMessage());
                             progressBar2.setVisibility(View.GONE);
                             noActiveOrder.setVisibility(View.VISIBLE);
                             timelineLayout.setVisibility(View.INVISIBLE);
                             timelineText.setVisibility(View.INVISIBLE);
                             deliveryImg.setVisibility(View.INVISIBLE);
                             timeRemaining.setVisibility(View.INVISIBLE);
                             if (isAdsNeeded)
                                 goToGetAdsWS();
                         }
                     }

        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setTimelineStep(int step) {
        if (step == 1 || step == 2 || step == 3 || step == 4) {
            firstCircle.setBackgroundResource(R.drawable.layout_circle3);
            firstLine.setBackgroundColor(Color.parseColor("#F5F5F5"));
            secondCircle.setBackgroundResource(R.drawable.layout_circle4);
            secondLine.setBackgroundColor(Color.parseColor("#F5F5F5"));
            thirdCircle.setBackgroundResource(R.drawable.layout_circle4);
            statusOneNameTV.setTextColor(Color.parseColor("#459283"));
            statusTwoNameTV.setTextColor(Color.parseColor("#F5F5F5"));
            statusThreeNameTV.setTextColor(Color.parseColor("#F5F5F5"));

        } else if (step == 5) {
            firstCircle.setBackgroundResource(R.drawable.layout_circle3);
            firstLine.setBackgroundColor(Color.parseColor("#449D44"));
            secondCircle.setBackgroundResource(R.drawable.layout_circle3);
            secondLine.setBackgroundColor(Color.parseColor("#F5F5F5"));
            thirdCircle.setBackgroundResource(R.drawable.layout_circle4);
            statusOneNameTV.setTextColor(Color.parseColor("#459283"));
            statusTwoNameTV.setTextColor(Color.parseColor("#459283"));
            statusThreeNameTV.setTextColor(Color.parseColor("#F5F5F5"));
        } else if (step == 6) {
            firstCircle.setBackgroundResource(R.drawable.layout_circle3);
            firstLine.setBackgroundColor(Color.parseColor("#449D44"));
            secondCircle.setBackgroundResource(R.drawable.layout_circle3);
            secondLine.setBackgroundColor(Color.parseColor("#449D44"));
            thirdCircle.setBackgroundResource(R.drawable.layout_circle3);
            statusOneNameTV.setTextColor(Color.parseColor("#459283"));
            statusTwoNameTV.setTextColor(Color.parseColor("#459283"));
            statusThreeNameTV.setTextColor(Color.parseColor("#459283"));

        } else if (step == 7) {
            progressBar2.setVisibility(View.GONE);
            noActiveOrder.setVisibility(View.VISIBLE);
            timelineLayout.setVisibility(View.INVISIBLE);
            timelineText.setVisibility(View.INVISIBLE);
            deliveryImg.setVisibility(View.INVISIBLE);
            timeRemaining.setVisibility(View.INVISIBLE);
        }
    }

    private void goToWS(String id) {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PharmacyDistance> call = apiService.getCustomerFavouritePharmacy(id, customer.getId(), customer.getLatitude(), customer.getLongitude());
        call.enqueue(new Callback<PharmacyDistance>() {
            @Override
            public void onResponse(Call<PharmacyDistance> call, Response<PharmacyDistance> response) {
                if (response.body() != null) {
                    pharmacyLogo.setVisibility(View.VISIBLE);
                    model = response.body();
                    Log.d("TEST_DIST", model.getDistanceResult() + "  E");
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        if (model.getTotalNoOrders() != null)
                            ordersTotalValueTV.setText(model.getTotalNoOrders());
                        if (model.getLastMeasure() != null)
                            lastMeasureValueTV.setText(model.getLastMeasure());
                        model.getPharmacy().setRate(model.getRate());
                        model.getPharmacy().setDistance(model.getDistanceResult());
                        favPharmDataTV.setText(getResources().getString(R.string.yourFavPcy) + "\n" + model.getPharmacy().getName() + "\n" +
                                model.getPharmacy().getAddress());
                        callFavPharmacy.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        favPharmDataTV.setVisibility(View.VISIBLE);
                        Log.d("TEST_IMG", model.getPharmacy().getLogoURL() + "  E");
                        if (model.getPharmacy().getLogoURL() != null && !model.getPharmacy().getLogoURL().isEmpty()) {

                            Glide
                                    .with(Dashboard.this)
                                    .load(Constants.BASE_URL + model.getPharmacy().getLogoURL())
                                    .centerCrop()
                                    .into(pharmacyLogo);
                        } else {
                            Glide
                                    .with(Dashboard.this)
                                    .load(Constants.NO_IMG_FOUND_URL)
                                    .centerCrop()
                                    .into(pharmacyLogo);
                        }
                        if (model.getPharmacy().getAllDay() != null && !model.getPharmacy().getAllDay().isEmpty()) {
                            twentyFourTV.setVisibility(View.VISIBLE);
                        }
                        if (model.getPharmacy().getDistance() != null && !model.getPharmacy().getDistance().isEmpty()) {

                            distanceTV.setText(model.getPharmacy().getDistance());
                            distanceTV.setVisibility(View.VISIBLE);
                        }
                        if (model.getPharmacy().getDelivery() != null && !model.getPharmacy().getDelivery().isEmpty()) {
                            deliveryButton.setVisibility(View.VISIBLE);
                        }
                        if (model.getPharmacy().getChat() != null && !model.getPharmacy().getChat().isEmpty()) {
                            phoneButton.setVisibility(View.VISIBLE);
                        }
                        if (model.getPharmacy().getRate() != null && !model.getPharmacy().getRate().isEmpty()) {
                            ratingBar.setRating(Float.parseFloat(model.getPharmacy().getRate()));
                            ratingBar.setVisibility(View.VISIBLE);
                        }
                        PrefManager.getInstance(Dashboard.this).write(model.getPharmacy().getId(), Constants.FAVOURITE_PCY_ID);
                        Log.d("TEST_UPDATE2", "Called from Succes favourite pcy ws");
                        goToActiveOrderWS(true);

                    } else {
                        favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                        callFavPharmacy.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        pharmacyLogo.setVisibility(View.GONE);
                        Toast.makeText(Dashboard.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                        Log.d("TEST_UPDATE2", "Called from null favourite pcy ws");
                        goToActiveOrderWS(true);
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<PharmacyDistance> call, Throwable t) {
                favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                callFavPharmacy.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Log.d("TEST_UPDATE2", "Called from failure favourite pcy ws");
                goToActiveOrderWS(true);
                Toast.makeText(Dashboard.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToGetAdsWS() {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        final Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        Log.d("TEST_UPDATE", customer.getId() + "");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<AdResponseModel>> call = apiService.getCircleAds(customer.getId());
        call.enqueue(new Callback<ArrayList<AdResponseModel>>() {
            @Override
            public void onResponse(Call<ArrayList<AdResponseModel>> call, Response<ArrayList<AdResponseModel>> response) {
                ads_progress_bar.setVisibility(View.GONE);
                if (response.body() != null && response.isSuccessful()) {
                    models = response.body();
                    activeAds = new ArrayList<>();
                    for (int i = 0; i < models.size(); i++) {
                        if (models.get(i).getIsActive() != null && models.get(i).getIsActive().equalsIgnoreCase("y"))
                            activeAds.add(models.get(i));
                    }
                    Log.d("TEST_ADS_SERVICE", new Gson().toJson(activeAds));
                    Log.d("TEST_BUG_BUG", response.body().size() + " models");
                    recycler.setLayoutManager
                            (new LinearLayoutManager(Dashboard.this, LinearLayoutManager.HORIZONTAL, true));
                    HomeAdsAdapter adapter = new HomeAdsAdapter(activeAds, Dashboard.this);
                    recycler.setAdapter(adapter);
                    numberOfActiveOffersTV.setText("(" + activeAds.size() + " " +
                            getResources().getString(R.string.activeOffersNumber) + ")");

                } else {
                    Log.d("TEST_Full2", response.code() + "");
                    ads_progress_bar.setVisibility(View.GONE);
                    // zero active ads
                    numberOfActiveOffersTV.setText("(" + 0 + " " +
                            getResources().getString(R.string.activeOffersNumber) + ")");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AdResponseModel>> call, Throwable t) {
                Toast.makeText(Dashboard.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                numberOfActiveOffersTV.setText("(" + 0 + " " +
                        getResources().getString(R.string.activeOffersNumber) + ")");

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
                    Intent intent = new Intent(Dashboard.this, SearchPharmacyByName.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_CIRCLES_FULL);
                    startActivity(intent);
                    finish();
                } else if (surrounding.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(Dashboard.this, AddPharmacy.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_CIRCLES_FULL);
                    startActivity(intent);
                    finish();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onStop() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onStop();
        if (timer != null) {
            timer.cancel();
            PrefManager.getInstance(Dashboard.this).write(Constants.TIMER_IS_STARTED, "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        Log.d("TEST_UPDATE2", "Called from on resume");
        goToActiveOrderWS(false);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.backAgainToExit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 220: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PrefManager.getInstance(this).write(Constants.SP_CALL_PERMISSION_GRANTED, true + "");
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + model.getPharmacy().getMobile()));
                    startActivity(intent);
                } else {
                    PrefManager.getInstance(this).write(Constants.SP_CALL_PERMISSION_GRANTED, false + "");
                }
                break;
            }
            default: {
                PrefManager.getInstance(this).write(Constants.SP_CALL_PERMISSION_GRANTED, false + "");
                break;
            }
        }
    }

}
