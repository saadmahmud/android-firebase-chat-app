package com.surf.saad.app.appUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.surf.saad.app.data.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AppUtils {

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        }
        return manufacturer.toUpperCase() + " " + model;
    }

    public static String getTimeFromTimestamp(String lastMessageTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("h:" +
                    "mma");
            return formatter.format(new Date(Long.parseLong(lastMessageTime)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFileNameAndExtention(String uploadFilePath) {
        return "jpeg";
    }

    public static void openFileSelectPage(Activity activity, String title, int
            requestCode) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        activity.startActivityForResult(Intent.createChooser(intent, title), requestCode);
    }

    public static String getMimeType(Uri uri, Context context) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String getImagePath(Uri uri, Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = context.getContentResolver().query(
                android.provider.MediaStore.Images.Media
                        .EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    public static boolean hasValue(String value) {
        return value != null && value.length() > 0;
    }

    public static boolean isImageUrl(String url) {
        return url.contains(".jpeg") || url.contains(".jpg") || url.contains
                (".png");
    }

    public static boolean thisUser(Context mContext, User user) {
        return UserPref.getUser(mContext).getId().equals(user.getId());
    }

    public static String getChatRoomName(Map<String, User> userList, Context context) {
        String name = "";

        Object[] keys = userList.keySet().toArray();

        for (int i = 0; i < keys.length; i++) {
            String userName = userList.get(keys[i]).getName();

            if (name.length() > 0) {
                name += ", ";
            }

            if (userName.equals(UserPref.getUser(context).getName())) {
            } else {
                name += userName;
            }
        }
        Log.d("BJIT", "getChatRoomName " + name);
        return name;
    }
}