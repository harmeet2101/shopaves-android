package com.shop.shopaves.dataModel;

import com.orm.SugarRecord;

/**
 * Created by Admin on 9/30/2016.
 */

public class GenderInfo extends SugarRecord{

    public String GENDER = "FEMALE";
    public GenderInfo() {
    }

    public GenderInfo(String GENDER) {
        this.GENDER = GENDER;
    }
}
