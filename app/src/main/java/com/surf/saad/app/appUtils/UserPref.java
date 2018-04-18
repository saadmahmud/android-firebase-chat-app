package com.surf.saad.app.appUtils;

import android.content.Context;

import com.surf.saad.app.data.Preference;
import com.surf.saad.app.data.User;
import com.surf.saad.app.screens.RegistrationActivity;

public class UserPref {
    public static User getUser(Context context) {
        return new User(Preference.get(context, Preference.USER_NUMBER, ""),
                Preference.get(context, Preference.USER_NAME, ""), "");
    }

    public static boolean isThisUser(Context context, String userId) {
        return (getUser(context).getId().equals(userId));
    }

    public static boolean isLoggedIn(Context context) {
        return Preference.get(context, Preference.USER_NUMBER, "").length() > 0;
    }
}
