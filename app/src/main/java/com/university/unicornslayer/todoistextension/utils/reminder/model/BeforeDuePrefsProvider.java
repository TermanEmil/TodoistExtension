package com.university.unicornslayer.todoistextension.utils.reminder.model;

public interface BeforeDuePrefsProvider {
    long getRemindBeforeDueMin();

    long getRemindBeforeDueMax();

    boolean getProduceSoundBeforeDue();
}
