package com.university.unicornslayer.todoistextension.ui.settings.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.university.unicornslayer.todoistextension.ui.token_input.TokenInputActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TokenPreferenceFragment extends BaseSettingsFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getContext(), TokenInputActivity.class);
        startActivity(intent);
    }
}
