package com.university.unicornslayer.todoistextension.ReminderManager;

import android.content.Context;
import android.content.ContextWrapper;

import com.university.unicornslayer.todoistextension.DataLayer.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.DataLayer.TodoistItem;
import com.university.unicornslayer.todoistextension.Utils.TodoistNotifHelper;

import java.util.List;

public class RemindAtDueAgent extends ContextWrapper {
    private final SharedPrefsUtils sharedPrefsUtils;
    private final TodoistNotifHelper notifHelper;

    public RemindAtDueAgent(Context context) {
        super(context);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        notifHelper = new TodoistNotifHelper(this);
    }

    public void createReminders(final RemindersData remindersData, List<TodoistItem> items) {
        
    }
}
