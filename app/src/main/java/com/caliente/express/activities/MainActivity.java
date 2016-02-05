package com.caliente.express.activities;

import com.caliente.express.R;
import com.caliente.express.api.ApiService;
import com.caliente.express.api.responses.AppSettingsResponse;
import com.caliente.express.api.responses.UserResponse;
import com.caliente.express.storage.LocalStorage;
import com.caliente.express.tasks.GetAppSettingsAsyncTask;
import com.caliente.express.tasks.GetUserAsyncTask;
import com.caliente.express.tasks.IApiResponseCallback;
import com.caliente.express.util.DateTimeUtil;
import com.caliente.express.util.StringUtil;

import android.content.Intent;
import android.os.Bundle;

import org.joda.time.DateTime;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Main application activity
 */
public class MainActivity extends ActivityBase {

    private int numberOfApiAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.activity_main);
        setShowMenu(false);
        setAppBarTitle("");
        setShowToolbar(false);

        super.onCreate(savedInstanceState);

        //do this to initialize joda datetime library - it's very slow
        DateTime dateTime = DateTimeUtil.StringToDateTime("01/01/2015 00:00");
        dateTime = DateTimeUtil.StringToDateTime("01/02/2015 00:00");
        String dtString = DateTimeUtil.DateTimeToString(dateTime);

        //configure API service
        ApiService.configure(getApplicationContext().getString(R.string.api_base_url));
        setProgressActivity(true);

        //initialize storage
        LocalStorage.initialize(getApplicationContext());

        goToMenu();

        //attempt to connect to the server
        //GetAppSettingsAsyncTask task = new GetAppSettingsAsyncTask(MainActivity.this, new GetAppSettingsCallback());
        //task.execute(0);
    }

    private void goToLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMenu()
    {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private class GetUserCallback implements IApiResponseCallback<UserResponse>
    {
        @Override
        public void onFinished(UserResponse response)
        {
            if (response != null)
            {
                //successful login
                if (response.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, OrdersViewActivity.class);
                    LocalStorage.setUserId(response.getUser().getId());
                    LocalStorage.setUsername(response.getUser().getUsername());
                    LocalStorage.setCurrentTargetUser(response.getUser());
                    startActivity(intent);
                    exit();
                    return;
                }
            }

            //otherwise redirect to login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            exit();
        }
    }

    private class GetAppSettingsCallback implements IApiResponseCallback<AppSettingsResponse>
    {
        @Override
        public void onFinished(AppSettingsResponse response)
        {
            boolean success = false;
            if (response != null)
            {
                LocalStorage.setAppSettings(response.getAppSettings());
                if (response.isSuccessful() && response.getAppSettings().getServerStatus().equalsIgnoreCase("UP")) {
                    success = true;

                    //check if user is already logged in, or can be auto-logged in. If not, go to login screen.
                    String authToken = LocalStorage.getAuthToken();
                    if (!StringUtil.isNullOrEmpty(authToken))
                    {
                        //make any call, for the purpose of checking API connectivity and auth token validity
                        //a method to create a task & execute it
                        //a return
                        GetUserAsyncTask task = new GetUserAsyncTask(MainActivity.this, new GetUserCallback());
                        task.execute(0);
                    }
                    else {
                        goToLogin();
                    }
                }
            }

            if (!success)
            {
                numberOfApiAttempts += 1;
                if (numberOfApiAttempts >= 3)
                {
                    //redirect to server down page
                    Intent intent = new Intent(MainActivity.this, ServerDownActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    GetAppSettingsAsyncTask task = new GetAppSettingsAsyncTask(MainActivity.this, new GetAppSettingsCallback());
                    task.execute(0);
                }
            }
        }
    }
}
