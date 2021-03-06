package com.university.unicornslayer.todoistextension.ui.token_input;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TokenInputActivity extends BaseActivity implements TokenInputMvpView {
    @BindView(R.id.tokenInputEditText) EditText tokenInput;
    @BindView(R.id.token_submit_btn) Button tokenSubmitBtn;

    private TokenInputPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_input);
        ButterKnife.bind(this);

        presenter = getDagger().getTokenInputPresenter();
        presenter.setView(this);
        presenter.onCreate();

        setupActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableUserInput();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitToken(View view) {
        presenter.onSubmitTokenPressed();
    }

    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setTokenInputText(String token) {
        tokenInput.setText(token);
    }

    @Override
    public String getTokenInputText() {
        return tokenInput.getText().toString();
    }

    @Override
    public void showEmptyTokenInputError() {
        tokenInput.setError(getString(R.string.error_token_cant_be_empty));
    }

    @Override
    public void showRequestIsBeingProcessed() {
        showProgressDialog(
            getString(R.string.spinner_title_token_validation),
            getString(R.string.spinner_msg_token_validation),
            dialog -> presenter.cancelTokenValidation()
        );
    }

    @Override
    public void disableUserInput() {
        tokenSubmitBtn.setEnabled(false);
    }

    @Override
    public void showTokenIsInvalid() {
        tokenInput.setError(getString(R.string.error_token_is_invalid));
    }

    @Override
    public void showTokenIsSaved() {
        toastMsg(getString(R.string.token_saved_msg));
    }

    @Override
    public void enableUserInput() {
        tokenSubmitBtn.setEnabled(true);
    }

    @Override
    public void showTokenValidationFailedError() {
        toastMsg(getString(R.string.error_token_validation_failed));
    }

    @Override
    public void showServiceUnavailableError() {
        toastMsg(getString(R.string.error_service_unavailable));
    }

    @Override
    public void hideRequestIsBeingProcessed() {
        dismissProgressDialog();
    }
}