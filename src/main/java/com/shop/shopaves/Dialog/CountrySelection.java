package com.shop.shopaves.Dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Interface.SelectionCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HANAMANTRAYA on 17-03-2017.
 */

public class CountrySelection extends Dialog implements Response.Listener<JSONObject>,Response.ErrorListener{
    private RecyclerView itemrecycleView;
    private CountryAdapter countryAdapter;
    private Context context;
    private ProgressDialog pd;
    private String type,id;
    private SelectionCallBack selectionCallBack;
    private ArrayList<Items> placeItems = new ArrayList<>();
    private String selectionId,selectionName;
    private int selectedPosition = -1;
    public CountrySelection(Context context, SelectionCallBack selectionCallBack, String id, String type) {
        super(context,R.style.Theme_Dialog);
        this.context = context;
        this.id = id;
        this.type = type;
        this.selectionCallBack = selectionCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_selection);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        this.setCancelable(true);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemrecycleView = (RecyclerView)findViewById(R.id.selection);
        TextView selectionType = (TextView)findViewById(R.id.type);
        makeCountriesRequest(id,type);
        selectionType.setText("Select " + type);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionCallBack.selection(selectionId,selectionName,type);
                dismiss();
            }
        });
    }

    private void makeCountriesRequest(String id,String type){
        pd = C.getProgressDialog(context);
        HashMap hashMap = new HashMap();
        if(type.equals(Constants.STATE))
            hashMap.put("countryId",id);
        else if(type.equals(Constants.CITY))
            hashMap.put("stateId",id);
        Net.makeRequest(C.APP_URL+ ApiName.GET_COUNTRIES_API,hashMap,this,this);
    }


    private void setCountryAdapter(){
        countryAdapter = new CountryAdapter(placeItems);
        itemrecycleView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        itemrecycleView.setLayoutManager(layoutManager);
        itemrecycleView.setAdapter(countryAdapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(context, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        Log.e("response",jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            for(Model data : dataArray){
                if(type.equals(Constants.COUNTRY))
                placeItems.add(new Items(""+data.getCountryId(),data.getName(),false));
                else if(type.equals(Constants.STATE))
                placeItems.add(new Items(""+data.getStateId(),data.getName(),false));
                else
                    placeItems.add(new Items(""+data.getCityId(),data.getName(),false));
            }
        }
        setCountryAdapter();
    }

    private class CountryAdapter extends RecyclerView.Adapter<MyViewHolder>{
        ArrayList<Items> source;

        public CountryAdapter(ArrayList<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_category_selection,parent,false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.name.setText(source.get(position).name);
           // holder.selection.setImageResource(source.get(position).ischeck ? R.drawable.check : R.drawable.unchecked);
            if(selectedPosition == position){
                holder.selection.setImageResource(R.drawable.check);
                selectedPosition = -1;
            }
            else
            holder.selection.setImageResource(R.drawable.unchecked);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = position;
                    selectionId = source.get(position).id;
                    selectionName = source.get(position).name;
                    countryAdapter.notifyDataSetChanged();
                }
            });
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

    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView selection;
        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.itemname);
            selection = (ImageView)itemView.findViewById(R.id.ic);
        }
    }

    private class Items{
        String name,id;
        boolean ischeck;
        public Items(String id,String name, boolean ischeck) {
            this.name = name;
            this.ischeck = ischeck;
            this.id = id;
        }
    }
}
