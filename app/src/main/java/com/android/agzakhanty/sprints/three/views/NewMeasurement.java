package com.android.agzakhanty.sprints.three.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.three.adapters.MeasurementsAdapter;
import com.android.agzakhanty.sprints.three.adapters.NewMeasurementRecyclerAdapter;
import com.android.agzakhanty.sprints.three.models.Measurement;
import com.android.agzakhanty.sprints.two.interfaces.ClickListener;
import com.android.agzakhanty.sprints.two.models.RecyclerTouchListener;
import com.android.agzakhanty.sprints.two.views.AddOrderByItemsSelection;
import com.android.agzakhanty.sprints.two.views.AddOrderByPrescriptionPhoto;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.two.views.MyOrders;
import com.android.agzakhanty.sprints.three.views.NewMeasurementDetails;
import com.android.agzakhanty.sprints.two.views.NewOrder;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewMeasurement extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.noMeasurementsTV)
    TextView noMeasurementsTV;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;

    ArrayList<Measurement> measurements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        ButterKnife.bind(this);
        measurements = new ArrayList<>();
        CommonTasks.setUpTranslucentStatusBar(this);
        CommonTasks.addLeftMenu(this, toolbar);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(NewMeasurement.this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(NewMeasurement.this, NewMeasurementDetails.class);
                //measurements.get(position).setIsTwoValues("y");
                Log.d("TEST_CHART_NEW",new Gson().toJson(measurements.get(position)));
                intent.putExtra("isView", "n");
                intent.putExtra("measure", new Gson().toJson(measurements.get(position)));
                intent.putExtra("id", measurements.get(position).getId());
                if (PrefManager.getInstance(NewMeasurement.this).readInt(Constants.APP_LANGUAGE) == 0)
                    intent.putExtra("title", measurements.get(position).getNameAr());
                else
                    intent.putExtra("title", measurements.get(position).getNameEn());
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        callGetAllMeasuresWS();
    }

    private void callGetAllMeasuresWS() {
        Log.d("TEST_WS", "WEB SERVICE");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Measurement>> call = apiService.getAllMeasures();
        call.enqueue(new Callback<ArrayList<Measurement>>() {
            @Override
            public void onResponse(Call<ArrayList<Measurement>> call, Response<ArrayList<Measurement>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    measurements = response.body();
                    NewMeasurementRecyclerAdapter adapter = new NewMeasurementRecyclerAdapter(measurements, NewMeasurement.this);
                    recyclerView.setLayoutManager(new GridLayoutManager(NewMeasurement.this, 2));
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    noMeasurementsTV.setVisibility(View.GONE);
                    progress_bar.setVisibility(View.GONE);

                } else {
                    recyclerView.setVisibility(View.GONE);
                    noMeasurementsTV.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Measurement>> call, Throwable t) {
                Toast.makeText(NewMeasurement.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
//                Log.d("TEST_CERT", t.getMessage());
                recyclerView.setVisibility(View.GONE);
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
        Intent intent = new Intent(NewMeasurement.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
