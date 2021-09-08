package com.app.swagse.sharedpreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.app.swagse.LoginActivity;
import com.app.swagse.constants.Constants;


public class PrefConnect {

    private static final String PREF_NAME = "SwageSe";
    private static final int MODE = Context.MODE_PRIVATE;

    public static final String IS_SORT = "IS_SORT";
    public static final String FCM_TOKEN = "FCM_TOKEN";

    public static void clearAllPrefs(Context context) {
        //FCM is rewrited after clearing prefs to make it unclear even after Shared prefference clearence it will be cleared only after app uninstall
        String FCM = PrefConnect.readString(context, PrefConnect.FCM_TOKEN, "");
        getEditor(context).clear().commit();
        PrefConnect.writeString(context, PrefConnect.FCM_TOKEN, FCM);
        Log.e("Giri ", "clearAllPrefs: " + PrefConnect.readString(context, PrefConnect.FCM_TOKEN, "null"));
    }

  /*  public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }*/

    public static void setFirstTimeLaunch(Context context, boolean isFirstTime) {
        getEditor(context).putBoolean(Constants.IS_FIRST_TIME_LAUNCH, isFirstTime).commit();
    }

    public static boolean isFirstTimeLaunch(Context context) {
        return getPreferences(context).getBoolean(Constants.IS_FIRST_TIME_LAUNCH, true);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).commit();
    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean readBoolean(Context context, String key,
                                      boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();

    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int readInteger(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeFloat(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).commit();
    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static float readFloat(Context context, String key, float defValue) {
        return getPreferences(context).getFloat(key, defValue);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).commit();
    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static long readLong(Context context, String key, long defValue) {
        return getPreferences(context).getLong(key, defValue);
    }

    /**
     * @param context
     * @return
     */
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    /**
     * @param context
     * @return
     */
    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

}
