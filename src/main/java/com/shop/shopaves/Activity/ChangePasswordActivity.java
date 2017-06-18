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
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity implements  Response.ErrorListener,Response.Listener<JSONObject>{

    private ProgressDialog pd;
    private AppStore aps;
    private EditText oldPassword,newPassword,confirmNewPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPassword = (EditText)findViewById(R.id.oldpassword);
        newPassword = (EditText)findViewById(R.id.newpassword);
        confirmNewPassword = (EditText)findViewById(R.id.confirmpassword);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Change Password");
        aps = new AppStore(this);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(oldPassword.getText().toString())){
                    Toast.makeText(ChangePasswordActivity.this,"Please enter old password",Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(newPassword.getText().toString())){
                    Toast.makeText(ChangePasswordActivity.this,"Please enter new password",Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(confirmNewPassword.getText().toString())){
                    Toast.makeText(ChangePasswordActivity.this,"Please enter confirm password",Toast.LENGTH_LONG).show();
                }else if(!confirmNewPassword.getText().toString().equals(newPassword.getText().toString())){
                    Toast.makeText(ChangePasswordActivity.this,"Confirm password not matched",Toast.LENGTH_LONG).show();
                }else
                    makeChangePasswordRequest();
            }
        });
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void makeChangePasswordRequest(){
        pd =  C.getProgressDialog(ChangePasswordActivity.this);
        Map<String,String> map = new HashMap<>();
        map.put("oldPassword", oldPassword.getText().toString());
        map.put("newPassword", newPassword.getText().toString());
        map.put("userId", aps.getData(Constants.USER_ID));
        Net.makeRequestParams(C.APP_URL+ ApiName.CHANGE_PASSWORD_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(ChangePasswordActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("changepassword Response",jsonObject.toString());
        Model model  = new Model(jsonObject.toString());
        Toast.makeText(ChangePasswordActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            finish();
        }

    }
}
