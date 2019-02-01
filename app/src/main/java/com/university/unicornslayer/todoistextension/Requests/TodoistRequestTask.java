package com.university.unicornslayer.todoistextension.Requests;

import org.json.JSONException;
import org.json.JSONObject;

public class TodoistRequestTask extends RequestTask {
    private static final String url = "https://todoist.com/api/v7/sync";
    private final String mToken;

    public TodoistRequestTask(String token, IRequestHandler handler) {
        super(url, handler);
        this.mToken = token;
    }

    @Override
    protected RequestResult executeRequest(JSONObject json) {
        try {
            json.put("token", mToken);
            json.put("sync_token", "*");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return super.executeRequest(json);
    }
}
