package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductGridActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{

    private ItemAdapter itemAdapter;
    private GridView productGrid;
    private ProgressDialog pd;
    private AppStore aps;
    private String type;
    private Item itemReference;
    private String id;
    private String productArray;
    private RelativeLayout buyItemsView;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wish_list);
        productGrid = (GridView)findViewById(R.id.galleryview);
        TextView title = (TextView)findViewById(R.id.title);
        buyItemsView = (RelativeLayout) findViewById(R.id.buyitemsview);
        ((ImageView)findViewById(R.id.itemclick)).setImageResource(R.drawable.coupon);
        aps = new AppStore(this);
        itemAdapter = new ItemAdapter();

        if(getIntent().getStringExtra(Constants.ADD_WISHLIST) != null) {
            type = Constants.SHOW_WISHLIST;
            findViewById(R.id.itemclick).setVisibility(View.GONE);
            id = getIntent().getStringExtra(Constants.ADD_WISHLIST);
            title.setText(getIntent().getStringExtra(Constants.NAME));
            makeShowWishListProduct(getIntent().getStringExtra(Constants.ADD_WISHLIST));
        }
        else if(getIntent().getStringExtra(Constants.PRODUCT)!=null){
            type = Constants.COLLECTION_PRODUCTS;
            buyItemsView.setVisibility(View.VISIBLE);
            productArray = getIntent().getStringExtra(Constants.PRODUCT);
            title.setText("ITEMS");
            Log.e("productArray",productArray.toString());
            Model model = new Model(productArray);
            Model dataArray[] = model.getArray(productArray);
            for(Model data : dataArray){
                itemAdapter.add(new Item(data));
            }
            productGrid.setAdapter(itemAdapter);
        }
        else{
            if(getIntent().getStringExtra(Constants.TYPE) != null)
            type = getIntent().getStringExtra(Constants.TYPE);
            title.setText("ITEMS");
            if(getIntent().getStringExtra(Constants.USER_ID)!=null)
                userId = getIntent().getStringExtra(Constants.USER_ID);
            getProductList();
        }

        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.itemclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductGridActivity.this,AvailableCouponsActivity.class));
            }
        });

        findViewById(R.id.createproduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductGridActivity.this,AddProductActivity.class));
            }
        });

    }


    private void makeShowWishListProduct(String wid){
        pd =  C.getProgressDialog(ProductGridActivity.this);
            Map<String,String> map = new HashMap<>();
                map.put("wid", wid);
                map.put("uid", aps.getData(Constants.USER_ID));
                Net.makeRequest(C.APP_URL+ ApiName.WISHLIST_PRODUCT_API,map,this,this);
        }

    private void getProductList(){
        pd =  C.getProgressDialog(ProductGridActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", TextUtils.isEmpty(userId) ? aps.getData(Constants.USER_ID) : userId);
       // map.put("myProduct",TextUtils.isEmpty(userId) ? "true" : "false");
        map.put("myProduct","1");
        Net.makeRequest(C.APP_URL+ ApiName.GET_PRODUCTS,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(ProductGridActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());

        if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
            Log.e("Response", jsonObject.toString());
            if(type.equals(Constants.SHOW_WISHLIST)) {
                Model data = new Model(model.getData());
                Model productArray[] = data.getProductArray();
                for (Model prodect : productArray) {
                    itemAdapter.add(new Item(prodect));
                }
                productGrid.setAdapter(itemAdapter);
                findViewById(R.id.notfound).setVisibility(productArray.length > 0 ? View.GONE : View.VISIBLE);
            }else if(type.equals(Constants.SHOW_PRODUCT)){
                Model dataArray[] = model.getDataArray();
                for(Model data : dataArray){
                    itemAdapter.add(new Item(data));
                }
                productGrid.setAdapter(itemAdapter);
                findViewById(R.id.notfound).setVisibility(dataArray.length > 0 ? View.GONE : View.VISIBLE);

            }else{
                itemAdapter.remove(itemReference);
                itemAdapter.notifyDataSetChanged();
            }
        }
    }

    private class ItemAdapter extends ArrayAdapter<Item> {

        private LayoutInflater inflater=null;
        public ItemAdapter() {
            super(ProductGridActivity.this, R.layout.single_product_hori);
            inflater = (LayoutInflater)ProductGridActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            View view = convertView;
            if(view == null){
                view = inflater.inflate(R.layout.single_product_hori,parent,false);
                holder = new Holder();
                holder.productName = (TextView) view.findViewById(R.id.product_name);
                holder.productPrice = (TextView) view.findViewById(R.id.price);
                holder.productDiscount = (TextView) view.findViewById(R.id.cross_price);
                holder.offDiscount = (TextView) view.findViewById(R.id.offdiscount);
                holder.prdImageView = (ImageView)view.findViewById(R.id.product_img);
                holder.delete = (ImageView)view.findViewById(R.id.wishlike);
                holder.ratingProduct = (RatingBar)view.findViewById(R.id.rate);
                holder.offDiscountView = (LinearLayout)view.findViewById(R.id.off);
                holder.couponAvailable = (TextView)view.findViewById(R.id.availablecoupon);
                holder.rateView = (LinearLayout)view.findViewById(R.id.rateview);
                holder.rateCount = (TextView)view.findViewById(R.id.ratecount);
                if(type.equals(Constants.SHOW_PRODUCT) && TextUtils.isEmpty(userId)){
                    holder.delete.setImageResource(R.drawable.edit);
                    holder.couponAvailable.setVisibility(View.VISIBLE);
                }
                else if(!type.equals(Constants.COLLECTION_PRODUCTS)&& TextUtils.isEmpty(userId))
                holder.delete.setImageResource(R.drawable.delete);
               // else if(!TextUtils.isEmpty(userId))

                holder.itemClick = (RelativeLayout)view.findViewById(R.id.clicklike);

                view.setTag(holder);
            }else {
                holder = (Holder) view.getTag();
            }
            final Item item = getItem(position);
            final Model model = item.itemModel;
            holder.productName.setText(model.getProductName());
            holder.productName.setTag(""+model.getId());
            holder.rateView.setVisibility(model.getRatingCount() > 0 ? View.VISIBLE : View.GONE);
            holder.rateCount.setText("("+model.getRatingCount()+")");

            if(type.equals(Constants.COLLECTION_PRODUCTS)) {
                PicassoCache.getPicassoInstance(ProductGridActivity.this).load(C.getImageUrl(model.getEditProductImage())).into(holder.prdImageView);
            }else
             PicassoCache.getPicassoInstance(ProductGridActivity.this).load(C.getImageUrl(model.getProductImage())).into(holder.prdImageView);

            holder.productPrice.setText("$ "+C.formateValue(Double.parseDouble(model.getDiscount())));
            holder.productDiscount.setText("$ "+C.formateValue(model.getItemPriceValue()));
            if(model.getPriceValue()>0) {
                holder.offDiscount.setText("" + C.formateValue((100 - (Double.parseDouble(model.getDiscount()) * 100) / model.getPriceValue())) + "% off");
                holder.offDiscountView.setVisibility((100 - (Double.parseDouble(model.getDiscount()) * 100) / model.getItemPriceValue()) > 0 ? View.VISIBLE : View.INVISIBLE);

               // productPrice.setText("$" + C.FormatterValue(Float.parseFloat(productModel.getDiscount())));
                //productDiscount.setText("$"+C.FormatterValue(Float.parseFloat(productModel.getPrice())));

            }
            holder.productDiscount.setPaintFlags(holder.productDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            //  holder.ratingProduct.setRating();
            final Holder finalHolder = holder;
          if(type.equals(Constants.COLLECTION_PRODUCTS)){
                    holder.itemClick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                        }
                    });
          }else{
              holder.itemClick.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                   /* deleteProductReference = holder.addProduct;
                    selectedPosition = holder.addProduct.indexOfChild(wishListProductView);*/

                      if(type.equals(Constants.SHOW_WISHLIST)) {
                          itemReference = item;
                          makeDeleteWishListProductRequest(id, "" + model.getProductId());
                      }else{
                          startActivityForResult(new Intent(ProductGridActivity.this, AddProductActivity.class).putExtra(Constants.PRODUCT_ID, "" + model.getProductId()), 100);
                      }
                  }
              });
          }

            final Holder finalHolder1 = holder;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("nameTag",finalHolder.productName.getTag().toString());
                    startActivity(new Intent(ProductGridActivity.this,ProductDetailsActivity.class).putExtra("productId", ""+model.getProductId()));
                }
            });

            holder.couponAvailable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("productId",""+model.getProductId());
                    startActivity(new Intent(ProductGridActivity.this,AvailableCouponsActivity.class).putExtra(Constants.PRODUCT_ID,""+model.getProductId()));
                }
            });
            return view;
        }

        public class Holder{
            private ImageView prdImageView,delete;
            private TextView productName,productPrice,productDiscount,offDiscount,couponAvailable,rateCount;
            private RatingBar ratingProduct;
            private RelativeLayout itemClick;
            private LinearLayout offDiscountView,rateView;

        }
    }
    private class Item{
       public Model itemModel;

        public Item(Model itemModel) {
            this.itemModel = itemModel;
        }
    }


    private void makeDeleteWishListProductRequest(String wishListId,String productId){
        type = Constants.PRODUCT_DELETE;
        Map<String,String> map = new HashMap<>();
        map.put("wid",wishListId);
        map.put("pid",productId);
        pd =  C.getProgressDialog(ProductGridActivity.this);
        Net.makeRequestParams(C.APP_URL+ ApiName.DELETE_WISHLIST_PRODUCT,map,this,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            if (resultCode == 100){
                itemAdapter.clear();
                getProductList();
            }
        }
    }
}
