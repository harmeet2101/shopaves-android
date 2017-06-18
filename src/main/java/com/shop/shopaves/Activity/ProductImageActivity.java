package com.shop.shopaves.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.shop.shopaves.R;
import com.shop.shopaves.Util.Model;

/**
 * Created by HANAMANTRAYA on 19-05-2017.
 */

public class ProductImageActivity extends Activity {

    private String editProductImage;
    private String productImage;
    private String imageProperties;
    private int productId;
    private ImageView backButton;
    protected  void onCreate(Bundle savedInstaneBundle){
        super.onCreate(savedInstaneBundle);
        setContentView(R.layout.activity_product_image);
        backButton = (ImageView)findViewById(R.id.back_to);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String productDetails = getIntent().getStringExtra("productDetails");
        Log.e("productDetails",productDetails);
        if(productDetails!=null){
            Model model = new Model(productDetails);
            Model dataValue = new Model(model.getData());
            Model prdArray[] = model.getProductArray();
            for (Model product : prdArray){

                imageProperties = product.getImageProperties();
                productId = product.getProductId();
            }
        }
    }
}
