package com.caliente.express.api.responses;

import com.caliente.express.api.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class MenuResponse extends ApiResponse {
    private List<MenuItem> menuItems;

    public List<MenuItem> getMenuItems(){return this.menuItems;}
    public void setMenuItems(List<MenuItem> value) {this.menuItems = value;}

    @Override
    public boolean isSuccessful()
    {
        return super.isSuccessful();
    }

    public MenuResponse(int responseCode) {
        super.setResponseCode(responseCode);
        this.menuItems = new ArrayList<>();
    }
}
