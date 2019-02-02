package com.university.unicornslayer.todoistextension.DataStuff;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class SharedPrefsUtils extends ContextWrapper {
    private static final String prefsName = "todoist_prefs";

    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mSharedPreferencesEditor = null;

    public SharedPrefsUtils(Context context) {
        super(context);
    }

    public SharedPreferences getSharedPreferences() {
        if (mSharedPreferences == null)
            mSharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE);

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

    public int getMinsRemindBeforeDue() {
        return this.getSharedPreferences().getInt("minsRemindBeforeDue", 20);
    }

    public int getSecRemindAtDue() {
        return this.getSharedPreferences().getInt("secRemindAtDue", 60);
    }

    public int getSecDueCanBeLate() {
        return this.getSharedPreferences().getInt("secDueCanBeLate", 60 * 5);
    }

    public boolean getProduceSoundBeforeDue() {
        return this.getSharedPreferences().getBoolean("produceSoundBeforeDue", false);
    }

    public boolean getProduceSoundAtDue() {
        return this.getSharedPreferences().getBoolean("produceSoundAtDue", true);
    }

    public int getMaxNbOfRemindersToShowAfterDue() {
        return this.getSharedPreferences().getInt("maxNbOfRemindersToShowAfterDue", 5);
    }

    public int getMilsIntervalRemindAfterDue() {
        return this.getSharedPreferences().getInt("milsIntervalRemindAfterDue", 1000 * 60 * 60 * 24);
    }

    public int getMaxContentSizeForShortDisplay() {
        return this.getSharedPreferences().getInt("maxContentSizeForShortDisplay", 20);
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
