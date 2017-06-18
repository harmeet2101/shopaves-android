package com.shop.shopaves.dataModel;

import com.orm.SugarRecord;

/**
 * Created by Admin on 9/30/2016.
 */

public class CityData extends SugarRecord {
    public String cityname,TAG_ID;
    public String TAG_VALUE = "0";

    public CityData() {
    }

    public CityData(String cityname,String TAG_VALUE,String TAG_ID) {
        this.cityname = cityname;
        this.TAG_VALUE = TAG_VALUE;
        this.TAG_ID = TAG_ID;

    }

}
