package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;

public class AppTokenPrefHelper implements TokenPrefHelper {
    private final String TOKEN_KEY = "token";

    private final Context context;

    public AppTokenPrefHelper(Context context) {
        this.context = context;
    }

    @Override
    public String getToken() {
        return AppSharedPrefs.getSharedPrefs(context).getString(TOKEN_KEY, null);
    }

    @Override
    public void setToken(String token) {
        AppSharedPrefs.getSharedPrefs(context).edit().putString(TOKEN_KEY, token).apply();
    }
}
