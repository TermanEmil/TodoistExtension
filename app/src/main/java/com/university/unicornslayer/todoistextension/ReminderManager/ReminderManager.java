package com.university.unicornslayer.todoistextension.ReminderManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;

import com.google.gson.Gson;
import com.university.unicornslayer.todoistextension.DataStuff.FileDataManager;
import com.university.unicornslayer.todoistextension.DataStuff.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.DataStuff.TodoistItem;
import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.Requests.ITodoistItemsHandler;
import com.university.unicornslayer.todoistextension.Requests.TodoistItemsRequestHelper;
import com.university.unicornslayer.todoistextension.Scheduling.ScheduleManager;
import com.university.unicornslayer.todoistextension.Utils.TodoistItemsUtils;

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
        remindBeforeDueAgent.createReminders(remindersData, items);
//        remindAtDueAgent.createReminders(remindersData, items);
//        remindAfterDueAgent.createReminders(remindersData, items);

//        remindersData.removeRedundantData(items, sharedPrefsUtils);
        fileDataManager.writeToFile(getRemindersDataFilename(), gson.toJson(remindersData));

        TodoistItem nextClosest = TodoistItemsUtils.getNextClosestItem(items);
        fileDataManager.writeToFile(getString(R.string.next_closest_item), gson.toJson(nextClosest));

        if (nextClosest != null) {
            long timeWhenToAlarm = getTimeUntilNextAlarm(nextClosest);
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

    private long getTimeUntilNextAlarm(TodoistItem nextClosestItem) {
        long now = Calendar.getInstance().getTimeInMillis();
        long due = nextClosestItem.getDueDate().getTime();
        long dif = due - now;

        if (dif <= sharedPrefsUtils.getRemindAtDue())
            return due + sharedPrefsUtils.getRemindAtDue() - 1000;

        long finalResult = due + sharedPrefsUtils.getRemindBeforeDue() - 1000;
        if (now + sharedPrefsUtils.getNetworkCheckInterval() > finalResult)
            return -1;
        else
            return finalResult;
    }
}
