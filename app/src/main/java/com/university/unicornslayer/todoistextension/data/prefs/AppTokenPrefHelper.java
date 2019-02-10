package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

public class AppTokenPrefHelper implements TokenPrefHelper {
    private final String TOKEN_KEY = "token";
    private final SharedPreferences sharedPreferences;

    @Inject
    public AppTokenPrefHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    @Override
    public void setToken(String token) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply();
    }
}
