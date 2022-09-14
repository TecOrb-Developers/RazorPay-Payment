package com.razorpay.library.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private static PrefManager instance;
    private static final String PREF_NAME = "razorpayApp";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    int PRIVATE_MODE = 0;

    public PrefManager(Context context) {

        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public static PrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new PrefManager(context);
        }
        return instance;
    }

    public boolean getBooleanValue(String key) {
        return this.preferences.getBoolean(key, false);
    }

    public void setBooleanValue(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
    }

    public String getStringValue(String key) {
        return this.preferences.getString(key, "");
    }

    public void setStringValue(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }

    private int getIntValue(String key) {
        return this.preferences.getInt(key, 0);
    }

    private void setIntValue(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
    }

    private long getLongValue(String key) {
        return this.preferences.getLong(key, 0);
    }

    private void setLongValue(String key, long value) {
        this.editor.putLong(key, value);
        this.editor.commit();
    }

    public void setLatitudeValue(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }

    public String getLatitudeValue(String key) {
        return this.preferences.getString(key, null);
    }

    public void setLongitudeValue(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }

    public String getLongitudeValue(String key) {
        return this.preferences.getString(key, null);
    }

    public void clearPrefs() {
        this.editor.clear();
        this.editor.commit();
    }

    public void removeFromPreference(String key) {
        this.editor.remove(key);
        this.editor.commit();
    }
}
