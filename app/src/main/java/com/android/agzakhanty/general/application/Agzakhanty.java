package com.android.agzakhanty.general.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate;
import com.android.agzakhanty.R;
import com.android.agzakhanty.general.push_notification.MyNotificationOpenedHandler;
import com.android.agzakhanty.general.push_notification.MyNotificationReceivedHandler;
import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.onesignal.OneSignal;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by a.moanes on 16/10/2017.
 */

public  class  Agzakhanty extends Application {
    public static SharedPreferences preferences;
    private static Context context;
    public static LocalizationApplicationDelegate localizationDelegate;

    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        localizationDelegate = new LocalizationApplicationDelegate(this);
        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                // Glide.with(imageView.getContext()).clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
       /* Locale myLocale = new Locale("ar");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);*/
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
        if (localizationDelegate == null)
            localizationDelegate = new LocalizationApplicationDelegate(this);
        super.attachBaseContext(localizationDelegate.attachBaseContext(base));
        MultiDex.install(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localizationDelegate.onConfigurationChanged(this);
    }

    @Override
    public Context getApplicationContext() {
        return localizationDelegate.getApplicationContext(super.getApplicationContext());
    }
}
