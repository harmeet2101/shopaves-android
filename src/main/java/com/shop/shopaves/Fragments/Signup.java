package com.shop.shopaves.Fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shop.shopaves.Activity.SignUpDetailActivity;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.InstagramApp;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.MyApp;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Signup extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private LinearLayout skipSighup;
    private Session.StatusCallback mFbCallback = new SessionStatusCallback();
    private String fb_token=null;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_GET_TOKEN = 9002;
    private InstagramApp mApp;
   /* private String TWITTER_KEY = "uChsqT54qurBFEMcfOGfUbElP";
    private String TWITTER_SECRET = "bKKYuCcgQPGxKNWtLw5NE7KVPqNinm3eUhH66QtlAXMPn6DCqR";*/
/*   private static final String TWITTER_KEY = "9bylT3fsZpNLRmhI91HcHvK8j";
    private static final String TWITTER_SECRET = "OtBClj4RFxs5Xic0NNgY4RB7Up2gVBKYzCgVzorTdB7aJxpQlI";*/
   private static final String TWITTER_KEY = "9rG5sXrl28ekkdzD97qz5gUsT";
    private static final String TWITTER_SECRET = "bkf6k0BboALDhUiKc3xzToC3lC5nbqkyU7KaQmGpXvAyRxxi0s";
    private Activity activity;


    public Signup() {
        // Requiredempty public constructor
    }

    private TwitterAuthClient mTwitterAuthClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        MyApp.getInstance().trackScreenView("Signup screen");
        LinearLayout twitter_custom_button = (LinearLayout)v.findViewById(R.id.tweeterlogin);
        mApp = new InstagramApp(getActivity(), C.instagram_Client_id,
                C.instagram_secret_id, C.CALLBACK_URL);
        mApp.setListener(listener);

        skipSighup = (LinearLayout) v.findViewById(R.id.signup_skip);
        skipSighup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignUpDetailActivity.class);
                startActivity(intent);
            }
        });

        v.findViewById(R.id.facebooklogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFacebookLogin();
            }
        });

        v.findViewById(R.id.googlelogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_GET_TOKEN);
            }
        });

        twitter_custom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTwitterAuthClient= new TwitterAuthClient();
                mTwitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        v.findViewById(R.id.instagramlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                makeInstagramLogin();
            }
        });
//for instagram
        if (mApp.hasAccessToken()) {
            //tvSummary.setText("Connected as " + mApp.getUserName());
          //  btnConnect.setText("Disconnect");
            Toast.makeText(getActivity(),"success"+mApp.getUserName(),Toast.LENGTH_LONG).show();
        }



        //for facebook
        AppEventsLogger.activateApp(getActivity());
       // C.printKeyHash(getActivity());
        createFacebookSession(savedInstanceState);
        initGoogle();
        return v;
    }

    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {
           // tvSummary.setText("Connected as " + mApp.getUserName());
            //btnConnect.setText("Disconnect");
            Toast.makeText(getActivity(),"success"+mApp.getUserName(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFail(String error) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    };

    //	Facebook setting methods
    private void createFacebookSession(Bundle savedInstanceState){
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(getActivity(), null, mFbCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(getActivity());
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

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(),this)
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
            Session.openActiveSession(getActivity(), true, mFbCallback);
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
                        getUserInfoFromFacebook(user);
                        fb_token = session.getAccessToken();
                       /* Map<String,String> map = new HashMap<>();
                        map.put(FB_ACCESS_TOKEN,fb_token);
                        Net.makeRequest(URL_FB,map,r_fb,e);
                        pd.show();*/
                        Log.e("Fb_token",fb_token);

                    }
                }
            });

            Bundle params = request.getParameters();
            params.putString("fields", "email,id,name");
            request.setParameters(params);
            request.executeAsync();

        }
    }
    private void getUserInfoFromFacebook(final GraphUser user) {
        try {
            String json = user.getInnerJSONObject().toString();
            Model m = new Model(json);
//            name = m.getName();
//            id = m.getFBID();
//            email = m.getEmail();
//            sp.edit().putString(NAME,name).putString(FACEBOOKID,id).putString(EMAIL,email).commit();

         /*   Map<String,String> map = new HashMap<>();
            map.put(FB_ACCESS_TOKEN,fb_token);
            Net.makeRequest(URL_FB,map,r_fb,e);
            pd.show();*/

        } catch (Exception ex) {
        }
    }


    private void makeInstagramLogin(){
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity());
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
            alert.show();
        } else {
            mApp.authorize();
        }
    }


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
        //mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
		if (requestCode == RC_GET_TOKEN) {
			// [START get_id_token]
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

			if (result.isSuccess()) {
				GoogleSignInAccount acct = result.getSignInAccount();
				String idToken = acct.getIdToken();
				Log.e("name",acct.getDisplayName());
				Log.e("email",acct.getEmail());

				Map<String, String> map = new HashMap<String, String>();

				map.put("username",acct.getDisplayName());
				map.put("fullname",acct.getDisplayName());
				map.put("google_id",acct.getEmail());

				Log.e("param", map.toString());
				//makeGoogleRequest(map);
		//	*//*	name.setText(""+acct.getDisplayName());
		//		email.setText(""+acct.getEmail());*//*
			}
		}
	}

    @Override
    public void onStart() {
        super.onStart();
        if(Session.getActiveSession() != null)
            Session.getActiveSession().addCallback(mFbCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Session.getActiveSession() != null)
            Session.getActiveSession().removeCallback(mFbCallback);
    }
/*
    public void authorizeTwitter() {
        mTwitterAuthClient.authorize(this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(final Result<TwitterSession> twitterSessionResult) {
                String output = "Status: " +
                        "Your login was successful " +
                        twitterSessionResult.data.getUserName() +
                        "\nAuth Token Received: " +
                        twitterSessionResult.data.getAuthToken().token;

                Log.e("output", output);

                TwitterAuthClient authClient = new TwitterAuthClient();
                authClient.requestEmail(twitterSessionResult.data, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        Twitter.getApiClient(twitterSessionResult.data).getAccountService()
                                .verifyCredentials(true, false, new Callback<User>() {
                                    @Override
                                    public void success(Result<User> userResult) {
                                        User user = userResult.data;
                                        name = user.name;
                                        email = user.email;

                                        Log.e("user + email", name + " , " + email);
                                    }

                                    @Override
                                    public void failure(TwitterException e) {
                                        Log.e("error twitt", e.toString());
                                    }

                                });
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.e("error twitt", exception.toString());

                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });
    }*/

}
