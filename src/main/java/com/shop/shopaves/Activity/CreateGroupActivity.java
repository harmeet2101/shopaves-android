package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity implements  Response.ErrorListener,Response.Listener<JSONObject>{

    private EditText groupName,description;
    private ProgressDialog pd;
    private AppStore aps;
    private String groupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        groupName = (EditText)findViewById(R.id.groupname);
        description = (EditText)findViewById(R.id.desp);

        aps = new AppStore(this);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.create_group));


        if(getIntent().getExtras() != null){
            groupName.setText(getIntent().getExtras().getString(Constants.GROUP_NAME));
            description.setText(getIntent().getExtras().getString(Constants.GROUP_DESCRIPTION));
            groupId = getIntent().getExtras().getString(Constants.GROUP_ID);
        }


        findViewById(R.id.next_to_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(groupName.getText().toString())){
                    Toast.makeText(CreateGroupActivity.this,"Please enter group name",Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(description.getText().toString())){
                    Toast.makeText(CreateGroupActivity.this,"Please enter description",Toast.LENGTH_LONG).show();
                }
                else{
                    createGroup();
                }
            }
        });
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createGroup(){
        pd = C.getProgressDialog(this);
        Map<String, String> map = new HashMap<String, String>();
            map.put("userId",aps.getData(Constants.USER_ID));
            map.put("name",groupName.getText().toString());
            map.put("desc",description.getText().toString());
        if(!TextUtils.isEmpty(groupId)){
            map.put("gid",groupId);
            Net.makeRequestParams(C.APP_URL+ ApiName.EDIT_GROUP_API,map,this,this);
        }else
            Net.makeRequestParams(C.APP_URL+ ApiName.CREATE_GROUP_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(CreateGroupActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Message");
            builder.setMessage("Your item is added Successfully");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog

                    dialog.dismiss();
                    setResult(100);
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();



        }

    }
}
