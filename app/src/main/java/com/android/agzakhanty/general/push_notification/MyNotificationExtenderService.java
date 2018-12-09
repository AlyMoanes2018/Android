package com.android.agzakhanty.general.push_notification;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Agzakhanty;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.two.views.AddAdvertismentRate;
import com.android.agzakhanty.sprints.two.views.DummyActivityToStartAlarm;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by Aly on 04/08/2018.
 */

// This calss is to handle notifications while app is in background
public class MyNotificationExtenderService extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        Log.d("TEST_DATA", notification.payload.body);
        PrefManager.getInstance(Agzakhanty.getContext()).write(Constants.NOTIFICATION_TEXT_REDIRECT, notification.payload.body);
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setVibrate(new long[]{200, 200, 200, 200, 200})
                        .setLights(Color.GREEN, 1000, 1000);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.setSmallIcon(R.drawable.ic_launcher_transparent);
                } else {
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                }
                return builder;
            }
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);

        return false;
    }
}
