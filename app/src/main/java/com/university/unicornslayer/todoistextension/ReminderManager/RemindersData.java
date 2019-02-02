package com.university.unicornslayer.todoistextension.ReminderManager;

import android.annotation.SuppressLint;

import com.university.unicornslayer.todoistextension.DataStuff.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.DataStuff.TodoistItem;
import com.university.unicornslayer.todoistextension.Utils.ITodoistItemIsGood;
import com.university.unicornslayer.todoistextension.Utils.TodoistItemsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class RemindersData {
    public HashMap<Integer, Reminder> beforeDueReminders = new HashMap<>();
    public HashMap<Integer, Reminder> atDueReminders = new HashMap<>();
    public HashMap<Integer, Reminder> afterDueReminders = new HashMap<>();
    public List<Integer> afterDueCurrentlyBeingMentioned = new ArrayList<>();

    public void removeRedundantData(List<TodoistItem> items, SharedPrefsUtils sharedPrefsUtils) {
        removeBeforeDue();
        removeAtDue(sharedPrefsUtils);
        removeAfterDue(items);
    }

    private void removeBeforeDue() {
        long now = Calendar.getInstance().getTimeInMillis();
        Iterator it = beforeDueReminders.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Reminder reminder = (Reminder) pair.getValue();

            if (reminder.dueDate.getTime() < now)
                it.remove();
        }
    }

    private void removeAtDue(SharedPrefsUtils sharedPrefsUtils) {
        long timePoint =
            Calendar.getInstance().getTimeInMillis() -
            sharedPrefsUtils.getSecDueCanBeLate() * 1000 + 1;

        Iterator it = atDueReminders.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Reminder reminder = (Reminder) pair.getValue();

            if (reminder.dueDate.getTime() < timePoint)
                it.remove();
        }
    }

    // Remove if there are no such items anymore
    private void removeAfterDue(List<TodoistItem> items) {
        Iterator it = afterDueReminders.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            final Reminder reminder = (Reminder) pair.getValue();

            boolean condition = TodoistItemsUtils.listContainsIf(items, new ITodoistItemIsGood() {
                @Override
                public boolean isGood(TodoistItem item) {
                    return item.getId() == reminder.itemId;
                }
            });

            if (!condition)
                it.remove();
        }
    }
}
