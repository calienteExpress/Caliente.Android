package com.caliente.express.api.models;

import java.util.ArrayList;

/**
 * Created by Home on 4/2/2559.
 */
public class MenuItem {
    private int id;
    private String name;
    private ArrayList<MenuItemOption> options;
    private int price;
    private int selectedOptionIndex;

    public int getId() { return id;}
    public void setId(int value) { this.id = value;}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        MenuItemOption selectedOption = getSelectedOption();
        if (selectedOption != null)
            price = Integer.parseInt(selectedOption.getPrice());

        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public MenuItemOption getSelectedOption() {
        if (selectedOptionIndex >= 0 && options != null && selectedOptionIndex < options.size())
            return options.get(selectedOptionIndex);

        return null;
    }
    public void setSelectedOption(int index) {
        this.selectedOptionIndex = index;
    }

    public ArrayList<MenuItemOption> getOptions() {
        return options;
    }
    public void setOptions(ArrayList<MenuItemOption> options) {
        this.options = options;
    }

    public boolean hasOptions()
    {
        return (this.options != null && this.options.size() > 0);
    }

    public MenuItem()
    {
        this.options = new ArrayList<>();
    }

    public void addOption(String name, String price)
    {
        MenuItemOption option = new MenuItemOption();
        option.setName(name);
        option.setPrice(price);
        options.add(option);
    }
}
