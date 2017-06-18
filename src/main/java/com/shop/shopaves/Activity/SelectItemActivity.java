package com.shop.shopaves.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Interface.ValueCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.dataModel.NewCategory;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectItemActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView  contentTitle;
    private ListView itemList;
    private ItemAdapter adapter;
    private String type = "";
    private AppStore aps;
    private Intent intent;
    private String categoryName,categoryId;
    private String specificationNames = "";
    private ArrayList<String> productSpecification = new ArrayList<>();
    private Intent sendIntent;
    private int selectedPosition = -1;
    private boolean firstLoad = true;
    private String OTHER_TYPE = "";
    private String specificationSelectiontype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_location);
        C.applyTypeface(C.getParentView(findViewById(R.id.activity_item_location)), C.getHelveticaNeueFontTypeface(SelectItemActivity.this));
         contentTitle = (TextView) findViewById(R.id.content_title);
        itemList = (ListView) findViewById(R.id.itemlist);
        adapter = new ItemAdapter(this, 0);

        aps = new AppStore(this);
         intent = getIntent();
        if (intent.getStringExtra("CONTENT_NAME").equals("CONDITION")) {
            contentTitle.setText("CONDITION");
            type = "CONDITION";
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            String condition = intent.getStringExtra("CONDITION");
            adapter.add(new Item("New", "0", condition.equals("New") ? true : false));
            adapter.add(new Item("Used", "1", condition.equals("Used")? true : false));
            itemList.setAdapter(adapter);
        } else if (intent.getStringExtra("CONTENT_NAME").equals("RETURN")) {
            contentTitle.setText("RETURNS");
            type = "RETURN";
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            int returnid = intent.getIntExtra("RETURN_ID",0);
            adapter.add(new Item("No Returns Accepted", "0", returnid == 0? true : false));
            adapter.add(new Item("Return Accepted", "1", returnid == 1? true : false));
            itemList.setAdapter(adapter);
        }/*else if (intent.getStringExtra("CONTENT_NAME").equals("PACKING_TYPE")) {
            contentTitle.setText("PACKAGING TYPE");
            type = "PACKAGING";
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            int returnid = intent.getIntExtra("RETURN_ID",0);
            adapter.add(new Item("UPS", "0", returnid == 0? true : false));
            adapter.add(new Item("USPS", "1", returnid == 1? true : false));
            itemList.setAdapter(adapter);
        }*/ else if (intent.getStringExtra("CONTENT_NAME").equals("CATEGORY")) {
            type = "CATEGORY";
            contentTitle.setText("CATEGORIES");
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            Model model = new Model(aps.getData(Constants.PUBLIC_COLLECTION_CATEGORY));
            Model categoryArray[] = model.getDataArray();
            for (Model category : categoryArray) {
                adapter.add(new Item(category.getName(), "" + category.getId(),false));
            }
            itemList.setAdapter(adapter);
        } else if (intent.getStringExtra("CONTENT_NAME").equals("CATEGORY_PRODUCT") || intent.getStringExtra("CONTENT_NAME").equals(Constants.EDIT_COLLECTION) || intent.getStringExtra("CONTENT_NAME").equals(Constants.PRODUCT_CATEGORY)) {
            type = "CATEGORY";
            if(intent.getStringExtra("CONTENT_NAME").equals(Constants.EDIT_COLLECTION)){
                OTHER_TYPE = Constants.EDIT_COLLECTION;
            }else if(intent.getStringExtra("CONTENT_NAME").equals("CATEGORY_PRODUCT"))
                OTHER_TYPE = Constants.CREATE_PRODUCT;
            if(intent.getStringExtra("CONTENT_NAME").equals(Constants.PRODUCT_CATEGORY))
            OTHER_TYPE = Constants.PRODUCT_CATEGORY;
           // OTHER_TYPE = Con
            findViewById(R.id.select).setVisibility(View.GONE);
            contentTitle.setText("SELECT CATEGORY");
            findViewById(R.id.listview).setVisibility(View.VISIBLE);

            List<NewCategory> categoryList = NewCategory.listAll(NewCategory.class);
            for (int i = 0; i < categoryList.size(); i++) {
                NewCategory category = categoryList.get(i);
                adapter.add(new Item(category.name, "" + category.categoryId,false));
            }
            itemList.setAdapter(adapter);
        } else if (intent.getStringExtra("CONTENT_NAME").equals("SUBCATEGORY")) {
            type = "SUBCATEGORY";
            contentTitle.setText("SUBCATEGORY");
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
           /* List<Category> categoryList = Category.listAll(Category.class);
            for (Category category : categoryList) {
                Model m[] = new Model().getArray(category.subCategory);
                if (category.name.equals(intent.getStringExtra("CATEGORY"))) {
                    for (Model m_sub : m)
                        adapter.add(new Item(m_sub.getName(), "" + m_sub.getId(),false));
                }
            }*/
            itemList.setAdapter(adapter);
        } else if (intent.getStringExtra("CONTENT_NAME").equals("HANDLING_TIME")) {
            type = "HANDLING TIME";
            contentTitle.setText("HANDLING TIME");
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            int handling = intent.getIntExtra("HANDLING_ID",0);
            setHandingTime(handling);
            itemList.setAdapter(adapter);
        } else if (intent.getStringExtra("CONTENT_NAME").equals("BRAND")) {
            contentTitle.setText("BRANDS");
            type = "BRAND";
            String brandId = intent.getStringExtra("BRAND_ID");
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            Model model = new Model(getIntent().getStringExtra("BRANDS_DATA"));
            if(!model.getStatus().equals(Constants.REQUEST_FAIL_CODE)) {
                Model brandsArray[] = model.getBrandsArray();
                for (Model brand : brandsArray) {
                    adapter.add(new Item(brand.getName(), "" + brand.getId(), (""+brand.getId()).equals(brandId) ? true : false));
                }
              itemList.setAdapter(adapter);
            }
        } else if(intent.getStringExtra("CONTENT_NAME").equals("VALUES_ARRAY")){
            findViewById(R.id.addcustom).setVisibility(View.VISIBLE);
            boolean select = false;
            type = "SPECIFICATION";
            contentTitle.setText(intent.getStringExtra("CONTENT_NA"));
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            if(intent.getStringExtra("SPECIFICATION_SELECTION_TYPE") != null){
                specificationSelectiontype = intent.getStringExtra("SPECIFICATION_SELECTION_TYPE");
                String selected = intent.getStringExtra("SELECTED_SPECIFICATIONS");
                try {
                    JSONArray valueArray = new JSONArray(intent.getStringExtra("valueArray"));
                    for(int i = 0; i< valueArray.length();i++){
                        select = false;
                        if(selected.equals(valueArray.get(i)) && !select){
                            select  =  true;
                        }
                        adapter.add(new Item(valueArray.getString(i),"",select));
                        itemList.setAdapter(adapter);
                    }
                } catch (JSONException e) {

                }

            }else{
                try {
                    JSONArray valueArray = new JSONArray(intent.getStringExtra("valueArray"));
                    if(intent.getStringArrayListExtra("SELECTED_SPECIFICATIONS")!=null && intent.getStringArrayListExtra("SELECTED_SPECIFICATIONS").size() > 0){
                        if(!TextUtils.isEmpty(intent.getStringArrayListExtra("SELECTED_SPECIFICATIONS").get(0)))
                            productSpecification = intent.getStringArrayListExtra("SELECTED_SPECIFICATIONS");
                    }

                    for(int i = 0; i< valueArray.length();i++){
                        select = false;

                        if(productSpecification.size()>0) {
                            for (int j = 0; j < productSpecification.size(); j++) {
                                if (productSpecification.get(j).equals(valueArray.getString(i))) {
                                    select = true;
                                    break;
                                }
                            }
                        }
                        adapter.add(new Item(valueArray.getString(i),"",select));
                    }
                    itemList.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }else if(intent.getStringExtra("CONTENT_NAME").equals("CATEGORY_COLLECTION")){
            type = "CATEGORY_COLLECTION";
            contentTitle.setText("CATEGORIES");
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            Model model = new Model(aps.getData(Constants.PUBLIC_COLLECTION_CATEGORY));
            Model categoryArray[] = model.getDataArray();
            for (Model category : categoryArray) {
                adapter.add(new Item(category.getName(), "" + category.getId(),false));
            }
            itemList.setAdapter(adapter);
        }else if(intent.getStringExtra("CONTENT_NAME").equals("SPECIFICATION")){
            type = "SPECIFICATION";
        }/*else if(intent.getStringExtra("CONTENT_NAME").equals("REGION")){
            type = "REGION";
            contentTitle.setText("REGION");
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            try {
                JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
                Log.e("region data",jsonArray.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Model model = new Model(loadJSONFromAsset());
            Model regionArray[] = model.getArray(loadJSONFromAsset());
            for(Model data : regionArray){
                adapter.add(new Item(data.getName(),"",false));
            }
            itemList.setAdapter(adapter);
        }*/
        else if(intent.getStringExtra("CONTENT_NAME").equals("SHIPPING")){
            type = "SHIPPING";
                for(int i = 0; i<Constants.SHIPPING_TYPE.length;i++){
                    adapter.add(new Item(Constants.SHIPPING_TYPE[i], ""+i,false));
                }
            itemList.setAdapter(adapter);
        }

        else if(intent.getStringExtra("CONTENT_NAME").equals("SHIPPING_SERVICE")){
            type = "SHIPPING_SERVICE";
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(getIntent().getStringExtra(Constants.DATA));
                for (int i = 0; i < jsonArray.length(); i++) {
                    adapter.add(new Item(jsonArray.get(i).toString(), "" + i, false));
                }
            }                catch (JSONException e) {
                e.printStackTrace();
            }
            itemList.setAdapter(adapter);
        }

        findViewById(R.id.back_addrs).setOnClickListener(this);
        findViewById(R.id.select).setOnClickListener(this);
        findViewById(R.id.addcustom).setOnClickListener(this);
    }

    private void setHandingTime(int handling){
        adapter.add(new Item("1 Business Day", "1", handling == 1? true : false));
        adapter.add(new Item("2 Business Day", "2", handling == 2? true : false));
        adapter.add(new Item("3 Business Day", "3", handling == 3? true : false));
        adapter.add(new Item("4 Business Day", "4", handling == 4? true : false));
        adapter.add(new Item("5 Business Day", "5", handling == 5? true : false));
        adapter.add(new Item("6 Business Day", "6", handling == 6? true : false));
        adapter.add(new Item("7 Business Day", "7", handling == 7? true : false));
    }

    private void makeSubcategoryList(){
        type = "SUBCATEGORY";
        contentTitle.setText("SUBCATEGORY");
        adapter.clear();
        findViewById(R.id.select).setVisibility(View.VISIBLE);
        findViewById(R.id.listview).setVisibility(View.VISIBLE);
       /* List<Category> categoryList = Category.listAll(Category.class);
        for (Category category : categoryList) {
            Model m[] = new Model().getArray(category.subCategory);
            if (category.name.equals(categoryName)) {
                for (Model m_sub : m)
                    adapter.add(new Item(m_sub.getName(), "" + m_sub.getId(),false));
            }
        }*/
        itemList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
             case R.id.back_addrs:
                finish();
                break;
            case R.id.select:
                setSelection();
                break;
            case R.id.addcustom:
                C.customDialog(SelectItemActivity.this,valueCallBack);
                break;
        }
    }

    private void setSelection(){
        if(type.equals("SUBCATEGORY")){
            setResult(400, sendIntent);
            finish();
        }else if(type.equals("CATEGORY")){
            if(!TextUtils.isEmpty(categoryName))
            makeSubcategoryList();
            else
                finish();
        }else if(type.equals("BRAND")){
            setResult(700, sendIntent);
            finish();
        }else if(type.equals("HANDLING TIME")){
            setResult(600, sendIntent);
            finish();
        }else if(type.equals("RETURN")){
            setResult(300, sendIntent);
            finish();
        }else if(type.equals("PACKAGING")){
            setResult(40, sendIntent);
            finish();
        }else if(type.equals("CONDITION")){
            setResult(100, sendIntent);
            finish();
        }else if(type.equals("CATEGORY_COLLECTION")){
            setResult(400, sendIntent);
            finish();
        }else if(type.equals("SPECIFICATION")){
            if(specificationSelectiontype.equals("single")){
                setResult(30, sendIntent);
            }else{
                if(productSpecification.size()>0) {
                    for (String name : productSpecification) {
                        specificationNames = specificationNames + "," + name;
                    }
                    setResult(20, new Intent().putExtra("NAME", productSpecification));
                }else
                    setResult(20, new Intent().putExtra("NAME", productSpecification));
            }

            finish();
        }else if(type.equals("SHIPPING")){
            setResult(100,sendIntent);
            finish();
        }
        else if(type.equals("SHIPPING_SERVICE")){
            setResult(200,sendIntent);
            finish();
        }else{
            finish();
        }
    }

    class ItemAdapter extends ArrayAdapter<Item> {

        public ItemAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_category_selection, parent, false);
            } else {
                view = convertView;
            }
                final Item item = getItem(position);
                final TextView name = (TextView) view.findViewById(R.id.itemname);
                final ImageView img = (ImageView) view.findViewById(R.id.ic);
                final ImageView dropright = (ImageView) view.findViewById(R.id.drright);
            dropright.setVisibility(View.GONE);

            name.setText(item.name);
            img.setImageBitmap(null);

            if(firstLoad) {
                if (type.equals("CATEGORY")) {
                    dropright.setVisibility(View.VISIBLE);
                } else if (item.isSelected) {
                    img.setImageResource(R.drawable.check);
                    img.setTag("1");
                } else {
                    img.setImageResource(R.drawable.unchecked);
                    img.setTag("0");
                }
            }

            else if (selectedPosition == position) {
                    img.setImageResource(R.drawable.check);
                   // selectedPosition = -1;
                }else
                img.setImageResource(R.drawable.unchecked);

            view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPosition = position;
                        firstLoad = false;
                        sendIntent = new Intent();
                        sendIntent.putExtra("NAME", name.getText().toString());
                        sendIntent.putExtra("id", item.id);

                        if (type.equals("SPECIFICATION")) {
                            if(specificationSelectiontype.equals("single")){
                                notifyDataSetChanged();
                            }else{
                                if (img.getTag().equals("1")) {
                                    img.setImageResource(R.drawable.unchecked);
                                    img.setTag("0");
                                    for(int i = 0; i<productSpecification.size();i++){
                                        if(productSpecification.get(i).equals(name.getText().toString()))
                                            productSpecification.remove(i);
                                    }
                                } else {
                                    img.setImageResource(R.drawable.check);
                                    img.setTag("1");
                                    productSpecification.add(name.getText().toString());
                                }
                            }

                        }
                        if (type.equals("CATEGORY")) {
                            /*selectedPosition = -1;
                            categoryName = name.getText().toString();
                            categoryId = item.id;
                            makeSubcategoryList();*/
                            categoryId = item.id;
                            startActivityForResult(new Intent(SelectItemActivity.this,SubCategoryActivity.class).putExtra(Constants.PARENT_ID,Integer.valueOf(item.id)).putExtra(Constants.NAME,item.name.toUpperCase()).putExtra(Constants.TYPE,OTHER_TYPE),400);
                        } else if (type.equals("SUBCATEGORY")) {
                            img.setImageResource(R.drawable.check);
                            sendIntent.putExtra("CategoryName", categoryName);
                            sendIntent.putExtra("CategoryId", categoryId);
                            notifyDataSetChanged();

                        } else if (type.equals("BRAND")) {
                           notifyDataSetChanged();
                        }else if(type.equals("CATEGORY_COLLECTION")){
                            notifyDataSetChanged();
                        }
                        else if(type.equals("HANDLING TIME")){
                            notifyDataSetChanged();
                        }else if(type.equals("RETURN")){
                            notifyDataSetChanged();
                        }else if(type.equals("PACKAGING")){
                            notifyDataSetChanged();
                        }else if(type.equals("CONDITION")){
                            notifyDataSetChanged();
                        }else if(type.equals("SHIPPING")){
                            notifyDataSetChanged();
                        }else if(type.equals("SHIPPING_SERVICE")){
                            notifyDataSetChanged();
                        }
                    }
                });
            return view;
        }
    }

    public class Item{
      public String name,id;

        boolean isSelected = false;
        public Item(String name,String id,boolean isSelected) {
            this.name = name;
            this.id = id;
            this.isSelected = isSelected;
        }
    }
    ValueCallBack valueCallBack = new ValueCallBack() {
        @Override
        public void callBack(String value) {
            productSpecification.add(value);
            adapter.add(new Item(value,"",true));
            itemList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    };


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("Countries.json");
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 200){
            if(data != null){
                data.putExtra("id",categoryId);
                setResult(400, data);
            }
            finish();
        }
    }
}
