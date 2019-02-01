package com.university.unicornslayer.todoistextension;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.university.unicornslayer.todoistextension.DataStuff.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.ReminderManager.ReminderManager;

public class MainActivity extends AppCompatActivity {
    private SharedPrefsUtils sharedPrefsUtils;
    private ReminderManager reminderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        reminderManager = new ReminderManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPrefsUtils.getToken() == null)
            gotoInputToken();
    }

    public void onChangeToken(View view) {
        gotoInputToken();
    }

    private void gotoInputToken() {
        Intent intent = new Intent(this, TokenInputActivity.class);
        startActivity(intent);
    }

    public void onCheckReminders(View view) {
        reminderManager.checkNotifications();
    }
}
