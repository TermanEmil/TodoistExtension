package com.university.unicornslayer.todoistextension.ReminderManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;

import com.google.gson.Gson;
import com.university.unicornslayer.todoistextension.data.FileDataManager;
import com.university.unicornslayer.todoistextension.data.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.Requests.ITodoistItemsHandler;
import com.university.unicornslayer.todoistextension.Requests.TodoistItemsRequestHelper;
import com.university.unicornslayer.todoistextension.Scheduling.ScheduleManager;
import com.university.unicornslayer.todoistextension.utils.TodoistItemsUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class ReminderManager extends ContextWrapper {
    private final SharedPrefsUtils sharedPrefsUtils;
    private final FileDataManager fileDataManager;
    private final ScheduleManager scheduleManager;
    private final Gson gson;

    private final RemindBeforeDueAgent remindBeforeDueAgent;
    private final RemindAtDueAgent remindAtDueAgent;
    private final RemindAfterDueAgent remindAfterDueAgent;

    private boolean verbose;
    private ITodoistItemsHandler todoistItemsHandler = null;

    public ReminderManager(Context context) {
        super(context);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        fileDataManager = new FileDataManager(this);
        scheduleManager = new ScheduleManager(this);
        gson = new Gson();

        remindBeforeDueAgent = new RemindBeforeDueAgent(this);
        remindAtDueAgent = new RemindAtDueAgent(this);
        remindAfterDueAgent = new RemindAfterDueAgent(this);
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setTodoistItemsHandler(ITodoistItemsHandler todoistItemsHandler) {
        this.todoistItemsHandler = todoistItemsHandler;
    }

    public int checkNotifications() {
        if (sharedPrefsUtils.getToken() == null)
            return -1;

        ITodoistItemsHandler handler = new ITodoistItemsHandler() {
            @Override
            public void onDone(List<TodoistItem> items) {
                checkNotifications(items);
                if (todoistItemsHandler != null)
                    todoistItemsHandler.onDone(items);
            }
        };

        TodoistItemsRequestHelper helper = new TodoistItemsRequestHelper(
                this,
                sharedPrefsUtils.getToken(),
                verbose,
                handler);

        helper.executeTask(new JSONObject());
        return 0;
    }

    public void checkNotifications(List<TodoistItem> items) {
        items = TodoistItemsUtils.extractWithDueDate(items);

        RemindersData remindersData = getRemindersData();
        if (sharedPrefsUtils.getRemindBeforeDue() > 0)
            remindBeforeDueAgent.createReminders(remindersData, items);

        if (sharedPrefsUtils.getRemindAtDue() > 0)
            remindAtDueAgent.createReminders(remindersData, items);

        if (sharedPrefsUtils.getDoRemindAboutUnfinishedTasks())
            remindAfterDueAgent.createReminders(remindersData, items);

        remindersData.removeRedundantData(items, sharedPrefsUtils);
        fileDataManager.writeToFile(getRemindersDataFilename(), gson.toJson(remindersData));

        TodoistItem nextClosest = TodoistItemsUtils.getNextClosestItem(items);
        fileDataManager.writeToFile(getString(R.string.next_closest_item), gson.toJson(nextClosest));

        if (nextClosest != null) {
            long timeWhenToAlarm = getTimeForNextAlarm(remindersData, items);
            if (timeWhenToAlarm != -1)
                scheduleManager.setExactAlarm(timeWhenToAlarm);
        }
    }

    private RemindersData getRemindersData() {
        String fileContent = fileDataManager.readFromFile(getRemindersDataFilename());

        if (fileContent == null)
            return new RemindersData();
        else
            return gson.fromJson(fileContent, RemindersData.class);
    }

    private String getRemindersDataFilename() {
        return getString(R.string.reminders_data_filename);
    }

    private long getTimeForNextAlarm(RemindersData remindersData, List<TodoistItem> items) {
        long now = Calendar.getInstance().getTimeInMillis();
        long atDue = sharedPrefsUtils.getRemindAtDue();
        long beforeDue = sharedPrefsUtils.getRemindBeforeDue();
        long targetDif = Long.MAX_VALUE;

        for (TodoistItem item : items) {
            if (item.getDueDate() <= now || remindersData.atDueReminders.containsKey(item.getId()))
                continue;

            long atDueDif = item.getDueDate() - (now + atDue);
            long beforeDueDif = item.getDueDate() - (now + beforeDue);

            if (atDue < 0 || atDueDif < 0) atDueDif = Long.MAX_VALUE;
            if (beforeDue < 0 || beforeDueDif < 0) beforeDueDif = Long.MAX_VALUE;

            long dif = Math.min(atDueDif, beforeDueDif);
            if (dif < targetDif)
                targetDif = dif;
        }

        if (targetDif == Long.MAX_VALUE)
            return -1;

        // if (atDue < 0 && beforeDue < 0) == always false
        long finalDif;
        if (atDue >= 0 && targetDif < atDue)
            finalDif = targetDif;
        else if (beforeDue >= 0 && targetDif < beforeDue)
            finalDif = targetDif - Math.min(atDue, 0);
        else
            finalDif = targetDif - Math.min(beforeDue, 0);

        int networkCheckInterval = sharedPrefsUtils.getNetworkCheckInterval();
        if (networkCheckInterval > 0 && finalDif > networkCheckInterval)
            return -1;

        return now + finalDif;
    }
}
