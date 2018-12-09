package com.android.agzakhanty.general.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.push_notification.MyNotificationOpenedHandler;
import com.android.agzakhanty.general.push_notification.MyNotificationReceivedHandler;
import com.onesignal.OneSignal;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by a.moanes on 16/10/2017.
 */

public class Agzakhanty extends Application {
    public static SharedPreferences preferences;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Locale myLocale = new Locale("ar");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
        preferences = getSharedPreferences("APP_SHARED_DATA", MODE_PRIVATE);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler(this))
                .setNotificationReceivedHandler(new MyNotificationReceivedHandler(this))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
