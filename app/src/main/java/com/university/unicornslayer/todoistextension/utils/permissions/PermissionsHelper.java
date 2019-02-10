package com.university.unicornslayer.todoistextension.utils.permissions;

public interface PermissionsHelper {
    void setHandler(OnGetPermissionDoneListener listener);

    boolean hasPermission(String permission);

    void requestPermission(String permission);

    void onGetPermissionDone(boolean granted);

    interface OnGetPermissionDoneListener {
        void onDone(boolean permissionGranted);
    }
}
