package com.shop.shopaves.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.ProductDetailsActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OutWears extends Fragment implements Response.ErrorListener,Response.Listener<JSONObject>{
    private RecyclerView recyclerView;
    private ArrayList<Items> list;
    private ProgressDialog pd;
    private String SubId;
    private AppStore aps;
    private boolean isProductDetail;

    public OutWears(String subId,boolean isProductDetail) {
        SubId  = subId;
        this.isProductDetail = isProductDetail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_out_wears, container, false);

        aps = new AppStore(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.outwear_recyclevw);
        list = new ArrayList<>();
        makeProductsRequest();
        return view;
    }

    private  void updateAdapter(){
        OutWearAdapter adapter = new OutWearAdapter(list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void makeProductsRequest(){
        pd = C.getProgressDialog(getActivity());
        Map<String,String> map = new HashMap<>();
        map.put("subCategoryId",SubId);
        Net.makeRequest(C.APP_URL+ ApiName.GET_PRODUCTS,map,this,this);
    }

    private class OutWearAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<Items> source;
        public OutWearAdapter(List<Items> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_outwear_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final Items items = source.get(position);
           // holder.imageView.setImageResource(items.productImage);
            if(!TextUtils.isEmpty(items.productImage))
            Picasso.with(getActivity()).load(C.ASSET_URL + items.productImage).resize(300,300).into(holder.imageView);
            holder.product_name.setText(items.productNam);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isProductDetail){
                        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class).putExtra("productId",items.productId);
                        startActivityForResult(intent,100);
                    }else{
                        if(holder.imageView.getDrawable().getConstantState() != null){
                            final BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.imageView.getDrawable();
                            final Bitmap yourBitmap = bitmapDrawable.getBitmap();
                            //  Intent intent = new Intent();
                            // intent.putExtra("imagebitmap",BitMapToString(yourBitmap));
                            aps.setData(Constants.PRODUCT_BITMAP,BitMapToString(yourBitmap));
                            aps.setData(Constants.PRODUCT_ID,items.productId);
                            aps.setData(Constants.PRODUCT_IMAGE_URL,items.productImage);
                            // ((FinishActivityCallBack)getActivity()).finishActivity(intent);
                            getActivity().finish();
                        }
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return source.size();
        }
    }

    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView product_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.product_img);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
        }
    }

    private class Items {
        public String productImage;
        public String productNam;
        public String productId;

        public Items(String productId,String productImage, String productNam) {
            this.productImage = productImage;
            this.productNam = productNam;
            this.productId = productId;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(getActivity(),""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();

        JSONArray dataArray = null;
        try {
            if(jsonObject.getString("status").equals(Constants.SUCCESS_CODE)) {
                list.clear();
                dataArray = jsonObject.getJSONArray("data");
                if(dataArray.length()>0) {
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject values = dataArray.getJSONObject(i);

                        JSONArray imageArray = values.getJSONArray("image");
                        if (imageArray.length() > 0)
                            list.add(new Items("" + values.getString("id"), imageArray.getString(0), values.getString("itemName")));
                        else
                            list.add(new Items("" + values.getString("id"), "", values.getString("itemName")));
                    }
                    updateAdapter();
                }else{
                    Toast.makeText(getActivity(),"No product to show",Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == 100){
            makeProductsRequest();
        }
    }
}