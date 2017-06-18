package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactUsActivity extends AppCompatActivity {
    EditText name,email,contact,message;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.contact_us));

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        contact = (EditText) findViewById(R.id.phon);
        message = (EditText) findViewById(R.id.msg);

        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.addrss_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUsRequest();
            }
        });
    }

    private void contactUsRequest(){
        if (isValidForm()){
            JSONObject map = new JSONObject();
            try {
                map.put("name",name.getText().toString());
                map.put("email",email.getText().toString());
                map.put("message",message.getText().toString());
                map.put("contactNumber",contact.getText().toString());
                Net.makeRequest(C.APP_URL+ ApiName.CONTACTUS,map.toString(),r,e);
                Log.e("contactus=",map.toString());
                pd = C.getProgressDialog(this);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean isValidForm(){
        boolean b = true;
        if (TextUtils.isEmpty(name.getText().toString().trim())){
            Toast.makeText(this,"Please enter name",Toast.LENGTH_SHORT).show();
            b= false ;
        }else if(TextUtils.isEmpty(contact.getText().toString().trim())){
            Toast.makeText(this,"Please enter contact",Toast.LENGTH_SHORT).show();
            b= false ;
        }else if(TextUtils.isEmpty(email.getText().toString().trim())){
            Toast.makeText(this,"Please enter email id",Toast.LENGTH_SHORT).show();
            b= false ;
        }else if(!C.isValidEmail(email.getText().toString().trim())){
            Toast.makeText(this,"Please enter Valid email id",Toast.LENGTH_SHORT).show();
            b= false ;
        }else if(TextUtils.isEmpty(message.getText().toString().trim())){
            Toast.makeText(this,"Please enter message",Toast.LENGTH_SHORT).show();
            b= false ;
        }
        return b;
    }
    public Response.ErrorListener e = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(ContactUsActivity.this, "" + VolleyErrors.setError(volleyError), Toast.LENGTH_LONG).show();
        }
    };

    public Response.Listener<JSONObject> r = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("address_response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                Toast.makeText(ContactUsActivity.this, "" + model.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    };
}
