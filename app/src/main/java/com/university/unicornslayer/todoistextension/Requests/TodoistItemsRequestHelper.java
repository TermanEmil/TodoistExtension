package com.university.unicornslayer.todoistextension.Requests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.widget.Toast;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.RawItemsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.Response;

public class TodoistItemsRequestHelper extends ContextWrapper {
    private final String token;
    private final boolean toastErrors;
    private final ITodoistItemsHandler handler;

    public TodoistItemsRequestHelper(
        Context context,
        String token,
        boolean toastErrors,
        ITodoistItemsHandler handler
    ) {
        super(context);
        this.token = token;
        this.toastErrors = toastErrors;
        this.handler = handler;
    }

    public void executeTask(JSONObject jsonData) {
        TodoistRequestTask task = new TodoistRequestTask(token, new IRequestHandler() {
            @Override
            public void onDone(RequestResult result) {
                onRequestDone(result);
            }
        });

        try {
            jsonData.put("resource_types", new JSONArray("[\"items\"]"));
        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(1);
        }

        task.execute(jsonData);
    }

    @SuppressLint("DefaultLocale")
    private void onRequestDone(RequestResult result) {
        switch (result.httpStatus) {
            case HttpURLConnection.HTTP_OK:
                onSuccess(result.response);
                break;

            case HttpURLConnection.HTTP_FORBIDDEN:
                showError("Invalid token");
                break;

            case HttpURLConnection.HTTP_UNAVAILABLE:
                showError("Service unavailable");
                break;

            default:
                showError(String.format("Failed with http %d", result.httpStatus));
                break;
        }
    }

    private void onSuccess(Response response) {
        List<TodoistItem> items = null;
        try {
            JSONObject bodyJson = new JSONObject(response.body().string());
            items = RawItemsUtils.extractItems(bodyJson.getJSONArray("items"));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load the Todoist items");
        }

        handler.onDone(items);
    }

    private void showError(String msg) {
        if (toastErrors)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
