package com.university.unicornslayer.todoistextension.utils.reminder.model;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;

public class NotifiModel {
    private final String title;
    private final String msg;
    private final TodoistItem[] items;

    public NotifiModel(String title, String msg, TodoistItem[] items) {
        this.title = title;
        this.msg = msg;
        this.items = items;
    }

    public NotifiModel(String title, String msg, TodoistItem item) {
        this.title = title;
        this.msg = msg;

        this.items = new TodoistItem[1];
        this.items[0] = item;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }

    public TodoistItem[] getItems() {
        return items;
    }
}
