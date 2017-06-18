package com.shop.shopaves.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.dataModel.CityData;

import cn.lankton.flowlayout.FlowLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectCities extends Fragment {
    private FlowLayout gridView;
    private EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_cities, container, false);
        MyApp.getInstance().trackScreenView("Select city screen");

        C.applyTypeface(C.getParentView(view.findViewById(R.id.selectcity_layout)), C.getHelveticaNeueFontTypeface(getActivity()));
        editText = (EditText) view.findViewById(R.id.enter_cities);
        gridView = (FlowLayout) view.findViewById(R.id.flowlayout);

        for(int i = 1; i<= CityData.count(CityData.class); i++){
            addView(CityData.findById(CityData.class, i).cityname, CityData.findById(CityData.class, i).TAG_VALUE);
        }
        view.findViewById(R.id.move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAdded = false;

                if (!TextUtils.isEmpty(editText.getText().toString().trim())){

                    for(int i = 1; i<= CityData.count(CityData.class); i++){
                        if(CityData.findById(CityData.class, i).cityname.equalsIgnoreCase(editText.getText().toString().trim())){
                            Toast.makeText(getActivity(),"Already added",Toast.LENGTH_LONG).show();
                            isAdded = true;
                            break;
                        }
                    }
                    if(!isAdded){
                        addView(editText.getText().toString(),"1");
                        new CityData(editText.getText().toString(),"1","").save();
                        editText.setText("");
                    }

                }
            }
        });

        return view;
    }

    private void addView(String tag,String TAG_VALUE){
        final View add_view = getActivity().getLayoutInflater().inflate(R.layout.city_flow_child,null);
        final LinearLayout lv = (LinearLayout) add_view.findViewById(R.id.parent);
        TextView textView = (TextView) add_view.findViewById(R.id.text);
        textView.setText(tag);
        final ImageView img = (ImageView) add_view.findViewById(R.id.check);
        lv.setBackgroundResource(TAG_VALUE.equals("1") ? R.drawable.orange_bttn : R.drawable.light_bluebtn );
        img.setImageResource(TAG_VALUE.equals("1") ? R.drawable.ok_tick : R.drawable.add );

        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityData CITY_DATA = CityData.findById(CityData.class,gridView.indexOfChild(add_view)+1);
                lv.setBackgroundResource(lv.getTag().toString().equals("0") ? R.drawable.orange_bttn : R.drawable.light_bluebtn );
                img.setImageResource(lv.getTag().toString().equals("0") ? R.drawable.ok_tick : R.drawable.add );
                CITY_DATA.TAG_VALUE = lv.getTag().toString().equals("0") ? "1" : "0";
                lv.setTag(lv.getTag().toString().equals("0") ? "1" : "0");
                CITY_DATA.save();
            }
        });

        gridView.addView(add_view);
    }

}
