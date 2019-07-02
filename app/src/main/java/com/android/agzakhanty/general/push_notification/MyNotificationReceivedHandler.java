package com.android.agzakhanty.general.push_notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Agzakhanty;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.two.views.AddAdvertismentRate;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by Aly on 04/08/2018.
 */
//This will be called when a notification is received while your app is running.
public class MyNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {

    Context context;

    public MyNotificationReceivedHandler(Context context) {
        this.context = context;
    }

    @Override
    public void notificationReceived(OSNotification notification) {
        PrefManager.getInstance(Agzakhanty.getContext()).write(Constants.NOTIFICATION_TEXT_REDIRECT, notification.payload.body);
        Log.d("TEST_SIGNAL", "NOTIFICATION RECEIVED");

        Log.d("TEST_UPDATE2", "Called from Foreground notification");
        //((Dashboard) context).xyz();
       /* Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        ((Activity) context).finish();*/

    }
}

