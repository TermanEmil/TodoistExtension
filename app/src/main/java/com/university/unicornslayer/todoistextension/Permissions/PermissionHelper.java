package com.university.unicornslayer.todoistextension.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.Requests.IObjectHandler;

public class PermissionHelper extends ContextWrapper {
    private final Activity activity;
    private IObjectHandler resultHandler;

    public PermissionHelper(Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    public void setWriteExternalStoragePermissionHandler(IObjectHandler handler) {
        this.resultHandler = handler;
    }

    public boolean hasWriteExternalStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestWriteExternalStoragePermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        final int requestNb = getResources().getInteger(R.integer.writeExternlPermissionRequestNb);
        ActivityCompat.requestPermissions(activity, permissions, requestNb);
    }

    public void onGetWriteExternalStoragePermissionDone(boolean granted) {
        if (resultHandler != null)
            resultHandler.onDone(granted);
    }
}
