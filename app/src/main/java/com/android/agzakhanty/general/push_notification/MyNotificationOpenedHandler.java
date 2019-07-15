package com.android.agzakhanty.general.push_notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Agzakhanty;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.views.Login;
import com.android.agzakhanty.sprints.two.views.AddAdvertismentRate;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.two.views.DummyActivityToStartAlarm;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Aly on 04/08/2018.
 */

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    Context context;

    public MyNotificationOpenedHandler(Context context) {
        this.context = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String orderId = "-1";
        String counter = "-1";
        String pcyID = "-1";

        if (data != null) {
            orderId = data.optString("OrderID", "-1");
            counter = data.optString("NoCstmrRepeat", "-1");
            pcyID = data.optString("PcyId", "-1");

            Log.d("TEST_NOTIFICATION", orderId + " " + counter + " " + pcyID);
        }
        //If we send notification with action buttons we need to specidy the button id's and retrieve it to
        //do the necessary operation.
        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
            //Yes action
            if (result.action.actionID.equals("id1")) {
                Log.d("TEST_ACTIONS", "ID1 Detected");
                Intent intent = new Intent(context, AddAdvertismentRate.class);
                intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "dash");
                intent.putExtra("orderNum", orderId);
                intent.putExtra("PcyId", pcyID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                /*((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                ((Activity) context).finish();*/
            }
            //Remind me after 2 mins
            else if (result.action.actionID.equals("id2")) {
                Log.d("TEST_ACTIONS", orderId + " " + counter);
                PrefManager.getInstance(Agzakhanty.getContext()).write(Constants.ORDER_ID, orderId);
                PrefManager.getInstance(Agzakhanty.getContext()).write(Constants.REMIND_ME_COUNTER, counter);
                Intent intent = new Intent(context, DummyActivityToStartAlarm.class);
                intent.putExtra("orderNum", orderId);
                intent.putExtra("counter", counter);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                /*((Activity)context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                ((Activity)context).finish();*/
            }
        }else {
             Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        //((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        //((Activity) context).finish();
        }
    }
}
