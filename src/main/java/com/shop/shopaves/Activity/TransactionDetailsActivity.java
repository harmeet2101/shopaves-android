package com.shop.shopaves.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class TransactionDetailsActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{

    private RecyclerView history;
    private ProgressDialog pd;
    private ArrayList<TransactionItems> transactionItemses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        history = (RecyclerView)findViewById(R.id.historylist);
        ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.payment_history));
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        makeTransactionHistoryRequest();
    }

    private void setAdapter(){
        HistoryAdapter historyAdapter = new HistoryAdapter(transactionItemses);
        history.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        history.setLayoutManager(linearLayoutManager);
        history.setAdapter(historyAdapter);
    }

    private void makeTransactionHistoryRequest(){
        pd = C.getProgressDialog(this);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("userId",new AppStore(this).getData(Constants.USER_ID));
        hashMap.put("skip","");
        Net.makeRequest(C.APP_URL+ ApiName.TRANSACTION_HISTORY_API,hashMap,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        Toast.makeText(TransactionDetailsActivity.this, VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("historyresponse",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Model dataArray[] = model.getDataArray();
            for(Model data : dataArray){
                transactionItemses.add(new TransactionItems(data));
            }
            setAdapter();
        }

    }

    class  HistoryAdapter extends RecyclerView.Adapter<MyViewHolder>{
            private ArrayList<TransactionItems> source;

        public HistoryAdapter(ArrayList<TransactionItems> source) {
            this.source = source;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history,parent,false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
           Model model = source.get(position).transactionModel;
            holder.date.setText(C.parseDateToddMMyyyy(model.getTimeStampSmall()));
            holder.amount.setText("+$ "+C.FormatterValue(Float.parseFloat(model.getAmount())));
            holder.type.setText(model.getType());
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }


    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date,type,amount;
        public MyViewHolder(View itemView) {
            super(itemView);
            date = (TextView)itemView.findViewById(R.id.time);
            type = (TextView)itemView.findViewById(R.id.transtype);
            amount = (TextView)itemView.findViewById(R.id.amount);
        }
    }

    private class TransactionItems{
        Model transactionModel;

        public TransactionItems(Model transactionModel) {
            this.transactionModel = transactionModel;
        }
    }

}
