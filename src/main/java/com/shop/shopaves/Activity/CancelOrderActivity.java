package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.ScrollingTabContainerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class CancelOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog pd;
    private AppStore aps;
    private EditText comment;
    private TextView productName,quantity,productPrice,delivery;
    private ImageView productImage;
    private String type = "Reasons";
    private String reason;
    private boolean isCancel;
    private ArrayList<String> reasonList = new ArrayList<>();
    private String id;
    private ListView reasonListView;
    private TextView selectReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);
        TextView title = (TextView)findViewById(R.id.title);

        aps = new AppStore(this);
        comment = (EditText) findViewById(R.id.comment);
        productImage = (ImageView)findViewById(R.id.product_img);
        productName = (TextView)findViewById(R.id.product_name);
        quantity = (TextView)findViewById(R.id.quant);
        productPrice = (TextView)findViewById(R.id.price);
        delivery = (TextView)findViewById(R.id.dating);
        reasonListView = (ListView)findViewById(R.id.reasonlist);
        TextView actionText = (TextView)findViewById(R.id.actiontext);
        selectReason = (TextView)findViewById(R.id.selectreason);

        if(getIntent() != null){
            if(getIntent().getStringExtra(Constants.TYPE).equals(Constants.RETURNED)){
                isCancel = false;
                title.setText("Return Product");
                actionText.setText("Return Product");
                ((TextView)findViewById(R.id.reasontext)).setText("Reason For Return");
            }else{
                isCancel = true;
                title.setText("CANCEL ORDER");
                actionText.setText("Confirm Cancellation");
                ((TextView)findViewById(R.id.reasontext)).setText("Reason For Cancellation");
            }
            PicassoCache.getPicassoInstance(CancelOrderActivity.this).load(C.ASSET_URL + getIntent().getStringExtra(Constants.PRODUCT_IMAGE)).into(productImage);
            productName.setText(getIntent().getStringExtra(Constants.NAME));
            productPrice.setText("$"+getIntent().getStringExtra(Constants.PRICE));
            quantity.setText(getIntent().getStringExtra(Constants.QUANTITY));
            delivery.setText(getIntent().getStringExtra(Constants.DELIVERED));
            id = getIntent().getStringExtra(Constants.ID);

        }

        findViewById(R.id.resclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reasonListView.setVisibility(View.VISIBLE);
            }
        });


        reasonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("selectedItem",((TextView)view).getText().toString());
                selectReason.setText(((TextView)view).getText().toString());
                reason = ((TextView)view).getText().toString();
                reasonListView.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.confirmCancel).setOnClickListener(this);
        makeResionRequest();
    }

    private void makeResionRequest(){
        pd = C.getProgressDialog(CancelOrderActivity.this);
        Net.makeRequest(C.APP_URL+ ApiName.REASON_API,new HashMap<String,String>(),r,e);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.confirmCancel:
                if (TextUtils.isEmpty(reason)){
                    Toast.makeText(this, "Please choose a reason", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(comment.getText().toString().trim())){
                    Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
                }else{
                    JSONObject jsonObject = new JSONObject();
                    try {
                        type = "cancel";
                        pd = C.getProgressDialog(this);
                        jsonObject.put("userId",new AppStore(CancelOrderActivity.this).getData(Constants.USER_ID));
                        jsonObject.put("productId",id);
                        jsonObject.put("comment",comment.getText().toString());
                        jsonObject.put("reason",reason);
                        if(isCancel)
                        Net.makeRequest(C.APP_URL+ ApiName.ORDERCANCEL, jsonObject.toString(), r, e);
                        else
                            Net.makeRequest(C.APP_URL+ ApiName.ORDER_RETURN_API, jsonObject.toString(), r, e);

                        Log.e("Cancel order api",jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.back_addrss:
                finish();
                break;
        }
    }

    Response.ErrorListener e = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(CancelOrderActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    Response.Listener<JSONObject> r = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("delete_response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                if(type.equals("Reasons")){
                    JSONArray reasonArray = model.getDataJsonArray();
                    for(int i = 0; i < reasonArray.length(); i++){
                        try {
                            reasonList.add(reasonArray.get(i).toString());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter= new ArrayAdapter<String>(CancelOrderActivity.this,android.
                            R.layout.simple_spinner_dropdown_item ,reasonList);
                    reasonListView.setAdapter(adapter);
                }else{
                   if(isCancel)
                       startActivity(new Intent(CancelOrderActivity.this,CancellationConfirmedActivity.class));
                    else
                       startActivity(new Intent(CancelOrderActivity.this,ReturnOrderConfirmationActivity.class));
                }
            }else{
                Toast.makeText(CancelOrderActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

}
