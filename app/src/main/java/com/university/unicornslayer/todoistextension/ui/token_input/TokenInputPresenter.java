package com.university.unicornslayer.todoistextension.ui.token_input;

import com.university.unicornslayer.todoistextension.data.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;

import java.net.HttpURLConnection;

public class TokenInputPresenter {
    private final TokenInputMvpView view;
    private final SharedPrefsUtils sharedPrefsUtils;
    private final ApiHelper apiHelper;

    private String tokenBeingValidated;

    public TokenInputPresenter(
        TokenInputMvpView view,
        SharedPrefsUtils sharedPrefsUtils,
        ApiHelper apiHelper
    ) {
        this.view = view;
        this.sharedPrefsUtils = sharedPrefsUtils;
        this.apiHelper = apiHelper;
    }

    public void onCreate() {
        String token = sharedPrefsUtils.getToken();
        if (token != null)
            view.setTokenInputText(token);
    }

    public void onSubmitTokenPressed() {
        view.disableUserInput();

        tokenBeingValidated = view.getTokenInputText();
        if (tokenBeingValidated.isEmpty()) {
            view.showEmptyTokenInputError();
            view.enableUserInput();
            return;
        }

        view.showRequestIsBeingProcessed();

        apiHelper.setToken(tokenBeingValidated);
        apiHelper.validateToken(new ApiHelper.TokenValidationListener() {
            @Override
            public void onResponse(boolean isValid) { onTokenValidationDone(isValid); }

            @Override
            public void onError(int errorCode) { onTokenValidationError(errorCode); }
        });
    }

    public void cancelTokenValidation() {
        apiHelper.cancelTokenValiation();
    }

    private void onTokenValidationDone(boolean tokenIsValid) {
        if (!tokenIsValid) {
            view.showTokenIsInvalid();
            view.enableUserInput();
            view.hideRequestIsBeingProcessed();
            return;
        }

        sharedPrefsUtils.setToken(tokenBeingValidated);
        view.showTokenIsSaved();
        view.enableUserInput();
        view.hideRequestIsBeingProcessed();
        view.finish();
    }

    private void onTokenValidationError(int errorCode) {
        switch (errorCode) {
            case HttpURLConnection.HTTP_UNAVAILABLE:
                view.showServiceUnavailableError();
                break;

            default:
                view.showTokenValidationFailedError();
                break;
        }

        view.enableUserInput();
        view.hideRequestIsBeingProcessed();
    }
}
