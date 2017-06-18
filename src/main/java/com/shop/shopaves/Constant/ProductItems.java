package com.shop.shopaves.Constant;

/**
 * Created by HANAMANTRAYA on 31-05-2017.
 */
public  class ProductItems{
    public String selectedProductImage;
    public String productId;
    public String Id;
    public String imageProperties;
    public String productImage;

    public ProductItems(String Id,String selectedProductImage, String productId,String imageProperties,String productImage) {
        this.selectedProductImage = selectedProductImage;
        this.productId = productId;
        this.Id = Id;
        this.imageProperties = imageProperties;
        this.productImage = productImage;
    }

}