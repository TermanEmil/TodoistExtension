package com.university.unicornslayer.todoistextension.ReminderManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.university.unicornslayer.todoistextension.data.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.ITodoistItemIsGood;
import com.university.unicornslayer.todoistextension.utils.TodoistItemsUtils;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class RemindBeforeDueAgent extends ContextWrapper {
    private static final SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    private final SharedPrefsUtils sharedPrefsUtils;
    private final TodoistNotifHelper notifHelper;

    public RemindBeforeDueAgent(Context context) {
        super(context);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        notifHelper = new TodoistNotifHelper(this);
    }

    public void createReminders(final RemindersData remindersData, List<TodoistItem> items) {
        final long now = new Date().getTime();
        final long milsMin = now + (sharedPrefsUtils.getRemindAtDue() < 0 ? 0 : sharedPrefsUtils.getRemindAtDue());
        final long milsMax = now + sharedPrefsUtils.getRemindBeforeDue();

        items = TodoistItemsUtils.filter(items, new ITodoistItemIsGood() {
            @Override
            public boolean isGood(TodoistItem item) {
                return
                    item.getDueDate() >= milsMin &&
                    item.getDueDate() <= milsMax &&
                   (!remindersData.beforeDueReminders.containsKey(item.getId()) ||
                    remindersData.beforeDueReminders.get(item.getId()).compareTo(item) != 0);
            }
        });

        if (items.size() == 0)
            return;

        for (TodoistItem item : items) {
            NotificationCompat.Builder builder = notifHelper.getBaseBuilder(
                    item.getContent(),
                    createTitle(item));

            if (sharedPrefsUtils.getProduceSoundBeforeDue())
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            notifHelper.notify(item.getId(), builder.build());
            remindersData.beforeDueReminders.put(item.getId(), new Reminder(item));
        }
    }

    private String createTitle(TodoistItem item) {
        return String.format("Due to %s", shortTimeFormat.format(item.getDueDate()));
    }
}
