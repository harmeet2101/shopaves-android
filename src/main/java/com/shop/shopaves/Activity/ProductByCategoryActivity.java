package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Dialog.SortByDialog;
import com.shop.shopaves.Fragments.ChildCategoryFragment;
import com.shop.shopaves.Interface.FragmentCallBack;
import com.shop.shopaves.Interface.ResultCallBack;
import com.shop.shopaves.Interface.ValueCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductByCategoryActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {
    private TabLayout tabLayout;
    private AppStore aps;
    private RecyclerView recyclerView;
    private ArrayList<Items> list;
    private LinearLayout changeProductViewStyle;
    private ProgressDialog pd;
    private ImageView likeProductIcon;
    private TextView likeProductCount;
    private boolean firstLoad = true;
    private ProductAdapter adapter;
    private GridView productgrid;
    private ItemAdapter itemAdapter;
    private String categoryId = "";
    private String subCategories;
    private String OTHER_TYPE = "";
    private String filterParams = "";
    private ImageView listChangeIcon;
    private String sortBy = "1";
    private LinearLayout categoryView;
    private TextView topCategory;
    private TextView showingResults;
    private List<Fragment> backStackList;
    private boolean isParentCategoryLoad = false;
    private int tabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_by_category);
        backStackList = new ArrayList<>();
        tabLayout = (TabLayout) findViewById(R.id.wn_fashion_tab_layout);

        TextView title = (TextView) findViewById(R.id.title);
        recyclerView = (RecyclerView) findViewById(R.id.prouduct_recycleview);
        productgrid = (GridView) findViewById(R.id.galleryview);
        listChangeIcon = (ImageView) findViewById(R.id.listchangeicon);
        changeProductViewStyle = (LinearLayout) findViewById(R.id.chnge_style);
        categoryView = (LinearLayout) findViewById(R.id.catlist);
        topCategory = (TextView) findViewById(R.id.topcat);
        itemAdapter = new ItemAdapter();
        list = new ArrayList<>();
        title.setText("Products");
        aps = new AppStore(this);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        if (getIntent().getStringExtra(Constants.TYPE) != null)
            OTHER_TYPE = getIntent().getStringExtra(Constants.TYPE);
        // makeProductBySubCatRequest(""+getIntent().getIntExtra(Constants.SUB_CATEGORY_ID,0));
        categoryId = "" + getIntent().getIntExtra(Constants.SUB_CATEGORY_ID, 0);
        tabPosition = getIntent().getIntExtra(Constants.POSITION, 0);
        makeFilterApiRequest("");
        subCategories = getIntent().getStringExtra(Constants.SUBCATEGORIES);

        if (!OTHER_TYPE.equals(Constants.DEALS)) {
            setTabData();
            title.setVisibility(View.GONE);
        }
        else {
            categoryView.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            findViewById(R.id.tabcategory).setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
            title.setText(getIntent().getStringExtra(Constants.NAME));
        }
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("tabid", "" + tab.getTag());
                // makeProductBySubCatRequest("" + tab.getTag());

                categoryId = "" + tab.getTag();
                makeFilterApiRequest(filterParams);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(filterParams))
                    startActivityForResult(new Intent(ProductByCategoryActivity.this, FilterActivity.class).putExtra(Constants.TYPE, OTHER_TYPE), 100);
                else
                    startActivityForResult(new Intent(ProductByCategoryActivity.this, FilterActivity.class).putExtra(Constants.TYPE, OTHER_TYPE).putExtra(Constants.SELECTED_ITEMS, filterParams), 100);
            }
        });

        findViewById(R.id.sortby).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SortByDialog(ProductByCategoryActivity.this, valueCallBack, sortBy).show();
            }
        });

        findViewById(R.id.tabcategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryView.setVisibility(categoryView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        changeProductViewStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeProductViewStyle.getTag().toString().equals("single")) {
                    changeStyle(R.layout.single_product_layout, new LinearLayoutManager(ProductByCategoryActivity.this));
                    changeProductViewStyle.setTag("sideview");
                    productgrid.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    listChangeIcon.setImageResource(R.drawable.listchangeicon);
                } else if (changeProductViewStyle.getTag().toString().equals("sideview")) {
                    changeProductViewStyle.setTag("grid");
                    listChangeIcon.setImageResource(R.drawable.listside);
                    changeStyle(R.layout.side_item_view, new LinearLayoutManager(ProductByCategoryActivity.this));
                } else if (changeProductViewStyle.getTag().toString().equals("grid")) {
                    changeProductViewStyle.setTag("single");
                    productgrid.setVisibility(View.VISIBLE);
                    itemAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.GONE);
                    listChangeIcon.setImageResource(R.drawable.grid);
                }
            }
        });
    }

    ValueCallBack valueCallBack = new ValueCallBack() {
        @Override
        public void callBack(String value) {
            sortBy = value;
            makeFilterApiRequest(filterParams);
        }
    };

    private void setTabData() {
        if(tabLayout.getTabCount() > 0)
            tabLayout.removeAllTabs();
        if (OTHER_TYPE.equals(Constants.BRAND_CATEGORY_PRODUCT) || OTHER_TYPE.equals(Constants.PRODUCT_CATEGORY)) {
            Model categoryModel = new Model(subCategories);
            Model categoryArray[] = categoryModel.getArray(subCategories);
            if (OTHER_TYPE.equals(Constants.BRAND_CATEGORY_PRODUCT))
                tabLayout.addTab(tabLayout.newTab().setTag(0).setText("All"));
            for (Model data : categoryArray) {
                Log.e("tabName", data.getName());
                tabLayout.addTab(tabLayout.newTab().setTag(data.getId()).setText(data.getName()));
            }
            tabLayout.getTabAt(tabPosition).select();

        } else if (OTHER_TYPE.equalsIgnoreCase(Constants.SHOP)) {
            Model model = new Model(subCategories);
            if (model.getStatus().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
                Model dataArray[] = model.getDataArray();
                for (Model data : dataArray) {
                    if (!data.isParentCatNull()) {
                        Model category = new Model(data.getParentCat());
                        tabLayout.addTab(tabLayout.newTab().setTag(data.getId()).setText(category.getName()));
                    } else
                        tabLayout.addTab(tabLayout.newTab().setTag(data.getId()).setText(data.getName()));
                }
                tabLayout.getTabAt(tabPosition).select();
            }
        } else {
            Model model = new Model(subCategories);
            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                Model dataArray[] = model.getDataArray();
                for (Model data : dataArray) {
                    Log.e("tabName", data.getName());
                    tabLayout.addTab(tabLayout.newTab().setTag(data.getId()).setText(data.getName()));
                }
                tabLayout.getTabAt(tabPosition).select();
            }
        }
        //   topCategory.setText(tabLayout.getTabAt(tabPosition).getText());
    }

    public void changeStyle(int res, LinearLayoutManager layoutManager) {
        firstLoad = true;
        adapter = new ProductAdapter(list, res);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(ProductByCategoryActivity.this, "" + VolleyErrors.setError(volleyError), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();

        Model model = new Model(jsonObject.toString());
        if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
            Log.e("response", jsonObject.toString());
            Model dataArray[] = model.getDataArray();
            list.clear();
            itemAdapter.clear();
            itemAdapter.notifyDataSetChanged();

            if (dataArray != null)
                if (dataArray.length > 0) {
                    findViewById(R.id.noproduct).setVisibility(View.GONE);
                    productgrid.setVisibility(View.VISIBLE);
                    for (Model product : dataArray) {

                        list.add(new Items(product.getProductId(), product.getProductName(), product.getAverageRating(), product.getRatingCount(), product.getProductImage(), product.getDiscount(), product.getMRP(), product.getProductLikeStatus(), product.getProductCommentCount(), product.getLikeCount(),product.getId(),product.getEditProductImage(),product.getImageProperties(),product.getProductImage()));
                        itemAdapter.add(new Items(product.getProductId(), product.getProductName(), product.getAverageRating(), product.getRatingCount(), product.getProductImage(), product.getDiscount(), product.getMRP(), product.getProductLikeStatus(), product.getProductCommentCount(), product.getLikeCount(),product.getId(),product.getEditProductImage(),product.getImageProperties(),product.getProductImage()));

                    }


                    itemAdapter.notifyDataSetChanged();
                    productgrid.setAdapter(itemAdapter);
                    if (changeProductViewStyle.getTag().equals("single")) {
                        productgrid.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else if (changeProductViewStyle.getTag().equals("sideview")) {
                        productgrid.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        changeStyle(R.layout.single_product_layout, new LinearLayoutManager(ProductByCategoryActivity.this));
                    } else {
                        productgrid.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        changeStyle(R.layout.side_item_view, new LinearLayoutManager(ProductByCategoryActivity.this));
                    }
                    //setProducrAdapter();
                } else {
                    recyclerView.setVisibility(View.GONE);
                    productgrid.setVisibility(View.GONE);
                    findViewById(R.id.noproduct).setVisibility(View.VISIBLE);
                }

            makeFilterListRequest();

            //  changeStyle(R.layout.single_product_hori,new GridLayoutManager(ProductByCategoryActivity.this,2));
        }
    }

    Response.Listener<JSONObject> filterResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                aps.setData(Constants.PRODUCT_FILTER_VALUES, jsonObject.toString());
                if(!isParentCategoryLoad)
                    makegetCategoryRequest();
            }


        }
    };

    private class ProductAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<Items> source;
        private int resourse;

        public ProductAdapter(List<Items> itemsList, int resourselayout) {
            this.source = itemsList;
            this.resourse = resourselayout;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(resourse, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final Items items = source.get(position);
            //  holder.collectionName.setText(model.getName());
            // holder.showingResults.setText()
            holder.productName.setText(C.getTitleCase(items.name));
            holder.productName.setTag("" + items.productId);
            PicassoCache.getPicassoInstance(ProductByCategoryActivity.this).load(C.getImageUrl(items.imageUrl)).into(holder.prdImageView);
            holder.productDiscount.setPaintFlags(holder.productDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productPrice.setText("$ " + C.formateValue(Double.parseDouble(items.discountValue)));
            holder.productDiscount.setText("$ " + C.formateValue(Double.parseDouble(items.mrpPrice)));
            holder.ratingProduct.setRating(items.rating);
            holder.commentCount.setText("" + items.commentCount);
            holder.likeCount.setText("" + items.likeCount);
            holder.ratingCount.setText("(" + items.ratingCount + ")");
            holder.rateView.setVisibility(items.ratingCount > 0 ? View.VISIBLE : View.GONE);
            if (Double.parseDouble(items.mrpPrice) > 0) {
                holder.offDiscount.setText("" + C.formateValue(100 - (Double.parseDouble(items.discountValue) * 100) / Double.parseDouble(items.mrpPrice)) + "% off");
                holder.offDiscountView.setVisibility((100 - (Double.parseDouble(items.discountValue) * 100) / Double.parseDouble(items.mrpPrice)) > 0 ? View.VISIBLE : View.GONE);
            }
            if (firstLoad) {
                if (items.likeStatus == 1) {
                    holder.likeIcon.setImageResource(R.drawable.like);
                    holder.likeIcon.setTag("1");
                } else {
                    holder.likeIcon.setImageResource(R.drawable.unlike);
                    holder.likeIcon.setTag("2");
                }
            }

            if ((100 - (Double.parseDouble(items.discountValue) * 100) / Double.parseDouble(items.mrpPrice)) > 50)
                holder.specialOffer.setVisibility(View.VISIBLE);
            else
                holder.specialOffer.setVisibility(View.GONE);

            holder.likeProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //firstLoad = false;
                    if (!TextUtils.isEmpty(aps.getData(Constants.USER_ID))) {
                        pd = C.getProgressDialog(ProductByCategoryActivity.this);
                        Map<String, String> map = new HashMap<>();
                        map.put("productId", "" + items.productId);
                        map.put("status", holder.likeIcon.getTag().equals("1") ? "2" : "1");
                        map.put("userId", aps.getData(Constants.USER_ID));
                        Net.makeRequestParams(C.APP_URL + ApiName.LIKE_PRODUCT_API, map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                pd.dismiss();
                                Model model = new Model(jsonObject);
                                if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                                    Log.e("productLikeStatus", jsonObject.toString());
                                    Log.e("like", model.getData().replace(" likes", ""));
                                    //  if (model.getCurrentLikeStatus() == 1) {
                                    //                  likeProductIcon.setImageResource(R.drawable.like);
                                    //              likeProductIcon.setTag("1");
                                    itemAdapter.getItem(position).likeStatus = model.getCurrentLikeStatus();
                                    items.likeStatus = model.getCurrentLikeStatus();
                                    items.likeCount = Integer.parseInt(model.getData().replace(" likes", ""));
                                    //    }
                                /* else {
                        //                likeProductIcon.setImageResource(R.drawable.unlike);
                            //            likeProductIcon.setTag("2");
                                        itemAdapter.getItem(position).likeStatus = model.getCurrentLikeStatus();

                                        items.likeStatus = model.getCurrentLikeStatus();
                                        items.likeCount = Integer.parseInt(model.getData().replace(" likes",""));
                                    }*/

                                    adapter.notifyDataSetChanged();
                                }            // makeCollectionDetailRequest();
                            }
                        }, ProductByCategoryActivity.this);
                    } else
                        startActivity(new Intent(ProductByCategoryActivity.this, LoginActivity.class));
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (OTHER_TYPE.equals(Constants.EDIT_COLLECTION)) {

//                        final BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.prdImageView.getDrawable();
//                        final Bitmap yourBitmap = bitmapDrawable.getBitmap();
                        //  Intent intent = new Intent();
                        // intent.putExtra("imagebitmap",BitMapToString(yourBitmap));
//                        aps.setData(Constants.PRODUCT_BITMAP, BitMapToString(yourBitmap));
                        aps.setData(Constants.PRODUCT_ID, "" + items.productId);
                        aps.setData(Constants.PRODUCT_IMAGE_URL, items.imageUrl);
                        aps.setData(Constants.ID,""+items.id);
                        aps.setData(Constants.EDIT_PRODUCT_IMAGE,""+items.editProductImage);
                        aps.setData(Constants.IMAGEPROPERTIES,""+items.imageProperties);
                        aps.setData(Constants.PRODUCT_IMAGE,""+items.productImage);
                        setResult(200);
                        finish();
                    } else {
                        startActivity(new Intent(ProductByCategoryActivity.this, ProductDetailsActivity.class).putExtra("productId", "" + items.productId));
                    }
                }
            });

            holder.productComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(ProductByCategoryActivity.this, CommentsActivity.class).putExtra(Constants.PRODUCT_ID, "" + items.productId),500);
                }
            });
            holder.addtoGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductByCategoryActivity.this, AddToGroupActivity.class).putExtra(Constants.ID, "" + items.productId));
                }
            });
            holder.shareProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap productBitmap = ((BitmapDrawable) holder.prdImageView.getDrawable()).getBitmap();
                    if (productBitmap != null)
                        C.shareContentExp(ProductByCategoryActivity.this, items.name, C.getImageUri(ProductByCategoryActivity.this, productBitmap));
                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
    }


    private String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void makeProductBySubCatRequest(String subCatId) {
        Map<String, String> map = new HashMap<>();
        map.put("catId", subCatId);
        map.put("myProduct", "false");
        if (!org.apache.http.util.TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
            map.put("userId", aps.getData(Constants.USER_ID));

        pd = C.getProgressDialog(ProductByCategoryActivity.this);

        /*userId(optional), catId(optional), myProduct[true/false](optional), forBrand[true/false](optional), bid(optional)*/

        Net.makeRequest(C.APP_URL + ApiName.GET_PRODUCTS, map, this, this);
    }

    private void makeFilterListRequest() {
        pd = C.getProgressDialog(ProductByCategoryActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("catId", categoryId);
        Net.makeRequest(C.APP_URL + ApiName.GET_SPECIFICATION_TITLES, map, filterResponse, this);
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView showingResults;
        TextView productName;
        TextView productPrice;
        TextView productDiscount;
        TextView offDiscount, likeCount, commentCount, ratingCount;
        ImageView prdImageView, likeIcon, shareProduct, addtoGroup;
        ImageView delete;
        RatingBar ratingProduct;
        RelativeLayout likeProduct, specialOffer;
        LinearLayout productComment, offDiscountView, rateView;

        public MyViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productPrice = (TextView) itemView.findViewById(R.id.price);
            //   showingResults = (TextView)findViewById(R.id.showingResults);
            productDiscount = (TextView) itemView.findViewById(R.id.cross_price);
            offDiscount = (TextView) itemView.findViewById(R.id.offdiscount);
            prdImageView = (ImageView) itemView.findViewById(R.id.product_img);
            delete = (ImageView) itemView.findViewById(R.id.wishlike);
            ratingProduct = (RatingBar) itemView.findViewById(R.id.rate);
            likeProduct = (RelativeLayout) itemView.findViewById(R.id.clicklike);
            likeIcon = (ImageView) itemView.findViewById(R.id.wishlike);
            likeCount = (TextView) itemView.findViewById(R.id.likecount);
            commentCount = (TextView) itemView.findViewById(R.id.commentcount);
            productComment = (LinearLayout) itemView.findViewById(R.id.productcomment);
            offDiscountView = (LinearLayout) itemView.findViewById(R.id.off);
            shareProduct = (ImageView) itemView.findViewById(R.id.productshare);
            addtoGroup = (ImageView) itemView.findViewById(R.id.addto);
            ratingCount = (TextView) itemView.findViewById(R.id.ratingcount);
            rateView = (LinearLayout) itemView.findViewById(R.id.rateview);
            specialOffer = (RelativeLayout) itemView.findViewById(R.id.specialoffer);
        }
    }

    public class Items {
        public int productId, rating, ratingCount, likeStatus, commentCount, likeCount,id;
        public String name, imageUrl, discountValue, mrpPrice,imageProperties,editProductImage,productImage;

        public Items(int productId, String name, int rating, int ratingCount, String imageUrl, String discountValue, String mrpPrice, int likeStatus, int commentCount, int likeCount,int id,String imageProperties,String editProductImage,String productImage) {
            this.productId = productId;
            this.name = name;
            this.ratingCount = ratingCount;
            this.rating = rating;
            this.likeStatus = likeStatus;
            this.imageUrl = imageUrl;
            this.discountValue = discountValue;
            this.mrpPrice = mrpPrice;
            this.commentCount = commentCount;
            this.likeCount = likeCount;
            this.id = id;
            this.imageProperties = imageProperties;
            this.editProductImage = editProductImage;
            this.productImage = productImage;
        }
    }

    public class ItemAdapter extends ArrayAdapter<Items> {
        boolean isMenuShow = false;
        private LayoutInflater inflater = null;

        public ItemAdapter() {
            super(ProductByCategoryActivity.this, R.layout.custom_grid_product);
            inflater = (LayoutInflater) ProductByCategoryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.custom_grid_product, parent, false);
                holder = new Holder();
                holder.productName = (TextView) view.findViewById(R.id.product_name);
                holder.productPrice = (TextView) view.findViewById(R.id.price);
                holder.productDiscount = (TextView) view.findViewById(R.id.cross_price);
                holder.offDiscount = (TextView) view.findViewById(R.id.offdiscount);
                holder.prdImageView = (ImageView) view.findViewById(R.id.product_img);
                holder.likeIcon = (ImageView) view.findViewById(R.id.wishlike);
                holder.menu = (ImageView) view.findViewById(R.id.menu);
                holder.ratingProduct = (RatingBar) view.findViewById(R.id.rate);
                holder.likeClick = (RelativeLayout) view.findViewById(R.id.clicklike);
                holder.menuView = (CardView) view.findViewById(R.id.menuview);
                holder.commentview = (LinearLayout) view.findViewById(R.id.comment);
                holder.addToGroup = (LinearLayout) view.findViewById(R.id.addto);
                holder.shareProduct = (LinearLayout) view.findViewById(R.id.share);
                holder.ratingProduct = (RatingBar) view.findViewById(R.id.rate);
                holder.offDicountView = (LinearLayout) view.findViewById(R.id.off);
                holder.rateCount = (TextView) view.findViewById(R.id.ratecount);
                holder.rateView = (LinearLayout) view.findViewById(R.id.rateView);
                holder.specialOffer = (RelativeLayout) view.findViewById(R.id.specialoffer);

                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }

            final Items item = getItem(position);
            holder.productName.setText(C.getTitleCase(item.name));
            holder.ratingProduct.setRating(item.rating);
            holder.productName.setTag("" + item.productId);
            holder.rateCount.setText("(" + item.ratingCount + ")");
            PicassoCache.getPicassoInstance(ProductByCategoryActivity.this).load(C.getImageUrl(item.imageUrl)).into(holder.prdImageView);
            Log.e("countTag", "" + item.ratingCount);
            holder.productDiscount.setPaintFlags(holder.productDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productPrice.setText("$ " + C.formateValue(Double.parseDouble(item.discountValue)));
            holder.productDiscount.setText("$ " + C.formateValue(Double.parseDouble(item.mrpPrice)));
            if (Double.parseDouble(item.mrpPrice) > 0)
                holder.offDiscount.setText("" + C.formateValue(100 - (Double.parseDouble(item.discountValue) * 100) / Double.parseDouble(item.mrpPrice)) + "% off");
            holder.offDicountView.setVisibility((100 - (Double.parseDouble(item.discountValue) * 100) / Double.parseDouble(item.mrpPrice)) > 0 ? View.VISIBLE : View.INVISIBLE);

            if ((100 - (Double.parseDouble(item.discountValue) * 100) / Double.parseDouble(item.mrpPrice)) > 50)
                holder.specialOffer.setVisibility(View.VISIBLE);
            else
                holder.specialOffer.setVisibility(View.GONE);
            holder.rateView.setVisibility(item.ratingCount > 0 ? View.VISIBLE : View.INVISIBLE);
            if (item.likeStatus == 1) {
                holder.likeIcon.setImageResource(R.drawable.itemlike);
                holder.likeIcon.setTag("2");
            } else {
                holder.likeIcon.setImageResource(R.drawable.itemunlike);
                holder.likeIcon.setTag("1");
            }

            final Holder finalHolder = holder;
            holder.likeClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = C.getProgressDialog(ProductByCategoryActivity.this);
                    Map<String, String> map = new HashMap<>();
                    map.put("productId", "" + item.productId);
                    map.put("status", "" + finalHolder.likeIcon.getTag());
                    map.put("userId", aps.getData(Constants.USER_ID));
                    Net.makeRequestParams(C.APP_URL + ApiName.LIKE_PRODUCT_API, map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            pd.dismiss();
                            Model model = new Model(jsonObject);
                            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                                Log.e("productLikeStatus", jsonObject.toString());

                                //if (model.getCurrentLikeStatus() == 1) {
                                //                finalHolder.likeIcon.setImageResource(R.drawable.itemlike);
                                //              finalHolder.likeIcon.setTag("2");
                                item.likeStatus = model.getCurrentLikeStatus();
                                list.get(position).likeStatus = model.getCurrentLikeStatus();
                                list.get(position).likeCount = Integer.parseInt(model.getData().replace(" likes", ""));
                                itemAdapter.notifyDataSetChanged();
                                //    }
                            /*else {
                                    //finalHolder.likeIcon.setImageResource(R.drawable.itemunlike);
                                    //finalHolder.likeIcon.setTag("1");
                                    item.likeStatus = model.getCurrentLikeStatus();
                                }*/
                            }            // makeCollectionDetailRequest();
                        }
                    }, ProductByCategoryActivity.this);
                }
            });

            final Holder finalHolder1 = holder;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (OTHER_TYPE.equals(Constants.EDIT_COLLECTION)) {

                        final BitmapDrawable bitmapDrawable = (BitmapDrawable) finalHolder1.prdImageView.getDrawable();
                        final Bitmap yourBitmap = bitmapDrawable.getBitmap();
                        //  Intent intent = new Intent();
                        // intent.putExtra("imagebitmap",BitMapToString(yourBitmap));
                        aps.setData(Constants.PRODUCT_BITMAP, BitMapToString(yourBitmap));
                        aps.setData(Constants.PRODUCT_ID, "" + item.productId);
                        aps.setData(Constants.PRODUCT_IMAGE_URL, item.imageUrl);
                        aps.setData(Constants.ID,""+item.id);
                        aps.setData(Constants.EDIT_PRODUCT_IMAGE,""+item.editProductImage);
                        aps.setData(Constants.IMAGEPROPERTIES,""+item.imageProperties);
                        aps.setData(Constants.PRODUCT_IMAGE,""+item.productImage);

                        setResult(200);
                        finish();
                    } else {
                        TextView prd = (TextView) v.findViewById(R.id.product_name);
                        startActivity(new Intent(ProductByCategoryActivity.this, ProductDetailsActivity.class).putExtra("productId", prd.getTag().toString()));

                    }
                }
            });

            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isMenuShow) {
                        finalHolder.menuView.setVisibility(View.VISIBLE);
                        isMenuShow = true;
                    } else {
                        finalHolder.menuView.setVisibility(View.GONE);
                        isMenuShow = false;
                    }
                }
            });

            final Holder productHolder = holder;
            holder.commentview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(ProductByCategoryActivity.this, CommentsActivity.class).putExtra(Constants.PRODUCT_ID, "" + productHolder.productName.getTag()),500);
                }
            });
            holder.addToGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductByCategoryActivity.this, AddToGroupActivity.class).putExtra(Constants.ID, "" + item.productId));
                }
            });

            holder.shareProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap productBitmap = ((BitmapDrawable) productHolder.prdImageView.getDrawable()).getBitmap();
                    C.shareContentExp(ProductByCategoryActivity.this, item.name, C.getImageUri(ProductByCategoryActivity.this, productBitmap));
                }
            });
            return view;
        }

        public class Holder {
            private ImageView prdImageView, menu, likeIcon;
            private TextView productName, productPrice, productDiscount, offDiscount, rateCount;
            private RatingBar ratingProduct;
            private RelativeLayout likeClick, specialOffer;
            private LinearLayout commentview, addToGroup, shareProduct, offDicountView, rateView;
            private CardView menuView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 500 && resultCode == 200){
            makeFilterApiRequest(filterParams);
        }
        else if (resultCode == 200) {
            Log.e("paramsAPICall", data.getStringExtra(Constants.API_PARAMS));
            filterParams = data.getStringExtra(Constants.API_PARAMS);
            makeFilterApiRequest(filterParams);
            // Net.makeRequest(C.APP_URL + ApiName.GET_PRODUCT_FILTER_API,data.getStringExtra(Constants.API_PARAMS), ProductByCategoryActivity.this, ProductByCategoryActivity.this);
        }
    }

    private void makeFilterApiRequest(String filterParams) {
        pd = C.getProgressDialog(ProductByCategoryActivity.this);
        try {
            if (!TextUtils.isEmpty(filterParams)) {
                JSONObject jsonObject = new JSONObject(filterParams);
                jsonObject.remove("sortBy");
                jsonObject.put("sortBy", sortBy);
                if (OTHER_TYPE.equals(Constants.DEALS))
                    jsonObject.put("dealId", categoryId);
                else
                    jsonObject.put("catId", categoryId);
                Net.makeRequest(C.APP_URL + ApiName.GET_PRODUCT_FILTER_API, jsonObject.toString(), ProductByCategoryActivity.this, ProductByCategoryActivity.this);
            } else {
                JSONObject params = new JSONObject();
                try {
                    params.put("hasOffer", "");
                    params.put("condition", "");
                    params.put("sortBy", sortBy);
                    if (OTHER_TYPE.equals(Constants.DEALS))
                        params.put("dealId", categoryId);
                    else
                        params.put("catId", categoryId);
                    //params.put("spcf","");
                    // params.put("bIds","");
                    //     params.put("skip","");
                    params.put("key", "");
                    if (!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
                        params.put("userId", aps.getData(Constants.USER_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("params", params.toString());
                Net.makeRequest(C.APP_URL + ApiName.GET_PRODUCT_FILTER_API, params.toString(), ProductByCategoryActivity.this, ProductByCategoryActivity.this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*for showing category view*/

    private FragmentManager fragmentManagers;

    private void replaceFragment(Fragment fragment) {
        // String backStateName = fragment.getClass().getName();
        fragmentManagers = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManagers.beginTransaction();
        transaction.replace(R.id.catlist, fragment);
        // transaction.addToBackStack(backStateName);
        transaction.commit();
    }

    private void makegetCategoryRequest() {
        pd.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("catId", categoryId);
        Net.makeRequest(C.APP_URL + ApiName.GET_PARENT_API, params, catResponse, this);
    }

    Response.Listener<JSONObject> catResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                isParentCategoryLoad = true;
                Model data = new Model(model.getData());
                topCategory.setText(data.getName());
                replaceFragment(new ChildCategoryFragment(fragmentCallBack,resultCallBack, data.getParentId(), data.getName()));
                C.IS_TOP_FILTER = true;
                backStackList.add(new ChildCategoryFragment(fragmentCallBack,resultCallBack, data.getParentId(), data.getName()));
            }
        }
    };

    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void callBack(String value,String subCat,int catPosition) {
            categoryView.setVisibility(View.GONE);
            categoryId = value;
            // makeFilterApiRequest(filterParams);
            subCategories = subCat;
            tabPosition = catPosition;
            finish();
            startActivity(new Intent(ProductByCategoryActivity.this,ProductByCategoryActivity.class).putExtra(Constants.SUB_CATEGORY_ID,Integer.parseInt(categoryId)).putExtra(Constants.POSITION,tabPosition).putExtra(Constants.SUBCATEGORIES,subCategories));
        }
    };

    FragmentCallBack fragmentCallBack = new FragmentCallBack() {
        @Override
        public void onFragmentCallBack(Fragment fragment) {
            replaceFragment(fragment);
            backStackList.add(fragment);
        }

        @Override
        public void finishFragment() {
            fragmentManagers.popBackStackImmediate();
            backStackList.remove(backStackList.size() - 1);
            if(backStackList.size() == 1)
                C.IS_TOP_FILTER = true;
            replaceFragment(backStackList.get(backStackList.size() - 1 ));
        }
    };
}
