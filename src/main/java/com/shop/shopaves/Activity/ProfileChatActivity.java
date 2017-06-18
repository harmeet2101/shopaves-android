package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileChatActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{
    private MessageAdapter adapter;
    private ArrayList<Item> adapterItemList = new ArrayList<Item>();
    private ListView listView;
    private EditText message;
    private AppStore aps;
    private ProgressDialog pd;
    private String type = Constants.HISTORY;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_chat);
        adapter = new MessageAdapter(adapterItemList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setAdapter(adapter);
        message = (EditText)findViewById(R.id.editcomment);
        TextView userName =(TextView)findViewById(R.id.name);
        aps = new AppStore(this);
        C.applyTypeface(C.getParentView(findViewById(R.id.profilechat)), C.getHelveticaNeueFontTypeface(ProfileChatActivity.this));

        userId = ""+getIntent().getIntExtra(Constants.USER_ID,0);
        if(getIntent().getStringExtra(Constants.NAME)!=null)
            userName.setText(getIntent().getStringExtra(Constants.NAME));

        findViewById(R.id.actionsend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(message.getText().toString().trim())){
                    makeSendMessageRequest();
                }
            }
        });
        findViewById(R.id.addpro_hdr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        makeChatHistoryRequest();
    }

    private void makeSendMessageRequest(){
        type = Constants.SEND_MESSAGE;
        Map<String,String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("myId", aps.getData(Constants.USER_ID));
        map.put("message",message.getText().toString());
        Net.makeRequestParams(C.APP_URL+ ApiName.SEND_MESSAGE_API,map,this,this);
        message.setText("");
    }

    private void makeChatHistoryRequest(){
        type = Constants.HISTORY;
        pd =  C.getProgressDialog(ProfileChatActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("myId", aps.getData(Constants.USER_ID));
        Net.makeRequestParams(C.APP_URL+ ApiName.CHAT_HISTORY_API,map,this,this);    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(ProfileChatActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        Log.e("charResponse",jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            if(type.equals(Constants.SEND_MESSAGE)) {
//                Toast.makeText(ProfileChatActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                makeChatHistoryRequest();
            }
            else{
                adapter.clear();
                Model dataArray[] = model.getDataArray();
                for(int i = dataArray.length-1; i >= 0; i--){
                    Model data = dataArray[i];
                    if((""+data.getSenderId()).equals(aps.getData(Constants.USER_ID))){
                        adapter.add(new Item(""+data.getTimeStampSmall(),data.getMessage(),true));
                    }else{
                        adapter.add(new Item(""+data.getTimeStampSmall(),data.getMessage(),false));
                    }
                }
                adapter.notifyDataSetChanged();
            }
            scrollMyListViewToBottom();
        }
    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }
    private class Item{
        boolean isSend=false;
        String message,time;
        public Item(String time,String message,boolean isSend) {
            this.message = message;
            this.isSend = isSend;
            this.time = time;
        }
    }


    private class MessageAdapter extends BaseAdapter {
        // View Type for Separators
        private static final int SEND = 0;
        // View Type for Regular rows
        private static final int RECEIVE = 1;
        // Types of Views that need to be handled
        // -- Separators and Regular rows --
        private static final int ITEM_VIEW_TYPE_COUNT = 2;
        private ArrayList<Item> list;

        @Override
        public int getViewTypeCount() {
            return ITEM_VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            boolean isSection = getItem(position).isSend;
            if (isSection) {
                return SEND;
            } else {
                return RECEIVE;
            }
        }

        public MessageAdapter(ArrayList<Item> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public void clear(){
            this.list.clear();
            notifyDataSetChanged();
        }

        public void add(Item item){
            this.list.add(item);
            notifyDataSetChanged();
        }

        @Override
        public Item getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Holder holder = null;

            final Item item =  getItem(position);
            int itemViewType = getItemViewType(position);
            if (view == null){
                LayoutInflater inflater = getLayoutInflater();
                switch (itemViewType){
                    case SEND:
                        view = inflater.inflate(R.layout.chat_right, null);
                        holder = new Holder();
                        holder.msg = (TextView) view.findViewById(R.id.message);
                        holder.time = (TextView)view.findViewById(R.id.time);
                        break;
                    case RECEIVE:
                        view = inflater.inflate(R.layout.chat_left, null);
                        holder = new Holder();
                        holder.msg = (TextView) view.findViewById(R.id.message);
                        holder.time = (TextView)view.findViewById(R.id.time);
                        break;
                }

                view.setTag(holder);

            }else {
                holder = (Holder) view.getTag();
            }
            holder.msg.setText(item.message);
            holder.time.setText(DateUtils.getRelativeTimeSpanString(C.getDateInMillis(item.time), Calendar.getInstance().getTimeInMillis()-19800000, DateUtils.SECOND_IN_MILLIS));
            return view;
        }
    }

    class Holder{
        TextView msg,time;
    }
}
