package com.university.unicornslayer.todoistextension.utils.files;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

public class AppFileIOHelper implements FileIOHelper {
    private final Context context;

    @Inject
    public AppFileIOHelper(Context context) {
        this.context = context;
    }

    @Override
    public String readFromFile(String filename) throws IOException {
        InputStream inputStream = context.openFileInput(filename);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String receiveString;

        while ((receiveString = bufferedReader.readLine()) != null) {
            stringBuilder.append(receiveString);
        }

        inputStream.close();
        return stringBuilder.toString();
    }

    @Override
    public void writeToFile(String filename, String content) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        outputStream.write(content.getBytes());
        outputStream.close();
    }
}
