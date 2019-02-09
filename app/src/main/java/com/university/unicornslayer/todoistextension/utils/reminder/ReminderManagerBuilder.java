package com.university.unicornslayer.todoistextension.utils.reminder;

import android.content.Context;

import com.university.unicornslayer.todoistextension.data.local.LocalDataManager;
import com.university.unicornslayer.todoistextension.data.prefs.AtDuePrefs;
import com.university.unicornslayer.todoistextension.data.prefs.BeforeDuePrefs;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.agent.RemindBeforeDueAgent;
import com.university.unicornslayer.todoistextension.utils.reminder.agent.RemindAtDueAgent;

public class ReminderManagerBuilder {
    private ReminderManagerBuilder() {
    }

    public static ReminderManager buildDefault(
        Context context,
        TodoistNotifHelper notifHelper,
        LocalDataManager localDataManager
    ) {
        RemindBeforeDueAgent beforeDueAgent = new RemindBeforeDueAgent(
            new BeforeDuePrefs(context), notifHelper);

        RemindAtDueAgent atDueAgent = new RemindAtDueAgent(
            new AtDuePrefs(context), notifHelper);

        ReminderManager manager = new AppReminderManager(localDataManager);
        manager.addReminderAgent(beforeDueAgent);
        manager.addReminderAgent(atDueAgent);

        return manager;
    }
}
