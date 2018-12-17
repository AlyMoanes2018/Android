package com.android.agzakhanty.sprints.two.broadcast_recievers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.two.views.AddAdvertismentRate;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.two.views.DummyActivityToStartAlarm;
import com.android.agzakhanty.sprints.two.views.dialogs.CancelReasonsDialog;

/**
 * Created by Aly on 30/07/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        String orderNumber = arg1.getStringExtra("orderNum");
        Log.d("TEST_NUM_ALARM", orderNumber);
        showNotification(arg0, orderNumber);
        PrefManager.getInstance(arg0).write(Constants.SCHEDULED_ALARM_ORDER_ID, "");

    }

    private void showNotification(Context context, String num) {
        Log.d("TEST_NOTI", "FROM FIREBASE");
        Intent snoozeIntent = new Intent(context, DummyActivityToStartAlarm.class);
        Intent deliveryIntent = new Intent(context, AddAdvertismentRate.class);
        deliveryIntent.putExtra("orderNum", num);
        snoozeIntent.putExtra("orderNum", num);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, Dashboard.class), 0);
        PendingIntent deliveryPendingIntent = PendingIntent.getActivity(context, 0, deliveryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent snoozePendingIntent = PendingIntent.getActivity(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(context.getResources().getString(R.string.cancelOrderDialogTitle) + " " + num)
                        .setContentText(context.getResources().getString(R.string.deliverdYet))
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(false)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .addAction(R.drawable.ic_alarm_add, context.getResources().getString(R.string.remind),
                                snoozePendingIntent)
                        .addAction(R.drawable.ic_add_shopping_cart, context.getResources().getString(R.string.yes),
                                deliveryPendingIntent)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setLights(Color.GREEN, 3000, 3000);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.ic_launcher_transparent);
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }
        NotificationManagerCompat mNotificationManager =
                NotificationManagerCompat.from(context);
        mNotificationManager.notify(1, mBuilder.build());

    }
}
