package com.university.unicornslayer.todoistextension.utils.reminder.model;

public interface BeforeDuePrefsProvider {
    long getIntervalMin();

    long getIntervalMax();

    boolean produceSound();
}
