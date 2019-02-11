package com.university.unicornslayer.todoistextension.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.ui.base.BaseActivity;
import com.university.unicornslayer.todoistextension.ui.settings.SettingsActivity;
import com.university.unicornslayer.todoistextension.ui.token_input.TokenInputActivity;
import com.university.unicornslayer.todoistextension.utils.app_updates.AppUpdater;
import com.university.unicornslayer.todoistextension.utils.app_updates.AppUpdatesView;

public class MainActivity extends BaseActivity implements MainMvpView, AppUpdatesView {
    private PermissionListener permissionListener;

    private MainPresenter presenter;
    private AppUpdater appUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDagger().inject(this);

        presenter = getDagger().getMainPresenter();
        presenter.setView(this);
        presenter.onCreate();

        appUpdater = getDagger().getAppUpdater();
        appUpdater.setView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appUpdater.onDestroy();
    }

    public void onCheckForUpdates(View view) {
        appUpdater.checkForUpdates();
    }

    public void onClickSettings(View view) {
        presenter.onClickSettings();
    }

    @Override
    public void gotoInputTokenView() {
        Intent intent = new Intent(this, TokenInputActivity.class);
        startActivity(intent);
    }

    @Override
    public void showFailedToPostNotifications() {
        showMsg(R.string.error_failed_to_post_notifs);
    }

    @Override
    public void disableInput() {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void enableInput() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void showDownloading() {
        showProgressDialog(
            getString(R.string.title_progress_dialog_downloading),
            getString(R.string.msg_progress_dialog_please_wait),
            null);
    }

    @Override
    public void showDownloadCancelled() {
        showMsg(R.string.msg_download_canceled);
    }

    @Override
    public void dismissDownloading() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void showFailedToInstallError() {
        showMsg(R.string.error_failed_to_install);
    }

    @Override
    public void showCheckingForUpdates() {
        showProgressDialog(
            getString(R.string.title_progress_dialog_checking_for_updates),
            getString(R.string.msg_progress_dialog_please_wait),
            dialog -> appUpdater.cancelCheckingForUpdates());
    }

    @Override
    public void dismissCheckingForUpdates() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void showServiceUnavailableError() {
        showMsg(R.string.error_service_unavailable);
    }

    @Override
    public void showServerNotFoundError() {
        showMsg(R.string.error_server_not_found);
    }

    @Override
    public void showFailedToCheckForUpdatesError() {
        showMsg(R.string.error_failed_to_check_for_updates);
    }

    @Override
    public void showServerProvidedInvalidInfoError() {
        showMsg(R.string.error_server_provided_invalid_info);
    }

    @Override
    public void showNoUpdatesAvailable() {
        showMsg(R.string.msg_no_updates_available);
    }

    @Override
    public void askForDownloadPermission(PermissionListener listener) {
        showYesNoDialog(getString(R.string.alert_dialog_title_download_new_version),
            (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE)
                    listener.onGranted();
                else
                    listener.onDenied();
            });
    }

    @Override
    public void askForWriteToExternalPermission(PermissionListener writePermissionListener) {
        permissionListener = writePermissionListener;
        requestPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            getResources().getInteger(R.integer.writeExternlPermissionRequestNb));
    }

    @Override
    public void showFailedToGetItems() {
        Toast.makeText(this, getString(R.string.error_failed_to_get_items), Toast.LENGTH_LONG).show();
    }

    @Override
    public void gotoSettingsView() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onCheckRemindersBtnPressed(View view) {
        presenter.onCheckRemindersBtnPressed();
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        @NonNull String[] permissions,
        @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == getResources().getInteger(R.integer.writeExternlPermissionRequestNb)) {
            if (permissionListener == null)
                return;

            boolean granted =
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (granted)
                permissionListener.onGranted();
            else
                permissionListener.onDenied();
        }
    }

    private void requestPermission(String permission, int requestNb) {
        String[] permissions = { permission };
        ActivityCompat.requestPermissions(this, permissions, requestNb);
    }
}
