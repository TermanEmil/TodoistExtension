package com.university.unicornslayer.todoistextension.utils.reminder.model;

public interface AtDuePrefsProvider {
    long getIntervalMin();

    long getIntervalMax();

    boolean produceSound();
}
