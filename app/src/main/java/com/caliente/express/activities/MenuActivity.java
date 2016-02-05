package com.caliente.express.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.caliente.express.R;
import com.caliente.express.api.models.MenuItem;
import com.caliente.express.api.models.MenuItemOption;
import com.caliente.express.api.models.Order;
import com.caliente.express.api.responses.MenuResponse;
import com.caliente.express.storage.LocalStorage;
import com.caliente.express.tasks.GetMenuAsyncTask;
import com.caliente.express.tasks.IApiResponseCallback;
import com.caliente.express.util.AlertUtil;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends ActivityBase {

    private ListView menuListView;
    private ArrayList<Integer> prices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.activity_menu);
        setAppBarTitle("Menu");
        setShowMenu(true);

        super.onCreate(savedInstanceState);

        Button nextButton = (Button)findViewById(R.id.next_button);
        menuListView = (ListView)findViewById(R.id.menu_list_view);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, DeliveryTypeActivity.class);
                startActivity(intent);
                overridePendingTransitionHorizontalEntrance();
            }
        });

                //load the menu
        if (LocalStorage.getMenu() == null) {
            GetMenuAsyncTask task = new GetMenuAsyncTask(this, new GetMenuCallback());
            task.execute();
        }
        else
            reloadListView();
    }

    private void reloadListView()
    {
        final MenuArrayAdapter listAdapter = new MenuArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                LocalStorage.getMenu().getMenuItems()
        );

        for(int n=0; n<LocalStorage.getMenu().getMenuItems().size(); n++)
            prices.add(0);

        menuListView.setAdapter(listAdapter);
    }

    private void setMenuPrice()
    {
        int total =0;
        for(int n=0; n<prices.size(); n++)
        {
            total += prices.get(n);
        }

        ((TextView)findViewById(R.id.menu_price_label)).setText(Integer.toString(total) + "฿");
    }


    private class MenuArrayAdapter extends ArrayAdapter<MenuItem>
    {
        private final Context context;
        private final List<MenuItem> items;

        public MenuArrayAdapter(Context context, int textViewResourceId, List<MenuItem> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.items = objects;
        }

        @Override
        public long getItemId(int position) {
            MenuItem item = getItem(position);
            return item.getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.menu_row_layout, parent, false);
            CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.menu_item_checkbox);
            final RadioGroup radioGroup = (RadioGroup)rowView.findViewById(R.id.menu_item_options_container);

            if (position < items.size()) {
                MenuItem item = items.get(position);
                if (item != null) {
                    checkbox.setText(item.getName());

                    /*
                    if (item.hasOptions()) {
                        for(MenuItemOption option : item.getOptions())
                        {
                            RadioButton button = new RadioButton(MenuActivity.this);
                            button.setText(option.getName() + " " + option.getPrice());
                            radioGroup.addView(button);
                        }
                    }
                    */
                }
            }

            final RadioButton hotOption = (RadioButton)rowView.findViewById(R.id.hot_option);
            final RadioButton icedOption = (RadioButton)rowView.findViewById(R.id.iced_option);

            hotOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        prices.set(position, 40);
                    setMenuPrice();
                }
            });

            icedOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        prices.set(position, 45);
                    setMenuPrice();
                }
            });

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (!hotOption.isChecked() && !icedOption.isChecked()) {
                            hotOption.setChecked(true);
                            prices.set(position, 40);
                        }

                        if (hotOption.isChecked())
                            prices.set(position, 40);
                        if (icedOption.isChecked())
                            prices.set(position, 45);
                    }
                    else
                    {
                        hotOption.setChecked(false);
                        icedOption.setChecked(false);
                        prices.set(position, 0);
                    }

                    setMenuPrice();
                }
            });

            return rowView;
        }
    }

    private class GetMenuCallback implements IApiResponseCallback<MenuResponse>
    {
        @Override
        public void onFinished(MenuResponse response)
        {
            if (response != null && response.isSuccessful())
            {
                LocalStorage.setMenu(response);
                reloadListView();
            }
            else
            {
                AlertUtil.showAlert(
                        MenuActivity.this,
                        "Get Menu Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Unable to retrieve menu."
                );
            }
        }
    }
}
