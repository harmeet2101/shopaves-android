package com.shop.shopaves.Constant;

import android.graphics.drawable.Drawable;

/**
 * Created by amsyt005 on 7/11/16.
 */
public class ImgItemProperties {

    public String scalex,scaley,centerx,centery,minx,miny,maxx,maxy,angle;
    public int Id;



    public ImgItemProperties(int Id,String scaley, String scalex, String centerx, String centery,String minx,String miny,String maxx,String maxy, String angle) {
        this.scaley = scaley;
        this.scalex = scalex;
        this.centerx = centerx;
        this.centery = centery;
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
        this.angle = angle;
        this.Id = Id;
    }
}
