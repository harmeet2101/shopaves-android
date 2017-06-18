package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CollectionActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{

    private LinearLayout collectionCatContainr;
    private RecyclerView recyclerView;
    private ArrayList<JSONITEM> lists = new ArrayList<>();
    private FloatingActionButton fab;
    private ProgressDialog pd;
    private AppStore aps;
    private  CollectionAdapter adapter;
    private int adapterPositionView = 0;
    private boolean firstLoad = true;
    private boolean isFirstLoad = true;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        collectionCatContainr = (LinearLayout) findViewById(R.id.categoryname);
        recyclerView = (RecyclerView) findViewById(R.id.collectionlist);
        fab = (FloatingActionButton)findViewById(R.id.addcollection);
        aps = new AppStore(this);
        C.applyTypeface(C.getParentView(findViewById(R.id.collectionactivity)), C.getHelveticaNeueFontTypeface(CollectionActivity.this));
        if(getIntent().getStringExtra(Constants.USER_ID)!=null)
            userId = getIntent().getStringExtra(Constants.USER_ID);
        makePublicCollectionRequest("");
        setCollectionAdapter();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CollectionActivity.this, CreateCollection.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.back_addrs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setCollectionAdapter(){
        adapter = new CollectionAdapter(lists);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(CollectionActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void makePublicCollectionRequest(String categoryId){
        pd = C.getProgressDialog(CollectionActivity.this);
        //       //categoryId=&key=&myCollection=1&myId=20795&skip=0&userId=20795

        Map<String, String> map = new HashMap<>();
        map.put("userId", TextUtils.isEmpty(userId) ? aps.getData(Constants.USER_ID) : userId);
        map.put("myId",aps.getData(Constants.USER_ID));
        map.put("myCollection","1");
        // if(!TextUtils.isEmpty(categoryId))
        map.put("categoryId", categoryId);
        Net.makeRequest(C.APP_URL+ ApiName.COLLECTION_LIST_API,map,collection_response,this);
    }

    private void makeLikeRequest(String id,String status){
        pd = C.getProgressDialog(CollectionActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("collectionId",id);
        map.put("status",status);
        map.put("userId",aps.getData(Constants.USER_ID));
        Net.makeRequestParams(C.APP_URL + ApiName.LIKE_COLLECTION_API, map, like_response, this);
    }

    public Response.Listener<JSONObject> like_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            Log.e("likeresponse",jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                if(model.getCurrentLikeStatus() == 1)
                    getViewByPosition(adapterPositionView,true,model.getData().replace(" likes",""));
                else{
                    getViewByPosition(adapterPositionView,false,model.getData().replace(" likes",""));

                }
            }
        }
    };

    public Response.Listener<JSONObject> collection_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                lists.clear();
                Model[] models = model.getDataArray();
                for(Model collection:models){
                    lists.add(new JSONITEM(collection));
                   /* CollectionPrivateDataModel data = new CollectionPrivateDataModel(""+collection.getId(),collection.getImage(),collection.getName(),collection.getName(),"1 hr ago","120","12");
                    data.save();*/
                }
                setCollectionAdapter();

                if(isFirstLoad)
                    makeCollectionCategoryRequest();
            }
        }
    };

    private void makeCollectionCategoryRequest(){
        pd = C.getProgressDialog(CollectionActivity.this);
        Map<String, String> map = new HashMap<>();
        Net.makeRequest(C.APP_URL + ApiName.COLLECTION_CATEGORY_API, map, this, this);
    }

    private void collectionCategory(String categoryName,String categoryId) {
        final View colletionCategoryItem = CollectionActivity.this.getLayoutInflater().inflate(R.layout.collection_categry_item, null);
        final TextView textView = (TextView) colletionCategoryItem.findViewById(R.id.colection_item);
        final LinearLayout lv = (LinearLayout) colletionCategoryItem.findViewById(R.id.lv);
        textView.setText(categoryName);
        textView.setTag(categoryId);
        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstLoad = false;
                int index = collectionCatContainr.indexOfChild(colletionCategoryItem);
                for(int i = collectionCatContainr.getChildCount()-1; i>=0; i--){
                    View vLine =  collectionCatContainr.getChildAt(i);
                    TextView textView = (TextView) vLine.findViewById(R.id.colection_item);
                    LinearLayout lv = (LinearLayout) vLine.findViewById(R.id.lv);
                    if(i == index){
                        lv.setBackgroundResource(R.drawable.orange_bttn);
                        textView.setTextColor(getResources().getColor(R.color.white));
                        if((textView.getText().toString().equals("All Fashion"))){
                            makePublicCollectionRequest("");
                        }else
                            makePublicCollectionRequest(""+textView.getTag());
                        // makeCollectionByCategoryRequeat(""+textView.getTag());
                    }else{
                        lv.setBackgroundResource(R.drawable.bttn_default);
                        textView.setTextColor(getResources().getColor(R.color.fade_black));
                    }
                }
            }
        });

        if(collectionCatContainr.getChildCount() <= 0){
            lv.setBackgroundResource(R.drawable.orange_bttn);
            textView.setTextColor(getResources().getColor(R.color.white));
        }

        collectionCatContainr.addView(colletionCategoryItem);
    }


    private class JSONITEM{
        private Model model;
        public JSONITEM(Model model) {
            this.model = model;
        }
    }

    private class CollectionAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<JSONITEM> source;

        public CollectionAdapter(List<JSONITEM> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_collection_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            //final Items items = source.get(position);

            final JSONITEM items = source.get(position);

            final Model model = items.model;
            holder.collectionName.setText(model.getName());
            holder.textView2.setText(model.getUserName());
            holder.textView3.setText(C.getDateFormat(model.getTimeStampSmall()));
            //holder.textView4.setText(""+model.getLikes());
            holder.textView5.setText(""+model.getCommentCount());
            PicassoCache.getPicassoInstance(CollectionActivity.this).load(C.getImageUrl(model.getImage())).into(holder.imageView);
            PicassoCache.getPicassoInstance(CollectionActivity.this).load(C.getImageUrl(model.getUserProfileImage())).placeholder(R.drawable.female).into(holder.userImage);
            PicassoCache.getPicassoInstance(CollectionActivity.this).load(C.getImageUrl(model.getImage())).into(target);

            //Picasso.with(CollectionActivity.this).load(C.ASSET_URL + items.productImage).error(R.drawable.collectiondefault).placeholder(R.drawable.collectiondefault).into(holder.imageView);

            if(firstLoad){
                if(String.valueOf(model.getLikeStatus()).equals("1")) {
                    holder.likeIcon.setImageResource(R.drawable.like);
                    holder.textView4.setText("" + model.getLikes());
                    holder.likeIcon.setTag("1");
                }
                else {
                    holder.likeIcon.setImageResource(R.drawable.unlike);
                    holder.textView4.setText("" + model.getLikes());
                    holder.likeIcon.setTag("2");
                }
            }
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(CollectionActivity.this, CollectionDetailActivity.class).putExtra(Constants.COLLECTION_ID,model.getId()));
                    //  startActivity(new Intent(CollectionActivity.this, PublishProductActivity.class).putExtra("ID",Integer.parseInt(items.collectionId)));
                }
            });

            holder.likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstLoad = false;

                    adapterPositionView = position;
                    if(holder.likeIcon.getTag().equals("1")){
                        makeLikeRequest(""+model.getId(),"2");
                    }else{
                        makeLikeRequest(""+model.getId(),"1");
                    }
                }
            });

            holder.addcomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CollectionActivity.this, CommentsActivity.class).putExtra("collectionId",""+model.getId()));
                }
            });
            holder.shareCollection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(""+model.getImage())) {
                        C.shareContentExp(CollectionActivity.this, model.getName(), C.getImageUri(CollectionActivity.this, bitmapArrayList.get(position)));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
        public int getItemViewType(int position) {
            return position;
        }

    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,shareCollection;
        TextView collectionName, textView2, textView3, textView4, textView5;
        ImageView likeIcon;
        LinearLayout addcomment,likes;
        CircleImageView userImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.col_im);
            shareCollection = (ImageView) itemView.findViewById(R.id.share);
            userImage = (CircleImageView) itemView.findViewById(R.id.usr);
            collectionName = (TextView) itemView.findViewById(R.id.usr_tag);
            textView2 = (TextView) itemView.findViewById(R.id.user_nme);
            textView3 = (TextView) itemView.findViewById(R.id.tag_tm);
            textView4 = (TextView) itemView.findViewById(R.id.likes);
            textView5 = (TextView) itemView.findViewById(R.id.commnts);
            addcomment = (LinearLayout)itemView.findViewById(R.id.comment);
            likeIcon = (ImageView)itemView.findViewById(R.id.likeicon);
            likes = (LinearLayout)itemView.findViewById(R.id.like);

        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(CollectionActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("collection data",jsonObject.toString());
        collectionCatContainr.removeAllViews();
        Model model = new Model(jsonObject);
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            aps.setData(Constants.PUBLIC_COLLECTION_CATEGORY,model.getJsonObject().toString());
            Model dataArray[] = model.getDataArray();
            collectionCategory("All Fashion","");
            for(int i =0; i<dataArray.length; i++){
                Model category = dataArray[i];
                collectionCategory(category.getName(),""+category.getId());
            }
            /*for(Model category : dataArray){
                collectionCategory(category.getName(),""+category.getId());
            }*/
        }
    }

    public void getViewByPosition(int pos, boolean isLike,String count) {
        View v = recyclerView.getLayoutManager().findViewByPosition(pos);
        ImageView likeI = (ImageView) v.findViewById(R.id.likeicon);
        TextView likesCount = (TextView)v.findViewById(R.id.likes);
        if(isLike) {
            likeI.setImageResource(R.drawable.like);
            likesCount.setText(count);
            likeI.setTag("1");
        }
        else{
            likeI.setImageResource(R.drawable.unlike);
            likesCount.setText(count);
            likeI.setTag("2");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            makePublicCollectionRequest("");
        }
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
