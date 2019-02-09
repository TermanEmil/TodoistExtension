package com.university.unicornslayer.todoistextension.utils.reminder.agent;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.AtDuePrefsProvider;
import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;

import java.util.List;
import java.util.Map;

public class RemindAtDueAgent implements ReminderAgent {
    private final AtDuePrefsProvider prefs;
    private final TodoistNotifHelper notifHelper;

    public RemindAtDueAgent(AtDuePrefsProvider prefs, TodoistNotifHelper notifHelper) {
        this.prefs = prefs;
        this.notifHelper = notifHelper;
    }

    @Override
    public void createReminders(Map<Integer, Reminder> data, List<TodoistItem> items) {

    }

    @Override
    public String getResourceKey() {
        return "at-due";
    }
}
