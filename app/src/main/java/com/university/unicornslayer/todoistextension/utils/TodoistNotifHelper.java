package com.university.unicornslayer.todoistextension.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import com.university.unicornslayer.todoistextension.R;

public class TodoistNotifHelper extends ContextWrapper {
    private static final String notifChannelId = "I'm not sure wtf is this, but ok";
    private static boolean channelIsCreated = false;

    private final NotificationManager notificationManager;

    public TodoistNotifHelper(Context context) {
        super(context);

        notificationManager = getSystemService(NotificationManager.class);
    }

    public NotificationCompat.Builder getBaseBuilder() {
        return new NotificationCompat.Builder(this, notifChannelId)
                .setSmallIcon(R.drawable.todoist_logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    public void notify(int notifId, Notification notif) {
        if (!channelIsCreated) {
            createNotificationChannel();
            channelIsCreated = true;
        }

        notificationManager.notify(notifId, notif);
    }

    public boolean notifIsVisible(int notifId) {
        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == notifId)
                return true;
        }

        return false;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(notifChannelId, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }
}
