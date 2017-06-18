package com.shop.shopaves.dataModel;

import com.orm.SugarRecord;

/**
 * Created by amsyt005 on 3/10/16.
 */
public class SelectCategoryData extends SugarRecord {
    public String Category_name;
    public boolean IS_CATEGORY_SELECTED = false;
    public String TAG_ID;
    public String categoryImage;

    public SelectCategoryData() {
    }

    public SelectCategoryData(String category_name,String categoryImage, boolean IS_CATEGORY_SELECTED, String TAG_ID) {
        this.Category_name = category_name;
        this.IS_CATEGORY_SELECTED = IS_CATEGORY_SELECTED;
        this.TAG_ID = TAG_ID;
        this.categoryImage = categoryImage;
    }
}
