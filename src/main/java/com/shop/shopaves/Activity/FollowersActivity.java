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

public class FollowersActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{
    private RecyclerView recyclerView;
    private ArrayList<RowItems> list;
    private ProgressDialog pd;
    private AppStore aps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        recyclerView = (RecyclerView) findViewById(R.id.followers_recyclevw);
         C.applyTypeface(C.getParentView(findViewById(R.id.activity_followers)), C.getHelveticaNeueFontTypeface(FollowersActivity.this));
        aps = new AppStore(this);
        list = new ArrayList<>();



        findViewById(R.id.back_followers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        makegetFollowersRequest();
    }

    private void setFollowerAdapter(){
        FollowerAdapter adapter = new FollowerAdapter(list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(FollowersActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void makegetFollowersRequest(){
        pd =  C.getProgressDialog(FollowersActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GET_FOLLOWERS_API,map,this,this);
    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(FollowersActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("FolloresResponse",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            for(Model data : dataArray){
                list.add(new RowItems(data.getId(),data.getImageUrl(),data.getName(),""+data.getItems(),""+data.getCollections()));
            }
            setFollowerAdapter();
        }
    }

    private class FollowerAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<RowItems> source;

        public FollowerAdapter(List<RowItems> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_followers_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final RowItems items = source.get(position);
            holder.userName.setText(items.username);
            holder.items.setText(items.itemsCount);
            holder.colletion.setText(items.colletionsCount);
            PicassoCache.getPicassoInstance(FollowersActivity.this).load(C.getImageUrl(items.profile)).placeholder(R.drawable.male).into(holder.imageView);
            holder.userChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FollowersActivity.this,ProfileChatActivity.class).putExtra(Constants.USER_ID,items.id).putExtra(Constants.NAME,items.username));
                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
    }



    private class RowItems{
        private String profile;
        private String username;
        private String itemsCount;
        private String colletionsCount;
        private int id;

        public RowItems(int userId,String profile, String username, String itemsCount, String colletionsCount) {
            this.profile = profile;
            this.username = username;
            this.itemsCount = itemsCount;
            this.colletionsCount = colletionsCount;
            this.id = id;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView userName,items,colletion;
        ImageView userChat;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.usr);
            userName = (TextView) itemView.findViewById(R.id.fl_username);
            items = (TextView) itemView.findViewById(R.id.fl_itemcount);
            colletion = (TextView) itemView.findViewById(R.id.fl_coletioncount);
            userChat = (ImageView) itemView.findViewById(R.id.right_img);
            userChat.setImageResource(R.drawable.chat);
        }
    }


}
