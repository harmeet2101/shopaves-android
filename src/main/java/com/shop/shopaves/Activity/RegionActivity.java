package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import org.json.JSONObject;

import java.util.HashMap;

public class RegionActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONObject>{

    private TextView countryText,cityText,stateText;
    private ProgressDialog pd;
    private String type = Constants.SAVE_REGION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.selectregion));
        countryText = (TextView)findViewById(R.id.country);
        stateText = (TextView)findViewById(R.id.state);
        cityText = (TextView)findViewById(R.id.city);
        findViewById(R.id.cityclick).setOnClickListener(this);
        findViewById(R.id.stateclick).setOnClickListener(this);
        findViewById(R.id.countryclick).setOnClickListener(this);
        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        getRegionRequest();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.countryclick:
                new CountrySelection(this,selectionCallBack,"",Constants.COUNTRY).show();
                break;
            case R.id.stateclick:
                if(stateText.getTag() != null)
                new CountrySelection(this,selectionCallBack,stateText.getTag().toString(),Constants.STATE).show();
                else
                    Toast.makeText(RegionActivity.this,"Please select country",Toast.LENGTH_LONG).show();
                break;
            case R.id.cityclick:
                if(cityText.getTag() != null)
                    new CountrySelection(this,selectionCallBack,cityText.getTag().toString(),Constants.CITY).show();
                else
                    Toast.makeText(RegionActivity.this,"Please select state",Toast.LENGTH_LONG).show();
                break;
            case R.id.back_addrss:
                finish();
                break;
            case R.id.save:
                if(countryText.getText().toString().equalsIgnoreCase(Constants.COUNTRY)) {
                    Toast.makeText(RegionActivity.this, "Please select country", Toast.LENGTH_LONG).show();
                    break;
                }else if(stateText.getText().toString().equalsIgnoreCase(Constants.STATE)){
                    Toast.makeText(RegionActivity.this, "Please select state", Toast.LENGTH_LONG).show();
                    break;
                }else if(cityText.getText().toString().equalsIgnoreCase(Constants.CITY)){
                    Toast.makeText(RegionActivity.this, "Please select city", Toast.LENGTH_LONG).show();
                    break;
                }else
                makeRegionRequest();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    private void makeRegionRequest(){
        type = Constants.SAVE_REGION;
        pd = C.getProgressDialog(this);
        JSONObject hashMap = new JSONObject();
        try {
            hashMap.put("userId", new AppStore(this).getData(Constants.USER_ID));
            hashMap.put("country", countryText.getText().toString());
            hashMap.put("state", stateText.getText().toString());
            hashMap.put("city", cityText.getText().toString());
        Net.makeRequest(C.APP_URL+ ApiName.USER_REGION_API,hashMap.toString(),this,this);
        }catch (Exception e){}
    }

    private void getRegionRequest(){
        type = Constants.GET_REGION;
        pd = C.getProgressDialog(this);
        HashMap<String,String> hashMap = new HashMap();
        hashMap.put("userId",new AppStore(this).getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GET_REGION_API,hashMap,this,this);

    }

    private SelectionCallBack selectionCallBack = new SelectionCallBack() {
        @Override
        public void selection(String id, String name, String type) {

            if(id != null && name != null && type != null){
                    switch (type){
                        case Constants.COUNTRY:
                            countryText.setText(name);
                            stateText.setTag(id);
                            break;
                        case Constants.STATE:
                            stateText.setText(name);
                            cityText.setTag(id);
                            break;
                        case Constants.CITY:
                            cityText.setText(name);
                            break;
                    }
            }
        }
    };

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(RegionActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        Log.e("region API response",jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                if(type.equals(Constants.SAVE_REGION)){
                        Toast.makeText(RegionActivity.this,model.getMessage(),Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Model data = new Model(model.getData());
                    cityText.setText(data.getCity());
                    stateText.setText(data.getState());
                    countryText.setText(data.getCountry());
                }
        }
    }
}
