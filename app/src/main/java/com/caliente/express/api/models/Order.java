package com.caliente.express.api.models;

import com.caliente.express.util.DateTimeUtil;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class Order {
    private int id;
    private String dateTime;
    private String orderType;
    private String orderStatus;
    private float totalPrice;
    private DateTime dateTimeObject;
    private ArrayList<OrderItem> items;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getOrderType() {
        return orderType;
    }
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public float getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderItem> getItems() {
        return items;
    }
    public void setItems(ArrayList<OrderItem> items) {
        this.items = items;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String value) {
        this.dateTime = value;
        this.dateTimeObject = DateTimeUtil.StringToDateTime(value);
    }

    //TODO: figure out how to serialize this more naturally
    public DateTime getDateTimeObject() {
        if (dateTimeObject == null) {
            this.dateTimeObject = DateTimeUtil.StringToDateTime(this.dateTime);
        }

        return dateTimeObject;
    }
    public void setDateTimeObject(DateTime value) {
        this.dateTimeObject = value;
        this.dateTime = DateTimeUtil.DateTimeToString(value);
    }
}
