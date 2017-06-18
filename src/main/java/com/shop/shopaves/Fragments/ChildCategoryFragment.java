package com.shop.shopaves.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.ProductByCategoryActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Interface.FragmentCallBack;
import com.shop.shopaves.Interface.ResultCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amsyt005 on 9/1/17.
 */
public class ChildCategoryFragment extends Fragment implements Response.ErrorListener,Response.Listener<JSONObject>{

    private RecyclerView childMenu;
    private ProgressDialog pd;
    private String lastChildSubCategory;
    private ArrayList<Items> list;
    private FragmentCallBack fragmentCallBack;
    private int parentId;
    private String parentName;
    private View parentView;
    private ResultCallBack resultCallBack;

    public ChildCategoryFragment(FragmentCallBack fragmentCallBack, ResultCallBack resultCallBack, int parentId, String parentName) {
        this.fragmentCallBack = fragmentCallBack;
        this.parentId  = parentId;
        this.parentName = parentName;
        this.resultCallBack = resultCallBack;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.child_category_fragment, container, false);
        v.startAnimation(inFromRightAnimation());
        parentView = v;
        childMenu = (RecyclerView)v.findViewById(R.id.childmenu);
        TextView headerName = (TextView)v.findViewById(R.id.lblListHeader);
        headerName.setText(parentName);
        list = new ArrayList<>();
        v.findViewById(R.id.backtoparent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentCallBack.finishFragment();
            }
        });

        if(C.IS_TOP_FILTER)
            v.findViewById(R.id.backtoparent).setVisibility(View.GONE);
        C.IS_TOP_FILTER = false;
        makeGetCategoryRequest();
        return v;
    }

    private void makeGetCategoryRequest(){
        pd = C.getProgressDialog(getActivity());
        Map<String,String> map = new HashMap<>();
        map.put("parentId",""+parentId);
        Net.makeRequest(C.APP_URL+ ApiName.GET_CATEGORY_API,map,this,this);
    }

    private  void updateAdapter(){
        OutWearAdapter  adapter = new OutWearAdapter(list, R.layout.custom_category_selection);
        childMenu.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        childMenu.setLayoutManager(layoutManager);
        childMenu.setAdapter(adapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(getActivity(),""+ VolleyErrors.setError(volleyError),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("getCategoryResponse",jsonObject.toString());
        lastChildSubCategory = jsonObject.toString();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            for(Model data : dataArray){
                list.add(new Items(data.hasSubCat(),data.getName(),data.getId()));
            }
            updateAdapter();
        }
    }

    private class OutWearAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private List<Items> source;
        private int resourse;

        public OutWearAdapter(List<Items> itemsList,int resourselayout) {
            this.source = itemsList;
            this.resourse=resourselayout;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(resourse, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final Items items = source.get(position);
            holder.product_name.setText(items.categoryName);
            holder.dropRight.setVisibility(items.hasSubCategory ? View.VISIBLE : View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(items.hasSubCategory){
                        parentView.setAnimation(outToLeftAnimation());

                        fragmentCallBack.onFragmentCallBack(new ChildCategoryFragment(fragmentCallBack,resultCallBack,items.id,items.categoryName));
                        //getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    }else{
                       // startActivity(new Intent(getActivity(),ProductByCategoryActivity.class).putExtra(Constants.SUB_CATEGORY_ID,items.id).putExtra(Constants.POSITION,position).putExtra(Constants.SUBCATEGORIES,lastChildSubCategory));
                       if(resultCallBack != null){
                           resultCallBack.callBack(""+items.id,lastChildSubCategory,position);
                       }else
                        startActivity(new Intent(getActivity(),ProductByCategoryActivity.class).putExtra(Constants.SUB_CATEGORY_ID,items.id).putExtra(Constants.POSITION,position).putExtra(Constants.SUBCATEGORIES,lastChildSubCategory));
                    }
                     //  startActivityForResult(new Intent(SubCategoryActivity.this,SubCategoryActivity.class).putExtra(Constants.PARENT_ID,items.id).putExtra(Constants.NAME,items.categoryName).putExtra(Constants.TYPE,type),200);
                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView dropRight,dropRightChecked;
        private TextView product_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            dropRight  = (ImageView) itemView.findViewById(R.id.drright);
            product_name = (TextView) itemView.findViewById(R.id.itemname);
            dropRightChecked = (ImageView)itemView.findViewById(R.id.ic);
            dropRight.setVisibility(View.VISIBLE);
            //dropRight.setVisibility(View.VISIBLE);
        }
    }

    private class Items {
        public boolean hasSubCategory;
        public String categoryName;
        public int id;

        public Items(boolean hasSubCategory, String categoryName, int id) {
            this.hasSubCategory = hasSubCategory;
            this.categoryName = categoryName;
            this.id = id;
        }
    }

    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }
    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
}
