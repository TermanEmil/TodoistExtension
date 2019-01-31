package com.university.unicornslayer.todoistextension;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.university.unicornslayer.todoistextension.DataLayer.SharedPrefsUtils;

public class MainActivity extends AppCompatActivity {
    private SharedPrefsUtils mSharedPrefsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefsUtils = new SharedPrefsUtils(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSharedPrefsUtils.getToken() == null)
            gotoInputToken();
    }

    public void onChangeToken(View view) {
        gotoInputToken();
    }

    private void gotoInputToken() {
        Intent intent = new Intent(this, TokenInputActivity.class);
        startActivity(intent);
    }
}
