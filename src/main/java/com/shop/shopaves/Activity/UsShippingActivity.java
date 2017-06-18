package com.shop.shopaves.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.shop.shopaves.R;

public class UsShippingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us_shipping);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("U.S. SHIPPING");
        findViewById(R.id.back_addrss).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_addrss:
                finish();
        }
    }
}
