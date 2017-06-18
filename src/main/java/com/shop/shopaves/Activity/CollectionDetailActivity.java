package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.googlecode.mp4parser.authoring.Edit;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CollectionDetailActivity extends AppCompatActivity implements  Response.ErrorListener,Response.Listener<JSONObject>{
    private LinearLayout itemSetContainr;
    private LinearLayout similarContainr2;
    private ImageView backbttn;
    private TextView commentfirst,commentSecond,commentsCount,productcommentsCount,collectionName,userName,collectionDescription,collectionTime;
    private boolean isCommentView = false;
    private int collectionId;
    private ProgressDialog pd;
    private ImageView collectionImage;

    private ImageView likeIcon;
    private ImageView createCollection ;
    private TextView like_count;
    private AppStore aps;
    private TextView first_username,second_username;
    private String productDetails;
    private Intent collectionIntent;
    private boolean isCommentShow = true;
    private String likeType;
    private  ImageView likeProductIcon;
    private ImageView drop_desp,drop_cmnt;
    private TextView editcollection;
    private CircleImageView userImg;
    private int userID=0;
    private String userProfileImage;
    private String userImage;

    private String collectionProductsArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collectiondetails);
        itemSetContainr = (LinearLayout) findViewById(R.id.collec_horin);
        similarContainr2 = (LinearLayout) findViewById(R.id.collec_horin2);
        backbttn = (ImageView) findViewById(R.id.back_collectn);
        createCollection= (ImageView)findViewById(R.id.collec);
        collectionImage = (ImageView)findViewById(R.id.collection_img);
        collectionName = (TextView)findViewById(R.id.collection_name);
        userName = (TextView)findViewById(R.id.usr_nm);
        editcollection  = (TextView) findViewById(R.id.editproduct);
        collectionDescription = (TextView)findViewById(R.id.details);
        commentfirst = (TextView)findViewById(R.id.cmmnts);
        commentSecond = (TextView)findViewById(R.id.cmmn);
        commentsCount = (TextView)findViewById(R.id.commentscount);
        productcommentsCount = (TextView)findViewById(R.id.comment_count);
        like_count = (TextView)findViewById(R.id.likecount);
        likeIcon = (ImageView)findViewById(R.id.likeicon);
        first_username = (TextView)findViewById(R.id.user_name);
        second_username = (TextView)findViewById(R.id.nm22);
        drop_desp = (ImageView)findViewById(R.id.dropdesp);
        drop_cmnt = (ImageView)findViewById(R.id.dropcmnt);
        userImg = (CircleImageView)findViewById(R.id.usrr);
        collectionTime = (TextView)findViewById(R.id.time);

        C.applyTypeface(C.getParentView(findViewById(R.id.activity_collectiondetails)), C.getHelveticaNeueFontTypeface(CollectionDetailActivity.this));

        aps = new AppStore(this);
        collectionIntent = new Intent(CollectionDetailActivity.this,PublishProductActivity.class);


        Intent intent = getIntent();
        if(intent != null){
            collectionId = intent.getIntExtra(Constants.COLLECTION_ID,0);
        }
        findViewById(R.id.hide_description_colle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView textView= (TextView) findViewById(R.id.details);
                // textView.setVisibility(textView.getTag().toString().equals("1")? View.VISIBLE : View.GONE);

                if(textView.getTag().toString().equals("1")){
                    textView.setVisibility(View.VISIBLE);
                    drop_desp.setImageResource(R.drawable.drop_up);
                }else
                {
                    textView.setVisibility(View.GONE);
                    drop_desp.setImageResource(R.drawable.drop_down);
                }

                textView.setTag(textView.getTag().toString().equals("1")? 0 : 1);
            }
        });

        createCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CollectionDetailActivity.this,MySetsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.seeallproduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(CollectionDetailActivity.this,ItemInSet.class));
            }
        });

        findViewById(R.id.center_cmnt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(CollectionDetailActivity.this,CommentsActivity.class).putExtra(Constants.COLLECTION_ID,collectionId),200);
                Log.e("CollectionId",""+collectionId);

            }
        });

        editcollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editcollection.getText().toString().equals("DELETE")){
                    makeDeleteCollectionRequest(""+collectionId);
                }else {
                    collectionIntent.putExtra("ID", collectionId);
                    collectionIntent.putExtra("productDetail", productDetails);
                    startActivity(collectionIntent);


                }

            }
        });

        findViewById(R.id.allcomment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CollectionDetailActivity.this,CommentsActivity.class).putExtra(Constants.COLLECTION_ID,collectionId),200);
            }
        });

        findViewById(R.id.commentclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CollectionDetailActivity.this,CommentsActivity.class).putExtra(Constants.COLLECTION_ID,""+collectionId),100);
            }
        });
        findViewById(R.id.liveTryOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CollectionDetailActivity.this,LiveTryOut.class).putExtra("productDetails",productDetails));
            }
        });
        findViewById(R.id.commentdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!commentsCount.equals("0 Comments")){
                    if(isCommentShow) {
                        if (!isCommentView) {
                            findViewById(R.id.comment_layout).setVisibility(View.VISIBLE);
                            drop_cmnt.setImageResource(R.drawable.drop_up);
                            isCommentView = true;
                        } else {
                            findViewById(R.id.comment_layout).setVisibility(View.GONE);
                            drop_cmnt.setImageResource(R.drawable.drop_down);
                            isCommentView = false;
                        }
                    }
                }
            }
        });

        findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                    // int resID = getResources().getIdentifier(R.drawable.like , "drawable", getPackageName());
                    if (likeIcon.getTag().equals("1")) {
                        makeLikeRequest("" + collectionId, "2");
                    } else {
                        makeLikeRequest("" +collectionId, "1");
                    }

                }else
                    C.setLoginMessage(CollectionDetailActivity.this);
            }
        });

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap productBitmap = ((BitmapDrawable)collectionImage.getDrawable()).getBitmap();
                if(productBitmap != null){
                    C.shareContentExp(CollectionDetailActivity.this, collectionName.getText().toString(), C.getImageUri(CollectionDetailActivity.this, productBitmap));
                }

                C.shareContent(CollectionDetailActivity.this,"","");
            }
        });

        findViewById(R.id.usrr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(""+userID).equals(aps.getData(Constants.USER_ID)))
                    startActivityForResult(new Intent(CollectionDetailActivity.this, ProfileActivity.class).putExtra(Constants.USER_ID,userID),200);
            }
        });

        findViewById(R.id.seeallproduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("productArrayString",collectionProductsArray);
                startActivity(new Intent(CollectionDetailActivity.this,ProductGridActivity.class).putExtra(Constants.PRODUCT,collectionProductsArray));
            }
        });
        backbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        makeCollectionDetailRequest(""+collectionId);
    }

    private void makeCollectionDetailRequest(String collectionId){
        Map<String,String> map = new HashMap<>();
        map.put("collectionId",collectionId);
        if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
            map.put("userId",aps.getData(Constants.USER_ID));
        pd =  C.getProgressDialog(CollectionDetailActivity.this);
        Net.makeRequest(C.APP_URL+ ApiName.GET_COLLECTION_API,map,this,this);
    }

    private void makeDeleteCollectionRequest(String collectionId){
        Map<String,String> map = new HashMap<>();
        map.put("id",collectionId);
       /* if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
            map.put("userId",aps.getData(Constants.USER_ID));*/
        pd =  C.getProgressDialog(CollectionDetailActivity.this);
        Net.makeRequest(C.APP_URL+ ApiName.DELETE_COLLECTION,map,DeleteCollection,this);
    }

    private void makeSimilarCollectionRequest(){
        Map<String,String> map = new HashMap<>();
        map.put("collectionId",""+collectionId);
        pd =  C.getProgressDialog(CollectionDetailActivity.this);
        Net.makeRequest(C.APP_URL+ ApiName.GET_SIMILAR_COLLECTION_API,map,similar_collection_response,this);
    }
    private void makeProductLikeRequest(String productId,String status){
        likeType = "productLike";
        pd = C.getProgressDialog(CollectionDetailActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("productId",productId);
        map.put("status",status);
        map.put("userId",aps.getData(Constants.USER_ID));

        Net.makeRequestParams(C.APP_URL + ApiName.LIKE_PRODUCT_API, map, like_response, this);
    }
    private void makeLikeRequest(String id,String status){
        likeType = "collectionLike";
        pd = C.getProgressDialog(CollectionDetailActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("collectionId",id);
        map.put("status",status);
        map.put("userId",aps.getData(Constants.USER_ID));
        Net.makeRequestParams(C.APP_URL + ApiName.LIKE_COLLECTION_API, map, like_response, this);
    }

    private void setCommentInfo(Model collectionModel){
        Model data = new Model(collectionModel.getData());
        final Model commentArray[] = collectionModel.getCommentsArray();
        if(commentArray.length>0){
            isCommentShow = true;
            commentsCount.setText(""+data.getCommentCount()+" Comments");
            if(commentArray.length > 0){
                commentfirst.setText(C.getFirstLetterCaps(commentArray[0].getComment()));
                first_username.setText(C.getFirstLetterCaps(commentArray[0].getUserName()));
            }
            if(commentArray.length > 1){
                commentSecond.setText(commentArray[1].getComment());
                second_username.setText(commentArray[1].getUserName());
                CircleImageView usercommentImg = (CircleImageView) findViewById(R.id.usercommentImg);
                PicassoCache.getPicassoInstance(CollectionDetailActivity.this).load(C.getImageUrl(commentArray[1].getUserImage())).into(usercommentImg);
                usercommentImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!(""+commentArray[1].getUserId()).equals(aps.getData(Constants.USER_ID)))
                            startActivity(new Intent(CollectionDetailActivity.this, ProfileActivity.class).putExtra(Constants.USER_ID,""+commentArray[1].getUserId()));
                    }
                });
            } else{
                findViewById(R.id.secondcommentdetails).setVisibility(View.GONE);
            }
        }else{
            isCommentShow = false;
            commentsCount.setVisibility(View.GONE);
            findViewById(R.id.dropcmnt).setVisibility(View.GONE);
            findViewById(R.id.center_cmnt).setVisibility(View.VISIBLE);
        }

    }

    /*
    private void itemSet() {
        for (int i = 0; i < 10; i++) {
            View fashion_trends_view = getLayoutInflater().inflate(R.layout.single_product_hori, null);
            itemSetContainr.addView(fashion_trends_view);
        }
    }*/
