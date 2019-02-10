package com.university.unicornslayer.todoistextension.utils.reminder.model;

public interface RelativeToNowPrefsProvider {
    long getIntervalMin();

    long getIntervalMax();

    boolean produceSound();
}
