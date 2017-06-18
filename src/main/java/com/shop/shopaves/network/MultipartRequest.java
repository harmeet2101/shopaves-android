package com.shop.shopaves.network;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.shop.shopaves.Util.MyApp;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class MultipartRequest extends Request<JSONObject> {
    private HttpEntity mHttpEntity;
    private Response.Listener mListener;
    public static final int TIMEOUT_MS = 60000;
    private boolean isVideo = false;
    public MultipartRequest(String url, String filePath, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mHttpEntity = buildMultipartEntity(filePath);
    }

    public MultipartRequest(String url, File file, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mHttpEntity = buildMultipartEntity(file);
    }

    public MultipartRequest(Context context, String url, String filePath, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mHttpEntity = buildMultipartEntity(filePath,context);
    }
    public MultipartRequest(Context context, String url, String filePath, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener,boolean isVideo) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        isVideo = true;
        mHttpEntity = buildMultipartEntity(filePath,context);
    }


    private HttpEntity buildMultipartEntity(String filePath) {
        File file = new File(filePath);
        return buildMultipartEntity(file);
    }
    private HttpEntity buildMultipartEntity(String filePath,Context context) {
        File file = new File(filePath);
        if(!isVideo)
        return buildMultipartEntity(file,context);else
            return buildVideoMultipartEntity(file,context);
    }
    private HttpEntity buildMultipartEntity(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("UTF-8"));
        ContentType contentType = ContentType.create("image/png");
        FileBody fileBody = new FileBody(file,contentType, file.getName());
        builder.addPart("userImage",fileBody);
        return builder.build();
    }

    private HttpEntity buildMultipartEntity(File file,Context context) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("UTF-8"));
        ContentType contentType = ContentType.create("image/*");
        FileBody fileBody = new FileBody(file,contentType, file.getName());
        builder.addPart("file",fileBody);
        return builder.build();
    }
    private HttpEntity buildVideoMultipartEntity(File file,Context context) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("UTF-8"));
        ContentType contentType = ContentType.create("video/*");
        FileBody fileBody = new FileBody(file,contentType, file.getName());
        builder.addPart("file",fileBody);
        return builder.build();
    }

   @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers=new HashMap<String,String>();
        headers.put("Accept","application/json");
       headers.put(
               "Authorization",
               String.format("Basic %s", Base64.encodeToString(String.format("%s:%s", "amsyt", "nokia1100").getBytes(), Base64.DEFAULT)));

       return headers;
    }
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

    @Override
    public String getBodyContentType() {
        String enctype = mHttpEntity.getContentType().getValue();
        Log.e("enctype",enctype);
        return enctype;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
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
        mListener.onResponse(response);
    }
}