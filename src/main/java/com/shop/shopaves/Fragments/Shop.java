package com.shop.shopaves.Fragments;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.AddProductActivity;
import com.shop.shopaves.Activity.AllCategoryBrandActivity;
import com.shop.shopaves.Activity.BrandDetailActivity;
import com.shop.shopaves.Activity.ProductByCategoryActivity;
import com.shop.shopaves.Activity.SelectItemActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Interface.FragmentCallBack;
import com.shop.shopaves.Interface.ValueCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.Util.NamePair;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */

public class Shop extends Fragment implements Response.ErrorListener,Response.Listener<JSONObject>{

    private ViewPager viewPager;
    private ShopPagerAdapter customPagerAdapter;
    private LinearLayout add_shop_category,add_shop_brands,add_fashion_trends,whats_new_shop,whats_hot_shop;
    private ProgressDialog pd;
    private ArrayList<Item> bannerResources = new ArrayList<>();
    private ImageView smallAddFirst,smallAddSecond,smallAddThird;
    private AppStore aps;
    private  ImageView likeProductIcon;
    private boolean isShop = true;
    private String shopCategories="";
    private EditText search;
    private FragmentCallBack fragmentCallBack;
    private ValueCallBack valueCallBack;
    private RelativeLayout searchingLauout;

