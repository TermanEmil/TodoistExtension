package com.university.unicornslayer.todoistextension.data.local;

import com.google.gson.Gson;
import com.university.unicornslayer.todoistextension.utils.files.FileIOHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppLocalDataManager implements LocalDataManager {
    private final String fileName;
    private final FileIOHelper fileIOHelper;
    private final Gson gson;

    private Map<String, Map<Integer, Reminder>> data;
    private boolean dataHasChanged;

    public AppLocalDataManager(String fileName, FileIOHelper fileIOHelper) {
        this.fileName = fileName;
        this.fileIOHelper = fileIOHelper;
        this.gson = new Gson();
    }

    @Override
    public void loadData() throws IOException {
        String dataStr;
        try {
            dataStr = fileIOHelper.readFromFile(fileName);
        } catch (FileNotFoundException e) {
            dataStr = null;
        }

        if (dataStr == null) {
            data = new HashMap<>();
            dataHasChanged = true;
        }  else {
            data = gson.fromJson(dataStr, data.getClass());
            dataHasChanged = false;
        }
    }

    @Override
    public void saveData() throws IOException {
        if (data == null) {
            data = new HashMap<>();
            dataHasChanged = true;
        }

        if (dataHasChanged) {
            fileIOHelper.writeToFile(fileName, gson.toJson(data));
            dataHasChanged = false;
        }
    }

    @Override
    public Map<Integer, Reminder> getDataWithKey(String key) {
        if (!data.containsKey(key))
            return null;
        else
            return data.get(key);
    }

    @Override
    public void setDataWithKey(String key, Map<Integer, Reminder> newData) {
        data.put(key, newData);
        dataHasChanged = true;
    }
}
