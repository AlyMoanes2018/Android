package com.android.agzakhanty.sprints.two.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderDetails;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderResponseModel;
import com.android.agzakhanty.sprints.two.adapters.UserRatingsAdapter;
import com.android.agzakhanty.sprints.two.models.CategoriesResponseModel;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.models.UserRatings;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.DetailedOrderResponseModel;
import com.android.agzakhanty.sprints.two.views.fragments.AdsFragment;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdDetails extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar appBar;
    @BindView(R.id.ratesRecycler)
    RecyclerView ratesRecycler;
    @BindView(R.id.adItemTV)
    TextView adItemTV;
    @BindView(R.id.pharmacyNameTV)
    TextView pharmacyNameTV;
    @BindView(R.id.expiryDateTV)
    TextView expiryDateTV;
    @BindView(R.id.adImg)
    ImageView adImg;
    @BindView(R.id.adPrice)
    TextView adPrice;
    @BindView(R.id.desc2TV)
    TextView descriptionTV;
    @BindView(R.id.categoriesValTV)
    TextView categoriesValTV;
    @BindView(R.id.noUserRatesTV)
    TextView noUserRatesTV;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    AdResponseModel ad;
    DetailedOrderResponseModel adItems;
    PrefManager prefManager;
    ProgressDialog dialog;
    LinearLayoutManager MyLayoutManager;
    Customer customer;

    @OnClick(R.id.order)
    public void placeReview() {
        Intent intent = new Intent(AdDetails.this, AddAdvertismentRate.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "adDet");
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
    }

    @OnClick(R.id.addToCart)
    public void confirmOrder() {
        //create the order object
        Log.d("TEST_AD_ITEM", new Gson().toJson(adItems.getListItem()));
        Log.d("TEST_AD_ITEM", adItems.getTotal());
        Intent intent = new Intent(AdDetails.this, AddOrderByItemsSelection.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("adItems", new Gson().toJson(adItems.getListItem()));
        intent.putExtra("adPrice", adItems.getTotal());
        intent.putExtra("adId", ad.getAdvId());
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);
        ButterKnife.bind(this);
        prefManager = PrefManager.getInstance(this);
        swiperefresh.setOnRefreshListener(this);
        String adJSON = PrefManager.getInstance(this).read(Constants.AD_DETAILS_JSON_OBJECT);
        ad = new Gson().fromJson(adJSON, new TypeToken<AdResponseModel>() {
        }.getType());
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        dialog = DialogCreator.getInstance(this);
        dialog.setMessage(getResources().getString(R.string.adDet));
        dialog.show();
        goToAdTagsWS();
        adItemTV.setText(ad.getAdvSubject());
        pharmacyNameTV.setText("(" + ad.getPcyName() + ")");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date now = Calendar.getInstance().getTime();
        Date end = null;
        long days = 0;
        try {
            end = formatter.parse(ad.getAdvExpireDate().split("T")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (now != null && end != null) {
            Log.d("TEST_DATE", "NOT NULL");
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            startDate.setTime(now);
            endDate.setTime(end);
            long diff = endDate.getTimeInMillis() - startDate.getTimeInMillis();
            Log.d("TEST_DATE", diff + "");
            days = diff / (24 * 60 * 60 * 1000);
            Log.d("TEST_DATE", days + "");
            String expiryText = getResources().getString(R.string.adExpiryDtl) + " " + days + " ";
            if (prefManager.readInt(Constants.APP_LANGUAGE) == 0) {
                if (days <= 0)
                    expiryText = getResources().getString(R.string.adExpiryDtl3);
                else if (days <= 10)
                    expiryText += getResources().getString(R.string.adExpiryDtl2);
                else if (days > 10)
                    expiryText += getResources().getString(R.string.adExpiryDtl4);
            } else if (prefManager.readInt(Constants.APP_LANGUAGE) == 1) {
                if (days <= 0)
                    expiryText = getResources().getString(R.string.adExpiryDtl3);
                else if (days >= 2)
                    expiryText += getResources().getString(R.string.adExpiryDtl2);
                else if (days == 1)
                    expiryText += getResources().getString(R.string.adExpiryDtl4);
            }
            expiryDateTV.setText(expiryText);
        } else Log.d("TEST_DATE", "NULL");

        if (ad.getAdvImage() != null && !ad.getAdvImage().equalsIgnoreCase("null") && !ad.getAdvImage().isEmpty()) {
            Glide
                    .with(AdDetails.this)
                    .load(Constants.BASE_URL + ad.getAdvImage())
                    .centerCrop()
                    .into(adImg);
        } else {
            Glide
                    .with(AdDetails.this)
                    .load(Constants.NO_IMG_FOUND_URL)
                    .centerCrop()
                    .into(adImg);
        }


        adPrice.setText(ad.getTotalOrgPrice() + " " + getResources().getString(R.string.egp) + "");
        descriptionTV.setText(ad.getDetails());
        CommonTasks.setUpTranslucentStatusBar(this);

        setSupportActionBar(appBar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ratesRecycler.setHasFixedSize(true);
        goToGetAdDetailsWS();


    }

    private void getUserRates() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<UserRatings>> call = apiService.loadAdUserRates(ad.getAdvId());
        call.enqueue(new Callback<ArrayList<UserRatings>>() {
                         @Override
                         public void onResponse(Call<ArrayList<UserRatings>> call, Response<ArrayList<UserRatings>> response) {
                             if (response.body() != null && response.isSuccessful()) {
                                 if (response.body().size() > 0 & ratesRecycler != null) {
                                     noUserRatesTV.setVisibility(View.GONE);
                                     ratesRecycler.setAdapter(new UserRatingsAdapter(response.body(), AdDetails.this));
                                     MyLayoutManager = new LinearLayoutManager(AdDetails.this);
                                     MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                     ratesRecycler.setLayoutManager(MyLayoutManager);
                                 }

                             } else {
                                 noUserRatesTV.setVisibility(View.VISIBLE);

                             }
                             dialog.dismiss();
                         }

                         @Override
                         public void onFailure(Call<ArrayList<UserRatings>> call, Throwable t) {
                             Log.d("TEST_FAIL", "IT's The Rates");
                             Toast.makeText(AdDetails.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                             noUserRatesTV.setVisibility(View.VISIBLE);
                             dialog.dismiss();
                         }
                     }

        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void goToAdTagsWS() {
        Log.d("TEST_FAIL", ad.getAdvId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<CategoriesResponseModel>> call = apiService.getAdvTags(ad.getAdvId());
        call.enqueue(new Callback<ArrayList<CategoriesResponseModel>>() {
                         @Override
                         public void onResponse(Call<ArrayList<CategoriesResponseModel>> call, Response<ArrayList<CategoriesResponseModel>> response) {
                             if (response.body() != null && response.isSuccessful()) {
                                 String tagsConcatenated = "";
                                 if (response.body().size() > 0) {
                                     for (int i = 0; i < response.body().size(); i++) {
                                         if (prefManager.readInt(Constants.SP_LANGUAGE_KEY) == 0) {
                                             if (i < response.body().size() - 1)
                                                 tagsConcatenated += response.body().get(i).getTagAr() + " - ";
                                             else
                                                 tagsConcatenated += response.body().get(i).getTagAr();
                                         } else if (prefManager.readInt(Constants.SP_LANGUAGE_KEY) == 1) {
                                             if (i < response.body().size() - 1)
                                                 tagsConcatenated += response.body().get(i).getTagEn() + " - ";
                                             else
                                                 tagsConcatenated += response.body().get(i).getTagEn();
                                         }
                                     }
                                     categoriesValTV.setText(tagsConcatenated);
                                 } else categoriesValTV.setText(getResources().getString(R.string.noCats));
                             } else {
                                 categoriesValTV.setText(getResources().getString(R.string.noCats));

                             }
                             dialog.dismiss();
                         }

                         @Override
                         public void onFailure(Call<ArrayList<CategoriesResponseModel>> call, Throwable t) {
                             Log.d("TEST_FAIL", t.getMessage());
                             categoriesValTV.setText(getResources().getString(R.string.noCats));
                             Toast.makeText(AdDetails.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                             dialog.dismiss();
                         }
                     }

        );
    }

    private void goToGetAdDetailsWS() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DetailedOrderResponseModel> call = apiService.getCircleAdDetails(ad.getAdvId());
        call.enqueue(new Callback<DetailedOrderResponseModel>() {
            @Override
            public void onResponse(Call<DetailedOrderResponseModel> call, Response<DetailedOrderResponseModel> response) {
                if (response.body() != null) {

                    adItems = response.body();
                    swiperefresh.setRefreshing(false);
                    getUserRates();
                } else {
                    Log.d("TEST_FAIL", "IT's The AD Details NULL");
                    Toast.makeText(AdDetails.this, getResources().getString(R.string.adItemFailure), Toast.LENGTH_LONG).show();
                    swiperefresh.setRefreshing(false);
                    getUserRates();
                }
            }

            @Override
            public void onFailure(Call<DetailedOrderResponseModel> call, Throwable t) {
                Toast.makeText(AdDetails.this, getResources().getString(R.string.adItemFailure), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                swiperefresh.setRefreshing(false);
                getUserRates();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("dash"))
                intent = new Intent(AdDetails.this, Dashboard.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("ads"))
                intent = new Intent(AdDetails.this, Ads.class);
            else
                intent = new Intent(AdDetails.this, Dashboard.class);
        } else intent = new Intent(AdDetails.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }

    @Override
    public void onRefresh() {
        goToGetAdDetailsWS();
    }

    private void goToSaveOrderWS(SaveOrderResponseModel order) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Boolean> call = apiService.saveCustomerOrder(order);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body()) {
                        Toast.makeText(AdDetails.this, getResources().getString(R.string.saveOrderSuccess), Toast.LENGTH_LONG).show();
                        //back to my orders after saving order
                        Intent intent = new Intent(AdDetails.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                        finish();
                    } else {
                        Toast.makeText(AdDetails.this, getResources().getString(R.string.saveOrderFailure), Toast.LENGTH_LONG).show();
                        Log.d("TEST_NULL", response.code() + "");
                    }
                } else {
                    Log.d("TEST_NULL", response.code() + "");
                    Toast.makeText(AdDetails.this, getResources().getString(R.string.saveOrderFailure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(AdDetails.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }
}
