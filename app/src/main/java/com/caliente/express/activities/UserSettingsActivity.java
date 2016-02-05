package com.caliente.express.activities;

import com.caliente.express.api.requests.UpdateUserRequest;
import com.caliente.express.api.responses.UserResponse;
import com.caliente.express.storage.LocalStorage;
import com.caliente.express.tasks.Callback;
import com.caliente.express.tasks.GetUserAsyncTask;
import com.caliente.express.tasks.IApiResponseCallback;
import com.caliente.express.tasks.UpdateUserAsyncTask;
import com.caliente.express.util.AlertUtil;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import com.caliente.express.R;
import com.caliente.express.util.FormValidationElement;
import com.caliente.express.util.FormValidationManager;
import com.caliente.express.util.PermissionUtil;
import com.caliente.express.util.StringUtil;
import com.caliente.express.util.ValidationCallback;
import com.caliente.express.util.ValidationUtil;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Form for editing user settings/properties.
 */
public class UserSettingsActivity extends ActivityBase {
    private static final String LogTag = "UserSettingsActivity";

    private EditText targetCaloriesText;
    private String permissionLevel;
    private FormValidationManager formValidationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentResId(R.layout.activity_user_settings);
        setAppBarTitle("User Settings");
        super.onCreate(savedInstanceState);

        targetCaloriesText = (EditText)findViewById(R.id.target_calories_text);

        final Button updateButton = (Button)findViewById(R.id.update_button);
        final Button cancelButton = (Button)findViewById(R.id.cancel_button);
        final TextView caloriesLabel = (TextView)findViewById(R.id.calories_label);

        //cancel button action
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit(RESULT_CANCELED);
            }
        });

        formValidationManager = new FormValidationManager();
        formValidationManager.setSubmitButton(updateButton);

        //updte button action
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidationManager.validateForm(true, true)) {
                    final UpdateUserAsyncTask updateUserAsyncTask = new UpdateUserAsyncTask(UserSettingsActivity.this, LocalStorage.getCurrentTargetUserId(), new UpdateUserCallback());
                    UpdateUserRequest request = new UpdateUserRequest();
                    request.setPermissionLevel(permissionLevel);
                    updateUserAsyncTask.execute(request);
                }
            }
        });

        //validation for calories
        formValidationManager.addElement(new FormValidationElement(
                targetCaloriesText,
                caloriesLabel,
                true,
                new ValidationCallback() {
                    @Override
                    public String validate(EditText editText) {
                        return "";
                    }

                    @Override
                    public void setErrorState(EditText editText, String errorMessage, TextView errorLabel) {
                        super.setErrorState(editText, errorMessage, errorLabel);
                        if (StringUtil.isNullOrEmpty(errorMessage)) {
                            if (errorLabel != null) {
                                errorLabel.setText("target calories / day");
                                errorLabel.setTextColor(ContextCompat.getColor(UserSettingsActivity.this, R.color.colorPrimaryDark));
                            }
                        } else
                            errorLabel.setTextColor(ContextCompat.getColor(UserSettingsActivity.this, R.color.colorRed));
                    }
                }
        ));

        formValidationManager.validateForm(false, true);

        //get current user record
        GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask(this, new GetUserCallback());
        getUserAsyncTask.execute(LocalStorage.getCurrentTargetUserId());
    }


    private class UpdateUserCallback implements IApiResponseCallback<UserResponse>
    {
        @Override
        public void onFinished(UserResponse response) {

            Log.i(LogTag, "Update user finished.");

            if (response.isSuccessful())
            {
                LocalStorage.setCurrentTargetUser(response.getUser());
                exit(RESULT_OK);
            }
            else
            {
                AlertUtil.showAlert(
                        UserSettingsActivity.this,
                        "Update User Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Update user was unsuccessful."
                );
            }
        }
    }

    private class GetUserCallback implements IApiResponseCallback<UserResponse>
    {
        @Override
        public void onFinished(UserResponse response) {

            Log.i(LogTag, "Get user finished.");

            if (response.isSuccessful())
            {
                permissionLevel = response.getUser().getPermissionLevel();

                targetCaloriesText.setSelection(targetCaloriesText.getText().length(), targetCaloriesText.getText().length());

                if (!PermissionUtil.currentUserCanEditTarget())
                    formValidationManager.disableForm();
            } else {
                AlertUtil.showAlert(
                        UserSettingsActivity.this,
                        "Get User Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Get user was unsuccessful.",
                        new Callback() {
                            @Override
                            public void execute() {
                                exit(RESULT_CANCELED);
                            }
                        }
                );
            }
        }
    }

    @Override
    protected void exit() {
        super.exit();
        overridePendingTransitionVerticalExit();
    }
}
