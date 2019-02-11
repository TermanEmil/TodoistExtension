package com.university.unicornslayer.todoistextension.utils.app_updates;

public interface AppUpdater {
    void setView(AppUpdatesView view);

    void checkForUpdates();

    void onDestroy();

    void cancelCheckingForUpdates();
}
