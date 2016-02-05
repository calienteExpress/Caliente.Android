package com.caliente.express.tasks;

import com.caliente.express.api.models.FilterParams;
import com.caliente.express.api.responses.OrderListResponse;
import android.content.Context;

import com.caliente.express.api.ApiService;
import android.util.Log;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class GetOrdersAsyncTask extends AsyncApiTask<Integer, Integer, OrderListResponse> {
    private static final String LogTag = "GetOrdersAsyncTask";
    private FilterParams filterParams;

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public GetOrdersAsyncTask(final Context context, final FilterParams filterParams, final IApiResponseCallback callback) {
        super(context, callback);
        this.filterParams = filterParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected OrderListResponse doInBackground(final Integer... userId) {
        OrderListResponse response = null;
        try {
            response = ApiService.getOrders(userId[0], filterParams);
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final OrderListResponse response) {
        super.onPostExecute(response);
    }
}
