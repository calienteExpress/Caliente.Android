package com.caliente.express.api.models;

/**
 * Created by Home on 4/2/2559.
 */
public class OrderItem {
    private String name;
    private float price;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }
}
