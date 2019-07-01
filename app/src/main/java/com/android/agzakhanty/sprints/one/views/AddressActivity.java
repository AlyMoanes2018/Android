package com.android.agzakhanty.sprints.one.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.City;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.one.models.api_responses.Governorate;
import com.android.agzakhanty.sprints.three.models.api_responses.ViolationTypesResponesModel;
import com.android.agzakhanty.sprints.three.views.ReportViolation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddressActivity extends AppCompatActivity {

    @BindView(R.id.govSpinner)
    AppCompatSpinner governeratesSpinner;
    int selectedGovIndex = -1;
    @BindView(R.id.govWrapper)
    TextInputLayout govTI;
    ArrayList<String> govNames;
    ArrayList<Integer> govIds;
    @BindView(R.id.cityWrapper)
    TextInputLayout citiesTI;
    int selectedCityIndex = -1;
    @BindView(R.id.citySpinner)
    AppCompatSpinner citiesSpinner;
    ArrayList<String> citiesNames;
    ArrayList<Integer> citiesIds;
    @BindView(R.id.addressDetailedET)
    EditText addressET;
    @BindView(R.id.toolbar)
    Toolbar appBar;
    ProgressDialog dialog;
    Customer customer;


    @OnClick(R.id.skip)
    public void skip() {
        Intent i = new Intent(this, CustomerLocationSelector.class);
        if (getIntent().getStringExtra("fromEdit") != null)
            i.putExtra("fromEdit", "y");
        startActivity(i);
    }

    @OnClick(R.id.registerNextButton)
    public void submit() {
        if (validate()) {
            dialog.setMessage("جاري حفظ البيانات..");
            dialog.show();
            gotoSaveWS();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        setSupportActionBar(appBar);
        CommonTasks.setUpTranslucentStatusBar(this);
        dialog = DialogCreator.getInstance(this);
        govNames = new ArrayList<String>();
        govIds = new ArrayList<Integer>();
        citiesNames = new ArrayList<>();
        citiesIds = new ArrayList<>();
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        if (getIntent().getStringExtra("fromEdit") != null &&
                getIntent().getStringExtra("fromEdit").equalsIgnoreCase("y") &&
                customer.getAddress() != null) {
            addressET.setText(customer.getAddress());
        }
        governeratesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGovIndex = govIds.get(i);
                Log.d("TEST_GOV", selectedGovIndex + "  E");
                getAllCitiesWS();
                if (selectedGovIndex > -1) {
                    govTI.setError(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCityIndex = citiesIds.get(i);
                if (selectedCityIndex > -1)
                    citiesTI.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dialog.setMessage("جاري تحميل البيانات");
        dialog.show();
        getAllGovenratesWS();
    }

    boolean validate() {
        if (selectedGovIndex == -1) {
            govTI.setError("يجب اختيار المحافظة والمدينة أولا");
            return false;
        }
        if (selectedCityIndex == -1) {
            govTI.setError("يجب اختيار المحافظة والمدينة أولا");
            return false;
        }
        if (addressET.getText().toString().isEmpty()) {
            govTI.setError("يجب ادخال العنوان بالتفصيل");
            return false;
        }
        return true;
    }

    private void gotoSaveWS() {

        customer.setGovernrate_id(selectedGovIndex);
        customer.setCityId(selectedCityIndex);
        customer.setAddress(addressET.getText().toString());
        customer.setRegId(PrefManager.getInstance(AddressActivity.this).read(Constants.REGISTRATION_ID));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.updateCustomerInfo(customer.getId(), customer);
        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        PrefManager.getInstance(AddressActivity.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(response.body().getCstmr()));
                        Log.d("TEST_PHOTO_AFTER", new Gson().toJson(response.body().getCstmr()));
                        Log.d("TEST_REG", response.body().getCstmr().getRegId() + " E");
                        Intent intent = new Intent(AddressActivity.this, CustomerLocationSelector.class);
                        if (getIntent().getStringExtra("fromEdit") != null)
                            intent.putExtra("fromEdit", "y");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AddressActivity.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(AddressActivity.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }

    private void getAllGovenratesWS() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Governorate>> call = apiService.getAllGovernrates();
        call.enqueue(new Callback<ArrayList<Governorate>>() {
            @Override
            public void onResponse(Call<ArrayList<Governorate>> call, Response<ArrayList<Governorate>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    govNames.clear();
                    govIds.clear();
                    for (int i = 0; i < response.body().size(); i++) {

                        if (response.body().get(i) != null && response.body().get(i).getName() != null) {
                            govNames.add(response.body().get(i).getName());
                            govIds.add(response.body().get(i).getId());
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            AddressActivity.this, R.layout.spinner_item, govNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    governeratesSpinner.setAdapter(adapter);
                    if (getIntent().getStringExtra("fromEdit") != null &&
                            getIntent().getStringExtra("fromEdit").equalsIgnoreCase("y")) {
                        if (customer.getGovernrate_id() != null && customer.getGovernrate_id() > 0)
                            governeratesSpinner.setSelection(govIds.indexOf(customer.getGovernrate_id()));
                    }


                } else {
                    Log.d("TEST_TYPES", response.code() + "");
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<Governorate>> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(AddressActivity.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getAllCitiesWS() {
        Log.d("TEST_GOV_CITY", "I'm Called");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<City>> call = apiService.getAllGovernrateCities(selectedGovIndex);
        call.enqueue(new Callback<ArrayList<City>>() {
            @Override
            public void onResponse(Call<ArrayList<City>> call, Response<ArrayList<City>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    citiesNames.clear();
                    citiesIds.clear();
                    for (int i = 0; i < response.body().size(); i++) {

                        citiesNames.add(response.body().get(i).getName());
                        citiesIds.add(response.body().get(i).getId());
                    }

                    for (String str : citiesNames)
                        Log.d("TEST_CITY", str);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            AddressActivity.this, R.layout.spinner_item, citiesNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citiesSpinner.setAdapter(adapter);
                    if (getIntent().getStringExtra("fromEdit") != null &&
                            getIntent().getStringExtra("fromEdit").equalsIgnoreCase("y")) {
                        if (customer.getCityId() != null && customer.getCityId() > 0)
                            citiesSpinner.setSelection(citiesIds.indexOf(customer.getCityId()));
                    }

                } else {
                    Log.d("TEST_TYPES", response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<City>> call, Throwable t) {
                Toast.makeText(AddressActivity.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
