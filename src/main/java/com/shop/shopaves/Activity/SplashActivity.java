package com.shop.shopaves.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.dataModel.CityData;
import com.shop.shopaves.dataModel.ColorData;
import com.shop.shopaves.dataModel.EnterTagData;
import com.shop.shopaves.dataModel.SelectCategoryData;
import com.shop.shopaves.dataModel.StoresData;
import com.shop.shopaves.dataModel.StyleData;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{

    private AppStore aps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MyApp.getInstance().trackScreenView("Splash screen");
        aps = new AppStore(this);
        Map<String,String> map = new HashMap<>();
        if(SelectCategoryData.count(SelectCategoryData.class) > 0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try{
                        if(!TextUtils.isEmpty(aps.getData(Constants.IS_PRESTEPS_COMPLETE)) && aps.getData(Constants.IS_PRESTEPS_COMPLETE)!=null && aps.getData(Constants.IS_PRESTEPS_COMPLETE).equalsIgnoreCase("YES")){
                            Intent i = new Intent(SplashActivity.this,HomeActivity.class);
                            startActivity(i);
                            finish();
                        }else{
                            Intent i = new Intent(SplashActivity.this,HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }, 3000);
        }else{

           Net.makeRequest(C.APP_URL+ ApiName.GET_PRE_STEPS,map,this,this);

        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(SplashActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
        Log.e("error",volleyError.toString());

    }

    @Override
    public void onResponse(JSONObject jsonObject) {

        Log.e("Result",jsonObject.toString());
        if(jsonObject != null){
            try {
                Model model = new Model(jsonObject);
                Model TAG_ARRAY[] = model.getTagArray();
                Model CITY_ARRAY[] = model.getCityArray();
                Model STYLE_ARRAY[] = model.getStyleArray();
                Model COLOR_ARRAY[] = model.getColorArray();
                Model CATEGORY_ARRAY[] = model.getCategoryArray();
                Model STORE_ARRAY[] = model.getStoresArray();

                for(int i = 0; i<TAG_ARRAY.length; i++){
                    new EnterTagData(TAG_ARRAY[i].getName(),"0",""+TAG_ARRAY[i].getId()).save();
                }
                for(int i = 0; i<CITY_ARRAY.length; i++)
                    new CityData(CITY_ARRAY[i].getName(),"0",""+CITY_ARRAY[i].getId()).save();

                for(int i = 0; i<STYLE_ARRAY.length; i++)
                    new StyleData(STYLE_ARRAY[i].getName(),"0",""+STYLE_ARRAY[i].getId()).save();

                for(int i = 0; i<CATEGORY_ARRAY.length; i++)
                    new SelectCategoryData(CATEGORY_ARRAY[i].getName(),CATEGORY_ARRAY[i].getIcon(),false,""+CATEGORY_ARRAY[i].getId()).save();

               for(int i = 0; i<COLOR_ARRAY.length; i++)
                    new ColorData(COLOR_ARRAY[i].getName(),"0",COLOR_ARRAY[i].getColorCode(),""+COLOR_ARRAY[i].getId()).save();

                for (int i = 0; i < STORE_ARRAY.length; i++)
                    new StoresData(""+STORE_ARRAY[i].getId(), STORE_ARRAY[i].getName(), STORE_ARRAY[i].getImage(),false).save();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent i = new Intent(SplashActivity.this,SignupStepActivity.class);
        startActivity(i);
        finish();
    }
}
