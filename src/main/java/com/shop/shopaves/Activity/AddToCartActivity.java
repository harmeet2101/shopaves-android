package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Dialog.CartSpecificationDialog;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddToCartActivity extends AppCompatActivity implements View.OnClickListener,Constants {

    long address_id;
    boolean isActive;
    private RecyclerView recyclerView;
    private List<MyItems> myItemsList = new ArrayList<>();
    private String productname,price,realprice,name,prd_img_url,product_id,user_img_url,type;
    private AppStore appStore;
    private int counter=1,qty,UserId;
    private TextView amount,countReferenceText,itemPriceReferenceText;
    private ProgressDialog pd;
    Response.ErrorListener e = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(AddToCartActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
    private CartAdapter cartAdapter;
    private String adress,dtime,scharges;
    private LinearLayout checkout;
    private Map<String,String> productMap = new HashMap<>();
    private Map<String,String> cartMap = new HashMap<>();
    private JSONArray arrayList = new JSONArray();
    Response.Listener<JSONObject> r_fetch = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("fetch_Response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                try {
                    Log.e("result",""+jsonObject.getString("result"));
                    Model data1 = new Model(model.getData());
                    myItemsList.clear();
                    if(data1.getAddressID()!=null)
                        address_id = Long.parseLong(data1.getAddressID());
                    adress = data1.getAddressID();
                    appStore.setData(Constants.ADDRESS_ID,adress);
                    dtime = data1.getDTime();
                    scharges = data1.getSCharge();
                    Model productArray[] = data1.getProductArr();
                    if (productArray.length>0){
                        for(Model data : productArray){
                            JSONArray js = data.getJSONArray("image");
                            if (js.length()>0){
                                try {
                                    String url = js.get(0).toString();
                                    productMap.put("qty",data.getQty());
                                    productMap.put("productId",""+data.getProductId());
                                    arrayList.put(new JSONObject(productMap));
                                    cartMap.put("product",arrayList.toString());
                                    myItemsList.add(new MyItems(""+data.getId(),""+data.getProductUserId(),""+data.getProductId(),url,data.getProductName(),""+data.getSalePrice(),""+data.getMRP(),data.getUserImage(),data.getUserName(),""+data.getQty(),data.getProducSpc()));
                                    cartAdapter.notifyDataSetChanged();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                    findViewById(R.id.textOfCart).setVisibility(myItemsList.size() > 0 ? View.GONE : View.VISIBLE);
                    setCartAdapter();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(AddToCartActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };
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
    private SharedPreferences sp;
    private Gson gson;
    private String addType = "increase";
    Response.Listener<JSONObject> cartIncreaseDecreaseResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                if(addType.equals("increase")){
                    Log.e("totalAmountPrice",amount.getText().toString().substring(1));
                    Log.e("totalProductPrice",itemPriceReferenceText.getText().toString().substring(1));
                    Log.e("addType",addType);
                    float totalAmount = (Float.parseFloat(amount.getText().toString().substring(1).replace(",","")) + Float.parseFloat(itemPriceReferenceText.getTag().toString()));
                    amount.setText("$ "+C.FormatterValue(totalAmount));
                    float itemPrice = (Float.parseFloat(itemPriceReferenceText.getText().toString().substring(1).replace(",",""))+ Float.parseFloat(itemPriceReferenceText.getTag().toString()));
                    itemPriceReferenceText.setText("$ "+C.FormatterValue(itemPrice));
                    // itemPriceReferenceText.setText("$ "+(Double.parseDouble(itemPriceReferenceText.getText().toString().substring(1).replace(",",""))+ Double.parseDouble(itemPriceReferenceText.getTag().toString())));
                    countReferenceText.setText(""+(Integer.parseInt(countReferenceText.getText().toString())+1));
                }else{
                    Log.e("totalAmountPrice",amount.getText().toString().substring(1));
                    Log.e("totalProductPrice",itemPriceReferenceText.getText().toString().substring(1));
                    Log.e("addType",addType);
                    float totalAmount = (Float.parseFloat(amount.getText().toString().substring(1).replace(",","")) - Float.parseFloat(itemPriceReferenceText.getTag().toString()));
                    amount.setText("$ "+C.FormatterValue(totalAmount));
                    float itemPrice = (Float.parseFloat(itemPriceReferenceText.getText().toString().substring(1).replace(",",""))- Float.parseFloat(itemPriceReferenceText.getTag().toString()));
                    itemPriceReferenceText.setText("$ "+C.FormatterValue(itemPrice));
                    countReferenceText.setText(""+(Integer.parseInt(countReferenceText.getText().toString())-1));
                }
            }
        }
    };

    public static double getTotal(List<MyItems> list){
        double total=0.0;
        for(int i=0;i<list.size();i++){
            total=total+(Double.parseDouble(list.get(i).salePrice) * Integer.parseInt(list.get(i).count));
        }
        Log.e("total",""+total);
        return total;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        recyclerView = (RecyclerView)findViewById(R.id.cartlist);
        TextView title = (TextView)findViewById(R.id.title);
        amount = (TextView)findViewById(R.id.amount);
        checkout = (LinearLayout) findViewById(R.id.checkout);
        title.setText("CART");
        appStore = new AppStore(this);
        sp = getSharedPreferences("KEY", Context.MODE_PRIVATE);
        Intent intent = getIntent();

        if (intent!=null && intent.getExtras()!=null){
            productname = intent.getStringExtra(PRODUCT);
            price = intent.getStringExtra(PRICE);
            realprice = intent.getStringExtra(PRICE);
            name = intent.getStringExtra(NAME);
            prd_img_url = intent.getStringExtra(PRODUCT_BITMAP);
            product_id = intent.getStringExtra(PRODUCT_ID);
            user_img_url = intent.getStringExtra(USER_IMG);
            qty = intent.getIntExtra(QUANTITY,1);
            type = intent.getStringExtra(TYPE);
            if (!TextUtils.isEmpty(product_id)) {
                productMap.put("qty", "1");
                productMap.put("productId", product_id);
                arrayList.put(new JSONObject(productMap));
            }
        }
        cartMap.put("addressId",appStore.getData(Constants.ADDRESS_ID));
        cartMap.put("userId",appStore.getData(Constants.USER_ID));
        cartMap.put("product",arrayList.toString());
        gson = new Gson();
        if (type!=null && type.equalsIgnoreCase("FromProduct")){
            String empty_list = gson.toJson(new ArrayList<MyItems>());
            List<MyItems> mSelectedList = gson.fromJson(sp.getString("cartItem", empty_list),
                    new TypeToken<ArrayList<MyItems>>() {
                    }.getType());
            if (mSelectedList.size()>0 && mSelectedList!=null){
                for (int i=0;i<mSelectedList.size();i++) {
                    MyItems x = mSelectedList.get(i);
                    myItemsList.add(new MyItems(x.id,"",x.productId,x.productImage,x.productName,x.salePrice,x.mrpPrice,x.userImage,x.userName,"1",null));
                }
            }
            myItemsList.add(new MyItems(product_id,"","",prd_img_url,productname,price,realprice,user_img_url,name,"1",null));
            String templist = gson.toJson(myItemsList);
            sp.edit().putString("cartItem",templist).commit();
        }else if (null!=appStore.getData(Constants.USER_ID)){
            fetchCartApi();
            isActive=true;
        }else{
            isActive=false;
            String empty_list = gson.toJson(new ArrayList<MyItems>());
            List<MyItems> mSelectedList = gson.fromJson(sp.getString("cartItem", empty_list),
                    new TypeToken<ArrayList<MyItems>>() {
                    }.getType());
            for (int i=0;i<mSelectedList.size();i++) {
                MyItems x = mSelectedList.get(i);
                myItemsList.add(new MyItems(x.id,"",x.productId,x.productImage,x.productName,x.salePrice,x.mrpPrice,x.userImage,x.userName,"1",null));
            }
        }
        setCartAdapter();
        findViewById(R.id.back_addrss).setOnClickListener(this);
        checkout.setOnClickListener(this);
        findViewById(R.id.arr).setOnClickListener(this);
        if (appStore.getData(Constants.USER_ID)==null){
            checkout.setAlpha(0.5f);
            checkout.setEnabled(false);
        }else {
            checkout.setAlpha(1.0f);
            checkout.setEnabled(true);
        }
    }

    private void setCartAdapter(){
        cartAdapter = new CartAdapter(myItemsList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddToCartActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cartAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.checkout:
                if(!TextUtils.isEmpty(appStore.getData(Constants.USER_ID)))
                    startActivityForResult(new Intent(this,DeliveryActivity.class),100);
                else
                    startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.back_addrss:
                finish();
                break;
        }
    }

    private void fetchCartApi(){
        Map<String ,String> map = new HashMap<>();
        map.put("uid",appStore.getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ Constants.GETCART,map,r_fetch,e);
        pd = C.getProgressDialog(this);
    }

    private void addCartApiRequest(String quantity,String id,String productId){
        pd = C.getProgressDialog(this);
        JSONObject map = new JSONObject();
        try {
            map.put("id", id);
            map.put("productId", productId);
            map.put("qty", quantity);
            map.put("userId", appStore.getData(Constants.USER_ID));
        }catch (Exception e){
        }
        Log.e("caratParams",map.toString());
        Net.makeRequest(C.APP_URL + ApiName.ADDCART, map.toString(), cartIncreaseDecreaseResponse, e);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==RESULT_OK){
            fetchCartApi();
        }
    }

    private View addCartView(final MyItems myItems){
        final TextView productName,mrpPrice,salePrice,count,offDiscount;
        final ImageView productImage,option,increase,decrease;
        final View view = LayoutInflater.from(AddToCartActivity.this).inflate(R.layout.custom_cart_layout,null);
        productName = (TextView)view.findViewById(R.id.product_name);
        mrpPrice = (TextView)view.findViewById(R.id.mrpprice);
        salePrice = (TextView)view.findViewById(R.id.price);
        count = (TextView)view.findViewById(R.id.count);
        productImage = (ImageView)view.findViewById(R.id.product_img);
        option  = (ImageView)view.findViewById(R.id.option);
        increase  = (ImageView)view.findViewById(R.id.incres_count);
        decrease  = (ImageView)view.findViewById(R.id.decres_count);
        offDiscount = (TextView)view.findViewById(R.id.offdiscount);
        productName.setText(myItems.productName);
        //mrpPrice.setText("$ "+C.FormatterValue(Float.parseFloat(myItems.mrpPrice)));
        salePrice.setText("$ "+(C.FormatterValue(Float.parseFloat(myItems.salePrice)* Integer.parseInt(myItems.count))));
        salePrice.setTag(C.FormatterValue(Float.parseFloat(myItems.salePrice)));
        count.setText(myItems.count);
        Log.e("myitemscount",""+myItems.count);
        PicassoCache.getPicassoInstance(AddToCartActivity.this).load(C.getImageUrl(myItems.productImage)).into(productImage);
        //offDiscount.setText("" + C.FormatterValue((100 - (Float.parseFloat(myItems.salePrice) * 100) / Float.parseFloat(myItems.mrpPrice)))  + " % off");
        view.findViewById(R.id.offdiscountview).setVisibility((100 - (Float.parseFloat(myItems.salePrice) * 100) / Float.parseFloat(myItems.mrpPrice)) == 0 ? View.INVISIBLE : View.VISIBLE);
        //mrpPrice.setPaintFlags(mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupMenu popup = new PopupMenu(AddToCartActivity.this, option);
                popup.getMenuInflater().inflate(R.menu.cartoption, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.info:
                                popup.dismiss();
                                new CartSpecificationDialog(AddToCartActivity.this).show();
                               // Model m[] = myItems.productSpc;

                                /*Model m[] = myItems.productSpc;
                                for(int i=0;i<=m.length;i++){
                                    String type=m[i].getName();
                                    String value=m[i].getValue();
                                }*/


                                break;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddToCartActivity.this);
                                builder.setMessage("Are you sure want to delete this item?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (isActive){
                                                    cartAdapter.getItems().remove(myItems);
                                                    recyclerView.getRecycledViewPool().clear();
                                                    cartAdapter.notifyDataSetChanged();

                                                    Map<String,String> pr = new HashMap<String, String>();
                                                    pr.put("productId", myItems.id);
                                                    Log.e("deleteCart",pr.toString());
                                                    Net.makeRequestParams(C.APP_URL + ApiName.DELETECARTPRODUCT, pr, r_delete, e);
                                                    pd = C.getProgressDialog(AddToCartActivity.this);

                                                }else{
                                                    cartAdapter.getItems().remove(myItems);
                                                    recyclerView.getRecycledViewPool().clear();
                                                    cartAdapter.notifyDataSetChanged();
                                                    myItemsList.remove(myItems);
                                                    sp.edit().remove("cartItem").commit();

                                                    String templist = gson.toJson(myItemsList);
                                                    sp.edit().putString("cartItem",templist).commit();
                                                }

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
                addType = "increase";
                countReferenceText = count;
                itemPriceReferenceText = salePrice;
                myItems.count = ""+(Integer.parseInt(myItems.count) + 1);
                // counter = Integer.parseInt(myItems.count)+1;
                // count.setText(myItems.count);
                addCartApiRequest(myItems.count,myItems.id,myItems.productId);
                //  getTotal(myItemsList);
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addType = "decrease";
                countReferenceText = count;
                itemPriceReferenceText = salePrice;
                if (Integer.parseInt(myItems.count)>1){
                    myItems.count = ""+(Integer.parseInt(myItems.count) - 1);
                    //  counter = Integer.parseInt(myItems.count)-1;
                    //  count.setText(myItems.count);
                    addCartApiRequest(myItems.count,myItems.id,myItems.productId);
                    //    getTotal(myItemsList);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddToCartActivity.this,ProductDetailsActivity.class).putExtra("productId",myItems.productId));
            }
        });

        return view;
    }

    private class CartAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<MyItems> source;
        public CartAdapter(List<MyItems> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view, parent, false);
            return new MyViewHolder(view);

        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.addCartView.removeAllViews();
            final MyItems myItems = source.get(position);
            PicassoCache.getPicassoInstance(AddToCartActivity.this).load(C.getImageUrl(myItems.userImage)).into(holder.userimage);
            holder.userName.setText(TextUtils.isEmpty(myItems.userName) ? "" : myItems.userName.equals("null") ? "" : "By "+myItems.userName);
            holder.addCartView.addView(addCartView(myItems));
            amount.setText("$ "+C.FormatterValue(Float.parseFloat(""+getTotal(source))));
            holder.userView.setVisibility(View.VISIBLE);
            holder.userView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(AddToCartActivity.this,ProfileActivity.class).putExtra(Constants.USER_ID,myItems.userId));
                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public  List<MyItems> getItems(){
            return source;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        CircleImageView userimage;
        LinearLayout addCartView,userView;
        public MyViewHolder(View itemView) {
            super(itemView);
            userName = (TextView)itemView.findViewById(R.id.username);
            userimage  = (CircleImageView)itemView.findViewById(R.id.usr);
            addCartView = (LinearLayout)itemView.findViewById(R.id.addcart);
            userView = (LinearLayout)itemView.findViewById(R.id.userview);
        }
    }

    private class MyItems{
        Model productSpc[];
        String id,userName,userImage,productName,mrpPrice,salePrice,productImage,count,productId,userId;
        public MyItems(String id,String userId,String productId, String productImage,String productName, String salePrice, String mrpPrice, String userImage, String userName,String count,Model productSpc[]) {
            this.id = id;
            this.productImage = productImage;
            this.salePrice = salePrice;
            this.mrpPrice = mrpPrice;
            this.userImage = userImage;
            this.userName = userName;
            this.productName = productName;
            this.count = count;
            this.userId = userId;
            this.productId = productId;
            this.productSpc = productSpc;
        }
    }
}
