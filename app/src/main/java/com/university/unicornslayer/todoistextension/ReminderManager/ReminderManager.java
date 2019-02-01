package com.university.unicornslayer.todoistextension.ReminderManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;

import com.google.gson.Gson;
import com.university.unicornslayer.todoistextension.DataLayer.FileDataManager;
import com.university.unicornslayer.todoistextension.DataLayer.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.DataLayer.TodoistItem;
import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.Requests.ITodoistItemsHandler;
import com.university.unicornslayer.todoistextension.Requests.TodoistItemsRequestHelper;
import com.university.unicornslayer.todoistextension.Utils.TodoistItemsUtils;

import org.json.JSONObject;

import java.util.List;

@SuppressLint("SimpleDateFormat")
public class ReminderManager extends ContextWrapper {
    private final SharedPrefsUtils sharedPrefsUtils;
    private final FileDataManager fileDataManager;
    private final Gson gson;

    private final RemindMinsBeforeDueAgent minsBeforeDueAgent;

    public ReminderManager(Context context) {
        super(context);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        fileDataManager = new FileDataManager(this);
        gson = new Gson();
        minsBeforeDueAgent = new RemindMinsBeforeDueAgent(this);
    }

    public int checkNotifications() {
        if (sharedPrefsUtils.getToken() == null)
            return -1;

        ITodoistItemsHandler handler = new ITodoistItemsHandler() {
            @Override
            public void onDone(List<TodoistItem> items) {
                checkNotifications(items);
            }
        };

        TodoistItemsRequestHelper helper = new TodoistItemsRequestHelper(
                this,
                sharedPrefsUtils.getToken(),
                false,
                handler);

        helper.executeTask(new JSONObject());
        return 0;
    }

    public void checkNotifications(List<TodoistItem> items) {
        items = TodoistItemsUtils.extractWithDueDate(items);

        RemindersData remindersData = getRemindersData();
        minsBeforeDueAgent.createReminders(remindersData, items);

        fileDataManager.writeToFile(getRemindersDataFilename(), gson.toJson(remindersData));
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
}
