package com.university.unicornslayer.todoistextension.ui.main;

import android.content.SharedPreferences;
import android.util.Log;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.AppSharedPrefs;
import com.university.unicornslayer.todoistextension.data.prefs.TokenPrefHelper;
import com.university.unicornslayer.todoistextension.utils.alarms.ScheduleManager;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManager;
import com.university.unicornslayer.todoistextension.utils.reminder.model.NextReminderModel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import javax.inject.Inject;

public class MainPresenter {
    private MainMvpView view;
    private final TokenPrefHelper tokenPrefHelper;
    private final ReminderManager reminderManager;
    private final ApiHelper apiHelper;
    private final ScheduleManager scheduleManager;

    @Inject
    public MainPresenter(
        TokenPrefHelper tokenPrefHelper,
        ReminderManager reminderManager,
        ApiHelper apiHelper,
        ScheduleManager scheduleManager
    ) {
        this.tokenPrefHelper = tokenPrefHelper;
        this.reminderManager = reminderManager;
        this.apiHelper = apiHelper;
        this.scheduleManager = scheduleManager;
    }

    public void setView(MainMvpView view) {
        this.view = view;
    }

    public void onCreate() {
        scheduleManager.setRepeatingAlarm();
    }

    public void onResume() {
        if (tokenPrefHelper.getToken() == null)
            view.gotoInputTokenView();
    }

    public void onCheckRemindersBtnPressed() {
        scheduleManager.setRepeatingAlarm();
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

            NextReminderModel nextToRemind = reminderManager.getNextItemToRemind(items);
            if (nextToRemind == null) {
                return;
            }

            scheduleManager.setExactAlarm(nextToRemind.getWhen());
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
