package com.university.unicornslayer.todoistextension.utils.reminder.agents;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.reminder.model.NextReminderModel;
import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;

import java.util.List;
import java.util.Map;

public interface ReminderAgent {
    void createReminders(Map<Integer, Reminder> data, List<TodoistItem> items);

    NextReminderModel getNextItemToRemind(List<TodoistItem> items);

    String getResourceKey();

    void removeOldData(Map<Integer, Reminder> data, List<TodoistItem> items);
}
