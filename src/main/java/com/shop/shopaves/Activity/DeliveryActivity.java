package com.shop.shopaves.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.textservice.SpellCheckerInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Dialog.CartSpecificationDialog;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.dataModel.TemporaryCart;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryActivity extends AppCompatActivity implements View.OnClickListener,Response.Listener<JSONObject>,Response.ErrorListener{
    private AppStore aps;
    private ProgressDialog pd;
    private TextView userName,address,mobile,amount,productsPrice,shippingCharge,totalCharge,applyText;
    private long addressId;
    private LinearLayout cartitems;
    private List<MyItems> myItemsList = new ArrayList<>();
    private int counter=1;
    private Float productTotalAmount = 0.0f,shippingAmount = 0.0f,totalAmount = 0.0f;
    private String productname,price,realprice,name,prd_img_url,product_id,user_img_url;
    private  String adress,dtime,scharges,url,mrp;
    private long address_id;
    private EditText couponCode;
    private String cartId;
    private boolean isCouponAvailable;
    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId("ARZnTE2-_ntS7IRrw_Q7YF4uy8fYnD1Nb0WP88_ee0I2zEoUno5ad1Dw3ZII5nmyBW3ow42aYzioNkQa");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("DELIVERY");
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        cartitems = (LinearLayout) findViewById(R.id.cartitems);
        userName = (TextView) findViewById(R.id.userName);
        address = (TextView) findViewById(R.id.address);
        mobile = (TextView) findViewById(R.id.mobile);
        amount = (TextView) findViewById(R.id.amount);
        productsPrice = (TextView)findViewById(R.id.productsprice);
        shippingCharge = (TextView)findViewById(R.id.shippingcharge);
        totalCharge = (TextView)findViewById(R.id.totalamount);
        couponCode = (EditText)findViewById(R.id.code);
        applyText = (TextView)findViewById(R.id.applytext);

        aps = new AppStore(this);
        findViewById(R.id.changeaddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DeliveryActivity.this,ChangeShippingAddressActivity.class),100);
            }
        });
        findViewById(R.id.check_lay).setOnClickListener(this);
        if (aps.getData(Constants.USER_ID)!=null){
            myItemsList.clear();
            makeRequest();
            fetchCartApi();
//            setCart();
        }
        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   startActivity(new Intent(DeliveryActivity.this,CoupanActivity.class));
                if(!TextUtils.isEmpty(couponCode.getText().toString()))
                {
                    if(!isCouponAvailable)
                        makeApplyCouponRequest(cartId,couponCode.getText().toString());
                }else if(isCouponAvailable || applyText.getText().toString().equalsIgnoreCase("clear"))
                    makeClearCouponRequest(cartId);
                else
                    Toast.makeText(DeliveryActivity.this,"please  enter coupon code",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }

    private void makeApplyCouponRequest(String cartId,String couponcode){
        pd = C.getProgressDialog(this);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("cartId",cartId);
        hashMap.put("couponCode",couponcode);
        Net.makeRequestParams(C.APP_URL+ApiName.APPLY_COUPON_API,hashMap,couponResponse,this);
    }

    private void makeRequest(){
        Map<String,String> map = new HashMap<>();
        map.put(Constants.USERID,aps.getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GET_ADDRESS,map,response,error);
        Log.e("address_param",map.toString());
        pd = C.getProgressDialog(this);
    }

    private void fetchCartApi(){
        if (aps.getData(Constants.USER_ID)!=null){
            Map<String ,String> map = new HashMap<>();
            map.put("uid",aps.getData(Constants.USER_ID));
            Net.makeRequest(C.APP_URL+ Constants.GETCART,map,r_fetch,error);
        }else {
            C.setLoginMessage(this);
        }
    }
    public static double getTotal(List<MyItems> list){
        double total=0.0;
        for(int i=0;i<list.size();i++){
            total=total+(Double.parseDouble(list.get(i).mrpPrice) * Integer.parseInt(list.get(i).count));
        }
        return total;
    }

    public Response.ErrorListener error = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                volleyError = error;
            }
            Log.e("error",volleyError.toString());
            Toast.makeText(DeliveryActivity.this, "" + VolleyErrors.setError(volleyError), Toast.LENGTH_LONG).show();
        }
    };

    public Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("address_response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                Model[] models = model.getDataArray();
                for (Model data : models) {
                    boolean isCurrent = data.getCurrent();
                    if (isCurrent){
                        userName.setText(data.getName());
                        address.setText(data.getAddress() + " " + data.getAddressLine2() + "\n" + data.getCity()+", "
                                + data.getState()+", "+ data.getCountry()+", "+ data.getZipCode());
                        mobile.setText(data.getMobile());
                        addressId = data.getId();
                        aps.setData(Constants.ADDRESS_ID,""+addressId);
                    }/*else {
                        startActivityForResult(new Intent(DeliveryActivity.this,ChangeShippingAddressActivity.class),100);
                    }*/
                }
            }
        }
    };
    Response.Listener<JSONObject> r_fetch = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("fetch_Response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                try {
                    Model data1 = new Model(model.getData());
                    cartId = ""+data1.getId();
                    if(jsonObject.has(Constants.MESSAGE)) {
                        if(model.getMessage().length()>2)
                            Toast.makeText(DeliveryActivity.this, model.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    if(data1.getAddressID()!=null)
                        address_id = Long.parseLong(data1.getAddressID());
                    adress = data1.getAddress();
                    Model productArray[] = data1.getProductArr();
                    if (productArray.length>0){
                        cartitems.removeAllViews();
                        isCouponAvailable = false;
//                    TemporaryCart.deleteAll(TemporaryCart.class);
                        for(Model data : productArray){
                            JSONArray js = data.getJSONArray("image");
                            if (js.length()>0)
                                try {
                                    url = js.get(0).toString();
                                    productname = data.getProductName();
                                    price  = data.getSalePrice();
                                    name =data.getUserName();
                                    product_id = ""+data.getId();
                                    user_img_url= data.getUserImage();
                                    counter = Integer.parseInt(data.getQty());
                                    mrp = data.getMRP();
//                                    new TemporaryCart(productname,price,name,url
//                                            ,product_id,user_img_url,""+counter).save();
                                    productTotalAmount = productTotalAmount+Float.parseFloat(price);
                                    shippingAmount = Float.parseFloat(data.getShippingCharges());
                                    myItemsList.add(new MyItems(product_id,url,productname,price,mrp,user_img_url,name,""+counter,data.getShippingCharges(),data.getDays(),data.getCoupon().equals("N") ? "" : data.getCoupon()));
                                    cartitems.addView(addCartView(new MyItems(product_id,url,productname,price,mrp,user_img_url,name,""+counter,data.getShippingCharges(),data.getDays(),data.getCoupon().equals("N") ? "" : data.getCoupon())));
                                    amount.setText("$"+getTotal(myItemsList));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                        }
                        setCartProductPrice(productTotalAmount,shippingAmount,true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    private Response.Listener<JSONObject> couponResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("Response",jsonObject.toString());
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Model productArray[] = model.getDataArray();

                cartitems.removeAllViews();
                isCouponAvailable = false;
//                    TemporaryCart.deleteAll(TemporaryCart.class);
                if(productArray != null) {
                    for (Model data : productArray) {
                        JSONArray js = data.getJSONArray("image");
                        if (js.length() > 0)
                            try {
                                url = js.get(0).toString();
                                productname = data.getProductName();
                                price = data.getSalePrice();
                                name = data.getUserName();
                                product_id = "" + data.getId();
                                user_img_url = data.getUserImage();
                                counter = Integer.parseInt(data.getQty());
                                mrp = data.getMRP();
//                                    new TemporaryCart(productname,price,name,url
//                                            ,product_id,user_img_url,""+counter).save();
                                productTotalAmount = productTotalAmount + Float.parseFloat(price);
                                shippingAmount = Float.parseFloat(data.getShippingCharges());
                                myItemsList.add(new MyItems(product_id, url, productname, price, mrp, user_img_url, name, "" + counter, data.getShippingCharges(), data.getDays(), data.getCoupon().equals("N") ? "" : data.getCoupon()));
                                cartitems.addView(addCartView(new MyItems(product_id, url, productname, price, mrp, user_img_url, name, "" + counter, data.getShippingCharges(), data.getDays(), data.getCoupon().equals("N") ? "" : data.getCoupon())));
                                amount.setText("$" + getTotal(myItemsList));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        setCartProductPrice(productTotalAmount, shippingAmount, true);
                    }
                }
            }
        }
    };

    private void setCart(){
        List<TemporaryCart> categoryList = TemporaryCart.listAll(TemporaryCart.class);
        for (int i = 0; i < categoryList.size(); i++) {
            TemporaryCart categoryValue = categoryList.get(i);
            myItemsList.add(new MyItems(categoryValue.product_id,categoryValue.product_img,categoryValue.itemName,categoryValue.priceValue,categoryValue.priceValue,categoryValue.user_img,categoryValue.username,categoryValue.count,categoryValue.shippingCharge,"",""));
        }
    }

    private View addCartView(final MyItems myItems){
        final TextView productName,mrpPrice,salePrice,count,offDiscount,couponCode,productAddress;
        final ImageView productImage,delete_item,increase,decrease,option;
        final View view = LayoutInflater.from(this).inflate(R.layout.custom_cart_layout,null);
        productName = (TextView)view.findViewById(R.id.product_name);
        mrpPrice = (TextView)view.findViewById(R.id.mrpprice);
        salePrice = (TextView)view.findViewById(R.id.price);
        count = (TextView)view.findViewById(R.id.count);
        offDiscount = (TextView)view.findViewById(R.id.offdiscount);
        productImage = (ImageView)view.findViewById(R.id.product_img);
        //delete_item  = (ImageView)view.findViewById(R.id.delete_item);
        increase  = (ImageView)view.findViewById(R.id.incres_count);
        decrease  = (ImageView)view.findViewById(R.id.decres_count);
        couponCode = (TextView)view.findViewById(R.id.couponcode);
        option=(ImageView)view.findViewById(R.id.option);
        productAddress = (TextView)view.findViewById(R.id.address);
        productName.setText(productname);
        mrpPrice.setText("$ "+myItems.mrpPrice);
        productAddress.setText(myItems.shippingAddress);
        salePrice.setText("$ "+C.formateValue(Double.parseDouble(myItems.salePrice)));
        if(!TextUtils.isEmpty(myItems.couponCode))
            couponCode.setText(myItems.couponCode +" Applied");
        else{
            couponCode.setText("");
        }
        if(!TextUtils.isEmpty(myItems.couponCode) && !isCouponAvailable) {
            applyText.setTextColor(Color.RED);
            isCouponAvailable = true;
            applyText.setText("CLEAR");
        }else if(!isCouponAvailable){
            applyText.setTextColor(getResources().getColor(R.color.black_pd));
            applyText.setText("APPLY");
            //  isCouponAvailable = false;
        }
        mrpPrice.setPaintFlags(mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        count.setText(myItems.count);

        offDiscount.setText("" + C.FormatterValue((100 - (Float.parseFloat(myItems.salePrice) * 100) / Float.parseFloat(myItems.mrpPrice)))  + " % off");
        view.findViewById(R.id.offdiscountview).setVisibility((100 - (Float.parseFloat(myItems.salePrice) * 100) / Float.parseFloat(myItems.mrpPrice)) == 0 ? View.INVISIBLE : View.VISIBLE);

        PicassoCache.getPicassoInstance(this).load(C.getImageUrl(myItems.productImage)).into(productImage);

//        delete_item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryActivity.this);
//                builder.setMessage("Do you want to delete this item?")
//                        .setCancelable(false)
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
////                                cartitems.removeView(view);
//                                Map<String,String> pr = new HashMap<String, String>();
//                                pr.put("productId", myItems.id);
//                                Log.e("deleteCart",pr.toString());
//                                Net.makeRequestParams(C.APP_URL + ApiName.DELETECARTPRODUCT, pr, r_delete, error);
//                                pd = C.getProgressDialog(DeliveryActivity.this);
//
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                //  Action for 'NO' Button
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.setTitle("Delete Item");
//                alert.show();
//            }
//        });
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupMenu popup = new PopupMenu(DeliveryActivity.this, option);
                popup.getMenuInflater().inflate(R.menu.cartoption, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.info:
                                popup.dismiss();
                                new CartSpecificationDialog(DeliveryActivity.this).show();
                                /*Model m[] = myItems.productSpc;
                                for(int i=0;i<=m.length;i++){
                                    m[i].getName();
                                    m[i].getValue();
                                   }
                              */
                               /* setContentView(R.layout.cart_specification);

                                Window window = AddToCartActivity.this.getWindow();
                                WindowManager.LayoutParams wlp = window.getAttributes();
                                getWindow().setGravity(Gravity.BOTTOM);
                                window.setAttributes(wlp);
                                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(getApplicationContext(),AddToCartActivity.class));
                                    }
                                });*/
                              /*deleteDialog.show();
                                setContentView(R.layout.activity_cart_specification);
                                getWindow().setGravity(Gravity.BOTTOM);
                                findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });*/
                                //m[0].getValue();
                                break;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryActivity.this);
                                builder.setMessage("Do you want to delete this item?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog, int id) {
                               cartitems.removeView(view);
                                  Map<String,String> pr = new HashMap<String, String>();
                                     pr.put("productId", myItems.id);
                                     Log.e("deleteCart",pr.toString());
                                     Net.makeRequestParams(C.APP_URL + ApiName.DELETECARTPRODUCT, pr, r_delete, error);
                                        pd = C.getProgressDialog(DeliveryActivity.this);

                           }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                               dialog.cancel();
                           }
                       });
               AlertDialog alert = builder.create();
               alert.setTitle("Delete Item");
                alert.show();
                                break;

                        }
                        return true;
                    }
                });
                popup.show();

            }

        });



        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter = counter+1;
                count.setText(""+counter);
                productTotalAmount =  productTotalAmount + Float.parseFloat(myItems.salePrice);
                shippingAmount = shippingAmount + Float.parseFloat(myItems.shippingCharge);
                setCartProductPrice(productTotalAmount,shippingAmount,true);

