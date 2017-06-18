package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.shop.shopaves.Util.NamePair;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BrandDetailActivity extends AppCompatActivity implements Response.ErrorListener{

    private LinearLayout addBrandCategory,newarrivalsAdd,addmostPopular;
    private ProgressDialog pd;
    private AppStore aps;
    private String brandId;
    private TextView followersCount,followbrandtext,shopItemText;
    private  ImageView likeProductIcon,brandBanner;
    private CircleImageView brandLogo;
    private String type = Constants.BRAND;
    private boolean firstLoad = false;
    private RelativeLayout allitemsView;
    private ImageView followIcon;
    private LinearLayout noProductFound;
    private int brandSelectedPosition = 0;
    private String brandCategories = "";
    private LinearLayout followBrands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail);
        addBrandCategory = (LinearLayout)findViewById(R.id.brandcat);
        newarrivalsAdd = (LinearLayout)findViewById(R.id.new_arrivals);
        addmostPopular = (LinearLayout)findViewById(R.id.mostpopular);
        followersCount = (TextView)findViewById(R.id.followerscount);
        final TextView title = (TextView)findViewById(R.id.title);
        followbrandtext = (TextView)findViewById(R.id.followbrandtext);
        shopItemText = (TextView)findViewById(R.id.shopitemtext);
        brandLogo = (CircleImageView)findViewById(R.id.brand_logo);
        brandBanner = (ImageView)findViewById(R.id.brandbanner);
        allitemsView = (RelativeLayout)findViewById(R.id.allitemsview);
        followIcon = (ImageView)findViewById(R.id.followicon);
        noProductFound = (LinearLayout)findViewById(R.id.noproduct);
       followBrands = (LinearLayout)findViewById(R.id.followbrands);
        C.applyTypeface(C.getParentView(findViewById(R.id.branddetailview)), C.getHelveticaNeueFontTypeface(this));
        title.setText(getIntent().getExtras().getString(Constants.BRAND_NAME).toUpperCase());
        brandId = getIntent().getExtras().getString(Constants.BRAND_ID);
        PicassoCache.getPicassoInstance(BrandDetailActivity.this).load(C.getImageUrl(getIntent().getExtras().getString(Constants.BRAND_BANNER))).placeholder(R.drawable.defaultbannerbg).into(brandBanner);
        PicassoCache.getPicassoInstance(BrandDetailActivity.this).load(C.getImageUrl(getIntent().getExtras().getString(Constants.BRAND_LOGO))).placeholder(R.drawable.defaultholder).into(brandLogo);
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        followBrands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aps.getData(Constants.USER_ID)!=null)
                makeFollowBrandRequest(brandId);
                else
                    C.setLoginMessage(BrandDetailActivity.this);
            }
        });

        findViewById(R.id.sharebrand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(brandLogo.getDrawable() != null && (BitmapDrawable)brandLogo.getDrawable() != null){
                Bitmap brandBitmap = ((BitmapDrawable)brandLogo.getDrawable()).getBitmap();
                C.shareContentExp(BrandDetailActivity.this, title.getText().toString(), C.getImageUri(BrandDetailActivity.this, brandBitmap));
            }else{
                    Bitmap brandBitmap = ((BitmapDrawable)brandBanner.getDrawable()).getBitmap();
                    C.shareContentExp(BrandDetailActivity.this, title.getText().toString(), C.getImageUri(BrandDetailActivity.this, brandBitmap));
                }
            }
        });

        findViewById(R.id.createproduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BrandDetailActivity.this,AddProductActivity.class));
            }
        });


        allitemsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BrandDetailActivity.this,ProductByCategoryActivity.class).putExtra(Constants.SUB_CATEGORY_ID,Integer.parseInt(""+allitemsView.getTag())).putExtra(Constants.POSITION,brandSelectedPosition).putExtra(Constants.SUBCATEGORIES,brandCategories).putExtra(Constants.TYPE,Constants.BRAND_CATEGORY_PRODUCT));
            }
        });
        aps = new AppStore(this);
