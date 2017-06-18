package com.shop.shopaves.Fragments;


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
import com.shop.shopaves.dataModel.GenderInfo;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectGender extends Fragment implements View.OnClickListener {
    private LinearLayout female,male;
    private ImageView malecheck,femalecheck;
    private TextView textView,textView2;
    private boolean isMale = false;


    public SelectGender() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_gender, container, false);
        MyApp.getInstance().trackScreenView("Select gender screen");

        C.applyTypeface(C.getParentView(view.findViewById(R.id.genderselect)), C.getHelveticaNeueFontTypeface(getContext()));
        female = (LinearLayout) view.findViewById(R.id.female_prodct);
        male = (LinearLayout) view.findViewById(R.id.male_prodct);
        textView = (TextView) view.findViewById(R.id.f_text);
        textView2 = (TextView) view.findViewById(R.id.m_text);
        malecheck = (ImageView) view.findViewById(R.id.male_check);
        femalecheck = (ImageView) view.findViewById(R.id.female_check);
        female.setOnClickListener(this);
        male.setOnClickListener(this);
        new GenderInfo("FEMALE").save();

        if(GenderInfo.findById(GenderInfo.class, 1).GENDER.equals("FEMALE")){
            defaultradiobtn();
            femalecheck.setImageResource(R.drawable.check);
            textView.setTextColor(getResources().getColor(R.color.white));
        }else{
            defaultradiobtn();
            malecheck.setImageResource(R.drawable.check);
            textView2.setTextColor(getResources().getColor(R.color.white));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == female.getId() ) {
           /* GenderInfo gender =  GenderInfo.findById(GenderInfo.class, 1);
               gender.GENDER = "MAIL";
                gender.save();
*/
            defaultradiobtn();
            femalecheck.setImageResource(R.drawable.check);
            textView.setTextColor(getResources().getColor(R.color.white));
            isMale = false;
            saveGenderInfo();
        } else if (v.getId() == male.getId()) {
            defaultradiobtn();
            malecheck.setImageResource(R.drawable.check);
            textView2.setTextColor(getResources().getColor(R.color.white));
            isMale = true;
            saveGenderInfo();
        }
    }

    private void defaultradiobtn(){
        malecheck.setImageResource(R.drawable.uncheck);
        femalecheck.setImageResource(R.drawable.uncheck);
        textView.setTextColor(getResources().getColor(R.color.fade_white));
        textView2.setTextColor(getResources().getColor(R.color.fade_white));
    }

    public void saveGenderInfo(){
        GenderInfo gender = GenderInfo.findById(GenderInfo.class, 1);
        if(isMale) {
            gender.GENDER = "MALE";
        }else {
            gender.GENDER = "FEMAIL";
        }
        gender.save();
    }
}
