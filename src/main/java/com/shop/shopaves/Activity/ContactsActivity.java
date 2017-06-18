package com.shop.shopaves.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Constant.SideSelector;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.EndlessScrollListener;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ContactsActivity extends AppCompatActivity implements Response.ErrorListener {
    private RecyclerView recycleList;
    private boolean isSearching  = false;
    private ProgressDialog pd;
    private int listScrollPosition = 0;
    private ArrayList<Alphabet> dataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        recycleList = (RecyclerView) findViewById(R.id.list);
        final EditText search = (EditText)findViewById(R.id.search);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.concats_c));
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        search.setFocusable(false);
        search.setFocusableInTouchMode(true);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(!isSearching) {
                        isSearching = true;
                        makeRequest("", search.getText().toString());
                        hideSoftKeyboard();
                    }
                    Log.i("enterPressed","Enter pressed");
                    return  true;
                }
                return false;
            }
        });

        makeRequest("","");

    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) ContactsActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(ContactsActivity.this.getCurrentFocus().getWindowToken(), 0);
    }
    private  void makeRequest(String skip,String key){
        Map<String,String> map = new HashMap<>();
        map.put("skip",skip);
        map.put("key",key);
        Net.makeRequest(C.APP_URL+ ApiName.GET_CONTACTS,map,contact_response,this);
        pd =  C.getProgressDialog(this);
    }
    public Response.Listener<JSONObject> contact_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject);
            Log.e("contacts_response",jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)) {
                if(isSearching) {
                    dataList.clear();
                    isSearching = false;
                }
                    Model dataArray[] = model.getDataArray();
                    for (Model contacts : dataArray) {
                        Alphabet alphabet = new Alphabet(contacts.getName(),contacts.getImageUrl(),""+contacts.getTimeStampSmall(),contacts.getId(),false);
                        dataList.add(alphabet);
                    }
                setContactAdapter();

            }
        }
    };

    private void setContactAdapter(){
        ContactAdapter contactAdapter = new ContactAdapter(dataList);
        recycleList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        recycleList.setLayoutManager(linearLayoutManager);
        recycleList.setAdapter(contactAdapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(ContactsActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }



    private class ContactAdapter extends RecyclerView.Adapter<ViewHolder>{
        ArrayList<Alphabet> source;

        public ContactAdapter(ArrayList<Alphabet> source) {
            this.source = source;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ContactsActivity.this).inflate(R.layout.row_contact,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.name.setText(source.get(position).mName);
            if (!TextUtils.isEmpty(source.get(position).image))
            PicassoCache.getPicassoInstance(ContactsActivity.this).load(C.ASSET_URL+source.get(position).image).placeholder(R.drawable.male).error(R.drawable.male).into(holder.img);
            holder.timestamp .setText("Joined "+ DateUtils.getRelativeTimeSpanString(C.getDateInMillis(source.get(position).time), Calendar.getInstance().getTimeInMillis()-19800000, DateUtils.DAY_IN_MILLIS));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(ContactsActivity.this,ProfileChatActivity.class).putExtra(Constants.USER_ID,source.get(position).id).putExtra(Constants.NAME,source.get(position).mName));
                }
            });
        }

        @Override
        public int getItemCount() {
            return source.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,timestamp;
        CircleImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
           name = (TextView) itemView.findViewById(R.id.user_name);
           timestamp = (TextView) itemView.findViewById(R.id.timestamp);
           img = (CircleImageView) itemView.findViewById(R.id.brand_logo);
        }
    }
    private class Alphabet {
        public String mName, image,time;
        public boolean mIsSeparator;
        int id;

        public Alphabet(String name, String image,String time,int id, boolean isSeparator) {
            mName = name;
            this.time = time;
            this.image = image;
            this.id = id;
            mIsSeparator = isSeparator;
        }
    }
}
