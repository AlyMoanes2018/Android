package com.android.agzakhanty.sprints.two.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DataValidator;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.RateResponseModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddAdvertismentRate extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar appBar;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.commentWrapper)
    TextInputLayout commentLayout;
    @BindView(R.id.commentET)
    EditText commentET;
    String orderNumber;
    Customer customer;
    ProgressDialog dialog;
    AdResponseModel ad;

    @OnClick(R.id.review)
    public void review() {
        if (DataValidator.isStringEmpty(commentET.getText().toString()))
            commentLayout.setError(getResources().getString(R.string.reviewRequired));
        if (ratingBar.getRating() < 1f)
            Toast.makeText(AddAdvertismentRate.this, getResources().getString(R.string.ratingErr), Toast.LENGTH_LONG).show();
        if (!DataValidator.isStringEmpty(commentET.getText().toString()) && ratingBar.getRating() >= 1f) {
            String type = "";
            String trnsID = "";
            String pcyID = "";
            if (orderNumber != null) {
                type = Constants.RATE_TYPE_ORDER;
                trnsID = orderNumber;
                if (getIntent().getStringExtra("PcyId") != null && !getIntent().getStringExtra("PcyId").equals("-1"))
                    pcyID = getIntent().getStringExtra("PcyId");
                //else pcyID = "1007";

            } else {
                type = Constants.RATE_TYPE_ADV;
                trnsID = ad.getAdvId();
                pcyID = ad.getPcyId();
            }
            dialog.setMessage(getResources().getString(R.string.placingRate));
            Log.d("TEST_RATE", type + " " + trnsID + " " + pcyID);
            callSaveAdWS(type, trnsID, pcyID);
        }
    }

    private void callSaveAdWS(String type, String trnsID, String pcyID) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RateResponseModel> call = apiService.saveUserRate(customer.getId(),
                ratingBar.getRating(),
                trnsID,
                pcyID,
                type,
                commentET.getText().toString());
        call.enqueue(new Callback<RateResponseModel>() {
            @Override
            public void onResponse(Call<RateResponseModel> call, Response<RateResponseModel> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        Toast.makeText(AddAdvertismentRate.this, getResources().getString(R.string.placingRateSuccess), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddAdvertismentRate.this, Dashboard.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                        finish();
                    } else
                        Toast.makeText(AddAdvertismentRate.this, getResources().getString(R.string.placingRateFailure), Toast.LENGTH_LONG).show();
                } else {
                    Log.d("TEST_NULL", response.code() + "");
                    Toast.makeText(AddAdvertismentRate.this, getResources().getString(R.string.placingRateFailure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RateResponseModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(AddAdvertismentRate.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advertisment_rate);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(appBar);
        // if this string is null then this is an ad rate else it's an order rate
        orderNumber = getIntent().getStringExtra("orderNum");
        Log.d("TEST_NUM_ADD", orderNumber + " E");
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        String adJSON = PrefManager.getInstance(this).read(Constants.AD_DETAILS_JSON_OBJECT);
        Log.d("TEST_AD", adJSON);
        ad = new Gson().fromJson(adJSON, new TypeToken<AdResponseModel>() {
        }.getType());
        dialog = DialogCreator.getInstance(this);
        if (orderNumber != null && !orderNumber.equals("-1")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            NotificationManagerCompat mNotificationManager =
                    NotificationManagerCompat.from(this);
            mNotificationManager.cancelAll();
            //update customer order
            dialog.setMessage(getResources().getString(R.string.updatingOrder));
            dialog.show();
            goToUpdateOrderWS();
        }

    }

    private void goToUpdateOrderWS() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Boolean> call = apiService.updateCustomerOrder(orderNumber, customer.getId(), "6");
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body()) {
                        Toast.makeText(AddAdvertismentRate.this, getResources().getString(R.string.updateOrderSuccess), Toast.LENGTH_LONG).show();

                    }
                    else
                        Toast.makeText(AddAdvertismentRate.this, getResources().getString(R.string.updateOrderFailure), Toast.LENGTH_LONG).show();
                } else {
                    Log.d("TEST_NULL", response.code() + "");
                    Toast.makeText(AddAdvertismentRate.this, getResources().getString(R.string.updateOrderFailure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(AddAdvertismentRate.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("dash"))
                intent = new Intent(AddAdvertismentRate.this, Dashboard.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("adDet"))
                intent = new Intent(AddAdvertismentRate.this, AdDetails.class);
            else
                intent = new Intent(AddAdvertismentRate.this, Dashboard.class);
        } else intent = new Intent(AddAdvertismentRate.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
