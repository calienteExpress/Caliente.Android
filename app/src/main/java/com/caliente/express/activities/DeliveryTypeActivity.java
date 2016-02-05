package com.caliente.express.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.caliente.express.R;

public class DeliveryTypeActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.activity_delivery_type);
        setAppBarTitle("Delivery Type");
        super.onCreate(savedInstanceState);

        final Button pickupButton = (Button)findViewById(R.id.pickup_button);
        Button deliveryButton = (Button)findViewById(R.id.delivery_button);

        pickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryTypeActivity.this, PickupActivity.class);
                startActivity(intent);
                overridePendingTransitionHorizontalEntrance();
            }
        });

        deliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryTypeActivity.this, DeliveryActivity.class);
                startActivity(intent);
                overridePendingTransitionHorizontalEntrance();
            }
        });
    }
}
