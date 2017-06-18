package com.shop.shopaves.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CoupanActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener{
    private ItemAdapter itemAdapter;
    private GridView productGrid;
    private ProgressDialog pd;
    private ArrayList<String> selectedProducts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupan);
        productGrid = (GridView)findViewById(R.id.galleryview);
        ((TextView)findViewById(R.id.title)).setText("COUPON");
        TextView name = (TextView)findViewById(R.id.discountname);
        TextView date = (TextView)findViewById(R.id.discountdate);
        TextView discription = (TextView)findViewById(R.id.discountdesp);
        name.setText(getIntent().getStringExtra(Constants.NAME));
        date.setText(C.getDateFormat(getIntent().getStringExtra(Constants.START_DATE)) + "-"+C.getDateFormat(getIntent().getStringExtra(Constants.END_DATE)));
        discription.setText(getIntent().getStringExtra(Constants.COUPON_DESCRIPTION));
        itemAdapter = new ItemAdapter();

/*
        itemAdapter = new ItemAdapter();
        itemAdapter.add(new Item(new Model()));
        itemAdapter.add(new Item(new Model()));
        itemAdapter.add(new Item(new Model()));
        itemAdapter.add(new Item(new Model()));
        itemAdapter.add(new Item(new Model()));
        itemAdapter.add(new Item(new Model()));
        itemAdapter.add(new Item(new Model()));
        itemAdapter.add(new Item(new Model()));
        productGrid.setAdapter(itemAdapter);*/
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.selectall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0 ; i < itemAdapter.getCount(); i++){
                    itemAdapter.getItem(i).isSelected = true;
                    itemAdapter.notifyDataSetChanged();
                    selectedProducts.clear();
                }
            }
        });
        findViewById(R.id.applycoupon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeSetProductsRequest(getIntent().getStringExtra(Constants.CODE));
            }
        });
        makeGetCouponProductsRequest(getIntent().getStringExtra(Constants.CODE));
    }

    private void makeGetCouponProductsRequest(String couponCode){
        pd = C.getProgressDialog(this);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("userId",new AppStore(this).getData(Constants.USER_ID));
        hashMap.put("couponCode",couponCode);
        Net.makeRequest(C.APP_URL+ ApiName.GET_COUPON_PRODUCTS_API,hashMap,this,this);
    }

    private void makeSetProductsRequest(String couponCode){
        pd = C.getProgressDialog(this);
        JSONObject map = new JSONObject();
        try {
            map.put("couponCode",couponCode);
            map.put("userId",new AppStore(this).getData(Constants.USER_ID));
            map.put("products",new JSONArray(selectedProducts));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Net.makeRequest(C.APP_URL+ApiName.SET_PRODUCT_API,map.toString(),setProductsresponse,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(CoupanActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    Response.Listener<JSONObject> setProductsresponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("response",jsonObject.toString());
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                    Toast.makeText(CoupanActivity.this,model.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("couponProducts",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
        Model data = new Model(model.getData());
            Model productArray[] = data.getProductArray();
            for(Model product : productArray){
                itemAdapter.add(new Item(product,false));
            }
            productGrid.setAdapter(itemAdapter);
        }
    }

    private class ItemAdapter extends ArrayAdapter<Item> {
        private LayoutInflater inflater=null;
        public ItemAdapter() {
            super(CoupanActivity.this, R.layout.single_product_hori);
            inflater = (LayoutInflater)CoupanActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ItemAdapter.Holder holder = null;
            View view = convertView;
            if(view == null){
                view = inflater.inflate(R.layout.single_product_hori,parent,false);
                holder = new ItemAdapter.Holder();
                holder.productName = (TextView) view.findViewById(R.id.product_name);
                holder.productPrice = (TextView) view.findViewById(R.id.price);
                holder.productDiscount = (TextView) view.findViewById(R.id.cross_price);
                holder.offDiscount = (TextView) view.findViewById(R.id.offdiscount);
                holder.prdImageView = (ImageView)view.findViewById(R.id.product_img);
                holder.selection = (ImageView)view.findViewById(R.id.wishlike);
                holder.ratingProduct = (RatingBar)view.findViewById(R.id.rate);
                holder.offDiscountView = (LinearLayout)view.findViewById(R.id.off);
                holder.rateCount = (TextView)view.findViewById(R.id.ratecount);

                holder.selection.setImageResource(R.drawable.edit);
                holder.itemClick = (RelativeLayout)view.findViewById(R.id.clicklike);

                view.setTag(holder);
            }else {
                holder = (ItemAdapter.Holder) view.getTag();
            }
            final Item item = getItem(position);
            final Model model = item.itemModel;

            holder.selection.setImageResource(item.isSelected ? R.drawable.check :  R.drawable.unchecked);


            if(item.isSelected)
                selectedProducts.add(""+model.getProductId());

            holder.productName.setText(model.getProductName());
            PicassoCache.getPicassoInstance(CoupanActivity.this).load(C.ASSET_URL+model.getProductImage()).into(holder.prdImageView);
            holder.ratingProduct.setRating(model.getAverageRating());
            holder.rateCount.setText(""+model.getRatingCount());

            holder.productPrice.setText("$" + C.FormatterValue(Float.parseFloat(model.getDiscount())));
            holder.productDiscount.setText("$"+ C.FormatterValue(Float.parseFloat(model.getPrice())));
            if(Float.parseFloat(model.getPrice())>0) {
                holder.offDiscount.setText("" + C.FormatterValue((100 - (Float.parseFloat(model.getDiscount()) * 100) / Float.parseFloat(model.getPrice())))  + "% off");
                holder.offDiscountView.setVisibility((100 - (Float.parseFloat(model.getDiscount()) * 100) / Float.parseFloat(model.getPrice())) == 0 ? View.INVISIBLE : View.VISIBLE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.isSelected = !item.isSelected;
                    selectedProducts.clear();
                    itemAdapter.notifyDataSetChanged();
                }
            });

            return view;
        }

        @Nullable
        @Override
        public Item getItem(int position) {
            return super.getItem(position);
        }

        public class Holder{

            private ImageView prdImageView,selection;
            private TextView productName,productPrice,productDiscount,offDiscount,rateCount;
            private RatingBar ratingProduct;
            private RelativeLayout itemClick;
            private LinearLayout offDiscountView;

        }
    }
    private class Item{
        public Model itemModel;
        boolean isSelected;
        public Item(Model itemModel,boolean isSelected) {
            this.itemModel = itemModel;
            this.isSelected = isSelected;
        }
    }
}
