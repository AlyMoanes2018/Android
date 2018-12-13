package com.android.agzakhanty.sprints.three.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.sprints.three.models.MeasurementChartDetails;
import com.android.agzakhanty.sprints.three.models.api_responses.MeasurementChartDetailsResponseModel;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MeasurementChart extends AppCompatActivity {

    @BindView(R.id.title)
    TextView chartTitle;
    @BindView(R.id.progress_bar)
    ProgressBar dialog;
    @BindView(R.id.lineChart)
    LineChart lineChart;
    @BindView(R.id.fromValueTV)
    TextView fromValueTV;
    @BindView(R.id.toValueTV)
    TextView toTV;
    MeasurementChartDetailsResponseModel chartData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_chart);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        chartTitle.setText(getIntent().getStringExtra("title"));
        chartData = new MeasurementChartDetailsResponseModel();

        goToChartWS();
    }

    private void goToChartWS() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Log.d( "TEST_CHART",getIntent().getStringExtra("cust")+ " " +getIntent().getStringExtra("pcy") + " " + getIntent().getStringExtra("msr"));
        Call<MeasurementChartDetailsResponseModel> call = apiService.getMeasureChartDetails(getIntent().getStringExtra("cust"),
                getIntent().getStringExtra("pcy"), getIntent().getStringExtra("msr"));
        call.enqueue(new Callback<MeasurementChartDetailsResponseModel>() {
            @Override
            public void onResponse(Call<MeasurementChartDetailsResponseModel> call, Response<MeasurementChartDetailsResponseModel> response) {
                dialog.setVisibility(View.GONE);
                if (response.body() != null && response.isSuccessful()) {
                    chartData = response.body();
                    fromValueTV.setText(chartData.getMinMsrDate().split("T")[0]);
                    toTV.setText(chartData.getMaxMsrDate().split("T")[0]);
                    drawChart();
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<MeasurementChartDetailsResponseModel> call, Throwable t) {
                dialog.setVisibility(View.GONE);
                Toast.makeText(MeasurementChart.this, getResources().getString(R.string.sendingMeasuresFailure), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void drawChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entries2 = new ArrayList<>();
        ArrayList<LineDataSet> lines = new ArrayList<LineDataSet>();
        String[] xAxis = new String[chartData.getExistChartCstMsrTrnList().size()];

        for (int i = 0; i < chartData.getExistChartCstMsrTrnList().size(); i++) {
            // get x axis data
            xAxis[i] = chartData.getExistChartCstMsrTrnList().get(i).getMsrDate().split("T")[0];
            // create the datasets
            entries.add(new Entry(Float.parseFloat(chartData.getExistChartCstMsrTrnList().get(i).getVal1()), i));
            if (chartData.getExistChartCstMsrTrnList().get(i).getVal2() != null)
                entries2.add(new Entry(Float.parseFloat(chartData.getExistChartCstMsrTrnList().get(i).getVal2()), i));
        }
        //draw the chart
        LineDataSet lDataSet1 = new LineDataSet(entries, "Value 1");
        lDataSet1.setColor(getResources().getColor(R.color.colorPrimary));
        lDataSet1.setFillColor(getResources().getColor(R.color.colorPrimary));
        lDataSet1.setFillAlpha(1000);
        lDataSet1.setDrawFilled(true);
        lines.add(lDataSet1);

        if (entries2.size() > 0) {
            LineDataSet lDataSet2 = new LineDataSet(entries2, "Value 2");
            lDataSet2.setColor(getResources().getColor(R.color.colorAccent));
            lDataSet2.setFillColor(getResources().getColor(R.color.colorAccent));
            lDataSet2.setFillAlpha(1000);
            lDataSet2.setDrawFilled(true);
            lines.add(lDataSet2);
        }

        lineChart.setData(new LineData(xAxis, lines));
        lineChart.animateY(5000);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MeasurementChart.this, NewMeasurementDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();
        super.onBackPressed();
    }
}
