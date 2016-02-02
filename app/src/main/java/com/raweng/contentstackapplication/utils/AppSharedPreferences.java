package com.raweng.contentstackapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/***
 * Created by built.io.
 */
public class AppSharedPreferences {

    public static void setLocale(Context context, String locale) {
        SharedPreferences prefs = context.getSharedPreferences("NEWS_APP", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("locale", locale);
        editor.apply();
    }

    public static String getLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("NEWS_APP", 0);
        return prefs.getString("locale", null);
    }

}
