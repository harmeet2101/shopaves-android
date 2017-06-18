package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddToGroupActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{

    private RecyclerView groupList;
    private List<GroupItems> groupItemsList;
    private AppStore aps;
    private ProgressDialog pd;
    private String id;
    private boolean isProdect = false;
    private String type = Constants.GROUP_LIST;
    private int selectedPosotion = 0;
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_group);
        groupList = (RecyclerView)findViewById(R.id.grouplist);
        TextView title = (TextView)findViewById(R.id.title);
        TextView newGroup = (TextView)findViewById(R.id.toright);
        aps = new AppStore(this);
        title.setText("MY SETS");
        newGroup.setText("NEW");
        newGroup.setVisibility(View.VISIBLE);

        if(getIntent().getStringExtra(Constants.ID) != null)
       id =  getIntent().getStringExtra(Constants.ID);
        if(getIntent().getExtras().getBoolean(Constants.IS_PRODUCT)){
            isProdect = true;
        }

        groupItemsList = new ArrayList<>();
        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddToGroupActivity.this,CreateGroupActivity.class),100);
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

    private void setGroupAdapter(){
        adapter = new GroupAdapter(groupItemsList);
        groupList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddToGroupActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        groupList.setLayoutManager(layoutManager);
        groupList.setAdapter(adapter);
    }

    private void addToGroupRequest(String gid){
        type = Constants.PRODUCT;
        pd = C.getProgressDialog(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("gid",gid);
        if(isProdect)
        map.put("type","1");
        else
            map.put("type","2");
        map.put("id",id);
        Net.makeRequestParams(C.APP_URL+ ApiName.ADD_ITEM_API,map,this,this);
    }

    private void makeGroupListRequest(){
        if (aps.getData(Constants.USER_ID )!=null){
            type = Constants.GROUP_LIST;
            pd =  C.getProgressDialog(AddToGroupActivity.this);
            Map<String,String> map = new HashMap<>();
            map.put("userId", aps.getData(Constants.USER_ID));
            map.put("myGroups", "true");
            Net.makeRequest(C.APP_URL+ ApiName.GET_GROUPS_API,map,this,this);
        }else
            C.setLoginMessage(this);
        }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(AddToGroupActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("Group response",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            if(type.equals(Constants.GROUP_LIST)) {
                Model dataArray[] = model.getDataArray();
                for (Model data : dataArray) {
                    JSONArray imageArray = data.getJsonImageArray();
                    Model productArray[] = data.getProductArray();
                    Model collectionArray[] = data.getCollectionsArray();
                    int count = (productArray != null ? productArray.length : 0) + (collectionArray != null ? collectionArray.length : 0);

                    try {
                        groupItemsList.add(new GroupItems("" + data.getId(), data.getName(),imageArray.length() > 0 ? imageArray.get(0).toString() : "",count));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                findViewById(R.id.noproduct).setVisibility(groupItemsList.size() > 0 ? View.GONE : View.VISIBLE);
                setGroupAdapter();
            }else if(type.equals(Constants.GROUP_DELETE)){
                groupList.removeAllViews();
                groupItemsList.remove(selectedPosotion);
                adapter.notifyDataSetChanged();
                // setGroupAdapter();
            }else{
                Toast.makeText(AddToGroupActivity.this,""+model.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(AddToGroupActivity.this,""+model.getMessage(),Toast.LENGTH_SHORT).show();

    }

    private class GroupAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<GroupItems> source;

        public GroupAdapter(List<GroupItems> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_addto_group, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final GroupItems items = source.get(position);
            //holder.groupImage.setImageResource(items.groupImage);
            holder.groupName.setText(items.groupName);
            holder.groupName.setTag(items.id);
            holder.groupItems.setText(""+items.groupItems);
            PicassoCache.getPicassoInstance(AddToGroupActivity.this).load(C.ASSET_URL + items.groupImage).into(holder.groupImage);
            holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(AddToGroupActivity.this);

                    builder.setTitle("Message");
                    builder.setMessage("Your group is updated successfully");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            type = Constants.GROUP_DELETE;
                            selectedPosotion = position;
                            makeDeleteGroupRequest(items.id);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert11 = builder.create();
                    alert11.show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
    }




    private class GroupItems {
        private String id;
        private String groupName;
        private String groupImage;
        private int groupItems;

        public GroupItems(String id, String groupName, String groupImage,int groupItems) {
            this.id = id;
            this.groupName = groupName;
            this.groupImage = groupImage;
            this.groupItems = groupItems;
        }
    }

    private void makeDeleteGroupRequest(String gid){
        pd =  C.getProgressDialog(AddToGroupActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        map.put("gid", gid);
        Net.makeRequestParams(C.APP_URL+ ApiName.DELETE_GROUP_API,map,this,this);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView groupImage,deleteItem;
        TextView groupName,groupItems;
        public MyViewHolder(View itemView) {
            super(itemView);
            groupImage = (ImageView) itemView.findViewById(R.id.group_pic);
            groupName = (TextView) itemView.findViewById(R.id.groupname);
            deleteItem  = (ImageView)itemView.findViewById(R.id.itemdelete);
            groupItems = (TextView)itemView.findViewById(R.id.itemcount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToGroupRequest(""+groupName.getTag());
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            groupItemsList.clear();
            makeGroupListRequest();
        }
    }
}
