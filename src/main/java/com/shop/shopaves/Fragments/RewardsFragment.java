package com.shop.shopaves.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.RewardFAQ;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.apache.http.util.TextUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amsyt005 on 23/12/16.
 */
public class RewardsFragment extends android.support.v4.app.Fragment implements  Response.ErrorListener,Response.Listener<JSONObject>{

    private ProgressDialog pd;
    private AppStore aps;
    private ProgressBar levelProgress;
    private RatingBar ratingLevel;
    private TextView levelStars,nextLevel,startCount;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);
        levelProgress = (ProgressBar)view.findViewById(R.id.progressBar);
        ratingLevel = (RatingBar)view.findViewById(R.id.rate);
        levelStars = (TextView)view.findViewById(R.id.stars);
        nextLevel = (TextView)view.findViewById(R.id.nextlevel);
        startCount = (TextView)view.findViewById(R.id.earnstar);

        C.applyTypeface(C.getParentView(view.findViewById(R.id.profile)), C.getHelveticaNeueFontTypeface(getActivity()));
        aps = new AppStore(getActivity());

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RewardFAQ.class));
            }
        });
        view.findViewById(R.id.read_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        makeRewardRequest();
        return  view;
    }
        public void makeRewardRequest(){
            pd =  C.getProgressDialog(getActivity());
            Map<String,String> map = new HashMap<>();
            if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
                map.put("uid",aps.getData(Constants.USER_ID));
            Net.makeRequest(C.APP_URL+ ApiName.REWARD_API,map,this,this);
        }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(getActivity(),""+ VolleyErrors.setError(volleyError),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model data = new Model(model.getData());
            levelProgress.setProgress(Integer.parseInt(data.getLevel()));
            ratingLevel.setRating(Integer.parseInt(data.getLevel()));
            startCount.setText(""+Integer.parseInt(data.getLevel()));
          //  if(Integer.parseInt(data.getLevel()) == 0)
            switch (Integer.parseInt(data.getLevel())){
                case 0:
                    levelStars.setVisibility(View.GONE);
                    break;
                case 1:
                    levelStars.setText("1 Star! Poor!");
                    break;
                case 2:
                    levelStars.setText("2 Stars! Average!");
                    break;
                case 3:
                    levelStars.setText("3 Stars! Good!");
                    break;
                case 4:
                    levelStars.setText("4 Stars! Very Good!");
                    break;
                case 5:
                    levelStars.setText("5 Stars! Excellent!");
                    break;
            }
            nextLevel.setText(data.getNextLevel()+"% till next level");
        }

    }
}
