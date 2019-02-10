package com.university.unicornslayer.todoistextension.ReminderManager;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;

import java.util.Calendar;
import java.util.Date;

public class Reminder implements Comparable<TodoistItem> {
    public int itemId;
    public long dueDate;
    public String itemContent;
    public Date publicationDate;

    public Reminder(TodoistItem item) {
        itemId = item.getId();
        dueDate = item.getDueDate();
        itemContent = item.getContent();
        publicationDate = Calendar.getInstance().getTime();
    }

    @Override
    public int compareTo(TodoistItem o) {
        if (
                o.getId() == itemId &&
                o.getContent().equals(itemContent) &&
                o.getDueDate() == dueDate
        ) {
            return 0;
        }

        return -1;
    }
}
