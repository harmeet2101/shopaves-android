package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.shop.shopaves.Dialog.CountrySelection;
import com.shop.shopaves.Interface.SelectionCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ShippingAddress extends AppCompatActivity implements Constants{
    private EditText name,phone,address1,address2,city,zipcode,state,country;
    private ProgressDialog pd;
    private AppStore aps;
    private String type="";
    int id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);
        aps = new AppStore(this);

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("SHIPPING ADDRESS");
        name = (EditText) findViewById(R.id.namme);
        phone = (EditText) findViewById(R.id.phone);
        address1 = (EditText) findViewById(R.id.adrs);
        address2 = (EditText) findViewById(R.id.adrs2);
        city = (EditText) findViewById(R.id.city);
        zipcode = (EditText) findViewById(R.id.zip);
        state = (EditText) findViewById(R.id.state);
        country = (EditText) findViewById(R.id.country);

        Intent intent = getIntent();
        if (intent!=null){
            type = intent.getStringExtra(Constants.TYPE);
            if (type!=null && type.equalsIgnoreCase("edit")){
                name.setText(intent.getStringExtra("name"));
                phone.setText(intent.getStringExtra("phone"));
                address1.setText(intent.getStringExtra("address1"));
                address2.setText(intent.getStringExtra("address2"));
                city.setText(intent.getStringExtra("city"));
                zipcode.setText(intent.getStringExtra("zip"));
                state.setText(intent.getStringExtra("state"));
                country.setText(intent.getStringExtra("country"));
                id =intent.getIntExtra("id",0);
            }
        }
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(city.getTag() != null)
                    new CountrySelection(ShippingAddress.this,selectionCallBack,city.getTag().toString(),Constants.CITY).show();
                else
                    Toast.makeText(ShippingAddress.this,"Please select state",Toast.LENGTH_LONG).show();
            }
        });
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(state.getTag() != null)
                    new CountrySelection(ShippingAddress.this,selectionCallBack,state.getTag().toString(),Constants.STATE).show();
                else
                    Toast.makeText(ShippingAddress.this,"Please select country",Toast.LENGTH_LONG).show();
            }
        });
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CountrySelection(ShippingAddress.this,selectionCallBack,"",Constants.COUNTRY).show();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.deliver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidForm()){
                    JSONObject map = new JSONObject();
                    try {
                        map.put(NAME,name.getText().toString());
                        map.put(MOBILE,phone.getText().toString());
                        map.put(ADDRESS,address1.getText().toString());
                        map.put(ADDRESSLINE2,address2.getText().toString());
                        map.put(CITY,city.getText().toString());
                        map.put(STATE,state.getText().toString());
                        map.put(ZIPCODE,zipcode.getText().toString());
                        map.put(COUNTRY,country.getText().toString());
                        map.put(USERID,aps.getData(Constants.USER_ID));
                        if (type != null && type.equalsIgnoreCase("edit"))
                            map.put(ID,id);

                        Net.makeRequest(C.APP_URL+ ApiName.ADD_ADDRESS,map.toString(),collection_response,error);
                        Log.e("address_param",map.toString());
                        pd = C.getProgressDialog(ShippingAddress.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private SelectionCallBack selectionCallBack = new SelectionCallBack() {
        @Override
        public void selection(String id, String name,String type) {
            if(id != null && name != null && type != null){
                if(type.equals(Constants.COUNTRY)){
                    state.setTag(id);
                    country.setText(name);
                }
                else if(type.equals(Constants.STATE)) {
                    city.setTag(id);
                    state.setText(name);
                }else{
                    city.setText(name);
                }
            }

        }
    };
    private Response.ErrorListener error = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(ShippingAddress.this, "" + VolleyErrors.setError(volleyError), Toast.LENGTH_LONG).show();
        }
    };

    private Response.Listener<JSONObject> collection_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("address_response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                Toast.makeText(ShippingAddress.this, "" + model.getMessage(), Toast.LENGTH_LONG).show();
//                startActivity(new Intent(ShippingAddress.this,DeliveryActivity.class));
                setResult(RESULT_OK);
                finish();
            }
        }
    };

    private boolean isValidForm(){
        boolean b =true;
        if (TextUtils.isEmpty(name.getText().toString().trim())){
            Toast.makeText(this,"Please enter name",Toast.LENGTH_SHORT).show();
            b = false;
        }else if (TextUtils.isEmpty(phone.getText().toString().trim())){
            Toast.makeText(this,"Please enter phone number",Toast.LENGTH_SHORT).show();
            b = false;
        }else if (TextUtils.isEmpty(address1.getText().toString().trim())){
            Toast.makeText(this,"Please enter address",Toast.LENGTH_SHORT).show();
            b = false;
        }else if (TextUtils.isEmpty(address2.getText().toString().trim())){
            Toast.makeText(this,"Please enter address line 2",Toast.LENGTH_SHORT).show();
            b = false;
        }else if (TextUtils.isEmpty(city.getText().toString().trim())){
            Toast.makeText(this,"Please enter city",Toast.LENGTH_SHORT).show();
            b = false;
        }else if (TextUtils.isEmpty(state.getText().toString().trim())){
            Toast.makeText(this,"Please enter state",Toast.LENGTH_SHORT).show();
            b = false;
        }else if (TextUtils.isEmpty(zipcode.getText().toString().trim())){
            Toast.makeText(this,"Please enter zipcode",Toast.LENGTH_SHORT).show();
            b = false;
        }else if (TextUtils.isEmpty(country.getText().toString().trim())){
            Toast.makeText(this,"Please enter country",Toast.LENGTH_SHORT).show();
            b = false;
        }
        return b;
    }
}
