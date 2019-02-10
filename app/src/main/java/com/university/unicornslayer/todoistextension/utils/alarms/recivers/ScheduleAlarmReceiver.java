package com.university.unicornslayer.todoistextension.utils.alarms.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.util.Log;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.data.local.AppLocalDataManager;
import com.university.unicornslayer.todoistextension.data.local.LocalDataManager;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.network.AppApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.AppTokenPrefHelper;
import com.university.unicornslayer.todoistextension.data.prefs.TokenPrefHelper;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.alarms.AppScheduleManager;
import com.university.unicornslayer.todoistextension.utils.alarms.ScheduleManager;
import com.university.unicornslayer.todoistextension.utils.files.AppFileIOHelper;
import com.university.unicornslayer.todoistextension.utils.files.FileIOHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManager;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManagerBuilder;
import com.university.unicornslayer.todoistextension.utils.reminder.model.NextReminderModel;

import java.io.IOException;
import java.util.List;

public class ScheduleAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "----------------------------- Broadcast received");

        TokenPrefHelper tokenPrefHelper = new AppTokenPrefHelper(context);
        if (tokenPrefHelper.getToken() == null)
            return;

        FileIOHelper fileIOHelper = new AppFileIOHelper(context);
        LocalDataManager localDataManager = new AppLocalDataManager(
            context.getString(R.string.reminders_data_filename),
            fileIOHelper);

        TodoistNotifHelper notifHelper = new TodoistNotifHelper(context);

        ReminderManager reminderManager = ReminderManagerBuilder.buildDefault(
            context, notifHelper, localDataManager);

        ScheduleManager scheduleManager = new AppScheduleManager(context);

        ApiHelper apiHelper = new AppApiHelper();
        apiHelper.setToken(tokenPrefHelper.getToken());
        apiHelper.getAllItems(new GetAllItemsListener(reminderManager, scheduleManager));
    }

    private class GetAllItemsListener implements ApiHelper.GetAllItemsListener {
        private final ReminderManager reminderManager;
        private final ScheduleManager scheduleManager;

        GetAllItemsListener(ReminderManager reminderManager, ScheduleManager scheduleManager) {
            this.reminderManager = reminderManager;
            this.scheduleManager = scheduleManager;
        }

        @Override
        public void onResponse(List<TodoistItem> items) {
            Log.i("Receiver", String.format("Received %d items", items.size()));

            try {
                reminderManager.checkNotifications(items);
            } catch (IOException e) {
                e.printStackTrace();
                onError(-1);
            }

            NextReminderModel nextToRemind = reminderManager.getNextItemToRemind(items);
            if (nextToRemind == null)
                return;

            scheduleManager.setExactAlarm(nextToRemind.getWhen());
        }

        @Override
        public void onError(int errorCode) {
            Log.e("Broadcast reciver error", String.format("Failed to check items with error = %d", errorCode));
        }
    }
}
