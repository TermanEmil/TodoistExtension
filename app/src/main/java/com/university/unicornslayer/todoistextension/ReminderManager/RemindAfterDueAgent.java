package com.university.unicornslayer.todoistextension.ReminderManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.format.DateUtils;

import com.university.unicornslayer.todoistextension.data.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.ITodoistItemIsGood;
import com.university.unicornslayer.todoistextension.utils.TodoistItemsUtils;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RemindAfterDueAgent extends ContextWrapper {
    private static final int notificationId = 42;

    private final SharedPrefsUtils sharedPrefsUtils;
    private final TodoistNotifHelper notifHelper;

    public RemindAfterDueAgent(Context context) {
        super(context);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        notifHelper = new TodoistNotifHelper(this);
    }

    public void createReminders(final RemindersData remindersData, List<TodoistItem> items) {
        final Date now = new Date();
        final long itsPastTimepoint = now.getTime() - (sharedPrefsUtils.getDueCanBeLate() + 1);
        final int interval = sharedPrefsUtils.getIntervalRemindAfterDue();

        final boolean mustBeRementioned = now.getTime() >= remindersData.lastAfterDueRemindersCheck + interval;

        List<TodoistItem> pastItems = TodoistItemsUtils.filter(items, new ITodoistItemIsGood() {
            @Override
            public boolean isGood(TodoistItem item) {
                return item.getDueDate() <= itsPastTimepoint;
            }
        });

        if (mustBeRementioned || thereAreNewItems(pastItems, remindersData.afterDueReminders)) {
            items = pastItems;
        } else
            return;

        if (items.size() == 0)
            return;

        sort(items);
        createNotification(items, getItemsToShow(items));

        for (TodoistItem item : items)
            remindersData.afterDueReminders.put(item.getId(), new Reminder(item));

        if (mustBeRementioned)
            remindersData.lastAfterDueRemindersCheck = now.getTime();
    }

    private void sort(List<TodoistItem> items) {
        Collections.sort(items, new Comparator<TodoistItem>() {
            @Override
            public int compare(TodoistItem o1, TodoistItem o2) {
                return (int) (o2.getDueDate() - o1.getDueDate());
            }
        });
    }

    private List<TodoistItem> getItemsToShow(List<TodoistItem> items) {
        int maxNbToShow = sharedPrefsUtils.getMaxNbOfRemindersToShowAfterDue();
        int n = items.size() > maxNbToShow ? maxNbToShow : items.size();

        List<TodoistItem> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(items.get(i));
        }

        return result;
    }

    @SuppressLint("DefaultLocale")
    private void createNotification(List<TodoistItem> allItems, List<TodoistItem> itemsToShow) {
        String title = String.format("You have %d unfinished task%s",
            allItems.size(),
            allItems.size() > 1 ? "s" : "");

        Date now = new Date();
        String firstLine = decorateContent(itemsToShow.get(0), now);
        firstLine = Html.fromHtml(firstLine).toString();

        NotificationCompat.Builder builder = notifHelper.getBaseBuilder(title, firstLine);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for (TodoistItem item : itemsToShow) {
            inboxStyle.addLine(Html.fromHtml(decorateContent(item, now)));
        }

        if (allItems.size() != itemsToShow.size()) {
            String andNMore = String.format("And <b>%d</b> more", allItems.size() - itemsToShow.size());
            inboxStyle.addLine(Html.fromHtml(andNMore));
        }

        builder.setStyle(inboxStyle);
        notifHelper.notify(notificationId, builder.build());
    }

    private String decorateContent(TodoistItem item, Date now) {
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
            item.getDueDate(),
            now.getTime(),
            0L,
            DateUtils.FORMAT_ABBREV_ALL);
        return String.format("<b>%s</b>  <i>%s</i>", Html.escapeHtml(item.getContent()), relativeTime);
    }

    private boolean thereAreNewItems(List<TodoistItem> items, HashMap<Integer, Reminder> reminders) {
        for (TodoistItem item : items) {
            if (!reminders.containsKey(item.getId()))
                return true;
        }

        return false;
    }
}
