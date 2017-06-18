package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MySetsActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{
    private RecyclerView recyclerView;
    private ArrayList<JSONITEM> lists = new ArrayList<>();
    private ProgressDialog pd;
    private AppStore aps;
    private int type = 0;
    private int SELECTED_POSITION = -1;
    private CollectionSetAdapter  adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sets);
        TextView title = (TextView)findViewById(R.id.title);
        C.applyTypeface(C.getParentView(findViewById(R.id.activity_sets)), C.getHelveticaNeueFontTypeface(MySetsActivity.this));
        recyclerView = (RecyclerView) findViewById(R.id.mysetslist);
        title.setText("MY SETS");
        aps = new AppStore(this);

        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        makeGroupListRequest();
    }

    private void makeGroupListRequest(){
        type = 0;
        pd =  C.getProgressDialog(MySetsActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        map.put("myGroups", "true");
        Net.makeRequest(C.APP_URL+ ApiName.GET_GROUPS_API,map,this,this);
    }

    private void makeDeleteGroupRequest(String gid){
        type = 1;
        pd =  C.getProgressDialog(MySetsActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        map.put("gid", gid);
        Net.makeRequestParams(C.APP_URL+ ApiName.DELETE_GROUP_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(MySetsActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("Group response",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
            if (type == 0) {
                Model dataArray[] = model.getDataArray();
                for (Model data : dataArray) {
                    lists.add(new JSONITEM(data));
                    //  list.add(new MySetsItem("" + data.getId(), data.getName(), data.getProductDescription(), data.getImage()));
                }
                findViewById(R.id.noproduct).setVisibility(lists.size() > 0 ? View.GONE : View.VISIBLE);
                setCollectionSetAdapter();
            }else{

                lists.remove(SELECTED_POSITION);
                adapter.notifyDataSetChanged();
               // setCollectionSetAdapter();
                //setGroupAdapter();
                Toast.makeText(MySetsActivity.this,""+model.getData(),Toast.LENGTH_SHORT).show();
            }
        }else{
            if(type == 1){
                Toast.makeText(MySetsActivity.this,""+model.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void setCollectionSetAdapter(){
          adapter = new CollectionSetAdapter(lists);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MySetsActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private class CollectionSetAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<JSONITEM> source;
        public CollectionSetAdapter(List<JSONITEM> source) {
            this.source = source;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_collection_sets, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final JSONITEM items = source.get(position);
            final Model model = items.model;

            holder.collectionName.setText(model.getName());
            holder.userName.setText(model.getUserName());
            holder.textView3.setText("1 hr ago");
            holder.commentCount.setText(""+model.getCommentCount());
            holder.addTo.setImageResource(R.drawable.delete);




            JSONArray collectionImageArray = model.getJsonImageArray();
            if(collectionImageArray != null){
                for(int i = 0; i<collectionImageArray.length(); i++){
                    try {
                        PicassoCache.getPicassoInstance(MySetsActivity.this).load(C.ASSET_URL+collectionImageArray.get(i).toString()).into(i == 0 ? holder.setOneImage : i == 1 ? holder.setTwoImage : i == 2 ? holder.setThreeImage : holder.setFourImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            PicassoCache.getPicassoInstance(MySetsActivity.this).load(C.ASSET_URL+model.getUserProfileImage()).placeholder(R.drawable.female).into(holder.userImg);
            if(model.getLikesStatus() == 1) {
                holder.likeIcon.setImageResource(R.drawable.like);
                holder.likeCount.setText("" + model.getLikes());
                holder.likeIcon.setTag("1");
            }
            else {
                holder.likeIcon.setImageResource(R.drawable.unlike);
                holder.likeCount.setText("" + model.getLikes());
                holder.likeIcon.setTag("2");
            }

            holder.setGroupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivityForResult(new Intent(MySetsActivity.this, GroupDetailsActivity.class).putExtra(Constants.GROUP_ID,""+model.getId()),100);
                }
            });
            holder.addcomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                        //    startActivity(new Intent(getActivity(), CommentsActivity.class).putExtra(Constants.GROUP_ID, "" + model.getId()));
                        startActivityForResult(new Intent(MySetsActivity.this,CommentsActivity.class).putExtra(Constants.GROUP_ID,"" + model.getId()),200);

                    }else
                        C.setLoginMessage(MySetsActivity.this);
                }
            });

            holder.likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pd =  C.getProgressDialog(MySetsActivity.this);
                    Map<String,String> map = new HashMap<>();
                    map.put("userId", aps.getData(Constants.USER_ID));
                    map.put("gid",  "" + model.getId());
                    map.put("status", holder.likeIcon.getTag().equals("1") ? "2" : "1");
                    Net.makeRequestParams(C.APP_URL+ ApiName.LIKE_GROUP_API,map,new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            pd.dismiss();
                            Model model = new Model(jsonObject);
                            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                                Log.e("LikeGroupStatus", jsonObject.toString());
                                if (model.getLikeStatus() == 1){
                                    holder.likeIcon.setImageResource(R.drawable.like);
                                    holder.likeIcon.setTag("1");
                                }else{
                                    holder.likeIcon.setImageResource(R.drawable.unlike);
                                    holder.likeIcon.setTag("2");
                                }
                                holder.likeCount.setText(""+model.getLikes());
                            }
                        }
                    },new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            pd.dismiss();
                        }
                    });
                }

            });

            holder.addTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SELECTED_POSITION = position;
                    makeDeleteGroupRequest(""+model.getId());
                    // startActivity(new Intent(MySetsActivity.this, AddToGroupActivity.class).putExtra(Constants.ID,""+model.getId()).putExtra(Constants.IS_PRODUCT,false));
                }
            });

            holder.userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(""+model.getUserId()).equals(aps.getData(Constants.USER_ID)))
                        startActivity(new Intent(MySetsActivity.this, ProfileActivity.class).putExtra(Constants.USER_ID,""+model.getUserId()));
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
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView setOneImage,setTwoImage,setThreeImage,setFourImage,addTo;
        TextView collectionName,userName,textView3,likeCount,commentCount;
        LinearLayout addcomment,likes,setGroupView;
        ImageView likeIcon;
        CircleImageView userImg;
        RelativeLayout userProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            setOneImage = (ImageView) itemView.findViewById(R.id.setoneimage);
            setTwoImage = (ImageView) itemView.findViewById(R.id.settwoimage);
            setThreeImage = (ImageView) itemView.findViewById(R.id.setthreeimage);
            setFourImage = (ImageView) itemView.findViewById(R.id.setfourimage);
            addTo = (ImageView) itemView.findViewById(R.id.addto);
            userImg = (CircleImageView) itemView.findViewById(R.id.usr);
            collectionName = (TextView) itemView.findViewById(R.id.usr_tag);
            userName = (TextView) itemView.findViewById(R.id.user_nme);
            textView3 = (TextView) itemView.findViewById(R.id.tag_tm);
            likeCount = (TextView) itemView.findViewById(R.id.likes);
            commentCount = (TextView) itemView.findViewById(R.id.commnts);
            addcomment = (LinearLayout)itemView.findViewById(R.id.comment);
            likeIcon = (ImageView)itemView.findViewById(R.id.likeicon);
            likes = (LinearLayout)itemView.findViewById(R.id.like);
            setGroupView = (LinearLayout)itemView.findViewById(R.id.setview);
            userProfile = (RelativeLayout)itemView.findViewById(R.id.usrprofile);

            itemView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap productBitmap = ((BitmapDrawable)setOneImage.getDrawable()).getBitmap();
                    if(productBitmap != null){
                        C.shareContentExp(MySetsActivity.this, collectionName.getText().toString(), C.getImageUri(MySetsActivity.this, productBitmap));
                    }

                }
            });
        }
    }

    private class JSONITEM{
        private Model model;
        public JSONITEM(Model model) {
            this.model = model;
        }
    }
}
