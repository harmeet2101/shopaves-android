package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

public class GroupDetailsActivity extends AppCompatActivity implements  Response.ErrorListener,Response.Listener<JSONObject>,View.OnClickListener{

    private ProgressDialog pd;
    private AppStore aps;
    private String groupId="";
    private TextView groupName,itemCount,likeCount,commentCount,groupDetails;
    private CircleImageView groupImage;
    private RecyclerView recyclerView;
    private boolean isShowDescription = true;
    private boolean isShowComment = false;
    private ImageView despIcon;
    private ImageView groupLikeIcon;
    private GroupItemListAdapter groupItemListAdapter;
    private ListView groupItemList;
    private ScrollView groupScrollView;
    private LinearLayout addComments;
    private ImageView dropCommentIcon;
    private ImageView referenceProductLike;
    private TextView referenceProductLikeCount;
    private String selectType;
    private int PRODUCT_LIKE_STATUS = -1;
    private int Collection_LIKE_STATUS = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        TextView title = (TextView)findViewById(R.id.title);
        groupName = (TextView)findViewById(R.id.groupname);
        itemCount = (TextView)findViewById(R.id.itemcount);
        groupImage = (CircleImageView)findViewById(R.id.groupimage);
        likeCount = (TextView)findViewById(R.id.likecount);
        commentCount = (TextView)findViewById(R.id.comment_count);
        groupDetails = (TextView)findViewById(R.id.details);
        recyclerView = (RecyclerView)findViewById(R.id.itemslist);
        despIcon = (ImageView)findViewById(R.id.dropdesp);
        groupLikeIcon = (ImageView)findViewById(R.id.grouplikeicon);
        groupItemList = (ListView)findViewById(R.id.groupitemlist);
        groupScrollView = (ScrollView)findViewById(R.id.groupscrollview);
        addComments = (LinearLayout)findViewById(R.id.addreview);
        dropCommentIcon = (ImageView)findViewById(R.id.dropcmnt);
        groupItemList.setFocusable(false);
        title.setText("SETS");
        aps = new AppStore(this);
        groupItemListAdapter = new GroupItemListAdapter(R.layout.single_product_layout, Constants.PRODUCT);
        if(getIntent().getStringExtra(Constants.GROUP_ID)!=null)
            groupId = getIntent().getStringExtra(Constants.GROUP_ID);

