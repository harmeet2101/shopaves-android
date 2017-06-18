package com.shop.shopaves.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.R;

public class WeightAndSizeActivity extends AppCompatActivity implements View.OnClickListener {
    EditText wt,len,wid,heit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_and_size);
        TextView select = (TextView)findViewById(R.id.toright);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("WEIGHT & SIZE");
        select.setVisibility(View.VISIBLE);
        wt = (EditText) findViewById(R.id.titlename);
        len = (EditText) findViewById(R.id.len);
        wid = (EditText) findViewById(R.id.wid);
        heit = (EditText) findViewById(R.id.heit);

        Intent intent = getIntent();
        if (intent!=null){
            wt.setText(intent.getStringExtra("weight"));
            len.setText(intent.getStringExtra("length"));
            wid.setText(intent.getStringExtra("width"));
            heit.setText(intent.getStringExtra("height"));
        }

        C.applyTypeface(C.getParentView(findViewById(R.id.wtdimensions)), C.getHelveticaNeueFontTypeface(WeightAndSizeActivity.this));

        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.toright).setVisibility(View.VISIBLE);
        findViewById(R.id.toright).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_addrss:
                finish();
                break;
            case R.id.toright:
                if (TextUtils.isEmpty(wt.getText().toString().trim())){
                    Toast.makeText(this,"Please enter weight",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(len.getText().toString().trim())){
                    Toast.makeText(this,"Please enter length",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(wid.getText().toString().trim())){
                Toast.makeText(this,"Please enter width",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(heit.getText().toString().trim())){
                    Toast.makeText(this,"Please enter height",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("Weight",wt.getText().toString().trim());
                    intent.putExtra("length",len.getText().toString().trim());
                    intent.putExtra("width",wid.getText().toString().trim());
                    intent.putExtra("height",heit.getText().toString().trim());
                    setResult(RESULT_OK,intent);
                    finish();
                }
                break;
        }
    }
}
