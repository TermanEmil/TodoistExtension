package com.university.unicornslayer.todoistextension.utils;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;

import java.util.ArrayList;
import java.util.Calendar;
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
                return item.getDueDate() != -1;
            }
        });
    }

    public static TodoistItem getNextClosestItem(List<TodoistItem> items) {
        TodoistItem closestItem = null;
        long now = Calendar.getInstance().getTimeInMillis();

        for (TodoistItem item : items) {
            long itemDue = item.getDueDate();
            if (itemDue >= now && (closestItem == null || itemDue < closestItem.getDueDate()))
                closestItem = item;
        }

        return closestItem;
    }

    public static boolean listContainsIf(List<TodoistItem> items, ITodoistItemIsGood condition) {
        for (TodoistItem item : items) {
            if (condition.isGood(item))
                return true;
        }

        return false;
    }
}
