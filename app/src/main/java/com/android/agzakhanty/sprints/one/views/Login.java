package com.android.agzakhanty.sprints.one.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
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
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login extends AppCompatActivity {

    @BindView(R.id.passwordET)
    EditText passwordET;
    @BindView(R.id.userNameET)
    EditText userNameET;
    @BindView(R.id.userNameWrapper)
    TextInputLayout userNameLayout;
    @BindView(R.id.passwordWrapper)
    TextInputLayout passwordLayout;
    @BindView(R.id.toolbar)
    Toolbar appBar;
    ProgressDialog dialog;


    @OnClick(R.id.loginButton)
    public void login() {
        if (DataValidator.isStringEmpty(userNameET.getText().toString())) {
            userNameLayout.setError(getResources().getString(R.string.userNameError));
        }
        if (DataValidator.isStringEmpty(passwordET.getText().toString())) {
            passwordLayout.setError(getResources().getString(R.string.passError));
        }

        if (!DataValidator.isStringEmpty(userNameET.getText().toString()) && !DataValidator.isStringEmpty(passwordET.getText().toString())) {
            //Implement Web Service (Not Simulation)
            dialog.setMessage(getResources().getString(R.string.loginMsg));
            dialog.show();
            goToLoginWebService(userNameET.getText().toString(), passwordET.getText().toString());
        }
    }



    @OnClick(R.id.forgetPass)
    public void forgetPassword() {
        Intent i = new Intent(Login.this, ForgetPassword.class);
        startActivity(i);
    }

    @OnClick(R.id.registerTV)
    public void register() {
        Intent i = new Intent(Login.this, Register.class);
        startActivity(i);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CommonTasks.setUpTranslucentStatusBar(this);
        CommonTasks.setUpPasswordETWithCustomFont(this, passwordET);
        CommonTasks.setUpTextInputLayoutErrors(userNameLayout, userNameET);
        CommonTasks.setUpTextInputLayoutErrors(passwordLayout, passwordET);
        dialog = DialogCreator.getInstance(this);

        userNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userNameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void goToLoginWebService(final String email, final String password) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.login(email, password);

        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                if (response.body().getStatus().equalsIgnoreCase("true")) {
                    Customer customer = response.body().getCstmr();
                    PrefManager.getInstance(Login.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(customer));
                    Log.d("TEST_NULL3", new Gson().toJson(customer));
                    Intent intent = new Intent(Login.this, Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                } else {
                    Toast.makeText(Login.this, getResources().getString(R.string.invalidLogin), Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(Login.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });

    }
}
