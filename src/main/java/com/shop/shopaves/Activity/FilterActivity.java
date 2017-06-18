package com.shop.shopaves.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Constant.Items;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.NamePair;
import com.shop.shopaves.dataModel.AppStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    private ListView specificationList;
    private AppStore aps;
    private ItemAdapter itemAdapter;
    private TextView brandSelections,titleNameSpecification,selectedSpecificationItems,conditionSelections;
    private HashMap<String,ArrayList<String>> specificationHashMap = new HashMap<>();
    private ArrayList<String> selectedBrands = new ArrayList<>();
    private SwitchCompat switchCompat;
    private ArrayList<Items> selectedSpecificationNames = new ArrayList<>();
    private TextView selectedCounts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        specificationList = (ListView)findViewById(R.id.specificationlist);
        brandSelections = (TextView)findViewById(R.id.brandselections);
        TextView title = (TextView)findViewById(R.id.title);
        conditionSelections = (TextView)findViewById(R.id.conditionselection);
        switchCompat = (SwitchCompat)findViewById(R.id.isspecialoffer);
        selectedCounts = (TextView)findViewById(R.id.count);
        TextView clearFilter = (TextView)findViewById(R.id.toright);
        title.setText(getResources().getString(R.string.filter));
        clearFilter.setText(getResources().getString(R.string.clear));
        clearFilter.setVisibility(View.VISIBLE);
       // clearFilter.setAlpha(0.6f);
        aps = new AppStore(this);
        itemAdapter = new ItemAdapter(FilterActivity.this,0);

        if(getIntent() != null){
            if(getIntent().getStringExtra(Constants.TYPE).equals(Constants.DEALS)){
                findViewById(R.id.sploffr).setVisibility(View.GONE);
                findViewById(R.id.brandclick).setVisibility(View.GONE);
                specificationList.setVisibility(View.GONE);
            }
        }

        findViewById(R.id.brandclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(FilterActivity.this,ProductFilterActivity.class).putExtra(Constants.SELECTED_BRANDS,selectedBrands).putExtra(Constants.CONTENT_NAME, Constants.BRAND),200);
            }
        });

        findViewById(R.id.conditionclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(FilterActivity.this,SelectItemActivity.class).putExtra("CONTENT_NAME","CONDITION").putExtra("CONDITION",conditionSelections.getText().toString()),50);
            }
        });

        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(200,new Intent().putExtra(Constants.API_PARAMS,setParams()));
                finish();
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setSelecterFilterCount();
            }
        });
        clearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brandSelections.setText("");
                conditionSelections.setText("");
                switchCompat.setChecked(false);
                for(int i = 0; i<itemAdapter.getCount();i++)
                itemAdapter.getItem(i).selectedSpecifications = "";
                itemAdapter.notifyDataSetChanged();

                selectedBrands.clear();
                selectedSpecificationNames.clear();
            }
        });
        setSpecificationValues(getIntent().getStringExtra(Constants.SELECTED_ITEMS) != null ? getIntent().getStringExtra(Constants.SELECTED_ITEMS) : "");
        setSelecterFilterCount();
    }


    private void setSelectedBrandsValues(JSONArray selectedBrandsIds){
        Model model = new Model(aps.getData(Constants.PRODUCT_FILTER_VALUES));
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model brandArray[] = model.getBrandsArray();
            String brandsText = "";
            for(int i = 0; i< brandArray.length; i++){
                Model brand = brandArray[i];

                for(int j = 0; j<selectedBrandsIds.length();j++){
                    try {
                        if((""+brand.getId()).equals(selectedBrandsIds.get(j).toString())){
                            brandsText = brandsText + ","+brand.getName();
                            selectedBrands.add(brand.getName());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // itemAdapter.add(new Item(brand.getName(),""+brand.getId(),"",isselect));
            }
            if(brandsText.startsWith(","))
            brandSelections.setText(brandsText.substring(1));
        }
    }

    private void setSpecificationValues(String selectedFilterItems){
        boolean isSelected = false;
        Model selectedSpecificationArray[] = new Model[0];
        if(!TextUtils.isEmpty(selectedFilterItems)){
            isSelected = true;
            Model selectedFilter = new Model(selectedFilterItems);
            selectedSpecificationArray = selectedFilter.getSpcfArray();
            switchCompat.setChecked(selectedFilter.hasOffer());
            conditionSelections.setText(selectedFilter.getCondition());
            JSONArray selectedBrandsIds = selectedFilter.getBrandIdsArray();
            setSelectedBrandsValues(selectedBrandsIds);
  }
        Model model = new Model(aps.getData(Constants.PRODUCT_FILTER_VALUES));

        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model data = new Model(model.getData());
            Model specificationArray[] = data.getSpecificationArray();
            /*if(itemAdapter != null)
            itemAdapter.clear();*/
            for(Model specification : specificationArray){
                boolean isSelectedSpecification = false;
                if(!isSelected || selectedSpecificationArray.length<1)
                itemAdapter.add(new Item(specification.getName(),specification.getString(NamePair.VALUES),""+specification.getId(),""));
                else{
                    for(Model spcf : selectedSpecificationArray){
                        if(spcf.getId() == specification.getId()){

                            String spcfValues = "";
                            JSONArray valuesArray = spcf.getJsonValuesArray();
                            ArrayList<String> selectedSpcfNameArray = new ArrayList<>();
                            for(int i = 0; i<valuesArray.length();i++){
                                try {
                                    spcfValues = spcfValues +","+ valuesArray.get(i).toString();
                                    selectedSpcfNameArray.add(valuesArray.get(i).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            selectedSpecificationNames.add(new Items(spcf.getName(),spcf.getid()));
                            specificationHashMap.put(spcf.getName(),selectedSpcfNameArray);
                            itemAdapter.add(new Item(specification.getName(),specification.getString(NamePair.VALUES),""+specification.getId(),spcfValues.substring(1)));
                            isSelectedSpecification = true;
                            break;
                        }
                    }
                    if(!isSelectedSpecification)
                        itemAdapter.add(new Item(specification.getName(),specification.getString(NamePair.VALUES),""+specification.getId(),""));
                }
            }
            itemAdapter.notifyDataSetChanged();
            specificationList.setAdapter(itemAdapter);
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_filter_selection, parent, false);
            } else {
                view = convertView;
            }
            final Item item = getItem(position);
            final TextView name = (TextView) view.findViewById(R.id.itemname);
            final ImageView selective = (ImageView) view.findViewById(R.id.ic);
            final ImageView dropright = (ImageView) view.findViewById(R.id.drright);
            SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.isoffer);
            TextView selectionSpf = (TextView)view.findViewById(R.id.selectionnames);
            name.setTag(item.specificationArray);
            name.setText(item.name);
            selectionSpf.setTag(item.id);
            selective.setImageBitmap(null);
            switchCompat.setVisibility(View.GONE);
            dropright.setVisibility(View.VISIBLE);
            selectionSpf.setText(item.selectedSpecifications);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleNameSpecification = (TextView)v.findViewById(R.id.itemname);
                    selectedSpecificationItems = (TextView) v.findViewById(R.id.selectionnames);
                    Intent intent = new Intent(FilterActivity.this,ProductFilterActivity.class);
                    intent.putExtra(Constants.CONTENT_NAME, Constants.SPECIFICATION);
                    intent.putExtra(Constants.SPECIFICATION_ARRAY,""+name.getTag());
                    intent.putExtra(Constants.SELECTED_SPECIFICATIONS,specificationHashMap.get(name.getText().toString()));
                    startActivityForResult(intent,300);
                }
            });
            return view;
        }
    }

    class Item {
        public String name, id, specificationArray,selectedSpecifications;
        public Item(String name,String specificationArray, String id, String selectedSpecifications) {
            this.name = name;
            this.id = id;
            this.specificationArray = specificationArray;
            this.selectedSpecifications = selectedSpecifications;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 200){
            ArrayList<String> brandsSelection= data.getStringArrayListExtra(Constants.SELECTED_BRANDS);
            if(brandsSelection.size()>0) {
                String collectBrands = "";
                selectedBrands.clear();
                for (int i = 0; i < brandsSelection.size(); i++) {
                    if (i == 0)
                        collectBrands = collectBrands + brandsSelection.get(i);
                    else
                        collectBrands = collectBrands + "," + brandsSelection.get(i);
                    selectedBrands.add(brandsSelection.get(i));
                }
                brandSelections.setText(collectBrands);
             //  selectedBrands = data.getStringArrayListExtra("NAME");
            }else{
                selectedBrands.clear();
                brandSelections.setText("");
            }
            //brandSelections.setText(data.getStringExtra("selected_brands"));
        }else if(resultCode == 300){
            ArrayList<String> madd= data.getStringArrayListExtra(Constants.SELECTED_SPECIFICATIONS);
            if(madd.size()>0) {
              //  ArrayList<Items> items = new ArrayList<>();
                String collectBrands = "";
                selectedSpecificationNames.clear();
                for (int i = 0; i < madd.size(); i++) {
                    if (i == 0)
                        collectBrands = collectBrands + madd.get(i);
                    else
                        collectBrands = collectBrands + "," + madd.get(i);
                }
                selectedSpecificationItems.setText(collectBrands);
                specificationHashMap.put(titleNameSpecification.getText().toString(), madd);

                if(selectedSpecificationNames.size() > 0){
                    for(int i = 0; i<selectedSpecificationNames.size();i++){
                        if(!selectedSpecificationNames.get(i).itemName.contains(titleNameSpecification.getText().toString()))
                            selectedSpecificationNames.add(new Items(titleNameSpecification.getText().toString(),""+selectedSpecificationItems.getTag()));
                    }
                }else
                    selectedSpecificationNames.add(new Items(titleNameSpecification.getText().toString(),""+selectedSpecificationItems.getTag()));


           /* String specificationNames = "";
            //  valuesSpecification.setText(data.getStringExtra("NAME").substring(1));
            specificationHashMap.put(titleNameSpecification.getText().toString(),data.getStringArrayListExtra("NAME"));

            if(data.getStringArrayListExtra("NAME").size()>0) {
                for (String name : data.getStringArrayListExtra("NAME")) {
                    specificationNames = specificationNames + "," + name;
                }
                selectedSpecificationItems.setText(specificationNames.substring(1));
            }else{
                selectedSpecificationItems.setText("");
            }*/
            }
            else{
                /*if(selectedSpecificationNames.contains(titleNameSpecification.getText().toString()))
                    selectedSpecificationNames.remove(titleNameSpecification.getText().toString());*/
                selectedSpecificationNames.clear();
                selectedSpecificationItems.setText("");

            }
        }
        else if(resultCode == 100){
            conditionSelections.setText(data.getStringExtra("NAME"));
        }
        setSelecterFilterCount();
    }

    private String setParams(){
        JSONObject params = new JSONObject();
        try {
            params.put("hasOffer",""+switchCompat.isChecked());
            params.put("condition",conditionSelections.getText().toString());
            params.put("sortBy","1");
            params.put("spcf",getSpecification());
            params.put("bIds",getBrandIds());
            params.put("skip","0");
            params.put("key","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("params",params.toString());
        return params.toString();
    }

    private JSONArray getBrandIds(){
        JSONArray brandsArray = new JSONArray();
        Model model = new Model(aps.getData(Constants.PRODUCT_FILTER_VALUES));
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            Model brandArray[] = model.getBrandsArray();
            itemAdapter.clear();
            boolean isselect = false;
            for(int i = 0; i< brandArray.length; i++){
                Model brand = brandArray[i];
                isselect = false;
                for(int j = 0; j<selectedBrands.size();j++){
                    if(brand.getName().equals(selectedBrands.get(j))){
                        isselect = true;
                        break;
                    }
                }
               // itemAdapter.add(new Item(brand.getName(),""+brand.getId(),"",isselect));
                if(isselect)
                    brandsArray.put(brand.getId());
            }
        }
        Log.e("brandsID",brandsArray.toString());
        return brandsArray;
    }

    private JSONArray getSpecification(){
        JSONArray allSelectedSpecification = new JSONArray();

        for(int i = 0; i<selectedSpecificationNames.size();i++){
         //  specificationArray.put(specificationHashMap.get(selectedSpecificationNames.get(i).itemName));
              JSONArray specificationArray = new JSONArray();
            for(int k = 0; k<specificationHashMap.get(selectedSpecificationNames.get(i).itemName).size();k++ ){
                Log.e("spec",specificationHashMap.get(selectedSpecificationNames.get(i).itemName).get(k));
                specificationArray.put(specificationHashMap.get(selectedSpecificationNames.get(i).itemName).get(k));
            }
            JSONObject specificationObject = new JSONObject();
            try {
                specificationObject.put("name",selectedSpecificationNames.get(i).itemName);
                specificationObject.put("id",selectedSpecificationNames.get(i).itemId);
                specificationObject.put("values",specificationArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("selectedspicationArray",specificationObject.toString());
            allSelectedSpecification.put(specificationObject);
        }
        return allSelectedSpecification;
    }


    private void setSelecterFilterCount(){
        List<String> keySet = new ArrayList<String>(specificationHashMap.keySet());
        int count = 0;
        for(int i = 0; i < keySet.size(); i++){
            count = count + specificationHashMap.get(keySet.get(i)).size();
        }
            count = count + selectedBrands.size()+ (switchCompat.isChecked() ? 1 : 0);
        selectedCounts.setText(""+count+" Selected");
    }

    /*{
  "hasOffer": false,
  "condition": "",
  "sortBy": 1,
  "bIds": [142],
  "spcf": [{
    "name": "OTG Compatible",
    "id": "386",
    "values": [
      "Yes"
    ]
  }],
  "skip": 0,
  "key": ""
}*/
}
