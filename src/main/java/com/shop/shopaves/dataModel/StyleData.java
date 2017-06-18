package com.shop.shopaves.dataModel;

import com.orm.SugarRecord;

/**
 * Created by Admin on 9/30/2016.
 */

public class StyleData extends SugarRecord{

    public String STYLE_TAG,TAG_VALUE,TAG_ID;
    public StyleData(String STYLE_TAG,String TAG_VALUE,String TAG_ID) {
        this.STYLE_TAG = STYLE_TAG;
        this.TAG_VALUE = TAG_VALUE;
        this.TAG_ID = TAG_ID;
    }

    public StyleData() {
    }
}
