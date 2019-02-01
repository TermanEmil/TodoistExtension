package com.university.unicornslayer.todoistextension.Requests;

import com.university.unicornslayer.todoistextension.DataLayer.TodoistItem;

import java.util.List;

public interface ITodoistItemsHandler {
    void onDone(List<TodoistItem> items);
}
