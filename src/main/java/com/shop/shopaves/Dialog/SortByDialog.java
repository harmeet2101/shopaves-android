package com.shop.shopaves.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.shop.shopaves.Interface.ValueCallBack;
import com.shop.shopaves.R;

/**
 * Created by amsyt005 on 27/12/16.
 */
public class SortByDialog extends Dialog implements View.OnClickListener{
    private ImageView popularityIcon,hightoLowIcon,lowToHeighIcon,newestIcon;
    private ValueCallBack valueCallBack;
    private String defaultSortBy = "1";

    public SortByDialog(Context context,ValueCallBack valueCallBack,String defaultSortBy) {
        super(context, R.style.Theme_Dialog);
        this.valueCallBack = valueCallBack;
        this.defaultSortBy = defaultSortBy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort_by_dialog);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        setCanceledOnTouchOutside(true);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        popularityIcon = (ImageView)findViewById(R.id.popularityicon);
        hightoLowIcon = (ImageView)findViewById(R.id.hightolowicon);
        lowToHeighIcon = (ImageView)findViewById(R.id.lowtogighicon);
        newestIcon = (ImageView)findViewById(R.id.newesticon);
        findViewById(R.id.popular).setOnClickListener(this);
        findViewById(R.id.pricehightolow).setOnClickListener(this);
        findViewById(R.id.pricelowtoheigh).setOnClickListener(this);
        findViewById(R.id.newestfirst).setOnClickListener(this);
        setDefaultSelection();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.popular:
                defaultSortBy = "1";
                setDefaultSelection();
               // popularityIcon.setImageResource(R.drawable.selecton);
                valueCallBack.callBack("1");
                dismiss();
                break;
            case R.id.pricehightolow:
                defaultSortBy = "2";
                setDefaultSelection();
              //  hightoLowIcon.setImageResource(R.drawable.selecton);
                valueCallBack.callBack("2");
                dismiss();
                break;
            case R.id.pricelowtoheigh:
                defaultSortBy = "3";
                setDefaultSelection();
               // lowToHeighIcon.setImageResource(R.drawable.selecton);
                valueCallBack.callBack("3");
                dismiss();
                break;
            case R.id.newestfirst:
                defaultSortBy = "4";
                setDefaultSelection();
               // newestIcon.setImageResource(R.drawable.selecton);
                valueCallBack.callBack("4");
                dismiss();
                break;
        }
    }

    private void setDefaultSelection(){
        popularityIcon.setImageResource(R.drawable.selectoff);
        lowToHeighIcon.setImageResource(R.drawable.selectoff);
        hightoLowIcon.setImageResource(R.drawable.selectoff);
        newestIcon.setImageResource(R.drawable.selectoff);

       // setDefaultSelection();
        if(defaultSortBy.equals("1"))
        popularityIcon.setImageResource(R.drawable.selecton);
        else if(defaultSortBy.equals("2"))
            hightoLowIcon.setImageResource(R.drawable.selecton);
        else if(defaultSortBy.equals("3"))
            lowToHeighIcon.setImageResource(R.drawable.selecton);
        else
            newestIcon.setImageResource(R.drawable.selecton);
    }
}