//        makeBrandRequest("");
        getBrandDetais();
    }

    private void makeBrandRequest(String categoryId){
        type = Constants.BRAND;
        pd =  C.getProgressDialog(BrandDetailActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("bid", brandId);
        map.put("userId", aps.getData(Constants.USER_ID));
        if(!TextUtils.isEmpty(categoryId)){
            type = Constants.BRANDS_CATEGORY;
            map.put("catId", categoryId);
        }
        Net.makeRequest(C.APP_URL+ ApiName.GET_BRANDS_API,map,brandDetails_response,this);
    }
    private void getBrandDetais(){
        type = Constants.BRAND;
        pd =  C.getProgressDialog(BrandDetailActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("bid", brandId);
        map.put("userId", aps.getData(Constants.USER_ID));
        firstLoad = false;
       /* map.put("userId", aps.getData(Constants.USER_ID));
        if(!TextUtils.isEmpty(categoryId)){
            type = Constants.BRANDS_CATEGORY;
            map.put("catId", categoryId);
        }*/
        Net.makeRequest(C.APP_URL+ ApiName.GET_BRANDS_DETAILS,map,brandDetails_response,this);
    }
    private void makeProductBySubCatRequest(String subCatId) {
        type = Constants.BRANDS_CATEGORY;
        Map<String, String> map = new HashMap<>();
        map.put("subCategoryId", subCatId);
        map.put("userId", aps.getData(Constants.USER_ID));
        map.put("forBrand", "true");
        map.put("bid", brandId);
        pd = C.getProgressDialog(BrandDetailActivity.this);
        Net.makeRequest(C.APP_URL + ApiName.GET_PRODUCTS_BY_SUBCATEGORY, map, brandDetails_response, this);
    }

    private void makeProductLikeRequest(String productId,String status){
        pd = C.getProgressDialog(BrandDetailActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("productId",productId);
        map.put("status",status);
        map.put("userId",aps.getData(Constants.USER_ID));
        Net.makeRequestParams(C.APP_URL + ApiName.LIKE_PRODUCT_API, map, like_response, this);
    }

    private void makeFollowBrandRequest(String brandId){
        type = Constants.FOLLOW_BRANDS;
        pd =  C.getProgressDialog(BrandDetailActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("brandId", brandId);
        // map.put("status",status);
        map.put("userId",aps.getData(Constants.USER_ID));
        /*id(brand id), status(1 for follow, 2 for unfollow)*/
        Net.makeRequestParams(C.APP_URL+ ApiName.FOLLOW_BRAND_API,map,followResponse,this);
    }
    private void brandsCategory(String categoryName,String categoryId) {

        final View brandCategoryItem = BrandDetailActivity.this.getLayoutInflater().inflate(R.layout.collection_categry_item, null);
        final TextView textView = (TextView) brandCategoryItem.findViewById(R.id.colection_item);
        final LinearLayout lv = (LinearLayout) brandCategoryItem.findViewById(R.id.lv);
        textView.setText(categoryName);
        textView.setTag(categoryId);

        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isFirstLoad = false;
                int index = addBrandCategory.indexOfChild(brandCategoryItem);
                for(int i = addBrandCategory.getChildCount()-1; i>=0; i--){
                    View vLine =  addBrandCategory.getChildAt(i);
                    TextView textView = (TextView) vLine.findViewById(R.id.colection_item);
                    LinearLayout lv = (LinearLayout) vLine.findViewById(R.id.lv);

                    if(i == index){
                        lv.setBackgroundResource(R.drawable.orange_bttn);
                        textView.setTextColor(getResources().getColor(R.color.white));
                        if(textView.getTag().equals("0")){
                            allitemsView.setTag("0");
                           // makeBrandRequest("0");
                            getBrandDetais();
                            brandSelectedPosition = 0;
                           // allitemsView.setVisibility(View.VISIBLE);
                        }
                        else {
                            brandSelectedPosition = i;
                            allitemsView.setTag(""+ textView.getTag());
                            //allitemsView.setVisibility(View.GONE);
                            makeBrandRequest("" + textView.getTag());
                        }
                    }else{
                        lv.setBackgroundResource(R.drawable.bttn_default);
                        textView.setTextColor(getResources().getColor(R.color.fade_black));
                    }
                }
            }
        });
        if(addBrandCategory.getChildCount() <= 0){
            lv.setBackgroundResource(R.drawable.orange_bttn);
            textView.setTextColor(getResources().getColor(R.color.white));
        }
        addBrandCategory.addView(brandCategoryItem);
    }

    private void addNewArrivalProducts(String id,String name,String imageName,Double price,Double salePrice,int status,int rating){
            View newArrival = this.getLayoutInflater().inflate(R.layout.single_product_hori, null);
            TextView productName = (TextView) newArrival.findViewById(R.id.product_name);
            TextView productPrice = (TextView) newArrival.findViewById(R.id.price);
            TextView productDiscount = (TextView) newArrival.findViewById(R.id.cross_price);
            TextView offDiscount = (TextView) newArrival.findViewById(R.id.offdiscount);
            final RelativeLayout likeClick = (RelativeLayout)newArrival.findViewById(R.id.clicklike);
            final ImageView like = (ImageView)newArrival.findViewById(R.id.wishlike);
            ImageView prdImageView = (ImageView)newArrival.findViewById(R.id.product_img);
            RatingBar rateProduct = (RatingBar)newArrival.findViewById(R.id.rate);
            productName.setText(name);
            productName.setTag(id);
            likeClick.setTag(id);
            PicassoCache.getPicassoInstance(BrandDetailActivity.this).load(C.getImageUrl(imageName)).into(prdImageView);
            like.setImageResource(status == 1 ? R.drawable.itemlike : R.drawable.itemunlike);
            like.setTag(status == 1 ? "1" : "2");
            productDiscount.setPaintFlags(productDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            productPrice.setText("$ "+salePrice);
            productDiscount.setText("$ "+price);
            if(price > 0)
            offDiscount.setText(""+C.formateValue(100 - (salePrice*100)/price)+"% off");
          rateProduct.setVisibility(rating > 0 ? View.VISIBLE : View.GONE);
            rateProduct.setRating(rating);

            newArrival.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView prd = (TextView) v.findViewById(R.id.product_name);
                    startActivity(new Intent(BrandDetailActivity.this,ProductDetailsActivity.class).putExtra("productId",""+prd.getTag()));
                }
            });

            likeClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeProductIcon = like;
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                        if (like.getTag().equals("1")) {
                            makeProductLikeRequest(""+likeClick.getTag(),"2");
                        } else {
                            makeProductLikeRequest("" +likeClick.getTag(), "1");
                        }
                    }else
                        startActivity(new Intent(BrandDetailActivity.this, LoginActivity.class));
                    //   C.setLoginMessage(getActivity());
                }
            });

            newarrivalsAdd.addView(newArrival);

    }private void addMostPopularProduct(String id,String name,String imageName,Double price,Double salePrice,int status,int rating){
            View mostPopularView = this.getLayoutInflater().inflate(R.layout.single_product_hori, null);
            TextView productName = (TextView) mostPopularView.findViewById(R.id.product_name);
            TextView productPrice = (TextView) mostPopularView.findViewById(R.id.price);
            TextView productDiscount = (TextView) mostPopularView.findViewById(R.id.cross_price);
            TextView offDiscount = (TextView) mostPopularView.findViewById(R.id.offdiscount);
            final RelativeLayout likeClick = (RelativeLayout)mostPopularView.findViewById(R.id.clicklike);
            final ImageView like = (ImageView)mostPopularView.findViewById(R.id.wishlike);
            ImageView prdImageView = (ImageView)mostPopularView.findViewById(R.id.product_img);
            RatingBar rateProduct = (RatingBar)mostPopularView.findViewById(R.id.rate);


            productName.setText(name);
            productName.setTag(id);
            likeClick.setTag(id);
            PicassoCache.getPicassoInstance(BrandDetailActivity.this).load(C.getImageUrl(imageName)).into(prdImageView);
            like.setImageResource(status == 1 ? R.drawable.itemlike : R.drawable.itemunlike);
            like.setTag(status == 1 ? "1" : "2");
            productDiscount.setPaintFlags(productDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            rateProduct.setRating(rating);
           rateProduct.setVisibility(rating > 0 ? View.VISIBLE : View.GONE);
            productPrice.setText("$"+salePrice);
            productDiscount.setText("$"+price);
            if(price > 0)
            offDiscount.setText(""+C.formateValue(100 - (salePrice*100)/price)+"% off");

            mostPopularView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView prd = (TextView) v.findViewById(R.id.product_name);
                    startActivity(new Intent(BrandDetailActivity.this,ProductDetailsActivity.class).putExtra("productId",""+prd.getTag()));
                }
            });

            likeClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeProductIcon = like;
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                        if (like.getTag().equals("1")) {
                            makeProductLikeRequest(""+likeClick.getTag(),"2");
                        } else {
                            makeProductLikeRequest("" +likeClick.getTag(), "1");
                        }
                    }else
                        startActivity(new Intent(BrandDetailActivity.this, LoginActivity.class));
                    //   C.setLoginMessage(getActivity());
                }
            });

            addmostPopular.addView(mostPopularView);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(BrandDetailActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onResponse(JSONObject jsonObject) {
//        pd.dismiss();
//        Log.e("brandDetailsResponse",jsonObject.toString());
//        Model model = new Model(jsonObject.toString());
//        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
//                Model data = new Model(model.getData());
//                Model newArrivalProducts[] = data.getNewArrivalArray();
//                Model mostPopularProducts[] = data.getMostPopularArray();
//                Model categoryArray[];
//                categoryArray = data.getCategoryDataArray();
//
//                newarrivalsAdd.removeAllViews();
//                if(newArrivalProducts != null && newArrivalProducts.length>0){
//                    for (Model arrivalProduct : newArrivalProducts) {
//                        addNewArrivalProducts("" + arrivalProduct.getId(), arrivalProduct.getItemName(), getProductImage(arrivalProduct), arrivalProduct.getPrice(), arrivalProduct.getDiscount(), arrivalProduct.getProductLikeStatus(), arrivalProduct.getAverageRating());
//                    }
//                }
//                addmostPopular.removeAllViews();
//                if(mostPopularProducts != null && mostPopularProducts.length>0)
//                    for (Model popularProduct : mostPopularProducts) {
//                    addMostPopularProduct("" + popularProduct.getId(), popularProduct.getItemName(), getProductImage(popularProduct), popularProduct.getPrice(), popularProduct.getDiscount(), popularProduct.getProductLikeStatus(), popularProduct.getAverageRating());
//                }
//            if(!type.equals(Constants.FOLLOW_BRANDS)){
//                findViewById(R.id.newarrivalview).setVisibility(newArrivalProducts != null ? View.VISIBLE : View.GONE);
//                findViewById(R.id.mostpopulatview).setVisibility(mostPopularProducts != null ? View.VISIBLE : View.GONE);
//            }
//
//               // addBrandCategory.removeAllViews();
//                if(type.equals(Constants.BRAND)) {
//                    if(!firstLoad) {
//                        firstLoad = true;
//                        brandsCategory("All", "" + 0);
//                        Model brands = new Model(data.getBrandInfo());
//                        followersCount.setText("" + brands.getFollowers());
//                        shopItemText.setText("Shop All" + " " + brands.getItems() + " Items");
//                        followbrandtext.setText(brands.getItemStatus() == 1 ? "UNFOLLOW" : "FOLLOW");
//                        followIcon.setImageResource(brands.getItemStatus() == 1 ? R.drawable.crossimg : R.drawable.plusblack);
//                        //followIcon.setTag(brands.getItemStatus() == 1 ? "1" : "2");
//                        if (categoryArray != null && categoryArray.length > 0)
//                            for (Model category : categoryArray) {
//                                brandsCategory(category.getName(), "" + category.getId());
//                            }
//                    }
//                }
//                else if(type.equals(Constants.FOLLOW_BRANDS)){
//                    if(followbrandtext.getText().toString().equals("FOLLOW")){
//                        followbrandtext.setText("UNFOLLOW");
//                    followIcon.setImageResource(R.drawable.crossimg);}
//                    else{
//                        followbrandtext.setText("FOLLOW");
//                        followIcon.setImageResource(R.drawable.plusblack);
//                    }
//                    Toast.makeText(BrandDetailActivity.this,model.getMessage(),Toast.LENGTH_SHORT).show();
//
//                    //   shopItemText.setText("Shop All" + " " + brands.getItems() + " Items");
//                }
//            } else{
//            }
//    }

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

    public Response.Listener<JSONObject> brandDetails_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
                pd.dismiss();
                Log.e("brandDetailsResponse",jsonObject.toString());
                Model model = new Model(jsonObject.toString());
                if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                    Model data = new Model(model.getData());
                    Model newArrivalProducts[] = data.getNewArrivalArray();
                    Model mostPopularProducts[] = data.getMostPopularArray();
                    Model categoryArray[];
                    categoryArray = data.getCategoryDataArray();
                    newarrivalsAdd.removeAllViews();
                    if(newArrivalProducts != null && newArrivalProducts.length>0){
                        for (Model arrivalProduct : newArrivalProducts) {
                            addNewArrivalProducts("" + arrivalProduct.getId(), arrivalProduct.getProductName(), arrivalProduct.getProductImage(), arrivalProduct.getItemPriceValue(), arrivalProduct.getItemDiscountValue(), arrivalProduct.getProductLikeStatus(), arrivalProduct.getAverageRating());
                        }
                    }
                    addmostPopular.removeAllViews();
                    if(mostPopularProducts != null && mostPopularProducts.length>0)
                        for (Model popularProduct : mostPopularProducts) {
                            addMostPopularProduct("" + popularProduct.getId(), popularProduct.getProductName(), popularProduct.getProductImage(), popularProduct.getItemPriceValue(), popularProduct.getItemDiscountValue(), popularProduct.getProductLikeStatus(), popularProduct.getAverageRating());
                        }

                        if(newArrivalProducts != null){
                            findViewById(R.id.newarrivalview).setVisibility(newArrivalProducts.length > 0 ? View.VISIBLE : View.GONE);
                            findViewById(R.id.newarrivalsproducts).setVisibility(mostPopularProducts.length > 0 ? View.VISIBLE : View.GONE);
                        }
                        if(mostPopularProducts != null){
                            findViewById(R.id.mostpopulatview).setVisibility(mostPopularProducts.length > 0 ? View.VISIBLE : View.GONE);
                            findViewById(R.id.mostpopularproducts).setVisibility(mostPopularProducts.length > 0 ? View.VISIBLE : View.GONE);
                        }

                    if(newArrivalProducts != null && mostPopularProducts != null){
                        noProductFound.setVisibility(newArrivalProducts.length > 0 || mostPopularProducts.length > 0 ? View.GONE : View.VISIBLE);
                        allitemsView.setVisibility(newArrivalProducts.length > 0 || mostPopularProducts.length > 0 ? View.VISIBLE : View.GONE);
                        shopItemText.setText("Shop All" + " " + data.getItems() + " Items");
                        Log.e("shopItems",""+data.getItems());
                    }

                    // addBrandCategory.removeAllViews();
                    if(type.equals(Constants.BRAND)) {
                        if(!firstLoad) {
                            firstLoad = true;
                            brandCategories = data.getString(NamePair.CATEGORY);
                            brandsCategory("All", "" + 0);
                            allitemsView.setTag("0");
                            Model brands = new Model(data.getBrandInfo());
                            followersCount.setText("" + brands.getFollowers());
                            shopItemText.setText("Shop All" + " " + brands.getItems() + " Items");
                           if(brands.getItemStatus() == 2){
                               followbrandtext.setText("FOLLOW");
                               followIcon.setImageResource(R.drawable.plusblack);
                               followBrands.setBackgroundResource(R.drawable.green_rect);
                           }else{
                               followbrandtext.setText("UNFOLLOW");
                               followIcon.setImageResource(R.drawable.unfollowicon);
                               followBrands.setBackgroundResource(R.drawable.publish_background_drawable);
                           }
                           // followbrandtext.setText(brands.getItemStatus() == 2 ? "UNFOLLOW" : "FOLLOW");
                          //  followIcon.setImageResource(brands.getItemStatus() == 2 ? R.drawable.unfollowicon : R.drawable.plusblack);
                            //followIcon.setTag(brands.getItemStatus() == 1 ? "1" : "2");
                            if (categoryArray != null && categoryArray.length > 0)
                                for (Model category : categoryArray) {
                                    brandsCategory(category.getName(), "" + category.getId());
                                }
                        }
                    }
                } else{
                }
            }            // makeCollectionDetailRequest();

    };

    Response.Listener<JSONObject> followResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                if(followbrandtext.getText().toString().equals(getResources().getString(R.string.follow))){
                    followbrandtext.setText(getResources().getString(R.string.unfollow));
                    followIcon.setImageResource(R.drawable.unfollowicon);
                    followBrands.setBackgroundResource(R.drawable.publish_background_drawable);
                }
                else{
                    followbrandtext.setText(getResources().getString(R.string.follow));
                    followIcon.setImageResource(R.drawable.plusblack);
                    followBrands.setBackgroundResource(R.drawable.green_rect);
                }
                Toast.makeText(BrandDetailActivity.this,model.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    };
}



