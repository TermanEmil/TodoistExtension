package com.university.unicornslayer.todoistextension.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeHelper {
    public static Date utcToLocal(Date date) {
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getRawOffset());
    }
}
