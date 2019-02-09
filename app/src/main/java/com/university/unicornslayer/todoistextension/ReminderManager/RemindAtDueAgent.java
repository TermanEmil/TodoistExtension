package com.university.unicornslayer.todoistextension.ReminderManager;

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

public class RemindAtDueAgent extends ContextWrapper {
    private static final SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    private final SharedPrefsUtils sharedPrefsUtils;
    private final TodoistNotifHelper notifHelper;

    public RemindAtDueAgent(Context context) {
        super(context);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        notifHelper = new TodoistNotifHelper(this);
    }

    public void createReminders(final RemindersData remindersData, List<TodoistItem> items) {
        Date now = new Date();
        final long milsMin = now.getTime() - sharedPrefsUtils.getDueCanBeLate();
        final long milsMax = now.getTime() + sharedPrefsUtils.getRemindAtDue();

        items = TodoistItemsUtils.filter(items, new ITodoistItemIsGood() {
            @Override
            public boolean isGood(TodoistItem item) {
                return
                    item.getDueDate() >= milsMin &&
                        item.getDueDate() <= milsMax &&
                   (!remindersData.atDueReminders.containsKey(item.getId()) ||
                    remindersData.atDueReminders.get(item.getId()).compareTo(item) != 0);
            }
        });

        if (items.size() == 0)
            return;

        for (TodoistItem item : items) {
            NotificationCompat.Builder builder = notifHelper.getBaseBuilder()
                .setContentTitle(item.getContent())
                .setContentText(createTitle(item));

            if (sharedPrefsUtils.getProduceSoundAtDue())
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            notifHelper.notify(item.getId(), builder.build());
            remindersData.atDueReminders.put(item.getId(), new Reminder(item));
        }
    }

    private String createTitle(TodoistItem item) {
        return String.format("Do it now! It's due to %s", shortTimeFormat.format(item.getDueDate()));
    }
}