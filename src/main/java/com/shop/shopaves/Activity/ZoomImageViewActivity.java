package com.shop.shopaves.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.imagezoom.ImageViewTouch;

public class ZoomImageViewActivity extends AppCompatActivity {

    private ImageViewTouch mImage;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_view);
        mImage = (ImageViewTouch)findViewById(R.id.touchimg);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        PicassoCache.getPicassoInstance(ZoomImageViewActivity.this).load(C.getImageUrl(getIntent().getStringExtra(Constants.PRODUCT_IMAGE))).into(mImage);
    }
}
