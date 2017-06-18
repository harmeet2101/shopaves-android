package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
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

public class ProfileActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>,View.OnClickListener{

    private ProgressDialog pd;
    private AppStore aps;
    private TextView name,email,followText,followingCount,followersCount,likeCount;
    private CircleImageView profileImg;
    private String userId;
    private int type = 0;
    private LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(300,300);
    private LinearLayout addImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        C.applyTypeface(C.getParentView(findViewById(R.id.profile)), C.getHelveticaNeueFontTypeface(ProfileActivity.this));
        addImages = (LinearLayout)findViewById(R.id.addview);
        TextView title = (TextView)findViewById(R.id.title);
        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        profileImg = (CircleImageView)findViewById(R.id.addphoto_imgvw);
        followText = (TextView)findViewById(R.id.followtext);
        followersCount = (TextView)findViewById(R.id.followerscount);
        followingCount = (TextView)findViewById(R.id.followingcount);
        likeCount= (TextView)findViewById(R.id.likecount);
        findViewById(R.id.followingclick).setVisibility(View.GONE);
        findViewById(R.id.mysets).setVisibility(View.GONE);
        title.setText(getResources().getString(R.string.profile));
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(150,150);
        parms.setMargins(10, 0, 0, 0);
        aps = new AppStore(this);

        if(getIntent().getStringExtra(Constants.USER_ID) != null)
            userId = getIntent().getStringExtra(Constants.USER_ID);

        findViewById(R.id.editprofile).setVisibility(View.GONE);
        findViewById(R.id.fl).setVisibility(View.GONE);


        findViewById(R.id.followersclick).setOnClickListener(this);
        findViewById(R.id.followingclick).setOnClickListener(this);
        findViewById(R.id.chatclick).setOnClickListener(this);
        findViewById(R.id.mysets).setOnClickListener(this);

        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.followuser).setOnClickListener(this);
        findViewById(R.id.itemsyou).setOnClickListener(this);
        findViewById(R.id.owncollection).setOnClickListener(this);
        makeProfileRequest(userId);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.top_follower:
            case R.id.followersclick:
                startActivity(new Intent(ProfileActivity.this,FollowersActivity.class));
                break;
            case R.id.followingclick:
            case R.id.top_following:
                startActivity(new Intent(ProfileActivity.this,FollowingActivity.class));
                break;
            case R.id.chatclick:
                startActivity(new Intent(ProfileActivity.this,ProfileChatActivity.class).putExtra(Constants.USER_ID,Integer.parseInt(userId)).putExtra(Constants.NAME,name.getText().toString()));
                break;
            case R.id.mysets:
                startActivity(new Intent(ProfileActivity.this,MySetsActivity.class));
                break;
            case R.id.followuser:
                makeFollowUserRequest();
                break;
            case R.id.back_addrss:
                finish();
                break;
            case R.id.top_like:
                break;
            case R.id.itemsyou:
                startActivity(new Intent(ProfileActivity.this, ProductGridActivity.class).putExtra(Constants.TYPE, Constants.SHOW_PRODUCT).putExtra(Constants.USER_ID,userId));
                break;
            case R.id.owncollection:
                startActivity(new Intent(ProfileActivity.this, CollectionActivity.class).putExtra(Constants.USER_ID,userId));
                break;
        }
    }
    private void addCollectionImages(String imageUrl, final int id){
        parms.setMargins(10, 0, 0, 0);
        ImageView img = new ImageView(ProfileActivity.this);
        img.setLayoutParams(parms);
        img.setTag(id);
        PicassoCache.getPicassoInstance(ProfileActivity.this).load(C.getImageUrl(imageUrl)).resize(200,200).into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, CollectionDetailActivity.class).putExtra("collectionId",id));
            }
        });
        addImages.addView(img);
    }

    private void makeFollowUserRequest(){
        type = 1;
        pd =  C.getProgressDialog(ProfileActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("myId", aps.getData(Constants.USER_ID));
        Net.makeRequestParams(C.APP_URL+ ApiName.FOLLOW_API,map,this,this);
    }
    private void makeProfileRequest(String userId){
        pd =  C.getProgressDialog(ProfileActivity.this);
        Map<String,String> map = new HashMap<>();
            map.put("myid", aps.getData(Constants.USER_ID));
            map.put("userId", userId);
            Net.makeRequest(C.APP_URL+ ApiName.GET_PROFILE_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(ProfileActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
            if (type == 1) {
                Toast.makeText(ProfileActivity.this,model.getMessage(),Toast.LENGTH_LONG).show();
                if(followText.getText().toString().equals("UNFOLLOW")){
                    followText.setText("FOLLOW");
                }else
                    followText.setText("UNFOLLOW");
            } else {
                Model data = new Model(model.getData());
                Model profile = new Model(data.getProfile());
                Model collectionArray[] = data.getCollectionArray();
                name.setText(profile.getName());
                email.setText(profile.getEmailId());
                followingCount.setText(""+profile.getFollowings());
                followersCount.setText(""+profile.getFollowers());
                likeCount.setText(""+profile.getLikes());
                if(profile.isFollow())
                    followText.setText("UNFOLLOW");
                    PicassoCache.getPicassoInstance(ProfileActivity.this).load(C.getImageUrl(profile.getImageUrl())).placeholder(R.drawable.female).into(profileImg);

                for(Model collection : collectionArray){
                    addCollectionImages(collection.getImage(),collection.getId());
                }
            }
        }

    }


}
