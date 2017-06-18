package com.shop.shopaves.network;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.shop.shopaves.Util.MyApp;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by amsyt007 on 27/4/16.
 */

public class Net {
    static int socketTimeout = 30000;//30 seconds - change to what you want
    static RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

   /* public static void makeRequest(String url, Map<String,String> map, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        CustomJsonRequest reqObject = new CustomJsonRequest(Request.Method.POST, url,map,responseListener,errorListener);
        reqObject.setShouldCache(false);
        reqObject.setRetryPolicy(policy);
        MyApp.getInstance().addToRequestQueue(reqObject);
    }*/



    public static void makeRequest(String url, String map, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        CustomJsonRequest reqObject = new CustomJsonRequest(Request.Method.POST, url,map,responseListener,errorListener);
        reqObject.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        reqObject.setShouldCache(false);
        MyApp.getInstance().addToRequestQueue(reqObject);
    }
    public static void makeRequest(String url, Map<String,String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        CustomJsonRequest reqObject = new CustomJsonRequest(Request.Method.GET, url,params,responseListener,errorListener,true);
        reqObject.setShouldCache(false);
        reqObject.setRetryPolicy(policy);
        MyApp.getInstance().addToRequestQueue(reqObject);
    }

    public static void makeRequestParams(String url, Map<String,String> map, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        CustomJsonRequest reqObject = new CustomJsonRequest(Request.Method.POST, url,map,responseListener,errorListener,true);
        reqObject.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        reqObject.setShouldCache(false);
        MyApp.getInstance().addToRequestQueue(reqObject);
    }

/*
    public static void makeRequestput(String url, Map<String,String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        CustomJsonRequest reqObject = new CustomJsonRequest(Request.Method.PUT, url,params,responseListener,errorListener);
        reqObject.setShouldCache(false);
        reqObject.setRetryPolicy(policy);
        MyApp.getInstance().addToRequestQueue(reqObject);
    }
    public static void makeRequestdelete(String url, Map<String,String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        CustomJsonRequest reqObject = new CustomJsonRequest(Request.Method.DELETE, url,params,responseListener,errorListener);
        reqObject.setShouldCache(false);
        reqObject.setRetryPolicy(policy);
        MyApp.getInstance().addToRequestQueue(reqObject);
    }*/
}
