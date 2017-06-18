package com.shop.shopaves.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.vision.text.Text;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;


/**
 * Created by HANAMANTRAYA on 18-05-2017.
 */

public class LiveTryOut extends Activity {
    private String productDetails;
    private FrameLayout blankCamera;
    private  TextView textView;
    private RelativeLayout addItem;
    private ImageView backbutton;
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_live_try_out);
        addItem =(RelativeLayout)findViewById(R.id.addditem);
        backbutton= (ImageView)findViewById(R.id.back_to);

        Intent intent = getIntent();
        productDetails = intent.getStringExtra("productDetails");
        Log.e("ProductDetails",""+productDetails);


//            Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
//            startActivity(intent1);

        textView = (TextView)findViewById(R.id.editProductLocation);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView.getText().toString().equals("Edit Product Location")){
                    textView.setText("Done");
                }
                else{
                    textView.setText("Edit Product Location");
                    }
                }
            });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LiveTryOut.this,ProductImageActivity.class);
                intent.putExtra("productDetails",productDetails);
                startActivity(intent);
            }
        });

    }
}
