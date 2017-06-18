package com.shop.shopaves.Dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amsyt005 on 9/12/16.
 */
public class AddToWishListDialog extends Dialog implements Response.ErrorListener,Response.Listener<JSONObject>{

    private Context ctx;
    private ProgressDialog pd;
    private AppStore aps;
    private ItemAdapter adapter;
    private ListView wishList;
    private String productId,wishListId;
    private boolean firstLoad= true;
    private int selectedPosition = -1;



    public AddToWishListDialog(Context context,String productId) {
        super(context,R.style.Theme_Dialog);
        this.ctx = context;
        this.productId = productId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_wishlist);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        this.setCancelable(true);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        adapter = new ItemAdapter(ctx, 0);

        final RelativeLayout createbtn = (RelativeLayout)findViewById(R.id.addditem);
        final TextView crtext = (TextView)findViewById(R.id.crtext);
        final TextView addTextBtn = (TextView)findViewById(R.id.addtextbtn);
        final EditText wishlistName = (EditText)findViewById(R.id.wishlistname);
        wishList = (ListView)findViewById(R.id.wishlist);

        aps = new AppStore(ctx);
        findViewById(R.id.createnew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createbtn.setVisibility(View.GONE);
                crtext.setVisibility(View.GONE);
                wishlistName.setVisibility(View.VISIBLE);
                addTextBtn.setText("Save & Add");
            }
        });

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(addTextBtn.getText().toString().equals("Add")){
                    if(!TextUtils.isEmpty(wishListId)){
                        makeAddProductToWishListRequest();
                    }else
                        Toast.makeText(ctx,"Please select wishlist",Toast.LENGTH_LONG).show();
                }else if(addTextBtn.getText().toString().equals("Save & Add")) {
                    if (!TextUtils.isEmpty(wishlistName.getText().toString()))
                        makeAddWishListRequest(wishlistName.getText().toString());
                }
            }
        }); findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        makeGetWishListRequest();
    }


    private void makeGetWishListRequest(){
            pd =  C.getProgressDialog(ctx);
            Map<String,String> map = new HashMap<>();
                map.put("userId", aps.getData(Constants.USER_ID));
                map.put("isAll", "false");
                Net.makeRequest(C.APP_URL+ ApiName.GET_WISHLIST_API,map,wishlist_response,this);
    }

    private void makeAddWishListRequest(String name){
        Map<String,String> map = new HashMap<>();
        map.put("name",name);
        map.put("userId",aps.getData(Constants.USER_ID));
        pd =  C.getProgressDialog(ctx);
        Net.makeRequestParams(C.APP_URL+ ApiName.ADD_TO_WISH_LIST_API,map,this,this);
    }


 private void makeAddProductToWishListRequest(){
        Map<String,String> map = new HashMap<>();
        map.put("pid",productId);
        map.put("wid",wishListId);
        map.put("userId",aps.getData(Constants.USER_ID));
        pd =  C.getProgressDialog(ctx);
        Net.makeRequestParams(C.APP_URL+ ApiName.ADD_PRODUCT_ON_WISHLIST,map,addProduct,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject);
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Toast.makeText(ctx,""+model.getMessage(),Toast.LENGTH_LONG).show();
            Model data = new Model(model.getData());
            wishListId = ""+data.getId();
            makeAddProductToWishListRequest();
        }else{
            Toast.makeText(ctx,""+model.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


    public Response.Listener<JSONObject> wishlist_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Model dataArray[] = model.getDataArray();
                for(Model data : dataArray){
                    adapter.add(new Item(data.getName(),""+data.getId()));
                }
                wishList.setAdapter(adapter);
            }
        }
    };
    public Response.Listener<JSONObject> addProduct = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE) || model.getStatus().equals("400")){
               Toast.makeText(ctx,""+model.getMessage(),Toast.LENGTH_LONG).show();
                dismiss();
            }
        }
    };


    class ItemAdapter extends ArrayAdapter<Item> {

        public ItemAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_category_selection, parent, false);
            } else {
                view = convertView;
            }
            final Item item = getItem(position);
            final TextView name = (TextView) view.findViewById(R.id.itemname);
            final ImageView img = (ImageView) view.findViewById(R.id.ic);
            final ImageView dropright = (ImageView) view.findViewById(R.id.drright);
            name.setText(item.name);
            img.setImageBitmap(null);

            if(firstLoad)
            img.setTag("0");

           /* if(img.getTag().equals("1")) {
                img.setImageResource(R.drawable.check);
                img.setTag("1");
            }
            else {
                img.setImageResource(R.drawable.unchecked);
                img.setTag("0");
            }*/
            if(selectedPosition == position){
                img.setImageResource(R.drawable.selecton);
                selectedPosition = -1;
            }else{
                img.setImageResource(R.drawable.unchecked);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstLoad = false;
                    selectedPosition = position;
                    if(img.getTag().equals("1")){
                        img.setImageResource(R.drawable.unchecked);
                        img.setTag("0");
                    }else{
                        img.setImageResource(R.drawable.selecton);
                        img.setTag("1");
                    }
                    wishListId = item.id;
                    notifyDataSetChanged();
                }
            });


            return view;
        }
    }

    public class Item{
        public String name,id;
        public Item(String name,String id) {
            this.name = name;
            this.id = id;
        }
    }
}
