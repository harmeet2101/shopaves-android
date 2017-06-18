package com.shop.shopaves.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.shop.shopaves.R;

public class PaymentSuccessFailed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success_failed);

        findViewById(R.id.continueshopping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(PaymentSuccessFailed.this,HomeActivity.class));
               finish();
            }
        });
    }
}
