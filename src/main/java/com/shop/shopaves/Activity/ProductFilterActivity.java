package com.shop.shopaves.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.dataModel.AppStore;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ProductFilterActivity extends AppCompatActivity {
    private ListView itemList;
    private ItemAdapter itemAdapter;
    private AppStore aps;
    private TextView count;
    private int counting = 0;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private Intent intent;
    private String type = Constants.BRAND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_filter);
        itemList = (ListView)findViewById(R.id.itemlist);
        count= (TextView)findViewById(R.id.count);
        aps = new AppStore(this);
        itemAdapter = new ItemAdapter(this,0);

        if(getIntent().getStringArrayListExtra(Constants.SELECTED_BRANDS)!=null)
            selectedItems = getIntent().getStringArrayListExtra(Constants.SELECTED_BRANDS);
        else if(getIntent().getStringArrayListExtra(Constants.SELECTED_SPECIFICATIONS)!=null)
            selectedItems = getIntent().getStringArrayListExtra(Constants.SELECTED_SPECIFICATIONS);


        findViewById(R.id.back_addrs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String collectBrands = "";

              //  Intent intent = new Intent();
                if(type.equals(Constants.BRAND)){
                  //  intent.putExtra("selected_brands",collectBrands);
                  //  intent.putExtra("NAME",brandSelection);
                   // setResult(200,new Intent().putExtra("selected_brands",collectBrands).putExtra("NAME",brandSelection));

                    setResult(200,new Intent().putExtra(Constants.SELECTED_BRANDS,selectedItems));
                    finish();
                }
                else if(type.equals(Constants.SPECIFICATION)){
                    //intent.putExtra("NAME",brandSelection);
                    //setResult(300,new Intent().putExtra("NAME",brandSelection));
                    setResult(300,new Intent().putExtra(Constants.SELECTED_SPECIFICATIONS,selectedItems));
                    finish();
                }

            }
        });
        intent = getIntent();
        if (intent.getStringExtra(Constants.CONTENT_NAME).equals(Constants.SPECIFICATION)) {
            type = Constants.SPECIFICATION;
            try {
                JSONArray valueArray = new JSONArray(intent.getStringExtra(Constants.SPECIFICATION_ARRAY));
                boolean select = false;

                //if(intent.getStringArrayListExtra(Constants.SELECTED_SPECIFICATIONS)!=null)
                //    selectedItems = intent.getStringArrayListExtra(Constants.SELECTED_SPECIFICATIONS);
                for(int i = 0; i< valueArray.length();i++){
                    select = false;
                    if(selectedItems.size()>0) {
                        for (int j = 0; j < selectedItems.size(); j++) {
                            if (selectedItems.get(j).equals(valueArray.getString(i))) {
                                counting = counting+1;
                                select = true;
                                break;
                            }
                        }
                    }

                    itemAdapter.add(new Item(valueArray.getString(i),"","",select));
                }
                itemList.setAdapter(itemAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(intent.getStringExtra(Constants.CONTENT_NAME).equals(Constants.BRAND)) {
            type = Constants.BRAND;
            setBrandValues(intent);
        }

        count.setText("" + counting + " Selected");
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
            final TextView selectionNames = (TextView) view.findViewById(R.id.selectionnames);
            final ImageView selective = (ImageView) view.findViewById(R.id.ic);
            final ImageView dropright = (ImageView) view.findViewById(R.id.drright);
            SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.isoffer);
            name.setText(item.name);
            selective.setImageBitmap(null);
            selectionNames.setText(item.collectSelection);
            if (item.isSelected) {
                selective.setTag("1");
                selective.setImageResource(R.drawable.check);
            } else {
                selective.setTag("0");
                selective.setImageResource(R.drawable.unchecked);
            }

            dropright.setVisibility(View.GONE);
            selective.setVisibility(View.VISIBLE);
            switchCompat.setVisibility(View.GONE);
           // selective.setImageResource(R.drawable.unchecked);
          //  selective.setTag("0");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selective.getTag().equals("1")) {
                        selective.setTag("0");
                        selective.setImageResource(R.drawable.unchecked);
                        counting = counting - 1;
                        count.setText("" + counting + " Selected");
                        item.isSelected = false;
                        selectedItems.remove(counting);

                    } else {
                        selective.setTag("1");
                        selective.setImageResource(R.drawable.check);
                        counting = counting + 1;
                        count.setText("" + counting + " Selected");
                        item.isSelected = true;
                        selectedItems.add(item.name);
                    }
                    itemAdapter.notifyDataSetChanged();
                }
            });
        return view;
    }
    }
    private void setBrandValues(Intent intent){
        ArrayList<String> selectedBrands = intent.getStringArrayListExtra(Constants.SELECTED_BRANDS);
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
                            counting = counting+1;
                            isselect = true;
                            break;
                        }
                    }
                    itemAdapter.add(new Item(brand.getName(),""+brand.getId(),"",isselect));
                }
            itemAdapter.notifyDataSetChanged();
            itemList.setAdapter(itemAdapter);
        }
    }
    public class Item{
        public String name,id;
        boolean isSelected = false;
        public String collectSelection;
        public Item(String name,String id,String collectSelection,boolean isSelected) {
            this.name = name;
            this.id = id;
            this.isSelected = isSelected;
            this.collectSelection = collectSelection;
        }
    }
}
