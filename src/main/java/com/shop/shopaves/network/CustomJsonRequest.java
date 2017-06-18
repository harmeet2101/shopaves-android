package com.shop.shopaves.network;


import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.shop.shopaves.Constant.C;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by amsyt007 on 2/2/16.
 */
public class CustomJsonRequest extends Request<JSONObject> {
    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;
    private int method;
    private String mUrl, body;
    private boolean isParams = false;

    public CustomJsonRequest(int method, String url, Map<String, String> params, Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener, boolean isParams) {
        super(method, url, errorListener);
        HttpsTrustManager.allowAllSSL();
        this.listener = reponseListener;
        this.params = params;
        this.method = method;
        this.mUrl = url;
        this.isParams = isParams;
    }

    public CustomJsonRequest(int method, String url, String params, Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        HttpsTrustManager.allowAllSSL();
        this.listener = reponseListener;
        this.body = params;
        this.method = method;
        this.mUrl = url;
    }

 /*  @Override
    public String getBodyContentType() {
        return TextUtils.isEmpty(body) ? super.getBodyContentType() : "application/json";
    }*/

/*
    @Override
    protected String getParamsEncoding() {
        return "utf-8";
    }*/


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        if (method != 1 || body != null)
            headers.put("Content-Type", "application/json");
        //headers.put("amsyt", "nokia1100");

        headers.put(
                "Authorization",
                String.format("Basic %s", Base64.encodeToString(String.format("%s:%s", "amsyt", "nokia1100").getBytes(), Base64.DEFAULT)));
        Log.e("headers",headers.toString());
        return headers;
    }
/*
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        if (method != 1 || body != null) {
            if(C.API_TYPE.equals("PAYPAL"))
            {
               // headers.put("Content-Type", "application/json");

                        headers.put(
                                "authorization",
                                String.format("Basic %s", Base64.encodeToString(String.format("AVHJDF560jMlIOVIChqkkAcPCUvmA-pD_EvO7GD9ih9Dr4DSEF-nP8PybIocWJxK1cYk5X9Q3OvLl-5i"+":"+"EH-lRvLMCqxTCkKJeI7-lhA7u6T1O1rxm-Jd-1tTpOJYM7CqB98UJoZ7OPicff3cn2fXF1fgpi6wQEQF").getBytes(), Base64.DEFAULT)));

                headers.put("Content-Type", "application/x-www-form-urlencoded");
                C.API_TYPE = "";
                Log.e("headervaluecolonr",String.format("%s:%s", "AVHJDF560jMlIOVIChqkkAcPCUvmA-pD_EvO7GD9ih9Dr4DSEF-nP8PybIocWJxK1cYk5X9Q3OvLl-5i","EH-lRvLMCqxTCkKJeI7-lhA7u6T1O1rxm-Jd-1tTpOJYM7CqB98UJoZ7OPicff3cn2fXF1fgpi6wQEQF").toString());
              */
/*  headers.put(
                        "authorization",
                        String.format("Basic %s", Base64.encodeToString(String.format("AVHJDF560jMlIOVIChqkkAcPCUvmA-pD_EvO7GD9ih9Dr4DSEF-nP8PybIocWJxK1cYk5X9Q3OvLl-5i"+":"+"EH-lRvLMCqxTCkKJeI7-lhA7u6T1O1rxm-Jd-1tTpOJYM7CqB98UJoZ7OPicff3cn2fXF1fgpi6wQEQF").getBytes(), Base64.DEFAULT)));
*//*

                //  headers.put("Accept", "application/json");
               // headers.put("Authorization","AVHJDF560jMlIOVIChqkkAcPCUvmA-pD_EvO7GD9ih9Dr4DSEF-nP8PybIocWJxK1cYk5X9Q3OvLl-5i"+":"+"EH-lRvLMCqxTCkKJeI7-lhA7u6T1O1rxm-Jd-1tTpOJYM7CqB98UJoZ7OPicff3cn2fXF1fgpi6wQEQF");
            }else{
                headers.put("Content-Type", "application/json");
                headers.put(
                        "Authorization",String.format("Basic %s", Base64.encodeToString(String.format("%s:%s", "amsyt", "nokia1100").getBytes(), Base64.DEFAULT)));
            }
        }
        //headers.put("amsyt", "nokia1100");

        Log.e("headers",headers.toString());
        return headers;
    }
*/


    @Override
    public byte[] getBody() throws AuthFailureError {
        Log.e("body-", TextUtils.isEmpty(body) ? "null" : body);
        return TextUtils.isEmpty(body) ? super.getBody() : body.getBytes();
    }
/*

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers=new HashMap<String,String>();
        headers.put("Content-Type","application/json;charset=UTF-8");
        return headers;
    }
*/



    /*
    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(
                "Authorization",
                String.format("Basic %s", Base64.encodeToString(
                        String.format("%s:%s", mobile, otp).getBytes(), Base64.DEFAULT)));
        return params;
    }*/


  /*  @Override
    public String getUrl() {
        if(method == Method.GET) {
            StringBuilder stringBuilder = new StringBuilder(mUrl);
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            int i = 1;
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if(i == 1) {
                    stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                } else {
                    stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                }
                iterator.remove();
                i++;
            }
            mUrl = stringBuilder.toString();

        }
        Log.i("Request Url",mUrl);
        return mUrl;
    }*/

   /* @Override
    public Map<String, String> getParams()  throws AuthFailureError{
        Log.e("parm-",params.toString());
        return params;

    }
*/

    @Override
    public String getUrl() {

        if (method == Method.GET) {
            StringBuilder stringBuilder = new StringBuilder(mUrl);
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            int i = 1;
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (i == 1) {
                    stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                } else {
                    stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                }
                iterator.remove();
                i++;
            }
            return mUrl = stringBuilder.toString();

        }
        return super.getUrl();
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params == null ? super.getParams() : params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }


    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);

    }
}
