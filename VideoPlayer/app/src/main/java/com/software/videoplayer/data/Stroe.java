package com.software.videoplayer.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * User: Moon
 * Data: 2017/3/8.
 */

public class Stroe {

    public static void storeData(Context context, String TableName, List<?> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TableName, Context.MODE_PRIVATE);
        String json = JSON.toJSONString(list);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TableName, json);
        editor.apply();
    }

    public static List<?> getData(Context context, String TableName,Class<?> object) {

        String json = context.getSharedPreferences(TableName,Context.MODE_PRIVATE).getString(TableName, "");
        if (JSON.parseArray(json, object) != null) {
            return JSON.parseArray(json,object);
        }
        return null;
    }



}
