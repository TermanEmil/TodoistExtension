package com.university.unicornslayer.todoistextension;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.university.unicornslayer.todoistextension.DataStuff.FileDataManager;
import com.university.unicornslayer.todoistextension.DataStuff.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.DataStuff.TodoistItem;
import com.university.unicornslayer.todoistextension.ReminderManager.ReminderManager;
import com.university.unicornslayer.todoistextension.Requests.ITodoistItemsHandler;
import com.university.unicornslayer.todoistextension.Scheduling.ScheduleManager;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SharedPrefsUtils sharedPrefsUtils;
    private ReminderManager reminderManager;
    private FileDataManager fileDataManager;
    private Gson gson;

    private TextView shortInfoView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        fileDataManager = new FileDataManager(this);
        gson = new Gson();

        reminderManager = new ReminderManager(this);
        reminderManager.setVerbose(true);
        reminderManager.setTodoistItemsHandler(new ITodoistItemsHandler() {
            @Override
            public void onDone(List<TodoistItem> items) {
                displayTheNextItem();
            }
        });

        ScheduleManager scheduleManager = new ScheduleManager(this);
        scheduleManager.setRepeatingAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPrefsUtils.getToken() == null)
            gotoInputToken();

        displayTheNextItem();
    }

    public void onChangeToken(View view) {
        gotoInputToken();
    }

    private void gotoInputToken() {
        Intent intent = new Intent(this, TokenInputActivity.class);
        startActivity(intent);
    }

    public void onCheckReminders(View view) {
        reminderManager.checkNotifications();
    }

    private void displayTheNextItem() {
        if (shortInfoView == null)
            shortInfoView = findViewById(R.id.shortInfo);

        String fileContent = fileDataManager.readFromFile(getString(R.string.next_closest_item));
        if (fileContent == null) {
            shortInfoView.setText(getString(R.string.msg_when_no_tasks_in_future));
            return;
        } else {
            TodoistItem item = gson.fromJson(fileContent, TodoistItem.class);
            if (item == null || !item.dueIsInFuture()) {
                shortInfoView.setText(getString(R.string.msg_when_no_tasks_in_future));
                return;
            }

            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                item.getDueDate().getTime(),
                Calendar.getInstance().getTimeInMillis(),
                0L,
                DateUtils.FORMAT_ABBREV_ALL);

            String itemContent = item.getTrimedContent(sharedPrefsUtils.getMaxContentSizeForShortDisplay());
            String text = String.format("<b>%s</b> <i>%s</i>", Html.escapeHtml(itemContent), relativeTime);
            shortInfoView.setText(Html.fromHtml(text));
        }
    }
}
