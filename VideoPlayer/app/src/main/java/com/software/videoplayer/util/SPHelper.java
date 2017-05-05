package com.software.videoplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by moon on 2017/2/16.
 */

public class SPHelper {

    public static void putBooleanMessage(Context context, String kinds,boolean isShow) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(kinds, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(kinds, isShow);
        editor.apply();
    }
    public static boolean getBooleanMessage(Context context, String kinds) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(kinds, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(kinds,false);
    }
    public static void putStringMessage(Context context, String kinds,String message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(kinds, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kinds, message);
        editor.apply();
    }
    public static String getStringnMessage(Context context, String kinds) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(kinds, Context.MODE_PRIVATE);
        return sharedPreferences.getString(kinds,"");
    }

}
