package com.university.unicornslayer.todoistextension.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.Scheduling.ScheduleManager;
import com.university.unicornslayer.todoistextension.data.local.AppLocalDataManager;
import com.university.unicornslayer.todoistextension.data.local.LocalDataManager;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.network.AppApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.AppTokenPrefHelper;
import com.university.unicornslayer.todoistextension.data.prefs.TokenPrefHelper;
import com.university.unicornslayer.todoistextension.ui.settings.SettingsActivity;
import com.university.unicornslayer.todoistextension.ui.token_input.TokenInputActivity;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.files.AppFileIOHelper;
import com.university.unicornslayer.todoistextension.utils.files.FileIOHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManager;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManagerBuilder;

public class MainActivity extends AppCompatActivity implements MainMvpView {
    private static final int writeToExternalStorageRequestCode = 112;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TokenPrefHelper tokenPrefHelper = new AppTokenPrefHelper(getApplicationContext());
        FileIOHelper fileIOHelper = new AppFileIOHelper(getApplicationContext());
        LocalDataManager localDataManager = new AppLocalDataManager(
            getString(R.string.reminders_data_filename),
            fileIOHelper);

        TodoistNotifHelper notifHelper = new TodoistNotifHelper(getApplicationContext());

        ReminderManager reminderManager = ReminderManagerBuilder.buildDefault(
            getApplicationContext(), notifHelper, localDataManager);

        ApiHelper apiHelper = new AppApiHelper();
        ScheduleManager scheduleManager = new ScheduleManager(this);

        presenter = new MainPresenter(this, tokenPrefHelper, reminderManager, apiHelper, scheduleManager);
        presenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void gotoInputTokenView() {
        Intent intent = new Intent(this, TokenInputActivity.class);
        startActivity(intent);
    }

    @Override
    public void showFailedToPostNotifications() {
        Toast.makeText(this, getString(R.string.error_failed_to_post_notifs), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showServiceUnavailableError() {
        Toast.makeText(this, getString(R.string.error_service_unavailable), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showFailedToGetItems() {
        Toast.makeText(this, getString(R.string.error_failed_to_get_items), Toast.LENGTH_LONG).show();
    }

    @Override
    public void gotoSettingsView() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void onCheckRemindersBtnPressed(View view) {
        presenter.onCheckRemindersBtnPressed();
    }

    private void displayTheNextItem() {
//        if (shortInfoView == null)
//            shortInfoView = findViewById(R.id.shortInfo);
//
//        String fileContent = fileDataManager.readFromFile(getString(R.string.next_closest_item));
//        if (fileContent == null) {
//            shortInfoView.setText(getString(R.string.msg_when_no_tasks_in_future));
//            return;
//        } else {
//            TodoistItem item = gson.fromJson(fileContent, TodoistItem.class);
//            if (item == null || !item.dueIsInFuture()) {
//                shortInfoView.setText(getString(R.string.msg_when_no_tasks_in_future));
//                return;
//            }
//
//            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
//                item.getDueDate(),
//                Calendar.getInstance().getTimeInMillis(),
//                0L,
//                DateUtils.FORMAT_ABBREV_ALL);
//
//            String itemContent = item.getTrimmedContent(sharedPrefsUtils.getMaxContentSizeForShortDisplay());
//            String text = String.format("<b>%s</b> <i>%s</i>", Html.escapeHtml(itemContent), relativeTime);
//            shortInfoView.setText(Html.fromHtml(text));
//        }
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
//            permissionHelper.onGetWriteExternalStoragePermissionDone(granted);
        }
    }

    public void onCheckForUpdates(View view) {
//        updateManager.checkForUpdates();
    }

    public void onClickSettings(View view) {
        presenter.onClickSettings();
    }
}
