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
import com.android.agzakhanty.sprints.three.adapters.ConsultationsAdapter;
import com.android.agzakhanty.sprints.three.models.Consultation;
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

public class MyConsultations extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.consultaionsList)
    ListView consultaionsList;
    @BindView(R.id.noConsultations)
    TextView noConsultations;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    ArrayList<Consultation> consultations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_consultations);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        CommonTasks.addLeftMenu(this, toolbar);
        goToGetConsultationsWS();
    }

    private void goToGetConsultationsWS() {
        Log.d("TEST_WS", "WEB SERVICE");
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Consultation>> call = apiService.getAllCustomerConsultations(customer.getId());
        call.enqueue(new Callback<ArrayList<Consultation>>() {
            @Override
            public void onResponse(Call<ArrayList<Consultation>> call, Response<ArrayList<Consultation>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    consultations = response.body();
                    ConsultationsAdapter adapter = new ConsultationsAdapter(consultations, MyConsultations.this);
                    consultaionsList.setAdapter(adapter);
                    consultaionsList.setVisibility(View.VISIBLE);
                    noConsultations.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                } else {
                    consultaionsList.setVisibility(View.GONE);
                    noConsultations.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Consultation>> call, Throwable t) {
                Toast.makeText(MyConsultations.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
//                Log.d("TEST_CERT", t.getMessage());
                consultaionsList.setVisibility(View.GONE);
                noConsultations.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
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
        Intent intent = new Intent(MyConsultations.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
