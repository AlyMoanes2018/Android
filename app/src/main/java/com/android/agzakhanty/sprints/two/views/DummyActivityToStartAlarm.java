package com.android.agzakhanty.sprints.two.views;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.Agzakhanty;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.two.broadcast_recievers.AlarmReceiver;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DummyActivityToStartAlarm extends AppCompatActivity {

    ProgressDialog dialog;
    String orderId, repeatCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TEST_DATA", PrefManager.getInstance(Agzakhanty.getContext()).read(Constants.NOTIFICATION_TEXT_REDIRECT));
        dialog = DialogCreator.getInstance(this);
        orderId = getIntent().getStringExtra("orderNum");
        repeatCounter = getIntent().getStringExtra("counter");
        if (!repeatCounter.equals("-1") && !orderId.equals("-1")) {
            dialog.setMessage(getResources().getString(R.string.loadingOrder));
            dialog.show();
            goTonNotifyAfterWS();
        } else Log.d("TEST_NOTI_DATA", repeatCounter + " " + orderId);
    }

    private void goTonNotifyAfterWS() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Boolean> call = apiService.notifyAfter(PrefManager.getInstance(Agzakhanty.getContext()).read(Constants.REGISTRATION_ID),
                PrefManager.getInstance(Agzakhanty.getContext()).read(Constants.NOTIFICATION_TEXT_REDIRECT),
                orderId,
                repeatCounter,
                Constants.DEFAULT_ORDER_REMINDER_TIME
        );
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body()) {
                        Toast.makeText(DummyActivityToStartAlarm.this, getResources().getString(R.string.TwoMins), Toast.LENGTH_LONG).show();

                    } else
                        Toast.makeText(DummyActivityToStartAlarm.this, getResources().getString(R.string.updateOrderFailure), Toast.LENGTH_LONG).show();
                } else {
                    Log.d("TEST_NULL", response.code() + "");
                    Toast.makeText(DummyActivityToStartAlarm.this, getResources().getString(R.string.updateOrderFailure), Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(Agzakhanty.getContext(), Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                finish();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(DummyActivityToStartAlarm.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Agzakhanty.getContext(), Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                finish();
            }
        });
    }
}
