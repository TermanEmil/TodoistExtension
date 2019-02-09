package com.university.unicornslayer.todoistextension.utils.reminder.agent;

import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;

public class RemindBeforeDueAgent extends RelativeToNowReminderAgent {
    public RemindBeforeDueAgent(RelativeToNowPrefsProvider prefsProvider, TodoistNotifHelper notifHelper) {
        super(prefsProvider, notifHelper);
    }

    @Override
    public String getResourceKey() {
        return "before-due";
    }
}
