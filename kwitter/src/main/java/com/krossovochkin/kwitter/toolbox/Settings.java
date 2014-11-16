package com.krossovochkin.kwitter.toolbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 04.08.14.
 */
public class Settings {

    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String ACCESS_TOKEN_SECRET_KEY = "access_token_secret";
    public static final String AUTH_DATA_EXISTS = "auth_data_exists";

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void saveAuthData(Context context, String accessToken, String accessTokenSecret) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.putString(ACCESS_TOKEN_SECRET_KEY, accessTokenSecret);
        editor.putBoolean(AUTH_DATA_EXISTS, true);
        editor.apply();
    }

}
