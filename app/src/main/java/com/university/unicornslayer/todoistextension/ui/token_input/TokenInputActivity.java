package com.university.unicornslayer.todoistextension.ui.token_input;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.data.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.data.network.AppApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.AppPrefHelper;
import com.university.unicornslayer.todoistextension.ui.base.BaseActivity;

public class TokenInputActivity extends BaseActivity implements TokenInputMvpView {
    private EditText tokenInput;
    private Button tokenSubmitBtn;

    private TokenInputPresenter presenter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_input);

        tokenInput = findViewById(R.id.tokenInputEditText);
        tokenSubmitBtn = findViewById(R.id.token_submit_btn);

        presenter = getDagger().getTokenInputPresenter();
        presenter.setView(this);
        presenter.onCreate();
    }

    public void submitToken(View view) {
        presenter.onSubmitTokenPressed();
    }

    private ProgressDialog buildValidationProgressDialog() {
        ProgressDialog spinner = new ProgressDialog(this);
        spinner.setMessage(getString(R.string.spinner_msg_token_validation));
        spinner.setTitle(getString(R.string.spinner_title_token_validation));
        spinner.setIndeterminate(false);
        spinner.setCancelable(true);

        spinner.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) { presenter.cancelTokenValidation(); }
        });

        return spinner;
    }

    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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
        if (progressDialog == null)
            progressDialog = buildValidationProgressDialog();

        progressDialog.show();
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
        if (progressDialog != null)
            progressDialog.hide();
    }
}