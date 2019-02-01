package com.university.unicornslayer.todoistextension.DataStuff;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TodoistItem {
    //Thu 31 Jan 2019 09:45:00 +0000
    private static final SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss Z");

    private JSONObject json;

    private boolean idIsSet = false;
    private int id;

    private boolean dueDateIsSet = false;
    private Date dueDate;

    private String content;

    public TodoistItem(JSONObject jsonObject) {
        json = jsonObject;
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

    public Date getDueDate() {
        if (!dueDateIsSet) {
            try {
                dueDate = format.parse(json.getString("due_date_utc"));
            } catch (ParseException e) {
                dueDate = null;
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
