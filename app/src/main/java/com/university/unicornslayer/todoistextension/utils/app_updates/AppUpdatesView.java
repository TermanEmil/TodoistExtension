package com.university.unicornslayer.todoistextension.utils.app_updates;

import com.university.unicornslayer.todoistextension.utils.app_updates.github_updater.GithubUpdateManager;

public interface AppUpdatesView {
    interface PermissionListener {
        void onGranted();
        void onDenied();
    }

    void disableInput();

    void enableInput();

    void showDownloading();

    void showDownloadCancelled();

    void dismissDownloading();

    void showFailedToInstallError();

    void showCheckingForUpdates();

    void dismissCheckingForUpdates();

    void showServiceUnavailableError();

    void showServerNotFoundError();

    void showFailedToCheckForUpdatesError();

    void showServerProvidedInvalidInfoError();

    void showNoUpdatesAvailable();

    void askForDownloadPermission(PermissionListener listener);

    void askForWriteToExternalPermission(PermissionListener writePermissionListener);
}
