package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.googlecode.mp4parser.authoring.Edit;
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

public class CreateCollection extends AppCompatActivity implements  Response.ErrorListener,Response.Listener<JSONObject>{
    private ImageView backbttn;
    private EditText collection_name,description;
    private ProgressDialog pd;
    private TextView category_name,reset;
    private String categoryId;
    private AppStore aps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_collection);
        C.applyTypeface(C.getParentView(findViewById(R.id.activity_create_collection)), C.getHelveticaNeueFontTypeface(CreateCollection.this));
        backbttn = (ImageView) findViewById(R.id.back_create_collectn);
        collection_name = (EditText)findViewById(R.id.collectionname);
        description = (EditText)findViewById(R.id.desp);
        category_name = (TextView)findViewById(R.id.subcategoryname);
        reset = (TextView)findViewById(R.id.reset);
        aps = new AppStore(this);

        backbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.next_to_collection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateCollection.this);

                builder.setTitle("Message");
                builder.setMessage("Collection has been created successfully");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        if(TextUtils.isEmpty(collection_name.getText().toString())){
                            Toast.makeText(CreateCollection.this,"Please enter collection name",Toast.LENGTH_LONG).show();
                        }else if(TextUtils.isEmpty(category_name.getText().toString())){
                            Toast.makeText(CreateCollection.this,"Please select category",Toast.LENGTH_LONG).show();
                        }else{
                            createCollection();
                            //startActivity(new Intent(CreateCollection.this,PublishProductActivity.class));
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                //startActivity(new Intent(CreateCollection.this,PublishProductActivity.class));
            }
        });

        collection_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
                    reset.setAlpha(1.0f);
                    reset.setClickable(true);
                }else{
                    reset.setAlpha(0.6f);
                    reset.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.selectcategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CreateCollection.this,SelectItemActivity.class).putExtra("CONTENT_NAME","CATEGORY_COLLECTION"),400);
            }
        });

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collection_name.setText("");
                description.setText("");
                category_name.setText("");
                category_name.setHint("Tap to select Category");
            }
        });

        // {"name":"My Collection","description":"new","subCategoryId":"149"}
    }

    private void createCollection(){

        pd = C.getProgressDialog(this);
        JSONObject map = new JSONObject();
        try {
            map.put("userid",aps.getData(Constants.USER_ID));
            map.put("name",collection_name.getText().toString());
            map.put("description",description.getText().toString());
            map.put("categoryId",categoryId);
            Net.makeRequest(C.APP_URL+ ApiName.CREATE_COLLECTION_API,map.toString(),this,this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(CreateCollection.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject);
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model data = new Model(model.getData());
            data.getId();
            //  Toast.makeText(CreateCollection.this,"Collection has been created successfully",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CreateCollection.this,PublishProductActivity.class);
            intent.putExtra("ID",data.getId());
            startActivity(intent);
            finish();
        }else if(model.getStatus().equals(Constants.REQUEST_FAIL_CODE)){
            startActivity(new Intent(CreateCollection.this,LoginActivity.class));
            // Toast.makeText(CreateCollection.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
        }else if(!TextUtils.isEmpty(model.getMessage())){
            Toast.makeText(CreateCollection.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(requestCode == 400)
                category_name.setText(data.getStringExtra("NAME"));
            categoryId = data.getStringExtra("id");
        }
    }
}
