package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Dialog.CountrySelection;
import com.shop.shopaves.Interface.SelectionCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.GPSTracker;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.dataModel.CityData;
import com.shop.shopaves.dataModel.ColorData;
import com.shop.shopaves.dataModel.EnterTagData;
import com.shop.shopaves.dataModel.GenderInfo;
import com.shop.shopaves.dataModel.SelectCategoryData;
import com.shop.shopaves.dataModel.StyleData;
import com.shop.shopaves.network.MultipartRequest;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject> {

    private ProgressDialog pd;
    private EditText address_one, address_two, city, state, zipcode,country;
    private String EMAIL, USERNAME, PHONE_NUMBER, PASSWORD, DOB;
    private String type;
    private String accessToken = "";
    private String type_sign_up = "0";
    private String APP_ID = "";
    private AppStore aps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        MyApp.getInstance().trackScreenView("Address screen");
        pd = new ProgressDialog(this);
        address_one = (EditText) findViewById(R.id.addressfirst);
        address_two = (EditText) findViewById(R.id.addresssecond);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        zipcode = (EditText) findViewById(R.id.zipcode);
        country = (EditText)findViewById(R.id.country);
        aps =new AppStore(this);
        C.applyTypeface(C.getParentView(findViewById(R.id.activity_address)), C.getHelveticaNeueFontTypeface(AddressActivity.this));
        EMAIL = getIntent().getExtras().getString("EMAIL");
        USERNAME = getIntent().getExtras().getString("USERNAME");
        PHONE_NUMBER = getIntent().getExtras().getString("PHONE_NUMBER");
        PASSWORD = getIntent().getExtras().getString("PASSWORD");
        DOB = getIntent().getExtras().getString("DOB");
        accessToken = getIntent().getExtras().getString("ACCESS_TOKEN");
        type_sign_up = getIntent().getExtras().getString("TYPE");
        APP_ID = getIntent().getExtras().getString("APP_ID");

        GPSTracker gpsTracker = new GPSTracker(this);
//        gpsTracker.setLocation(gpsTracker.getLocation().getLatitude(),gpsTracker.getLocation().getLongitude());

        city.setFocusable(false);
        city.setClickable(true);
        state.setFocusable(false);
        state.setClickable(true);
        country.setFocusable(false);
        country.setClickable(true);

        findViewById(R.id.addrss_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidForm()) {
                    pd = C.getProgressDialog(AddressActivity.this);
                    if (!TextUtils.isEmpty(getIntent().getExtras().getString("IMAGE"))) {
                        UploadImage(getIntent().getExtras().getString("IMAGE"));
                    } else {
                        signUpdRequest("", type_sign_up, accessToken);
                    }
                    //  signUpdRequest();
                }
            }
        });


        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       /* if(gpsTracker != null){
            address_one.setText(gpsTracker.getStreet());
            address_two.setText(gpsTracker.getArea());
            state.setText(gpsTracker.getState());
            zipcode.setText(gpsTracker.getPinCode());
        }*/


        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            String con = gpsTracker.getCountryName(this);
            country.setText(con);

            String cities = gpsTracker.getLocality(this);
            city.setText(cities);

            String postalCode = gpsTracker.getPostalCode(this);
            zipcode.setText(postalCode);

            String addressLine = gpsTracker.getAddressLine(this);
            address_one.setText(addressLine);
            address_two.setText(addressLine);
        }
    }

    private void signUpdRequest(String imageUrl, String type_signup, String token) {

        type = Constants.SIGN_UP;
        JSONObject SIGN_UP_ADDRESS_OBJECT = new JSONObject();
        //JSONObject SIGN_UP_CITIES_OBJECT = new JSONObject();
        final JSONObject SIGN_UP_MAP = new JSONObject();
        JSONArray SIGN_UP_CITIES_ARRAY = new JSONArray();
        JSONArray SIGN_UP_COLORS_ARRAY = new JSONArray();
        JSONArray SIGN_UP_TAGS_ARRAY = new JSONArray();
        JSONArray SIGN_UP_STYLE_ARRAY = new JSONArray();
        JSONArray SIGN_UP_CATEGORY_ARRAY = new JSONArray();
        //Map<String,String> CITY_MAP = new HashMap<>();
        try {
            SIGN_UP_ADDRESS_OBJECT.put("addresses", address_one.getText().toString());
            SIGN_UP_ADDRESS_OBJECT.put("addressLine2", address_two.getText().toString());
            SIGN_UP_ADDRESS_OBJECT.put("state", state.getText().toString());
            SIGN_UP_ADDRESS_OBJECT.put("zipCode", zipcode.getText().toString());

/*CITY*/
            List<CityData> list = CityData.listAll(CityData.class);
            for (CityData cd : list) {
                if (cd.TAG_VALUE.equals("1")) {
                    Map CITY_MAP = new HashMap();
                    CITY_MAP.put("name", cd.cityname);
                    CITY_MAP.put("id", cd.TAG_ID);
                    SIGN_UP_CITIES_ARRAY.put(new JSONObject(CITY_MAP));
                }
            }

/*Color*/
            List<ColorData> colorList = ColorData.listAll(ColorData.class);
            for (ColorData cd : colorList) {
                if (cd.TAG_VALUE.equals("1")) {
                    Map COLOR_MAP = new HashMap();
                    COLOR_MAP.put("name", cd.TAG_NAME);
                    COLOR_MAP.put("colorCode", cd.COLOR_CODE);
                    COLOR_MAP.put("id", cd.TAG_ID);
                    SIGN_UP_COLORS_ARRAY.put(new JSONObject(COLOR_MAP));
                }
            }

            /*TAG*/

            List<EnterTagData> tagList = EnterTagData.listAll(EnterTagData.class);
            for (EnterTagData cd : tagList) {
                if (cd.TAG_VALUE.equals("1")) {
                    Map TAG_MAP = new HashMap();
                    TAG_MAP.put("name", cd.TAG);
                    TAG_MAP.put("id", cd.TAG_ID);
                    SIGN_UP_TAGS_ARRAY.put(new JSONObject(TAG_MAP));
                }
            }
/*style*/

            List<StyleData> styleList = StyleData.listAll(StyleData.class);
            for (StyleData cd : styleList) {
                if (cd.TAG_VALUE.equals("1")) {
                    Map STYLE_MAP = new HashMap();
                    STYLE_MAP.put("name", cd.STYLE_TAG);
                    STYLE_MAP.put("id", cd.TAG_ID);
                    SIGN_UP_STYLE_ARRAY.put(new JSONObject(STYLE_MAP));
                }
            }

            /*category*/

            List<SelectCategoryData> categoryList = SelectCategoryData.listAll(SelectCategoryData.class);
            for (SelectCategoryData cd : categoryList) {
                if (cd.IS_CATEGORY_SELECTED) {
                    Map CATEGORY_MAP = new HashMap();
                    CATEGORY_MAP.put("name", cd.Category_name);
                    CATEGORY_MAP.put("id", cd.TAG_ID);
                    SIGN_UP_CATEGORY_ARRAY.put(new JSONObject(CATEGORY_MAP));
                }
            }

            SIGN_UP_MAP.put("address", new JSONArray().put(SIGN_UP_ADDRESS_OBJECT));
            SIGN_UP_MAP.put("cities", SIGN_UP_CITIES_ARRAY);
            SIGN_UP_MAP.put("colors", SIGN_UP_COLORS_ARRAY);
            SIGN_UP_MAP.put("tags", SIGN_UP_TAGS_ARRAY);
            SIGN_UP_MAP.put("styles", SIGN_UP_STYLE_ARRAY);
            SIGN_UP_MAP.put("intrusts", SIGN_UP_CATEGORY_ARRAY);
            SIGN_UP_MAP.put("name", USERNAME);
            SIGN_UP_MAP.put("emailId", EMAIL);
            SIGN_UP_MAP.put("mobileNumber", PHONE_NUMBER);
            SIGN_UP_MAP.put("dob", DOB);
            SIGN_UP_MAP.put("password", PASSWORD);
            SIGN_UP_MAP.put("imageurl", imageUrl);
            if(!type_signup.equals("0"))
            SIGN_UP_MAP.put("signUpBy", getSignUpBy(type_signup, token));
            SIGN_UP_MAP.put("city", city.getText().toString());
            SIGN_UP_MAP.put("gender", new GenderInfo().GENDER);
           // SIGN_UP_MAP.put("appId", APP_ID);
            Net.makeRequest(C.APP_URL + ApiName.SIGN_UP_API, SIGN_UP_MAP.toString(), AddressActivity.this, AddressActivity.this);
        } catch (Exception e) {
        }
    }

    private boolean isValidForm() {

        if (TextUtils.isEmpty(address_one.getText().toString())) {
            Toast.makeText(AddressActivity.this, "Please enter address", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(address_two.getText().toString())) {
            Toast.makeText(AddressActivity.this, "Please enter address line 2", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(city.getText().toString())) {
            Toast.makeText(AddressActivity.this, "Please enter city name", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(zipcode.getText().toString())) {
            Toast.makeText(AddressActivity.this, "Please enter zip code", Toast.LENGTH_LONG).show();
            return false;
        } else if (zipcode.getText().toString().length() < 3 && zipcode.getText().toString().length() > 6) {
            Toast.makeText(AddressActivity.this, "Please enter valid zip code", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onBackPressed();
    }

    private JSONObject getSignUpBy(String type, String token) {
        JSONObject signup = new JSONObject();
        try {
            signup.put("type", type);
            signup.put("authToken", token);
            signup.put("appId", APP_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  signup;
    }


    //Mulitpart request for image upload
    private void UploadImage(final String path) {
        type = Constants.UPLOAD_FILE;
        new AsyncTask<Void,Void,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // pd = C.getProgressDialog(PublishProductActivity.this);
             /*   try {
                   // Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }*/
            }

            @Override
            protected String doInBackground(Void... params) {
                return path;
            }

            @Override
            protected void onPostExecute(String image) {
                super.onPostExecute(image);
                if (!TextUtils.isEmpty(image)) {
                    MultipartRequest multipartRequest = new MultipartRequest(AddressActivity.this, C.APP_URL+ Constants.UPLOAD_FILE,image, AddressActivity.this, AddressActivity.this);
                    RequestQueue requestQueue = Volley.newRequestQueue(AddressActivity.this);
                    multipartRequest.setShouldCache(false);
                    requestQueue.add(multipartRequest);
                    Log.e("image===",multipartRequest.toString());
                }else {
                    signUpdRequest("",type_sign_up,accessToken);
                    pd.dismiss();
                }
            }
        }.execute();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("error",volleyError.toString());
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        if(type.equals(Constants.UPLOAD_FILE)){
            signUpdRequest("",type_sign_up,accessToken);
        }else{
            Toast.makeText(AddressActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        if(jsonObject != null){
            Log.e("SIGNUP RESPONSE",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                if (type.equals(Constants.SIGN_UP)) {
                    Toast.makeText(this, "Successfully Registered!" , Toast.LENGTH_SHORT).show();
                    aps.setData(Constants.LOGIN_INFO, jsonObject.toString());
                    Model data = new Model(model.getData());
                    aps.setData(Constants.USER_ID, "" + data.getId());
                    startActivity(new Intent(AddressActivity.this, HomeActivity.class));
                } else if (type.equals(Constants.UPLOAD_FILE)) {
                    signUpdRequest(model.getFileName(), type_sign_up, accessToken);
                }
            }
        }
    }
}
