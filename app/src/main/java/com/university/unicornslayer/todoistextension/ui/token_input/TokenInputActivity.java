package com.university.unicornslayer.todoistextension.ui.token_input;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.university.unicornslayer.todoistextension.data.IOnTaskDone;
import com.university.unicornslayer.todoistextension.data.SharedPrefsUtils;
import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.Requests.IRequestHandler;
import com.university.unicornslayer.todoistextension.Requests.RequestResult;
import com.university.unicornslayer.todoistextension.Requests.TodoistRequestTask;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class TokenInputActivity extends AppCompatActivity {
    private EditText mTokenInput = null;
    private SharedPrefsUtils mSharedPrefsUtils;
    private TodoistRequestTask mValidationTask;
    private ProgressDialog mProgressSpinner;

    private String mTokenBeingValidated = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_input);

        mTokenInput = findViewById(R.id.tokenInputEditText);
        mSharedPrefsUtils = new SharedPrefsUtils(this);

        String token = mSharedPrefsUtils.getToken();
        if (token != null)
            mTokenInput.setText(token);
    }

    public void submitToken(View view) {
        mTokenBeingValidated = mTokenInput.getText().toString();
        if (mTokenBeingValidated.isEmpty()) {
            showError("Token can't be empty");
            return;
        }

        mValidationTask = new TodoistRequestTask(mTokenBeingValidated, new IRequestHandler() {
            @Override
            public void onDone(RequestResult result) {
                onValidationRequestDone(result);
            }
        });

        mProgressSpinner = buildValidationProgressDialog();
        mProgressSpinner.show();

        mValidationTask.execute(new JSONObject());
    }

    @SuppressLint("DefaultLocale")
    private void onValidationRequestDone(RequestResult result) {
        if (mProgressSpinner != null) {
            mProgressSpinner.dismiss();
            mProgressSpinner = null;
        }

        switch (result.httpStatus) {
            case HttpURLConnection.HTTP_OK:
                saveToken(mTokenBeingValidated);
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

    private void saveToken(String token) {
        mSharedPrefsUtils.setToken(token);
        mSharedPrefsUtils.commitAndWait(new IOnTaskDone() {
            @Override
            public void onDone() {
                Toast.makeText(TokenInputActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showError(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    private ProgressDialog buildValidationProgressDialog() {
        ProgressDialog spinner = new ProgressDialog(this);
        spinner.setMessage("Validating...");
        spinner.setTitle("Token validation");
        spinner.setIndeterminate(false);
        spinner.setCancelable(true);

        spinner.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mValidationTask != null)
                    mValidationTask.cancel(true);
            }
        });

        return spinner;
    }
}