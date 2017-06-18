package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.AppEventsLogger;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Constant.InstagramApp;
import com.shop.shopaves.Fragments.SignIn;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,Response.ErrorListener,Response.Listener<JSONObject>{

    private TextView signIn, signUp;
    private TwitterAuthClient mTwitterAuthClient;
    private RelativeLayout viewsignUp;

    private LinearLayout skipSighup;
    private Session.StatusCallback mFbCallback = new SessionStatusCallback();
    //private String facebookToken=null;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_GET_TOKEN = 9002;
    private InstagramApp mApp;
    private boolean isFacebook = false;
    private ProgressDialog pd;
    private String emailSignupName = "";
    private String socialUserName = "";
    private String socialAccessToken = "";
    private String socialType = "0";
    private String socialEmailAppId = "";
    private String socialAppId = "";
    private boolean gotoFacebook = false;
    private boolean gotoGoogle = false;
    private AppStore aps;
    private String socialEmailId = "";
    private String socialUserPicture = "";
    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:" +
            "(id,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApp.getInstance().trackScreenView("Login screen");
        signIn = (TextView) findViewById(R.id.signin);
        signUp = (TextView) findViewById(R.id.signup);
        viewsignUp = (RelativeLayout)findViewById(R.id.signuplayout);
        LinearLayout twitter_custom_button = (LinearLayout)findViewById(R.id.tweeterlogin);
        mApp = new InstagramApp(LoginActivity.this, C.instagram_Client_id,
                C.instagram_secret_id, C.CALLBACK_URL);
        mApp.setListener(listener);
        aps = new AppStore(this);
        skipSighup = (LinearLayout) findViewById(R.id.signup_skip);
        skipSighup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpDetailActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.facebooklogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFacebook = true;
                onFacebookLogin();
            }
        });

        findViewById(R.id.googlelogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGoogle = true;
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_GET_TOKEN);
            }
        });

        twitter_custom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTwitterAuthClient= new TwitterAuthClient();
                mTwitterAuthClient.authorize(LoginActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                     //   Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                        TwitterSession session = twitterSessionResult.data;
                        TwitterAuthToken authToken = session.getAuthToken();
                        String token = authToken.token;

                        socialAppId = ""+session.getId();
                        socialType = "2";
                        socialAccessToken = token;
                        socialUserName = session.getUserName();

                      /*  new MyTwitterApiClient(session).getUsersService().show(12L, null, true,
                                new Callback<User>() {
                                    @Override
                                    public void success(Result<User> result) {
                                        Log.d("twittercommunity", "user's profile url is "
                                                + result.data.profileImageUrlHttps);
                                    }

                                    @Override
                                    public void failure(TwitterException exception) {
                                        Log.d("twittercommunity", "exception is " + exception);
                                    }
                                });
*/

                        socialLoginRequest(""+session.getId(),socialType);
                    }
                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(LoginActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        findViewById(R.id.instagramlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeInstagramLogin();
            }
        });

        findViewById(R.id.linkedin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkedSetup();
            }
        });

