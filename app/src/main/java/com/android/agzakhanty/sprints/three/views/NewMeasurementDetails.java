package com.android.agzakhanty.sprints.three.views;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.three.models.Measurement;
import com.android.agzakhanty.sprints.three.models.api_requests.SaveMeasurementRequestModel;
import com.android.agzakhanty.sprints.three.models.api_requests.SaveViolationtRequestModel;
import com.android.agzakhanty.sprints.three.models.api_requests.UpdateMeasurementRequestModel;
import com.android.agzakhanty.sprints.three.models.api_responses.LastMeasureResponseModel;
import com.android.agzakhanty.sprints.three.models.api_responses.UpdateMeasureResponseModel;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewMeasurementDetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.regTitle)
    TextView title;
    @BindView(R.id.value)
    EditText value;
    @BindView(R.id.timeValue)
    TextView timeValue;
    @BindView(R.id.dateValue)
    TextView dateValue;
    @BindView(R.id.dateTimeView)
    RelativeLayout dateTimeView;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.enterDate)
    Button enterDate;
    @BindView(R.id.enterCurrentDate)
    Button enterCurrentDate;
    @BindView(R.id.viewDataLayout)
    LinearLayout viewDataLayout;
    @BindView(R.id.editTime)
    TextView editTime;
    @BindView(R.id.editDate)
    TextView editDate;
    @BindView(R.id.appBarEditButton)
    ImageView appBarEditButton;
    @BindView(R.id.separator)
    TextView separator;
    @BindView(R.id.value2)
    EditText value2;
    @BindView(R.id.unit)
    TextView unit;

    ProgressDialog dialog;
    Measurement measurement;
    String isView = "n";
    String isUpdate = "n";
    String measurementDate = "";
    String measurementTime = "";
    String customerId = "";
    String pcyId;
    Calendar c;
    int yearNow;
    int monthNow;
    int dayNow;
    int hoursNow;
    int minutesNow;
    int secondsNow;
    LastMeasureResponseModel latestMeasure;


    @OnClick(R.id.save)
    public void saveMeasurement() {


        if (measurementDate.isEmpty() || measurementTime.isEmpty())
            Toast.makeText(this, getResources().getString(R.string.MeasuresDateTime), Toast.LENGTH_LONG).show();
        if (value.getText().toString().isEmpty())
            Toast.makeText(this, getResources().getString(R.string.MeasuresValue), Toast.LENGTH_LONG).show();

        if (!measurementDate.isEmpty() && !measurementTime.isEmpty() && pcyId != null && !pcyId.isEmpty()
                && !value.getText().toString().isEmpty()) {
            String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
            customerId = ((Customer) new Gson().fromJson(custJSON, new TypeToken<Customer>() {
            }.getType())).getId();
            if (isUpdate.equalsIgnoreCase("n")) {
                goToSaveMeasureWS();
            } else {

                goToUpdateMeasureWS();
            }

        }

    }

    private void goToUpdateMeasureWS() {
        dialog.setMessage(getResources().getString(R.string.updatingMeasures));
        dialog.show();
        final UpdateMeasurementRequestModel request = new UpdateMeasurementRequestModel();
        request.setCstId(customerId);
        request.setPcyId(pcyId);
        Log.d("TEST_SAVE", Integer.parseInt(measurement.getId()) + "");
        request.setMsrId(latestMeasure.getMsrId());
        request.setId(latestMeasure.getId());
        request.setVal1(value.getText().toString());
        measurement.setIsTwoValues("y");
        if (measurement.getIsTwoValues().equalsIgnoreCase("y"))
            request.setVal2(value2.getText().toString());
        else
            request.setVal2(null);
        Log.d("TEST_MEASURE", latestMeasure.getId());
        Log.d("TEST_MEASURE", measurementDate + " " + measurementTime);
        Log.d("TEST_MEASURE", new Gson().toJson(request));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UpdateMeasureResponseModel> call = apiService.updateMeasure(latestMeasure.getId(),
                measurementDate + " " + measurementTime, request);
        call.enqueue(new Callback<UpdateMeasureResponseModel>() {
            @Override
            public void onResponse(Call<UpdateMeasureResponseModel> call, Response<UpdateMeasureResponseModel> response) {
                dialog.dismiss();
                Log.d("TEST_MEASURE", measurementDate + " " + measurementTime);
                Log.d("TEST_MEASURE", new Gson().toJson(request));
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        Toast.makeText(NewMeasurementDetails.this, getResources().getString(R.string.updatingMeasuresSuccess), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(NewMeasurementDetails.this, Dashboard.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    } else {

                        Toast.makeText(NewMeasurementDetails.this, getResources().getString(R.string.updatingMeasuresFailure), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<UpdateMeasureResponseModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(NewMeasurementDetails.this, getResources().getString(R.string.updatingMeasuresFailure), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToSaveMeasureWS() {
        dialog.setMessage(getResources().getString(R.string.savingMeasures));
        dialog.show();
        final SaveMeasurementRequestModel request = new SaveMeasurementRequestModel();
        request.setCstId(customerId);
        request.setPcyId(pcyId);
        Log.d("TEST_SAVE", Integer.parseInt(measurement.getId()) + "");
        request.setMsrId(measurement.getId());
        request.setVal1(value.getText().toString());
        measurement.setIsTwoValues("y");
        if (measurement.getIsTwoValues().equalsIgnoreCase("y"))
            request.setVal2(value2.getText().toString());
        else
            request.setVal2(null);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Boolean> call = apiService.saveMeasure(measurementDate + " " + measurementTime, request);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();
                Log.d("TEST_MEASURE", measurementDate + " " + measurementTime);
                Log.d("TEST_MEASURE", new Gson().toJson(request));
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body()) {
                        Toast.makeText(NewMeasurementDetails.this, getResources().getString(R.string.sendingMeasuresSuccess), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(NewMeasurementDetails.this, Dashboard.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    } else {

                        Toast.makeText(NewMeasurementDetails.this, getResources().getString(R.string.sendingMeasuresFailure), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

                Toast.makeText(NewMeasurementDetails.this, getResources().getString(R.string.sendingMeasuresFailure), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.follow)
    public void follow() {
        Intent intent = new Intent(NewMeasurementDetails.this, MyMeasurements.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();
    }

    @OnClick(R.id.chartButton)
    public void goToCharts() {
        Intent intent = new Intent(NewMeasurementDetails.this, MeasurementChart.class);
        intent.putExtra("title", title.getText());
        intent.putExtra("cust", customerId);
        intent.putExtra("pcy", pcyId);
        intent.putExtra("msr", measurement.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();
    }

    @OnClick(R.id.editTime)
    public void onEditTimeClicked() {
        measurementDate = "";
        measurementTime = "";
        dateValue.setText(measurementDate);
        timeValue.setText(measurementTime);
        enterDate.setVisibility(View.VISIBLE);
        enterCurrentDate.setVisibility(View.VISIBLE);
        dateTimeView.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
    }

    @OnClick(R.id.appBarEditButton)
    public void onEditTimeClicked2() {
        measurementDate = "";
        measurementTime = "";
        value.setEnabled(true);
        if (measurement.getIsTwoValues().equalsIgnoreCase("y")) {
            value2.setEnabled(true);
            separator.setVisibility(View.VISIBLE);
        } else {
            value2.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
        }
        dateValue.setText(measurementDate);
        timeValue.setText(measurementTime);
        enterDate.setVisibility(View.VISIBLE);
        enterCurrentDate.setVisibility(View.VISIBLE);
        dateTimeView.setVisibility(View.GONE);
        save.setText(getResources().getString(R.string.update));
        save.setVisibility(View.GONE);
        viewDataLayout.setVisibility(View.GONE);
        isUpdate = "y";

    }


    @OnClick(R.id.editDate)
    public void onEditDateClicked() {
        measurementDate = "";
        measurementTime = "";
        value.setEnabled(true);
        dateValue.setText(measurementDate);
        timeValue.setText(measurementTime);
        enterDate.setVisibility(View.VISIBLE);
        enterCurrentDate.setVisibility(View.VISIBLE);
        dateTimeView.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        viewDataLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.enterDate)
    public void onEnterDateClicked() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                NewMeasurementDetails.this, NewMeasurementDetails.this, yearNow, monthNow, dayNow);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
        datePickerDialog.show();
    }

    @OnClick(R.id.enterCurrentDate)
    public void onEnterCurrentDateClicked() {
        measurementDate = String.format(Locale.US, "%02d", dayNow) + "/"
                + String.format(Locale.US, "%02d", (monthNow + 1)) + "/" +
                String.format(Locale.US, "%02d", yearNow);

        measurementTime = String.format(Locale.US, "%02d", hoursNow) + ":" +
                String.format(Locale.US, "%02d", minutesNow) + ":" +
                String.format(Locale.US, "%02d", secondsNow);
        dateValue.setText(measurementDate);
        timeValue.setText(measurementTime);
        enterDate.setVisibility(View.GONE);
        enterCurrentDate.setVisibility(View.GONE);
        dateTimeView.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);


    }

    //In intent measure: Measure object
    //               &
    //isView: (y or n)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement_details);
        ButterKnife.bind(this);
        dialog = DialogCreator.getInstance(this);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        pcyId = ((Customer) new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType())).getFavPcy();
        customerId = ((Customer) new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType())).getId();
        latestMeasure = new LastMeasureResponseModel();
        c = Calendar.getInstance();
        yearNow = c.get(Calendar.YEAR);
        monthNow = c.get(Calendar.MONTH);
        dayNow = c.get(Calendar.DAY_OF_MONTH);
        hoursNow = c.get(Calendar.HOUR_OF_DAY);
        minutesNow = c.get(Calendar.MINUTE);
        secondsNow = c.get(Calendar.SECOND);
        if (getIntent().getStringExtra("measure") != null) {
            measurement = new Gson().fromJson(getIntent().getStringExtra("measure"), new TypeToken<Measurement>() {
            }.getType());
            measurement.setId(getIntent().getStringExtra("id"));
            Log.d("TEST_CHART_DETAILS", measurement.getId());
            measurement.setIsTwoValues("y");
            unit.setText(measurement.getUnit());
        }

        value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    // This is a test comment

                    // This is another test comment
                }
                return false;
            }
        });

        value2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    // This is a test comment

                    // This is another test comment
                }
                return false;
            }
        });

        title.setText(getIntent().getStringExtra("title"));

        if (getIntent().getStringExtra("isView") != null)
            isView = getIntent().getStringExtra("isView");
        if (isView.equals("y")) {
            value.setEnabled(false);
            if (measurement.getIsTwoValues().equalsIgnoreCase("y")) {
                value2.setEnabled(false);
                separator.setVisibility(View.VISIBLE);
            } else {
                value2.setVisibility(View.GONE);
                separator.setVisibility(View.GONE);
            }
            enterDate.setVisibility(View.GONE);
            enterCurrentDate.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            editDate.setVisibility(View.GONE);
            editTime.setVisibility(View.GONE);
            appBarEditButton.setVisibility(View.VISIBLE);
            viewDataLayout.setVisibility(View.VISIBLE);
            dateTimeView.setVisibility(View.VISIBLE);
            goToGetLatestWS();
        } else {
            value.setEnabled(true);
            if (measurement.getIsTwoValues().equalsIgnoreCase("y")) {
                value2.setEnabled(true);
                separator.setVisibility(View.VISIBLE);
            } else {
                value2.setVisibility(View.GONE);
                separator.setVisibility(View.GONE);
            }
            enterDate.setVisibility(View.VISIBLE);
            enterCurrentDate.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            editDate.setVisibility(View.VISIBLE);
            editTime.setVisibility(View.VISIBLE);
            appBarEditButton.setVisibility(View.GONE);
            viewDataLayout.setVisibility(View.GONE);
            dateTimeView.setVisibility(View.GONE);


        }
    }

    private void goToGetLatestWS() {
        //dialog.show();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LastMeasureResponseModel> call = apiService.getLastMeasureDetails(customerId, measurement.getId(), pcyId);
        call.enqueue(new Callback<LastMeasureResponseModel>() {
            @Override
            public void onResponse(Call<LastMeasureResponseModel> call, Response<LastMeasureResponseModel> response) {
                dialog.dismiss();
                if (response.body() != null && response.isSuccessful()) {
                    latestMeasure = response.body();
                    Log.d("TEST_LATEST", (int) Float.parseFloat(latestMeasure.getVal1()) + "");
                    value.setText((int) Float.parseFloat(latestMeasure.getVal1()) + "");
                    if (latestMeasure.getVal2() != null)
                        value2.setText((int) Float.parseFloat(latestMeasure.getVal2()) + "");
                    dateValue.setText(latestMeasure.getMsrDate().split("T")[0]);
                    timeValue.setText(latestMeasure.getMsrDate().split("T")[1]);
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<LastMeasureResponseModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(NewMeasurementDetails.this, getResources().getString(R.string.sendingMeasuresFailure), Toast.LENGTH_LONG).show();
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
        Intent intent;
        if (isView.equals("n"))
            intent = new Intent(NewMeasurementDetails.this, NewMeasurement.class);
        else intent = new Intent(NewMeasurementDetails.this, MyMeasurements.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        measurementDate = i2 + "/" + (i1 + 1) + "/" + i;
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                NewMeasurementDetails.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                measurementTime = String.format("%02d", i) + ":" + String.format("%02d", i1) + ":00";
                dateValue.setText(measurementDate);
                timeValue.setText(measurementTime);
                enterDate.setVisibility(View.GONE);
                enterCurrentDate.setVisibility(View.GONE);
                dateTimeView.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        }, hoursNow, minutesNow, true);
        timePickerDialog.show();
    }
}
