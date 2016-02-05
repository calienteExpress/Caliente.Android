package com.caliente.express.api.responses;

import com.caliente.express.api.models.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class OrderListResponse extends ApiResponse {
    private List<Order> orders;

    public List<Order> getOrders(){
        if (orders == null)
            orders = new ArrayList<>();
        return this.orders;
    }
    public void setOrders(List<Order> value) {this.orders = value;}

    @Override
    public boolean isSuccessful()
    {
        return super.isSuccessful() && this.orders != null;
    }

    public OrderListResponse()
    {
        orders = new ArrayList<>();
    }

    public OrderListResponse(int responseCode)
    {
        orders = new ArrayList<>();
        super.setResponseCode(responseCode);
    }
}
