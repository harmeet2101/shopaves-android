package com.shop.shopaves.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shop.shopaves.Interface.FragmentCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.dataModel.NewCategory;

import java.util.List;

/**
 * Created by amsyt005 on 9/1/17.
 */
@SuppressLint("ValidFragment")
public class ParentCategoryFragment extends android.support.v4.app.Fragment {
    private MenuCategoryApapter menuCategoryApapter;
    private ListView categoryMenu;
    private FragmentCallBack fragmentCallBack;
    private View parentView;


    public ParentCategoryFragment() {
    }

    @SuppressLint("ValidFragment")
    public ParentCategoryFragment(FragmentCallBack fragmentCallBack) {
        this.fragmentCallBack = fragmentCallBack;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.parent_category, container, false);
        menuCategoryApapter = new MenuCategoryApapter();
        categoryMenu = (ListView) v.findViewById(R.id.catemenu);
        v.setAnimation(inFromLeftAnimation());
        setMenuListItems();
        parentView = v;
        return v;
    }

    private class MenuCategoryApapter extends ArrayAdapter<CategoryItem> {
        MyHolder holder;
        int iconArray[] = {R.drawable.homefurniture, R.drawable.electronic, R.drawable.lifestyle, R.drawable.jewelry, R.drawable.watches, R.drawable.healthbeauty};

        public MenuCategoryApapter() {
            super(getActivity(), R.layout.listgroup);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            holder = null;
            if (row == null) {
                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                row = inflater.inflate(R.layout.listgroup, parent, false);
                holder = new MyHolder();
                holder.categoryName = (TextView) row.findViewById(R.id.lblListHeader);
                holder.catIcon = (ImageView) row.findViewById(R.id.listimg);
                row.setTag(holder);
            } else {
                holder = (MyHolder) row.getTag();
            }
            final CategoryItem item = getItem(position);
            holder.categoryName.setText(item.name);
            holder.catIcon.setImageResource(iconArray[position]);
            //  PicassoCache.getPicassoInstance(getActivity()).load(C.ASSET_URL+item.catImg).into(holder.catIcon);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.hasSubCat) {
                        parentView.setAnimation(outToLeftAnimation());
                        fragmentCallBack.onFragmentCallBack(new ChildCategoryFragment(fragmentCallBack,null, item.catId, item.name));
                        //  getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

                        //  startActivity(new Intent(getActivity(),SubCategoryActivity.class).putExtra(Constants.PARENT_ID,item.catId).putExtra(Constants.NAME,item.name));
                    }
                }
            });
            // PicassoCache.getPicassoInstance(getActivity()).load(C.ASSET_URL+item.logo).into(holder.brandLogo);

            return row;
        }
    }


    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
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

    class MyHolder {
        public TextView categoryName;
        public ImageView catIcon;
    }

    private void setMenuListItems() {
        List<NewCategory> categoryList = NewCategory.listAll(NewCategory.class);
        for (int i = 0; i < categoryList.size(); i++) {
            NewCategory categoryValues = categoryList.get(i);
            menuCategoryApapter.add(new CategoryItem(categoryValues.categoryId, categoryValues.name, categoryValues.hasSubCategory));
        }
        categoryMenu.setAdapter(menuCategoryApapter);
    }

    public class CategoryItem {
        public int catId;
        public String name;
        public boolean hasSubCat;

        public CategoryItem(int catId, String name, boolean hasSubCat) {
            this.catId = catId;
            this.name = name;
            this.hasSubCat = hasSubCat;
        }
    }


}
