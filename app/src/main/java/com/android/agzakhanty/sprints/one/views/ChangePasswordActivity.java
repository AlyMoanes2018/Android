package com.android.agzakhanty.sprints.one.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.currentPasswordWrapper)
    TextInputLayout currentPasTI;
    @BindView(R.id.currentPassET)
    TextInputEditText currentPassET;
    @BindView(R.id.passwordWrapper)
    TextInputLayout newPasTI;
    @BindView(R.id.passwordET)
    TextInputEditText newPassET;
    @BindView(R.id.toolbar)
    Toolbar appBar;
    Customer customer;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CommonTasks.addLeftMenu(this, appBar);
        CommonTasks.setUpTranslucentStatusBar(this);
        CommonTasks.setUpPasswordETWithCustomFont(this, currentPassET);
        CommonTasks.setUpPasswordETWithCustomFont(this, newPassET);
        CommonTasks.setUpTextInputLayoutErrors(currentPasTI, currentPassET);
        CommonTasks.setUpTextInputLayoutErrors(newPasTI, newPassET);
        dialog = DialogCreator.getInstance(this);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
    }

    boolean validate(){
        if (currentPassET.getText().toString().isEmpty()){
            currentPasTI.setError("يجب إدخال كلمة المرور الحالية أولا");
            return false;
        }
        if (newPassET.getText().toString().isEmpty()){
            newPasTI.setError("يجب إدخال كلمة المرور الجديدة أولا");
            return false;
        }
        if (!currentPassET.getText().toString().equals(customer.getPwd())){
            currentPasTI.setError("كلمة المرور غير صحيحة");
            return false;
        }

        return true;
    }

    @OnClick(R.id.registerNextButton)
    public void submit(){
        if (validate()){
            dialog.setMessage("جاري تحديث بياناتك..");
            dialog.show();
            goToUpdateWS();
        }
    }

    private void goToUpdateWS() {
        customer.setPwd(newPassET.getText().toString());
        customer.setRegId(PrefManager.getInstance(ChangePasswordActivity.this).read(Constants.REGISTRATION_ID));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.updateCustomerInfo(customer.getId(), customer);
        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        PrefManager.getInstance(ChangePasswordActivity.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(response.body().getCstmr()));
                        Log.d("TEST_PHOTO_AFTER", new Gson().toJson(response.body().getCstmr()));
                        Log.d("TEST_REG", response.body().getCstmr().getRegId() + " E");
                        Intent intent = new Intent(ChangePasswordActivity.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
