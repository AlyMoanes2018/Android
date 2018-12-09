package com.android.agzakhanty.sprints.three.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
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
import com.android.agzakhanty.sprints.three.adapters.MeasurementsAdapter;
import com.android.agzakhanty.sprints.three.models.Measurement;
import com.android.agzakhanty.sprints.three.models.api_responses.MeasureDetailsResponseModel;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyMeasurements extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    @BindView(R.id.noMeasurementsTV)
    TextView noMeasurementsTV;
    String pcyId, cstmrId;

    ArrayList<MeasureDetailsResponseModel> measurements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_measurements);
        ButterKnife.bind(this);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        pcyId = ((Customer) new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType())).getFavPcy();
        cstmrId = ((Customer) new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType())).getId();
        measurements = new ArrayList<>();
        CommonTasks.setUpTranslucentStatusBar(this);
        CommonTasks.addLeftMenu(this, toolbar);
        callGetMeasuresDetailsWS();
    }

    private void callGetMeasuresDetailsWS() {
        Log.d("TEST_WS", "WEB SERVICE");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<MeasureDetailsResponseModel>> call = apiService.getMeasureDetails(cstmrId, pcyId);
        call.enqueue(new Callback<ArrayList<MeasureDetailsResponseModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MeasureDetailsResponseModel>> call, Response<ArrayList<MeasureDetailsResponseModel>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    measurements = response.body();
                    MeasurementsAdapter adapter = new MeasurementsAdapter(measurements, MyMeasurements.this);
                    list.setAdapter(adapter);
                    list.setVisibility(View.VISIBLE);
                    noMeasurementsTV.setVisibility(View.GONE);
                    progress_bar.setVisibility(View.GONE);

                } else {
                    list.setVisibility(View.GONE);
                    noMeasurementsTV.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<MeasureDetailsResponseModel>> call, Throwable t) {
                Toast.makeText(MyMeasurements.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
//                Log.d("TEST_CERT", t.getMessage());
                list.setVisibility(View.GONE);
                noMeasurementsTV.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyMeasurements.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
