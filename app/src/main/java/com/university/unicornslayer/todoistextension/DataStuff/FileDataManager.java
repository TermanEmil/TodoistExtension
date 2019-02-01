package com.university.unicornslayer.todoistextension.DataStuff;

import android.content.Context;
import android.content.ContextWrapper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileDataManager extends ContextWrapper {
    public FileDataManager(Context context) {
        super(context);
    }

    public String readFromFile(String filename) {
        String result = null;

        InputStream inputStream;
        try {
            inputStream = openFileInput(filename);

            if (inputStream == null)
                return null;

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String receiveString;

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            result = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void writeToFile(String filename, String content) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
