package com.university.unicornslayer.todoistextension.data.network;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;

import java.util.List;

public interface ApiHelper {
    void cancelGetAllItems();

    interface GetAllItemsListener {
        void onResponse(List<TodoistItem> items);
        void onError(int errorCode);
    }

    interface TokenValidationListener {
        void onResponse(boolean isValid);
        void onError(int errorCode);
    }

    void setToken(String token);

    void validateToken(TokenValidationListener listener);

    void cancelTokenValiation();

    void getAllItems(GetAllItemsListener listener);
}
