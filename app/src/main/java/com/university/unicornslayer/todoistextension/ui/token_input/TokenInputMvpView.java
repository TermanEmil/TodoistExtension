package com.university.unicornslayer.todoistextension.ui.token_input;

public interface TokenInputMvpView {
    void setTokenInputText(String token);

    String getTokenInputText();

    void showEmptyTokenInputError();

    void showRequestIsBeingProcessed();

    void disableUserInput();

    void showTokenIsInvalid();

    void showTokenIsSaved();

    void enableUserInput();

    void finish();

    void showTokenValidationFailedError();

    void showServiceUnavailableError();

    void hideRequestIsBeingProcessed();
}
