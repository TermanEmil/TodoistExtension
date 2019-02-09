package com.university.unicornslayer.todoistextension.utils.reminder;

import com.university.unicornslayer.todoistextension.data.local.LocalDataManager;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.TodoistItemsUtils;
import com.university.unicornslayer.todoistextension.utils.reminder.agent.ReminderAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppReminderManager implements ReminderManager {
    private final LocalDataManager localDataManager;
    private List<ReminderAgent> reminderAgents = new ArrayList<>();

    public AppReminderManager(LocalDataManager localDataManager) {
        this.localDataManager = localDataManager;
    }

    @Override
    public void addReminderAgent(ReminderAgent reminderAgent) {
        reminderAgents.add(reminderAgent);
    }

    @Override
    public void checkNotifications(List<TodoistItem> items) throws IOException {
        items = TodoistItemsUtils.extractWithDueDate(items);

        localDataManager.loadData();
        for (ReminderAgent reminderAgent : reminderAgents) {
            reminderAgent.createReminders(
                localDataManager.getDataFromKey(reminderAgent.getResourceKey()), items);
        }

        localDataManager.saveData();
    }
}
