package com.shop.shopaves.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Dialog.AddToWishListDialog;
import com.shop.shopaves.Dialog.WriteReviewDialog;
import com.shop.shopaves.Interface.Callback;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.Util.TimeAgo;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailsActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject> {
    private LinearLayout viewedProduct, similarProduct, showSpecification, addProductImage,recentlyViewedProduct, bottomViewShow;
    private ViewPager pager;
    private ArrayList<ImageView> dots = new ArrayList<>();
    private ProgressDialog pd;
    private String type;
    private TextView itemName, userName, priceValue, details, likeCount, commentCount, discountPrice, priceOff,productTime;
    private String productDetails;
    private ArrayList<String> imageArrayList = new ArrayList<>();
    private ArrayList<String> videoArrayList = new ArrayList<>();
    private PagerAdapter adapter;
    private ImageView likeicon, despIcon;
    private AppStore aps;
    private ScrollView scrollView;
    private String productId;
    private ImageView likeProductIcon;
    private String type_like = "singleProducts";
    private LinearLayout showReview;
    private RelativeLayout showHideReview,viewAllReview;
    private boolean isReviewShow = true;
    private TextView reviewCount,productsReview,titleNameSpecification,valuesSpecification;
    private String userComment = "";
    private int userRating = 5;
    private int userReviewId = 0;
    private String productSingleImage="";
    private ImageView zoomProduct;
    private TextView writeReviewText;
    private TextView deleteReview;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    private int pagerPosition = 0;
    private boolean isReviewAvailable = false;
    private CircleImageView userImage;
    private boolean isScrollUp = true;
    private String productUserId = "";
    private HashMap<String,ArrayList<String>> specificationHashMap = new HashMap<>();
    private RelativeLayout offDiscountView;
    private String user_img;
    private LinearLayout rateView;
    private RelativeLayout specialOffer;
    private RatingBar productRate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        //linearLayout = (LinearLayout) findViewById(R.id.horizontl_product);
        pager = (ViewPager) findViewById(R.id.viewpager_product);
        viewedProduct = (LinearLayout) findViewById(R.id.customerviewed);
        similarProduct = (LinearLayout) findViewById(R.id.similarproducts);
        itemName = (TextView) findViewById(R.id.product_name);
        userName = (TextView) findViewById(R.id.usr_name);
        priceValue = (TextView) findViewById(R.id.price);
        commentCount = (TextView) findViewById(R.id.commentcount);
        details = (TextView) findViewById(R.id.seater_capacity_details);
        likeCount = (TextView) findViewById(R.id.likecount);
        discountPrice = (TextView) findViewById(R.id.cross_price);
        priceOff = (TextView) findViewById(R.id.offp);
        reviewCount = (TextView) findViewById(R.id.reviewcount);
        productsReview = (TextView) findViewById(R.id.reviewt);
        likeicon = (ImageView) findViewById(R.id.likeproduct);
        final ImageView dropreview = (ImageView) findViewById(R.id.drop);
        despIcon = (ImageView) findViewById(R.id.hide_description);
        addProductImage = (LinearLayout) findViewById(R.id.addproductviewimg);
        showSpecification = (LinearLayout) findViewById(R.id.showspecification);
        bottomViewShow = (LinearLayout) findViewById(R.id.cartviewshow);
        showReview = (LinearLayout)findViewById(R.id.addreview);
        showHideReview = (RelativeLayout)findViewById(R.id.showhidereview);
        viewAllReview = (RelativeLayout)findViewById(R.id.viewallreview);
        zoomProduct = (ImageView)findViewById(R.id.product_zoom);
        writeReviewText = (TextView)findViewById(R.id.writereview);
        deleteReview = (TextView)findViewById(R.id.deletereview);
        userImage = (CircleImageView) findViewById(R.id.usr);
        scrollView = (ScrollView) findViewById(R.id.scrollp);
        productTime = (TextView)findViewById(R.id.prdtime);
        rateView= (LinearLayout)findViewById(R.id.rateview);
        productRate = (RatingBar)findViewById(R.id.productrate);
        offDiscountView = (RelativeLayout)findViewById(R.id.offdiscountview);
        specialOffer = (RelativeLayout)findViewById(R.id.specialoffer);
        recentlyViewedProduct = (LinearLayout)findViewById(R.id.recentlyviewed);
        discountPrice.setPaintFlags(discountPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        aps = new AppStore(this);
        details.setTag("0");
        C.applyTypeface(C.getParentView(findViewById(R.id.activityproduct)), C.getHelveticaNeueFontTypeface(ProductDetailsActivity.this));

       /* PagerAdapter adapter = new ProductAdapter(ProductDetailsActivity.this);
        pager.setAdapter(adapter);*/
        if (!TextUtils.isEmpty(getIntent().getStringExtra("productId"))) {
            productId = getIntent().getStringExtra("productId");
        }
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                productSingleImage = imageArrayList.get(position);
                pagerPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        findViewById(R.id.hide_description).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView textView = (TextView) findViewById(R.id.seater_capacity_details);
                textView.setVisibility(textView.getTag().toString().equals("1") ? View.VISIBLE : View.GONE);
                textView.setTag(textView.getTag().toString().equals("1") ? 0 : 1);
            }
        });


        findViewById(R.id.likeclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_like = "singleProducts";
                if (!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                    // int resID = getResources().getIdentifier(R.drawable.like , "drawable", getPackageName());
                    if (likeicon.getTag().equals("1")) {
                        makeProductLikeRequest("" + productId, "2");
                    } else {
                        makeProductLikeRequest("" + productId, "1");
                    }
                } else
                    startActivity(new Intent(ProductDetailsActivity.this,LoginActivity.class));
                   // C.setLoginMessage(ProductDetailsActivity.this);
            }
        });

        findViewById(R.id.desp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // textView.setVisibility(textView.getTag().toString().equals("1")? View.VISIBLE : View.GONE);

                if (details.getTag().toString().equals("1")) {
                    details.setVisibility(View.VISIBLE);
                    despIcon.setImageResource(R.drawable.drop_down);
                } else {
                    details.setVisibility(View.GONE);
                    despIcon.setImageResource(R.drawable.drop_up);
                }
                details.setTag(details.getTag().toString().equals("1") ? 0 : 1);
            }
        });

        findViewById(R.id.commentprd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ProductDetailsActivity.this, CommentsActivity.class).putExtra(Constants.PRODUCT_ID, "" + productId), 100);
            }
        });

        findViewById(R.id.cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this,AddToCartActivity.class));
            }
        });

        findViewById(R.id.wishlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this,WishListActivity.class));
            }
        });


        findViewById(R.id.edit_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                    startActivityForResult(new Intent(ProductDetailsActivity.this, AddProductActivity.class).putExtra("PRODUCT_DETAILS", productDetails), 100);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "You have to login first for this operation", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                }
            }
        });

        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.reviewclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReviewAvailable)
                startActivity(new Intent(ProductDetailsActivity.this, AllReviewActivity.class).putExtra(Constants.PRODUCT_ID,productId));
            }
        });

        findViewById(R.id.addtolist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddToWishListDialog(ProductDetailsActivity.this, productId).show();
            }
        });

        findViewById(R.id.writereview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {

                    new WriteReviewDialog(ProductDetailsActivity.this,false,productId,"",5,"",reviewcallBack).show();
                }else startActivity(new Intent(ProductDetailsActivity.this,LoginActivity.class));
            }
        });

        findViewById(R.id.editproduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductDetailsActivity.this,AddProductActivity.class).putExtra(Constants.PRODUCT_ID,productId));
            }
        });
        showHideReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isReviewShow){
                    isReviewShow = true;
                    showReview.setVisibility(View.VISIBLE);
                    viewAllReview.setVisibility(View.VISIBLE);
                    dropreview.setImageResource(R.drawable.drop_up);
                }else{
                    isReviewShow = false;
                    showReview.setVisibility(View.GONE);
                    viewAllReview.setVisibility(View.GONE);
                    dropreview.setImageResource(R.drawable.drop_down);
                }
            }
        });

        findViewById(R.id.deletereview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                    if (!TextUtils.isEmpty("" + userReviewId)) {
                        new WriteReviewDialog(ProductDetailsActivity.this, true, productId, "" + userReviewId, userRating, userComment,reviewcallBack).show();
                    }else
                        Toast.makeText(ProductDetailsActivity.this,"No review for edit",Toast.LENGTH_LONG).show();
                }else
                    startActivity(new Intent(ProductDetailsActivity.this,LoginActivity.class));
            }
        });

        zoomProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(productSingleImage))
                startActivity(new Intent(ProductDetailsActivity.this,ZoomImageViewActivity.class).putExtra(Constants.PRODUCT_IMAGE,productSingleImage));
            }
        });

        findViewById(R.id.shareproduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmapArrayList.size() > 0) {
                    C.shareContentExp(ProductDetailsActivity.this, itemName.getText().toString(), getImageUri(ProductDetailsActivity.this, bitmapArrayList.get(pagerPosition)));
                }/*else{
                    C.shareContentExp(ProductDetailsActivity.this, itemName.getText().toString(), null);
                }*/
            }
        });

        findViewById(R.id.viewallreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReviewAvailable)
                startActivity(new Intent(ProductDetailsActivity.this, AllReviewActivity.class).putExtra(Constants.PRODUCT_ID,productId));
            }
        });

        findViewById(R.id.addgroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this,AddToGroupActivity.class).putExtra(Constants.ID,productId).putExtra(Constants.IS_PRODUCT,true));
            }
        });

        findViewById(R.id.otherprofile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this,ProfileActivity.class).putExtra(Constants.USER_ID,productUserId));
            }
        });

        findViewById(R.id.addtocart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // if (aps.getData(Constants.ADDRESS_ID)!=null && !TextUtils.isEmpty(aps.getData(Constants.USER_ID))){

                AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
                builder.setMessage("Items added to cart successfully");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        if (!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                            if (!TextUtils.isEmpty(productId)) {
                                JSONObject pr = new JSONObject();
                                try {
                                    pr.put("qty",1);
                                    pr.put("productId", productId);
                                    pr.put("userId",aps.getData(Constants.USER_ID));
                                    Log.e("Request_addcart",pr.toString());
                                    Net.makeRequest(C.APP_URL + ApiName.ADDCART, pr.toString(), r_fetch, e);
                                    pd = C.getProgressDialog(ProductDetailsActivity.this);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
//                        arrayList.put(new JSONObject(productMap));
                            }
//                    cartMap.put("addressId",aps.getData(Constants.ADDRESS_ID));
//                    cartMap.put("userId",aps.getData(Constants.USER_ID));
//                    cartMap.put("product",arrayList.toString());

                        }
                        else {
                            startActivity(new Intent(ProductDetailsActivity.this,AddToCartActivity.class)
                                    .putExtra(Constants.PRODUCT,itemName.getText().toString())
                                    .putExtra(Constants.PRICE,priceValue.getText().toString())
                                    .putExtra(Constants.NAME,userName.getText().toString())
                                    .putExtra(Constants.PRODUCT_BITMAP,imageArrayList.size()>0 ? imageArrayList.get(0) : "")
                                    .putExtra(Constants.PRODUCT_ID,productId)
                                    .putExtra(Constants.USER_IMG,user_img)
                                    .putExtra(Constants.QUANTITY,1)
                                    .putExtra(Constants.TYPE,"FromProduct")
                            );  }
                        dialog.dismiss();
                    }
                });
                AlertDialog alert11 = builder.create();
                alert11.show();
                }
        });

        makeProductDetailsRequest(productId);
    }

    Response.ErrorListener e = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(ProductDetailsActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    Response.Listener<JSONObject> r_fetch = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("addproductresponse",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Model data1 = new Model(model.getData());
                aps.setData(Constants.ADDRESS_ID,data1.getAddressID());
                //Toast.makeText(ProductDetailsActivity.this,"product added to cart successfully",Toast.LENGTH_LONG).show();
                startActivity(new Intent(ProductDetailsActivity.this,AddToCartActivity.class));
            }else{
                Toast.makeText(ProductDetailsActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void makeProductLikeRequest(String productId, String status) {
        pd = C.getProgressDialog(ProductDetailsActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("productId", productId);
        map.put("status", status);
        map.put("userId", aps.getData(Constants.USER_ID));

        Net.makeRequestParams(C.APP_URL + ApiName.LIKE_PRODUCT_API, map, like_response, this);
    }

    private void makeProductDetailsRequest(String productId) {
        type = "details";
        pd = C.getProgressDialog(ProductDetailsActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("productId", productId);
        if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
        map.put("userId",aps.getData(Constants.USER_ID));
        Net.makeRequestParams(C.APP_URL + ApiName.GET_PRODUCT_DETAILS_API, map, ProductDetailsActivity.this, ProductDetailsActivity.this);
    }

    private void setReview(Model reviewInfo[]){
        showReview.removeAllViews();
        if(reviewInfo.length > 0) {
            isReviewAvailable = true;
            for (int i = 0; i < reviewInfo.length; i++) {
                reviewCount.setVisibility(View.VISIBLE);
                Model model = reviewInfo[i];
                View review = ProductDetailsActivity.this.getLayoutInflater().inflate(R.layout.custom_review, null);
                CircleImageView userImg = (CircleImageView) review.findViewById(R.id.reviewer);
                TextView userName = (TextView) review.findViewById(R.id.username);
                TextView reviewText = (TextView) review.findViewById(R.id.reviewtext);
                RatingBar reviewRate = (RatingBar)review.findViewById(R.id.review);
                TextView time = (TextView) review.findViewById(R.id.time);
                time.setText(C.parseDate(C.getDateInMillis(model.getTimeStampSmall())));
                if (i < 3) {
                    PicassoCache.getPicassoInstance(ProductDetailsActivity.this).load(C.getImageUrl(model.getUserProfileImage())).placeholder(R.drawable.male).into(userImg);
                    userName.setText(model.getUserName());
                    reviewText.setText(model.getComment());
                    reviewRate.setRating(model.getRatingValue());
                    showReview.addView(review);
                }
                if (("" + model.getProductUserId()).equals(aps.getData(Constants.USER_ID))) {
                    writeReviewText.setVisibility(View.GONE);
                    userReviewId = model.getId();
                    userRating = model.getRatingValue();
                    userComment = model.getComment();
                    deleteReview.setVisibility(View.VISIBLE);
                }
                writeReviewText.setText(getResources().getString(R.string.writereview));
                productsReview.setText(reviewInfo.length == 1 ? "" + reviewInfo.length + " Review" : "" + reviewInfo.length + " Reviews");
                reviewCount.setText(reviewInfo.length == 1 ? "" + reviewInfo.length + " Review" : "" + reviewInfo.length + " Reviews");
            }

            if(deleteReview.getVisibility() == View.VISIBLE){
                writeReviewText.setVisibility(View.GONE);
            }
        }else{
            isReviewAvailable = false;
            reviewCount.setVisibility(View.GONE);
            writeReviewText.setVisibility(View.VISIBLE);
            writeReviewText.setText(getResources().getString(R.string.be_the_first_review_this_item));
            deleteReview.setVisibility(View.GONE);
            viewAllReview.setVisibility(View.GONE);
        }
    }

    private void initDots(int size) {
        LinearLayout lv = (LinearLayout) findViewById(R.id.pro_dots);
        lv.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView dotimg = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15, 15);
            layoutParams.leftMargin = 3;
            dotimg.setLayoutParams(layoutParams);
            lv.addView(dotimg);
        }

        dots.clear();
        for (int i = 0; i < lv.getChildCount(); i++) {
            ImageView img = (ImageView) lv.getChildAt(i);
            dots.add(img);
        }
        setIndicator(0);
    }

    private void setIndicator(int position) {
        for (int i = 0; i < dots.size(); i++) {
            ImageView img = dots.get(i);
            img.setImageResource(i == position ? R.drawable.indicator_yellow : R.drawable.grey_indicator);
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(ProductDetailsActivity.this, "" + VolleyErrors.setError(volleyError), Toast.LENGTH_LONG).show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject);
        if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
            if (type.equals("details")) {
                C.PRODUCT_INFO = jsonObject.toString();
                imageArrayList.clear();
                addProductImage.removeAllViews();
                if(isScrollUp)
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                productDetails = jsonObject.toString();
                Model data = new Model(model.getData());
                productUserId = ""+data.getProductUserId();
                findViewById(R.id.editproduct).setVisibility(aps.getData(Constants.USER_ID).equals(""+data.getProductUserId()) ? View.VISIBLE : View.GONE);
                itemName.setText(C.getTitleCase(data.getItemName()));
                if(data.getRateCount() > 0){
                    productRate.setRating(data.getAverageRating());
                }
                //findViewById(R.id.ratinglayout).setVisibility(data.getRateCount() > 0 ? View.VISIBLE : View.GONE);
                details.setText(data.getProductDescription());
                likeCount.setText("" + data.getLikeCount());
                commentCount.setText("" + data.getProductCommentCount());
                user_img = data.getUserImage();
                PicassoCache.getPicassoInstance(ProductDetailsActivity.this).load(C.getImageUrl(user_img)).into(userImage);
                productTime.setText(C.getDateFormat(data.getTimeStampSmall()));
                /*for review*/
                setReview(model.getReviewProductArray());
                /*for viewde*/
                viewedProduct.removeAllViews();
                Model viewedProductArray[] = model.getProductViewedArray();
                findViewById(R.id.cst).setVisibility(viewedProductArray.length > 0 ? View.VISIBLE : View.GONE);
                for (Model viewedProducts : viewedProductArray) {
                    viewedProduct.addView(C.setProductView(ProductDetailsActivity.this,viewedProducts));
                }
                /*for similar products*/
                similarProduct.removeAllViews();
                Model similarProductArray[] = model.getSimilarProductArray();
                findViewById(R.id.smtext).setVisibility(similarProductArray.length > 0 ? View.VISIBLE : View.GONE);
                for (Model similarProductModel : similarProductArray) {
                    similarProduct.addView(C.setProductView(ProductDetailsActivity.this,similarProductModel));
                }
                /*for recently viewed*/
                recentlyViewedProduct.removeAllViews();
                Model recentlyViewed[] = model.getRecentlyViewedProductArray();
                findViewById(R.id.rvtext).setVisibility(recentlyViewed.length > 0 ? View.VISIBLE : View.GONE);
                for(Model recentProduct : recentlyViewed){
                    recentlyViewedProduct.addView(C.setProductView(ProductDetailsActivity.this,recentProduct));
                }
                likeicon.setImageDrawable(null);
                if (data.getProductLikeStatus() == 1) {
                    likeicon.setTag("1");
                    likeicon.setImageResource(R.drawable.like);
                } else {
                    likeicon.setTag("2");
                    likeicon.setImageResource(R.drawable.unlike);
                }
                try {
                    JSONObject jsondata = jsonObject.getJSONObject("data");
                    JSONArray jsonimgarray = jsondata.getJSONArray("image");
                    if(!TextUtils.isEmpty(jsondata.getString("video"))) {
                        videoArrayList = new ArrayList<String>(Arrays.asList(jsondata.getString("video").split(" , ")));
                        Log.e("videoArray",jsondata.getString("video"));
                    }
                    userName.setText("by " + jsondata.getString("userName"));
                    JSONArray specificationArray = new JSONArray(jsondata.getString("specifications"));
                    addSpecification(specificationArray);
                    discountPrice.setText("$" + C.FormatterValue(Float.parseFloat(jsondata.getString("mrp"))));
                    priceValue.setText("$"+ C.FormatterValue(Float.parseFloat(jsondata.getString("salePrice"))));
                    if(Float.parseFloat(jsondata.getString("mrp"))>0)
                    priceOff.setText(""+ C.formateValue(100 - (Double.parseDouble(jsondata.getString("salePrice"))*100)/Double.parseDouble(jsondata.getString("mrp")))+"% off");
                    offDiscountView.setVisibility((100 - (Double.parseDouble(jsondata.getString("salePrice"))*100)/Double.parseDouble(jsondata.getString("mrp"))>0 ? View.VISIBLE : View.GONE));

                    if((100 - (Double.parseDouble(jsondata.getString("salePrice"))*100)/Double.parseDouble(jsondata.getString("mrp"))) > 50)
                        specialOffer.setVisibility(View.VISIBLE);
                    else
                        specialOffer.setVisibility(View.GONE);

                    if (jsonimgarray.length() > 0) {
                        for (int i = 0; i < jsonimgarray.length(); i++) {
                           if( i == 0)
                               productSingleImage = jsonimgarray.getString(0);
                            findViewById(R.id.prodectdefault).setVisibility(View.GONE);
                            imageArrayList.add(jsonimgarray.getString(i));
                            final View imgscroll = getLayoutInflater().inflate(R.layout.imagescroll, null);

                            imgscroll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = addProductImage.indexOfChild(imgscroll);
                                    pager.setCurrentItem(index);
                                }
                            });

                            ImageView imgs = (ImageView) imgscroll.findViewById(R.id.imgscr);

                            Log.e("imageArray", jsonimgarray.getString(0));
                            // ImageView img = new ImageView(this);
                            // img.setLayoutParams(parms);
                            PicassoCache.getPicassoInstance(ProductDetailsActivity.this).load(C.getImageUrl(imageArrayList.get(i))).into(imgs);
                            addProductImage.addView(imgscroll);
                        }
                       /* adapter = new ProductAdapter(ProductDetailsActivity.this, imageArrayList);
                        pager.setAdapter(adapter);*/
                    }


                    /*for video*/
                    if(videoArrayList.size() > 0){
                        findViewById(R.id.prodectdefault).setVisibility(View.GONE);
                        for(int i = 0; i<videoArrayList.size();i++){
                            imageArrayList.add(videoArrayList.get(i)+"video");

                            final View imgscroll = getLayoutInflater().inflate(R.layout.imagescroll, null);

                            imgscroll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = addProductImage.indexOfChild(imgscroll);
                                    pager.setCurrentItem(index);
                                }
                            });

                            ImageView imgs = (ImageView) imgscroll.findViewById(R.id.imgscr);
                         try {
                                Bitmap bitCreateVideo = retriveVideoFrameFromVideo(C.ASSET_URL + videoArrayList.get(i));

                                imgs.setImageBitmap(bitCreateVideo);
                                Log.e("convertVit",bitCreateVideo.toString());
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                          addProductImage.addView(imgscroll);
                        }
                    }
                    adapter = new ProductAdapter(ProductDetailsActivity.this, imageArrayList);
                    pager.setAdapter(adapter);
                    initDots(imageArrayList.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(ProductDetailsActivity.this, "" + model.getMessage(), Toast.LENGTH_LONG).show();
                setResult(100);
                finish();
            }
        }
    }

    public Response.Listener<JSONObject> like_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                Log.e("LikeProductStatus",jsonObject.toString());
                if (type_like.equals("singleProducts")) {
                    if (model.getCurrentLikeStatus() == 1) {
                        likeicon.setImageResource(R.drawable.like);
                        likeicon.setTag("1");
                    } else {
                        likeicon.setImageResource(R.drawable.unlike);
                        likeicon.setTag("2");
                    }
                    likeCount.setText("" + model.getData().replace(" likes", ""));
                }else{
                    setLikes(model);
                }
            }
            // makeCollectionDetailRequest();
        }
    };

    private void setLikes(Model model) {
        if(model.getCurrentLikeStatus()==1)
        {
            likeProductIcon.setImageResource(R.drawable.itemlike);
            likeProductIcon.setTag("1");
        }
        else
        {
            likeProductIcon.setImageResource(R.drawable.itemunlike);
            likeProductIcon.setTag("2");
        }
    }

    private void addSpecification(JSONArray specificationArray){
        ArrayList<String> valueSpecificationArray = new ArrayList<>();
        showSpecification.removeAllViews();
        for(int i = 0; i< specificationArray.length();i++){
            String specificationtextValue = "";
            View v = getLayoutInflater().inflate(R.layout.product_specification, null);
            titleNameSpecification = (TextView) v.findViewById(R.id.addtitle);
            v.findViewById(R.id.removeview).setVisibility(View.GONE);
            valuesSpecification =  (TextView) v.findViewById(R.id.addvalue);
            ImageView dropRight = (ImageView)v.findViewById(R.id.removeview);
            try {
                JSONObject specificationValue =   specificationArray.getJSONObject(i);
                titleNameSpecification.setText(specificationValue.getString("title"));
                //valuesSpecification.setText(specificationValue.getString("value"));
                Log.e("specE",specificationValue.getString("value"));
                JSONArray valueArray = new JSONArray(specificationValue.getString("value"));
                dropRight.setVisibility(valueArray.length() > 1 ? View.VISIBLE : View.GONE);
                for(int j = 0; j< valueArray.length();j++){
                    if(j == 0)
                    valuesSpecification.setText(""+valueArray.get(j));
                    valueSpecificationArray.add(""+valueArray.get(j));
                }

                v.setTag(""+valueArray.toString());
                specificationHashMap.put(specificationValue.getString("title"),valueSpecificationArray);
                valueSpecificationArray.clear();
                /*if(!TextUtils.isEmpty(specificationtextValue))
                valuesSpecification.setText(specificationtextValue.substring(1));*/
                if(valueArray.length()>1){
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            valuesSpecification =(TextView) v.findViewById(R.id.addvalue);
                            titleNameSpecification =(TextView) v.findViewById(R.id.addtitle);
                            Intent intent = new Intent(ProductDetailsActivity.this,SelectItemActivity.class);
                            // intent.putExtra("CONTENT_NAME","SPECIFICATION");
                            intent.putExtra("valueArray",""+v.getTag());
                            intent.putExtra("CONTENT_NAME","VALUES_ARRAY");
                            intent.putExtra("SPECIFICATION_SELECTION_TYPE","single");
                            //intent.putExtra("SELECTED_SPECIFICATIONS",specificationHashMap.get(titleNameSpecification.getText().toString()));

                            intent.putExtra("SELECTED_SPECIFICATIONS",valuesSpecification.getText().toString());
                            startActivityForResult(intent,20);
                        }
                    });
                }

                showSpecification.addView(v);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

