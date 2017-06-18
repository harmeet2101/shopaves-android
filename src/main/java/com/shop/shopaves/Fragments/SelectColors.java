package com.shop.shopaves.Fragments;


import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.dataModel.ColorData;

import cn.lankton.flowlayout.FlowLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectColors extends Fragment {
    private FlowLayout gridView;
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_colors, container, false);
        MyApp.getInstance().trackScreenView("Select color screen");

        C.applyTypeface(C.getParentView(view.findViewById(R.id.selectcolorview)), C.getHelveticaNeueFontTypeface(getActivity()));
        gridView = (FlowLayout) view.findViewById(R.id.colr_flowlayout);

            for(int i = 1; i<= ColorData.count(ColorData.class); i++){
                if(ColorData.findById(ColorData.class, i).COLOR_CODE != null)
                addView(ColorData.findById(ColorData.class, i).TAG_NAME, ColorData.findById(ColorData.class, i).COLOR_CODE,i-1, ColorData.findById(ColorData.class, i).TAG_VALUE);

        }
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void addView(String tag, String COLOR_CODE, int position, String TAG_VALUE){
        final View added_view = getActivity().getLayoutInflater().inflate(R.layout.color_flow_child,null);
        final LinearLayout lv = (LinearLayout) added_view.findViewById(R.id.color_parent);
        TextView textView = (TextView) added_view.findViewById(R.id.color_name);
        Drawable drawable = getActivity().getResources().getDrawable(R.drawable.purple_bttn);
        drawable.setColorFilter(Color.parseColor(COLOR_CODE.trim()), PorterDuff.Mode.SRC_ATOP);
        lv.setBackground(drawable);
        textView.setText(tag);
        if(COLOR_CODE.equalsIgnoreCase("#ffffff"))
            textView.setTextColor(Color.BLACK);
        final ImageView img = (ImageView) added_view.findViewById(R.id.select);

        if(position == 10){
           textView.setTextColor(Color.parseColor("#000000"));
        }
        img.setVisibility(TAG_VALUE.equals("1") ? View.VISIBLE : View.GONE );

        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorData colorData = ColorData.findById(ColorData.class,gridView.indexOfChild(added_view)+1);
                img.setVisibility(lv.getTag().toString().equals("0") ? View.VISIBLE : View.GONE );
                colorData.TAG_VALUE = lv.getTag().toString().equals("0") ? "1" : "0";
                lv.setTag(lv.getTag().toString().equals("0") ? "1" : "0");
                colorData.save();
            }
        });

        gridView.addView(added_view);
    }

}
