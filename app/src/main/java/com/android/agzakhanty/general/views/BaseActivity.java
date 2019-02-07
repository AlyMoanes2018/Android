package com.android.agzakhanty.general.views;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate;
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity implements OnLocaleChangedListener {
    private LocalizationActivityDelegate localizationDelegate = new LocalizationActivityDelegate(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        localizationDelegate.addOnLocaleChangedListener(this);
        localizationDelegate.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        localizationDelegate.onResume(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(localizationDelegate.attachBaseContext(newBase)));
    }

    @Override
    public Context getApplicationContext() {
        return localizationDelegate.getApplicationContext(super.getApplicationContext());
    }

    @Override
    public Resources getResources() {
        return localizationDelegate.getResources(super.getResources());
    }

    public final void setLanguage(String language) {
        localizationDelegate.setLanguage(this, language);
    }

    public final void setLanguage(Locale locale) {
        localizationDelegate.setLanguage(this, locale);
    }

    public final void setDefaultLanguage(String language) {
        localizationDelegate.setDefaultLanguage(language);
    }

    public final void setDefaultLanguage(Locale locale) {
        localizationDelegate.setDefaultLanguage(locale);
    }

    public final Locale getCurrentLanguage() {
        return localizationDelegate.getLanguage(this);
    }






    @Override
    public void onBeforeLocaleChanged() {

    }

    @Override
    public void onAfterLocaleChanged() {

    }
}