private class ProductAdapter extends PagerAdapter {

        Context context;
        //int[] imageId = {R.drawable.image1, R.drawable.image1, R.drawable.image1, R.drawable.image1};
    ArrayList<String> imageUrls = new ArrayList<>();

        public ProductAdapter(Context context,ArrayList<String> imageUrls){
            this.context = context;

            this.imageUrls = imageUrls;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();

            final View viewItem = inflater.inflate(R.layout.product_image_layout, container, false);
            final ImageView imageView = (ImageView) viewItem.findViewById(R.id.imageView3);

            final View productMediaView = getLayoutInflater().inflate(R.layout.add_product_video, null);
            final VideoView prdVid = (VideoView) productMediaView.findViewById(R.id.pdimg);

            final ImageView videoIcon = (ImageView) productMediaView.findViewById(R.id.videoicon);
            final ImageView removeIcon = (ImageView) productMediaView.findViewById(R.id.removeimg);
            removeIcon.setVisibility(View.GONE);

            // imageView.setImageResource(imageId[position]);

            if(!imageUrls.get(position).substring(imageUrls.get(position).length()- 5,imageUrls.get(position).length()).equals("video")) {
                PicassoCache.getPicassoInstance(ProductDetailsActivity.this).load(C.getImageUrl(imageUrls.get(position))).into(target);
                PicassoCache.getPicassoInstance(ProductDetailsActivity.this).load(C.getImageUrl(imageUrls.get(position))).into(imageView);
                imageView.setTag("IMAGE");
            }else{
                try {

                    MediaController mediacontroller = new MediaController(
                            ProductDetailsActivity.this);

                    Uri uri = Uri.parse(C.ASSET_URL + imageUrls.get(position).substring(0,imageUrls.get(position).length()- 5));
                    prdVid.setVideoURI(uri);
                    prdVid.seekTo(100);
                    videoIcon.setImageResource(R.drawable.video1);
                    prdVid.setTag("VIDEO");
                    prdVid.setMediaController(mediacontroller);
                    productMediaView.setTag(C.ASSET_URL + imageUrls.get(position).substring(0,imageUrls.get(position).length()- 5));

//                    Bitmap bitCreateVideo = retriveVideoFrameFromVideo(C.ASSET_URL + imageUrls.get(position).substring(0,imageUrls.get(position).length()- 5));
//                    imageView.setImageBitmap(bitCreateVideo);
//                    imageView.setTag("VIDEO");
//                    Log.e("videoValueUri",""+C.ASSET_URL + imageUrls.get(position).substring(0,imageUrls.get(position).length()- 5));
//                    viewItem.setTag(C.ASSET_URL + imageUrls.get(position).substring(0,imageUrls.get(position).length()- 5));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(prdVid.getTag()!=null && prdVid.getTag().equals("VIDEO")){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(""+productMediaView.getTag()));
                       Log.e("videoValueUri",""+productMediaView.getTag());
                        intent.setDataAndType(Uri.parse(""+productMediaView.getTag()), "video/mp4");
                        startActivity(intent);
                    }else if(imageView.getTag().equals("IMAGE")){
                        if(!TextUtils.isEmpty(productSingleImage))
                            startActivity(new Intent(ProductDetailsActivity.this,ZoomImageViewActivity.class).putExtra(Constants.PRODUCT_IMAGE,productSingleImage));
                    }
                }
            });
            ((ViewPager)container).addView(viewItem);
            return viewItem;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            // TODO Auto-generated method stub

            return view == ((View)object);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeView((View) object);
        }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bitmapArrayList.add(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
    }
    Callback reviewcallBack = new Callback() {
        @Override
        public void CallBack(boolean isChange) {

            if(isChange){
                isScrollUp = false;
                viewAllReview.setVisibility(View.VISIBLE);
                deleteReview.setVisibility(View.GONE);
                writeReviewText.setVisibility(View.VISIBLE);
                makeProductDetailsRequest(productId);
            }
        }
    };

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("rescode",""+resultCode);
        if(resultCode == 100){
            makeProductDetailsRequest(data.getStringExtra("ID"));
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            dots = new ArrayList<>();
            imageArrayList= new ArrayList<>();
            showSpecification.removeAllViews();
            addProductImage.removeAllViews();
        }else if(resultCode == 200){
            makeProductDetailsRequest(productId);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            dots = new ArrayList<>();
            imageArrayList= new ArrayList<>();
            showSpecification.removeAllViews();
            addProductImage.removeAllViews();
        }else if(resultCode == 30){
            String specificationNames = "";
            //  valuesSpecification.setText(data.getStringExtra("NAME").substring(1));
            if(data != null)
            valuesSpecification.setText(data.getStringExtra("NAME"));

            /*specificationHashMap.put(titleNameSpecification.getText().toString(),data.getStringArrayListExtra("NAME"));

            if(data.getStringArrayListExtra("NAME").size()>0) {
                for (String name : data.getStringArrayListExtra("NAME")) {
                    specificationNames = specificationNames + "," + name;
                }
                valuesSpecification.setText(specificationNames.substring(1));
            }else{
                valuesSpecification.setText("");
            }*/
        }
    }
}
