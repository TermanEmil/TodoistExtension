package com.university.unicornslayer.todoistextension.Requests;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;

import java.util.List;

public interface ITodoistItemsHandler {
    void onDone(List<TodoistItem> items);
}
