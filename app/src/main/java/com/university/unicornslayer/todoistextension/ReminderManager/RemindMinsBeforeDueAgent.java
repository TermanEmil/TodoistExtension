package com.university.unicornslayer.todoistextension.ReminderManager;

import android.app.Notification;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.university.unicornslayer.todoistextension.DataLayer.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.DataLayer.TodoistItem;
import com.university.unicornslayer.todoistextension.Utils.ITodoistItemIf;
import com.university.unicornslayer.todoistextension.Utils.TodoistItemsUtils;
import com.university.unicornslayer.todoistextension.Utils.TodoistNotifHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RemindMinsBeforeDueAgent extends ContextWrapper {
    private static final SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    private final SharedPrefsUtils sharedPrefsUtils;
    private final TodoistNotifHelper notifHelper;

    public RemindMinsBeforeDueAgent(Context context) {
        super(context);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        notifHelper = new TodoistNotifHelper(this);
    }

    public void createReminders(final RemindersData remindersData, List<TodoistItem> items) {
        final Date now = new Date();
        final long milsMin = now.getTime() + sharedPrefsUtils.getSecRemindAtDue() * 1000;
        final long milsMax = milsMin + sharedPrefsUtils.getMinsRemindBeforeDue() * 60 * 1000;

        items = TodoistItemsUtils.filter(items, new ITodoistItemIf() {
            @Override
            public boolean isGood(TodoistItem item) {
                long itemDue = item.getDueDate().getTime();

                return
                    itemDue >= milsMin &&
                    itemDue <= milsMax &&
                    !TodoistItemsUtils.listContainsItem(remindersData.beforeDueReminders, item);
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
            remindersData.beforeDueReminders.add(new Reminder(item));
        }
    }

    private String createTitle(TodoistItem item) {
        return String.format("Due to %s", shortTimeFormat.format(item.getDueDate()));
    }
}
