package com.example.quiz.authSharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.quiz.utilities.Constants;

public class AuthPreferences {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    public AuthPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_AUTH, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void clearAuthPreferences() {
        editor.clear();
        editor.apply();
    }
 }
