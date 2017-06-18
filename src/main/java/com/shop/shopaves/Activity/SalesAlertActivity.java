package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.shop.shopaves.Util.NamePair;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SalesAlertActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener{
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private SalesAdapter salesAdapter;
    private ProgressDialog pd;
    private String type = Constants.NEW_ORDER;
    private List<MyItems> pendingItemsList = new ArrayList<>();
    private List<MyItems> newOrderItemsList = new ArrayList<>();
    private List<MyItems> cancelledOrderList = new ArrayList<>();
    private List<MyItems> deliveredItemsList = new ArrayList<>();
    private int confirmationItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_alert);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.sales_alert));
        tabLayout = (TabLayout) findViewById(R.id.alert_tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.new_orders)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.cancel_orders)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.pending_orders)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.delivered_orders)));
        recyclerView = (RecyclerView) findViewById(R.id.sales_recyclevw);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("tabid",""+tab.getTag());
                if(tab.getText().toString().equals(Constants.NEW_ORDER)){
                    type = Constants.NEW_ORDER;
                    salesAdapter = new SalesAdapter(newOrderItemsList);
                    setHistoryAdapter();
                }else if(tab.getText().toString().equals(Constants.CANCELLED_ORDER)){
                    type = Constants.CANCELLED_ORDER;
                    salesAdapter = new SalesAdapter(cancelledOrderList);
                    setHistoryAdapter();
                }else if(tab.getText().toString().equals(Constants.PENDING_ORDER)){
                    type = Constants.PENDING_ORDER;
                    salesAdapter = new SalesAdapter(pendingItemsList);
                    setHistoryAdapter();
                }else if(tab.getText().toString().equals(Constants.DELIVERED_ORDER)){
                    type = Constants.DELIVERED_ORDER;
                    salesAdapter = new SalesAdapter(deliveredItemsList);
                    setHistoryAdapter();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        makeSaleOrderApiRequest();
    }

    private void makeSaleOrderApiRequest(){
        pd = C.getProgressDialog(this);
        HashMap<String,String> map = new HashMap<>();
        map.put("uid",new AppStore(SalesAlertActivity.this).getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL + ApiName.SALE_ORDER_API,map,this,this);
    }

    private void makeConfirmOrderRequest(String productId){
        pd = C.getProgressDialog(this);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("productId",productId);
        hashMap.put("userId",new AppStore(this).getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL + ApiName.CONFIRM_ORDER_API,hashMap,confirmResponse,this);
    }

    private void setHistoryAdapter(){
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(salesAdapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(SalesAlertActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("saleAlertResponse",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            for(Model data : dataArray){
                if(data.getStatus().equalsIgnoreCase(Constants.PENDING)){
                    pendingItemsList.add(new MyItems(data));
                }else if(data.getStatus().equalsIgnoreCase(Constants.NEW)){
                    newOrderItemsList.add(new MyItems(data));
                }else if(data.getStatus().equalsIgnoreCase(Constants.CANCELED)){
                    cancelledOrderList.add(new MyItems(data));
                }else
                    deliveredItemsList.add(new MyItems(data));
            }
            salesAdapter = new SalesAdapter(newOrderItemsList);
            setHistoryAdapter();
        }
    }

    Response.Listener<JSONObject> confirmResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("confirmAPIResponse",jsonObject.toString());
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                newOrderItemsList.remove(confirmationItemPosition);
                pendingItemsList.add(new MyItems(new Model(model.getData())));
                Toast.makeText(SalesAlertActivity.this,"Your order have been confirmed",Toast.LENGTH_LONG).show();
                tabLayout.getTabAt(2).select();
                type = Constants.PENDING_ORDER;
                salesAdapter = new SalesAdapter(pendingItemsList);
                setHistoryAdapter();
            }
        }
    };
    private class SalesAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<MyItems> source;
        public SalesAdapter(List<MyItems> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_salesalert, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            //final Items items = source.get(position);
            MyItems myItems = source.get(position);
           final Model dataModel =  myItems.itemModel;
            holder.product_name.setText(dataModel.getProductName());
            holder.quant.setText(""+dataModel.getItemQuantity());
            holder.price.setText("$ "+C.FormatterValue(Float.parseFloat(dataModel.getSalePrice())));
            holder.orderBy.setText(dataModel.getUserName());
            Model addressModel = new Model(dataModel.getAddress());
            holder.address.setText(addressModel.getAddress()+","+addressModel.getAddressLine2()+","+addressModel.getCity()+","+addressModel.getState()+","+addressModel.getZipCode()+" "+addressModel.getCountry());
            holder.orderDate.setText(C.parseDateToddMMyyyy(dataModel.getTimeStampSmall()));
            holder.totalPrice.setText("$ "+C.FormatterValue(Float.parseFloat(""+(Double.parseDouble(dataModel.getSalePrice()) + Double.parseDouble(dataModel.getShippingCharges())))));
            try {
                JSONArray imageModel = new JSONArray(dataModel.getString(NamePair.IMAGE));
                PicassoCache.getPicassoInstance(SalesAlertActivity.this).load(C.ASSET_URL+imageModel.get(0)).into(holder.productImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.confirm.setVisibility(type.equals(Constants.NEW_ORDER) ? View.VISIBLE : View.GONE);

            holder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmationItemPosition = position;
                    makeConfirmOrderRequest(""+dataModel.getId());
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

    class MyItems{
        private Model itemModel;
        public MyItems(Model itemModel) {
            this.itemModel = itemModel;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView  product_name, price, quant, orderDate,orderBy,address,totalPrice;
        LinearLayout cancel,track;
        Button confirm;

        public MyViewHolder(View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.product_img);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.price);
            quant = (TextView) itemView.findViewById(R.id.quant);
            orderDate = (TextView) itemView.findViewById(R.id.dating);
            cancel = (LinearLayout) itemView.findViewById(R.id.cancl);
            track = (LinearLayout) itemView.findViewById(R.id.track);
            orderBy = (TextView)itemView.findViewById(R.id.order_by);
            address = (TextView)itemView.findViewById(R.id.addrss);
            totalPrice = (TextView)itemView.findViewById(R.id.producttotalprice);
            confirm = (Button)itemView.findViewById(R.id.confirm);
        }
    }
}
