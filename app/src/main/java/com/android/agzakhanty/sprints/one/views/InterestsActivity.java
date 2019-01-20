package com.android.agzakhanty.sprints.one.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.adapters.InterestsAdapter;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.TagAndUser;
import com.android.agzakhanty.sprints.one.models.TagsAndStatus;
import com.android.agzakhanty.sprints.one.models.api_responses.TagUserResponseModel;
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

public class InterestsActivity extends AppCompatActivity {

    @BindView(R.id.intersts)
    ListView tagsList;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    ArrayList<TagsAndStatus> allTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        allTags = new ArrayList<>();
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        goToGetTagsWS();
        /*Bundle args = new Bundle();
        args.putString("text", getResources().getString(R.string.zegoOffers));
        args.putInt("mode", Constants.ZEGO_DEFAULT);*/


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.NextButton)
    public void onNextClicked() {
        ArrayList<TagAndUser> tagUsr = new ArrayList<>();
        for (int i = 0; i < allTags.size(); i++) {
            if (allTags.get(i) != null && allTags.get(i).getStatus().equalsIgnoreCase("true"))
                tagUsr.add(allTags.get(i).getIfCheckedObject());
        }
        Log.d("TEST_TAG", new Gson().toJson(tagUsr));
        goToSaveTagsWS(tagUsr);

    }

    @OnClick(R.id.skip)
    public void onSkipClicked(){
        Intent i = new Intent(InterestsActivity.this, FinalRegistrationStep.class);
        startActivity(i);
        finish();
    }

    private void goToGetTagsWS() {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<TagsAndStatus>> call = apiService.getAllCustomerTags(customer.getId());
        call.enqueue(new Callback<ArrayList<TagsAndStatus>>() {
            @Override
            public void onResponse(Call<ArrayList<TagsAndStatus>> call, Response<ArrayList<TagsAndStatus>> response) {
                if (response.body() != null) {
                    allTags = response.body();
                    Log.d("TEST_RESPONSE2", new Gson().toJson(allTags) + "   DATA IS HERE");
                    InterestsAdapter adapter = new InterestsAdapter(allTags, InterestsActivity.this);
                    tagsList.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    tagsList.setVisibility(View.VISIBLE);

                } else {
                    Log.d("TEST_RESPONSE", response.code() + "  EE");
                    progressBar.setVisibility(View.GONE);
                    //tagsList.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<TagsAndStatus>> call, Throwable t) {
                Toast.makeText(InterestsActivity.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                //tagsList.setVisibility(View.VISIBLE);
                Log.d("TEST_CERT", t.getMessage());
            }
        });
    }

    public void goToSaveTagsWS(ArrayList<TagAndUser> listOfUserTags) {
        progressBar.setVisibility(View.VISIBLE);
        tagsList.setVisibility(View.GONE);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<TagUserResponseModel> call = apiService.saveCustomerTags(customer.getId(), listOfUserTags);
        call.enqueue(new Callback<TagUserResponseModel>() {
            @Override
            public void onResponse(Call<TagUserResponseModel> call, Response<TagUserResponseModel> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
                    Intent i = new Intent(InterestsActivity.this, FinalRegistrationStep.class);
                    startActivity(i);
                    finish();

                } else {
                    Log.d("TEST_RESPONSE", response.code() + "  EE");
                    progressBar.setVisibility(View.GONE);
                    tagsList.setVisibility(View.VISIBLE);
                    Toast.makeText(InterestsActivity.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<TagUserResponseModel> call, Throwable t) {
                Toast.makeText(InterestsActivity.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                tagsList.setVisibility(View.VISIBLE);
                //tagsList.setVisibility(View.VISIBLE);
                Log.d("TEST_CERT", t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, FavouritePharmacy.class);
        startActivity(i);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
    }
}
