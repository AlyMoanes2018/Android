package com.android.agzakhanty.sprints.one.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.android.agzakhanty.sprints.one.models.TempRegisterData;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rilixtech.CountryCodePicker;

import java.lang.reflect.Type;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Register extends AppCompatActivity {

    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.phoneET2)
    AppCompatEditText edtPhoneNumber;
    @BindView(R.id.phoneWrapper)
    TextInputLayout phoneLayout;
    @BindView(R.id.userNameWrapper)
    TextInputLayout usernameLayout;
    @BindView(R.id.userNameET)
    EditText usernameET;
    @BindView(R.id.passwrdWrapper)
    TextInputLayout passwordLayout;
    @BindView(R.id.passwordET)
    EditText passwordET;
    @BindView(R.id.emailWrapper)
    TextInputLayout emailLayout;
    @BindView(R.id.emailET)
    EditText emailET;
    @BindView(R.id.toolbar)
    Toolbar appBar;
    EditText DOBET;
    @BindView(R.id.DOBWrapper)
    TextInputLayout dobLayout;
    @BindView(R.id.radioMale)
    RadioButton maleRB;
    @BindView(R.id.radioFemale)
    RadioButton femaleRB;
    @BindView(R.id.password)
    TextView passwordTV;
    int validationPassedRulesCount;
    ProgressDialog dialog;
    Customer cstmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        dialog = DialogCreator.getInstance(this);
        validationPassedRulesCount = 0;
        DOBET = (EditText) findViewById(R.id.DOBET);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ccp.registerPhoneNumberTextView(edtPhoneNumber);
        edtPhoneNumber.setText("");
        edtPhoneNumber.setHint("");
        CommonTasks.setUpTranslucentStatusBar(this);
        Log.d("TEST_CCP", ccp.getSelectedCountryCodeWithPlus());
        String cstmrJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_TEMP_CUSTOMER_KEY);
        Log.d("TEST_NULL", cstmrJSON + "     E");
        if (!cstmrJSON.isEmpty()) {
            Type type = new TypeToken<Customer>() {
            }.getType();
            cstmr = new Gson().fromJson(cstmrJSON, type);

            if (cstmr.getName() != null)
                usernameET.setText(cstmr.getName());

            if (cstmr.getE_Mail() != null)
                emailET.setText(cstmr.getE_Mail());

            passwordET.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.GONE);
            passwordTV.setVisibility(View.GONE);
            passwordET.setText("1234567");
            cstmr.setPwd(passwordET.getText().toString());

            if (cstmr.getDateOfBirth() != null)
                DOBET.setText(cstmr.getDateOfBirth());

            if (cstmr.getGender() != null) {
                Log.d("TEST_NULL", cstmr.getGender());
                if (cstmr.getGender().equalsIgnoreCase("male")) {
                    cstmr.setGender("M");
                    maleRB.setSelected(true);
                } else {
                    femaleRB.setSelected(true);
                    cstmr.setGender("F");
                }
            }
            PrefManager.getInstance(this).write(Constants.SP_LOGIN_TEMP_CUSTOMER_KEY, "");
        }
        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameLayout.setError(null);
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

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        DOBET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dobLayout.setError(null);
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

    @OnClick(R.id.registerNextButton)
    public void finishRegistration() {
        // Registration process
        if (cstmr == null)
            cstmr = new Customer();

        if (DataValidator.isStringEmpty(usernameET.getText().toString())) {
            usernameLayout.setError(getResources().getString(R.string.userNameError));
        } else {
            validationPassedRulesCount++;
            cstmr.setName(usernameET.getText().toString());
        }
        if (DataValidator.isStringEmpty(passwordET.getText().toString())) {
            passwordLayout.setError(getResources().getString(R.string.passError));
        } else {
            validationPassedRulesCount++;
        }

        if (DataValidator.isStringEmpty(emailET.getText().toString())) {
            emailLayout.setError(getResources().getString(R.string.emailError));
        } else {
            validationPassedRulesCount++;
        }

        if (DataValidator.isStringEmpty(edtPhoneNumber.getText().toString())) {
            phoneLayout.setError(getResources().getString(R.string.phoneError));
        } else {
            validationPassedRulesCount++;

            cstmr.setMobile(ccp.getSelectedCountryCodeWithPlus() + edtPhoneNumber.getText().toString());
        }

        if (DataValidator.isStringEmpty(DOBET.getText().toString())) {
            dobLayout.setError(getResources().getString(R.string.dobError));
        } else {
            validationPassedRulesCount++;
            cstmr.setDateOfBirth(DOBET.getText().toString());
        }

        if (!DataValidator.isPasswordNotLessThan(passwordET.getText().toString(), 6)) {
            passwordLayout.setError(getResources().getString(R.string.passLenghtError));
        } else {
            validationPassedRulesCount++;
        }

        if (!DataValidator.validateEmail(emailET.getText().toString())) {
            emailLayout.setError(getResources().getString(R.string.validEmailError));
        } else {
            validationPassedRulesCount++;
            cstmr.setE_Mail(emailET.getText().toString());
        }

        if (!ccp.isValid())
            phoneLayout.setError(getResources().getString(R.string.validPhoneError));
        else {
            validationPassedRulesCount++;
        }
        if (maleRB.isChecked()) cstmr.setGender("M");
        else cstmr.setGender("F");
        cstmr.setRegId(PrefManager.getInstance(Register.this).read(Constants.REGISTRATION_ID));
        cstmr.setFileName();
        if (validationPassedRulesCount == 8) {
            if (PrefManager.getInstance(Register.this).read(Constants.SP_LOGIN_TEMP_CUSTOMER_KEY).isEmpty())
                cstmr.setPwd(passwordET.getText().toString());
            dialog.setMessage(getResources().getString(R.string.registerMsg));
            dialog.show();
            validationPassedRulesCount = 0;
            goToRegisterWS(cstmr);


        } else {
            Log.d("TEST_NULL", "NOOOOOOOO" + validationPassedRulesCount);
            validationPassedRulesCount = 0;
        }


    }

    @OnClick(R.id.DOBET)
    public void onDOBETClicked() {
        new DatePickerDialog(Register.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //DO SOMETHING
                populateSetDate(year, monthOfYear + 1, dayOfMonth);
            }
        }, 1989, 12, 1).show();
    }

    public void populateSetDate(int year, int month, int day) {

        DOBET.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));

    }

    @OnClick(R.id.terms)
    public void onTermsLabelClicked() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getResources().getString(R.string.termsText));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        /*int sex = maleRB.isChecked()?0:1;
        TempRegisterData trd = new TempRegisterData(usernameET.getText().toString(),
                emailET.getText().toString(),
                passwordET.getText().toString(),
                edtPhoneNumber.getText().toString(),
                DOBET.getText().toString(),
                sex);
        PrefManager.getInstance(this).write("trd", new Gson().toJson(trd));
        Intent i = new Intent(Register.this, TermsAndConditions.class);
        startActivity(i);*/
    }

    public void goToRegisterWS(Customer cstmr) {
        Log.d("TEST_NULL", new Gson().toJson(cstmr));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.register(cstmr);
        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        PrefManager.getInstance(Register.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(response.body().getCstmr()));
                        Log.d("TEST_NULL2", new Gson().toJson(response.body().getCstmr()));
                        Intent intent = new Intent(Register.this, ProfilePhotoSetter.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Register.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(Register.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
//                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }
}
