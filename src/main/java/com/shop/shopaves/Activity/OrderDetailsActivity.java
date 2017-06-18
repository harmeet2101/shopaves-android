package com.shop.shopaves.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Dialog.WriteReviewDialog;
import com.shop.shopaves.Interface.Callback;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.NamePair;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends Activity implements View.OnClickListener,Response.ErrorListener{

    private ImageView productImage;
    private TextView productName,quantity,productPrice,delivery,shippingCharge,price,totalAmount;
    private ProgressDialog pd;
    private String productId = "", trackingId = "";
    private String type = "";
    private String itemImage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.orderdetails));
        productImage = (ImageView)findViewById(R.id.product_img);
        productName = (TextView)findViewById(R.id.product_name);
        quantity = (TextView)findViewById(R.id.quant);
        productPrice = (TextView)findViewById(R.id.price);
        delivery = (TextView)findViewById(R.id.dating);
        shippingCharge = (TextView)findViewById(R.id.shippingcharge);
        price = (TextView)findViewById(R.id.productprice);
        totalAmount = (TextView)findViewById(R.id.totalamount);
        TextView orderDate = (TextView)findViewById(R.id.orderdate);

        if(getIntent() != null){
            orderDate.setText(getIntent().getStringExtra(Constants.DATE));
            makeOrderDetailsRequest(getIntent().getStringExtra(Constants.ORDER_ID));
           type = getIntent().getStringExtra(Constants.TYPE);
            if(type.equals(Constants.ONGOING)){
                findViewById(R.id.returnproduct).setVisibility(View.GONE);
                findViewById(R.id.cancel).setVisibility(View.VISIBLE);
            }
            else if(type.equals(Constants.DELIVERED)){
                findViewById(R.id.returnproduct).setVisibility(View.VISIBLE);
                findViewById(R.id.cancel).setVisibility(View.GONE);
            }else if(type.equals(Constants.CANCELED)){
                findViewById(R.id.returnproduct).setVisibility(View.GONE);
                findViewById(R.id.cimg).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.canceltext)).setText("Cancelled");
            }else if(type.equals(Constants.RETURNED)){
                findViewById(R.id.cancel).setVisibility(View.GONE);
                findViewById(R.id.retimg).setVisibility(View.GONE);
                findViewById(R.id.returnproduct).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.rtr)).setText("Returned");
            }
        }


        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.returnproduct).setOnClickListener(this);
        findViewById(R.id.leavefeedback).setOnClickListener(this);
        findViewById(R.id.track).setOnClickListener(this);
    }

    private void makeOrderDetailsRequest(String orderId){
        pd = C.getProgressDialog(this);
        HashMap<String,String> map = new HashMap<>();
        map.put("orderId",orderId);
        Net.makeRequest(C.APP_URL + ApiName.ORDER_DETAILS,map,orderResponseResponse,this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancel:
                if(type.equals(Constants.ONGOING)){
                    Intent detailsIntent = new Intent(OrderDetailsActivity.this,CancelOrderActivity.class);
                    detailsIntent.putExtra(Constants.ID,getIntent().getStringExtra(Constants.ID));
                    detailsIntent.putExtra(Constants.NAME,productName.getText().toString());
                    detailsIntent.putExtra(Constants.PRICE,price.getText().toString());
                    detailsIntent.putExtra(Constants.QUANTITY,quantity.getText().toString());
                    detailsIntent.putExtra(Constants.DELIVERED,delivery.getText().toString());
                    detailsIntent.putExtra(Constants.PRODUCT_IMAGE,itemImage);
                    detailsIntent.putExtra(Constants.TYPE,Constants.CANCELED);
                    startActivity(detailsIntent);
                 }
                break;
            case R.id.returnproduct:
                if(type.equals(Constants.DELIVERED)){
                    Intent detailsIntent = new Intent(OrderDetailsActivity.this,CancelOrderActivity.class);
                    detailsIntent.putExtra(Constants.ID,getIntent().getStringExtra(Constants.ID));
                    detailsIntent.putExtra(Constants.NAME,productName.getText().toString());
                    detailsIntent.putExtra(Constants.PRICE,price.getText().toString());
                    detailsIntent.putExtra(Constants.QUANTITY,quantity.getText().toString());
                    detailsIntent.putExtra(Constants.DELIVERED,delivery.getText().toString());
                    detailsIntent.putExtra(Constants.PRODUCT_IMAGE,itemImage);
                    detailsIntent.putExtra(Constants.TYPE,Constants.RETURNED);
                    startActivity(detailsIntent);
                }
                break;
            case R.id.back_addrss:
                finish();
                break;
            case R.id.leavefeedback:
                new WriteReviewDialog(OrderDetailsActivity.this,false, productId, "", 5, "",  callback).show();
                break;
            case R.id.track:
                startActivity(new Intent(OrderDetailsActivity.this,TrackOrderActivity.class).putExtra(Constants.TRACKING_ID,trackingId));
        }
    }


     Callback callback = new Callback() {
        @Override
        public void CallBack(boolean isChange) {

        }
    };

    Response.Listener<JSONObject> orderResponseResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model response = new Model(jsonObject.toString());
            Model model = new Model(response.getData());
            if(response.getStatus().equals(Constants.SUCCESS_CODE)){
                JSONArray imageArray = null;
                try {
                    imageArray = model.getJSONArray(NamePair.IMAGE);
                    PicassoCache.getPicassoInstance(OrderDetailsActivity.this).load(C.ASSET_URL + imageArray.get(0).toString()).into(productImage);
                    itemImage = imageArray.get(0).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                trackingId = model.getTrackingNumber();
                productName.setText(model.getProductName());
                productPrice.setText("$ "+C.FormatterValue(Float.parseFloat(model.getSalePrice())));
                quantity.setText(""+model.getItemQuantity());
                delivery.setText(response.getDays());
                shippingCharge.setText("$"+Float.parseFloat(model.getShippingCharges()));
                price.setText("$ "+C.FormatterValue(Float.parseFloat(model.getSalePrice())));
                productId = ""+model.getProductId();
                Double totalPrice = Double.parseDouble(model.getSalePrice()) + Double.parseDouble(model.getShippingCharges());
                totalAmount.setText("$ "+C.FormatterValue(Float.parseFloat(String.valueOf(totalPrice))));
            }
        }
    };

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(OrderDetailsActivity.this,VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }
}
