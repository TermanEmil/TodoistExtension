package com.university.unicornslayer.todoistextension.Requests;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.Response;

public class ResponseReaderTask extends AsyncTask<Void, Void, String> {
    private final Response response;
    private final IObjectHandler handler;

    public ResponseReaderTask(Response response, IObjectHandler handler) {
        this.response = response;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(Void... voids) {
        if (response.body() == null)
            return null;

        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        handler.onDone(s);
    }
}
