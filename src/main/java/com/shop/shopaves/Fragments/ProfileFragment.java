package com.shop.shopaves.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.CollectionActivity;
import com.shop.shopaves.Activity.CollectionDetailActivity;
import com.shop.shopaves.Activity.EditProfileActivity;
import com.shop.shopaves.Activity.FollowersActivity;
import com.shop.shopaves.Activity.FollowingActivity;
import com.shop.shopaves.Activity.MessageActivity;
import com.shop.shopaves.Activity.MySetsActivity;
import com.shop.shopaves.Activity.ProductGridActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by amsyt005 on 23/12/16.
 */
public class ProfileFragment extends android.support.v4.app.Fragment implements Response.ErrorListener,Response.Listener<JSONObject>{

    private ProgressDialog pd;
    private AppStore aps;
    private TextView name,email,followingCount,followersCount,likeCount;
    private CircleImageView profileImg;
    private LinearLayout addImages;
    private LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(300,300);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        C.applyTypeface(C.getParentView(view.findViewById(R.id.profile)), C.getHelveticaNeueFontTypeface(getActivity()));
         addImages = (LinearLayout)view.findViewById(R.id.addview);
        name = (TextView)view.findViewById(R.id.name);
        email = (TextView)view.findViewById(R.id.email);
        followersCount = (TextView)view.findViewById(R.id.followerscount);
        followingCount = (TextView)view.findViewById(R.id.followingcount);
         likeCount= (TextView)view.findViewById(R.id.likecount);
        profileImg = (CircleImageView)view.findViewById(R.id.addphoto_imgvw);
        aps = new AppStore(getActivity());
        view.findViewById(R.id.profiletop).setVisibility(View.GONE);
        view.findViewById(R.id.followmsgview).setVisibility(View.GONE);
        view.findViewById(R.id.editprofile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(getActivity(),EditProfileActivity.class),200);
            }
        });

        view.findViewById(R.id.followersclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),FollowersActivity.class));
            }
        });

        view.findViewById(R.id.followingclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),FollowingActivity.class));
            }
        });

        view.findViewById(R.id.chatclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),MessageActivity.class));
               /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://s3-ap-southeast-1.amazonaws.com/shopaves/img_1482405924569-a439caaf-add6-4042-a0ad-2c074de32862.mp4"));
                intent.setDataAndType(Uri.parse("https://s3-ap-southeast-1.amazonaws.com/shopaves/img_1482405924569-a439caaf-add6-4042-a0ad-2c074de32862.mp4"), "video/mp4");
                startActivity(intent);*/
            }
        });

        view.findViewById(R.id.mysets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),MySetsActivity.class));
            }
        });
        view.findViewById(R.id.owncollection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CollectionActivity.class));
            }
        });

        view.findViewById(R.id.itemsyou).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProductGridActivity.class).putExtra(Constants.TYPE, Constants.SHOW_PRODUCT));
            }
        });

        view.findViewById(R.id.top_follower).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),FollowersActivity.class));
            }
        });
        view.findViewById(R.id.top_following).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),FollowingActivity.class));
            }
        });
        makeProfileRequest();
        return  view;
    }

    private void addCollectionImages(String imageUrl, final int id){
        parms.setMargins(10, 0, 0, 0);
        ImageView img = new ImageView(getActivity());
        img.setLayoutParams(parms);
        img.setTag(id);
        PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(imageUrl)).into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CollectionDetailActivity.class).putExtra("collectionId",id));
            }
        });
        addImages.addView(img);
    }

    private void makeProfileRequest(){
        pd =  C.getProgressDialog(getActivity());
        Map<String,String> map = new HashMap<>();
        map.put("myid", aps.getData(Constants.USER_ID));
        map.put("userId", aps.getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GET_PROFILE_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(getActivity(),""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model data = new Model(model.getData());
            Model profile = new Model(data.getProfile());
            Model collectionArray[] = data.getCollectionArray();
            name.setText(profile.getName());
            email.setText(profile.getEmailId());
            followingCount.setText(""+profile.getFollowings());
            followersCount.setText(""+profile.getFollowers());
            likeCount.setText(""+profile.getLikes());
            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(profile.getImageUrl())).placeholder(R.drawable.female).into(profileImg);
            for(Model collection : collectionArray){
                addCollectionImages(collection.getImage(),collection.getId());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 200){
            makeProfileRequest();
        }
    }
}
