package com.university.unicornslayer.todoistextension.ReminderManager;

import android.annotation.SuppressLint;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("UseSparseArrays")
public class RemindersData {
    public HashMap<Integer, Reminder> beforeDueReminders = new HashMap<>();
    public HashMap<Integer, Reminder> atDueReminders = new HashMap<>();
    public HashMap<Integer, Reminder> afterDueReminders = new HashMap<>();
}
