package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllCategoryBrandActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{

    private ProgressDialog pd;
    private RecyclerView recyclerView;
    private ArrayList<Items> list;
    private String type = Constants.CATEGORY;
    private AppStore aps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category_brand);
        recyclerView = (RecyclerView) findViewById(R.id.categories_recyclevw);
        TextView title = (TextView) findViewById(R.id.titlesub);
        aps = new AppStore(this);
        list = new ArrayList<>();
        findViewById(R.id.back_categories).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getStringExtra(Constants.CATEGORY) != null) {
/*
            makeAllCategoryRequest();
*/
            setAllCategoryData();
            title.setText("Category");
        } else {
            type = Constants.BRAND;
            title.setText("Brands");
            makeAllBrandRequest();
        }
    }

    private void updateAdapter(){
        BrandAdapter adapter = new BrandAdapter(list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    private void makeAllBrandRequest(){
        pd =  C.getProgressDialog(AllCategoryBrandActivity.this);
        Map<String,String> map = new HashMap<>();
        Net.makeRequest(C.APP_URL+ ApiName.ALL_BRAND_API,map,this,this);
    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(AllCategoryBrandActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
            Log.e("ResponseCategoryBrand", jsonObject.toString());
            if(type.equals(Constants.CATEGORY)){
                aps.setData(Constants.SHOP_CATEGORY_DATA,jsonObject.toString());
            }
            Model dataArray[] = model.getDataArray();
            for (Model data : dataArray) {
                if(type.equals(Constants.BRAND))
                list.add(new Items("", "", data.getName()));
                else
                    list.add(new Items("" + data.getId(), data.getImage(), data.getName()));
            }
            updateAdapter();
        }

    }

    private void setAllCategoryData(){
        if(!TextUtils.isEmpty(aps.getData(Constants.SHOP_CATEGORY_DATA))) {
            Model model = new Model(aps.getData(Constants.SHOP_CATEGORY_DATA));
            Model dataArray[] = model.getDataArray();
            for (Model data : dataArray) {
                list.add(new Items("" + data.getId(), data.getImage(), data.getName()));
            }
            updateAdapter();
        }
    }

    private class BrandAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;

        public BrandAdapter(List<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_category_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final Items items = source.get(position);
            holder.cateName.setText(items.categoryName);

            if(type.equals(Constants.CATEGORY)){
                Picasso.with(AllCategoryBrandActivity.this).load(C.ASSET_URL+items.categoryImage).into(holder.cateImage);


                holder.categoryClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                            startActivity(new Intent(AllCategoryBrandActivity.this, ProductByCategoryActivity.class).putExtra("catId", items.catId).putExtra("position", position));
                        }else
                            startActivity(new Intent(AllCategoryBrandActivity.this,LoginActivity.class));
                    }
                });
            }

        }
        @Override
        public int getItemCount() {
            return source.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView cateImage;
        private TextView cateName;
        private LinearLayout categoryClick;

        public MyViewHolder(View itemView) {
            super(itemView);
            cateImage = (ImageView) itemView.findViewById(R.id.cat_img);
            cateName = (TextView) itemView.findViewById(R.id.cat_name);
            categoryClick = (LinearLayout)itemView.findViewById(R.id.category);
        }
    }

    private class Items {
        public String categoryImage;
        public String categoryName;
        public String catId;

        public Items(String categoryImage, String categoryName) {
            this.categoryImage = categoryImage;
            this.categoryName = categoryName;
        }

        public Items(String catId,String categoryImage, String categoryName) {
            this.categoryImage = categoryImage;
            this.categoryName = categoryName;
            this.catId = catId;
        }
    }
}
