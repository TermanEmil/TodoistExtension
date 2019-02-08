package com.university.unicornslayer.todoistextension.ui.token_input;

import com.university.unicornslayer.todoistextension.data.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.TokenPrefHelper;

import java.net.HttpURLConnection;

public class TokenInputPresenter {
    private final TokenInputMvpView view;
    private final TokenPrefHelper tokenPrefHelper;
    private final ApiHelper apiHelper;

    private String tokenBeingValidated;

    public TokenInputPresenter(
        TokenInputMvpView view,
        TokenPrefHelper tokenPrefHelper,
        ApiHelper apiHelper
    ) {
        this.view = view;
        this.tokenPrefHelper = tokenPrefHelper;
        this.apiHelper = apiHelper;
    }

    public void onCreate() {
        String token = tokenPrefHelper.getToken();
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
        view.enableUserInput();
        view.hideRequestIsBeingProcessed();
    }

    private void onTokenValidationDone(boolean tokenIsValid) {
        if (!tokenIsValid) {
            view.showTokenIsInvalid();
            view.enableUserInput();
            view.hideRequestIsBeingProcessed();
            return;
        }

        tokenPrefHelper.setToken(tokenBeingValidated);
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