        groupScrollView.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                groupScrollView.fullScroll(View.FOCUS_UP);
            }
        });
        findViewById(R.id.hide_description_colle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(groupDetails.getText().toString())){
                    if(isShowDescription){
                        isShowDescription = false;
                        groupDetails.setVisibility(View.GONE);
                        despIcon.setImageResource(R.drawable.drop_down);
                    }else{
                        isShowDescription = true;
                        groupDetails.setVisibility(View.VISIBLE);
                        despIcon.setImageResource(R.drawable.drop_up);
                    }
                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        findViewById(R.id.like).setOnClickListener(this);
        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.commentclick).setOnClickListener(this);
        findViewById(R.id.allcomment).setOnClickListener(this);
        findViewById(R.id.commentdown).setOnClickListener(this);
        findViewById(R.id.editgroup).setOnClickListener(this);
        findViewById(R.id.groupshare).setOnClickListener(this);
        makeGroupDetailsRequest(groupId);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.like:
                selectType = Constants.GROUP_LIKE;
                makeGroupLikeRequest(groupId,groupLikeIcon.getTag().equals("1") ? "2" : "1");
                break;
            case R.id.back_addrss:
                finish();
                break;
            case R.id.commentclick:
                startActivityForResult(new Intent(GroupDetailsActivity.this,CommentsActivity.class).putExtra(Constants.GROUP_ID,groupId),200);
                break;
            case R.id.allcomment:
                startActivityForResult(new Intent(GroupDetailsActivity.this,CommentsActivity.class).putExtra(Constants.GROUP_ID,groupId),200);
                break;
            case R.id.groupshare:
                Bitmap productBitmap = ((BitmapDrawable)groupImage.getDrawable()).getBitmap();
                if(productBitmap != null){
                    C.shareContentExp(GroupDetailsActivity.this, groupName.getText().toString(), C.getImageUri(GroupDetailsActivity.this, productBitmap));
                }
                break;
            case R.id.commentdown:
               if(isShowComment){
                   dropCommentIcon.setImageResource(R.drawable.drop_down);
                   findViewById(R.id.allcomment).setVisibility(View.GONE);
                   addComments.setVisibility(View.GONE);
                   isShowComment = false;
               }else{
                   dropCommentIcon.setImageResource(R.drawable.drop_up);
                   findViewById(R.id.allcomment).setVisibility(View.VISIBLE);
                   addComments.setVisibility(View.VISIBLE);
                   isShowComment = true;
               }
                break;
            case R.id.editgroup:
                Intent editGroupIntent = new Intent(GroupDetailsActivity.this,CreateGroupActivity.class);
                Bundle groupBundle = new Bundle();
                groupBundle.putString(Constants.GROUP_ID,groupId);
                groupBundle.putString(Constants.GROUP_NAME,groupName.getText().toString());
                groupBundle.putString(Constants.GROUP_DESCRIPTION,groupDetails.getText().toString());
                editGroupIntent.putExtras(groupBundle);
                startActivityForResult(editGroupIntent,100);
                break;
        }
    }


    private class GroupItemListAdapter extends ArrayAdapter<Items> implements Constants {

        MyAdapterViewHolder holder ;
        int res;
        String type;
        private static final int ITEM_VIEW_TYPE_COUNT = 2;
        private static final int PRODUCT = 0;
        // View Type for Regular rows
        private static final int COLLECTION = 1;

        public GroupItemListAdapter(int res,String type) {
            super(GroupDetailsActivity.this, res);
            this.res = res;
            this.type = type;
        }

        @Override
        public int getViewTypeCount() {
            return ITEM_VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            boolean isSection = getItem(position).isProduct;
            if (isSection) {
                return PRODUCT;
            } else {
                return COLLECTION;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView =convertView;
            holder = null;
            if (itemView == null) {
             //   LayoutInflater inflater = (GroupDetailsActivity.this).getLayoutInflater();
              //  itemView = inflater.inflate(res, parent, false);

                int itemViewType = getItemViewType(position);
                LayoutInflater inflater = getLayoutInflater();
                switch (itemViewType){
                    case PRODUCT:
                        itemView = inflater.inflate(R.layout.single_product_layout, null);
                        holder = new MyAdapterViewHolder();
                        holder.itemImage = (ImageView) itemView.findViewById(R.id.product_img);
                        holder.product_name = (TextView) itemView.findViewById(R.id.product_name);
                        holder.mrpPrice = (TextView) itemView.findViewById(R.id.cross_price);
                        holder.salePrice = (TextView) itemView.findViewById(R.id.price);
                        holder.itemLikeCount = (TextView) itemView.findViewById(R.id.likecount);
                        holder.itemCommentCount = (TextView) itemView.findViewById(R.id.commentcount);
                        holder.offDiscount = (TextView) itemView.findViewById(R.id.offdiscount);
                        holder.ratingCount = (TextView) itemView.findViewById(R.id.ratingcount);
                        holder.rating = (RatingBar)itemView.findViewById(R.id.rate);
                        holder.productLike = (LinearLayout)itemView.findViewById(R.id.productlike);
                        holder.likeProductIcon = (ImageView)itemView.findViewById(R.id.wishlike);
                        holder.productComment = (LinearLayout)itemView.findViewById(R.id.productcomment);
                        holder.shareProduct = (ImageView)itemView.findViewById(R.id.productshare);
                        holder.addToGroup = (ImageView)itemView.findViewById(R.id.addto);
                        holder.productOffView = (LinearLayout)itemView.findViewById(R.id.off);
                        holder.rateProductView = (LinearLayout)itemView.findViewById(R.id.rateview);
                        break;
                    case COLLECTION:
                        itemView = inflater.inflate(R.layout.single_collection_row, null);
                        holder = new MyAdapterViewHolder();
                        holder.itemImage = (ImageView) itemView.findViewById(R.id.col_im);
                        holder.shareProduct = (ImageView) itemView.findViewById(R.id.share);
                        holder.userImg = (CircleImageView) itemView.findViewById(R.id.usr);
                        holder.product_name = (TextView) itemView.findViewById(R.id.usr_tag);
                        holder.userName = (TextView) itemView.findViewById(R.id.user_nme);
                        holder.time = (TextView) itemView.findViewById(R.id.tag_tm);
                        holder.itemLikeCount = (TextView) itemView.findViewById(R.id.likes);
                        holder.itemCommentCount = (TextView) itemView.findViewById(R.id.commnts);
                        holder.productComment = (LinearLayout)itemView.findViewById(R.id.comment);
                        holder.likeProductIcon = (ImageView)itemView.findViewById(R.id.likeicon);
                        holder.productLike = (LinearLayout)itemView.findViewById(R.id.like);
                        break;
                }

               // holder = new MyAdapterViewHolder();

                itemView.setTag(holder);
            }
            else {
                holder = (MyAdapterViewHolder) itemView.getTag();
            }

            int itemViewType = getItemViewType(position);
            switch (itemViewType) {
                case PRODUCT:
                    final Items items = getItem(position);
                    final Model model = items.model;
                    PicassoCache.getPicassoInstance(GroupDetailsActivity.this).load(C.getImageUrl(model.getProductImage())).into(holder.itemImage);
                    holder.product_name.setText(model.getProductName());
                    holder.salePrice.setText("$ "+C.formateValue(Double.parseDouble(model.getSalePrice())));
                    holder.mrpPrice.setText("$ "+C.formateValue(model.getItemPriceValue()));
                    holder.rating.setRating(model.getAverageRating());
                    holder.itemLikeCount.setText(""+model.getLikeCount());
                    holder.itemCommentCount.setText(""+model.getProductCommentCount());
                    holder.product_name.setTag(""+model.getProductId());

                    if(model.getPriceValue()>0){
                        if((100 - (Double.parseDouble(model.getSalePrice())*100)/model.getItemPriceValue()) > 0)
                        holder.offDiscount.setText(""+(100 - (Double.parseDouble(model.getSalePrice())*100)/model.getItemPriceValue())+"% off");
                        else holder.productOffView.setVisibility(View.GONE);
                    }
                    holder.mrpPrice.setPaintFlags(holder.mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.ratingCount.setText("("+model.getRatingCount()+")");

                    holder.rateProductView.setVisibility(model.getRatingCount() > 0 ? View.VISIBLE : View.GONE);
                    if(model.getItemStatus() == 1) {
                        holder.likeProductIcon.setImageResource(R.drawable.like);
                        holder.likeProductIcon.setTag(2);
                    }
                    else {
                        holder.likeProductIcon.setImageResource(R.drawable.unlike);
                        holder.likeProductIcon.setTag(1);
                    }
                    holder.productLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            referenceProductLike = (ImageView)v.findViewById(R.id.wishlike);
                            referenceProductLikeCount = (TextView) v.findViewById(R.id.likecount);
                            selectType = Constants.PRODUCT_LIKE;
                                    // makeProductLikeRequest(""+model.getProductId(),""+holder.likeProductIcon.getTag());

                            makeProductLikeRequest(""+model.getProductId(),""+referenceProductLike.getTag());
                        }
                    });

                    holder.productComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(GroupDetailsActivity.this,CommentsActivity.class).putExtra(Constants.PRODUCT_ID,""+model.getProductId()),200);
                        }
                    });

                    holder.shareProduct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View groupItemView = groupItemList.getAdapter().getView(position,null,groupItemList);
                            Bitmap productBitmap = ((BitmapDrawable)((ImageView) groupItemView.findViewById(R.id.product_img)).getDrawable()).getBitmap();
                            if(productBitmap != null){
                                C.shareContentExp(GroupDetailsActivity.this, ((TextView) groupItemView.findViewById(R.id.product_name)).getText().toString(), C.getImageUri(GroupDetailsActivity.this, productBitmap));
                            }
                        }
                    });

                    holder.addToGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(GroupDetailsActivity.this,AddToGroupActivity.class).putExtra(Constants.ID,""+holder.product_name.getTag()).putExtra(Constants.IS_PRODUCT,true));
                        }
                    });

                    holder.itemImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(GroupDetailsActivity.this,ProductDetailsActivity.class).putExtra("productId",""+model.getProductId()));
                        }
                    });


                    break;
                case COLLECTION:
                    final Items itemsCollection = getItem(position);
                    final Model modelCollection = itemsCollection.model;
                    holder.product_name.setText(modelCollection.getName());
                    holder.userName.setText(modelCollection.getUserName());
                    holder.time.setText(C.getDateFormat(modelCollection.getTimeStampSmall()));
                    holder.itemLikeCount.setText(""+modelCollection.getLikes());
                    holder.itemCommentCount.setText(""+modelCollection.getCommentCount());
                    PicassoCache.getPicassoInstance(GroupDetailsActivity.this).load(C.getImageUrl(modelCollection.getImage())).into(holder.itemImage);
                    PicassoCache.getPicassoInstance(GroupDetailsActivity.this).load(C.getImageUrl(modelCollection.getUserProfileImage())).placeholder(R.drawable.female).into(holder.userImg);

                    if(modelCollection.getLikeStatus() == 2) {
                        holder.likeProductIcon.setImageResource(R.drawable.unlike);
                        holder.likeProductIcon.setTag(1);
                    }
                    else {
                        holder.likeProductIcon.setImageResource(R.drawable.like);
                        holder.likeProductIcon.setTag(2);
                    }

                    holder.itemImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(GroupDetailsActivity.this,CollectionDetailActivity.class).putExtra(Constants.COLLECTION_ID,modelCollection.getId()));
                        }
                    });

                    holder.productComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(GroupDetailsActivity.this,CommentsActivity.class).putExtra(Constants.COLLECTION_ID,""+modelCollection.getId()),200);
                        }
                    });

                    holder.shareProduct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View groupItemView = groupItemList.getAdapter().getView(position,null,groupItemList);
                            Bitmap productBitmap = ((BitmapDrawable)((ImageView)groupItemView.findViewById(R.id.col_im)).getDrawable()).getBitmap();
                            if(productBitmap != null){
                                C.shareContentExp(GroupDetailsActivity.this, ((TextView) groupItemView.findViewById(R.id.usr_tag)).getText().toString(), C.getImageUri(GroupDetailsActivity.this, productBitmap));
                            }
                        }
                    });
                    holder.productLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            referenceProductLike = (ImageView)v.findViewById(R.id.likeicon);
                            referenceProductLikeCount = (TextView) v.findViewById(R.id.likes);
                            selectType = Constants.COLLECTION_LIKE;
                            makeProductLikeRequest(""+modelCollection.getId(),""+referenceProductLike.getTag());
                        }
                    });
                    break;
            }
            return itemView;
        }
    }

    private class MyAdapterViewHolder{
        private ImageView itemImage,likeProductIcon,shareProduct,addToGroup,userImg;
        private TextView product_name,salePrice,mrpPrice,itemLikeCount,itemCommentCount,offDiscount,time,ratingCount,userName;
        private RatingBar rating;
        private LinearLayout productLike,productComment,productOffView;
        private LinearLayout rateProductView;
    }

    private class Items{
        public Model model;
        public boolean isProduct;
        public Items(Model model, boolean isProduct) {
            this.model = model;
            this.isProduct = isProduct;
        }
    }
    private void makeGroupDetailsRequest(String groupId){
        pd =  C.getProgressDialog(GroupDetailsActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("gid", groupId);
        map.put("userId", aps.getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GROUP_DETAILS_API,map,this,this);
    }


    private void makeGroupLikeRequest(String gid,String status){
        pd =  C.getProgressDialog(GroupDetailsActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        map.put("gid", gid);
        map.put("status", status);
        Net.makeRequestParams(C.APP_URL+ ApiName.LIKE_GROUP_API,map,like_response,this);
    }

    private void makeProductLikeRequest(String productId, String status) {
        pd = C.getProgressDialog(GroupDetailsActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("status", status);
        map.put("userId", aps.getData(Constants.USER_ID));
        if(selectType.equals(Constants.PRODUCT_LIKE)) {
            map.put("productId", productId);
            Net.makeRequestParams(C.APP_URL + ApiName.LIKE_PRODUCT_API, map, like_response, this);
        }
        else {
            map.put("collectionId", productId);
            Net.makeRequestParams(C.APP_URL + ApiName.LIKE_COLLECTION_API, map, like_response, this);

        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(GroupDetailsActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("GroupDetails Response",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            groupItemListAdapter.clear();
            Model data = new Model(model.getData());
            if(data.getCommentsArray().length>0)
            setCommentsData(data.getCommentsArray());
            itemCount.setText(""+data.getItems());
            Model group = new Model(data.getGroup());
            groupName.setText(group.getName());
            likeCount.setText(""+group.getLikes());
            commentCount.setText(""+group.getCommentCount());
            findViewById(R.id.editgroup).setVisibility((""+group.getProductUserId()).equals(aps.getData(Constants.USER_ID)) ? View.VISIBLE : View.GONE);
            if(group.getLikesStatus() == 2){
                groupLikeIcon.setImageResource(R.drawable.unlike);
                groupLikeIcon.setTag("2");
            }else {
                groupLikeIcon.setImageResource(R.drawable.like);
                groupLikeIcon.setTag("1");
            }
            if(!TextUtils.isEmpty(group.getDescriptionItem())) {
                groupDetails.setText(group.getDescriptionItem());
                groupDetails.setVisibility(View.VISIBLE);
                despIcon.setImageResource(R.drawable.drop_up);
            }
            PicassoCache.getPicassoInstance(GroupDetailsActivity.this).load(C.getImageUrl(group.getImage())).placeholder(R.drawable.female).into(groupImage);
            Model productsArray[] = data.getProductArray();
            Model collectionsArray[] = data.getCollectionsArray();
            for(Model product : productsArray){
              //  listItems.add(new Items(product));
                groupItemListAdapter.add(new Items(product,true));
                //groupScrollView.scrollTo(0, 0);
            }
            for(Model collection : collectionsArray){
                groupItemListAdapter.add(new Items(collection,false));
            }

            groupItemList.setAdapter(groupItemListAdapter);
            groupItemListAdapter.notifyDataSetChanged();
            if(groupItemListAdapter.getCount() > 0)
            setListViewHeightBasedOnChildren(groupItemList);

// changeStyle(R.layout.single_product_layout,new LinearLayoutManager(GroupDetailsActivity.this));
             }
    }

    public Response.Listener<JSONObject> like_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                Log.e("LikeProductStatus", jsonObject.toString());
                if (selectType.equals(Constants.GROUP_LIKE)) {
                    if (model.getLikeStatus() == 1) {
                        groupLikeIcon.setImageResource(R.drawable.like);
                        groupLikeIcon.setTag("1");
                    } else {
                        groupLikeIcon.setImageResource(R.drawable.unlike);
                        groupLikeIcon.setTag("2");
                    }
                    likeCount.setText("" + model.getLikes());
                }else{

                    if (model.getCurrentLikeStatus() == 1) {
                        referenceProductLike.setImageResource(R.drawable.like);
                        referenceProductLike.setTag(2);
                        PRODUCT_LIKE_STATUS = 1;
                    } else {
                        referenceProductLike.setImageResource(R.drawable.unlike);
                        referenceProductLike.setTag(1);
                        PRODUCT_LIKE_STATUS = 2;
                    }

                    referenceProductLikeCount.setText("" + model.getData().replace(" likes", ""));
                }
            }
        }
    };

    private void setCommentsData(Model commentsArray[]){
        addComments.removeAllViews();
        for(int i = 0; i<commentsArray.length;i++) {
            if (i < 3) {
                Model groupComments = commentsArray[i];
                View commentsView = GroupDetailsActivity.this.getLayoutInflater().inflate(R.layout.custom_comments, null);
                TextView name = (TextView) commentsView.findViewById(R.id.user_name);
                TextView comments = (TextView) commentsView.findViewById(R.id.cmmnts);
                CircleImageView userImg = (CircleImageView) commentsView.findViewById(R.id.usercommentImg);

                name.setText(groupComments.getUserName());
                comments.setText(groupComments.getComment());
                PicassoCache.getPicassoInstance(GroupDetailsActivity.this).load(C.ASSET_URL + groupComments.getUserImage()).placeholder(R.drawable.male).into(userImg);
                addComments.addView(commentsView);
            }
        }
        findViewById(R.id.allcomment).setVisibility(View.VISIBLE);
        dropCommentIcon.setImageResource(R.drawable.drop_down);
        isShowComment = true;
    }

    public  void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            //pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+10;
        listView.setLayoutParams(params);
        listView.requestLayout();
//        groupScrollView.requestFocus(View.FOCUS_UP);
//        groupScrollView.scrollTo(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 200 || resultCode == 100){
            makeGroupDetailsRequest(groupId);
        }
    }
}
