package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{
    private RecyclerView recyclerView;
    private ArrayList<MessageItems> list;
    private AppStore aps;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        C.applyTypeface(C.getParentView(findViewById(R.id.activity_message)), C.getHelveticaNeueFontTypeface(MessageActivity.this));
        recyclerView = (RecyclerView) findViewById(R.id.message_recyclevw);
        list = new ArrayList<>();
        aps = new AppStore(this);
        findViewById(R.id.back_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        makeConversationListRequest();
    }

    private void updateMessageAdapter(){
        MessageAdapter adapter = new MessageAdapter(list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void makeConversationListRequest(){
        pd =  C.getProgressDialog(MessageActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        Net.makeRequestParams(C.APP_URL+ ApiName.CONVERSATION_LIST_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(MessageActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        Log.e("Conversation api",jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            for(Model data : dataArray){
                list.add(new MessageItems(data.getId(),data.getImage(),data.getName(),data.getTimeDATE(),data.getMessage()));
            }
            updateMessageAdapter();
        }
    }

    private class MessageAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<MessageItems> source;

        public MessageAdapter(List<MessageItems> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final MessageItems items = source.get(position);
            holder.name.setText(items.username);
            holder.time.setText(DateUtils.getRelativeTimeSpanString(C.getDateInMillis(items.time), Calendar.getInstance().getTimeInMillis()-19800000, DateUtils.SECOND_IN_MILLIS));
            holder.message.setText(items.message);
            PicassoCache.getPicassoInstance(MessageActivity.this).load(C.getImageUrl(items.profile)).placeholder(R.drawable.female).into(holder.imageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MessageActivity.this,ProfileChatActivity.class).putExtra(Constants.USER_ID,items.userId).putExtra(Constants.NAME,items.username));
                }
            });
        }
        @Override
        public int getItemCount() {
            return source.size();
        }
    }


    private class MessageItems {
        private String profile;
        private String username;
        private String time;
        private String message;
        private int userId;

        public MessageItems(int userId,String profile, String username, String time, String message) {
            this.profile = profile;
            this.username = username;
            this.time = time;
            this.message = message;
            this.userId = userId;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView name,time,message;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.usr_pic);
            name = (TextView) itemView.findViewById(R.id.user_name);
            time = (TextView) itemView.findViewById(R.id.time);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
