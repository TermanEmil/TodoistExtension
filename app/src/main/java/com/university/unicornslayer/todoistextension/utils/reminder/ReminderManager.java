package com.university.unicornslayer.todoistextension.utils.reminder;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.reminder.agent.ReminderAgent;
import com.university.unicornslayer.todoistextension.utils.reminder.model.NextReminderModel;

import java.io.IOException;
import java.util.List;

public interface ReminderManager {
    void addReminderAgent(ReminderAgent reminderAgent);

    void checkNotifications(List<TodoistItem> items) throws IOException;

    NextReminderModel getNextItemToRemind(List<TodoistItem> items);
}
