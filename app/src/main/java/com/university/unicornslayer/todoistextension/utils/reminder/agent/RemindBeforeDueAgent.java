package com.university.unicornslayer.todoistextension.utils.reminder.agent;

import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;

import javax.inject.Inject;

public class RemindBeforeDueAgent extends ReminderRelativeToNowAgent {
    @Inject
    public RemindBeforeDueAgent(RelativeToNowPrefsProvider prefsProvider, TodoistNotifHelper notifHelper) {
        super(prefsProvider, notifHelper);
    }

    @Override
    public String getResourceKey() {
        return "before-due";
    }
}
