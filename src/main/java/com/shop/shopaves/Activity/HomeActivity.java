package com.shop.shopaves.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Fragments.HomeFragment;
import com.shop.shopaves.Fragments.ParentCategoryFragment;
import com.shop.shopaves.Fragments.ProfileFragment;
import com.shop.shopaves.Fragments.RewardsFragment;
import com.shop.shopaves.Interface.FragmentCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.dataModel.NewCategory;
import com.shop.shopaves.network.Net;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.paypal.android.sdk.bm.e;
import static com.shop.shopaves.R.id.map;

public class HomeActivity extends FragmentActivity implements Response.ErrorListener,Response.Listener<JSONObject>{
    private DrawerLayout drawer;
    private ListView menu_tab_list;
//    private String [] menu_tab_name = {getRes,"Deals","Create Product","Collections","Rewards","Account","Wishlist","Help","Settings"};
    private String [] menu_tab_name;

    private int [] menu_tab_icon = {R.drawable.shop, R.drawable.offer, R.drawable.create_products, R.drawable.collections, R.drawable.reward, R.drawable.account, R.drawable.wishlist, R.drawable.help, R.drawable.setting};
    private MenuTabAdapter menuTabAdapter;
    private ProgressDialog pd;
    private AppStore aps;
    private int menuTabPosition = 0;
    private TextView title;
    private TextView signinText;
    private ImageView itemRight;
    private ListView categoryMenu;
    private MenuCategoryApapter menuCategoryApapter;
    private FragmentManager fragmentManager ;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging gcm;
    private String SENDER_ID = "865320875457";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aps = new AppStore(this);
        if(TextUtils.isEmpty(aps.getData(Constants.LANGUAGE))){
            aps.setData(Constants.LANGUAGE,"en");
        }else{
            setLocale(aps.getData(Constants.LANGUAGE));
        }
        setContentView(R.layout.activity_navigation);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        menu_tab_list = (ListView) findViewById(R.id.add_menu_tab);
        signinText = (TextView)findViewById(R.id.signin);
         title = (TextView)findViewById(R.id.title);
        itemRight = (ImageView) findViewById(R.id.itemclick);
        categoryMenu = (ListView)findViewById(R.id.catemenu);
        menu_tab_name = new String[]{getResources().getString(R.string.sho_p), getResources().getString(R.string.deal_s),getResources().getString(R.string.create_product),getResources().getString(R.string.collections),getResources().getString(R.string.rewards),getResources().getString(R.string.account), getResources().getString(R.string.wishlist),getResources().getString(R.string.help),getResources().getString(R.string.action_settings)};;
        menuCategoryApapter = new MenuCategoryApapter();
        fragmentManager = getSupportFragmentManager();
        aps.setData(Constants.IS_PRESTEPS_COMPLETE,"YES");
        C.applyTypeface(C.getParentView(findViewById(R.id.drawer_layout)), C.getHelveticaNeueFontTypeface(HomeActivity.this));
        if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))  && aps.getData(Constants.USER_ID)!=null)
            signinText.setText(getResources().getString(R.string.signout));
            //findViewById(R.id.signin).setVisibility(View.INVISIBLE);
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        menu_tab_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemRight.setVisibility(View.GONE);
                if(position == 0){
                    replaceMenuFragment(new ParentCategoryFragment(fragmentCallBack));
                    openHomeFragment(Constants.SHOP);
                }
                else if(position == 1){
                    replaceMenuFragment(new ParentCategoryFragment(fragmentCallBack));
                    openHomeFragment(Constants.DEALS);
                }
                else if (position==3){
                    replaceMenuFragment(new ParentCategoryFragment(fragmentCallBack));
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                        openHomeFragment(Constants.COLLECTION);
                    }else{
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                        Toast.makeText(HomeActivity.this,"Please login to create your collection",Toast.LENGTH_SHORT).show();
                    }
                }else if(position == 2){
                    replaceMenuFragment(new ParentCategoryFragment(fragmentCallBack));
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                        drawer.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(HomeActivity.this,AddProductActivity.class));
                    }else{
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    }
                }else if(position == 4){
                    replaceMenuFragment(new ParentCategoryFragment(fragmentCallBack));
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                        title.setText(getResources().getString(R.string.shopaves_reward));
                        title.setVisibility(View.VISIBLE);
                        findViewById(R.id.homeView).setVisibility(View.GONE);
                        drawer.closeDrawer(GravityCompat.START);
                        replaceFragment(new RewardsFragment());
                    }else{
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    }
                }else if(position == 5){
                    replaceMenuFragment(new ParentCategoryFragment(fragmentCallBack));
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                        title.setText(getResources().getString(R.string.profile));
                        title.setVisibility(View.VISIBLE);
                        findViewById(R.id.homeView).setVisibility(View.GONE);
                        itemRight.setVisibility(View.VISIBLE);
                        itemRight.setImageResource(R.drawable.chaticon);
                        drawer.closeDrawer(GravityCompat.START);
                        itemRight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(HomeActivity.this,MessageActivity.class));
                            }
                        });
                        replaceFragment(new ProfileFragment());
                    }else{
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    }
                }else if(position==6) {
                    replaceMenuFragment(new ParentCategoryFragment(fragmentCallBack));
                    if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
                    startActivity(new Intent(HomeActivity.this,WishListActivity.class));
                    else
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                }else if(position==8) {
                    replaceMenuFragment(new SettingActivity(fragmentCallBack));
//                    startActivity(new Intent(HomeActivity.this,SettingActivity.class));
                }
                menuTabPosition = position;
                menuTabAdapter.notifyDataSetChanged();
            }
        });



        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signinText.getText().toString().equals(getResources().getString(R.string.signout))){
                    makeLogoutRequest();
                    aps.clearDataByKey(Constants.USER_ID);
                }else{
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                }
            }
        });findViewById(R.id.cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,AddToCartActivity.class));
            }
        });
        findViewById(R.id.wishopen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID)))
                    startActivity(new Intent(HomeActivity.this,WishListActivity.class));
                else
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
        init();
        if(TextUtils.isEmpty(C.GCM_REGISTERED_ID)){
            gcm = GoogleCloudMessaging.getInstance(this);
            C.GCM_REGISTERED_ID = getRegistrationId(HomeActivity.this);

            if (C.GCM_REGISTERED_ID.isEmpty()) {
                registerInBackground();
            }
        }

    }

    private void makeLogoutRequest(){
        pd = C.getProgressDialog(this);
        HashMap<String,String> map = new HashMap<>();
        map.put("userId",new AppStore(HomeActivity.this).getData(Constants.USER_ID));
        Net.makeRequest(C.APP_URL+ApiName.LOGOUT_API,map,logoutResponse,this);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.GET_ACCOUNTS,Manifest.permission.MEDIA_CONTENT_CONTROL,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManagers = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManagers.beginTransaction();
        transaction.replace(R.id.home_container, fragment);
        transaction.commit();
    }

    private void replaceMenuFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
      //  FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.menucontainer, fragment);
        transaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
       // transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.addToBackStack(backStateName);
        transaction.commit();

        /*String backStateName = fr.getClass().getName();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_holder, fr);
		ft.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
		ft.addToBackStack(backStateName);
		ft.commit();*/
    }

    private final int PERMISSION_REQUEST_CODE = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // Permission Denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Do you want to close the application?")
                    .setCancelable(false)
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).setNegativeButton("NO", null).show();
    }
    }

    private class  MenuTabAdapter extends ArrayAdapter<MenuItem>{

        public MenuTabAdapter(Context context) {
            super(context,0);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView menuTab;
            TextView menuText;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_tab, parent, false);
            }
            menuTab = (ImageView)convertView.findViewById(R.id.tabIcon);
            menuText = (TextView)convertView.findViewById(R.id.menuItem);
            menuTab.setColorFilter(getResources().getColor(R.color.fade_black));

            if(position == menuTabPosition){
                convertView.setBackgroundColor(getResources().getColor(R.color.yellowc));
                menuTab.setColorFilter(Color.BLACK);
                menuText.setTextColor(Color.BLACK);
            }else{
                convertView.setBackgroundColor(Color.WHITE);
                menuTab.setColorFilter(getResources().getColor(R.color.fade_black));
                menuText.setTextColor(getResources().getColor(R.color.fade_black));
            }

            TextView item = (TextView)convertView.findViewById(R.id.menuItem);
            ImageView tab_icon = (ImageView)convertView.findViewById(R.id.tabIcon);
            MenuItem item_menu = getItem(position);
            item.setText(item_menu.MENU_NAME);
            tab_icon.setImageResource(item_menu.MENU_ICON);
            return convertView;
        }
    }

    public class MenuItem{
         int MENU_ICON;
         String MENU_NAME;

        public MenuItem(int MENU_ICON, String MENU_NAME) {
            this.MENU_ICON = MENU_ICON;
            this.MENU_NAME = MENU_NAME;
        }
    }

    private void init(){
       // replaceFragment(new HomeFragment());
        HomeFragment homeFragment = new HomeFragment();
        Bundle bdl = new Bundle();
        bdl.putString(Constants.TYPE, Constants.SHOP);
        homeFragment.setArguments(bdl);
        replaceFragment(homeFragment);
        /*if (Category.count(Category.class) > 0){
            loadFromDb();
        }else {
            pd = C.getProgressDialog(this);
            Map<String,String> map = new HashMap<>();
            Net.makeRequest(C.APP_URL+ ApiName.GET_CATEGORY_API,map,this,this);
        }*/
        if(NewCategory.count(NewCategory.class) > 0){
            setMenuListItems();
            replaceMenuFragment(new ParentCategoryFragment(fragmentCallBack));
        }else{
            pd = C.getProgressDialog(this);
            Map<String,String> map = new HashMap<>();
            Net.makeRequest(C.APP_URL+ ApiName.GET_CATEGORY_API,map,this,this);
        }
        menuTabAdapter = new MenuTabAdapter(this);
        for(int i = 0; i< 9; i++){
            menuTabAdapter.add(new MenuItem(menu_tab_icon[i],menu_tab_name[i]));
        }
        menu_tab_list.setAdapter(menuTabAdapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("menu result",jsonObject.toString());
        Model model = new Model(jsonObject);
        if(model.getStatus().equals("200")){
            Model DATA_ARRAY[] = model.getDataArray();
            for(int i  = 0; i< DATA_ARRAY.length; i++){
                Model data = DATA_ARRAY[i];
                NewCategory newCategory = new NewCategory(data.getId(),data.hasSubCat(),data.getName());
                newCategory.save();
               /* Model CATEGORY_ARRAY[] = DATA_ARRAY[i].getCategoryDataArray();
                for(int j = 0; j < CATEGORY_ARRAY.length; j++){
                    Category category = new Category(CATEGORY_ARRAY[j].getImage(),CATEGORY_ARRAY[j].getName(),""+CATEGORY_ARRAY[j].getId(),CATEGORY_ARRAY[j].getIcon(),CATEGORY_ARRAY[j].getSubCatJsonArray());
                    category.save();
                }*/
            }
           // loadFromDb();
            replaceMenuFragment(new ParentCategoryFragment(fragmentCallBack));
            setMenuListItems();
        }
    }

    FragmentCallBack fragmentCallBack = new FragmentCallBack() {
        @Override
        public void onFragmentCallBack(Fragment fragment) {
            replaceMenuFragment(fragment);
        }

        @Override
        public void finishFragment() {
            fragmentManager.popBackStackImmediate();
        }
    };

    private void setMenuListItems(){
        List<NewCategory> categoryList = NewCategory.listAll(NewCategory.class);
        for(int i = 0; i< categoryList.size();i++){
            NewCategory categoryValues = categoryList.get(i);
            menuCategoryApapter.add(new CategoryItem(categoryValues.categoryId,categoryValues.name,categoryValues.hasSubCategory));
        }
        categoryMenu.setAdapter(menuCategoryApapter);
    }

    private void openHomeFragment(String type){
        drawer.closeDrawer(GravityCompat.START);
        findViewById(R.id.homeView).setVisibility(View.VISIBLE);
        findViewById(R.id.title).setVisibility(View.GONE);
        HomeFragment homeFragment = new HomeFragment();
        Bundle bdl = new Bundle();
        bdl.putString(Constants.TYPE, type);
        homeFragment.setArguments(bdl);
        replaceFragment(homeFragment);
    }

    private class MenuCategoryApapter extends ArrayAdapter<CategoryItem> {

        MyHolder holder ;
        public MenuCategoryApapter() {
            super(HomeActivity.this, R.layout.listgroup);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row =convertView;
            holder = null;
            if (row == null) {
                LayoutInflater inflater = (HomeActivity.this).getLayoutInflater();
                row = inflater.inflate(R.layout.listgroup, parent, false);
                holder = new MyHolder();
                holder.categoryName = (TextView) row.findViewById(R.id.lblListHeader);
                holder.imageView = (ImageView) row.findViewById(R.id.listimg);
                row.setTag(holder);
            }
            else {
                holder = (MyHolder) row.getTag();
            }
            final CategoryItem item = getItem(position);
            holder.categoryName.setText(item.name);

           // PicassoCache.getPicassoInstance(HomeActivity.this).load(C.ASSET_URL+item.).into(holder.imageView);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.hasSubCat){
                        startActivity(new Intent(HomeActivity.this,SubCategoryActivity.class).putExtra(Constants.PARENT_ID,item.catId).putExtra(Constants.NAME,item.name));
                    }
                }
            });
           // PicassoCache.getPicassoInstance(getActivity()).load(C.ASSET_URL+item.logo).into(holder.brandLogo);

            return row;
        }
    }

    Response.Listener<JSONObject> logoutResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Model model = new Model(jsonObject.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            }
        }
    };

    class  MyHolder{
       public TextView categoryName;
        ImageView imageView;
    }


    public class CategoryItem{
        public int catId;
        public String name;
        public boolean hasSubCat;

        public CategoryItem(int catId, String name, boolean hasSubCat) {
            this.catId = catId;
            this.name = name;
            this.hasSubCat = hasSubCat;
        }
    }

    /**
     * Gets the current registration token for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration token, or empty string if there is no existing
     *         registration token.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("no", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("change", "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(HomeActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Registers the application with GCM connection servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(HomeActivity.this);
                    }
                    C.GCM_REGISTERED_ID = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + C.GCM_REGISTERED_ID;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //  sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    //storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }

           /* @Override
            protected void onPostExecute(String msg) {
                mDisplay.append(msg + "\n");
            }*/
        }.execute(null, null, null);

    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
