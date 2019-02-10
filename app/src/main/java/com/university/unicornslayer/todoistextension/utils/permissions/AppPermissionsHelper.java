package com.university.unicornslayer.todoistextension.utils.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;

public class AppPermissionsHelper implements PermissionsHelper {
    private final Context context;
    private final Activity activity;
    private OnGetPermissionDoneListener resultListener;

    @Inject
    public AppPermissionsHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void setHandler(OnGetPermissionDoneListener listener) {
        this.resultListener = listener;
    }

    @Override
    public boolean hasPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestPermission(String permission) {
        String[] permissions = { permission };

        int requestNb = permission.hashCode();
        ActivityCompat.requestPermissions(activity, permissions, requestNb);
    }

    @Override
    public void onGetPermissionDone(boolean granted) {
        if (resultListener != null)
            resultListener.onDone(granted);
    }
}
