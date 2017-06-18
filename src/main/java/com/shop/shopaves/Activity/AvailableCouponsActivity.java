package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Fragments.Collection;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AvailableCouponsActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener{
    private RecyclerView recyclerView;
    private CouponAdapter couponAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<MyItems> itemList = new ArrayList<>();
    private ProgressDialog pd;
    private boolean isSelect;
    private ArrayList<String> couponList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_coupons);
        recyclerView = (RecyclerView)findViewById(R.id.coupanrecycleview);
        ((TextView)findViewById(R.id.title)).setText("AVAILABLE COUPONS");

     //   Log.e("PRODUCT_ID",getIntent().getStringExtra(Constants.PRODUCT_ID));
        isSelect = getIntent().getStringExtra(Constants.PRODUCT_ID) != null ? true : false;
        findViewById(R.id.applycoupon).setVisibility(getIntent().getStringExtra(Constants.PRODUCT_ID) != null ? View.VISIBLE : View.GONE);
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.applycoupon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeApplyCouponRequest(getIntent().getStringExtra(Constants.PRODUCT_ID));
            }
        });

     //   makeAvailableCoupanRequest(getIntent().getStringExtra(Constants.PRODUCT_ID) != null ? getIntent().getStringExtra(Constants.PRODUCT_ID) : "");
        makeAvailableCoupanRequest("");
    }

    private void makeApplyCouponRequest(String productId){
       JSONObject map = new JSONObject();
        try {
            map.put("coupons",new JSONArray(couponList));
            map.put("productId",productId);
            map.put("userId",new AppStore(AvailableCouponsActivity.this).getData(Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Net.makeRequest(C.APP_URL+ApiName.SET_COUPONS_API,map.toString(),apply,this);
    }

    private void makeAvailableCoupanRequest(String productId){
        pd = C.getProgressDialog(this);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("productId",productId);
        Net.makeRequest(C.APP_URL+ ApiName.GET_COUPONS,hashMap,this,this);
    }

    private void setCouponAdapter(){
        couponAdapter = new CouponAdapter(itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager = new LinearLayoutManager(AvailableCouponsActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(couponAdapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(AvailableCouponsActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("error",volleyError.toString());
    }


    Response.Listener<JSONObject> apply = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                    Toast.makeText(AvailableCouponsActivity.this,model.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            for(Model data : dataArray){
                itemList.add(new MyItems(""+data.getId(),data.getName(),data.getStartDate(),data.getEndDate(),data.getDescriptionItem(),data.getCode(),false));
            }
            setCouponAdapter();
        }
    }

    private class CouponAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<MyItems> source;
        public CouponAdapter(List<MyItems> source) {
            this.source = source;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_coupon, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final MyItems myItems = source.get(position);
            holder.offDiscount.setText(myItems.name);
            holder.description.setText(myItems.description);
            holder.discountDate.setText(C.getDateFormat(myItems.startDate)+"-"+C.getDateFormat(myItems.endDate));
            holder.selectedCoupons.setVisibility(isSelect ? View.VISIBLE : View.GONE);

            holder.selectedCoupons.setImageResource(myItems.isSelected ? R.drawable.check : R.drawable.unchecked);
            if(myItems.isSelected)
                couponList.add(myItems.id);

            if(!isSelect){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AvailableCouponsActivity.this,CoupanActivity.class).putExtra(Constants.NAME, myItems.name).putExtra(Constants.START_DATE,myItems.startDate).putExtra(Constants.END_DATE,myItems.endDate).putExtra(Constants.COUPON_DESCRIPTION,myItems.description).putExtra(Constants.CODE,myItems.code));
                    }
                });
            }else{
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    myItems.isSelected = !myItems.isSelected;
                        couponList.clear();
                        couponAdapter.notifyDataSetChanged();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView offDiscount,discountDate,description;
        ImageView selectedCoupons;

        public MyViewHolder(View itemView) {
            super(itemView);
            offDiscount = (TextView)itemView.findViewById(R.id.offdiscount);
            discountDate = (TextView)itemView.findViewById(R.id.date);
            description = (TextView)itemView.findViewById(R.id.desc);
            selectedCoupons = (ImageView)itemView.findViewById(R.id.selectedcoupons);
        }
    }

    private class MyItems{
        private String name,startDate,endDate,description,code,id;
        private boolean isSelected;

        public MyItems(String id,String name, String startDate,String endDate, String description, String code,boolean isSelected) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.description = description;
            this.isSelected = isSelected;
            this.code = code;
            this.id = id;
        }
    }

}
