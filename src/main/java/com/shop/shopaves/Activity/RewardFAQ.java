package com.shop.shopaves.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.shop.shopaves.R;

public class RewardFAQ extends AppCompatActivity implements View.OnClickListener{

    private TextView standard_details,set_details,asdf_details,aslal_details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_faq);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.reward_faq));
        standard_details = (TextView)findViewById(R.id.details);
        set_details = (TextView)findViewById(R.id.set_details);
        asdf_details = (TextView)findViewById(R.id.asdf_details);
        aslal_details = (TextView)findViewById(R.id.aslal_description);
        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.stand_click).setOnClickListener(this);
        findViewById(R.id.asdf).setOnClickListener(this);
        findViewById(R.id.aslalkaba).setOnClickListener(this);
        findViewById(R.id.setu_click).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_addrss:
                finish();
                break;
            case R.id.stand_click:
              //  setDefault();
                standard_details.setVisibility(standard_details.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.asdf:
              //  setDefault();
                asdf_details.setVisibility(asdf_details.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.aslalkaba:
              //  setDefault();
                aslal_details.setVisibility(aslal_details.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.setu_click:
              //  setDefault();
                set_details.setVisibility(set_details.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                break;
        }
    }

    private void setDefault(){
        standard_details.setVisibility(View.GONE);
        set_details.setVisibility(View.GONE);
        asdf_details.setVisibility(View.GONE);
        aslal_details.setVisibility(View.GONE);
    }
}
