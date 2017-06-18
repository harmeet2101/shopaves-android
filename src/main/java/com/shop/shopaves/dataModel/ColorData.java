package com.shop.shopaves.dataModel;

import com.orm.SugarRecord;

/**
 * Created by amsyt005 on 5/10/16.
 */
public class ColorData extends SugarRecord {

    public String TAG_VALUE = "0";
    public String TAG_NAME;
    public String COLOR_CODE,TAG_ID;

    public ColorData(String TAG_NAME,String TAG_VALUE,String COLOR_CODE,String TAG_ID) {
        this.TAG_VALUE = TAG_VALUE;
        this.TAG_NAME = TAG_NAME;
        this.COLOR_CODE = COLOR_CODE;
        this.TAG_ID = TAG_ID;
    }

    public ColorData() {
    }
}
