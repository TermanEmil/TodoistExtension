package com.university.unicornslayer.todoistextension.utils.alarms;

public interface ScheduleManager {
    void setRepeatingAlarm();

    void setExactAlarm(long localTargetTime);
}
