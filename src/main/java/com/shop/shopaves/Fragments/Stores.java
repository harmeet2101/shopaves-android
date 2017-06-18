package com.shop.shopaves.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.StoresData;

/**
 * A simple {@link Fragment} subclass.
 */
public class Stores extends Fragment {
    private GridView gridView;
    private  StoreAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stores, container, false);
        MyApp.getInstance().trackScreenView("Select store screen");

        gridView = (GridView) view.findViewById(R.id.stores_gridview);
        adapter = new StoreAdapter();
        if(StoresData.count(StoresData.class) > 1){
            for(int i = 1; i<= StoresData.count(StoresData.class); i++){
                adapter.add(new StoreItem(StoresData.findById(StoresData.class, i).IS_STORE_SELECTED, StoresData.findById(StoresData.class, i).storeImage));
            }
        }
        gridView.setAdapter(adapter);
        return view;
    }


    /*

    private void getStoreInfo(){
        for(int i = 0; i <= 2; i++){
            StoresData cat_data = new StoresData(false);
            cat_data.save();
            adapter.add(new StoreItem(false));
        }
    }
*/

    private class StoreAdapter extends ArrayAdapter<StoreItem> implements Constants {
        MyHolder holder ;
        public StoreAdapter() {
            super(getActivity(), R.layout.single_store_row);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                holder =null;
                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                row = inflater.inflate(R.layout.single_store_row, parent, false);
                holder = new MyHolder();
                holder.storeImage = (ImageView) row.findViewById(R.id.store_img);
                holder.storeLayout = (LinearLayout)row.findViewById(R.id.store_view);
                holder.checkImg=(ImageView)row.findViewById(R.id.check_store);
                row.setTag(holder);
            }
            else {

                holder = (MyHolder) row.getTag();
            }

            final StoreItem storeItem = getItem(position);
//            holder.storeImage.setImageResource(STORES_IMG_ID[position]);
            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(storeItem.storeImage)).into(holder.storeImage);

            //Picasso.with(getActivity()).load(C.ASSET_URL+storeItem.storeImage).into(holder.storeImage);
            holder.storeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storeItem.itemChecked();
                    updateCategoryValue(position,storeItem);
                    adapter.notifyDataSetChanged();
                }
            });
            holder.storeLayout.setBackgroundResource(storeItem.isChecked()? R.drawable.selected_back: R.drawable.unselected_back);
            holder.checkImg.setImageResource(storeItem.isChecked()? R.drawable.check: R.drawable.unchecked);
            return row;
        }
    }

    private static class MyHolder{
        ImageView storeImage;
        LinearLayout storeLayout;
        ImageView checkImg;
    }

    private class StoreItem{
        boolean isChecked=false;
        String storeImage;

        public StoreItem(boolean isChecked,String storeImage) {
            this.isChecked = isChecked;
            this.storeImage = storeImage;
        }

        public boolean isChecked(){
            return isChecked;
        }
        public void itemChecked(){
            isChecked = !isChecked;
        }

    }

    private void updateCategoryValue(int position ,StoreItem item){
        StoresData update_data = StoresData.findById(StoresData.class, position+1);
        update_data.IS_STORE_SELECTED = item.isChecked;
        update_data.save();
    }




}
