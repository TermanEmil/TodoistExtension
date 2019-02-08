package com.university.unicornslayer.todoistextension.utils.files;

import java.io.IOException;

public interface FileIOHelper {
    String readFromFile(String filename) throws IOException;

    void writeToFile(String filename, String content) throws IOException;
}
