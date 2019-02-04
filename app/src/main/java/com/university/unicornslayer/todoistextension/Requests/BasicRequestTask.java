package com.university.unicornslayer.todoistextension.Requests;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BasicRequestTask extends AsyncTask<Void, Void, RequestResult> {
    private final String url;
    private final IRequestHandler handler;

    public BasicRequestTask(String url, IRequestHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    protected RequestResult doInBackground(Void... voids) {
        Request request = new Request.Builder()
            .url(url)
            .build();

        RequestResult requestResult = new RequestResult();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

        try {
            requestResult.response = okHttpClient.newCall(request).execute();
            requestResult.httpStatus = requestResult.response.code();
        } catch (IOException e) {
            e.printStackTrace();
            requestResult.httpStatus = HttpURLConnection.HTTP_UNAVAILABLE;
            requestResult.response = null;
        }

        return requestResult;
    }

    @Override
    protected void onPostExecute(RequestResult requestResult) {
        super.onPostExecute(requestResult);
        handler.onDone(requestResult);
    }
}
