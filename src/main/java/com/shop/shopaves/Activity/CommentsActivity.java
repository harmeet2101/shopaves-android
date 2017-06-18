package com.shop.shopaves.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity implements  Response.ErrorListener,Response.Listener<JSONObject>{

    private RecyclerView commentsList;
    private ArrayList<Items> lists = new ArrayList<>();
    private AppStore aps;
    private int  collectionId;
    private String productId;
    private String groupId;
    private EditText editComment;
    private ProgressDialog pd;
    private String type;
    private boolean isChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        commentsList = (RecyclerView)findViewById(R.id.commentslist);
        editComment = (EditText)findViewById(R.id.editcomment);
        C.applyTypeface(C.getParentView(findViewById(R.id.commentlayout)), C.getHelveticaNeueFontTypeface(CommentsActivity.this));

        Intent intent = getIntent();

        if(intent != null){
            if(intent.getIntExtra(Constants.COLLECTION_ID,0) != 0) {
                collectionId = intent.getIntExtra(Constants.COLLECTION_ID,0);
                type = Constants.COLLECTION;
            }
            else if(intent.getStringExtra(Constants.PRODUCT_ID) != null){
                productId = intent.getStringExtra(Constants.PRODUCT_ID);
                type = Constants.PRODUCT;
            }else{
                groupId = intent.getStringExtra(Constants.GROUP_ID);
                type = Constants.GROUP;
            }
        }

        aps = new AppStore(this);

        findViewById(R.id.actionsend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)) && !TextUtils.isEmpty(editComment.getText().toString()))
                    makeAddComentRequest();
                else
                    C.setLoginMessage(CommentsActivity.this);
            }
        });

        findViewById(R.id.back_addrs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChange)
                    setResult(200);
                finish();
            }
        });
        addCommentList();
        setupParent(findViewById(R.id.back_addrs));
    }

    protected void setupParent(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }
        //If a layout container, iterate over children
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupParent(innerView);
            }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    private void addCommentList(){
        pd =  C.getProgressDialog(CommentsActivity.this);
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(String.valueOf(collectionId))) {
            map.put("skip", ""+lists.size());
            map.put("collectionId", String.valueOf(collectionId));
            Net.makeRequest(C.APP_URL+ ApiName.GET_COMMENTS_API,map,comment_response,this);
        }
        else if(!TextUtils.isEmpty(productId)){
            map.put("skip",""+lists.size());
            map.put("productid", productId);
            Net.makeRequest(C.APP_URL+ ApiName.GET_PRODUCT_COMMENTS,map,comment_response,this);
        }else{
            map.put("gid", groupId);
            Net.makeRequest(C.APP_URL+ ApiName.GET_GROUP_COMMENTS_API,map,comment_response,this);
        }
    }

    private class CommentsAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;

        public CommentsAdapter(List<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_comments_list, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Items items = source.get(position);
            holder.textView.setText(items.name);
            holder.textView2.setText(items.message);
            holder.textView3.setText(items.time);
            PicassoCache.getPicassoInstance(CommentsActivity.this).load(C.ASSET_URL+items.userImage).into(holder.userImg);
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
    }

    private class Items{
        public String userImage;
        public String name;
        public String message;
        public String time;


        public Items(String userImage,String name, String message, String time) {
            this.userImage = userImage;
            this.name = name;
            this.message = message;
            this.time = time;

        }
    }
    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        TextView textView,textView2,textView3;
        public MyViewHolder(View itemView) {
            super(itemView);
            userImg = (ImageView) itemView.findViewById(R.id.usr);
            textView = (TextView) itemView.findViewById(R.id.username);
            textView2 = (TextView) itemView.findViewById(R.id.message);
            textView3 = (TextView) itemView.findViewById(R.id.time);
        }
    }

    private void makeAddComentRequest(){
        pd =  C.getProgressDialog(CommentsActivity.this);
        if(!TextUtils.isEmpty(editComment.getText().toString())){
            Map<String,String> map = new HashMap<>();
            //  map.put("time","500");
            if(!TextUtils.isEmpty(String.valueOf(collectionId))) {
                map.put("userid",aps.getData(Constants.USER_ID));
                map.put("collectionid", "" + collectionId);
                map.put("time", ""+Calendar.getInstance().getTimeInMillis());
                map.put("comment", editComment.getText().toString());
                Net.makeRequestParams(C.APP_URL+ ApiName.ADD_COLLECTION_COMMENTS_API,map,CommentsActivity.this,CommentsActivity.this);
            }else if(!TextUtils.isEmpty(productId)){
                map.put("userId",aps.getData(Constants.USER_ID));
                map.put("productId", "" + productId);
                map.put("time", ""+Calendar.getInstance().getTimeInMillis());
                map.put("comment", editComment.getText().toString());
                Net.makeRequestParams(C.APP_URL+ ApiName.ADD_COMMENT_PRODUCT,map,CommentsActivity.this,CommentsActivity.this);
            }else{
                map.put("userid",aps.getData(Constants.USER_ID));
                map.put("gid", "" + groupId);
                map.put("comment", editComment.getText().toString());
                Net.makeRequestParams(C.APP_URL+ ApiName.ADD_GROUP_COMMENT_API,map,CommentsActivity.this,CommentsActivity.this);
            }
            //productId, time(milliseconds),comments, userId
        }
    }

    private void setAdapter(){

        CommentsAdapter adapter = new CommentsAdapter(lists);
        commentsList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(CommentsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentsList.setLayoutManager(layoutManager);
        commentsList.setAdapter(adapter);
        commentsList.invalidate();
    }

    private  JSONArray collectionCommentsArray(){
        JSONArray array = new JSONArray();
        for(int i=0;i<lists.size();i++){
            Items items = lists.get(i);


        }
        return  array;
    }
    public Response.Listener<JSONObject> comment_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);

            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                lists.clear();
                if(type.equals(Constants.COLLECTION)) {
                    Model commentsArray[] = model.getCommentsArray();
                    for (Model comments : commentsArray) {
                        lists.add(new Items(comments.getUserImage(), comments.getUserName(), comments.getComment(), ""));
                    }
                }else{
                    Model dataArray[] = model.getDataArray();
                    for (Model comments : dataArray) {
                        lists.add(new Items(comments.getUserImage(), comments.getUserName(), comments.getComment(), ""));
                    }
                }
            }
            setAdapter();
        }
    };

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(CommentsActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        isChange = true;
        pd.dismiss();
        Model model = new Model(jsonObject);
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            // Toast.makeText(CommentsActivity.this,model.getMessage(),Toast.LENGTH_LONG).show();
            editComment.setText("");
            addCommentList();
            setAdapter();
        }
    }
}
