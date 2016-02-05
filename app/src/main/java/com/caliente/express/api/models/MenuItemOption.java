package com.caliente.express.api.models;

/**
 * Created by Home on 4/2/2559.
 */
public class MenuItemOption {
    private int id;
    private String name;
    private String price;

    public int getId() { return id;}
    public void setId(int value) { this.id = value;}

    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name;}

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
}