//for instagram
        if (mApp.hasAccessToken()) {
            //tvSummary.setText("Connected as " + mApp.getUserName());
            //  btnConnect.setText("Disconnect");
           // Toast.makeText(LoginActivity.this,"success"+mApp.getUserName(),Toast.LENGTH_LONG).show();
        }

        //for facebook
        AppEventsLogger.activateApp(LoginActivity.this);
        // C.printKeyHash(getActivity());
      //  createFacebookSession(savedInstanceState);
        initGoogle();

        C.applyTypeface(C.getParentView(findViewById(R.id.activity_login)), C.getHelveticaNeueFontTypeface(LoginActivity.this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("SIGN IN"));
        tabLayout.addTab(tabLayout.newTab().setText("SIGN UP"));
        tabLayout.setTabTextColors(Color.BLACK, getResources().getColor(R.color.colorPrimary));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn.setTextColor(getResources().getColor(R.color.colorPrimary));
                signUp.setTextColor(getResources().getColor(R.color.black));
                findViewById(R.id.signupview).setVisibility(View.GONE);
                findViewById(R.id.signview).setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(0);
                viewPager.setVisibility(View.VISIBLE);
                viewsignUp.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp.setTextColor(getResources().getColor(R.color.colorPrimary));
                signIn.setTextColor(getResources().getColor(R.color.black));
                findViewById(R.id.signupview).setVisibility(View.VISIBLE);
                findViewById(R.id.signview).setVisibility(View.GONE);
               // viewPager.setCurrentItem(1);
                viewPager.setVisibility(View.GONE);
                viewsignUp.setVisibility(View.VISIBLE);
            }
        });
    }

    public void linkedSetup(){
//Linkedin Integration
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        LISessionManager.getInstance(getApplicationContext()).init(LoginActivity.this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {

//                setUpdateState();
//                Toast.makeText(getApplicationContext(), "success" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "Successfully fetched data, Please enter mobile no to continue.", Toast.LENGTH_SHORT).show();
                String lin_token = LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString();
                try {
                    JSONObject jsonObject = new JSONObject(lin_token);
                    //linkedin_token = jsonObject.getString("accessTokenValue");
                    Log.e("linkedtoken",jsonObject.getString("accessTokenValue"));
                    //Linkedin
                    socialAccessToken = jsonObject.getString("accessTokenValue");
                    getUserData();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                Log.e("Linkedin Access token",lin_token);
                // Log.e("Linkedin token",linkedin_token);
                // Authentication was successful.  You can now do
                // other calls with the SDK.
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
//                setUpdateState();
                Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast.LENGTH_LONG).show();
                Log.e("linkedin_error",error.toString());
            }
        }, true);
    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.W_SHARE, Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }
    public void getUserData(){
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(LoginActivity.this, topCardUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    setUserProfile(result.getResponseDataAsJson());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onApiError(LIApiError error) {
                Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

/*for linkedin*/
    public  void  setUserProfile(JSONObject response){
        try {

           // user_email = response.get("emailAddress").toString();
            Log.e("useremail",response.get("emailAddress").toString());

          //  user_name = response.get("formattedName").toString();
            Log.e("user_name",response.get("formattedName").toString());

          //  profile_url=(response.getString("pictureUrl"));
          //  Log.e("profile_url",response.getString("pictureUrl"));

            socialEmailId = response.get("emailAddress").toString();
            socialType = "3";
            socialAppId = response.getString("id");
            socialUserName = response.get("formattedName").toString();
            socialLoginRequest(socialAppId,socialType);

            // sp.edit().putString(NAME,user_name).putString(EMAIL,user_email).putString(PROFILE_URL,profile_url).commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {
            // tvSummary.setText("Connected as " + mApp.getUserName());
            //btnConnect.setText("Disconnect");
        //    Toast.makeText(LoginActivity.this,"success"+mApp.getUserName(),Toast.LENGTH_LONG).show();


            socialAppId = ""+mApp.getId();
            socialUserName = ""+mApp.getUserName();
            socialAccessToken =mApp.getAccessToken();
            socialType = "4";
            socialLoginRequest(""+mApp.getId(),socialType);
        }

        @Override
        public void onFail(String error) {
            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
        }
    };
    //	Facebook setting methods
    private void createFacebookSession(Bundle savedInstanceState){
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(LoginActivity.this, null, mFbCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(LoginActivity.this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(mFbCallback));
            }
        }
    }
    private void initGoogle(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.serverclientid))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this).enableAutoManage(LoginActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            getFacebookUserInfo();
        }
    }

    private void onFacebookLogin() {
        Session session = Session.getActiveSession();
        if (session != null && !session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(mFbCallback));
        } else {
            Session.openActiveSession(LoginActivity.this, true, mFbCallback);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private void getFacebookUserInfo() {

        final Session session = Session.getActiveSession();
        if (session.isOpened()) {
            com.facebook.Request request = com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback(){
                @Override
                public void onCompleted(GraphUser user, com.facebook.Response response) {
                    if (user != null) {
                        //facebookToken = session.getAccessToken();
                        socialAccessToken = session.getAccessToken();
                        String json = user.getInnerJSONObject().toString();
                        Model m = new Model(json);
                       /* Map<String,String> map = new HashMap<>();
                        map.put(FB_ACCESS_TOKEN,fb_token);
                        Net.makeRequest(URL_FB,map,r_fb,e);
                        pd.show();*/
                        Log.e("Fb_token",socialAccessToken);
                        if(gotoFacebook) {
                            if (!isFacebook && !TextUtils.isEmpty(socialAccessToken)) {
                                isFacebook = true;
                                socialUserName = m.getName();
                                socialType = "1";
                                socialAppId = ""+m.getId();
//                                socialEmailId = String.valueOf(user.asMap().get("email"));
                                socialEmailId = m.getEmail();
                                socialUserPicture = "https://graph.facebook.com/" + m.getid() + "/picture?type=large";
                                socialLoginRequest("" + m.getid(), socialType);
                            }
                        }
                    }
                }
            });

            Bundle params = request.getParameters();
            params.putString("fields", "email,id,name,picture.type(large)");
            request.setParameters(params);
            request.executeAsync();
        }
    }

    private void makeInstagramLogin(){
        if (mApp.hasAccessToken()) {
            /*final AlertDialog.Builder builder = new AlertDialog.Builder(
                    LoginActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    mApp.resetAccessToken();
                                    //  btnConnect.setText("Connect");
                                    //tvSummary.setText("Not connected");
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();*/
            socialAppId = ""+mApp.getId();
            socialUserName = ""+mApp.getUserName();
            socialAccessToken =mApp.getAccessToken();


            socialType = "4";
            socialLoginRequest(""+mApp.getId(),socialType);

        } else {
            mApp.authorize();
        }
    }



    private class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = 1;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new SignIn();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
       /* if(Session.getActiveSession() != null)
            Session.getActiveSession().addCallback(mFbCallback);*/
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Session.getActiveSession() != null)
            Session.getActiveSession().removeCallback(mFbCallback);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 140)
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 64206)
        Session.getActiveSession().onActivityResult(LoginActivity.this, requestCode, resultCode, data);
        if (requestCode == RC_GET_TOKEN) {
            // [START get_id_token]
            /*for google signup*/
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                //emailSignupName = acct.getDisplayName();
                socialEmailAppId = acct.getId();
                socialUserName = acct.getDisplayName();
                Log.e("name",acct.getDisplayName());
                Log.e("email",acct.getEmail());
                acct.getId();
                new RetrieveGoogleTokenTask().execute(acct.getEmail());
                Map<String, String> map = new HashMap<String, String>();

                map.put("username",acct.getDisplayName());
                map.put("fullname",acct.getDisplayName());
                map.put("google_id",acct.getEmail());
                socialUserPicture = acct.getPhotoUrl().toString();
                socialEmailId = acct.getEmail();

                Log.e("param", map.toString());
                //makeGoogleRequest(map);
                //	*//*	name.setText(""+acct.getDisplayName());
                //		email.setText(""+acct.getEmail());*//*
            }
        }
        if(requestCode == 3672)
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

        if(requestCode == 64206)
            getFacebookUserInfo();

    }

    private class RetrieveGoogleTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";

            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
                socialAccessToken = token;
            } catch (IOException e) {
                Log.e("message", e.getMessage());
            } catch (UserRecoverableAuthException e) {
              //  startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
                Log.e("exception",e.toString());
            } catch (GoogleAuthException e) {
                Log.e("exception",e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // ((TextView) findViewById(R.id.token_value)).setText("Token Value: " + s);
           // GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

         //   socialLoginRequest();
            if(gotoGoogle) {
                socialType = "6";
                socialAppId = socialEmailAppId;
                socialLoginRequest(socialEmailAppId, socialType);
            }
           /* Intent intent = new Intent(LoginActivity.this, SignUpDetailActivity.class).putExtra("NAME", emailSignupName).putExtra("ACCESS_TOKEN", s).putExtra("TYPE","6");
            startActivity(intent);
            finish();*/
            Log.e("token value",s);
        }
    }

    private void socialLoginRequest(String appId,String type){
        Map<String,String> map = new HashMap<>();
        map.put("appId",appId);
        map.put("type",type);
        pd =  C.getProgressDialog(LoginActivity.this);
        Net.makeRequestParams(C.APP_URL+ ApiName.SOCIAL_LOGIN_API,map,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(LoginActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject);
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            aps.setData(Constants.LOGIN_INFO,jsonObject.toString());
            Model data = new Model(model.getData());
            aps.setData(Constants.USER_ID,""+data.getId());
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }
       else if(model.getStatus().equals("400")){
            Model data = new Model(model.getData());
//           aps.setData(Constants.USER_ID,""+data.getId());
           Intent intent = new Intent(LoginActivity.this, SignUpDetailActivity.class).putExtra("NAME", socialUserName).putExtra("IMAGE_URL",socialUserPicture).putExtra("ACCESS_TOKEN", socialAccessToken).putExtra("APP_ID",socialAppId).putExtra("SOCIAL_EMAIL",socialEmailId).putExtra("TYPE",socialType);
           startActivity(intent);
          // finish();
       }
    }

    class MyTwitterApiClient extends TwitterApiClient {
        public MyTwitterApiClient(TwitterSession session) {
            super(session);
        }

        public UsersService getUsersService() {
            return getService(UsersService.class);
        }
    }

    interface UsersService {
        @GET("/1.1/users/show.json")
        void show(@Query("user_id") Long userId,
                  @Query("screen_name") String screenName,
                  @Query("include_entities") Boolean includeEntities,
                  Callback<User> cb);
    }
}
