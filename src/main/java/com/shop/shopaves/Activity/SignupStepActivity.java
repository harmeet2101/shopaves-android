package com.shop.shopaves.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Constant.NonSwipeableViewPager;
import com.shop.shopaves.Fragments.Categories;
import com.shop.shopaves.Fragments.EnterTags;
import com.shop.shopaves.Fragments.SelectCities;
import com.shop.shopaves.Fragments.SelectColors;
import com.shop.shopaves.Fragments.SelectGender;
import com.shop.shopaves.Fragments.SelectStyle;
import com.shop.shopaves.Fragments.Stores;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.dataModel.CityData;
import com.shop.shopaves.dataModel.ColorData;
import com.shop.shopaves.dataModel.EnterTagData;
import com.shop.shopaves.dataModel.GenderInfo;
import com.shop.shopaves.dataModel.SelectCategoryData;
import com.shop.shopaves.dataModel.StoresData;
import com.shop.shopaves.dataModel.StyleData;
import com.shop.shopaves.network.Net;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupStepActivity extends AppCompatActivity {

    private FragmentPagerAdapter adapter;
    private NonSwipeableViewPager viewPager;
    RelativeLayout header;
    private ArrayList<ImageView> dots = new ArrayList<>();
    boolean isPreference;
    RelativeLayout nxt;
    TextView next;
    AppStore appStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_gender);
        C.applyTypeface(C.getParentView(findViewById(R.id.main)), C.getHelveticaNeueFontTypeface(SignupStepActivity.this));
        MyApp.getInstance().trackScreenView("pre steps activity");
        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
        header = (RelativeLayout) findViewById(R.id.top_header);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        final TextView headText = (TextView) findViewById(R.id.header_txt);
        headText.setText(Constants.H_TITLES[0]);
        header.setVisibility(viewPager.getCurrentItem() == 0 ? View.GONE : View.VISIBLE);
        appStore = new AppStore(this);
        nxt = (RelativeLayout) findViewById(R.id.nxt);
        next = (TextView) findViewById(R.id.next);
        Intent intent = getIntent();
        if (intent!=null && intent.getStringExtra(Constants.TYPE)!=null){
            isPreference = intent.getStringExtra(Constants.TYPE).equalsIgnoreCase("preference");
            if (isPreference){
                findViewById(R.id.cancel).setVisibility(View.VISIBLE);
                findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }else
                findViewById(R.id.cancel).setVisibility(View.GONE);
        }

        viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                headText.setText(Constants.H_TITLES[position]);
                header.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                //findViewById(R.id.nxt).setVisibility(position == adapter.getCount()-1 ? View.INVISIBLE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initDots();
        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                if(viewPager.getCurrentItem()==adapter.getCount()-1 && !isPreference)
                    logipage();
                else{
                    if(getItem() == 1){
                        List<StoresData> storeList = StoresData.listAll(StoresData.class);
                        for (StoresData cd : storeList){
                            if (cd.IS_STORE_SELECTED){
                                count = count+1;
                            }
                        }
                        if(count<3)
                        {
                            Toast.makeText(SignupStepActivity.this,"Please select at least 3 options to continue",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else if(getItem() == 2) {
                        List<SelectCategoryData> categoryList = SelectCategoryData.listAll(SelectCategoryData.class);
                        for (SelectCategoryData cd : categoryList){
                            if (cd.IS_CATEGORY_SELECTED){
                                count = count+1;
                            }
                        }
                        if (count < 3) {
                            Toast.makeText(SignupStepActivity.this, "Please select at least 3 options to continue", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else if(getItem() == 3){
                        List<EnterTagData> tagList = EnterTagData.listAll(EnterTagData.class);
                        for (EnterTagData cd : tagList){
                            if (cd.TAG_VALUE.equals("1")){
                                count  = count+1;
                            }
                        }
                        if(count < 1){
                            Toast.makeText(SignupStepActivity.this, "Please select at least 1 options to continue", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else if(getItem() == 4){
                        List<StyleData> styleList = StyleData.listAll(StyleData.class);
                        for (StyleData cd : styleList){
                            if (cd.TAG_VALUE.equals("1")){
                                count  = count+1;
                            }
                        }
                        if(count < 1){
                            Toast.makeText(SignupStepActivity.this, "Please select at least 1 options to continue", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else if(getItem() == 5){
                        List<CityData> cityList = CityData.listAll(CityData.class);
                        for (CityData cd : cityList){
                            if (cd.TAG_VALUE.equals("1")){
                                count  = count+1;
                            }
                            if (isPreference)
                                next.setText("SAVE");
                        }
                        if(count < 1){
                            Toast.makeText(SignupStepActivity.this, "Please select at least 1 options to continue", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else if(getItem() == 6){

                        List<ColorData> colorList = ColorData.listAll(ColorData.class);
                        for (ColorData cd : colorList){
                            if (cd.TAG_VALUE.equals("1")){
                                count  = count+1;
                            }
                        }
                        if (isPreference && !TextUtils.isEmpty(appStore.getData(Constants.USER_ID))){
                            signUpdRequest();
                        }else {
                            C.setLoginMessage(SignupStepActivity.this);
                        }

                        if(count < 1){
                            Toast.makeText(SignupStepActivity.this, "Please select at least 1 options to continue", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    viewPager.setCurrentItem(getItem() + 1, true);
                }
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(getItem() - 1, true);
            }
        });
    }
    private void signUpdRequest() {
        final JSONObject SIGN_UP_MAP = new JSONObject();
        JSONArray SIGN_UP_CITIES_ARRAY = new JSONArray();
        JSONArray SIGN_UP_COLORS_ARRAY = new JSONArray();
        JSONArray SIGN_UP_TAGS_ARRAY = new JSONArray();
        JSONArray SIGN_UP_STYLE_ARRAY = new JSONArray();
        JSONArray SIGN_UP_CATEGORY_ARRAY = new JSONArray();
        try {

/*CITY*/
            List<CityData> list = CityData.listAll(CityData.class);
            for (CityData cd : list) {
                if (cd.TAG_VALUE.equals("1")) {
                    Map CITY_MAP = new HashMap();
                    CITY_MAP.put("name", cd.cityname);
                    CITY_MAP.put("id", TextUtils.isEmpty(cd.TAG_ID) ? "" : cd.TAG_ID);
                    SIGN_UP_CITIES_ARRAY.put(new JSONObject(CITY_MAP));
                }
            }

/*Color*/
            List<ColorData> colorList = ColorData.listAll(ColorData.class);
            for (ColorData cd : colorList) {
                if (cd.TAG_VALUE.equals("1")) {
                    Map COLOR_MAP = new HashMap();
                    COLOR_MAP.put("name", cd.TAG_NAME);
                    COLOR_MAP.put("colorCode", cd.COLOR_CODE);
                    COLOR_MAP.put("id", TextUtils.isEmpty(cd.TAG_ID) ? "" : cd.TAG_ID);
                    SIGN_UP_COLORS_ARRAY.put(new JSONObject(COLOR_MAP));
                }
            }

            /*TAG*/

            List<EnterTagData> tagList = EnterTagData.listAll(EnterTagData.class);
            for (EnterTagData cd : tagList) {
                if (cd.TAG_VALUE.equals("1")) {
                    Map TAG_MAP = new HashMap();
                    TAG_MAP.put("name", cd.TAG);
                    TAG_MAP.put("id", TextUtils.isEmpty(cd.TAG_ID) ? "" : cd.TAG_ID);
                    SIGN_UP_TAGS_ARRAY.put(new JSONObject(TAG_MAP));
                }
            }
/*style*/

            List<StyleData> styleList = StyleData.listAll(StyleData.class);
            for (StyleData cd : styleList) {
                if (cd.TAG_VALUE.equals("1")) {
                    Map STYLE_MAP = new HashMap();
                    STYLE_MAP.put("name", cd.STYLE_TAG);
                    STYLE_MAP.put("id", TextUtils.isEmpty(cd.TAG_ID) ? "" : cd.TAG_ID);
                    SIGN_UP_STYLE_ARRAY.put(new JSONObject(STYLE_MAP));
                }
            }

            /*category*/

            List<SelectCategoryData> categoryList = SelectCategoryData.listAll(SelectCategoryData.class);
            for (SelectCategoryData cd : categoryList) {
                if (cd.IS_CATEGORY_SELECTED) {
                    Map CATEGORY_MAP = new HashMap();
                    CATEGORY_MAP.put("name", cd.Category_name);
                    CATEGORY_MAP.put("id", TextUtils.isEmpty(cd.TAG_ID) ? "" : cd.TAG_ID);
                    SIGN_UP_CATEGORY_ARRAY.put(new JSONObject(CATEGORY_MAP));
                }
            }

            SIGN_UP_MAP.put("userId",""+appStore.getData(Constants.USER_ID));
            SIGN_UP_MAP.put("prefId","");
            SIGN_UP_MAP.put("cities", SIGN_UP_CITIES_ARRAY);
            SIGN_UP_MAP.put("colors", SIGN_UP_COLORS_ARRAY);
            SIGN_UP_MAP.put("tags", SIGN_UP_TAGS_ARRAY);
            SIGN_UP_MAP.put("styles", SIGN_UP_STYLE_ARRAY);
            SIGN_UP_MAP.put("intrusts", SIGN_UP_CATEGORY_ARRAY);
            SIGN_UP_MAP.put("gender", new GenderInfo().GENDER);
            Log.e("preference=",SIGN_UP_MAP.toString());
            Net.makeRequest(C.APP_URL + ApiName.EDITPREFRENCE, SIGN_UP_MAP.toString(), r, e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Response.ErrorListener e = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(SignupStepActivity.this,volleyError.getMessage(),Toast.LENGTH_SHORT);
        }
    };

    Response.Listener<JSONObject> r =new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            Log.e("preference response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if (model.getStatus().equals(Constants.SUCCESS_CODE)){
                Toast.makeText(SignupStepActivity.this, model.getMessage() , Toast.LENGTH_SHORT).show();
                logipage();
            }
        }
    };
    private void initDots(){
        LinearLayout lv = (LinearLayout) findViewById(R.id.dots);
        dots.clear();
        for (int i=0;i< lv.getChildCount();i++){
            ImageView img = (ImageView) lv.getChildAt(i);
            dots.add(img);
        }
    }

    private void setIndicator(int position){
        for (int i =0; i < dots.size();i++){
            ImageView img = dots.get(i);
            img.setImageResource(i == position ? R.drawable.indicator_yellow : R.drawable.indicator);
            img.setAlpha(i<=viewPager.getCurrentItem() ? 1 : 0.4f);
        }
    }

    private void logipage(){
            Intent intent = new Intent(getApplication(),HomeActivity.class);
            startActivity(intent);
            finish();
    }

    private int getItem() {
        return viewPager.getCurrentItem();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 7;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }



        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SelectGender();
                case 1:
                    return new Stores();
                case 2:
                    return new Categories();
                case 3:
                    return new EnterTags();

                case 4:
                    return new SelectStyle();
                case 5:
                    return new SelectCities();

                case 6:
                    return new SelectColors();
                default:
                    return null;
            }
        }

    }


}
