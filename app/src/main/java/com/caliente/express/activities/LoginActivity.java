package com.caliente.express.activities;

import com.caliente.express.api.models.ErrorInfo;
import com.caliente.express.api.responses.UserResponse;
import com.caliente.express.storage.LocalStorage;
import com.caliente.express.tasks.LoginAsyncTask;
import com.caliente.express.api.requests.LoginRequest;

import com.caliente.express.tasks.IApiResponseCallback;
import com.caliente.express.util.AlertUtil;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.caliente.express.R;
import com.caliente.express.util.FormValidationElement;
import com.caliente.express.util.FormValidationManager;
import com.caliente.express.util.ValidationCallback;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Login screen
 */
public class LoginActivity extends ActivityBase {

    private static final String LogTag = "LoginActivity";

    private EditText usernameText;
    private EditText passwordText;
    private TextView usernameLabel;
    private TextView passwordLabel;
    private CheckBox rememberPasswordCheckbox;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentResId(R.layout.activity_login);
        setAppBarTitle("Login");
        setShowMenu(false);
        super.onCreate(savedInstanceState);

        //get widgets
        usernameText = (EditText) findViewById(R.id.username_text);
        passwordText = (EditText) findViewById(R.id.password_text);
        rememberPasswordCheckbox = (CheckBox)findViewById(R.id.remember_password_checkbox);
        usernameLabel = (TextView)findViewById(R.id.username_label);
        passwordLabel = (TextView)findViewById(R.id.password_label);

        final Button loginButton = (Button) findViewById(R.id.login_button);
        final TextView signupTextLink = (TextView) findViewById(R.id.signup_text_link);

        //restore saved username/Password
        usernameText.setText(LocalStorage.getUsername());
        rememberPasswordCheckbox.setChecked(LocalStorage.getRememberPassword());
        if (rememberPasswordCheckbox.isChecked())
            passwordText.setText(LocalStorage.getPassword());

        //auto login (for testing)
        //autoLogin();

        //login action
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                usernameLabel.setText("");
                passwordLabel.setText("");
                LoginActivity.this.setProgressActivity(true);
                doLogin();
            }
        });

        //signup action
        signupTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //form validation
        final FormValidationManager formValidationManager = new FormValidationManager();
        formValidationManager.setSubmitButton(loginButton);

        //validation for username text
        formValidationManager.addElement(new FormValidationElement(
                usernameText,
                null,
                true,
                new ValidationCallback() {
                    @Override
                    public String validate(EditText editText) {
                        return (editText.getText().length() > 0 ? "" : "username required");
                    }
                }
        ));

        //validation for password text
        formValidationManager.addElement(new FormValidationElement(
                passwordText,
                null,
                true,
                new ValidationCallback() {
                    @Override
                    public String validate(EditText editText) {
                        return (editText.getText().length() > 0 ? "" : "password required");
                    }
                }
        ));

        //prevalidate
        formValidationManager.validateForm(false, true);
    }

    private void doLogin()
    {
        final String username = usernameText.getText().toString();
        final String password = passwordText.getText().toString();
        this.password = password;

        final LoginAsyncTask loginAsyncTask = new LoginAsyncTask(LoginActivity.this, new LoginCallback());
        loginAsyncTask.execute(new LoginRequest(username, password));
    }

    private void autoLoginForTesting()
    {
        usernameText.setText("user1"); //LocalStorage.getUsername());
        passwordText.setText("pass1"); //LocalStorage.getPassword());

        doLogin();
    }

    @Override
    protected void exit()
    {
        super.exit();
        overridePendingTransitionHorizontalEntrance();
    }

    private class LoginCallback implements IApiResponseCallback<UserResponse>
    {
        @Override
        public void onFinished(UserResponse response)
        {
            Log.i(LogTag, "Login finished.");

            if (response != null && response.isSuccessful()) {
                //set user settings
                LocalStorage.setRememberPassword(rememberPasswordCheckbox.isChecked());

                Intent intent = new Intent(LoginActivity.this, OrdersViewActivity.class);
                startActivity(intent);
                exit();
            }
            else
            {
                //if we can find out what the error's about, we can display it above the appropriate textbox
                boolean showingErrorOnForm = false;
                if (response != null && response.hasError())
                {
                    ErrorInfo errorInfo = response.getErrorInfo();
                    if (errorInfo != null && errorInfo.getMessage() != null)
                    {
                        if (errorInfo.getMessage().toLowerCase().contains("username")) {
                            showingErrorOnForm = true;
                            usernameLabel.setText(errorInfo.getMessage());
                        }

                        else if (errorInfo.getMessage().toLowerCase().contains("password")) {
                            showingErrorOnForm = true;
                            passwordLabel.setText(errorInfo.getMessage());
                        }
                    }
                }

                //for unhandled errors, display popup
                if (!showingErrorOnForm) {
                    AlertUtil.showAlert(
                            LoginActivity.this,
                            "Login Error",
                            (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Login was unsuccessful."
                    );
                }
            }
        }
    }
}
