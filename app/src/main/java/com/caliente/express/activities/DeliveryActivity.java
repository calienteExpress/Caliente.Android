package com.caliente.express.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.caliente.express.R;

public class DeliveryActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.activity_delivery);
        setAppBarTitle("Delivery");

        super.onCreate(savedInstanceState);

        Button confirmButton = (Button)findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryActivity.this, OrderConfirmationActivity.class);
                startActivity(intent);
                overridePendingTransitionVerticalEntrance();
            }
        });
    }
}
