package com.university.unicornslayer.todoistextension.utils.app_updates.github_updater;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.university.unicornslayer.todoistextension.BuildConfig;
import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.utils.app_updates.AppUpdater;
import com.university.unicornslayer.todoistextension.utils.app_updates.AppUpdatesView;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import okhttp3.Response;

// Looks on the latest release on the github to download the apk
public class GithubUpdateManager extends ContextWrapper implements AppUpdater {
    private static final String NETWORK_TAG_CHECK_UPDATES = "github-update-manager-check-for-updates";

    private final DownloadManager downloadManager;

    private long downloadEnqueueNb;
    private AppUpdatesView view;

    private BroadcastReceiver onCompleteReciver;

    @Inject
    public GithubUpdateManager(Context context, DownloadManager downloadManager) {
        super(context);
        this.downloadManager = downloadManager;
    }

    @Override
    public void setView(AppUpdatesView view) {
        this.view = view;
    }

    @Override
    public void checkForUpdates() {
        view.disableInput();
        view.showCheckingForUpdates();
        AndroidNetworking
            .get(getString(R.string.update_url))
            .setTag(NETWORK_TAG_CHECK_UPDATES)
            .build()
            .getAsOkHttpResponse(new CheckForUpdatesListener());
    }

    @Override
    public void onDestroy() {
        view = null;

        if (onCompleteReciver != null) {
            try {
                unregisterReceiver(onCompleteReciver);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    @Override
    public void cancelCheckingForUpdates() {
        AndroidNetworking.cancel(NETWORK_TAG_CHECK_UPDATES);
    }

    private void onGetDoneSuccess(Response response) {
        String url = response.request().url().toString();
        Matcher m = Pattern
            .compile(getString(R.string.github_app_updater_link_version_pattern))
            .matcher(url);

        // The pattern must find 2 groups only
        if (!m.find() || m.groupCount() != 2) {
            view.showServerProvidedInvalidInfoError();
            return;
        }

        Version webVersion = extractVersion(m);
        if (!versionIsBiggerThanCurrent(webVersion)) {
            view.showNoUpdatesAvailable();
            return;
        }

        view.askForDownloadPermission(new DownloadPermissionListener(webVersion));
    }

    private Version extractVersion(Matcher m) {
        return new Version(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
    }

    private String formatLinkToApk(Version version) {
        return String.format(
            getString(R.string.github_appupdate_link_to_apk_format),
            version.part1,
            version.part2);
    }

    private void onDownloadPermissionGranted(final Version version) {
        if (hasWriteToExternalPermission()) {
            onWriteToExternalPermissionAccepted(version);
            return;
        }

        view.askForWriteToExternalPermission(new WritePermissionListener(version));
    }

    private boolean hasWriteToExternalPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("DefaultLocale")
    private static String formatFileName(Version version) {
        return String.format("TodoistExtensionV.%d.%d.apk", version.part1, version.part2);
    }

    private void onWriteToExternalPermissionAccepted(Version version) {
        view.showDownloading();
        view.enableInput();

        final String filename = formatFileName(version);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(formatLinkToApk(version)))
            .setTitle(filename)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setVisibleInDownloadsUi(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        request.allowScanningByMediaScanner();

        onCompleteReciver = new DownloadFinishedBroadcast();
        registerReceiver(onCompleteReciver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadEnqueueNb = downloadManager.enqueue(request);
    }

    private void onDownloadBroadcastReceived(Context context) {
        if (view == null) {
            // It was cancelled.
            return;
        }
        view.dismissDownloading();

        File toInstall = getFileToInstall();
        if (toInstall == null) {
            view.showFailedToInstallError();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            openForInstall_ForApi24(toInstall);
        else
            openForInstall_ForApiLower24(toInstall);
    }

    private File getFileToInstall() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadEnqueueNb);
        Cursor cursor = downloadManager.query(query);

        if (!cursor.moveToFirst())
            return null;

        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        if (DownloadManager.STATUS_SUCCESSFUL != cursor.getInt(columnIndex))
            return null;

        String filePath = cursor
            .getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            .replaceFirst("^file://", "");

        File toInstall = new File(filePath);
        if (!toInstall.exists())
            return null;

        return toInstall;
    }

    private void openForInstall_ForApi24(File toInstall) {
        Uri apkUri = FileProvider.getUriForFile(
            GithubUpdateManager.this,
            BuildConfig.APPLICATION_ID + ".provider",
            toInstall);

        Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE)
            .setData(apkUri)
            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(install);
    }

    private void openForInstall_ForApiLower24(File toInstall) {
        Uri apkUri = Uri.fromFile(toInstall);
        Intent install = new Intent(Intent.ACTION_VIEW)
            .setDataAndType(apkUri, "application/vnd.android.package-archive")
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(install);
    }

    private void onWriteToExternalPermissionRefused() {
        view.showDownloadCancelled();
    }

    private boolean versionIsBiggerThanCurrent(Version webVersion) {
        int currentVersionPart1 = getResources().getInteger(R.integer.versionPart1);
        int currentVersionPart2 = getResources().getInteger(R.integer.versionPart2);

        if (webVersion.part1 > currentVersionPart1)
            return true;

        if (webVersion.part1 == currentVersionPart1)
            return webVersion.part2 > currentVersionPart2;

        return false;
    }

    private class CheckForUpdatesListener implements OkHttpResponseListener {
        @Override
        public void onResponse(Response response) {
            if (view == null)
                return;

            view.dismissCheckingForUpdates();

            if (response.code() != HttpURLConnection.HTTP_OK)
                onError(response.code());
            else
                onGetDoneSuccess(response);
        }

        @Override
        public void onError(ANError anError) {
            if (view == null)
                return;

            view.dismissCheckingForUpdates();
            view.enableInput();
            onError(anError.getErrorCode());
        }

        private void onError(int errorCode) {
            switch (errorCode) {
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    view.showServiceUnavailableError();
                    break;

                case HttpURLConnection.HTTP_NOT_FOUND:
                    view.showServerNotFoundError();
                    break;

                default:
                    view.showFailedToCheckForUpdatesError();
                    break;
            }
        }
    }

    private class DownloadPermissionListener implements AppUpdatesView.PermissionListener {
        private final Version version;

        public DownloadPermissionListener(Version version) {
            this.version = version;
        }

        @Override
        public void onGranted() {
            onDownloadPermissionGranted(version);
        }

        @Override
        public void onDenied() {
            view.enableInput();
        }
    }

    private class WritePermissionListener implements AppUpdatesView.PermissionListener {
        private final Version version;

        public WritePermissionListener(Version version) {
            this.version = version;
        }

        @Override
        public void onGranted() {
            onWriteToExternalPermissionAccepted(version);
        }

        @Override
        public void onDenied() {
            view.enableInput();
            onWriteToExternalPermissionRefused();
        }
    }

    private class DownloadFinishedBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()))
                return;

            unregisterReceiver(this);
            onDownloadBroadcastReceived(context);
        }
    }
}

