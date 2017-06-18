package com.shop.shopaves.dataModel;

import com.orm.SugarRecord;

/**
 * Created by amsyt003 on 15/2/17.
 */

public class TemporaryCart extends SugarRecord {
public String itemName,priceValue,username,product_img,product_id,user_img,count,shippingCharge;

    public TemporaryCart() {
    }

    public TemporaryCart(String itemName, String priceValue, String username, String product_img, String product_id, String user_img,String count,String shippingCharge) {
        this.itemName = itemName;
        this.priceValue = priceValue;
        this.username = username;
        this.product_img = product_img;
        this.product_id = product_id;
        this.user_img = user_img;
        this.count = count;
        this.shippingCharge = shippingCharge;
    }
}
