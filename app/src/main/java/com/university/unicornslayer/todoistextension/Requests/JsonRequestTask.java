package com.university.unicornslayer.todoistextension.Requests;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsonRequestTask extends AsyncTask<JSONObject, Void, RequestResult> {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private String mUrl;
    private IRequestHandler mRequestHandler;

    private OkHttpClient mOkHttpClient = new OkHttpClient();

    public JsonRequestTask(String url, IRequestHandler handler) {
        mUrl = url;
        mRequestHandler = handler;
    }

    @Override
    protected RequestResult doInBackground(JSONObject... data) {
        RequestResult result = executeRequest(data[0]);
        return result;
    }

    protected RequestResult executeRequest(JSONObject json) {
        RequestBody requestBody = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(mUrl)
                .post(requestBody)
                .build();

        RequestResult requestResult = new RequestResult();
        try {
            requestResult.response = mOkHttpClient.newCall(request).execute();
            requestResult.httpStatus = requestResult.response.code();
        } catch (IOException e) {
            e.printStackTrace();
            requestResult.httpStatus = HttpURLConnection.HTTP_UNAVAILABLE;
            requestResult.response = null;
        }

        return requestResult;
    }

    @Override
    protected void onPostExecute(RequestResult result) {
        super.onPostExecute(result);
        mRequestHandler.onDone(result);
    }
}
