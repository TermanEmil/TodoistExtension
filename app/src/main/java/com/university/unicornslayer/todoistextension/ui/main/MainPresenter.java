package com.university.unicornslayer.todoistextension.ui.main;

import com.university.unicornslayer.todoistextension.Scheduling.ScheduleManager;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.TokenPrefHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class MainPresenter {
    private final MainMvpView view;
    private final TokenPrefHelper tokenPrefHelper;
    private final ReminderManager reminderManager;
    private final ApiHelper apiHelper;
    private final ScheduleManager scheduleManager;

    public MainPresenter(
        MainMvpView view,
        TokenPrefHelper tokenPrefHelper,
        ReminderManager reminderManager,
        ApiHelper apiHelper,
        ScheduleManager scheduleManager
    ) {
        this.view = view;
        this.tokenPrefHelper = tokenPrefHelper;
        this.reminderManager = reminderManager;
        this.apiHelper = apiHelper;
        this.scheduleManager = scheduleManager;
    }

    public void onCreate() {
        scheduleManager.setRepeatingAlarm();
    }

    public void onResume() {
        if (tokenPrefHelper.getToken() == null)
            view.gotoInputTokenView();
    }

    public void onCheckRemindersBtnPressed() {
        apiHelper.setToken(tokenPrefHelper.getToken());
        apiHelper.getAllItems(new GetAllItemsListener());
    }

    public void onClickSettings() {
        view.gotoSettingsView();
    }

    private class GetAllItemsListener implements ApiHelper.GetAllItemsListener {
        @Override
        public void onResponse(List<TodoistItem> items) {
            try {
                reminderManager.checkNotifications(items);
            } catch (IOException e) {
                e.printStackTrace();
                view.showFailedToPostNotifications();
            }
        }

        @Override
        public void onError(int errorCode) {
            switch (errorCode) {
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    view.showServiceUnavailableError();
                    break;

                case HttpURLConnection.HTTP_FORBIDDEN:
                    view.showFailedToGetItems();
                    // TODO: If it's forbidden, then the token has been changed
                    break;

                default:
                    view.showFailedToGetItems();
                    break;
            }
        }
    }
}
