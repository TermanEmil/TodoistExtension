package com.university.unicornslayer.todoistextension.utils.reminder.agent;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.ITodoistItemIsGood;
import com.university.unicornslayer.todoistextension.utils.TodoistItemsUtils;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;
import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class RelativeToNowReminderAgent implements ReminderAgent {
    @SuppressLint("SimpleDateFormat")
    protected static final SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

    protected final RelativeToNowPrefsProvider prefs;
    protected final TodoistNotifHelper notifHelper;

    protected RelativeToNowReminderAgent(
        RelativeToNowPrefsProvider prefs,
        TodoistNotifHelper notifHelper
    ) {
        this.prefs = prefs;
        this.notifHelper = notifHelper;
    }

    @Override
    public void createReminders(final Map<Integer, Reminder> data, List<TodoistItem> items) {
        if (prefs.getIntervalMax() == -1 || items.size() == 0)
            return;

        final long now = Calendar.getInstance().getTimeInMillis();
        final long milsMin = now + prefs.getIntervalMin();
        final long milsMax = now + prefs.getIntervalMax();
        items = TodoistItemsUtils.filter(items, new ITodoistItemIsGood() {
            @Override
            public boolean isGood(TodoistItem item) {
                return
                    item.getDueDate() >= milsMin &&
                    item.getDueDate() <= milsMax &&
                    (!data.containsKey(item.getId()) ||
                    Objects.requireNonNull(data.get(item.getId())).compareTo(item) != 0);
            }
        });

        if (items.size() == 0)
            return;

        for (TodoistItem item : items)
            createReminder(data, item);
    }

    private void createReminder(Map<Integer, Reminder> data, TodoistItem item) {
        notifHelper.notify(item.getId(), buildNotification(item, prefs.produceSound()));
        data.put(item.getId(), new Reminder(item));
    }

    protected Notification buildNotification(TodoistItem item, boolean withSound) {
        NotificationCompat.Builder builder = notifHelper.getBaseBuilder()
            .setContentText(createNotifMsg(item))
            .setContentTitle(createNotifTitle(item));

        if (withSound)
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        return builder.build();
    }

    protected String createNotifTitle(TodoistItem item) {
        return item.getContent();
    }

    protected String createNotifMsg(TodoistItem item) {
        return String.format("Due to %s", shortTimeFormat.format(item.getDueDate()));
    }

    @Override
    public abstract String getResourceKey();
}
