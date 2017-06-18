package com.shop.shopaves.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.SelectCategoryData;

public class Categories extends Fragment {
    private GridView gridView;
    private  GridviewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        MyApp.getInstance().trackScreenView("Category screen");

        C.applyTypeface(C.getParentView(view.findViewById(R.id.category_view)), C.getHelveticaNeueFontTypeface(getActivity()));
        gridView = (GridView) view.findViewById(R.id.categories_gridview);
        adapter = new GridviewAdapter();

        if(SelectCategoryData.count(SelectCategoryData.class) > 1){
            for(int i = 1; i<= SelectCategoryData.count(SelectCategoryData.class); i++){
                adapter.add(new Item(SelectCategoryData.findById(SelectCategoryData.class, i).Category_name, SelectCategoryData.findById(SelectCategoryData.class, i).categoryImage, SelectCategoryData.findById(SelectCategoryData.class, i).IS_CATEGORY_SELECTED));
            }
        }else{
           // getCategoryInfo();
        }
        gridView.setAdapter(adapter);
        return view;
    }

    private class GridviewAdapter extends ArrayAdapter<Item> implements Constants {

        ItemHolder holder ;
        public GridviewAdapter() {
            super(getActivity(), R.layout.single_category_itm);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row =convertView;
            holder = null;
            if (row == null) {
                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                row = inflater.inflate(R.layout.single_category_itm, parent, false);
                holder = new ItemHolder();
                holder.itemTitle = (TextView) row.findViewById(R.id.itemtitle);
                holder.imageItem = (ImageView) row.findViewById(R.id.img);
                holder.product = (LinearLayout)row.findViewById(R.id.product_view);
                holder.checkradio=(ImageView)row.findViewById(R.id.checkitem);
                row.setTag(holder);
            }
            else {
                holder = (ItemHolder) row.getTag();
            }
            final Item item = getItem(position);

            holder.product.setTag(position);
            holder.itemTitle.setText(item.getCategoryName());
          //  Picasso.with(getActivity()).load(C.ASSET_URL+item.categoryImage).into(holder.imageItem);
            PicassoCache.getPicassoInstance(getActivity()).load(C.getImageUrl(item.categoryImage)).resize(150,150).into(holder.imageItem);

            holder.product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.itemChecked();
                    updateCategoryValue(position,item);
                    adapter.notifyDataSetChanged();
                }
            });
            holder.product.setBackgroundResource(item.isChecked()? R.drawable.selected_back: R.drawable.unselected_back);
            holder.checkradio.setImageResource(item.isChecked()? R.drawable.check: R.drawable.unchecked);
            return row;
        }
    }

    static class ItemHolder{
        TextView itemTitle;
        ImageView imageItem;
        LinearLayout product;
        ImageView checkradio;
    }
    public class Item{
        boolean isChecked=false;
        public String category_name;
        public String categoryImage;

        public Item(String category_name,String categoryImage,boolean isChecked) {
            this.category_name = category_name;
            this.isChecked = isChecked;
            this.categoryImage = categoryImage;
        }

        public boolean isChecked(){
            return isChecked;
        }
        public void itemChecked(){
            isChecked = !isChecked;
        }

        public String getCategoryName(){
            return  category_name;
        }
    }
    private void updateCategoryValue(int position ,Item item){
        SelectCategoryData update_data = SelectCategoryData.findById(SelectCategoryData.class, position+1);
        update_data.IS_CATEGORY_SELECTED = item.isChecked;
        update_data.save();
    }
}
