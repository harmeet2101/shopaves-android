package com.shop.shopaves.dataModel;

import com.orm.SugarRecord;

/**
 * Created by Admin on 10/7/2016.
 */

public class StoresData extends SugarRecord {
    public boolean IS_STORE_SELECTED = false;
    public String storeName,storeId,storeImage;

    public StoresData() {
    }

    public StoresData(String storeId,String storeName,String storeImage,boolean IS_STORE_SELECTED) {
        this.IS_STORE_SELECTED = IS_STORE_SELECTED;
        this.storeId = storeId;
        this.storeImage = storeImage;
        this.storeName = storeName;
    }
}
