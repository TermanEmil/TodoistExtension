package com.university.unicornslayer.todoistextension.utils;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RawItemsUtils {
    public static List<TodoistItem> extractItems(JSONArray jsonArray) throws JSONException {
        List<TodoistItem> result = new ArrayList<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++)
            result.add(new TodoistItem(jsonArray.getJSONObject(i)));

        return result;
    }
}
