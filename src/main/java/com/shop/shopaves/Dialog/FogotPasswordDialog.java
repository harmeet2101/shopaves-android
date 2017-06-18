package com.shop.shopaves.Dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 9/29/2016.
 */

public class FogotPasswordDialog extends Dialog {
    private ProgressDialog pd;
    private Context ctx;
    public FogotPasswordDialog(Context context) {
        super(context);
        this.ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.fogot_password_dialog);
        final EditText email = (EditText)findViewById(R.id.email);

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(ctx,"Please enter email",Toast.LENGTH_LONG).show();
                }
                else if(!C.isValidEmail(email.getText().toString())){
                    Toast.makeText(ctx,"Please enter valid email",Toast.LENGTH_LONG).show();
                }else {
                    Map<String, String> map = new HashMap<>();
                    map.put("email", email.getText().toString());
                    pd = C.getProgressDialog(ctx);
                    Net.makeRequest(C.APP_URL + ApiName.FORGOT_PASSWORD, map, login_response, login_error);
                }
            }
        });
    }

    Response.ErrorListener login_error = new Response.ErrorListener(){

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(ctx,""+ VolleyErrors.setError(volleyError), Toast.LENGTH_SHORT).show();
        }
    };

    Response.Listener<JSONObject> login_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("ForgotPassword",jsonObject.toString());

            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                if(!TextUtils.isEmpty(model.getResult())) {
                    Toast.makeText(ctx, "" + model.getResult(), Toast.LENGTH_LONG).show();
                    dismiss();
                }else
                    Toast.makeText(ctx, "" + model.getMessage(), Toast.LENGTH_LONG).show();
            }
               // aps.setData(Constants.USER_ID,result.getString("id"));
        }
    };
}
