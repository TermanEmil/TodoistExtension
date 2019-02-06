package com.university.unicornslayer.todoistextension.data.network;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.RawItemsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.Response;

public class AppApiHelper implements ApiHelper {
    private static final String url = "https://todoist.com/api/v7/sync";
    private final String token;

    public AppApiHelper(String token) {
        this.token = token;
    }

    @Override
    public void validateToken(final ValidateTokenListener listener) {
        AndroidNetworking
            .post(url)
            .addBodyParameter("token", token)
            .build()
            .getAsOkHttpResponse(new OkHttpResponseListener() {
                @Override
                public void onResponse(Response response) {
                    switch (response.code()) {
                        case HttpURLConnection.HTTP_OK:
                            listener.onResponse(true);
                            break;

                        case HttpURLConnection.HTTP_FORBIDDEN:
                            listener.onResponse(false);
                            break;

                        default:
                            listener.onError(response.code());
                    }
                }

                @Override
                public void onError(ANError anError) {
                    listener.onError(anError.getErrorCode());
                }
            });
    }

    @Override
    public void getAllItems(final GetAllItemsListener listener) {
        AndroidNetworking
            .post(url)
            .addBodyParameter("token", token)
            .addBodyParameter("resource_types", "[\"items\"]")
            .addBodyParameter("sync_token", "*")
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("items");
                        listener.onResponse(RawItemsUtils.extractItems(jsonArray));
                    } catch (JSONException e) {
                        listener.onError(-1);
                    }
                }

                @Override
                public void onError(ANError anError) {
                    listener.onError(anError.getErrorCode());
                }
            });
    }
}
