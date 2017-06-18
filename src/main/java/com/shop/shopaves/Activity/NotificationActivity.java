package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.LinkedHashSet;

public class NotificationActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener{
    private RecyclerView notiview;
    private NotificationAdapter adapter;
    private ProgressDialog pd;
    private ArrayList<MyItems> listItems = new ArrayList<>();
    private LinkedHashSet<String> timeList = new LinkedHashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.notification_c));
        notiview = (RecyclerView) findViewById(R.id.notification_recyclevw);
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        makeGetNotificationRequest();
    }
    private void makeGetNotificationRequest(){
        pd = C.getProgressDialog(NotificationActivity.this);
        HashMap<String,String> hashMap  = new HashMap<String, String>();
        hashMap.put("userId",new AppStore(NotificationActivity.this).getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GET_NOTIFICATION_API,hashMap,this,this);
    }

    private void setNotificationAdapter(){
        adapter = new NotificationAdapter(listItems);
        notiview.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notiview.setLayoutManager(layoutManager);
        notiview.setAdapter(adapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("error",volleyError.toString());
        Toast.makeText(getApplicationContext(),""+ VolleyErrors.setError(volleyError),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            for(Model data : dataArray){
                //listItems.add(data);
                timeList.add(C.getDayTomorrowDateFormat(C.getDateInMillis(data.getTimeStampSmall())));
            }
            for(String t : timeList){
                    boolean isShow = true;
                for(Model data : dataArray){
                 if(t.equals(C.getDayTomorrowDateFormat(C.getDateInMillis(data.getTimeStampSmall())))){
                     listItems.add(new MyItems(data,isShow));
                     isShow = false;
                 }
                  //  timeList.add(C.getDayTomorrowDateFormat(C.getDateInMillis(model.getTimeStampSmall())));
                }
            }
            setNotificationAdapter();
            pd.dismiss();
        }
    }

    private class NotificationAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private ArrayList<MyItems> source;

        public NotificationAdapter(ArrayList<MyItems> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
         MyItems myItems = source.get(position);
            Model model = myItems.model;

            String name1 = "<font color='#252D51'>"+model.getNameOne()+"</font>";
            String name2 = "<font color='#252D51'>"+model.getNameTwo()+"</font>";
            Log.e("name1",name1);
            Log.e("name2",name2);
            holder.message.setText(Html.fromHtml(name1+" "+model.getText()+" "+name2));
            holder.timeStamp.setText(C.getDateFormatInMonth(model.getTimeStampSmall()));
            holder.timesView.setVisibility(myItems.isShow ? View.VISIBLE : View.GONE);
            holder.dayByDay.setText(C.getDayTomorrowDateFormat(C.getDateInMillis(model.getTimeStampSmall())));
            PicassoCache.getPicassoInstance(NotificationActivity.this).load(C.ASSET_URL+model.getImage()).placeholder(R.drawable.male).error(R.drawable.male).into(holder.userImg);
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message,contactName,timeStamp,dayByDay;
        ImageView userImg;
        RelativeLayout timesView;
        public MyViewHolder(View itemView) {
            super(itemView);
            message = (TextView)itemView.findViewById(R.id.message);
            contactName = (TextView)itemView.findViewById(R.id.contact_name);
            userImg = (ImageView) itemView.findViewById(R.id.profile_pic);
            timeStamp = (TextView)itemView.findViewById(R.id.timestamp);
            timesView = (RelativeLayout)itemView.findViewById(R.id.top);
            dayByDay = (TextView)itemView.findViewById(R.id.today);
        }
    }

    private class MyItems{
        private Model model;
        boolean isShow;

        public MyItems(Model model, boolean isShow) {
            this.model = model;
            this.isShow = isShow;
        }
    }
}
