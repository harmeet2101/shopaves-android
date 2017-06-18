package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Dialog.WriteReviewDialog;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllReviewActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{

    private ListView reviewList;
    private ReviewAdapter reviewAdapter;
    private String productid;
    private ProgressDialog pd;
    private String type = Constants.PRODUCT_REVIEW_DETAILS;
    private LinearLayout allReview,positiveReview,negativeReview,mostRecentReview;
    private TextView allReviewText,positiveReviewText,negativeReviewText,mostRecentReviewText;
    private TextView countReview;
    private RatingBar allRating;
    private TextView setRate,oneReviewText,twoReviewText,threeReviewText,fourReviewText,fiveReviewText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_review);
        reviewList = (ListView)findViewById(R.id.reviewlist);
        allReview = (LinearLayout)findViewById(R.id.forall);
        positiveReview = (LinearLayout)findViewById(R.id.forpositive);
        negativeReview = (LinearLayout)findViewById(R.id.fornegative);
        mostRecentReview = (LinearLayout)findViewById(R.id.formostrecent);
        allReviewText = (TextView)findViewById(R.id.foralltext);
        positiveReviewText = (TextView)findViewById(R.id.forpositivetext);
        negativeReviewText = (TextView)findViewById(R.id.fornegativetext);
        mostRecentReviewText = (TextView)findViewById(R.id.formostrecenttext);
        oneReviewText = (TextView)findViewById(R.id.oneratecount);
        twoReviewText = (TextView)findViewById(R.id.tworatecount);
        threeReviewText = (TextView)findViewById(R.id.threeratecount);
        fourReviewText = (TextView)findViewById(R.id.fourratecount);
        fiveReviewText = (TextView)findViewById(R.id.fiveratecount);
        countReview = (TextView)findViewById(R.id.reviewcount);
        setRate = (TextView)findViewById(R.id.ratef);
        allRating = (RatingBar)findViewById(R.id.allrating);
        reviewAdapter = new ReviewAdapter(this, R.layout.custom_review_list);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.allreview));
        if(!TextUtils.isEmpty(getIntent().getStringExtra(Constants.PRODUCT_ID)))
            productid = getIntent().getStringExtra(Constants.PRODUCT_ID);
        findViewById(R.id.writereview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WriteReviewDialog(AllReviewActivity.this,false,productid,"",5,"",null).show();
            }
        });

        setDefaultView();
        allReview.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_bttn));
        allReviewText.setTextColor(Color.WHITE);

        allReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = Constants.PRODUCTS_REVIEW;
                setDefaultView();
                allReview.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_bttn));
                allReviewText.setTextColor(Color.WHITE);
                makeReviewRequest("0");
            }
        });
        positiveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = Constants.PRODUCTS_REVIEW;
                setDefaultView();
                positiveReview.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_bttn));
                positiveReviewText.setTextColor(Color.WHITE);
                type = Constants.PRODUCTS_REVIEW;
                makeReviewRequest("1");

            }
        });
        negativeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = Constants.PRODUCTS_REVIEW;
                setDefaultView();
                negativeReview.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_bttn));
                negativeReviewText.setTextColor(Color.WHITE);
                makeReviewRequest("2");
            }
        });
        mostRecentReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = Constants.PRODUCTS_REVIEW;
                setDefaultView();
                mostRecentReview.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_bttn));
                mostRecentReviewText.setTextColor(Color.WHITE);
                makeReviewRequest("3");
            }
        });


        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        makeReviewRequest("");
    }


    private void setDefaultView(){
        allReview.setBackgroundDrawable(getResources().getDrawable(R.drawable.bttn_default));
        negativeReview.setBackgroundDrawable(getResources().getDrawable(R.drawable.bttn_default));
        positiveReview.setBackgroundDrawable(getResources().getDrawable(R.drawable.bttn_default));
        mostRecentReview.setBackgroundDrawable(getResources().getDrawable(R.drawable.bttn_default));
        allReviewText.setTextColor(getResources().getColor(R.color.fade_black));
        negativeReviewText.setTextColor(getResources().getColor(R.color.fade_black));
        positiveReviewText.setTextColor(getResources().getColor(R.color.fade_black));
        mostRecentReviewText.setTextColor(getResources().getColor(R.color.fade_black));
    }

    private void makeReviewRequest(String filter){
        pd =  C.getProgressDialog(AllReviewActivity.this);
        Map<String,String> map = new HashMap<>();
            map.put("productId", productid);
        if(type.equals(Constants.PRODUCTS_REVIEW)) {
            map.put("filter", filter);
        }
        Net.makeRequest(C.APP_URL+ (type.equals(Constants.PRODUCT_REVIEW_DETAILS) ? ApiName.GET_REVIEW_DETAIL_API : ApiName.GET_PRODUCT_REVIEW_API ),map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(AllReviewActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject);
        if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
            if (type.equals(Constants.PRODUCT_REVIEW_DETAILS)) {

                Model data = new Model(model.getData());
                countReview.setText(""+data.getReviewCount());
                allRating.setRating(data.getAverageRate());
                setRate.setText(""+data.getAverageRate()+"/5");
                String startingArray = data.getString("staring");
                try {
                    JSONArray rateArray = new JSONArray(startingArray);
                    for(int i = 0; i<rateArray.length(); i++){
                        fiveReviewText.setText(rateArray.get(4).toString());
                        fourReviewText.setText(rateArray.get(3).toString());
                        threeReviewText.setText(rateArray.get(2).toString());
                        twoReviewText.setText(rateArray.get(1).toString());
                        oneReviewText.setText(rateArray.get(0).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                type = Constants.PRODUCTS_REVIEW;
                makeReviewRequest("0");
            }else {
                Model dataArray[] = model.getDataArray();
                if(dataArray.length>0) {
                    findViewById(R.id.noreview).setVisibility(View.GONE);
                    reviewList.setVisibility(View.VISIBLE);
                    reviewAdapter.clear();
                    for (Model ratingInfo : dataArray) {
                        reviewAdapter.add(new StoreItem(ratingInfo.getUserImage(), ratingInfo.getComment(), ratingInfo.getRatingValue(), ratingInfo.getUserName()));
                    }
                    reviewList.setAdapter(reviewAdapter);
                    reviewAdapter.notifyDataSetChanged();
                }else{
                    findViewById(R.id.noreview).setVisibility(View.VISIBLE);
                    reviewList.setVisibility(View.GONE);
                }
            }
        }
    }

    private class ReviewAdapter extends ArrayAdapter<StoreItem> {
        MyHolder holder ;
        public ReviewAdapter(Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                holder =null;
                LayoutInflater inflater = (AllReviewActivity.this).getLayoutInflater();
                row = inflater.inflate(R.layout.custom_review_list, parent, false);
                holder = new MyHolder();
                row.setTag(holder);
            }
            else {
                holder = (MyHolder) row.getTag();
            }

            holder.userName = (TextView) row.findViewById(R.id.username);
            holder.userRate = (RatingBar) row.findViewById(R.id.userrate);
            holder.userComment = (TextView) row.findViewById(R.id.usercomment);
            holder.userImage = (CircleImageView) row.findViewById(R.id.usr);
            final StoreItem storeItem = getItem(position);
            PicassoCache.getPicassoInstance(AllReviewActivity.this).load(C.ASSET_URL+storeItem.storeImage).placeholder(R.drawable.male).into(holder.userImage);
            holder.userName.setText(storeItem.userName);
            holder.userRate.setRating(storeItem.rating);
            return row;
        }
    }

    private static class MyHolder{
        CircleImageView userImage;
        TextView userComment,userName;
        RatingBar userRate;
    }

    private class StoreItem{
        String storeImage;
        String comment,userName;
        int  rating;

        public StoreItem(String storeImage, String comment, int rating, String userName) {
            this.storeImage = storeImage;
            this.comment = comment;
            this.rating = rating;
            this.userName = userName;
        }
    }


}
