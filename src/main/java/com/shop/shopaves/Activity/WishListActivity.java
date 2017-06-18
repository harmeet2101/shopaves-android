package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class WishListActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    private ListView wishList;
    private WishListAdapter wishListAdapter;
    private ProgressDialog pd;
    private AppStore aps;
    private EditText wishListName;
    private String type = Constants.SHOW_WISHLIST;
    private int selectedPosition = -1;
    private StoreItem storeItemReference;
    private LinearLayout deleteProductReference;
    private String requestType = Constants.PRODUCT_DELETE;
    private LinearLayout noProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        wishList = (ListView) findViewById(R.id.wishlist);
        TextView title = (TextView) findViewById(R.id.title);
        wishListName = (EditText) findViewById(R.id.name);
        noProduct = (LinearLayout)findViewById(R.id.noproduct);
        wishListAdapter = new WishListAdapter(WishListActivity.this, 1);
        title.setText(getResources().getString(R.string.mywish_list));
        aps = new AppStore(this);
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.addwishlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(wishListName.getText().toString())) {
                    type = Constants.ADD_WISHLIST;
                    makeAddWishListRequest(wishListName.getText().toString());
                } else
                    Toast.makeText(WishListActivity.this, "Please enter wishlist name", Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.createproduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WishListActivity.this,AddProductActivity.class));
            }
        });
        makeGetWishListRequest();
    }

    private void makeAddWishListRequest(String name) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("userId", aps.getData(Constants.USER_ID));
        pd = C.getProgressDialog(WishListActivity.this);
        Net.makeRequestParams(C.APP_URL + ApiName.ADD_TO_WISH_LIST_API, map, this, this);
    }

    private void makeGetWishListRequest() {
        pd = C.getProgressDialog(WishListActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("userId", aps.getData(Constants.USER_ID));
        map.put("isAll", "true");
        Net.makeRequest(C.APP_URL + ApiName.GET_WISHLIST_API, map, this, this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(WishListActivity.this, "" + VolleyErrors.setError(volleyError), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject);
        Log.e("wishListResponse",jsonObject.toString());
        if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
            if (type.equals(Constants.SHOW_WISHLIST)) {
                wishListAdapter.clear();
                Model dataArray[] = model.getDataArray();
                if(dataArray.length > 0){
                    wishList.setVisibility(View.VISIBLE);
                    noProduct.setVisibility(View.GONE);
                    for (Model data : dataArray) {
                        wishListAdapter.add(new StoreItem(data));
                    }
                }else{
                    wishList.setVisibility(View.GONE);
                    noProduct.setVisibility(View.VISIBLE);
                }

                wishList.setAdapter(wishListAdapter);
                wishListAdapter.notifyDataSetChanged();
                // wishList.removeViewAt();

            } else {
                wishListName.setText("");
                Toast.makeText(WishListActivity.this, "" + model.getMessage(), Toast.LENGTH_LONG).show();
                makeGetWishListRequest();
                type = Constants.SHOW_WISHLIST;
            }
        }else{
            Toast.makeText(WishListActivity.this, "" + model.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class WishListAdapter extends ArrayAdapter<StoreItem> {
        MyHolder holder;

        public WishListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                holder = null;
                LayoutInflater inflater = (WishListActivity.this).getLayoutInflater();
                row = inflater.inflate(R.layout.customlist_wishlist, parent, false);
                holder = new MyHolder();
                row.setTag(holder);
            } else {
                holder = (MyHolder) row.getTag();
            }
            holder.addProduct = (LinearLayout) row.findViewById(R.id.addproduct);
            holder.wishListName = (TextView) row.findViewById(R.id.wishname);
            holder.itemsize=(TextView) row.findViewById(R.id.itemsize);
            holder.wishListDelete = (TextView) row.findViewById(R.id.wishlistdelete);
            holder.addProduct.removeAllViews();
            final StoreItem storeItem = getItem(position);

            holder.wishListDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(WishListActivity.this);
                    builder1.setMessage("Do you want to delete wishlist.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    storeItemReference = storeItem;
                                    selectedPosition = position;
                                    TextView del = (TextView) v.findViewById(R.id.wishlistdelete);
                                    makeDeleteWishListRequest("" + del.getTag());

                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            });

            final Model data = storeItem.model;
            holder.wishListName.setText(data.getName());
            holder.itemsize.setText(" ("+data.getSize()+")");
            Model productArray[] = data.getProductArray();
            holder.wishListDelete.setTag("" + data.getId());
            if(productArray != null){
                for (Model product : productArray) {
                    addWishListProduct("" + data.getId(), "" + product.getProductId(), product.getProductName(), product.getProductImage(),product.getItemPriceValue(), product.getItemDiscountValue(), product.getAverageRating(),product.getRatingCount());
                }
            }
            row.findViewById(R.id.seeall).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WishListActivity.this, ProductGridActivity.class).putExtra(Constants.NAME,data.getName()).putExtra(Constants.ADD_WISHLIST, "" + data.getId()));
                }
            });
            // holder.wishListName.setText(storeItem.name);
            // PicassoCache.getPicassoInstance(AllReviewActivity.this).load(C.ASSET_URL+storeItem.storeImage).placeholder(R.drawable.productsdefault).into(holder.storeImage);
            return row;
        }

        private void addWishListProduct(final String wishListId, final String productId, String name, String prdImage, Double price, Double salePrice, int rating,int ratingCount) {

            final View wishListProductView = WishListActivity.this.getLayoutInflater().inflate(R.layout.single_product_hori, null);
            final TextView productName = (TextView) wishListProductView.findViewById(R.id.product_name);
            TextView productPrice = (TextView) wishListProductView.findViewById(R.id.price);
            RelativeLayout offerdiscount=(RelativeLayout) wishListProductView.findViewById(R.id.offerdiscount);
            TextView offdis = (TextView) wishListProductView.findViewById(R.id.offdis);
            TextView productDiscount = (TextView) wishListProductView.findViewById(R.id.cross_price);
            TextView offDiscount = (TextView) wishListProductView.findViewById(R.id.offdiscount);
            ImageView prdImageView = (ImageView) wishListProductView.findViewById(R.id.product_img);
            final ImageView delete = (ImageView) wishListProductView.findViewById(R.id.wishlike);
            RatingBar ratingProduct = (RatingBar) wishListProductView.findViewById(R.id.rate);
            LinearLayout rateView = (LinearLayout)wishListProductView.findViewById(R.id.rateview);
            TextView rate_count = (TextView)wishListProductView.findViewById(R.id.ratecount);
            delete.setImageResource(R.drawable.delete);
            rateView.setVisibility(rating > 0 ? View.VISIBLE : View.INVISIBLE);
            rate_count.setText("("+ratingCount+")");
            ratingProduct.setRating(rating);
            final RelativeLayout deleteClick = (RelativeLayout) wishListProductView.findViewById(R.id.clicklike);
            productName.setText(name);
            productName.setTag(productId);
            deleteClick.setTag(wishListId);
            PicassoCache.getPicassoInstance(WishListActivity.this).load(C.getImageUrl(prdImage)).into(prdImageView);
            wishListProductView.findViewById(R.id.rateview).setVisibility(ratingCount > 0 ? View.VISIBLE : View.GONE);
            deleteClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(WishListActivity.this);
                    builder1.setMessage("Do you want to delete this item from wishlist.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

//                    makeDeleteWishListProductRequest(""+deleteClick.getTag(),""+productName.getTag());
                                    Map<String, String> map = new HashMap<>();
                                    map.put("wid", deleteClick.getTag().toString());
                                    map.put("pid", productName.getTag().toString());
                                    pd = C.getProgressDialog(WishListActivity.this);

                                    Net.makeRequestParams(C.APP_URL + ApiName.DELETE_WISHLIST_PRODUCT, map, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject jsonObject) {
                                            pd.dismiss();
                                            Log.e("delete",jsonObject.toString());
                                            Model model = new Model(jsonObject.toString());
                                            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                                                Toast.makeText(WishListActivity.this, "" + model.getMessage(), Toast.LENGTH_LONG).show();
//                                                holder.addProduct.removeView(wishListProductView);
//                                                int i=holder.addProduct.getChildCount();
//                                                holder.itemsize.setText(" ("+i+")");
                                                makeGetWishListRequest();
                                                //Log.e("value",""+i);

                                                // holder.itemsize.setText(model.getSize());
                                            }
                                            // makeCollectionDetailRequest();
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {
                                            pd.dismiss();
                                        }
                                    });
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                     }
            });

            wishListProductView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WishListActivity.this,ProductDetailsActivity.class).putExtra("productId",productName.getTag().toString()));
                }
            });
            productPrice.setText("$ "+salePrice);
            if(salePrice<price)
            productDiscount.setText("$ "+price);
            productDiscount.setPaintFlags(productDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            double dis=0;
            if(price > 0)
            dis=(100 - (salePrice*100)/price);
            offerdiscount.setVisibility(dis > 0 ? View.VISIBLE : View.INVISIBLE);
            offdis.setText(new DecimalFormat("##.##").format(dis)+" %OFF");
            holder.addProduct.addView(wishListProductView);
        }
    }

    private static class MyHolder {
        ImageView storeImage;
        LinearLayout addProduct;
        ImageView checkImg;
        TextView wishListName, wishListDelete,itemsize;
    }

    private class StoreItem {
        Model model;

        public StoreItem(Model model) {
            this.model = model;
        }
    }

    private void makeDeleteWishListProductRequest(String wishListId, String productId) {
        requestType = Constants.PRODUCT_DELETE;
        Map<String, String> map = new HashMap<>();
        map.put("wid", wishListId);
        map.put("pid", productId);
        pd = C.getProgressDialog(WishListActivity.this);
        Net.makeRequestParams(C.APP_URL + ApiName.DELETE_WISHLIST_PRODUCT, map, deleteProduct_Response, this);
    }

    private void makeDeleteWishListRequest(String wid) {
        requestType = Constants.WISH_LIST_DELETE;
        Map<String, String> map = new HashMap<>();
        map.put("wid", wid);
        pd = C.getProgressDialog(WishListActivity.this);
        Net.makeRequestParams(C.APP_URL + ApiName.DELETE_WISHLIST, map, deleteProduct_Response, this);

    }

    public Response.Listener<JSONObject> deleteProduct_Response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                Toast.makeText(WishListActivity.this, "" + model.getMessage(), Toast.LENGTH_LONG).show();
                //  wishListAdapter.clear();
                //  makeGetWishListRequest();
                /*wishList.removeViewAt(selectedPosition);
                wishListAdapter.remove();*/
                if (requestType.equals(Constants.PRODUCT_DELETE)) {
                    deleteProductReference.removeViewAt(selectedPosition);
                } else if (requestType.equals(Constants.WISH_LIST_DELETE)) {
                    wishListAdapter.remove(storeItemReference);
                    wishListName.setText("");
                }
                selectedPosition = -1;
                wishListAdapter.notifyDataSetChanged();
            }
            // makeCollectionDetailRequest();
        }
    };
}
