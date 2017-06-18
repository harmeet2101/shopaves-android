package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;


public class OrderHistory extends AppCompatActivity implements Constants,View.OnClickListener{
    private RecyclerView recyclerView;
    private AppStore aps;
    private HistoryAdapter adapter;
    private ProgressDialog pd;
    private String historyType = Constants.ONGOING;
    private TextView ongoingText,deleveredText,cencelText,returnedText;
    private List<MyOrder> myItemsList = new ArrayList<>();
    private List<MyOrder> ongoingOrderList = new ArrayList<>();
    private List<MyOrder> deliveredOrderList = new ArrayList<>();
    private List<MyOrder> canceledOrderList = new ArrayList<>();
    private List<MyOrder> returnedOrderList = new ArrayList<>();
    private LinkedHashSet<Integer> orderIds = new LinkedHashSet<>();
    private ArrayList<Model> ongoingModel = new ArrayList<>();
    private ArrayList<Model> deliveredModel = new ArrayList<>();
    private ArrayList<Model> canceledModel = new ArrayList<>();
    private ArrayList<Model> returnedModel = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.orderhistory));
        ongoingText = (TextView)findViewById(R.id.ongingtext);
        deleveredText = (TextView)findViewById(R.id.deleveredtext);
        cencelText = (TextView)findViewById(R.id.cenceledtext);
        returnedText = (TextView)findViewById(R.id.returntext);
        aps = new AppStore(this);
        recyclerView = (RecyclerView) findViewById(R.id.order_recyclevw);
       // setHistoryAdapter();
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.ongoinglist).setOnClickListener(this);
        findViewById(R.id.deleveredlist).setOnClickListener(this);
        findViewById(R.id.cancelorderlist).setOnClickListener(this);
        findViewById(R.id.returnedlist).setOnClickListener(this);
        if (aps.getData(USER_ID)!=null){
            makeOrderRequest();
        }
    }

    private void renderingView(){
        cencelText.setTextColor(getResources().getColor(R.color.fade_white));
        returnedText.setTextColor(getResources().getColor(R.color.fade_white));
        deleveredText.setTextColor(getResources().getColor(R.color.fade_white));
        ongoingText.setTextColor(getResources().getColor(R.color.fade_white));

        findViewById(R.id.ongoingview).setVisibility(View.INVISIBLE);
        findViewById(R.id.deleveredview).setVisibility(View.INVISIBLE);
        findViewById(R.id.cancelview).setVisibility(View.INVISIBLE);
        findViewById(R.id.returnview).setVisibility(View.INVISIBLE);
    }

    private void makeOrderRequest(){
        Map<String,String > map = new HashMap<>();
        map.put("uid",aps.getData(USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GETORDER,map,r,e);
        pd = C.getProgressDialog(this);
    }
    Response.ErrorListener e = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(OrderHistory.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
    Response.Listener<JSONObject> r = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Model data1[] = model.getDataArray();
                myItemsList.clear();
//                Model productArray[] = data1.getProductArr();
                if (data1.length>0) {
                    for (Model data : data1) {
                        orderIds.add(data.getOrderId());
                        if(data.getStatus().equals("CANCELED")){
                            canceledModel.add(data);
                        }else if(data.getStatus().equals("RETURNED")){
                            returnedModel.add(data);
                        }else if(data.getStatus().equals("DELIVERED")){
                                deliveredModel.add(data);
                        }else
                            ongoingModel.add(data);
                        //  setOrderHistory(data.getStatus(),data);
                 /*       JSONArray js = data.getJSONArray("image");
                        if (js.length()>0){
                            try {
                                String url = js.get(0).toString();
                                myItemsList.add(new MyOrder(""+data.getId(),data.getShipId(),data.getProductName(),url,""+data.getSalePrice(),""+data.getQty(),data.getDays(),data.getShippingCharges()));
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }*/
                    }
                    for (int id : orderIds) {
                        boolean isShow = true;
                        for (Model data : ongoingModel) {
                            if (data.getOrderId() == id) {
                                    setOrderHistory(data,isShow);
                                    isShow = false;
                            }
                        }
                        isShow = true;
                        for (Model data : canceledModel) {
                            if (data.getOrderId() == id) {
                                    setOrderHistory(data,isShow);
                                    isShow = false;
                            }
                        }
                        isShow = true;
                        for (Model data : returnedModel) {
                            if (data.getOrderId() == id) {
                                    setOrderHistory(data,isShow);
                                    isShow = false;
                            }
                        }
                        isShow = true;
                        for (Model data : deliveredModel) {
                            if (data.getOrderId() == id) {
                                    setOrderHistory(data,isShow);
                                    isShow = false;
                            }
                        }
                    }
                }
                updateOrderAdapter(ongoingOrderList);
                setHistoryAdapter();
            }else{
                Toast.makeText(OrderHistory.this, model.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void setHistoryAdapter(){
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setOrderHistory(Model data,boolean isShow){
        JSONArray js = data.getJSONArray("image");
        String url = "";
        if (js.length()>0){
            try {
                 url = js.get(0).toString();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

         if(data.getStatus().equals("CANCELED")){
            canceledOrderList.add(new MyOrder(""+data.getId(),""+data.getOrderId(),data.getTimeStampSmall(),data.getProductName(),""+data.getProductId(),url,""+data.getSalePrice(),""+data.getQty(),data.getDays(),data.getShippingCharges(),isShow));
        }else if(data.getStatus().equals("RETURNED")){
            returnedOrderList.add(new MyOrder(""+data.getId(),""+data.getOrderId(),data.getTimeStampSmall(),data.getProductName(),""+data.getProductId(),url,""+data.getSalePrice(),""+data.getQty(),data.getDays(),data.getShippingCharges(),isShow));
        }else if(data.getStatus().equals("DELIVERED")){
            deliveredOrderList.add(new MyOrder(""+data.getId(),""+data.getOrderId(),data.getTimeStampSmall(),data.getProductName(),""+data.getProductId(),url,""+data.getSalePrice(),""+data.getQty(),data.getDays(),data.getShippingCharges(),isShow));
        }else
            ongoingOrderList.add(new MyOrder(""+data.getId(),""+data.getOrderId(),data.getTimeStampSmall(),data.getProductName(),""+data.getProductId(),url,""+data.getSalePrice(),""+data.getQty(),data.getDays(),data.getShippingCharges(),isShow));

    }

    private void updateOrderAdapter(List<MyOrder> mylistItems){
        adapter = new HistoryAdapter(mylistItems);
        setHistoryAdapter();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ongoinglist:
                historyType = Constants.ONGOING;
                renderingView();
                ongoingText.setTextColor(Color.WHITE);
                findViewById(R.id.ongoingview).setVisibility(View.VISIBLE);
                updateOrderAdapter(ongoingOrderList);
                break;
            case R.id.deleveredlist:
                historyType = Constants.DELIVERED;
                renderingView();
                deleveredText.setTextColor(Color.WHITE);
                findViewById(R.id.deleveredview).setVisibility(View.VISIBLE);
                updateOrderAdapter(deliveredOrderList);
                break;
            case R.id.cancelorderlist:
                historyType = Constants.CANCELED;
                renderingView();
                cencelText.setTextColor(Color.WHITE);
                findViewById(R.id.cancelview).setVisibility(View.VISIBLE);
                updateOrderAdapter(canceledOrderList);
                break;
            case R.id.returnedlist:
                historyType = Constants.RETURNED;
                renderingView();
                returnedText.setTextColor(Color.WHITE);
                findViewById(R.id.returnview).setVisibility(View.VISIBLE);
                updateOrderAdapter(returnedOrderList);
                break;
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<MyOrder> source;
        public HistoryAdapter(List<MyOrder> source) {
            this.source=source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_ongoing_history, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final MyOrder items = source.get(position);
            PicassoCache.getPicassoInstance(OrderHistory.this).load(C.ASSET_URL + items.productImage).into(holder.imageView);
            holder.product_name.setText(items.productName);
            holder.price.setText("$ "+C.FormatterValue(Float.parseFloat(items.salePrice)));
            holder.quant.setText(items.count);
            Log.e("delivery location",items.delivery);
            holder.dating.setText(items.delivery);
            holder.order_id.setText("("+items.ship_id+")");
            holder.date.setText(C.getDateFormat(items.time));
            holder.orderView.setVisibility(items.isDifferent ? View.VISIBLE : View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsIntent = new Intent(OrderHistory.this,OrderDetailsActivity.class);
                    detailsIntent.putExtra(Constants.ID,items.id);
                    detailsIntent.putExtra(Constants.TYPE,historyType);
                    detailsIntent.putExtra(Constants.ORDER_ID,items.id);
                    detailsIntent.putExtra(Constants.DATE,C.getDateFormat(items.time));
                    startActivity(detailsIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
        public int getItemViewType(int position) {
            return position;
        }

    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView  product_name, price, quant, dating,date,order_id;
        LinearLayout orderView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.product_img);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.price);
            quant = (TextView) itemView.findViewById(R.id.quant);
            dating = (TextView) itemView.findViewById(R.id.dating);
            date = (TextView) itemView.findViewById(R.id.date);
            order_id = (TextView) itemView.findViewById(R.id.orderId);
            orderView = (LinearLayout)itemView.findViewById(R.id.orderview);
        }
    }

    private class MyOrder{
        String id,ship_id,productName,salePrice,productImage,count,delivery,shippingCharge,productId,time;
        boolean isDifferent;

        public MyOrder(String id,String ship_id,String time,String productName,String productId, String productImage,String salePrice,String count, String delivery,String shippingCharge,boolean isDifferent) {
            this.id = id;
            this.productId = productId;
            this.ship_id = ship_id;
            this.productImage = productImage;
            this.salePrice = salePrice;
            this.productName = productName;
            this.count = count;
            this.delivery = delivery;
            this.shippingCharge = shippingCharge;
            this.isDifferent = isDifferent;
            this.time = time;
        }
    }
}
