package com.shop.shopaves.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*My changes */
public class AddPaymentDetailActivity extends AppCompatActivity implements View.OnClickListener,Response.ErrorListener,Response.Listener<JSONObject>{
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
    private TextView payPalEmail;
    private ProgressDialog pd;
    private EditText bankName,bankAddress;

    // note that these credentials will differ between live & sandbox environments.

    //private static final String CONFIG_CLIENT_ID = "ARZnTE2-_ntS7IRrw_Q7YF4uy8fYnD1Nb0WP88_ee0I2zEoUno5ad1Dw3ZII5nmyBW3ow42aYzioNkQa";
    private static final String CONFIG_CLIENT_ID = "AVHJDF560jMlIOVIChqkkAcPCUvmA-pD_EvO7GD9ih9Dr4DSEF-nP8PybIocWJxK1cYk5X9Q3OvLl-5i";
    private static PayPalConfiguration config = new PayPalConfiguration()
           .environment(CONFIG_ENVIRONMENT)
          //  .environment("sandbox")
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("ShopAves")
          //  .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
           // .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    .merchantPrivacyPolicyUri(Uri.parse("http://amsyt.com/"))
            .merchantUserAgreementUri(Uri.parse("http://amsyt.com/"));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_detail);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.add_payment_details));
        payPalEmail = (TextView)findViewById(R.id.paypalemail);
        bankName = (EditText)findViewById(R.id.bankname);
        bankAddress = (EditText)findViewById(R.id.address);
        findViewById(R.id.back_addrss).setOnClickListener(this);
        findViewById(R.id.paypalemail).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_addrss:
                finish();
                break;
            case R.id.paypalemail:
                onProfileSharingPressed();
                break;
            case R.id.save:
                if(!payPalEmail.getText().toString().equals("YOUR PAYPAL EMAIL ADDRESS"))
                AddAccountDetails("paypal");
                else if(TextUtils.isEmpty(bankName.getText().toString())){
                    Toast.makeText(AddPaymentDetailActivity.this,"Please enter bank name",Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(bankAddress.getText().toString())){
                    Toast.makeText(AddPaymentDetailActivity.this,"Please enter bank address",Toast.LENGTH_LONG).show();
                }else{
                    AddAccountDetails("bank");
                }
                break;
        }
    }

    private void AddAccountDetails(String type){

        pd = C.getProgressDialog(AddPaymentDetailActivity.this);
        JSONObject account = new JSONObject();
        try {
            if(type.equals("paypal")){
                account.put("email",payPalEmail.getText().toString());
            }else{
                account.put("name",bankName.getText().toString());
                account.put("address",bankAddress.getText().toString());
            }
            account.put("type",type);
            account.put("userId",new AppStore(this).getData(Constants.USER_ID));
            Net.makeRequest(C.APP_URL + ApiName.REGISTER_PAYPAL_API, account.toString(), AddPaymentDetailActivity.this, AddPaymentDetailActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS) );
        return new PayPalOAuthScopes(scopes);
    }
    public void onProfileSharingPressed() {
        Intent intent = new Intent(AddPaymentDetailActivity.this, PayPalProfileSharingActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());

        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
    }
    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("environment", auth.getEnvironment());
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));
                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);
                        payPalEmail.setText("demo@amsyt.com");
                        sendAuthorizationToServer(auth);
                        makeTokenRequest(authorization_code);
                        //displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }


    private void makeTokenRequest(String code){
        pd = C.getProgressDialog(this);
        String appUrl = "https://api.sandbox.paypal.com/v1/identity/openidconnect/tokenservice";
        try {
            HashMap hashMap = new HashMap();
            hashMap.put("grant_type", "authorization_code");

            hashMap.put("code", code);
            C.API_TYPE = "PAYPAL";
            Net.makeRequestParams(appUrl, hashMap, this, this);
        }catch (Exception e){

        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(AddPaymentDetailActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Toast.makeText(AddPaymentDetailActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
            setResult(200);
            finish();
        }else
            Toast.makeText(AddPaymentDetailActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
    }
}
