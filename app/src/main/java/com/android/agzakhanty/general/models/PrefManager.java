package com.android.agzakhanty.general.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Aly on 17/07/2017.
 */

public class PrefManager {

    private static PrefManager insatnce ;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private final String PREFERENCES_NAME = "APP_SHARED_DATA";
    private final String LANGUAGE_KEY = "lang";
    private final String USER_KEY = "user";
    private final String ACCESS_TOKEN_KEY = "token";
    private final String ACCESS_TOKEN_EXPIRY_DATE_KEY = "tokenExpiry";
    private final String USER_TYPE_KEY = "userType";
    private final String USER_NAME_KEY = "userName";
    private final String USER_PROFILE_PIC_KEY = "profilePic";




    private PrefManager(Context ctx){
        context = ctx;
        preferences = ctx.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static PrefManager getInstance(Context context)
    {
        insatnce = new PrefManager(context);
        return insatnce;
    }

    public void write(String text, String key){
        editor.putString(text, key);
        editor.apply();
    }

    public String read(String key){
        return preferences.getString(key, "");
    }

    public void write(int number, String key){
        editor.putInt(key, number);
        editor.apply();
    }

    public int readInt(String key){
        return preferences.getInt(key, 0);
    }


}
