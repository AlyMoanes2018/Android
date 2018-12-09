package com.android.agzakhanty.sprints.three.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.android.agzakhanty.sprints.three.models.api_requests.SaveConsultationRequestModel;
import com.android.agzakhanty.sprints.three.models.api_requests.SaveViolationtRequestModel;
import com.android.agzakhanty.sprints.three.models.api_responses.ConsultationTypesResponesModel;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderDetails;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderResponseModel;
import com.android.agzakhanty.sprints.three.models.api_responses.ViolationTypesResponesModel;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.two.views.MyOrders;
import com.android.agzakhanty.sprints.two.views.NewOrder;
import com.android.agzakhanty.sprints.two.views.SearchCirclePharmacyByName;
import com.android.agzakhanty.sprints.two.views.SearchPharmacyByName;
import com.android.agzakhanty.sprints.two.views.dialogs.AddItemsDialog;
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

public class ReportViolation extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner1)
    Spinner violationTypes;
    @BindView(R.id.typeWrapper)
    TextInputLayout typeWrapper;
    @BindView(R.id.detailsWrapper)
    TextInputLayout detailsWrapper;
    @BindView(R.id.detailsET)
    EditText detailsET;
    @BindView(R.id.addedItemNameTV)
    TextView addedItemNameTV;
    @BindView(R.id.itemNameAndDeleteAction)
    RelativeLayout itemNameAndDeleteAction;


    int spinnerSelectedIndex;
    ProgressDialog dialog;
    ArrayList<String> typesNames;
    ArrayList<String> typesIds;
    Customer customer;
    String violationPcyName;
    String violationPcyID;


    @OnClick(R.id.deletIcon)
    public void deletePCY() {
        violationPcyName = "";
        violationPcyID = "";
        itemNameAndDeleteAction.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_violation);
        ButterKnife.bind(this);
        violationPcyID = "";
        violationPcyName = "";
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        typesIds = new ArrayList<>();
        typesNames = new ArrayList<>();
        spinnerSelectedIndex = -1;
        CommonTasks.setUpTranslucentStatusBar(this);
        CommonTasks.addLeftMenu(this, toolbar);
        violationTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedIndex = Integer.parseInt(typesIds.get(i));
                if (spinnerSelectedIndex > -1)
                    typeWrapper.setError(null);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dialog = DialogCreator.getInstance(this);
        getViolationTypes();
    }

    private void getViolationTypes() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<ViolationTypesResponesModel>> call = apiService.getViolationTypes();
        call.enqueue(new Callback<ArrayList<ViolationTypesResponesModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ViolationTypesResponesModel>> call, Response<ArrayList<ViolationTypesResponesModel>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    typesNames.clear();
                    typesIds.clear();
                    for (int i = 0; i < response.body().size(); i++) {

                        if (PrefManager.getInstance(ReportViolation.this).readInt(Constants.SP_LANGUAGE_KEY) == 0 &&
                                response.body().get(i).getActive().equalsIgnoreCase("y")) {
                            typesNames.add(response.body().get(i).getNameAr());
                            typesIds.add(response.body().get(i).getId());
                        } else if (PrefManager.getInstance(ReportViolation.this).readInt(Constants.SP_LANGUAGE_KEY) == 1 &&
                                response.body().get(i).getActive().equalsIgnoreCase("y")) {
                            typesNames.add(response.body().get(i).getNameEn());
                            typesIds.add(response.body().get(i).getId());
                        } else Log.d("TEST_TYPES", "NOT SAVED");

                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            ReportViolation.this, android.R.layout.simple_spinner_item, typesNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    violationTypes.setAdapter(adapter);

                } else {
                    Log.d("TEST_TYPES", response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ViolationTypesResponesModel>> call, Throwable t) {
                Toast.makeText(ReportViolation.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(ReportViolation.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }

    //sendButton
    @OnClick(R.id.submit)
    public void sendConsultation() {

        if (spinnerSelectedIndex == -1)
            typeWrapper.setError(getResources().getString(R.string.chooseConsultType));

        if (DataValidator.isStringEmpty(detailsET.getText().toString()))
            detailsWrapper.setError(getResources().getString(R.string.chooseConsultTTopic));


        if (spinnerSelectedIndex > -1 && !DataValidator.isStringEmpty(detailsET.getText().toString())) {
            dialog.setMessage(getResources().getString(R.string.seindingVios));
            dialog.show();
            goToSendReportWS();
        }

    }

    private void goToSendReportWS() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        SaveViolationtRequestModel request = new SaveViolationtRequestModel();
        request.setDetails(detailsET.getText().toString());
        request.setReportTypeId(spinnerSelectedIndex + "");
        Call<Boolean> call = apiService.saveViolation(customer.getId(), request);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body()) {
                        Toast.makeText(ReportViolation.this, getResources().getString(R.string.sendingViosSuccess), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ReportViolation.this, Dashboard.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    } else {

                        Toast.makeText(ReportViolation.this, getResources().getString(R.string.sendingViosFailure), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

                Toast.makeText(ReportViolation.this, getResources().getString(R.string.sendingViosFailure), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.addItemTV)
    public void addItem() {
        Intent intent = new Intent(ReportViolation.this, SearchPharmacyByName.class);
        startActivityForResult(intent, 127);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 127 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Log.d("TEST_RESULT", extras.getString("pcyName") + "  E");
                //get pcy data
                violationPcyName = extras.getString("pcyName");
                violationPcyID = extras.getString("pcyID");
                itemNameAndDeleteAction.setVisibility(View.VISIBLE);
                addedItemNameTV.setText(violationPcyName);
            } else Log.d("TEST_RESULT", "NULL");
        } else {
            Toast.makeText(ReportViolation.this, getResources().getString(R.string.noOrdersForResult), Toast.LENGTH_LONG).show();
        }
    }

}
