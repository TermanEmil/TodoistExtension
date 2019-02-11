package com.university.unicornslayer.todoistextension.utils.reminder;

import com.university.unicornslayer.todoistextension.data.local.LocalDataManager;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;
import com.university.unicornslayer.todoistextension.utils.todoist_common.TodoistItemsUtils;
import com.university.unicornslayer.todoistextension.utils.reminder.agents.ReminderAgent;
import com.university.unicornslayer.todoistextension.utils.reminder.model.NextReminderModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class AppReminderManager implements ReminderManager {
    private final LocalDataManager dataManager;
    private List<ReminderAgent> reminderAgents = new ArrayList<>();

    @Inject
    public AppReminderManager(LocalDataManager localDataManager) {
        this.dataManager = localDataManager;
    }

    @Override
    public void addReminderAgent(ReminderAgent reminderAgent) {
        reminderAgents.add(reminderAgent);
    }

    @Override
    public void checkNotifications(List<TodoistItem> items) throws IOException {
        items = TodoistItemsUtils.extractWithDueDate(items);

        dataManager.loadData();
        for (ReminderAgent reminderAgent : reminderAgents) {
            Map<Integer, Reminder> reminderData = dataManager.getDataFromKey(reminderAgent.getResourceKey());

            reminderAgent.createReminders(reminderData, items);
            reminderAgent.removeOldData(reminderData, items);
        }
        dataManager.saveData();
    }

    @Override
    public NextReminderModel getNextItemToRemind(List<TodoistItem> items) {
        long now = Calendar.getInstance().getTimeInMillis();
        long targetDif = Long.MAX_VALUE;
        NextReminderModel targetModel = null;

        for (ReminderAgent agent : reminderAgents) {
            NextReminderModel model = agent.getNextItemToRemind(items);

            if (model == null)
                continue;

            if (model.getTimeRemaining(now) < targetDif) {
                targetDif = model.getTimeRemaining(now);
                targetModel = model;
            }
        }

        return targetModel;
    }
}
