package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StripePaymentActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edit_card_number;
    private Spinner spinner_exp_month;
    private Spinner spinner_exp_year;
    private EditText edit_cvc_number;
    private Button btn_make_payment;
    private AppStore aps;

    private ProgressDialog progress_creating_token;

    private static final String publishableKey = "pk_test_lQzesoXSZwH2fqeeQwjtKxN7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        edit_card_number = (EditText) findViewById(R.id.edit_card_number);
        spinner_exp_month = (Spinner) findViewById(R.id.spinner_exp_month);
        spinner_exp_year = (Spinner) findViewById(R.id.spinner_exp_year);
        edit_cvc_number = (EditText) findViewById(R.id.edit_cvc_number);
        btn_make_payment = (Button) findViewById(R.id.btn_make_payment);

        btn_make_payment.setOnClickListener(this);

        progress_creating_token = new ProgressDialog(this);
        progress_creating_token.setMessage("Creating Token...");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        stopProgress();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_make_payment) {
            if (isDataComplete()) {
                String cardNumber = edit_card_number.getText().toString();
                int cardExpMonth = Integer.parseInt(spinner_exp_month
                        .getSelectedItem().toString());
                int cardExpYear = Integer.parseInt(spinner_exp_year
                        .getSelectedItem().toString());
                String cardCVC = edit_cvc_number.getText().toString();

                Stripe stripe = new Stripe(this,publishableKey);

                // Create Card instance containing customer's payment
                // information obtained
                Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);

                // Check if card is valid. If valid, create token
                if (card.validateCard()) {

                    startProgress();
                    stripe.createToken(card, new TokenCallback() {

                        @Override
                        public void onSuccess(Token token) {
                            stopProgress();
                            Toast.makeText(StripePaymentActivity.this, "Token created successfully!", Toast.LENGTH_SHORT).show();
                            chargeCustomer(token);

                        }

                        @Override
                        public void onError(Exception error) {
                            stopProgress();
                            showAlert("Validation Error",
                                    error.getLocalizedMessage());

                            Log.e("Error in creating token",
                                    error.toString());
                        }
                    });
                } else {
                    showAlert("Invalid Details",
                            "Card details are invalid. Enter valid data");
                    edit_card_number.setText(null);
                    edit_cvc_number.setText(null);
                    spinner_exp_month.setSelection(0);
                    spinner_exp_year.setSelection(0);
                }

            }
        }

    }

    public void chargeCustomer(Token token) {
        if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
        {
            Map<String,String> map = new HashMap<>();
            map.put("uid",aps.getData(Constants.USER_ID));
            map.put("amount", "400");
            map.put("paymentId",token.getId());
            Net.makeRequest(C.APP_URL+ ApiName.CHECK_OUT_API,map,r_pay,error);
            Log.e("payment_param",map.toString());
        }
        else{
            C.setLoginMessage(this);
        }

}
    Response.Listener<JSONObject> r_pay = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {

            Log.e("payment_response",jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                startActivity(new Intent(StripePaymentActivity.this,PaymentSuccessFailed.class));
                finish();
            }else{
                startActivity(new Intent(StripePaymentActivity.this,PaymentFailedActivity.class));
            }
        }
    };

    public Response.ErrorListener error = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {

            Log.e("error",volleyError.toString());
            Toast.makeText(StripePaymentActivity.this, "" + VolleyErrors.setError(volleyError), Toast.LENGTH_LONG).show();
        }
    };
    private boolean isDataComplete() {
        boolean isDataComplete = true;

        if (edit_card_number.getText().length() == 0) {
            isDataComplete = false;
            edit_card_number.setError("Enter Card Number");
        }

        if (edit_cvc_number.getText().length() == 0) {
            isDataComplete = false;
            edit_cvc_number.setError("Enter CVC Number");
        }

        if (spinner_exp_month.getSelectedItemPosition() == 0
                || spinner_exp_year.getSelectedItemPosition() == 0) {
            isDataComplete = false;

            if (spinner_exp_month.getSelectedItemPosition() == 0
                    && spinner_exp_year.getSelectedItemPosition() == 0) {
                showAlert("Incomplete Data!", "Enter expiry month and year");
            } else if (spinner_exp_month.getSelectedItemPosition() == 0) {
                showAlert("Incomplete Data!", "Enter expiry month");
            } else if (spinner_exp_year.getSelectedItemPosition() == 0) {
                showAlert("Incomplete Data!", "Enter Expiry Year");
            }
        }

        return isDataComplete;

    }

    private void showAlert(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dialog.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void startProgress() {
        progress_creating_token.show();
    }
    public static  void setLoginMessage(Context context){
        Toast.makeText(context, "you have to login first for this operation.", Toast.LENGTH_SHORT).show();
    }

    public void stopProgress() {
        if (progress_creating_token.isShowing()) {
            progress_creating_token.dismiss();
        }
    }


}
