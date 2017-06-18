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
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{
    private RecyclerView recyclerView;
    private ArrayList<RowItems> peopleListItems;
    private ArrayList<RowItems> brandListItems;
    private View pple_view,brand_view;
    private LinearLayout people_click,brand_click;
    private ProgressDialog pd;
    private AppStore aps;
    private FollowingAdapter adapter;
    private String type = Constants.FOLLOW_PEOPLES;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        recyclerView = (RecyclerView) findViewById(R.id.following_recyclevw);
        pple_view = findViewById(R.id.peple_view);
        brand_view = findViewById(R.id.brand_view);
        people_click = (LinearLayout) findViewById(R.id.people_click);
        brand_click = (LinearLayout) findViewById(R.id.brand_click);
        aps = new AppStore(this);
        C.applyTypeface(C.getParentView(findViewById(R.id.activity_following)), C.getHelveticaNeueFontTypeface(FollowingActivity.this));

        peopleListItems = new ArrayList<>();
        brandListItems = new ArrayList<>();
        pple_view.setVisibility(View.VISIBLE);
        brand_view.setVisibility(View.INVISIBLE);
        people_click.setAlpha(1);
        brand_click.setAlpha((float) .5);

        people_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = Constants.FOLLOW_PEOPLES;
                setAdapter(peopleListItems, R.layout.single_followers_row);
                pple_view.setVisibility(View.VISIBLE);
                brand_view.setVisibility(View.INVISIBLE);
                people_click.setAlpha(1);
                brand_click.setAlpha((float) .5);
            }
        });

        brand_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = Constants.FOLLOW_BRANDS;
                setAdapter(brandListItems, R.layout.single_brand_layout);
                pple_view.setVisibility(View.INVISIBLE);
                brand_view.setVisibility(View.VISIBLE);
                brand_click.setAlpha(1);
                people_click.setAlpha((float) .5);
            }
        });

        findViewById(R.id.back_following).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        makegetFollowingRequest();
    }

    private void setAdapter(List<RowItems> listitem, int resource){
         adapter = new FollowingAdapter(listitem,resource);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(FollowingActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void makegetFollowingRequest(){
        type = Constants.FOLLOW_PEOPLES;
        pd =  C.getProgressDialog(FollowingActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GET_FOLLOWING_API,map,this,this);
    }
    private void makeBrandFollowingRequest(){
        type = Constants.FOLLOW_BRANDS;
        pd =  C.getProgressDialog(FollowingActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GET_FOLLOWING_BRAND_API,map,this,this);
    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(FollowingActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("FollowingResponse",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            if(type.equals(Constants.FOLLOW_PEOPLES)) {
                for (Model data : dataArray) {
                    peopleListItems.add(new RowItems(data.getImageUrl(),"", data.getName(),"",0, "" + data.getItems(), "" + data.getCollections(), R.drawable.admin,data.getId()));
                }
                makeBrandFollowingRequest();
            }else{
                type = Constants.FOLLOW_PEOPLES;
                for (Model data : dataArray) {
                    brandListItems.add(new RowItems(data.getIcon(),data.getBannerImage(), data.getName(),""+data.getId(),data.getItemStatus(), "" + data.getItems(), ""+data.getFollowers(), R.drawable.admin,data.getId()));
                    //setAdapter(peopleListItems, R.layout.single_brand_layout);
                }
                setAdapter(peopleListItems, R.layout.single_followers_row);
            }
        }
    }

    private class FollowingAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<RowItems> source;
        private int resource;

        public FollowingAdapter(List<RowItems> source,int resource) {
            this.source = source;
            this.resource=resource;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final RowItems items = source.get(position);
            if(type.equals(Constants.FOLLOW_BRANDS)){
                holder.brandName.setText(items.username);
                if(items.followStatus == 2){
                    holder.followIcon.setImageResource(R.drawable.plusblack);
                    holder.followBrand.setBackgroundResource(R.drawable.green_rect);
                }else{
                    holder.followIcon.setImageResource(R.drawable.unfollowicon);
                    holder.followBrand.setBackgroundResource(R.drawable.publish_background_drawable);
                }
                //holder.followIcon.setImageResource(items.followStatus == 2 ? R.drawable.unfollowicon : R.drawable.plusblack);
                holder.followersCount.setText(items.colletionsCount);
                holder.itemsCount.setText(items.itemsCount);
                PicassoCache.getPicassoInstance(FollowingActivity.this).load(C.ASSET_URL + items.profileImg).placeholder(R.drawable.defaultholder).into(holder.brandLogo);
                PicassoCache.getPicassoInstance(FollowingActivity.this).load(C.ASSET_URL + items.bannerImg).placeholder(R.drawable.defaultbannerbg).into(holder.brandBanner);

                holder.followIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("follow icon tag click",""+holder.followIcon.getTag());
                         pd =  C.getProgressDialog(FollowingActivity.this);
                            Map<String,String> map = new HashMap<>();
                            map.put("brandId", items.brandId);
                            map.put("userId",aps.getData(Constants.USER_ID));
                            Net.makeRequestParams(C.APP_URL + ApiName.FOLLOW_BRAND_API, map, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    pd.dismiss();
                                    Model model = new Model(jsonObject.toString());
                                    if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                                        brandListItems.get(position).followStatus = brandListItems.get(position).followStatus == 1 ? 2 : 1;
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(FollowingActivity.this,model.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    pd.dismiss();
                                    Toast.makeText(FollowingActivity.this,VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(FollowingActivity.this,BrandDetailActivity.class).putExtra(Constants.BRAND_NAME,items.username).putExtra(Constants.BRAND_ID,items.brandId));
                    }
                });
            }else{
                PicassoCache.getPicassoInstance(FollowingActivity.this).load(C.ASSET_URL + items.profileImg).placeholder(R.drawable.male).into(holder.imageView);
                holder.userName.setText(items.username);
                holder.items.setText(items.itemsCount);
                holder.colletion.setText(items.colletionsCount);
                holder.changeTo.setText("Collection. ");
                holder.image_check.setTag("0");
                holder.image_check.setImageResource(R.drawable.crossimg);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(FollowingActivity.this,ProfileActivity.class).putExtra(Constants.USER_ID,""+items.userId));
                    }
                });
                holder.image_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pd =  C.getProgressDialog(FollowingActivity.this);
                        Map<String,String> map = new HashMap<>();
                        map.put("myId", aps.getData(Constants.USER_ID));
                        map.put("userId", ""+items.userId);

                        Net.makeRequestParams(C.APP_URL + ApiName.FOLLOW_API, map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                pd.dismiss();
                                Model model = new Model(jsonObject.toString());
                                if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                                    holder.image_check.setImageResource(holder.image_check.getTag().equals("0") ? R.drawable.admin : R.drawable.crossimg);
                                    holder.image_check.setTag(holder.image_check.getTag().equals("1") ? "0" : "1");
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                pd.dismiss();
                                pd.dismiss();
                                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                                    volleyError = error;
                                    Log.e("error",volleyError.toString());
                                }
                            }
                        });
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
    }

    private class RowItems {
        private String profileImg;
        private String username;
        private String itemsCount;
        private String colletionsCount,bannerImg,brandId;
        private int chek;
        private int userId;
        private int followStatus;

        public RowItems(String profileImg,String bannerImg, String username,String brandId,int followStatus, String itemsCount, String colletionsCount, int chek,int userId) {
            this.profileImg = profileImg;
            this.username = username;
            this.itemsCount = itemsCount;
            this.colletionsCount = colletionsCount;
            this.chek = chek;
            this.bannerImg = bannerImg;
            this.userId = userId;
            this.followStatus = followStatus;
            this.brandId = brandId;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView,brandLogo;
        TextView userName,items,colletion,changeTo,brandName,followersCount,itemsCount;
        RelativeLayout followBrand;
        ImageView image_check,brandsBanner,followIcon,brandBanner;
        public MyViewHolder(View itemView) {
            super(itemView);
            if(type.equals(Constants.FOLLOW_PEOPLES)){
                imageView = (CircleImageView) itemView.findViewById(R.id.usr);
                userName = (TextView) itemView.findViewById(R.id.fl_username);
                items = (TextView) itemView.findViewById(R.id.fl_itemcount);
                colletion = (TextView) itemView.findViewById(R.id.fl_coletioncount);
                changeTo = (TextView) itemView.findViewById(R.id.changeto);
                image_check = (ImageView) itemView.findViewById(R.id.right_img);
                if(type.equals(Constants.FOLLOW_BRANDS)){
                    brandsBanner = (ImageView)itemView.findViewById(R.id.brandbanner);
                }
            }else{
                brandName = (TextView) itemView.findViewById(R.id.brand_name);
                followBrand = (RelativeLayout)itemView.findViewById(R.id.followbrand);
                followIcon = (ImageView)itemView.findViewById(R.id.followicon);
                followersCount = (TextView)itemView.findViewById(R.id.followerscount);
                itemsCount = (TextView)itemView.findViewById(R.id.itemscount);
                brandLogo = (CircleImageView)itemView.findViewById(R.id.brand_logo);
                brandBanner = (ImageView)itemView.findViewById(R.id.brandbanner);
            }

        }
    }
}
