package com.university.unicornslayer.todoistextension.ui.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.di.component.AppComponent;
import com.university.unicornslayer.todoistextension.di.component.DaggerAppComponent;
import com.university.unicornslayer.todoistextension.di.module.AppModule;

public abstract class BaseActivity extends AppCompatActivity {
    private AppComponent appComponent;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appComponent = DaggerAppComponent.builder()
            .appModule(new AppModule(this, this))
            .build();
    }

    public AppComponent getDagger() {
        return appComponent;
    }

    protected void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void showMsg(int rid) {
        showMsg(getString(rid));
    }

    protected void showProgressDialog(String title, String msg, ProgressDialog.OnCancelListener listener) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }

        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);

        if (listener != null) {
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setOnCancelListener(listener);
        } else {
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();
    }

    protected void showYesNoDialog(String msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setMessage(msg)
            .setPositiveButton("Yes", listener)
            .setNegativeButton("No", listener);

        builder.show();
    }
}
