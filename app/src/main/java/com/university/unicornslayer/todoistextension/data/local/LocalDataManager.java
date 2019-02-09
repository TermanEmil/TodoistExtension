package com.university.unicornslayer.todoistextension.data.local;

import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;

import java.io.IOException;
import java.util.Map;

public interface LocalDataManager {
    void loadData() throws IOException;

    void saveData() throws IOException;

    Map<Integer, Reminder> getDataFromKey(String key);
}
