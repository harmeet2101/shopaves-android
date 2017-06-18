package com.shop.shopaves.Fragments;


import android.content.Intent;
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

import com.shop.shopaves.Activity.LoginActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.dataModel.EnterTagData;

import cn.lankton.flowlayout.FlowLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnterTags extends Fragment {
    private FlowLayout gridView;
    private EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_enter_tags, container, false);
        MyApp.getInstance().trackScreenView("Enter tag screen");

        C.applyTypeface(C.getParentView(view.findViewById(R.id.tagslayout)), C.getHelveticaNeueFontTypeface(getActivity()));
        editText = (EditText) view.findViewById(R.id.entertag);
        gridView = (FlowLayout) view.findViewById(R.id.tag_flowlayout);

        //Toast.makeText(getActivity(),"id"+ EnterTagData.findById(EnterTagData.class, 2).TAG_ID,Toast.LENGTH_LONG).show();
        for(int i = 1; i<= EnterTagData.count(EnterTagData.class); i++){
            addView(EnterTagData.findById(EnterTagData.class, i).TAG, EnterTagData.findById(EnterTagData.class, i).TAG_VALUE);
        }
        view.findViewById(R.id.move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAdded = false;
                if (!TextUtils.isEmpty(editText.getText().toString().trim())){
                    for(int i = 1; i<= EnterTagData.count(EnterTagData.class); i++){
                        if(EnterTagData.findById(EnterTagData.class, i).TAG.equalsIgnoreCase(editText.getText().toString().trim())){
                            Toast.makeText(getActivity(),"Already added",Toast.LENGTH_LONG).show();
                            isAdded = true;
                            break;
                        }
                    }
                    if(!isAdded){
                        addView(editText.getText().toString(),"1");
                        new EnterTagData(editText.getText().toString(),"1","").save();
                        editText.setText("");
                    }
                }else{
                    Toast.makeText(getActivity(),"Please enter tag name.",Toast.LENGTH_LONG).show();
                }
            }
        });

        view.findViewById(R.id.h_tx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });


       /* if(!(EnterTagData.count(EnterTagData.class) > 0)){
            for(int i = 0; i< C.TAG_NAMES.length;i++){
                new EnterTagData(C.TAG_NAMES[i],"0").save();
                addView(C.TAG_NAMES[i],"0");
            }
        }*/

        return view;
    }

    private void addView(String tag, final String TAG_VALUE){
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
                EnterTagData TAG_DATA = EnterTagData.findById(EnterTagData.class,gridView.indexOfChild(add_view)+1);
                lv.setBackgroundResource(lv.getTag().toString().equals("0") ? R.drawable.orange_bttn : R.drawable.light_bluebtn );
                img.setImageResource(lv.getTag().toString().equals("0") ? R.drawable.ok_tick : R.drawable.add );
                TAG_DATA.TAG_VALUE = lv.getTag().equals("0") ? "1" : "0";
                lv.setTag(lv.getTag().toString().equals("0") ? "1" : "0");
                TAG_DATA.save();
            }
        });
        gridView.addView(add_view);
    }

}
