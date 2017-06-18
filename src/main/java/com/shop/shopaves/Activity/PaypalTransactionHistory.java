package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaypalTransactionHistory extends AppCompatActivity implements View.OnClickListener,Response.Listener<JSONObject>,Response.ErrorListener{

    private RecyclerView historyRecyclerView;
    private ArrayList<MyItems> historyList = new ArrayList<>();
    private TransactionHistoryAdapter transactionHistoryAdapter;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_transaction_history);
        historyRecyclerView = (RecyclerView)findViewById(R.id.history);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.paypal_email));
        findViewById(R.id.back_addrss).setOnClickListener(this);
        makeHistoryRequest(getIntent().getStringExtra(Constants.ID));
        ((TextView)findViewById(R.id.email)).setText(getIntent().getStringExtra(Constants.EMAIL));
        ((TextView)findViewById(R.id.date)).setText("Added on "+getIntent().getStringExtra(Constants.DATE));
    }


    private void makeHistoryRequest(String accountId){
        pd = C.getProgressDialog(this);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("userId",new AppStore(this).getData(Constants.USER_ID));
        hashMap.put("skip","");
        hashMap.put("accId",accountId);
        Net.makeRequest(C.APP_URL+ ApiName.TRANSACTION_HISTORY_API,hashMap,this,this);
    }

    private void setTransactionHistoryAdapter(){
        transactionHistoryAdapter = new TransactionHistoryAdapter(historyList);
        historyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(PaypalTransactionHistory.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historyRecyclerView.setLayoutManager(layoutManager);
        historyRecyclerView.setAdapter(transactionHistoryAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_addrss:
                finish();
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("historyResponse",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model dataArray[] =model.getDataArray();
            for(Model data : dataArray){
                historyList.add(new MyItems(data.getType(),C.FormatterValue(Float.parseFloat(data.getAmount())),data.getTimeStampSmall()));
            }
            setTransactionHistoryAdapter();
        }
    }

    private class TransactionHistoryAdapter extends RecyclerView.Adapter<MyHolder>{
        private List<MyItems> source;
        public TransactionHistoryAdapter(List<MyItems> source) {
            this.source = source;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = (LayoutInflater.from(PaypalTransactionHistory.this)).inflate(R.layout.transaction_history,parent,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            final MyItems myItems = source.get(position);
            holder.type.setText(myItems.itemName);
            holder.time.setText(C.parseDateToddMMyyyy(myItems.time));
            holder.amount.setText("+ $"+myItems.amount);
        }


        @Override
        public int getItemCount() {
            return source.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder{

        TextView time,amount,type;
         public MyHolder(View itemView) {
             super(itemView);
             time = (TextView)itemView.findViewById(R.id.time);
             amount = (TextView)itemView.findViewById(R.id.amount);
             type = (TextView)itemView.findViewById(R.id.transtype);
         }
     }

    private class MyItems{
        private String itemName,amount,time;

        public MyItems(String itemName, String amount, String time) {
            this.itemName = itemName;
            this.amount = amount;
            this.time = time;
        }
    }
}
