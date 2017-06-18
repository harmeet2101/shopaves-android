package com.shop.shopaves.Util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
//import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.orm.SugarContext;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

//import io.fabric.sdk.android.Fabric;


public class MyApp extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    public static final String TAG = MyApp.class.getSimpleName();
    private static MyApp mInstance;
    private RequestQueue mRequestQueue;
    private static Context context;
    private static final String TWITTER_KEY = "9rG5sXrl28ekkdzD97qz5gUsT";
    private static final String TWITTER_SECRET = "bkf6k0BboALDhUiKc3xzToC3lC5nbqkyU7KaQmGpXvAyRxxi0s";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MyApp.context=getApplicationContext();
        SugarContext.init(this);
        FileUtils.createApplicationFolder();
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
//        ApiInvoker.initializeInstance();

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        // Setup handler for uncaught exceptions.
      /*  Fabric.with(this, new Crashlytics());*/
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), Base64.DEFAULT));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    public static Context getAppContext() {
        return MyApp.context;
    }
    public static synchronized MyApp getInstance() {
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);


        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }


    public SharedPreferences getPreferences(){
        return getAppContext().getSharedPreferences("MyPreference",MODE_PRIVATE);
    }
}