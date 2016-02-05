package com.caliente.express.tasks;

import com.caliente.express.api.models.FilterParams;
import com.caliente.express.api.responses.MenuResponse;
import com.caliente.express.api.responses.OrderListResponse;
import android.content.Context;

import com.caliente.express.api.ApiService;
import android.util.Log;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class GetMenuAsyncTask extends AsyncApiTask<Integer, Integer, MenuResponse> {
    private static final String LogTag = "GetMenuAsyncTask";

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public GetMenuAsyncTask(final Context context, final IApiResponseCallback callback) {
        super(context, callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected MenuResponse doInBackground(final Integer... userId) {
        MenuResponse response = null;
        try {
            response = ApiService.getMenu();
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final MenuResponse response) {
        super.onPostExecute(response);
    }
}
