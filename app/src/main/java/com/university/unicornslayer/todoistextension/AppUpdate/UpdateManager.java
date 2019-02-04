package com.university.unicornslayer.todoistextension.AppUpdate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.university.unicornslayer.todoistextension.BuildConfig;
import com.university.unicornslayer.todoistextension.DataStuff.FileDataManager;
import com.university.unicornslayer.todoistextension.DataStuff.IOnTaskDone;
import com.university.unicornslayer.todoistextension.Permissions.PermissionHelper;
import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.Requests.BasicRequestTask;
import com.university.unicornslayer.todoistextension.Requests.IObjectHandler;
import com.university.unicornslayer.todoistextension.Requests.IRequestHandler;
import com.university.unicornslayer.todoistextension.Requests.RequestResult;
import com.university.unicornslayer.todoistextension.Requests.ResponseReaderTask;
import com.university.unicornslayer.todoistextension.Utils.TodoistNotifHelper;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

import static android.support.v4.app.ActivityCompat.requestPermissions;

@SuppressLint("DefaultLocale")
public class UpdateManager extends ContextWrapper {
    private static final Pattern versionNumberPattern =
        Pattern.compile("<a href=\"(/TermanEmil/TodoistExtension/releases/download/v(\\d+).(\\d+)/.+\\.apk)");

    private final PermissionHelper permissionHelper;

    private ProgressDialog spinner;
    private BasicRequestTask getSiteTask1 = null;
    private ResponseReaderTask getSiteTask2 = null;
    private boolean canCheckForUpdatesAgain = true;

    private long downloadEnqueueNb;

    public UpdateManager(Context context, PermissionHelper permissionHelper) {
        super(context);
        this.permissionHelper = permissionHelper;

        spinner = new ProgressDialog(this);
        spinner.setIndeterminate(false);

        spinner.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (getSiteTask1 != null) {
                    getSiteTask1.cancel(true);
                    getSiteTask1 = null;
                }
                if (getSiteTask2 != null) {
                    getSiteTask2.cancel(true);
                    getSiteTask2 = null;
                }

                canCheckForUpdatesAgain = true;
            }
        });
    }

    public void checkForUpdates() {
        if (!canCheckForUpdatesAgain)
            return;
        canCheckForUpdatesAgain = false;

        spinner.setMessage("Please wait");
        spinner.setTitle("Checking for updates");
        spinner.setCancelable(true);
        spinner.show();

        getSiteTask1 = new BasicRequestTask(getString(R.string.update_url), new IRequestHandler() {
            @Override
            public void onDone(RequestResult result) {
                onGetDone(result);
                getSiteTask1 = null;
                spinner.dismiss();
                canCheckForUpdatesAgain = true;
            }
        });

        getSiteTask1.execute();
    }

    @SuppressLint("DefaultLocale")
    private void onGetDone(RequestResult result) {
        switch (result.httpStatus) {
            case HttpURLConnection.HTTP_OK:
                onGetDoneSuccess(result.response);
                break;

            case HttpURLConnection.HTTP_UNAVAILABLE:
                showError("Service unavailable. Try again latter");
                break;

            case HttpURLConnection.HTTP_NOT_FOUND:
                showError("The server no longer provides updates");
                break;

            default:
                showError(String.format("Failed with http status %d", result.httpStatus));
                break;
        }
    }

    private void onGetDoneSuccess(Response response) {
        canCheckForUpdatesAgain = false;
        spinner.show();
        getSiteTask2 = new ResponseReaderTask(response, new IObjectHandler() {
            @Override
            public void onDone(Object o) {
                onResponseBodyReadDone((String) o);
                getSiteTask2 = null;
                spinner.dismiss();
                canCheckForUpdatesAgain = true;
            }
        });
        getSiteTask2.execute();
    }

    private void onResponseBodyReadDone(String bodyStr) {
        if (bodyStr == null) {
            showError("Failed to check for updates");
            Log.e("Updater", "Failed to read response body");
            return;
        }

        Matcher m = versionNumberPattern.matcher(bodyStr);
        if (!m.find() || m.groupCount() != 3) {
            showError(getString(R.string.update_regex_fail_errormsg));
            Log.e("Internal", getString(R.string.update_regex_fail_errormsg));
            return;
        }

        final Version webVersion = new Version(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
        if (!versionIsBiggerThanCurrent(webVersion)) {
            Toast.makeText(this, getString(R.string.its_latest_version_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        final String downloadLink = "https://github.com" + m.group(1);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        onDownloadNewVersionClick(webVersion, downloadLink);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setMessage("A new version is available. Download it?")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener);

        builder.show();
    }

    private void onDownloadNewVersionClick(final Version version, final String downloadLink) {
        permissionHelper.setWriteExternalStoragePermissionHandler(new IObjectHandler() {
            @Override
            public void onDone(Object o) {
                boolean granted = (Boolean) o;

                if (granted) {
                    onDownloadNewVersionPermissionGranted(version, downloadLink);
                }
                else {
                    Toast.makeText(
                        UpdateManager.this,
                        "The app was not allowed to download",
                        Toast.LENGTH_LONG).show();
                }
            }
        });

        if (permissionHelper.hasWriteExternalStoragePermission())
            permissionHelper.onGetWriteExternalStoragePermissionDone(true);
        else
            permissionHelper.requestWriteExternalStoragePermission();
    }

    private void onDownloadNewVersionPermissionGranted(Version version, String downloadLink) {
        canCheckForUpdatesAgain = false;

        spinner.show();
        spinner.setCancelable(false);
        spinner.setTitle("Downloading");
        spinner.setMessage("Downloading, please wait");

        final String filename = String.format("TodoistExtensionV.%d.%d.apk", version.part1, version.part2);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadLink))
            .setTitle(filename)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setVisibleInDownloadsUi(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        request.allowScanningByMediaScanner();

        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()))
                    return;

                spinner.dismiss();
                canCheckForUpdatesAgain = true;

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadEnqueueNb);

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Cursor c = downloadManager.query(query);

                if (!c.moveToFirst())
                    return;

                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    filePath = filePath.replaceFirst("^file://", "");
                    File toInstall = new File(filePath);
                    if (!toInstall.exists()) {
                        Log.e("Internal", String.format("%s: does not exist", toInstall.getAbsolutePath()));
                        System.exit(1);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri apkUri = FileProvider.getUriForFile(UpdateManager.this, BuildConfig.APPLICATION_ID + ".provider", toInstall);
                        Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        install.setData(apkUri);
                        install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(install);
                    } else {
                        Uri apkUri = Uri.fromFile(toInstall);
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(install);
                    }
                }
            }
        };

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadEnqueueNb = downloadmanager.enqueue(request);
    }

    private void showError(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
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

}

class Version {
    public final int part1;
    public final int part2;

    public Version(int part1, int part2) {
        this.part1 = part1;
        this.part2 = part2;
    }
}