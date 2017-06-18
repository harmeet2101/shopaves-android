package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCategoryActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{

    private int parentId;
    private RecyclerView categoryView;
    private ArrayList<Items> list;
    private ProgressDialog pd;
    private String lastChildSubCategory;
    private String type = "";
    private TextView selectItem;
    private Intent sendIntent;
    private int selectedPosition = -1;
    private String OTHER_TYPE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        categoryView = (RecyclerView)findViewById(R.id.categoryrecycleview);
        TextView title = (TextView)findViewById(R.id.title);
        selectItem = (TextView)findViewById(R.id.toright);
        selectItem.setText("SELECT");
        selectItem.setVisibility(View.GONE);
        if(getIntent().getStringExtra(Constants.NAME)!=null)
            title.setText(getIntent().getStringExtra(Constants.NAME));
        parentId = getIntent().getIntExtra(Constants.PARENT_ID,0);
        if(getIntent().getStringExtra(Constants.TYPE) != null)
        type = getIntent().getStringExtra(Constants.TYPE);
        list = new ArrayList<>();
        if(type.equals(Constants.CREATE_PRODUCT))
            selectItem.setVisibility(View.VISIBLE);
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "";
                finish();
            }
        });

        selectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    setResult(200, sendIntent);
                    finish();
            }
        });
        makeGetCategoryRequest();
    }

    private void makeGetCategoryRequest(){
        pd = C.getProgressDialog(this);
        Map<String,String> map = new HashMap<>();
        map.put("parentId",""+parentId);
        Net.makeRequest(C.APP_URL+ ApiName.GET_CATEGORY_API,map,this,this);
    }

    private  void updateAdapter(){
        OutWearAdapter  adapter = new OutWearAdapter(list, R.layout.custom_category_selection);
        categoryView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(SubCategoryActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryView.setLayoutManager(layoutManager);
        categoryView.setAdapter(adapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(SubCategoryActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("getCategoryResponse",jsonObject.toString());
        lastChildSubCategory = jsonObject.toString();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            for(Model data : dataArray){
                list.add(new Items(data.hasSubCat(),data.getName(),data.getId()));
            }
            updateAdapter();
        }
    }

    private class OutWearAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;
        private int resourse;

        public OutWearAdapter(List<Items> itemsList,int resourselayout) {
            this.source = itemsList;
            this.resourse=resourselayout;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(resourse, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final Items items = source.get(position);
            holder.product_name.setText(items.categoryName);

            if(items.hasSubCategory || type.equals(Constants.EDIT_COLLECTION)){
                holder.dropRight.setVisibility(View.VISIBLE);
                holder.dropRightChecked.setVisibility(View.GONE);
            }else if(type.equals(Constants.PRODUCT_CATEGORY)){
                holder.dropRight.setVisibility(View.GONE);
            }
            else{
                holder.dropRight.setVisibility(View.GONE);
                holder.dropRightChecked.setVisibility(View.VISIBLE);

                if(type.equals(Constants.CREATE_PRODUCT)){
                    if(!items.hasSubCategory) {
                        //holder.dropRight.setImageResource(R.drawable.unchecked);
                    }
                }
                if(selectedPosition == position){
                    holder.dropRightChecked.setImageResource(R.drawable.check);
                    selectedPosition = -1;
                }else
                    holder.dropRightChecked.setImageResource(R.drawable.unchecked);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //finish();
                    selectedPosition = position;
                    sendIntent = new Intent();
                    if(items.hasSubCategory)
                        startActivityForResult(new Intent(SubCategoryActivity.this,SubCategoryActivity.class).putExtra(Constants.PARENT_ID,items.id).putExtra(Constants.NAME,items.categoryName.toUpperCase()).putExtra(Constants.TYPE,type),200);
                    else if(!type.equals(Constants.CREATE_PRODUCT))
                        startActivityForResult(new Intent(SubCategoryActivity.this,ProductByCategoryActivity.class).putExtra(Constants.SUB_CATEGORY_ID,items.id).putExtra(Constants.POSITION,position).putExtra(Constants.SUBCATEGORIES,lastChildSubCategory).putExtra(Constants.TYPE,type.equals(Constants.PRODUCT_CATEGORY) ? "" : type),200);
                    else {
                        sendIntent.putExtra("CategoryName",items.categoryName);
                        sendIntent.putExtra("CategoryId",""+items.id);
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView dropRight,dropRightChecked;
        private TextView product_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            dropRight  = (ImageView) itemView.findViewById(R.id.drright);
            product_name = (TextView) itemView.findViewById(R.id.itemname);
            dropRightChecked = (ImageView)itemView.findViewById(R.id.ic);
            //dropRight.setVisibility(View.VISIBLE);
        }
    }

    private class Items {
        public boolean hasSubCategory;
        public String categoryName;
        public int id;

        public Items(boolean hasSubCategory, String categoryName, int id) {
            this.hasSubCategory = hasSubCategory;
            this.categoryName = categoryName;
            this.id = id;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 200){
            if(type.equals(Constants.CREATE_PRODUCT) || type.equals(Constants.EDIT_COLLECTION)) {
                setResult(200, data);
                finish();
            }
        }
    }
}
