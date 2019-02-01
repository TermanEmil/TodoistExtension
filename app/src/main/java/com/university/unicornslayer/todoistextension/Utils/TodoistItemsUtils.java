package com.university.unicornslayer.todoistextension.Utils;

import com.university.unicornslayer.todoistextension.DataStuff.TodoistItem;
import com.university.unicornslayer.todoistextension.ReminderManager.Reminder;

import java.util.ArrayList;
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

    public static List<TodoistItem> withDueInInterval(
            List<TodoistItem> items,
            final long milsMin,
            final long milsMax
    ) {
        return filter(items, new ITodoistItemIsGood() {
            @Override
            public boolean isGood(TodoistItem item) {
                long itemDue = item.getDueDate().getTime();
                return itemDue >= milsMin && itemDue <= milsMax;
            }
        });
    }

    public static boolean listContainsItem(List<Reminder> reminders, TodoistItem item) {
        for (Reminder reminder : reminders) {
            if (reminder.compareTo(item) == 0)
                return true;
        }

        return false;
    }
}
