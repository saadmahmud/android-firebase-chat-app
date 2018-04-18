package com.surf.saad.app.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {
    public static String USER_NUMBER = "USER_NUMBER";
    public static String USER_NAME = "USER_NAME";

    private static SharedPreferences Manager(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void save(Context context, String key, String value) {
        SharedPreferences.Editor editor = Manager(context).edit();
        editor.putString(key, value);

        editor.commit();
    }

    public static String get(Context context, String key, String defaultValue) {
        return Manager(context).getString(key, defaultValue);
    }

    public static boolean clear(Context context) {
        return Manager(context).edit().clear().commit();
    }
}
