package com.university.unicornslayer.todoistextension.utils.alarms.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.TokenPrefHelper;
import com.university.unicornslayer.todoistextension.di.component.AppComponent;
import com.university.unicornslayer.todoistextension.di.component.DaggerAppComponent;
import com.university.unicornslayer.todoistextension.di.module.AppModule;
import com.university.unicornslayer.todoistextension.utils.alarms.ScheduleManager;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManager;
import com.university.unicornslayer.todoistextension.utils.reminder.model.NextReminderModel;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class ScheduleAlarmReceiver extends BroadcastReceiver {
    private static String TAG = "ScheduleReceiver";

    @Inject TokenPrefHelper tokenPrefHelper;
    @Inject ApiHelper apiHelper;
    @Inject ScheduleManager scheduleManager;
    @Inject ReminderManager reminderManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        AppComponent dagger = DaggerAppComponent.builder()
            .appModule(new AppModule(context))
            .build();
        dagger.inject(this);

        Log.i(TAG, "Broadcast received");
        if (tokenPrefHelper.getToken() == null) {
            Log.w(TAG, "Token is not set. Exiting");
            return;
        }

        apiHelper.getAllItems(new GetAllItemsListener());
    }

    private class GetAllItemsListener implements ApiHelper.GetAllItemsListener {
        @Override
        public void onResponse(List<TodoistItem> items) {
            Log.i(TAG, String.format("Received %d items", items.size()));

            try {
                reminderManager.checkNotifications(items);
            } catch (IOException e) {
                e.printStackTrace();
                onError(-1);
            }

            NextReminderModel nextToRemind = reminderManager.getNextItemToRemind(items);
            if (nextToRemind == null) {
                Log.i(TAG, "No items to schedule for.");
                return;
            }

            Log.i(TAG, String.format("Scheduling for %d", nextToRemind.getWhen()));
            scheduleManager.setExactAlarm(nextToRemind.getWhen());
        }

        @Override
        public void onError(int errorCode) {
            Log.e(TAG, String.format("Failed to check items. Error = %d", errorCode));
        }
    }
}
