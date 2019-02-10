package com.university.unicornslayer.todoistextension.data.local;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.university.unicornslayer.todoistextension.utils.files.FileIOHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

public class AppLocalDataManager implements LocalDataManager {
    private final String fileName;
    private final FileIOHelper fileIOHelper;
    private final Gson gson;
    private String loadedDataStr = null;

    private Map<String, Map<Integer, Reminder>> data;

    @Inject
    public AppLocalDataManager(
        @Named("localDataManagerFileName") String fileName,
        FileIOHelper fileIOHelper
    ) {
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
        }  else {
            data = gson.fromJson(dataStr, new TypeToken<Map<String, Map<Integer, Reminder>>>(){}.getType());
        }

        loadedDataStr = dataStr;
    }

    @Override
    public void saveData() throws IOException {
        if (data == null) {
            data = new HashMap<>();
        }

        String dataToJson = gson.toJson(data);
        if (loadedDataStr == null || !loadedDataStr.equals(dataToJson))
            fileIOHelper.writeToFile(fileName, dataToJson);
    }

    @Override
    public Map<Integer, Reminder> getDataFromKey(String key) {
        if (!data.containsKey(key)) {
            data.put(key, new HashMap<Integer, Reminder>());
        }

        return data.get(key);
    }
}
