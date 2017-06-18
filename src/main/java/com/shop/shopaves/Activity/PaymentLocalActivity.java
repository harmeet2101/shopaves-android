package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class PaymentLocalActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONObject>{

    private ListView accountList;
    private AccountAdapter accountAdapter;
    private ProgressDialog pd;
    private String type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        accountList = (ListView)findViewById(R.id.accountlist);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.payment));
        C.applyTypeface(C.getParentView(findViewById(R.id.paymentlayout)), C.getHelveticaNeueFontTypeface(PaymentLocalActivity.this));
        if(getIntent().getStringExtra(Constants.TYPE) != null){
            type = getIntent().getStringExtra(Constants.TYPE);
        }
        accountAdapter = new AccountAdapter(this,0);
        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.addmore).setOnClickListener(this);
        makeAccountListRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_addrss:
                finish();
                break;
            case R.id.addmore:
                startActivityForResult(new Intent(PaymentLocalActivity.this,AddPaymentDetailActivity.class),200);
                break;
        }
    }

    private void makeAccountListRequest(){
        pd = C.getProgressDialog(PaymentLocalActivity.this);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("userId",new AppStore(PaymentLocalActivity.this).getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ ApiName.GET_PAYPAL_ACCOUNTS_API,hashMap,this,this);
    }

    private void makeAccountSettingRequest(String payId,String type){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("userId",new AppStore(PaymentLocalActivity.this).getData(Constants.USER_ID));
        hashMap.put("payId",payId);
        hashMap.put("type",type);
        Net.makeRequestParams(C.APP_URL+ ApiName.SET_PAYPAL_ACCOUNT_API,hashMap,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(PaymentLocalActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        Log.e("AccountAPIResponse",jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] = model.getDataArray();
            accountAdapter.clear();
            for(Model data : dataArray){
                accountAdapter.add(new AccountItems(""+data.getId(),data.getEmail(),data.getName(),data.getAddress(), C.getDateFormat(data.getTimeStamp()),data.isCurrent(),data.getType()));
            }
            accountAdapter.notifyDataSetChanged();

            findViewById(R.id.noproduct).setVisibility(accountAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
            accountList.setAdapter(accountAdapter);
        }
    }

    private class AccountAdapter extends ArrayAdapter<AccountItems>{
        MyHolder myHolder;

        public AccountAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
             myHolder = null;
            if(view == null){
                view = getLayoutInflater().inflate(R.layout.custom_paypal_account,parent,false);
                myHolder = new MyHolder();
                myHolder.paypalEmail = (TextView)view.findViewById(R.id.email);
                myHolder.date  =  (TextView)view.findViewById(R.id.date);
                myHolder.removeAccount = (TextView)view.findViewById(R.id.remove);
                myHolder.defaultEmail = (ToggleButton)view.findViewById(R.id.chkState);
                myHolder.paypalIcon = (ImageView)view.findViewById(R.id.paypalicon);
                myHolder.accountName = (TextView)view.findViewById(R.id.accountname);
                myHolder.address = (TextView)view.findViewById(R.id.address);
                view.setTag(myHolder);
            }else{
                myHolder = (MyHolder) view.getTag();
            }
            final AccountItems accountItems = getItem(position);
            if(accountItems.type.equals("paypal")) {
                myHolder.paypalEmail.setText(accountItems.email);
                myHolder.paypalIcon.setImageResource(R.drawable.paypalicon);
                myHolder.accountName.setText("Paypal");
                myHolder.address.setVisibility(View.GONE);
            }
            else{
                myHolder.paypalEmail.setText(accountItems.name);
                myHolder.paypalIcon.setImageResource(R.drawable.bankcheck);
                myHolder.accountName.setText("Bank Check");
                myHolder.address.setVisibility(View.VISIBLE);
                myHolder.address.setText(accountItems.address != null ? accountItems.address : "");
            }
            myHolder.date.setText("Added on "+accountItems.date);
            myHolder.defaultEmail.setChecked(accountItems.isDefault);

            myHolder.defaultEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    makeAccountSettingRequest(accountItems.id,"1");
                }
            });

            myHolder.removeAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeAccountSettingRequest(accountItems.id,"2");
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type.equals(Constants.ADD_PRODUCT)){
                        setResult(101,new Intent().putExtra(Constants.PAYMENT_MODE,accountItems.type.equals("paypal") ? "Paypal" : "BankCheck"));
                        finish();
                    }else
                   if(accountItems.type.equals("paypal"))
                    startActivity(new Intent(PaymentLocalActivity.this,PaypalTransactionHistory.class).putExtra(Constants.ID,accountItems.id).putExtra(Constants.EMAIL,accountItems.email).putExtra(Constants.DATE,accountItems.date));
                }
            });
            return view;
        }
    }

    class MyHolder{
        ImageView paypalIcon;
        TextView paypalEmail,date,removeAccount,accountName,address;
        ToggleButton defaultEmail;
    }

    class AccountItems{
        String email,date,id,type,name,address;
        boolean isDefault;

        public AccountItems(String id,String email,String name,String address, String date, boolean isDefault,String type) {
            this.email = email;
            this.date = date;
            this.isDefault = isDefault;
            this.id = id;
            this.type = type;
            this.name = name;
            this.address = address;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 200)
            makeAccountListRequest();
    }
}
