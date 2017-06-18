package com.shop.shopaves.dataModel;

import com.orm.SugarRecord;
import com.shop.shopaves.R;

/**
 * Created by amsyt005 on 6/1/17.
 */
public class NewCategory extends SugarRecord {
    public int categoryId;
    public String name;
    public boolean hasSubCategory;

    public NewCategory() {
    }

    public NewCategory(int categoryId, boolean hasSubCategory,String name) {
        this.name = name;
        this.hasSubCategory = hasSubCategory;
        this.categoryId = categoryId;
    }
}
