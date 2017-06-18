package com.shop.shopaves.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.ProductByCategoryActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Deals extends Fragment implements Response.ErrorListener,Response.Listener<JSONObject>{

    private ListView dealsList;
    private ProgressDialog pd;
    private DealAdapter dealAdapter;
    private LinearLayout addCategory;
    private LinearLayout noDataFound;
    private EditText search;
    public Deals(EditText search){
        this.search = search;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_deals, container, false);
        dealsList = (ListView)view.findViewById(R.id.dealslist);
         addCategory = (LinearLayout)view.findViewById(R.id.addCaterory);
        noDataFound = (LinearLayout)view.findViewById(R.id.nodatafoundview);
         dealAdapter = new DealAdapter();
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });
        makeDealRequest();
        return view;
    }
    private void brandsCategory(final Model model) {
        final View brandCategoryItem = getActivity().getLayoutInflater().inflate(R.layout.collection_categry_item, null);
        final TextView textView = (TextView) brandCategoryItem.findViewById(R.id.colection_item);
        final LinearLayout lv = (LinearLayout) brandCategoryItem.findViewById(R.id.lv);
        textView.setText(model.getName());
        lv.setTag(model.getId());

        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isFirstLoad = false;
                int index = addCategory.indexOfChild(brandCategoryItem);
                for(int i = addCategory.getChildCount()-1; i>=0; i--){
                    View vLine =  addCategory.getChildAt(i);
                    TextView textView = (TextView) vLine.findViewById(R.id.colection_item);
                    LinearLayout lv = (LinearLayout) vLine.findViewById(R.id.lv);

                    if(i==index){
                        lv.setBackgroundResource(R.drawable.orange_bttn);
                        textView.setTextColor(getResources().getColor(R.color.white));
                        setDealsAdapter(model);
                           // dealAdapter.add(model);
                           // dealsList.setAdapter(dealAdapter);
                           // dealAdapter.notifyDataSetChanged();

                    }else{
                        lv.setBackgroundResource(R.drawable.bttn_default);
                        textView.setTextColor(getResources().getColor(R.color.fade_black));
                    }
                }
            }
        });
        if(addCategory.getChildCount() == 2){
            lv.setBackgroundResource(R.drawable.orange_bttn);
            textView.setTextColor(getResources().getColor(R.color.white));
           /* if (model.getDealARR().length>0){
                dealsList.setAdapter(dealAdapter);
                *//* dealAdapter.add(model);
                dealsList.setAdapter(dealAdapter);
                dealAdapter.notifyDataSetChanged();*//*
            }*/
            setDealsAdapter(model);
        }
        addCategory.addView(brandCategoryItem);
    }



    private void makeDealRequest(){
        pd =  C.getProgressDialog(getActivity());
        Map<String,String> map = new HashMap<>();
        Net.makeRequest(C.APP_URL+ ApiName.GET_DEALS_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(getActivity(),""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
            Log.e("DealsResponse", jsonObject.toString());
            Model dataArray[] = model.getDataArray();
            for(int i = 0; i < dataArray.length;i++){
                Model deals = dataArray[i];
                if(i == 0){
                    setDealsAdapter(deals);
                }
                brandsCategory(deals);
            }
           /* for(Model deals : dataArray){
                dealAdapter.add(deals);
                brandsCategory(deals);
            }*/
        }
    }

    private void setDealsAdapter(Model deal){
        Model modelArray[] = deal.getDealARR();
        if(modelArray.length > 0){
            noDataFound.setVisibility(View.GONE);
            dealsList.setVisibility(View.VISIBLE);
            dealAdapter.clear();
            for(Model model : modelArray){
                dealAdapter.add(new DealItems(model.getBannerImage(),model.getId(),model.getName()));
            }
            dealsList.setAdapter(dealAdapter);
            dealAdapter.notifyDataSetChanged();
        }else{
            noDataFound.setVisibility(View.VISIBLE);
            dealsList.setVisibility(View.GONE);
            dealAdapter.clear();
            dealAdapter.notifyDataSetChanged();
        }

    }

    private class DealAdapter extends ArrayAdapter<DealItems> implements Constants {
        ItemHolder holder ;
        public DealAdapter() {
            super(getActivity(), R.layout.custom_deals);
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row =convertView;
            holder = null;
            if (row == null) {
                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                row = inflater.inflate(R.layout.custom_deals, parent, false);
                holder = new ItemHolder();
                holder.itemTitle = (TextView) row.findViewById(R.id.dealname);
                holder.banner = (ImageView) row.findViewById(R.id.dealbanner);
                holder.dealbackground = (RelativeLayout)row.findViewById(R.id.dealview);
                holder.blurImg = (ImageView)row.findViewById(R.id.blurimg);
                row.setTag(holder);
            }
            else {
                holder = (ItemHolder) row.getTag();
            }
            final DealItems item = getItem(position);
            //holder.itemTitle.setText(item.getName());
           // Model m[] = item.getDealARR();
           // if(m.length>0 && m!=null){
               // for (Model model : m){
                   // holder.banner.setTag(""+model.getBannerLink());
                    PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(item.dealImg)).into(holder.banner);
                  /*  PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(item.dealImg)).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            //Blurry.with(getActivity()).radius(25).sampling(2).onto(holder.dealbackground);
                            Bitmap blurred = blurRenderScript(getActivity(),bitmap, 25);
                            holder.blurImg.setImageBitmap(blurred);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
*/
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), ProductByCategoryActivity.class).putExtra(Constants.SUB_CATEGORY_ID,item.id).putExtra(Constants.POSITION,0).putExtra(Constants.SUBCATEGORIES,"").putExtra(Constants.TYPE,Constants.DEALS).putExtra(Constants.NAME,item.name));
                }
            });

             //   }
           // }else{
           //     textvieww.setVisibility(View.VISIBLE);
           // }

            return row;
        }
    }

    static class ItemHolder{
        TextView itemTitle;
        ImageView banner,blurImg;
        RelativeLayout dealbackground;
    }

    public class DealItems{
        String dealImg,name;
        int id;

        public DealItems(String dealImg,int id,String name) {
            this.dealImg = dealImg;
            this.id = id;
            this.name = name;
        }
    }

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}
