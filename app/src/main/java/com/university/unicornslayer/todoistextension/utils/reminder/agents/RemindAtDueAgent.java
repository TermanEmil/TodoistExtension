package com.university.unicornslayer.todoistextension.utils.reminder.agents;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.notif.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;

import javax.inject.Inject;

public class RemindAtDueAgent extends ReminderRelativeToNowAgent {
    @Inject
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
