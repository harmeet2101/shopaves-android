package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static com.google.android.gms.R.id.progressBar;

public class TrackOrderActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener {

    private ProgressDialog pd;
    private String trackingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("TRACK");
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
       // makeTrackingRequest(getIntent().getStringExtra(Constants.TRACKING_ID));
        trackingId = getIntent().getStringExtra(Constants.TRACKING_ID);
        new RetrieveFeedTask().execute();
    }
/*
    private void makeTrackingRequest(String trackingId){
        pd = C.getProgressDialog(TrackOrderActivity.this);
        Net.makeRequest("https://api.goshippo.com/tracks/usps/"+trackingId,"",this,this);
    }*/

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Log.e("volley error",volleyError.toString());
        Toast.makeText(TrackOrderActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        Log.e("tracking response",jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){

        }
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            pd = C.getProgressDialog(TrackOrderActivity.this);
          //  progressBar.setVisibility(View.VISIBLE);
          //  responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
          //  String email = emailText.getText().toString();
            // Do some validation here

            try {
                URL url = new URL("https://api.goshippo.com/tracks/usps/" + trackingId);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            pd.dismiss();
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
          //  progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
          //  responseView.setText(response);
        }
    }
}
