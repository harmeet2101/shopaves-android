package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeShippingAddressActivity extends AppCompatActivity {
    private AdressAdapter adapter;
    private RecyclerView recyclerView;
    private AppStore aps;
    private ProgressDialog pd;
    private ArrayList<Items> list;
    private TextView numberofAddress;
    private int EDIT_ADDRESS = 131;
    private String type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_address_initial);
        TextView title = (TextView)findViewById(R.id.title);
        numberofAddress = (TextView)findViewById(R.id.numberofAddress);
        title.setText(getResources().getString(R.string.changeaddress_c));
        recyclerView = (RecyclerView) findViewById(R.id.address_recyclevw);
        type = getIntent().getStringExtra(Constants.TYPE) != null ? getIntent().getStringExtra(Constants.TYPE) : "";
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        findViewById(R.id.add_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ChangeShippingAddressActivity.this,ShippingAddress.class),100);
            }
        });

        list = new ArrayList<>();
        adapter = new AdressAdapter(list);
        aps = new AppStore(this);
        makeRequest();
    }
    private void makeRequest(){
        Map<String,String> map = new HashMap<>();
        map.put(Constants.USERID,aps.getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GET_ADDRESS,map,response,error);
        Log.e("address_param",map.toString());
        pd = C.getProgressDialog(ChangeShippingAddressActivity.this);
    }
    public Response.ErrorListener error = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(ChangeShippingAddressActivity.this, "" + VolleyErrors.setError(volleyError), Toast.LENGTH_LONG).show();
        }
    };

    public Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("address_response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                list.clear();
                Model[] models = model.getDataArray();
                for (Model data : models) {
                    list.add(new Items(data.getName(),data.getAddress(),data.getAddressLine2(),data.getMobile(),data.getCity(),data.getZipCode(),data.getState(),data.getCountry(),data.getId(),data.getCurrent()));

                    }
                numberofAddress.setText(" (" + models.length +")");
            }
            setAdapter();
        }
    };

    public Response.Listener<JSONObject> r_set = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("address_response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
               setResult(RESULT_OK);
               finish();
            }
        }
    };
    public void clear() {
        int size = this.list.size();
        this.list.clear();
        adapter.notifyDataSetChanged();
    }

    private void setAdapter(){
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
    private class AdressAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;

        public AdressAdapter(List<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_change_address, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final Items items = source.get(position);
            holder.userName.setText(items.name);
            holder.phone.setText(items.phone);
            holder.Address.setText(items.address1 + ", "+items.address2 + " "+ items.city+", "+items.state+", "+items.country+", "+items.zip);
            aps.setData(Constants.ADDRESS,holder.Address.getText().toString());
            if (items.isCurrent) {
                holder.check1.setChecked(true);
                holder.shippingChange.setText(getResources().getString(R.string.default_shipping_address));
            }
            else {
                holder.check1.setChecked(false);
                holder.shippingChange.setText(getResources().getString(R.string.make_this_my_default_shipping_address));
            }

            holder.check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    /*Map<String,String> map = new HashMap<>();
                    map.put(Constants.USERID,aps.getData(Constants.USER_ID));
                    map.put("adrId",""+items.id);
                    map.put("type","1");
                    Net.makeRequestParams(C.APP_URL+ ApiName.ADDRESSSETTING,map,response,error);
                    Log.e("address_param",map.toString());
                    pd = C.getProgressDialog(ChangeShippingAddressActivity.this);*/
                    makeAddressSettingRequest(items.id,"1");
                }
            });


            holder.edit_adrs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(ChangeShippingAddressActivity.this,ShippingAddress.class).putExtra(
                            Constants.TYPE,"edit"
                    ).putExtra("name",items.name)
                     .putExtra("address1",items.address1)
                     .putExtra("address2",items.address2)
                     .putExtra("phone",items.phone)
                            .putExtra("city",items.city)
                            .putExtra("state",items.state)
                            .putExtra("country",items.country)
                            .putExtra("zip",items.zip)
                            .putExtra("id",items.id),EDIT_ADDRESS);
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeAddressSettingRequest(items.id,"2");
                }
            });
           /* holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String,String> map = new HashMap<>();
                    map.put(Constants.USERID,aps.getData(Constants.USER_ID));
                    map.put("adrId",""+items.id);
                    map.put("type","1");
                    Net.makeRequestParams(C.APP_URL+ ApiName.ADDRESSSETTING,map,r_set,error);
                    Log.e("address_param",map.toString());
                    pd = C.getProgressDialog(ChangeShippingAddressActivity.this);
                }
            });*/

            holder.optionClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if(findViewById(R.id.menuview).getVisibility() == View.VISIBLE)
                    holder.menuView.setVisibility(holder.menuView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(type.equals(Constants.SELECTED_ITEMS))
                    setResult(900,new Intent().putExtra(Constants.COUNTRY,items.country).putExtra(Constants.STREET_ADDRESS,items.address1+items.address2).putExtra(Constants.CITY,items.city).putExtra(Constants.STATE,items.state).putExtra(Constants.PINCODE,items.zip).putExtra(Constants.ADDRESS,items.address1+", "+items.address2));
                    finish();
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

        public void makeAddressSettingRequest(int id,String type){
            Map<String,String> map = new HashMap<>();
            map.put(Constants.USERID,aps.getData(Constants.USER_ID));
            map.put("adrId",""+id);
            map.put("type",type);
            Net.makeRequestParams(C.APP_URL+ ApiName.ADDRESSSETTING,map,response,error);
            Log.e("address_param",map.toString());
            pd = C.getProgressDialog(ChangeShippingAddressActivity.this);
        }
    }
    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userName,Address,phone,shippingChange,remove;
        TextView edit_adrs;
        ToggleButton check1;
        RelativeLayout optionClick;
        CardView menuView;
        public MyViewHolder(View itemView) {
            super(itemView);
            userName = (TextView)itemView.findViewById(R.id.user_name);
            Address = (TextView)itemView.findViewById(R.id.addresss);
            phone = (TextView)itemView.findViewById(R.id.mob);
            edit_adrs = (TextView) itemView.findViewById(R.id.edit_adrs);
            check1 = (ToggleButton) itemView.findViewById(R.id.chkState);
            optionClick = (RelativeLayout)itemView.findViewById(R.id.optionclick);
            shippingChange = (TextView)itemView.findViewById(R.id.shippingchange);
            remove = (TextView)itemView.findViewById(R.id.remove);
            menuView = (CardView)itemView.findViewById(R.id.menuview);
        }
    }
    private class Items {
        public String name,address1,address2,phone,city,zip,state,country;
        int id;
        boolean isCurrent;

        public Items(String name,String address1,String address2,String phone,String city,String zip,String state,String country,int id,boolean isCurrent) {
            this.name = name;
            this.address1 = address1;
            this.address2 = address2;
            this.phone = phone;
            this.city = city;
            this.zip = zip;
            this.state = state;
            this.country = country;
            this.id = id;
            this.isCurrent = isCurrent;
        }
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==RESULT_OK || requestCode == EDIT_ADDRESS && resultCode==RESULT_OK){
            makeRequest();
        }
    }
}
