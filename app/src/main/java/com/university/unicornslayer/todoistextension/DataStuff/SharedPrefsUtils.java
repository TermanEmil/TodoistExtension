package com.university.unicornslayer.todoistextension.DataStuff;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.university.unicornslayer.todoistextension.R;

public class SharedPrefsUtils extends ContextWrapper {
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mSharedPreferencesEditor = null;

    public SharedPrefsUtils(Context context) {
        super(context);
    }

    public SharedPreferences getSharedPreferences() {
        if (mSharedPreferences == null)
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        return mSharedPreferences;
    }

    public SharedPreferences.Editor getSharedPreferencesEditor() {
        if (mSharedPreferencesEditor == null)
            mSharedPreferencesEditor = this.getSharedPreferences().edit();

        return mSharedPreferencesEditor;
    }

    public String getToken() {
        return this.getSharedPreferences().getString("token", null);
    }

    public void setToken(String token) {
        this.getSharedPreferencesEditor().putString("token", token);
    }

    public int getRemindBeforeDue() {
        return this.getSharedPreferences().getInt(
            "remindBeforeDue",
            getResources().getInteger(R.integer.default_mins_remind_before_due) * 1000 * 60);
    }

    public int getRemindAtDue() {
        return this.getSharedPreferences().getInt(
            "remindAtDue",
            getResources().getInteger(R.integer.default_mins_remind_at_due) * 1000 * 60);
    }

    // How much time due can be late before it's considered unfinished
    public int getDueCanBeLate() {
        return this.getSharedPreferences().getInt("dueCanBeLate", 1000 * 60 * 5);
    }

    public boolean getProduceSoundBeforeDue() {
        return this.getSharedPreferences().getBoolean(
            "produceSoundBeforeDue",
            getResources().getBoolean(R.bool.default_before_due_make_sound));
    }

    public boolean getProduceSoundAtDue() {
        return this.getSharedPreferences().getBoolean(
            "produceSoundAtDue",
            getResources().getBoolean(R.bool.default_at_due_make_sound));
    }

    public int getMaxNbOfRemindersToShowAfterDue() {
        return this.getSharedPreferences().getInt("maxNbOfRemindersToShowAfterDue", 5);
    }

    public int getIntervalRemindAfterDue() {
        return this.getSharedPreferences().getInt(
            "intervalRemindAfterDue",
            1000 * 60 * 60 * 24);
    }

    public boolean getDoRemindAboutUnfinishedTasks() {
        return this.getSharedPreferences().getBoolean(
            "doRemindAboutUnfinishedTasks",
            getResources().getBoolean(R.bool.default_do_remind_about_unfinished));
    }

    public int getMaxContentSizeForShortDisplay() {
        return this.getSharedPreferences().getInt(
            "maxContentSizeForShortDisplay",
            20);
    }

    public int getNetworkCheckInterval() {
        return this.getSharedPreferences().getInt(
            "networkCheckInterval",
            getResources().getInteger(R.integer.default_mins_network_check) * 1000 * 60);
    }

    public void commitAndWait(final IOnTaskDone onDoneHandler) {
        final ProgressDialog spinner = new ProgressDialog(this);
        spinner.setMessage("Saving, please wait");
        spinner.setTitle("Saving data");
        spinner.setIndeterminate(false);
        spinner.setCancelable(false);
        spinner.show();

        new CommitTask(this.getSharedPreferencesEditor(), new IOnTaskDone() {
            @Override
            public void onDone() {
                spinner.dismiss();

                if (onDoneHandler != null)
                    onDoneHandler.onDone();
            }
        }).execute();
    }
}

class CommitTask extends AsyncTask<Void, Void, Void> {

    private SharedPreferences.Editor mEditor;
    private IOnTaskDone mHandler;

    CommitTask(SharedPreferences.Editor editor, IOnTaskDone handler) {
        mEditor = editor;
        mHandler = handler;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mEditor.commit();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mHandler.onDone();
    }
}