/*
    private void similarCollection() {
        for (int i = 0; i < 10; i++) {
            View fashion_trends_view = getLayoutInflater().inflate(R.layout.single_similar_collection, null);
            similarContainr2.addView(fashion_trends_view);
        }
    }*/

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(CollectionDetailActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }
    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("Collection_Details",jsonObject.toString());
        Model model = new Model(jsonObject);
        Model data = new Model(model.getData());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            collectionId = data.getId();
            userProfileImage = data.getUserProfileImage();
            userImage = data.getImage() ;
            makeSimilarCollectionRequest();
            productDetails = jsonObject.toString();

            Picasso.with(CollectionDetailActivity.this).load(C.getImageUrl(userImage)).into(collectionImage);
            Picasso.with(CollectionDetailActivity.this).load(C.getImageUrl(userProfileImage)).into(userImg);

            collectionName.setText(C.getFirstLetterCaps(data.getName()));
            collectionTime.setText(C.getDateFormat(data.getTimeStampSmall()));
            userName.setText("By "+ C.getFirstLetterCaps(data.getUserName()));
            userID = data.getUserId();
            collectionIntent.putExtra("isPublish",data.getIsPublish());

            if(!TextUtils.isEmpty(data.getCollectionDescription())) {
                collectionDescription.setText(data.getCollectionDescription());
                collectionDescription.setVisibility(View.VISIBLE);
                drop_desp.setImageResource(R.drawable.drop_up);
                collectionDescription.setTag(collectionDescription.getTag().toString().equals("1")? 0 : 1);

            }
            like_count.setText(""+data.getLikes());
            productcommentsCount.setText(""+data.getCommentCount());

            if((""+data.getUserId()).equals(aps.getData(Constants.USER_ID))){
                editcollection.setVisibility(View.VISIBLE);
                if(data.getIsPublish()) {
                    // findViewById(R.id.editproduct).setVisibility(View.VISIBLE);
                    editcollection.setText("DELETE");
                }
            }else{
                editcollection.setVisibility(View.GONE);
            }
            if(data.getLikeStatus() == 1) {
                likeIcon.setImageResource(R.drawable.like);
                likeIcon.setTag("1");
            }
            else {
                likeIcon.setImageResource(R.drawable.unlike);
                likeIcon.setTag("2");
            }
            if(jsonObject.has("products")){
                Model productArray[] = model.getProductArray();
                collectionProductsArray = model.getString(NamePair.PRODUCTS);
                itemSetContainr.removeAllViews();
                for(int i = 0; i<productArray.length; i++){
                  /*  Model prodectD = productArray[i];
                    View fashion_trends_view = getLayoutInflater().inflate(R.layout.single_product_hori, null);
                    ImageView iv = (ImageView) fashion_trends_view.findViewById(R.id.product_img);
                    final TextView prdName = (TextView) fashion_trends_view.findViewById(R.id.product_name);
                    final TextView priceValue = (TextView) fashion_trends_view.findViewById(R.id.price);
                    final TextView discountOff = (TextView) fashion_trends_view.findViewById(R.id.offdiscount);
                    final TextView price_cross = (TextView) fashion_trends_view.findViewById(R.id.cross_price);
                    final ImageView likePrd = (ImageView)fashion_trends_view.findViewById(R.id.wishlike);
                    TextView rateCount = (TextView)fashion_trends_view.findViewById(R.id.ratecount);
                    RatingBar productRate = (RatingBar)fashion_trends_view.findViewById(R.id.rate);

                    prdName.setText(C.getFirstLetterCaps(prodectD.getProductName()));
                    prdName.setTag(""+prodectD.getProductId());
                    PicassoCache.getPicassoInstance(CollectionDetailActivity.this).load(C.ASSET_URL+prodectD.getEditProductImage()).resize(300,300).into(iv);

                    rateCount.setText("("+prodectD.getRateCount()+")");
                    productRate.setRating(prodectD.getAverageRating());
                    price_cross.setText("$"+prodectD.getPriceValue());
                    discountOff.setText(prodectD.getDiscount()+"% off");
                    priceValue.setText("$"+(prodectD.getPriceValue()*Float.parseFloat(prodectD.getDiscount())/100));

                    if(prodectD.getProductLikeStatus() == 1){
                        likePrd.setImageResource(R.drawable.itemlike);
                    }else{
                        likePrd.setImageResource(R.drawable.itemunlike);
                    }

                  //  Picasso.with(CollectionDetailActivity.this).load(C.ASSET_URL + data.getEditProductImage()).error(R.drawable.productsdefault).placeholder(R.drawable.collectiondefault).into(iv);
                   fashion_trends_view.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           startActivity(new Intent(CollectionDetailActivity.this,ProductDetailsActivity.class).putExtra("productId",""+prdName.getTag()));
                       }
                   });
                    fashion_trends_view.findViewById(R.id.clicklike).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            likeProductIcon = likePrd;
                            if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                                if (likePrd.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.like).getConstantState())) {
                                    makeProductLikeRequest(""+prdName.getTag(),"2");
                                } else {
                                    makeProductLikeRequest("" +prdName.getTag(), "1");
                                }
                            }else
                                C.setLoginMessage(CollectionDetailActivity.this);
                        }
                    });*/
                    //itemSetContainr.addView(fashion_trends_view);
                    itemSetContainr.addView(C.setProductView(CollectionDetailActivity.this,productArray[i]));
                }
            }
            setCommentInfo(model);
        }
    }

    public Response.Listener<JSONObject> like_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Log.e("LikeProductStatus",jsonObject.toString());
                if(likeType.equals("collectionLike")){
                    if(model.getCurrentLikeStatus() == 1){
                        likeIcon.setImageResource(R.drawable.like);
                        likeIcon.setTag("1");
                    }else{
                        likeIcon.setImageResource(R.drawable.unlike);
                        likeIcon.setTag("2");
                    }
                    like_count.setText(""+model.getData().replace(" likes",""));
                }else{
                    if(model.getCurrentLikeStatus() == 1){
                        likeProductIcon.setImageResource(R.drawable.itemlike);
                    }else{
                        likeProductIcon.setImageResource(R.drawable.itemunlike);
                    }
                }
            }
            // makeCollectionDetailRequest();
        }
    };


    public Response.Listener<JSONObject> DeleteCollection = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Toast.makeText(CollectionDetailActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
                setResult(100);
                finish();
            }

            // makeCollectionDetailRequest();
        }
    };

    public Response.Listener<JSONObject> similar_collection_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            similarContainr2.removeAllViews();
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Model dataArray[] = model.getDataArray();
                for(final Model values : dataArray){
                    View fashion_trends_view = getLayoutInflater().inflate(R.layout.single_similar_collection, null);
                    TextView prdName= (TextView)fashion_trends_view.findViewById(R.id.product_name);
                    final TextView prdBy= (TextView)fashion_trends_view.findViewById(R.id.productby);
                    ImageView prdImg = (ImageView)fashion_trends_view.findViewById(R.id.product_img);
                    CircleImageView user_collection_img = (CircleImageView)fashion_trends_view.findViewById(R.id.collectionuserimg);
                    prdBy.setTag(values.getId());
                    PicassoCache.getPicassoInstance(CollectionDetailActivity.this).load(C.getImageUrl(values.getImage())).into(prdImg);
                    PicassoCache.getPicassoInstance(CollectionDetailActivity.this).load(C.getImageUrl(values.getUserProfileImage())).into(user_collection_img);
                    prdBy.setText(C.getFirstLetterCaps(values.getUserName()));
                    prdName.setText(C.getFirstLetterCaps(values.getName()));
                    fashion_trends_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            startActivity(new Intent(CollectionDetailActivity.this,CollectionDetailActivity.class).putExtra(Constants.COLLECTION_ID,Integer.parseInt(prdBy.getTag().toString())));
                            //makeCollectionDetailRequest(""+prdBy.getTag());
                            //values.getId();
                        }
                    });

                    similarContainr2.addView(fashion_trends_view);
                }
            }
            // makeCollectionDetailRequest();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 200){
            makeCollectionDetailRequest(""+collectionId);
        }
    }


    /*
    * {"result":"OK","embells":[],"comments":[],"data":{"id":161609,"name":"Newwww","userid":149779,"userProfileImage":"https:\/\/s3-ap-southeast-1.amazonaws.com\/shopaves\/img_1494336941637-6b13ff2b-7202-4882-b041-8906694ee74d.png","userName":"Simmi dhingra","image":"https:\/\/s3-ap-southeast-1.amazonaws.com\/shopaves\/img_1496756637888-ae6e3b8d-4958-4320-84f7-4783e004d938.png","categoryId":104,"description":"xfv","likes":0,"comments":0,"likeStatus":2,"serverTime":0,"isPublish":false,"backBg":"","updateTime":1496756638487,"timestamp":"2017-06-06T12:39:57.045Z","publishTime":null,"updateTimestamp":null,"products":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]},"status":"200","products":[]}
    * */
}
