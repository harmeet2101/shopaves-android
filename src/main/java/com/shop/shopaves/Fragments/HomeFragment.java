package com.shop.shopaves.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.AddToCartActivity;
import com.shop.shopaves.Activity.ProductByCategoryActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Interface.FragmentCallBack;
import com.shop.shopaves.Interface.ValueCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.network.Net;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amsyt005 on 23/12/16.
 */

public class HomeFragment extends Fragment {
    private TabLayout tabLayout;
    private String type = Constants.SHOP;
    private EditText searchValue;
    private ListView list;
    private Adapter adapter;
    private String shopSearchResponse;
    private TextView notFound;
    private RelativeLayout searchingLauout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        tabLayout = (TabLayout)view.findViewById(R.id.home_tab);
        searchValue = (EditText)view.findViewById(R.id.searchvalue);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.brands)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.deals)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.shop)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.collection)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.sets)));
        notFound = (TextView)view.findViewById(R.id.notfound);
        searchingLauout = (RelativeLayout)view.findViewById(R.id.searchingdisplay);
      //  tabLayout.addTab(tabLayout.newTab().setText("FURNISHING"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);     //   tabLayout.setTabMode(TabLayout.MODE_FIXED);
        replaceFragment(new Brands(searchValue));
        type = getArguments().getString(Constants.TYPE);
        list = (ListView) view.findViewById(R.id.list);
        adapter = new Adapter(getActivity(), R.layout.row_search_result);

        list.setAdapter(adapter); tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabSelection(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tabSelection(tab);
            }
        });

        if(type.equals(Constants.SHOP))
        tabLayout.getTabAt(2).select();
        else if(type.equals(Constants.COLLECTION))
            tabLayout.getTabAt(3).select();
        else if(type.equals(Constants.DEALS))
            tabLayout.getTabAt(1).select();

        searchValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });
        searchValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
//                    list.setVisibility(View.VISIBLE);
//                    Map<String,String> map = new HashMap<>();
//                    map.put("key",searchValue.getText().toString());
//                    Net.makeRequest(C.APP_URL+ ApiName.SEARCH,map,r_predictive,error);
                }else{
                    list.setVisibility(View.GONE);
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
      /*  searchValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Map<String,String> map = new HashMap<>();
                    map.put("key",searchValue.getText().toString());
                    Net.makeRequest(C.APP_URL+ ApiName.SEARCH,map,r_predictive,error);
                    return true;
                }else{
                    list.setVisibility(View.GONE);
                }
                return false;
            }
        });*/

     /* view.findViewById(R.id.homeview).setOnTouchListener(new View.OnTouchListener() {
          @Override
          public boolean onTouch(View view, MotionEvent motionEvent) {
              searchingLauout.setVisibility(View.GONE);
              return false;
          }
      });*/
        return view;
    }

    private ValueCallBack valueCallBack = new ValueCallBack() {
        @Override
        public void callBack(String value) {
            Model m = new Model(value);
            if (m.getStatus().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
                searchingLauout.setVisibility(View.VISIBLE);
                shopSearchResponse = value;
                Model[] model = m.getDataArray();
                if (model.length > 0 && model != null) {
                    notFound.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                    adapter.clear();
                    for (Model m1 : model) {
                        adapter.add(m1);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    list.setVisibility(View.GONE);
                    notFound.setVisibility(View.VISIBLE);
                }
            }
        }
    };
    FragmentCallBack fragmentCallBack = new FragmentCallBack() {
        @Override
        public void onFragmentCallBack(Fragment fragment) {
            if(fragment instanceof Brands){
                replaceFragment(fragment);
                tabLayout.getTabAt(0).select();
            }
        }

        @Override
        public void finishFragment() {
        }
    };

    private void tabSelection(TabLayout.Tab tab){
        if (tab.getPosition() == 0) {
            C.SEARCH_TYPE = Constants.BRAND;
            replaceFragment(new Brands(searchValue));
        } else if (tab.getPosition() == 1) {
            C.SEARCH_TYPE = Constants.DEALS;
            replaceFragment(new Deals(searchValue));
        } else if(tab.getPosition()==2) {
            C.SEARCH_TYPE = Constants.SHOP;
            replaceFragment(new Shop(searchValue,fragmentCallBack,valueCallBack,searchingLauout));
        }
        else if(tab.getPosition()==3) {
            C.SEARCH_TYPE = Constants.COLLECTION;
            replaceFragment(new Collection(searchValue));
        }
        else if(tab.getPosition()==4) {
            C.SEARCH_TYPE = Constants.SETS;
            replaceFragment(new MySetsFragment(searchValue));
        }
        else {
            C.SEARCH_TYPE = Constants.BRAND;
            replaceFragment(new Brands(searchValue));
        }
    }

    private class Adapter extends ArrayAdapter<Model> {
        private int resource;
        private H h;
        Context context;

        public Adapter(Context context, int resource) {
            super(context, resource);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            h = new H();
            final Model item = getItem(position);
            View convertview = view;
            if (convertview == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertview = inflater.inflate(resource, null);
                h.name = (TextView) convertview.findViewById(R.id.insti_text);
                h.category = (TextView) convertview.findViewById(R.id.category);
                convertview.setTag(h);

            } else {
                h = (H) convertview.getTag();
            }
            try {
                String name = item.getName();
                h.name.setText(name);
                Model model = new Model(item.getParentCategory());
                h.category.setText("in "+model.getName());

                convertview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    list.setVisibility(View.GONE);
                        startActivity(new Intent(getActivity(), ProductByCategoryActivity.class).putExtra(Constants.TYPE,Constants.SHOP).putExtra(Constants.SUB_CATEGORY_ID,item.getId()).putExtra(Constants.POSITION,position).putExtra(Constants.SUBCATEGORIES,shopSearchResponse));
                    }
                });

            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return convertview;
        }
    }

        class H{
            TextView name,category;
        }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.containr, fragment);
        transaction.commit();
    }

    private String getSearchValue(){
        return  searchValue.getText().toString();
    }



    /*  searchValue.setOnEditorActionListener(new OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG,"Enter pressed");
            }
            return false;
        }
    });*/
}
