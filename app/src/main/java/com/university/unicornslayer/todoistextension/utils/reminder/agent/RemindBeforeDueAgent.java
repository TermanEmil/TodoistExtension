package com.university.unicornslayer.todoistextension.utils.reminder.agent;

import android.app.Notification;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.ITodoistItemIsGood;
import com.university.unicornslayer.todoistextension.utils.TodoistItemsUtils;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.BeforeDuePrefsProvider;
import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RemindBeforeDueAgent implements ReminderAgent {
    private static final SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    private final BeforeDuePrefsProvider prefs;
    private final TodoistNotifHelper notifHelper;

    public RemindBeforeDueAgent(
        BeforeDuePrefsProvider beforeDuePrefsProvider,
        TodoistNotifHelper notifHelper
    ) {
        this.prefs = beforeDuePrefsProvider;
        this.notifHelper = notifHelper;
    }

    @Override
    public void createReminders(final Map<Integer, Reminder> data, List<TodoistItem> items) {
        if (prefs.getRemindBeforeDueMax() == -1 || items.size() == 0)
            return;

        final long now = Calendar.getInstance().getTimeInMillis();
        final long milsMin = now + prefs.getRemindBeforeDueMin();
        final long milsMax = now + prefs.getRemindBeforeDueMax();
        items = TodoistItemsUtils.filter(items, new ITodoistItemIsGood() {
            @Override
            public boolean isGood(TodoistItem item) {
                return
                    item.getDueDate() >= milsMin &&
                    item.getDueDate() <= milsMax &&
                   (!data.containsKey(item.getId()) ||
                     data.get(item.getId()).compareTo(item) != 0);
            }
        });

        if (items.size() == 0)
            return;

        for (TodoistItem item : items)
            createReminder(data, item);
    }

    private void createReminder(Map<Integer, Reminder> data, TodoistItem item) {
        notifHelper.notify(item.getId(), buildNotification(item, prefs.getProduceSoundBeforeDue()));
        data.put(item.getId(), new Reminder(item));
    }

    private Notification buildNotification(TodoistItem item, boolean withSound) {
        NotificationCompat.Builder builder = notifHelper.getBaseBuilder(
            item.getContent(),
            createTitle(item));

        if (withSound)
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        return builder.build();
    }

    private String createTitle(TodoistItem item) {
        return String.format("Due to %s", shortTimeFormat.format(item.getDueDate()));
    }

    @Override
    public String getResourceKey() {
        return "before-due";
    }
}
