package com.university.unicornslayer.todoistextension.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.university.unicornslayer.todoistextension.AppUpdate.UpdateManager;
import com.university.unicornslayer.todoistextension.Permissions.PermissionHelper;
import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.ReminderManager.ReminderManager;
import com.university.unicornslayer.todoistextension.Requests.ITodoistItemsHandler;
import com.university.unicornslayer.todoistextension.Scheduling.ScheduleManager;
import com.university.unicornslayer.todoistextension.data.FileDataManager;
import com.university.unicornslayer.todoistextension.data.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.ui.settings.SettingsActivity;
import com.university.unicornslayer.todoistextension.ui.token_input.TokenInputActivity;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainMvpView {
    private static final int writeToExternalStorageRequestCode = 112;

    private SharedPrefsUtils sharedPrefsUtils;
    private ReminderManager reminderManager;
    private FileDataManager fileDataManager;
    private PermissionHelper permissionHelper;
    private UpdateManager updateManager;
    private Gson gson;

    private MainPresenter presenter;

    private TextView shortInfoView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefsUtils = new SharedPrefsUtils(this);
        fileDataManager = new FileDataManager(this);
        gson = new Gson();

        permissionHelper = new PermissionHelper(this, this);
        updateManager = new UpdateManager(this, permissionHelper);

        reminderManager = new ReminderManager(this);
        reminderManager.setVerbose(true);
        reminderManager.setTodoistItemsHandler(new ITodoistItemsHandler() {
            @Override
            public void onDone(List<TodoistItem> items) {
                displayTheNextItem();
            }
        });

        presenter = new MainPresenter(this);

        ScheduleManager scheduleManager = new ScheduleManager(this);
        scheduleManager.setRepeatingAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();

        if (sharedPrefsUtils.getToken() == null)
            gotoInputToken();

        displayTheNextItem();
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
                item.getDueDate(),
                Calendar.getInstance().getTimeInMillis(),
                0L,
                DateUtils.FORMAT_ABBREV_ALL);

            String itemContent = item.getTrimmedContent(sharedPrefsUtils.getMaxContentSizeForShortDisplay());
            String text = String.format("<b>%s</b> <i>%s</i>", Html.escapeHtml(itemContent), relativeTime);
            shortInfoView.setText(Html.fromHtml(text));
        }
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        @NonNull String[] permissions,
        @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == getResources().getInteger(R.integer.writeExternlPermissionRequestNb)) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            permissionHelper.onGetWriteExternalStoragePermissionDone(granted);
        }
    }

    public void onCheckForUpdates(View view) {
        updateManager.checkForUpdates();
    }

    public void onClickSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
