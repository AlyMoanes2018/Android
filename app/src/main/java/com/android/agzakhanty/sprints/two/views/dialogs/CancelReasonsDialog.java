package com.android.agzakhanty.sprints.two.views.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.two.adapters.CancelReasonsAdapter;
import com.android.agzakhanty.sprints.two.adapters.ItemsAdapter;
import com.android.agzakhanty.sprints.two.models.CancelReason;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.android.agzakhanty.sprints.two.views.AddOrderByItemsSelection;
import com.android.agzakhanty.sprints.two.views.ViewOrderDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by a.moanes on 05/07/2018.
 */

public class CancelReasonsDialog extends AlertDialog {


    private ProgressBar progressBar;
    private ListView results;
    private Context context;
    private PrefManager prefManager;
    private ArrayList<CancelReason> reasons;
    private CancelReason selectedReason;
    private String orderId;


    public CancelReasonsDialog(@NonNull Context context, String orderId) {
        super(context);
        this.context = context;
        this.orderId = orderId;
        prefManager = PrefManager.getInstance(this.context);
        reasons = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cancel_reasons);
        results = (ListView) findViewById(R.id.results);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        goToGetReasonsWS();


    }


    private void goToGetReasonsWS() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<CancelReason>> call = apiService.getOrderCanelReasons();
        call.enqueue(new Callback<ArrayList<CancelReason>>() {
            @Override
            public void onResponse(Call<ArrayList<CancelReason>> call, Response<ArrayList<CancelReason>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null && response.isSuccessful()) {
                    reasons = response.body();
                    Log.d("TEST_BUG_BUG", response.body().size() + " models");
                    CancelReasonsAdapter adapter = new CancelReasonsAdapter(reasons, context, CancelReasonsDialog.this);
                    results.setAdapter(adapter);
                    results.setVisibility(View.VISIBLE);


                } else {
                    Log.d("TEST_Full2", response.code() + "");
                    progressBar.setVisibility(View.GONE);
                    dismiss();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<CancelReason>> call, Throwable t) {
                Toast.makeText(context, context.getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                dismiss();
                Log.d("TEST_CERT", t.getMessage());

            }
        });
    }


    public void dismissCancelReasonDialog(CancelReason reason) {
        selectedReason = reason;
        if (reason != null) {
            Log.d("TEST_SELECTED_REASON", selectedReason.getDescAr());
            ((ViewOrderDetails) context).onCancelReasonSelected(selectedReason);
            dismiss();
        } else
            Toast.makeText(context, context.getString(R.string.noReasonSelected), Toast.LENGTH_LONG).show();
    }

}
