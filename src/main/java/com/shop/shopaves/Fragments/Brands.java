package com.shop.shopaves.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.AddProductActivity;
import com.shop.shopaves.Activity.BrandDetailActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Constant.SideSelector;
import com.shop.shopaves.Interface.FragmentCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.EndlessScrollListener;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.apache.http.util.TextUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Brands extends Fragment implements Response.ErrorListener,Response.Listener<JSONObject>{
    private StickyListHeadersListView stickyList;
    private ArrayList<BrandItem> dataList = new ArrayList<>();
    private BrandsApapter adapter;
    private SideSelector holderSelector;
    private ProgressDialog pd;
    private AppStore aps;
    private String type = "BrandList";
    private ListView brandList;
    private int listScrollPosition = 0;
    private EditText search;
    private boolean isLoadMore = true;
    private boolean isSearching  = false;
    private ImageView followReference;

    public Brands(EditText search) {
        this.search = search;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyApp.getInstance().trackScreenView("Brands screen");
        View v = inflater.inflate(R.layout.fragment_brands, container, false);
        stickyList = (StickyListHeadersListView)v.findViewById(R.id.list);
        brandList = (ListView)v.findViewById(R.id.brandlist);
        adapter = new BrandsApapter();
        search.setFocusable(false);
        search.setFocusableInTouchMode(true);
        //side bar
        search.setText("");
        holderSelector= (SideSelector)v.findViewById(R.id.alphabet_view);

        aps = new AppStore(getActivity());
        for (char c : SideSelector.ALPHABET) {
            TextView textView=new TextView(getActivity());
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTextSize(12.0f);
            textView.setLayoutParams(new LinearLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1.0f));
            textView.setGravity(Gravity.CENTER);
            textView.setText(String.valueOf(c));
            holderSelector.addView(textView);
        }
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //    startActivity(new Intent(getActivity(),DragImage.class));
               startActivity(new Intent(getActivity(), BrandDetailActivity.class));
            }
        });

        brandList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
              Log.e("totalbrandscount",""+totalItemsCount);
                listScrollPosition = totalItemsCount-3;
                if(totalItemsCount > 40)
                makeAllBrandRequest(""+totalItemsCount,"");                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        if(C.SEARCH_TYPE.equals(Constants.BRAND)){
            search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if(!isSearching) {
                            isSearching = true;
                            makeAllBrandRequest("", search.getText().toString());
                            hideSoftKeyboard();
                        }
                        Log.i("enterPressed","Enter pressed");
                        return  true;
                    }
                    return false;
                }
            });
        }

        v.findViewById(R.id.createproduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddProductActivity.class));
            }
        });
        makeAllBrandRequest("","");
        return v;
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
    public void makeAllBrandRequest(String skip,String key){
        pd =  C.getProgressDialog(getActivity());
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
        map.put("userId",aps.getData(Constants.USER_ID));
        map.put("skip",skip);
        map.put("key",C.getEncodedString(key));
        Net.makeRequest(C.APP_URL+ ApiName.GET_BRANDS_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
       Toast.makeText(getActivity(), VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
            Log.e("BrandResponse", jsonObject.toString());
            if(type.equals("BrandList")) {
                Model dataArray[] = model.getDataArray();
                if(isSearching){
                    adapter.clear();
                }
                for (Model data : dataArray) {
                    adapter.add(new BrandItem(data.getName(),data.getIcon(),data.getBannerImage(), ""+data.getFollowers(),""+data.getItems(),data.getItemStatus(),"" + data.getId()));
                }
                brandList.setAdapter(adapter);
                if(isSearching){
                    isSearching = false;
                }else
                brandList.setSelection(listScrollPosition);
                adapter.notifyDataSetChanged();
              //  stickyList.setAdapter(adapter);
            }else{
                if(followReference.getTag().equals("1")){
                    followReference.setTag("1");
                    followReference.setImageResource(R.drawable.crossimg);
                }else{
                    followReference.setTag("2");
                    followReference.setImageResource(R.drawable.plusblack);
                }
                Toast.makeText(getActivity(),model.getMessage(),Toast.LENGTH_SHORT).show();
            }}
    }

    private static class MyHolder{
        TextView BrandName,followersCount,itemsCount;
        RelativeLayout followBrand;
        CircleImageView brandLogo;
        ImageView brandBanner,followIcon;
    }

    private class BrandsApapter extends ArrayAdapter<BrandItem> implements Constants {
        MyHolder holder ;
        public BrandsApapter() {
            super(getActivity(), R.layout.single_brand_layout);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row =convertView;
            holder = null;
            if (row == null) {
                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                row = inflater.inflate(R.layout.single_brand_layout, parent, false);
                holder = new MyHolder();
                holder.BrandName = (TextView) row.findViewById(R.id.brand_name);
                holder.followBrand = (RelativeLayout)row.findViewById(R.id.followbrand);
                holder.followIcon = (ImageView)row.findViewById(R.id.followicon);
                holder.followersCount = (TextView)row.findViewById(R.id.followerscount);
                holder.itemsCount = (TextView)row.findViewById(R.id.itemscount);
                holder.brandLogo = (CircleImageView)row.findViewById(R.id.brand_logo);
                holder.brandBanner = (ImageView)row.findViewById(R.id.brandbanner);
                row.setTag(holder);
            }
            else {
                holder = (MyHolder) row.getTag();
            }
            final BrandItem item = getItem(position);

            holder.BrandName.setText(item.title);
            holder.followersCount.setText(item.followersCount);
            holder.itemsCount.setText(item.itemsCount);
            holder.followBrand.setBackground(getResources().getDrawable(R.drawable.green_circle_drawable));
          if(item.followStatus == 2){
              holder.followBrand.setBackgroundResource(R.drawable.green_circle_drawable);
              holder.followIcon.setImageResource(R.drawable.plusblack);
          }else{
              holder.followBrand.setBackgroundResource(R.drawable.yellow_circle_drawable);
              holder.followIcon.setImageResource(R.drawable.unfollowicon);
          }
         //   holder.followIcon.setImageResource(item.followStatus == 2 ? R.drawable.unfollowicon  : R.drawable.plusblack);
            Log.e("follow icon tag",""+holder.followIcon.getTag());
            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(item.logo)).placeholder(R.drawable.defaultholder).into(holder.brandLogo);
            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(item.banner)).placeholder(R.drawable.defaultbannerbg).into(holder.brandBanner);
            holder.followIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("follow icon tag click",""+holder.followIcon.getTag());
                    if (aps.getData(Constants.USER_ID)!=null){
                        type = "follow";
                        pd =  C.getProgressDialog(getActivity());
                        Map<String,String> map = new HashMap<>();
                        map.put("brandId", item.id);
                        map.put("userId",aps.getData(Constants.USER_ID));
                        Net.makeRequestParams(C.APP_URL + ApiName.FOLLOW_BRAND_API, map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                pd.dismiss();
                                Model model = new Model(jsonObject.toString());
                                if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                                    adapter.getItem(position).followStatus = adapter.getItem(position).followStatus == 1 ? 2 : 1;
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(),model.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                pd.dismiss();
                            }
                        });
                    }else
                        C.setLoginMessage(getContext());

                }
            });

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(getActivity(), BrandDetailActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString(Constants.BRAND_NAME, item.title);
                    extras.putString(Constants.BRAND_ID, item.id);
                    extras.putString(Constants.BRAND_BANNER, item.banner);
                    extras.putString(Constants.BRAND_LOGO, item.logo);
                    mIntent.putExtras(extras);
                    startActivity(mIntent);
                }
            });
            return row;
        }
    }

    private class BrandItem{
        private String title;
        private String id ;
        private String followersCount,logo,banner;
        private String itemsCount;
        private int followStatus;

        public BrandItem(String title,String logo,String banner,String followersCount,String itemsCount,int followStatus,String id) {
            this.title = title;
            this.id = id;
            this.itemsCount = itemsCount;
            this.followersCount = followersCount;
            this.logo = logo;
            this.banner = banner;
            this.followStatus = followStatus;
        }
    }
}
