package com.app.swagse.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.app.swagse.R;
import com.app.swagse.firebase.MyFirebaseInstanceIDService;
import com.google.firebase.iid.FirebaseInstanceId;


public class AppUtil {


    private static final String TAG = AppUtil.class.getSimpleName();

    public static String getApplicationVersionName(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Exception while fetching version name");
            Log.d(TAG, e.toString());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return context.getString(R.string.not_available);
    }

    /**
     * Retrieve Application Version code for sending in the header of api calls
     *
     * @param context
     * @return App Code
     */
    public static int getApplicationVersionCode(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Exception while fetching version name");
            Log.d(TAG, e.toString());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return -1;
    }

    public static String getDeviceId(Context context) {
        String android_id;
        try {
            android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return android_id;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return null;
    }

    public static String getFCMToken(Context context) {
        String fcmToken;
        try {
            fcmToken = FirebaseInstanceId.getInstance().getToken();
            return fcmToken;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return null;
    }
}
