package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

public class AddPaypalEmailActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONObject>{

    private EditText paypalEmail;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paypal_email);
        TextView title = (TextView)findViewById(R.id.title);
        paypalEmail = (EditText)findViewById(R.id.paypalemail);
        title.setText("ADD PAYPAL EMAIL");
        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.saveemail).setOnClickListener(this);
        C.applyTypeface(C.getParentView(findViewById(R.id.paypalemailview)), C.getHelveticaNeueFontTypeface(AddPaypalEmailActivity.this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_addrss:
                finish();
                break;
            case R.id.saveemail:
                if(TextUtils.isEmpty(paypalEmail.getText().toString())){
                    Toast.makeText(AddPaypalEmailActivity.this,"Please enter email or username",Toast.LENGTH_LONG).show();
                }
                else if(!C.isValidEmail(paypalEmail.getText().toString())){
                    Toast.makeText(AddPaypalEmailActivity.this,"Please enter valid email",Toast.LENGTH_LONG).show();
                }
                else{
                    AddPaypalAccount();
                }

                break;
        }
    }

    private void AddPaypalAccount(){
        pd = C.getProgressDialog(AddPaypalEmailActivity.this);
        JSONObject account = new JSONObject();
        try {
            account.put("userId",new AppStore(this).getData(Constants.USER_ID));
            account.put("email",paypalEmail.getText().toString());
            Net.makeRequest(C.APP_URL + ApiName.REGISTER_PAYPAL_API, account.toString(), AddPaypalEmailActivity.this, AddPaypalEmailActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(AddPaypalEmailActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Toast.makeText(AddPaypalEmailActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
            setResult(200);
            finish();
        }else
            Toast.makeText(AddPaypalEmailActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();

    }
}
