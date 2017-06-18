package com.shop.shopaves.Dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.LoginActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Interface.Callback;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amsyt005 on 7/12/16.
 */
public class WriteReviewDialog extends Dialog implements Response.ErrorListener,Response.Listener<JSONObject> {
    private Context context;
    private ProgressDialog pd;
    private AppStore aps;
    private EditText writeReview;
    private RatingBar ratingBar;
    private String productId;
    private boolean isDelete = false;
    private String reviewId = "";
    private String comment = "";
    private int ratingReview = 5;
    private Callback callback;
    private TextView performRating;
    public WriteReviewDialog(Context context, boolean isEdit, String productId, String reviewId, int ratingReview, String comment, Callback callback) {
        super(context, R.style.Theme_Dialog);
        this.context = context;
        this.isDelete = isEdit;
        this.reviewId = reviewId;
        this.comment = comment;
        this.productId = productId;
        this.ratingReview = ratingReview;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_dialog);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        setCanceledOnTouchOutside(true);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        aps = new AppStore(context);
        ratingBar = (RatingBar)findViewById(R.id.reviewrating);
        writeReview = (EditText)findViewById(R.id.writereview);
        performRating = (TextView) findViewById(R.id.rateperformance);

        ratingBar.setStepSize(0.5f);
        if(!TextUtils.isEmpty(comment))
        writeReview.setText(comment);

        ratingBar.setRating(ratingReview);
        ratingPerform(ratingReview);



        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingPerform(rating);
            }
        });
        findViewById(R.id.review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                    if(!isDelete)
                    makeWriteReviewRequest();
                    else
                        makeDeleteReviewRequest();                }
                else
                    context.startActivity(new Intent(context, LoginActivity.class));
            }
        });
    }

    private void ratingPerform(float rating){
        if(rating <= 1.5)
            performRating.setText("poor");
        else if(rating <=2.5)
            performRating.setText("Average");
        else if(rating <=3.5)
            performRating.setText("good");
        else
            performRating.setText("excellent");
    }
    private void makeWriteReviewRequest(){
        Map<String,String> map = new HashMap<>();
        map.put("productId",productId);
        map.put("rating",""+ratingBar.getRating());
        map.put("comment",writeReview.getText().toString());
        map.put("userId",aps.getData(Constants.USER_ID));
        pd =  C.getProgressDialog(context);
        Net.makeRequestParams(C.APP_URL+ ApiName.RATE_PRODUCT_API,map,this,this);
    }

    private void makeDeleteReviewRequest(){
        Map<String,String> map = new HashMap<>();
        map.put("rid",reviewId);
        map.put("userId",aps.getData(Constants.USER_ID));
        pd =  C.getProgressDialog(context);
        Net.makeRequestParams(C.APP_URL+ ApiName.DELETE_REVIEW_API,map,this,this);
    }
/*
 private void makeEditReviewRequest(){
        Map<String,String> map = new HashMap<>();
        map.put("rid",reviewId);
        map.put("comment",""+writeReview.getText().toString());
        map.put("rate",""+ratingBar.getRating());
        pd =  C.getProgressDialog(context);
        Net.makeRequestParams(C.APP_URL+ ApiName.EDIT_REVIEW_API,map,this,this);

    }
*/

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(context,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject);
        if(!isDelete)
            Toast.makeText(context,""+model.getMessage(),Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context,""+model.getData(),Toast.LENGTH_LONG).show();
        callback.CallBack(true);
            dismiss();
    }
}
