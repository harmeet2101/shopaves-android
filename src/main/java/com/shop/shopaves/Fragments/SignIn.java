package com.shop.shopaves.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.HomeActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Dialog.FogotPasswordDialog;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignIn extends Fragment implements  Response.ErrorListener,Response.Listener<JSONObject>{

    private ProgressDialog pd;
    private AppStore aps;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        final EditText email_address = (EditText)view.findViewById(R.id.email);
        final EditText password = (EditText)view.findViewById(R.id.password);
        MyApp.getInstance().trackScreenView("Signin screen");

        aps = new AppStore(getActivity());
        view.findViewById(R.id.forgottext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FogotPasswordDialog(getActivity()).show();
            }
        });

        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(email_address.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter email or username",Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter password",Toast.LENGTH_LONG).show();
                }
                else if(!C.isValidEmail(email_address.getText().toString())){
                    Toast.makeText(getActivity(),"Please enter valid email",Toast.LENGTH_LONG).show();
                }
                else{
                    Map<String,String> map = new HashMap<>();
                    map.put("email",email_address.getText().toString().trim());
                    map.put("password",password.getText().toString().trim());
                    pd =  C.getProgressDialog(getActivity());
                    Net.makeRequestParams(C.APP_URL+ ApiName.LOGIN_API,map,login_response,login_error);
                }
            }
        });

        view.findViewById(R.id.skiplogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),HomeActivity.class));
            }
        });


        return view;
    }

    private void makeGcmRegisterRequest(){
        HashMap<String,String> map = new HashMap<>();
        map.put("device","android");
        map.put("userId",new AppStore(getActivity()).getData(Constants.USER_ID));
        map.put("gcmId", C.GCM_REGISTERED_ID);
        Net.makeRequestParams(C.APP_URL+ ApiName.GCM_REGISTER_API,map,gcmresponse,login_error);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(getActivity(),""+ VolleyErrors.setError(volleyError), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(),HomeActivity.class));
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
    }


    Response.ErrorListener login_error = new Response.ErrorListener(){

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
                Toast.makeText(getActivity(),""+ VolleyErrors.setError(volleyError), Toast.LENGTH_SHORT).show();
        }
    };

    Response.Listener<JSONObject> login_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
           // pd.dismiss();
            Log.e("loginn_response",jsonObject.toString());
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                aps.setData(Constants.LOGIN_INFO,jsonObject.toString());
                Model data = new Model(model.getData());
                aps.setData(Constants.USER_ID,""+data.getId());
              //  startActivity(new Intent(getActivity(),HomeActivity.class));
                makeGcmRegisterRequest();
            }

        }
    };

    Response.Listener<JSONObject> gcmresponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("gcmResponse",jsonObject.toString());
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                startActivity(new Intent(getActivity(),HomeActivity.class));
            }

        }
    };

}