    public Shop(EditText search, FragmentCallBack fragmentCallBack, ValueCallBack valueCallBack, RelativeLayout searchingLauout) {
        this.search = search;
        this.fragmentCallBack = fragmentCallBack;
        this.valueCallBack = valueCallBack;
        this.searchingLauout = searchingLauout;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        MyApp.getInstance().trackScreenView("Shop tab screen");
        add_shop_category = (LinearLayout)view.findViewById(R.id.addCaterory);
        add_shop_brands = (LinearLayout)view.findViewById(R.id.addBrands);
        add_fashion_trends = (LinearLayout)view.findViewById(R.id.addfashiontrends);
        whats_new_shop = (LinearLayout)view.findViewById(R.id.whatsnew);
        whats_hot_shop = (LinearLayout)view.findViewById(R.id.whatshot);
        smallAddFirst = (ImageView)view.findViewById(R.id.smalladds_first);
        smallAddSecond = (ImageView)view.findViewById(R.id.smalladds_second);
        smallAddThird = (ImageView)view.findViewById(R.id.smalladds_third);
        viewPager = (ViewPager)view.findViewById(R.id.pager);
        aps = new AppStore(getActivity());
        search.setFocusable(false);
        search.setFocusableInTouchMode(true);
        search.setText("");

        try {
            smallAddFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(""+smallAddFirst.getTag()));
                    try {
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setPackage("com.android.chrome");
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        // Chrome is probably not installed
                        // Try with the default browser
                        i.setPackage(null);
                        startActivity(Intent.createChooser(i, "Open With"));
                    }
                }
            });smallAddSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(""+smallAddSecond.getTag()));
                    try {
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setPackage("com.android.chrome");
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        // Chrome is probably not installed
                        // Try with the default browser
                        i.setPackage(null);
                        startActivity(Intent.createChooser(i, "Open With"));
                    }
                }
            });smallAddThird.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(""+smallAddThird.getTag()));
                    try {
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setPackage("com.android.chrome");
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        // Chrome is probably not installed
                        // Try with the default browser
                        i.setPackage(null);
                        startActivity(Intent.createChooser(i, "Open With"));
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        view.findViewById(R.id.createproduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddProductActivity.class));
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!TextUtils.isEmpty(search.getText().toString())){
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        pd = C.getProgressDialog(getActivity());
                        Map<String,String> map = new HashMap<>();
                        map.put("key",C.getEncodedString(search.getText().toString()));
                        Net.makeRequest(C.APP_URL+ ApiName.SEARCH,map,r_predictive,error);
                        return true;
                    }
                }/*else{
                    list.setVisibility(View.GONE);
                }*/
                return false;
            }
        });

        view.findViewById(R.id.scrolview).setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                searchingLauout.setVisibility(View.GONE);
            }
        });
        makeHomeShopRequest();
        return view;
    }

    private void shopByCategory(String id,String name,String img){
            final View category_view = getActivity().getLayoutInflater().inflate(R.layout.shop_by_category,null);
            ImageView categori_img = (ImageView)category_view.findViewById(R.id.product_img);
            final TextView itenName = (TextView)category_view.findViewById(R.id.categoryname);
            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(img)).placeholder(R.drawable.defaultholder).into(categori_img);
            itenName.setText(name);
            itenName.setTag(id);

        if(name.equals("See All")){
            categori_img.setImageResource(R.drawable.categoryall);
            itenName.setTextColor(getResources().getColor(R.color.google));
        }else{
            categori_img.setImageResource(R.drawable.defaultholder);
            itenName.setTextColor(getResources().getColor(R.color.black_pd));
        }

        category_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = add_shop_category.indexOfChild(category_view);
                TextView seeall = (TextView) v.findViewById(R.id.categoryname);
                if(seeall.getText().toString().equals("See All")){
                    startActivityForResult(new Intent(getActivity(),SelectItemActivity.class).putExtra("CONTENT_NAME",Constants.PRODUCT_CATEGORY),400);
                }else{
                    startActivity(new Intent(getActivity(), ProductByCategoryActivity.class).putExtra(Constants.SUB_CATEGORY_ID,Integer.parseInt(seeall.getTag().toString())).putExtra(Constants.POSITION,index-1).putExtra(Constants.SUBCATEGORIES,shopCategories).putExtra(Constants.TYPE,Constants.PRODUCT_CATEGORY));
                }
            }
        });
            add_shop_category.addView(category_view);
    }

    private void shopByBrands(int id,String name,String pathofImage){
            View brands_view = getActivity().getLayoutInflater().inflate(R.layout.shop_by_category, null);
            ImageView brands_img = (ImageView) brands_view.findViewById(R.id.product_img);
            TextView brandName = (TextView)brands_view.findViewById(R.id.categoryname);
             brandName.setText(name);
             brandName.setTag(id);
        //brands_img.setImageResource(R.drawable.collectiondefault);
           // brands_img.setImageResource(PRODUCT_BRANDS_RESOURCE[i]);
        if(name.equals("See All")){
            brands_img.setImageResource(R.drawable.allbrands);
            brandName.setTextColor(getResources().getColor(R.color.google));
        }else{
            Picasso.with(getContext()).load(C.getImageUrl(pathofImage)).placeholder(R.drawable.defaultholder)
                    .error(R.drawable.defaultholder).into(brands_img);
//            brands_img.setImageResource(R.drawable.defaultholder);
            brandName.setTextColor(getResources().getColor(R.color.black_pd));
        }
        brands_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView seeall = (TextView) v.findViewById(R.id.categoryname);
                if(seeall.getText().toString().equals("See All")){
                  //  startActivity(new Intent(getActivity(),AllCategoryBrandActivity.class));
                    //fragmentCallBack.onFragmentCallBack(new Brands(search));
                    fragmentCallBack.onFragmentCallBack(new Brands(search));
                }else{
                    startActivity(new Intent(getActivity(), BrandDetailActivity.class).putExtra(Constants.BRAND_ID,""+seeall.getTag()).putExtra(Constants.BRAND_NAME,seeall.getText().toString()));
                }

            }
        });


        add_shop_brands.addView(brands_view);
    }
    Response.Listener<JSONObject> r_predictive = new Response.Listener<JSONObject>(){

        @Override
        public void onResponse(JSONObject response) {
            pd.dismiss();
            try{
                Log.i("volley Response", response.toString());
                Model m = new Model(response);
                if(m.getStatus().equalsIgnoreCase(Constants.SUCCESS_CODE)){
                    valueCallBack.callBack(response.toString());
                    /*shopSearchResponse = response.toString();
                    Model[] model = m.getDataArray();
                    if (model.length>0 && model!=null){
                        list.setVisibility(View.VISIBLE);
                        adapter.clear();
                        for (Model m1 : model){
                            adapter.add(m1);
                            adapter.notifyDataSetChanged();
                        }
                    }else {
                        list.setVisibility(View.GONE);
                    }*/
                }
            }catch (Exception e){
            }
        }
    };

    Response.ErrorListener error = new Response.ErrorListener(){

        @Override
        public void onErrorResponse(VolleyError volleyError) {
         //   adapter.clear();
            pd.dismiss();
            try{
                String responseBody = new String(volleyError.networkResponse.data, "utf-8" );
                Log.e("Error", responseBody);
            }catch (NullPointerException e){
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), "Oops! something went wrong.", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };
    class ShopPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public ShopPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return bannerResources.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((CardView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.shop_pager_item, container, false);
            final ImageView imageView = (ImageView) itemView.findViewById(R.id.shoppager_img);
           // imageView.setImageResource(bannerResources.get(position));
            imageView.setTag(bannerResources.get(position).bannerLink);
            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(bannerResources.get(position).bannerImage)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(""+imageView.getTag()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setPackage("com.android.chrome");
                    try {
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        // Chrome is probably not installed
                        // Try with the default browser
                        i.setPackage(null);
                        startActivity(Intent.createChooser(i, "Open With"));
                    }*/

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(""+bannerResources.get(position).bannerLink));


                    String title = "Complete Action Using";

                    Intent chooser = Intent.createChooser(intent, title);
                    startActivity(chooser);
                }
            });

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((CardView) object);
        }
    }

    public class  Item{
        private String bannerImage;
        private String bannerLink;

        public Item(String bannerImage, String bannerLink) {
            this.bannerImage = bannerImage;
            this.bannerLink = bannerLink;
        }
    }

    private void makeHomeShopRequest(){
        pd = C.getProgressDialog(getActivity());
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(new AppStore(getActivity()).getData(Constants.USER_ID)))
        map.put("userId",new AppStore(getActivity()).getData(Constants.USER_ID));
        else
        map.put("","");
        Net.makeRequest(C.APP_URL+ ApiName.HOME_SHOP_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        try {
            Toast.makeText(getActivity(),""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("shopResponse",jsonObject.toString());
       if(isShop) {
           Model model = new Model(jsonObject);
           Model addsLarge[] = model.getAddsLargeArray();
           for (Model adds : addsLarge) {
               bannerResources.add(new Item(adds.getBannerImage(), adds.getLink()));
           }
           customPagerAdapter = new ShopPagerAdapter(getActivity());
           viewPager.setAdapter(customPagerAdapter);
           viewPager.setCurrentItem(2);

           Model shopBrandsArray[] = model.getShopBrandsArray();
           shopByBrands(0,"See All","");
           for (Model shoBrands : shopBrandsArray) {
               shopByBrands(shoBrands.getId(),shoBrands.getName(),shoBrands.getIcon());
           }
           shopCategories = model.getString(NamePair.SHOP_CATEGORY);
           Model shopCategoryArray[] = model.getShopCategoryArray();
           shopByCategory("", "See All","");
           for (Model shopCategory : shopCategoryArray) {
               shopByCategory(""+shopCategory.getId(),shopCategory.getName(), shopCategory.getIcon());
           }

           Model smallAdds[] = model.getShopAddsSmallArray();
           for (int i = 0; i < smallAdds.length; i++) {
               Model adds = smallAdds[i];
               if (i == 0) {
                   PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(adds.getBannerImage())).into(smallAddFirst);
                   smallAddFirst.setTag(adds.getBannerLink());
               }
               if (i == 1) {
                   PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(adds.getBannerImage())).into(smallAddSecond);
                   smallAddSecond.setTag(adds.getBannerLink());
               }
               if (i == 2) {
                   PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(adds.getBannerImage())).into(smallAddThird);
                   smallAddThird.setTag(adds.getBannerLink());
               }
           }

           Model fashionTrendsArray[] = model.getShopFashionTrendsArray();
           for (Model fashionTrends : fashionTrendsArray) {
             add_fashion_trends.addView(C.setProductView(getActivity(),fashionTrends));
           }

           Model shopNewProductArray[] = model.getShopNewProductArray();
           for (Model newProduct : shopNewProductArray) {
               whats_new_shop.addView(C.setProductView(getActivity(),newProduct));
           }
           Model shopHotProductArray[] = model.getShopHotProductArray();
           for (Model hotProduct : shopHotProductArray) {
               whats_hot_shop.addView(C.setProductView(getActivity(),hotProduct));
           }
           isShop = false;
       }else{
           //aps.setData(Constants.SHOP_CATEGORY_DATA,jsonObject.toString());
           //pd.dismiss();
       }
    }


    public Response.Listener<JSONObject> like_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                Log.e("productLikeStatus",jsonObject.toString());

                if (model.getCurrentLikeStatus() == 1) {
                    likeProductIcon.setImageResource(R.drawable.itemlike);
                    likeProductIcon.setTag("1");
                } else {
                    likeProductIcon.setImageResource(R.drawable.itemunlike);
                    likeProductIcon.setTag("2");

                }
            }            // makeCollectionDetailRequest();
        }
    };

    @Override
    public void onResume() {
        Log.e("resumeView","resumeview");
        super.onResume();
        searchingLauout.setVisibility(View.GONE);
        Log.e("resumeView","resume");
    }

}
