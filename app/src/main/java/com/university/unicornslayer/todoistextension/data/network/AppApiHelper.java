package com.university.unicornslayer.todoistextension.data.network;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.university.unicornslayer.todoistextension.utils.todoist_common.TodoistItemsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import javax.inject.Inject;

import okhttp3.Response;

public class AppApiHelper implements ApiHelper {
    private static final String url = "https://todoist.com/api/v7/sync";

    private static final String tokenValidationTag = "token-validation";
    private static final String getAllItemsTag = "get-all-items";

    private String token = null;

    @Inject
    public AppApiHelper() {
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void validateToken(final TokenValidationListener listener) {
        AndroidNetworking
            .post(url)
            .addBodyParameter("token", token)
            .setTag(tokenValidationTag)
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
    public void cancelTokenValiation() {
        AndroidNetworking.cancel(tokenValidationTag);
    }

    @Override
    public void getAllItems(final GetAllItemsListener listener) {
        if (token == null) {
            Log.e("AppApiHelper", "Token was not set");
            return;
        }

        AndroidNetworking
            .post(url)
            .addBodyParameter("token", token)
            .addBodyParameter("resource_types", "[\"items\"]")
            .addBodyParameter("sync_token", "*")
            .setTag(getAllItemsTag)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("items");
                        listener.onResponse(TodoistItemsUtils.extractItems(jsonArray));
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

    @Override
    public void cancelGetAllItems() {
        AndroidNetworking.cancel(getAllItemsTag);
    }
}
