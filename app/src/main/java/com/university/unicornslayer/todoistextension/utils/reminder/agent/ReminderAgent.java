package com.university.unicornslayer.todoistextension.utils.reminder.agent;

import com.university.unicornslayer.todoistextension.data.local.LocalDataManager;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;

import java.util.List;
import java.util.Map;

public interface ReminderAgent {
    void createReminders(Map<Integer, Reminder> data, List<TodoistItem> items);

    String getResourceKey();
}
