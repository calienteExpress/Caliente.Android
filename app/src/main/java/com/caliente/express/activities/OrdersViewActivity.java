package com.caliente.express.activities;

import android.app.Activity;
import com.caliente.express.api.models.Order;
import com.caliente.express.api.models.FilterParams;
import com.caliente.express.api.responses.OrderListResponse;
import com.caliente.express.storage.LocalStorage;
import com.caliente.express.tasks.GetOrdersAsyncTask;
import com.caliente.express.tasks.IApiResponseCallback;
import com.caliente.express.util.AlertUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.caliente.express.R;
import com.caliente.express.util.PermissionUtil;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//TODO: controls do not usually need to be class-level variables
//TODO: make the list not jump back to top when refreshing
/**
 * Created by John R. Kosinski on 25/1/2559.
 * Main screen that shows list of orders, has options for create/update/delete/filter orders.
 */
public class OrdersViewActivity extends ActivityBase
{
    private static final String LogTag = "OrdersViewActivity";

    private ListView ordersListView;
    private List<Order> ordersList;
    private boolean refreshList;
    private Button filterButton;
    private Button addButton;
    private View filterViewContainer;
    private TextView filterLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentResId(R.layout.activity_orders_view);
        setAppBarTitle("Orders");
        super.onCreate(savedInstanceState);

        ordersListView = (ListView)findViewById(R.id.orders_list_view);

        this.refreshListFromServer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case UserSettingsActivityId:
                if (resultCode == Activity.RESULT_OK) {
                    this.refreshList = true;
                }
                break;

            case FilterActivityId:
                if (resultCode == Activity.RESULT_OK)
                {
                    this.refreshList = true;
                }
                break;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(this.refreshList)
        {
            this.refreshListFromServer();
            refreshList=false;
        }
    }

    private void refreshListFromServer()
    {
        this.displayFilterParams();
        this.addButton.setEnabled(PermissionUtil.currentUserCanEditTarget());

        final GetOrdersAsyncTask task = new GetOrdersAsyncTask(OrdersViewActivity.this, LocalStorage.getFilterParams(), new GetOrdersCallback());
        task.execute(LocalStorage.getCurrentTargetUser().getId());
    }

    private void reloadListView()
    {
        if (this.ordersList == null)
            this.ordersList = new ArrayList<Order>();

        //disable filter button if no orders
        filterButton.setEnabled(ordersList.size() > 0);

        final OrderArrayAdapter listAdapter = new OrderArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                this.ordersList
        );

        ordersListView.setAdapter(listAdapter);
    }

    private void displayFilterParams()
    {
        FilterParams filterParams = LocalStorage.getFilterParams();

        if (filterParams != null && !filterParams.isEmpty())
        {
            filterButton.setVisibility(View.GONE);
            filterViewContainer.setVisibility(View.VISIBLE);

            StringBuilder filterText = new StringBuilder();
            boolean showingDate = false;
            if (filterParams.getDateFrom() != null || filterParams.getDateTo() != null) {
                filterText.append("from ");
                filterText.append((filterParams.getDateFrom() != null) ? filterParams.getDateFrom() : "?");
                filterText.append(" - ");
                filterText.append((filterParams.getDateTo() != null) ? filterParams.getDateTo() : "?");
                showingDate = true;
            }

            if (filterParams.getTimeFrom() != null || filterParams.getTimeTo() != null) {
                if (showingDate)
                    filterText.append("\n");

                filterText.append("between ");
                filterText.append((filterParams.getTimeFrom() != null) ? filterParams.getTimeFrom() : "00:00");
                filterText.append(" - ");
                filterText.append((filterParams.getTimeTo() != null) ? filterParams.getTimeTo() : "23:59");
            }

            filterLabel.setText(filterText.toString());
        }
        else
        {
            filterButton.setVisibility(View.VISIBLE);
            filterViewContainer.setVisibility(View.GONE);
        }
    }


    private class OrderArrayAdapter extends ArrayAdapter<Order>
    {
        private final Context context;
        private final List<Order> orders;

        public OrderArrayAdapter(Context context, int textViewResourceId, List<Order> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.orders = objects;
        }

        @Override
        public long getItemId(int position) {
            Order item = getItem(position);
            return item.getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.order_row_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.description_label);

            textView.setText(orders.get(position).getId());

            return rowView;
        }
    }

    private class GetOrdersCallback implements IApiResponseCallback<OrderListResponse>
    {
        @Override
        public void onFinished(OrderListResponse response)
        {
            Log.i(LogTag, "Get orders finished.");

            if (response != null && response.isSuccessful()) {
                ordersList = (response.getOrders());
                reloadListView();
            }
            else
            {
                AlertUtil.showAlert(
                        OrdersViewActivity.this,
                        "Get Orders Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Unable to retrieve orders."
                );
            }
        }
    }
}
