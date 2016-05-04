package com.wetrain.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Map;

/**
 * Created by Administrator on 12/25/15.
 */
public class Utills {

    private static Context mContext;

    public Utills(Context context) {

        this.mContext = context;

    }



    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static void addEditScheduleDataInPref(Context context, String scheduleObjId, long schTime){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putLong(scheduleObjId, schTime).commit();

    }

    public static void removeScheduleDataInPref(Context context, String scheduleObjId){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().remove(scheduleObjId).commit();

    }

    public static boolean checkScheduleIsAlreadyNotified(Context context, String scheduleObjId){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> prefKeys =  pref.getAll();

        for(Map.Entry<String, ?> entry : prefKeys.entrySet()) {
            if (scheduleObjId != null) {
                if (scheduleObjId.equals(entry.getKey())) {
                    return false;
                }
            }
        }

        return true;
    }




    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }


    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Shows the soft keyboard
     */
    public static void showSoftKeyboard(View view) {
        if(view == null) return;
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);


    }

    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(View view) {
        if(view == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
