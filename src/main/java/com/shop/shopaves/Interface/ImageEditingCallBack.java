package com.shop.shopaves.Interface;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by amsyt005 on 4/11/16.
 */
public interface ImageEditingCallBack {

    void onRemoveImageCallBack();
    void onForwardImageBitmapCallBack();
    void onBackwardImageBitmapCallBack();
    void onSetEditedImageBitmapCallBack(int position, Bitmap drawable);

}
