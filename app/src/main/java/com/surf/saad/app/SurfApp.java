package com.surf.saad.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.surf.saad.app.data.Preference;
import com.surf.saad.app.screens.RegistrationActivity;

public class SurfApp extends Application {
    private static SurfApp surfApplication;

    public static SurfApp getInstance() {
        if (surfApplication == null)
            surfApplication = new SurfApp();

        return surfApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showLog("App Crated");
    }

    public void logout(Context context) {
        Preference.clear(context);
    }

    public void launchRegActivity(Context context) {
        Intent intent = new Intent(context, RegistrationActivity.class);
        context.startActivity(intent);
    }

    private void showLog(String s) {
        Log.d(this.getClass().getName(), s);

    }
}
