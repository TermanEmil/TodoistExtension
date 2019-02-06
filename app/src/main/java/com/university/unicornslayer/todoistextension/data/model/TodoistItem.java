package com.university.unicornslayer.todoistextension.data;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressLint("SimpleDateFormat")
public class TodoistItem {
    //Thu 31 Jan 2019 09:45:00 +0000
    private static final SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss Z");

    private JSONObject json;

    private boolean idIsSet = false;
    private int id;

    private boolean dueDateIsSet = false;
    private long dueDate;

    private String content;

    public TodoistItem(JSONObject jsonObject) {
        json = jsonObject;
    }

    public String getTrimedContent(int maxSize) {
        if (getContent().length() <= maxSize)
            return getContent();

        return getContent().substring(0, maxSize) + "...";
    }

    public boolean dueIsInFuture(long now) {
        return getDueDate() > now;
    }

    public boolean dueIsInFuture() {
        return dueIsInFuture(Calendar.getInstance().getTimeInMillis());
    }

    public int getId() {
        if (!idIsSet) {
            try {
                id = json.getInt("id");
            } catch (JSONException e) {
                onException(e);
            }

            idIsSet = true;
        }

        return id;
    }

    public String getContent() {
        if (content == null) {
            try {
                content = json.getString("content");
            } catch (JSONException e) {
                onException(e);
            }
        }

        return content;
    }

    public long getDueDate() {
        if (!dueDateIsSet) {
            try {
                dueDate = format.parse(json.getString("due_date_utc")).getTime();
            } catch (ParseException e) {
                dueDate = -1;
            } catch (JSONException e) {
                onException(e);
            }

            dueDateIsSet = true;
        }

        return dueDate;
    }

    private void onException(JSONException e) {
        e.printStackTrace();
        System.exit(1);
    }
}
