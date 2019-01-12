package com.uhf.uhf.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Description:
 * Data: 2018/6/28
 *
 * @author: cqian
 */

public class SharedPreferencesUtils {
    private static SharedPreferences preferences;
    public static String GET_PAN_DATA="GET_PAN_DATA";


    public static void getPreference(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences("PanDianData", Context.MODE_PRIVATE);
        }
    }

    /**
     * @param context 上下文 最好用Application的context
     * @param key     属性名
     * @param value   属性值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        getPreference(context);
        preferences.edit().putBoolean(key, value).commit();
    }

    /**
     * @param context      上下文 最好用Application的context
     * @param key          属性名
     * @param defaultValue 属性值 默认
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        getPreference(context);
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * @param context 上下文 最好用Application的context
     * @param key     属性名
     * @param value   属性值
     */
    public static void putFloat(Context context, String key, float value) {
        getPreference(context);
        preferences.edit().putFloat(key, value).commit();
    }

    /**
     * @param context      上下文 最好用Application的context
     * @param key          属性名
     * @param defaultValue 属性值 默认
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        getPreference(context);
        return preferences.getFloat(key, defaultValue);
    }

    /**
     * @param context 上下文 最好用Application的context
     * @param key     属性名
     * @param value   属性值
     */
    public static void putString(Context context, String key, String value) {
        getPreference(context);
        preferences.edit().putString(key, value).commit();
    }

    /**
     * @param context 上下文 最好用Application的context
     * @param key     属性名
     * @param value   属性值
     */
    public static boolean putStringWithResult(Context context, String key, String value) {
        getPreference(context);
        return preferences.edit().putString(key, value).commit();
    }

    /**
     * @param context      上下文 最好用Application的context
     * @param key          属性名
     * @param defaultValue 属性值 默认
     */
    public static String getString(Context context, String key, String defaultValue) {
        getPreference(context);
        return preferences.getString(key, defaultValue);
    }

    /**
     * @param context 上下文 最好用Application的context
     * @param key     属性名
     * @param value   属性值
     */
    public static void putLong(Context context, String key, long value) {
        getPreference(context);
        preferences.edit().putLong(key, value).commit();
    }

    /**
     * @param context      上下文 最好用Application的context
     * @param key          属性名
     * @param defaultValue 属性值 默认
     */
    public static long getLong(Context context, String key, long defaultValue) {
        getPreference(context);
        return preferences.getLong(key, defaultValue);
    }

    /**
     * @param context 上下文 最好用Application的context
     * @param key     属性名
     * @param value   属性值
     */
    public static void putInt(Context context, String key, int value) {
        getPreference(context);
        preferences.edit().putInt(key, value).commit();
    }

    /**
     * @param context      上下文 最好用Application的context
     * @param key          属性名
     * @param defaultValue 属性值 默认
     */
    public static int getInt(Context context, String key, int defaultValue) {
        getPreference(context);
        return preferences.getInt(key, defaultValue);
    }

    /**
     * @param context 上下文 最好用Application的context
     * @param key     属性名
     */
    public static void remove(Context context, String key) {
        getPreference(context);
        preferences.edit().remove(key).commit();
    }


}