//                new TemporaryCart(myItems.productName,myItems.mrpPrice,myItems.userName,myItems.productImage
//                        ,myItems.id,myItems.userImage,""+count.getText().toString()).save();
//
//                addToCartApi(count.getText().toString());
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter>1){
                    counter = counter-1;
                    count.setText(""+counter);
                    productTotalAmount =  productTotalAmount - Float.parseFloat(myItems.salePrice);
                    shippingAmount = shippingAmount - Float.parseFloat(myItems.shippingCharge);
                    setCartProductPrice(productTotalAmount,shippingAmount,false);
//                    new TemporaryCart(myItems.productName,myItems.mrpPrice,myItems.userName,myItems.productImage
//                            ,myItems.id,myItems.userImage,""+count.getText().toString()).save();
//
//                    addToCartApi(count.getText().toString());
                }
            }
        });

        return view;
    }
    Response.Listener<JSONObject> r_delete = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("delete_response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                fetchCartApi();
            }
        }
    };

    Response.Listener<JSONObject> r_pay = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("payment_response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                startActivity(new Intent(DeliveryActivity.this,PaymentSuccessFailed.class));
                finish();
            }else{
                startActivity(new Intent(DeliveryActivity.this,PaymentFailedActivity.class));
            }
        }
    };
    /**
     * Preparing final cart amount that needs to be sent to PayPal for payment
     * */
  /*  private PayPalPayment prepareFinalCart() throws Exception{

        PayPalItem[] items = new PayPalItem[myItemsList.size()];
//        items = myItemsList.toArray(items);

        // Total amount
        BigDecimal subtotal = PayPalItem.getItemTotal(items);

        // If you have shipping cost, add it here
        BigDecimal shipping = new BigDecimal("0.0");

        // If you have tax, add it here
        BigDecimal tax = new BigDecimal("0.0");

        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(
                shipping, subtotal, tax);

        BigDecimal amount = subtotal.add(shipping).add(tax);

        PayPalPayment payment = new PayPalPayment(
                amount,
                "USD",
                "Description about transaction. This will be displayed to the user.",
                PayPalPayment.PAYMENT_INTENT_SALE);

        payment.items(items).paymentDetails(paymentDetails);

        // Custom field like invoice_number etc.,
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.check_lay:
                Intent intent=new Intent(DeliveryActivity.this,StripePaymentActivity.class);
                startActivity(intent);
                /*PayPalPayment payment = new PayPalPayment(new BigDecimal(totalAmount), "USD", "Products",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(this, PaymentActivity.class);

                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                try {
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, 0);
                break;*/
        }
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==RESULT_OK){
            makeRequest();
        }else if (requestCode==0 && resultCode==RESULT_OK){
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                    String id = confirm.toJSONObject().getJSONObject("response").getString("id");

                    Map<String,String> map = new HashMap<>();
                    map.put("uid",aps.getData(Constants.USER_ID));
                    map.put("paymentId",id);
                    Net.makeRequest(C.APP_URL+ ApiName.CHECK_OUT_API,map,r_pay,error);
                    Log.e("payment_param",map.toString());

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }*/


    private void makeClearCouponRequest(String cartId){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("cartId",cartId);
        Net.makeRequest(C.APP_URL+ApiName.CLEAR_COUPON_API,hashMap,couponResponse,this);
    }
    private void setCartProductPrice(Float productPrice, Float shippingAmount,boolean isAdd){
        shippingCharge.setText("$ "+C.FormatterValue(shippingAmount));
        productsPrice.setText("$ "+C.FormatterValue(productPrice));
        totalAmount = shippingAmount + productPrice;
        totalCharge.setText("$ "+C.FormatterValue(totalAmount));
        amount.setText("$ "+C.FormatterValue(totalAmount));
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(DeliveryActivity.this,VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            isCouponAvailable = false;
            Toast.makeText(DeliveryActivity.this,model.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private class MyItems{
        String id,userName,userImage,productName,mrpPrice,salePrice,productImage,count,shippingCharge,shippingAddress,couponCode;
        public MyItems(String id, String productImage,String productName, String salePrice, String mrpPrice, String userImage, String userName,String count,String shippingCharge,String shippingAddress,String couponCode) {
            this.id = id;
            this.productImage = productImage;
            this.salePrice = salePrice;
            this.mrpPrice = mrpPrice;
            this.userImage = userImage;
            this.userName = userName;
            this.productName = productName;
            this.count = count;
            this.shippingCharge = shippingCharge;
            this.shippingAddress = shippingAddress;
            this.couponCode = couponCode;
        }
    }
}
