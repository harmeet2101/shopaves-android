package com.shop.shopaves.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.AddToGroupActivity;
import com.shop.shopaves.Activity.CollectionDetailActivity;
import com.shop.shopaves.Activity.CommentsActivity;
import com.shop.shopaves.Activity.CreateCollection;
import com.shop.shopaves.Activity.LoginActivity;
import com.shop.shopaves.Activity.ProfileActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.EndlessRecyclerViewScrollListener;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class Collection extends Fragment implements Response.ErrorListener,Response.Listener<JSONObject> {
    private LinearLayout collectionCatContainr;
    private RecyclerView recyclerView;
    private ArrayList<JSONITEM> lists = new ArrayList<>();
    private FloatingActionButton fab;
    private ProgressDialog pd;
    private AppStore aps;
    private  CollectionAdapter adapter;
    private int adapterPositionView = 0;
    private boolean firstLoad = true;
    private boolean isCategoryLoaded = false;
    private TextView noCollection;
    private EditText search;
    private boolean isSearching = false;
    private String collectionCategoryId ="";
    private int listScrollPosition = 0;
    private boolean isPagination = false;
    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;;
    private String searchKey = "";

    public Collection(EditText search) {
        this.search = search;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        C.applyTypeface(C.getParentView(view.findViewById(R.id.collectn)), C.getHelveticaNeueFontTypeface(getActivity()));
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        collectionCatContainr= (LinearLayout) view.findViewById(R.id.collection_category);
        noCollection = (TextView)view.findViewById(R.id.nocollection);
        recyclerView = (RecyclerView) view.findViewById(R.id.collection_recyclevw);
        aps = new AppStore(getActivity());
        C.SEARCH_TYPE = Constants.COLLECTION;
        makePublicCollectionRequest("");
        search.setText("");
       /* recyclerView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                Log.e("totalbrandscount",""+totalItemsCount);
                listScrollPosition = totalItemsCount-3;
                if(totalItemsCount > 40){}
                    //make(""+totalItemsCount,"");                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });*/
       /* LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                listScrollPosition = totalItemsCount-3;
                Log.e("totalCount",""+totalItemsCount);
                isPagination = true;
                makePublicCollectionRequest(""+totalItemsCount,"");
            }
        };
        recyclerView.addOnScrollListener(scrollListener);*/
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (!isPagination)
                    {
                      //  if(totalItemCount > 40)
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            isPagination = true;
                            Log.e("totalItemCount",""+totalItemCount);
                            Log.v("...", "Last Item Wow !");
                            makePublicCollectionRequest(""+totalItemCount);
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });
        if(C.SEARCH_TYPE.equals(Constants.COLLECTION)){
            search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        searchKey = search.getText().toString();
                            makePublicCollectionRequest("");
                        Log.i("enterPressed","Enter pressed");
                        C.hideSoftKeyboard(getActivity());
                        return  true;
                    }
                    return false;
                }
            });
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),CreateCollection.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void setCollectionAdapter(){
         adapter = new CollectionAdapter(lists);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
       // LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }
    private void makePublicCollectionRequest(String skip){
        pd = C.getProgressDialog(getActivity());
       /* if(!TextUtils.isEmpty(searchKey)){
            try {
                searchKey = URLEncoder.encode(searchKey, "utf-8");
                Log.e("skip",searchKey);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }*/
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
            map.put("myId", aps.getData(Constants.USER_ID));
            map.put("myCollection", "0");
            map.put("key", C.getEncodedString(searchKey));
            map.put("skip", skip);
            map.put("categoryId", collectionCategoryId);
        //myCollection=false&userId=683&categoryId = 102&key=&skip=
        Net.makeRequest(C.APP_URL+ ApiName.COLLECTION_LIST_API,map,collection_response,this);
    }

    public Response.Listener<JSONObject> collection_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals("400") || model.getStatus().equals(Constants.SUCCESS_CODE)){

                if(!isPagination)
                    lists.clear();
                aps.setData(Constants.PUBLIC_COLLECTION_DATA,jsonObject.toString());
                Model[] models = model.getDataArray();
                if(models.length > 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    noCollection.setVisibility(View.GONE);
                    for(Model collection:models){
                        lists.add(new JSONITEM(collection));
                    }

                    if(!isPagination)
                    setCollectionAdapter();
                    adapter.notifyDataSetChanged();
                }else if(!isPagination && models.length<1){
                    noCollection.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                if(isPagination){
                  /*  visibleItemCount = 0;
                    pastVisiblesItems = 0;
                    totalItemCount = 0;*/
                    isPagination = false;
                }
                if(!isCategoryLoaded)
                makeCollectionCategoryRequest();
            }
        }
    };

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
    private void collectionCategory(String categoryName,String categoryId) {
            final View colletionCategoryItem = getActivity().getLayoutInflater().inflate(R.layout.collection_categry_item, null);
            final TextView textView = (TextView) colletionCategoryItem.findViewById(R.id.colection_item);
            final LinearLayout lv = (LinearLayout) colletionCategoryItem.findViewById(R.id.lv);
            textView.setText(categoryName);
            textView.setTag(categoryId);
            lv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isCategoryLoaded = true;
                    int index = collectionCatContainr.indexOfChild(colletionCategoryItem);
                    for(int i = collectionCatContainr.getChildCount()-1; i >= 0; i--){
                       View vLine =  collectionCatContainr.getChildAt(i);
                         TextView textView = (TextView) vLine.findViewById(R.id.colection_item);
                         LinearLayout lv = (LinearLayout) vLine.findViewById(R.id.lv);
                        if(i == index){
                            lv.setBackgroundResource(R.drawable.orange_bttn);
                            textView.setTextColor(getResources().getColor(R.color.white));
                            if(textView.getText().toString().equals("All Fashion")){
                                collectionCategoryId = "";
                                makePublicCollectionRequest("");
                            }else{
                                collectionCategoryId = ""+textView.getTag();
                                makePublicCollectionRequest("");
                            }
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

    private void makeCollectionCategoryRequest(){
            pd = C.getProgressDialog(getActivity());
            Map<String, String> map = new HashMap<>();
            Net.makeRequest(C.APP_URL + ApiName.COLLECTION_CATEGORY_API, map, this, this);
    }

/*
    private void makeCollectionByCategoryRequeat(String categoryId){

        pd = C.getProgressDialog(getActivity());
        Map<String, String> map = new HashMap<>();
        map.put("categoryId",categoryId);
        if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
        map.put("userId",aps.getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL + ApiName.GET_COLLECTION_BY_CATEGORY_API, map, collection_by_category_response, this);
    }*/

    private void makeLikeRequest(String id,String status){
//        pd = C.getProgressDialog(getActivity());
        Map<String, String> map = new HashMap<>();
        map.put("collectionId",id);
        map.put("status",status);
        map.put("userId",aps.getData(Constants.USER_ID));
        Net.makeRequestParams(C.APP_URL + ApiName.LIKE_COLLECTION_API, map, like_response, this);
    }

    private class CollectionAdapter extends RecyclerView.Adapter<MyViewHolder>{
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
                final JSONITEM items = source.get(position);
            final Model model = items.model;
                holder.collectionName.setText(model.getName());
                holder.textView2.setText(model.getUserName());
              //  holder.textView3.setText("1 hr ago");
            //holder.textView3.setText(DateUtils.getRelativeTimeSpanString(C.getDateInMillis(model.getTimeStampSmall()), Calendar.getInstance().getTimeInMillis()-19800000, DateUtils.MINUTE_IN_MILLIS));
        /*2017-02-28T13:16:45.435Z*/
          holder.textView3.setText(C.parseDate(C.getDateInMillis(model.getTimeStampSmall())));
            // holder.textView3.setText(C.parseDate(String.valueOf(C.getDateInMillis(model.getTimeStampSmall()))));
            holder.textView5.setText(""+model.getCommentCount());

               // Picasso.with(getActivity()).load(C.ASSET_URL + model.getImage()).error(R.drawable.collectiondefault).placeholder(R.drawable.collectiondefault).into(holder.imageView);
            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(model.getImage())).into(holder.imageView);
            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(model.getUserProfileImage())).placeholder(R.drawable.female).into(holder.userImg);

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

                        startActivityForResult(new Intent(getActivity(), CollectionDetailActivity.class).putExtra(Constants.COLLECTION_ID,model.getId()),100);
                    }
                });
                holder.addcomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                            startActivityForResult(new Intent(getActivity(), CommentsActivity.class).putExtra("collectionId", "" + model.getId()),111);
                        }else
                            C.setLoginMessage(getActivity());
                    }
                });

                holder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firstLoad = false;
                        if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                            if((holder.likeIcon.getTag())!=null){
                                if (holder.likeIcon.getTag().equals("1")) {
                                    makeLikeRequest("" + model.getId(), "2");
                                    adapterPositionView = position;
                                } else {
                                    makeLikeRequest("" + model.getId(), "1");
                                    adapterPositionView = position;
                                }
                            }
                        } else{
                            C.setLoginMessage(getActivity());
                        }
                    }

                });

            holder.addTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), AddToGroupActivity.class).putExtra(Constants.ID,""+model.getId()).putExtra(Constants.IS_PRODUCT,false));
                }
            });

            holder.userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                        if (!("" + model.getUserId()).equals(aps.getData(Constants.USER_ID)))
                            startActivity(new Intent(getActivity(), ProfileActivity.class).putExtra(Constants.USER_ID, "" + model.getUserId()));
                    }else
                        startActivity(new Intent(getActivity(), LoginActivity.class));

                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
     //   super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == 100|| requestCode ==111) {
             firstLoad = true;
             makePublicCollectionRequest("");

         }
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,addTo;
        TextView collectionName,textView2,textView3,textView4,textView5;
        LinearLayout addcomment,likes;
        ImageView likeIcon;
        CircleImageView userImg;
        RelativeLayout userProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.col_im);
            addTo = (ImageView) itemView.findViewById(R.id.addto);
            userImg = (CircleImageView) itemView.findViewById(R.id.usr);
            collectionName = (TextView) itemView.findViewById(R.id.usr_tag);
            textView2 = (TextView) itemView.findViewById(R.id.user_nme);
            textView3 = (TextView) itemView.findViewById(R.id.tag_tm);
            textView4 = (TextView) itemView.findViewById(R.id.likes);
            textView5 = (TextView) itemView.findViewById(R.id.commnts);
            addcomment = (LinearLayout)itemView.findViewById(R.id.comment);
            likeIcon = (ImageView)itemView.findViewById(R.id.likeicon);
            likes = (LinearLayout)itemView.findViewById(R.id.like);
            userProfile = (RelativeLayout)itemView.findViewById(R.id.usrprofile);

            itemView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap productBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    if(productBitmap != null){
                        C.shareContentExp(getActivity(), collectionName.getText().toString(), C.getImageUri(getActivity(), productBitmap));
                    }
                }
            });
        }
    }

    private class JSONITEM{
        private Model model;
        public JSONITEM(Model model) {
            this.model = model;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        try {
            Toast.makeText(getActivity(),""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            for(int i = 0; i<dataArray.length; i++){
                Model category = dataArray[i];
                collectionCategory(category.getName(),""+category.getId());
            }
        }
    }

    public Response.Listener<JSONObject> like_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE))
               // makePublicCollectionRequest();
            if(model.getCurrentLikeStatus() == 1)
            getViewByPosition(adapterPositionView,true,model.getData().replace(" likes",""));
            else{
                getViewByPosition(adapterPositionView,false,model.getData().replace(" likes",""));
            }
        }
    };


    public Response.Listener<JSONObject> collection_by_category_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                lists.clear();
                Model[] models = model.getDataArray();
                if(models.length > 0) {
                    noCollection.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for (Model collection : models) {
                        lists.add(new JSONITEM(collection));
                    }
                    setCollectionAdapter();
                }else{
                    noCollection.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                    //Toast.makeText(getActivity(),"No collection for this category",Toast.LENGTH_LONG).show();
            }
        }
    };

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
}
