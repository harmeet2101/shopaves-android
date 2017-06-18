package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.shop.shopaves.Util.NamePair;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ShippingSelect extends AppCompatActivity implements View.OnClickListener,Response.Listener<JSONObject>,Response.ErrorListener{

    private TextView shippingType;
    private ProgressDialog pd;
    private String selectedId = "";
    private JSONArray flat,calculated;
    private String typeBasedService = "";
    private EditText cost;
    private TextView selectedService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_select);
        shippingType = (TextView)findViewById(R.id.typename);
        cost = (EditText)findViewById(R.id.ammount);
        selectedService = (TextView)findViewById(R.id.service);
        ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.shipping_c));
        ((TextView)findViewById(R.id.toright)).setText(getResources().getString(R.string.save));
        ((TextView)findViewById(R.id.toright)).setVisibility(View.VISIBLE);
        findViewById(R.id.selectshippingtype).setOnClickListener(this);
        findViewById(R.id.toright).setOnClickListener(this);
        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.selectservice).setOnClickListener(this);
        setShippingInfo();
        makeShippingTypeRequest();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.selectshippingtype:
                startActivityForResult(new Intent(ShippingSelect.this,SelectItemActivity.class).putExtra("CONTENT_NAME","SHIPPING"),100);
                break;
            case R.id.toright:
                setResult(888,new Intent().putExtra(Constants.SHIPPING_SELECTION,shippingSelection()));
                finish();
                break;
            case R.id.back_addrss:
                finish();
                break;
            case R.id.selectservice:
                if(selectedId.equals("0"))
                    typeBasedService = flat.toString();
                else if(selectedId.equals("1"))
                    typeBasedService = calculated.toString();

                startActivityForResult(new Intent(ShippingSelect.this,SelectItemActivity.class).putExtra("CONTENT_NAME","SHIPPING_SERVICE").putExtra(Constants.DATA,typeBasedService),100);
                break;
        }
    }

    private void setShippingInfo(){
        if(!TextUtils.isEmpty(getIntent().getStringExtra(Constants.TYPE)))
        shippingType.setText(getIntent().getStringExtra(Constants.TYPE));
        if(!TextUtils.isEmpty(getIntent().getStringExtra(Constants.SELECT_SERVICE)))
            selectedService.setText(getIntent().getStringExtra(Constants.SELECT_SERVICE));
           if(!TextUtils.isEmpty(getIntent().getStringExtra(Constants.COST)))
               cost.setText(getIntent().getStringExtra(Constants.COST));
        if(!TextUtils.isEmpty(getIntent().getStringExtra(Constants.TYPE))){
            if(getIntent().getStringExtra(Constants.TYPE).equals(Constants.SHIPPING_TYPE[0]))
                selectedId = "0";
             else if(getIntent().getStringExtra(Constants.TYPE).equals(Constants.SHIPPING_TYPE[1]))
                selectedId =  "1";
            else {
                selectedId = "2";
                findViewById(R.id.serviceview).setVisibility(View.GONE);
            }
        }

    }
    private String shippingSelection(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("shippingType",shippingType.getText().toString());
            jsonObject.put("shippingDays",selectedService.getText().toString().equals("Select Service") ? "" : selectedService.getText().toString());
            jsonObject.put("isShippFree",selectedId.equals("3") ? true : false);
            jsonObject.put("shippingCost",cost.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    private void makeShippingTypeRequest(){
        pd = C.getProgressDialog(this);
        HashMap<String,String> hashMap = new HashMap<>();
        Net.makeRequest(C.APP_URL+ ApiName.GET_SHIPPING_TYPE_API,hashMap,this,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            shippingType.setText(data.getStringExtra("NAME"));
            selectedId = data.getStringExtra("id");
            findViewById(R.id.serviceview).setVisibility(selectedId.equals("2") ? View.GONE : View.VISIBLE);
        }else if(resultCode == 200){
            selectedService.setText(data.getStringExtra("NAME"));
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        Model data = new Model(model.getData());
        Log.e("ShippingTypeResponse",jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                flat = data.getJsonFlatArray();
                calculated = data.getJsonCalculatedArray();
        }

    }
}
