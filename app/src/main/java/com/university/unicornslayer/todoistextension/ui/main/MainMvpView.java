package com.university.unicornslayer.todoistextension.ui.main;

public interface MainMvpView {
    void gotoInputTokenView();

    void showFailedToPostNotifications();

    void showServiceUnavailableError();

    void showFailedToGetItems();

    void gotoSettingsView();

    void showCheckingReminders();

    void hideCheckingReminders();
}
