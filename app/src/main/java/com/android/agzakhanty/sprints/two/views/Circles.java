package com.android.agzakhanty.sprints.two.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.one.views.AddPharmacy;
import com.android.agzakhanty.sprints.one.views.FavouritePharmacy;
import com.android.agzakhanty.sprints.one.views.InterestsActivity;
import com.android.agzakhanty.sprints.two.models.api_responses.PharmacyResponseModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Circles extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar appBar;
    @BindView(R.id.favPharmData)
    TextView favPharmDataTV;
    @BindView(R.id.editButton)
    Button editFavPharmacyButton;
    @BindView(R.id.callButton)
    ImageView callFavPharmacy;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circles_empty);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(appBar);
        CommonTasks.addLeftMenu(this, appBar);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
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
                        Toast.makeText(Circles.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
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
                Toast.makeText(Circles.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @OnClick(R.id.selectPharamacy)
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
                    Intent intent = new Intent(Circles.this, SearchCirclePharmacyByName.class);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "circles");
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                } else if (surrounding.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(Circles.this, NearbyCircleSearch.class);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "circles");
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                }
            }
        });
        dialog.show();
    }

    @OnClick(R.id.editButton)
    public void editClicked() {
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
                    Intent intent = new Intent(Circles.this, SearchPharmacyByName.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_CIRCLES);
                    startActivity(intent);
                    finish();
                } else if (surrounding.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(Circles.this, AddPharmacy.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_CIRCLES);
                    startActivity(intent);
                    finish();
                }
            }
        });
        dialog.show();
    }
}
