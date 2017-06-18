package com.shop.shopaves.dataModel;

import com.orm.SugarRecord;

/**
 * Created by Admin on 9/30/2016.
 */

public class EnterTagData extends SugarRecord {

    public String TAG;
    public String TAG_VALUE = "0";
    public String TAG_ID;

    public EnterTagData() {
    }

    public EnterTagData(String TAG,String TAG_VALUE,String TAG_ID) {
        this.TAG = TAG;
        this.TAG_VALUE = TAG_VALUE;
        this.TAG_ID = TAG_ID;
    }
}
