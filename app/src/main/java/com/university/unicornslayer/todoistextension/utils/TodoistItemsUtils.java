package com.university.unicornslayer.todoistextension.utils;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodoistItemsUtils {
    public static List<TodoistItem> extractItems(JSONArray jsonArray) throws JSONException {
        List<TodoistItem> result = new ArrayList<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++)
            result.add(new TodoistItem(jsonArray.getJSONObject(i)));

        return result;
    }

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
}
