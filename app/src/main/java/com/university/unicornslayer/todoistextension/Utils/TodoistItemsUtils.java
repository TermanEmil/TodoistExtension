package com.university.unicornslayer.todoistextension.Utils;

import com.university.unicornslayer.todoistextension.DataStuff.TodoistItem;
import com.university.unicornslayer.todoistextension.ReminderManager.Reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TodoistItemsUtils {
    public static List<TodoistItem> filter(List<TodoistItem> items, ITodoistItemIsGood condition) {
        List<TodoistItem> result = new ArrayList<>();
        for (TodoistItem item : items) {
            if (condition.isGood(item))
                result.add(item);
        }

        return result;
    }

    public static List<TodoistItem> extractWithDueDate(List<TodoistItem> items) {
        return filter(items, new ITodoistItemIsGood() {
            @Override
            public boolean isGood(TodoistItem item) {
                return item.getDueDate() != null;
            }
        });
    }

    public static boolean itemMustBeRementioned(
        HashMap<Integer, Reminder> reminders,
        TodoistItem item,
        int milsInterval,
        Date now
    ) {
        if (!reminders.containsKey(item.getId()))
            return true;

        return now.getTime() - reminders.get(item.getId()).publicationDate.getTime() >= milsInterval;
    }

    public static TodoistItem getNextClosestItem(List<TodoistItem> items) {
        TodoistItem closestItem = null;
        long now = Calendar.getInstance().getTimeInMillis();

        for (TodoistItem item : items) {
            long itemDue = item.getDueDate().getTime();
            if (itemDue >= now && (closestItem == null || itemDue >= closestItem.getDueDate().getTime()))
                closestItem = item;
        }

        return closestItem;
    }
}
