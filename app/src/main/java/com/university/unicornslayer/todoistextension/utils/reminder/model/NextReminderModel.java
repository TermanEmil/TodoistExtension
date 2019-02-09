package com.university.unicornslayer.todoistextension.utils.reminder.model;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;

public class NextReminderModel {
    private final TodoistItem item;
    private final long when;

    public NextReminderModel(TodoistItem item, long when) {
        this.item = item;
        this.when = when;
    }

    public TodoistItem getItem() {
        return item;
    }

    public long getWhen() {
        return when;
    }

    public long getTimeRemaining(long time) {
        return getWhen() - time;
    }
}
