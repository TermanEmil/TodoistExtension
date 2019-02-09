package com.university.unicornslayer.todoistextension.utils.reminder.agent;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;

public class RemindAtDueAgent extends ReminderRelativeToNowAgent {
    public RemindAtDueAgent(RelativeToNowPrefsProvider prefs, TodoistNotifHelper notifHelper) {
        super(prefs, notifHelper);
    }

    @Override
    protected String createNotifMsg(TodoistItem item) {
        return "NOW! " + super.createNotifMsg(item);
    }

    @Override
    public String getResourceKey() {
        return "at-due";
    }
}
