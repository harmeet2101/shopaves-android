package com.shop.shopaves.Activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.FileUtils;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.NamePair;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.dataModel.NewCategory;
import com.shop.shopaves.network.MultipartRequest;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;
import com.shop.shopaves.video.MediaController;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class AddProductActivity extends AppCompatActivity implements  Response.ErrorListener,Response.Listener<JSONObject>,View.OnClickListener{

    private EditText title,description,price,offered_discount;
    private  TextView condition,locationName,returns,category,handling_days,brand,wtsize,Reset;
    private ProgressDialog pd;
    private String categoryId,subCategoryId,brandId;
    private String colorId = "0";
    private LinearLayout addProductImages,addProductVideos;
    private int setTag = 0;
    private TextView addbtn;
    private Intent addressIntent;
    private Place placeProduct;
    private ArrayList<String> ProductImagePathArray = new ArrayList<>();
    private ArrayList<String> ProductImageUrlArray = new ArrayList<>();
    private ArrayList<String> ProductVideoPathArray = new ArrayList<>();
    private ArrayList<String> ProductVideeoUrlArray = new ArrayList<>();
    private ArrayList<String> editTitleArray = new ArrayList<>();
    private int imagePathSize = 0;
    private LinearLayout addSpecification;
    private TextView valuesSpecification,paymentType,titleText,titleNameSpecification,packagingType;
    private AppStore aps;
    private boolean isEditProduct = false;
    private String productId;
    private HashMap<String,ArrayList<String>> specificationHashMap = new HashMap<>();
    private String brandsResponse;
    private int handlingTimeId = 1;
    private int packingId = 0;
    private int returnId = -1;
    private int REQUEST_CAMERA = 1;
    private int SELECT_FILE = 2;
    private int selectedBrandId = 0;
    private ScrollView scrollView;
    private int SELECT_VIDEO = 111,WEIGHT=564;
    private int videoPathSize = 0,CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE=1111;
    private String videoLink = "";
    private File tempVideoFile;
    private View specificationView;
    private String type = Constants.PRODUCT;
    private LinearLayout salePriceLayout;
    private String wei,len,wid,hei;
    private JSONObject locationParameter;
    private String shippingType,shippingCost,shippingDays;
    private boolean isshippingFree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        C.applyTypeface(C.getParentView(findViewById(R.id.activity_add_product)), C.getHelveticaNeueFontTypeface(AddProductActivity.this));
        aps = new AppStore(this);
        title = (EditText)findViewById(R.id.titlename);
        category = (TextView)findViewById(R.id.selectcategory);
        Reset = (TextView)findViewById(R.id.reset);
        description = (EditText)findViewById(R.id.description);
        brand = (TextView)findViewById(R.id.brandselection);
        condition = (TextView) findViewById(R.id.condition);
        wtsize = (TextView) findViewById(R.id.wtsize);
        price = (EditText)findViewById(R.id.price);
        offered_discount = (EditText)findViewById(R.id.offereddiscount);
        locationName = (TextView)findViewById(R.id.itemlocation_name);
        titleText = (TextView)findViewById(R.id.titleact);
        returns = (TextView)findViewById(R.id.accept_or_not);
        addbtn = (TextView)findViewById(R.id.prdbtn);
        handling_days = (TextView)findViewById(R.id.days);
        addProductImages = (LinearLayout)findViewById(R.id.addproduct_images);
        addProductVideos = (LinearLayout)findViewById(R.id.addproduct_video);
        packagingType = (TextView)findViewById(R.id.pacttype);
        salePriceLayout = (LinearLayout)findViewById(R.id.salepricelayout);

        paymentType = (TextView)findViewById(R.id.paymenttype);
        addSpecification = (LinearLayout)findViewById(R.id.addspecification);
        scrollView = (ScrollView)findViewById(R.id.scroll);
        condition.setOnClickListener(this);
        findViewById(R.id.item_location_btn).setOnClickListener(this);
        findViewById(R.id.add_product).setOnClickListener(this);
        findViewById(R.id.returnclick).setOnClickListener(this);
        findViewById(R.id.tap_to_category).setOnClickListener(this);
        findViewById(R.id.handlingclick).setOnClickListener(this);
        findViewById(R.id.brandclick).setOnClickListener(this);
        findViewById(R.id.back_addrs).setOnClickListener(this);
        findViewById(R.id.addPphoto).setOnClickListener(this);
        findViewById(R.id.addnewspec).setOnClickListener(this);
        findViewById(R.id.uploadvideo).setOnClickListener(this);
        findViewById(R.id.paymentclick).setOnClickListener(this);
        findViewById(R.id.wsclick).setOnClickListener(this);
        findViewById(R.id.packagingclick).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);
        description.setImeOptions(EditorInfo.IME_ACTION_DONE);

        try {
            addressIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(AddProductActivity.this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        if(!TextUtils.isEmpty(getIntent().getStringExtra(Constants.PRODUCT_ID))){
          //  setEditProductDetails(getIntent().getStringExtra("PRODUCT_DETAILS"));
            productId = getIntent().getStringExtra(Constants.PRODUCT_ID);
            makeProductDetailsRequest(productId);
            isEditProduct = true;
            salePriceLayout.setVisibility(View.VISIBLE);
            addbtn.setText(getResources().getString(R.string.save));
            titleText.setText(getResources().getString(R.string.edit_product));
        }
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
                    Reset.setEnabled(true);
                    Reset.setAlpha(1.0f);
                }else {
                    Reset.setEnabled(false);
                    Reset.setAlpha(0.6f);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setEditProductDetails(String jsonString){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Model model = new Model(jsonObject);
        Model data = new Model(model.getData());
            title.setText(data.getItemName());
            setCategories(""+data.getCatId());
            category.setText("Default");
            selectedBrandId = data.getBrandId();
            offered_discount.setText(data.getDiscount());
            description.setText(data.getProductDescription());
            subCategoryId = ""+data.getSubCategoryId();
            productId = ""+data.getId();
            categoryId = ""+data.getCatId();
            brandId = ""+data.getBrandId();
            price.setText(data.getPrice());
            condition.setText(data.getCondition());
            shippingCost = ""+data.getShippingCost();
            shippingDays = data.getShippingDays();
            shippingType = data.getShippingType();
            isshippingFree = data.isShippingfree();
            packagingType.setText(data.getShippingDays());
            handling_days.setText(""+data.getHandlingTime()+" Business Days");

            returns.setText(data.getReturns() == 1 ? "Return Accepted" : "No Returns Accepted");
            paymentType.setText(data.getPaymentType());
            len = data.getLength();
            wei= data.getWeight();
            wid = data.getWidth();
            hei = data.getHeight();
            try {
                JSONObject jsondata = jsonObject.getJSONObject("data");
                JSONArray jsonimgarray = jsondata.getJSONArray("image");
                JSONArray specificationArray = new JSONArray(jsondata.getString("specifications"));
                for(int i = 0; i<specificationArray.length();i++){
                    ArrayList<String> editValueArray = new ArrayList<>();
                    JSONObject specificationValue  = specificationArray.getJSONObject(i);
                    editTitleArray.add(specificationValue.getString("title"));
                    JSONArray valueArray = new JSONArray(specificationValue.getString("value"));
                    for(int editvalue = 0; editvalue<valueArray.length();editvalue++){
                        editValueArray.add(""+valueArray.get(editvalue));
                    }
                    specificationHashMap.put(specificationValue.getString("title"),editValueArray);
                }
               // price.setText(jsonObject.getString("price"));
                if(jsonimgarray.length()>0){
                    for(int i = 0; i< jsonimgarray.length();i++) {
                        ProductImageUrlArray.add(jsonimgarray.getString(i));
                        Log.e("imageArray", jsonimgarray.getString(0));
                        PicassoCache.getPicassoInstance(AddProductActivity.this).load(C.getImageUrl(ProductImageUrlArray.get(i))).into(target);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        Model location = new Model(data.getProductLocationa());
        locationName.setText(location.getStreetAddress());
        locationParameter = editProductLocationRequest(location.getCountry(),location.getCity(),location.getState(),location.getPinCode(),location.getLattitude(),location.getLongitude());
        setWeightAndSize();
        getSpecificationTitles();
    }

    private void setWeightAndSize(){
        String weight = wei+"lb, ";
        String length = len+"*";
        String width = wid+"*";
        wtsize.setText("  "+weight+length+width+hei);
    }

    private void setCategories(String categoryId){
        List<NewCategory> categoryList = NewCategory.listAll(NewCategory.class);
        for (int i = 0; i < categoryList.size(); i++) {
            NewCategory categoryValue = categoryList.get(i);
           if(categoryId.equals(categoryValue.categoryId)){
               category.setText(categoryValue.name);
               category.setTextColor(getResources().getColor(R.color.black));
           }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.add_product:
                if(isValidate()){
                    if(ProductImagePathArray.size()>0){
                        UploadImage(ProductImagePathArray.get(0));
                    }else if(ProductVideoPathArray.size()>0){
                        UploadVideo(ProductVideoPathArray.get(0));
                    }
                    else
                        addProduct();
                }
                break;
            case R.id.item_location_btn:
               // startActivityForResult(new Intent(getApplication(),SelectItemActivity.class).putExtra("CONTENT_NAME","ITEM_LOCATION"),200);
               //startActivityForResult(addressIntent,900);
                startActivityForResult(new Intent(AddProductActivity.this,ChangeShippingAddressActivity.class).putExtra(Constants.TYPE,Constants.SELECTED_ITEMS),900);
                 break;
            case R.id.condition:
                startActivityForResult(new Intent(AddProductActivity.this,SelectItemActivity.class).putExtra("CONTENT_NAME","CONDITION").putExtra("CONDITION",condition.getText().toString()),100);
                break;
            case R.id.returnclick:
                startActivityForResult(new Intent(AddProductActivity.this,SelectItemActivity.class).putExtra("CONTENT_NAME","RETURN").putExtra("RETURN_ID",returnId),300);
                break;
            case R.id.tap_to_category:
                startActivityForResult(new Intent(AddProductActivity.this,SelectItemActivity.class).putExtra("CONTENT_NAME","CATEGORY_PRODUCT"),400);
                break;
            case R.id.handlingclick:
                startActivityForResult(new Intent(AddProductActivity.this,SelectItemActivity.class).putExtra("CONTENT_NAME","HANDLING_TIME").putExtra("HANDLING_ID",handlingTimeId),600);
                break;
            case R.id.uploadvideo:
                selectVideo();
                break;
            case R.id.brandclick:
                Intent intentBrand = new Intent(AddProductActivity.this,SelectItemActivity.class);
                if(!TextUtils.isEmpty(brandsResponse)) {
                    intentBrand.putExtra("BRANDS_DATA", brandsResponse);
                    intentBrand.putExtra("CONTENT_NAME", "BRAND");
                    intentBrand.putExtra("BRAND_ID", brandId);
                    startActivityForResult(intentBrand, 700);
                }else{
                    Toast.makeText(AddProductActivity.this,"Please select category first",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.back_addrs:
                finish();
                break;case
                R.id.addPphoto:
                if(ProductImagePathArray.size()<5)
                selectImage();
                else
                Toast.makeText(AddProductActivity.this,"You can select only five images for product",Toast.LENGTH_LONG).show();
                break;
            case R.id.paymentclick:
                //startActivity(new Intent(AddProductActivity.this,PaymentActivity.class));

                startActivityForResult(new Intent(AddProductActivity.this,PaymentLocalActivity.class).putExtra(Constants.TYPE,Constants.ADD_PRODUCT),101);
                break;
            case R.id.wsclick:
                startActivityForResult(new Intent(AddProductActivity.this,WeightAndSizeActivity.class).putExtra("weight",wei)
                        .putExtra("length",len).putExtra("width",wid).putExtra("height",hei),WEIGHT);
                break;
            case R.id.packagingclick:
              //  startActivityForResult(new Intent(AddProductActivity.this,SelectItemActivity.class).putExtra("CONTENT_NAME","PACKING_TYPE").putExtra("PACKING_ID",packingId),40);
                startActivityForResult(new Intent(AddProductActivity.this,ShippingSelect.class).putExtra(Constants.TYPE,shippingType).putExtra(Constants.SELECT_SERVICE,shippingDays).putExtra(Constants.COST,shippingCost),888);
                break;
            case R.id.reset:
                makeFieldBlank();
                break;
        }
    }

    private void selectVideo() {
        final CharSequence[] items = { "Take Video", "Choose from gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
        builder.setTitle("Add Video!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Video")) {
//                    startActivityForResult(new Intent(AddProductActivity.this,EnregistrementVideoStackActivity.class),1111);
                    // create new Intentwith with Standard Intent action that can be
                    // sent to have the camera application capture an video and return it.
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    // create a file to save the video
                    Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

                    // set the image file name
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    // set the video image quality to high
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                    // start the Video Capture Intent
                    startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);


                } else if (items[item].equals("Choose from gallery")) {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select a Video "), SELECT_VIDEO);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /** Create a file Uri for saving an image or video */
    private  Uri getOutputMediaFileUri(int type){

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){

        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraVideo");


        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){


                Toast.makeText(AddProductActivity.this, "Failed to create directory MyCameraVideo.",
                        Toast.LENGTH_LONG).show();

                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }


        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());

        File mediaFile;

        if(type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");

        } else {
            return null;
        }

        return mediaFile;
    }
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from gallery")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri selectedImage = data.getData();
        ProductImagePathArray.add(getPath(AddProductActivity.this,selectedImage));
        setTag = setTag + 1;
        setImageInLayout(bm,false,null);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProductImagePathArray.add(destination.getPath());
        setImageInLayout(thumbnail,false,null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    private boolean isValidate(){
        if(TextUtils.isEmpty(title.getText().toString())){
            Toast.makeText(AddProductActivity.this,"Please enter title name",Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(categoryId)){
            Toast.makeText(AddProductActivity.this,"Please select category",Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(subCategoryId)){
            Toast.makeText(AddProductActivity.this,"Please select sub-category",Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(brandId)){
            Toast.makeText(AddProductActivity.this,"Please select brand",Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(condition.getText().toString())){
            Toast.makeText(AddProductActivity.this,"Please select condition",Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(price.getText().toString())){

            Toast.makeText(AddProductActivity.this,"Please enter price",Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(offered_discount.getText().toString()) && salePriceLayout.getVisibility() == View.VISIBLE){
            Toast.makeText(AddProductActivity.this,"Please enter offered discount",Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(handling_days.getText().toString())){
            Toast.makeText(AddProductActivity.this,"Please select handling time",Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(returns.getText().toString())){
            Toast.makeText(AddProductActivity.this,"Please select returns",Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(wtsize.getText().toString())){
            Toast.makeText(AddProductActivity.this,"Please select weight and size",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void makeFieldBlank(){
        title.setText("");
        condition.setText("");
        price.setText("");
        category.setText("");
        category.setHint("Tap to select Category");
        offered_discount.setText("");
        brand.setText("");
        brand.setHint("Tap to select Brand");
        handling_days.setText("");
        returns.setText("");
        description.setText("");
        paymentType.setText("");
        locationName.setText("");
        packagingType.setText("");
        addProductImages.removeAllViews();
        addProductVideos.removeAllViews();
        addSpecification.removeAllViews();
    }

    private void addProduct(){
        pd = C.getProgressDialog(this);
        final JSONObject map = new JSONObject();
       try {
           if(isEditProduct)
               map.put("id", productId);
           map.put("itemName", title.getText().toString());
           map.put("image",getImageArray());
           map.put("video", getVideoUrlsToApi());
           map.put("catId", categoryId);
         //  map.put("subCategoryId",subCategoryId);
           map.put("condition", condition.getText().toString());
           map.put("mrp", price.getText().toString());
           map.put("salePrice", TextUtils.isEmpty(offered_discount.getText().toString()) ? price.getText().toString() : offered_discount.getText().toString());
           map.put("size", "small");
           map.put("color", colorId);
           map.put("length", len);
           map.put("description", description.getText().toString());
           map.put("paymenttype", paymentType.getText().toString());
           if(locationParameter != null)
           map.put("productLocation",locationParameter);
          /* else
           map.put("productLocation",locationParameter);*/
           map.put("handlingTime","  "+ handlingTimeId);
           map.put("returns", returnId);
           map.put("brandId", brandId);
           map.put("height", hei);
           map.put("weight", wei);
           map.put("width", wid);
           map.put("specifications", ""+getSpecificationValuesForAPI());
           map.put("userId", aps.getData(Constants.USER_ID));
           map.put("shippingType",shippingType);
           map.put("shippingDays",shippingDays);
           map.put("isShippFree",isshippingFree);
           map.put("shippingCost",shippingCost);
           Log.e("addProductRequest",map.toString());
           if(isEditProduct)
               Net.makeRequest(C.APP_URL + ApiName.CREATE_PRODUCT_API, map.toString(), this, this);
           else
               Net.makeRequest(C.APP_URL + ApiName.CREATE_PRODUCT_API, map.toString(), this, this);
       }catch (Exception e){
           pd.dismiss();
       }
    }

    private void addSpecificationView(String specificationName,String valuesArray,String editValues){

                View v = getLayoutInflater().inflate(R.layout.product_specification, null);
                 titleNameSpecification = (TextView) v.findViewById(R.id.addtitle);
                //TextView value = (TextView) v.findViewById(R.id.addvalue);
                 valuesSpecification =  (TextView) v.findViewById(R.id.addvalue);
                titleNameSpecification.setText(specificationName);
                valuesSpecification.setText(editValues);
                v.setTag(valuesArray);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     //   Toast.makeText(AddProductActivity.this,"tag"+v.getTag(),Toast.LENGTH_LONG).show();
                        valuesSpecification =(TextView) v.findViewById(R.id.addvalue);
                        titleNameSpecification =(TextView) v.findViewById(R.id.addtitle);
                        specificationView = v;
                        Intent intent = new Intent(AddProductActivity.this,SelectItemActivity.class);
                        intent.putExtra("CONTENT_NA",titleNameSpecification.getText().toString());
                        intent.putExtra("valueArray",""+v.getTag());
                        intent.putExtra("CONTENT_NAME","VALUES_ARRAY");
                        intent.putExtra("SELECTED_SPECIFICATIONS",specificationHashMap.get(titleNameSpecification.getText().toString()));
                        startActivityForResult(intent,20);
                    }
                });
                addSpecification.addView(v);
    }

    private JSONArray getSpecificationValuesForAPI(){
        JSONArray specArray = new JSONArray();
        for(int i = 0;i<addSpecification.getChildCount();i++){
            JSONObject specObject = new JSONObject();
            JSONArray specValueArray = new JSONArray();
            View v = addSpecification.getChildAt(i);
           TextView title = (TextView) v.findViewById(R.id.addtitle);
           TextView value = (TextView) v.findViewById(R.id.addvalue);

            if(specificationHashMap.get(title.getText()) != null && specificationHashMap.get(title.getText()).size()>0) {
                for (int sp = 0; sp < specificationHashMap.get(title.getText()).size(); sp++) {
                    specValueArray.put("" + specificationHashMap.get(title.getText()).get(sp));
                }
            }else{
                specValueArray.put("");
            }
            try {
                specObject.put("title",title.getText().toString());
                specObject.put("value",specValueArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            specArray.put(specObject);
        }
        return specArray;
    }
    private void getSpecificationTitles(){
        Map<String,String> map = new HashMap<>();
        map.put("catId",categoryId);
        pd =  C.getProgressDialog(AddProductActivity.this);
        Net.makeRequest(C.APP_URL+ ApiName.GET_SPECIFICATION_TITLES_API,map,specificationresponse,this);
    }

    private void makeProductDetailsRequest(String productId) {
        type = Constants.PRODUCT_DETAILS;
        pd = C.getProgressDialog(AddProductActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("productId", productId);
        Net.makeRequestParams(C.APP_URL + ApiName.GET_PRODUCT_DETAILS_API, map, AddProductActivity.this, AddProductActivity.this);
    }

    Response.Listener<JSONObject> specificationresponse = new Response.Listener<JSONObject>(){
        @Override
        public void onResponse(JSONObject response) {
            pd.dismiss();
            addSpecification.removeAllViews();
            Model model = new Model(response);
            brandsResponse = response.toString();

            Model data = new Model(model.getData());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Model specificationArray[] = data.getSpecificationArray();
                Model brandsArray[] =  model.getBrandsArray();
                for(Model brandDetail : brandsArray){
                    if(selectedBrandId == brandDetail.getId()){
                        brand.setText(brandDetail.getName());
                        brandId = ""+brandDetail.getId();
                    }
                }
                if(specificationArray != null) {
                    for(int i = 0; i< specificationArray.length;i++){
                        Model specification = null;
                         specification = specificationArray[i];
                        if(isEditProduct){
                            for(int j = 0; j<editTitleArray.size();j++){
                                String editvalues = "";
                                if(editTitleArray.get(j).equals(specification.getName())){
                                    for(int k = 0; k<specificationHashMap.get(editTitleArray.get(j)).size();k++){
                                        editvalues = editvalues +","+specificationHashMap.get(editTitleArray.get(j)).get(k);
                                    }
                                    addSpecificationView(specification.getName(),specification.getString(NamePair.VALUES),editvalues.substring(1));
                               break;
                                }
                            }
                        }
                        else
                            addSpecificationView(specification.getName(),specification.getString(NamePair.VALUES),"");
                    }
                    }
            }
        }
    };


    //Mulitpart request for image upload
    private void UploadImage(final String path) {
        new AsyncTask<Void,Void,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = C.getProgressDialog(AddProductActivity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                return path;
            }

            @Override
            protected void onPostExecute(String image) {
                super.onPostExecute(image);
                if (!TextUtils.isEmpty(image)) {
                    MultipartRequest multipartRequest = new MultipartRequest(AddProductActivity.this, C.APP_URL+"uploadFile",image, r_upload, AddProductActivity.this);
                    RequestQueue requestQueue = Volley.newRequestQueue(AddProductActivity.this);
                    multipartRequest.setShouldCache(false);
                    requestQueue.add(multipartRequest);
                    Log.e("image===",multipartRequest.toString());
                }else {
                    pd.dismiss();
                }
            }
        }.execute();
    }

    /*for upload video*/

    //Mulitpart request for image upload
    private void UploadVideo(final String path) {
        new AsyncTask<Void,Void,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = C.getProgressDialog(AddProductActivity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                return path;
            }

            @Override
            protected void onPostExecute(String image) {
                super.onPostExecute(image);
                if (!TextUtils.isEmpty(image)) {
                    MultipartRequest multipartRequest = new MultipartRequest(AddProductActivity.this, C.APP_URL+"uploadFile",image, r_video_upload, AddProductActivity.this,true);
                    RequestQueue requestQueue = Volley.newRequestQueue(AddProductActivity.this);
                    multipartRequest.setShouldCache(false);
                    requestQueue.add(multipartRequest);
                    Log.e("image===",multipartRequest.toString());
                }else {
                    pd.dismiss();
                }
            }
        }.execute();
    }



    @Override
    public void onErrorResponse(VolleyError volleyError) {
        pd.dismiss();
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
        }
        Toast.makeText(AddProductActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("Add produce response",jsonObject.toString());
        Model model = new Model(jsonObject);
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            if(type.equals(Constants.PRODUCT_DETAILS)){
                type = Constants.PRODUCT;
                setEditProductDetails(jsonObject.toString());
            }
            else if(isEditProduct) {
                Model data = new Model(model.getData());
                Toast.makeText(AddProductActivity.this, "product updated successfully.", Toast.LENGTH_LONG).show();
                setResult(100,new Intent().putExtra("ID",""+data.getId()));
                finish();
            }
            else {
               // Toast.makeText(AddProductActivity.this, "" + model.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(AddProductActivity.this, "Product has been created succcessfully", Toast.LENGTH_LONG).show();
                Model data = new Model(model.getData());
                startActivity(new Intent(AddProductActivity.this, ProductDetailsActivity.class).putExtra("productId", "" + data.getId()));
                data.getId();
                finish();
            }
    }else{
            Toast.makeText(AddProductActivity.this, "" + model.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    Response.Listener<JSONObject> r_upload = new Response.Listener<JSONObject>(){
        @Override
        public void onResponse(JSONObject response) {
            pd.dismiss();
            Model model = new Model(response);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                imagePathSize = imagePathSize+1;
              //  Toast.makeText(AddProductActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
                ProductImageUrlArray.add(model.getFileName());
                if(imagePathSize < ProductImagePathArray.size()){
                    UploadImage(ProductImagePathArray.get(imagePathSize));
                }
                    else if(ProductVideoPathArray.size()>0){
                        UploadVideo(ProductVideoPathArray.get(0));
                    }else
                    addProduct();
            }
        }
    };

    Response.Listener<JSONObject> r_video_upload = new Response.Listener<JSONObject>(){
        @Override
        public void onResponse(JSONObject response) {
            pd.dismiss();
            Log.e("videoResponse",response.toString());
            Model model = new Model(response.toString());
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                videoPathSize = videoPathSize+1;
                //Toast.makeText(AddProductActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
                ProductVideeoUrlArray.add(model.getFileName());
                if(videoPathSize < ProductVideoPathArray.size()){
                    UploadVideo(ProductVideoPathArray.get(videoPathSize));
                }else{
                    addProduct();
                }
            }
        }
    };

    private void setImageInLayout(Bitmap bitmap, boolean isVideo, Uri uri) {

        if (isVideo){
            View productMediaView = getLayoutInflater().inflate(R.layout.add_product_video, null);
            final VideoView prdVid = (VideoView) productMediaView.findViewById(R.id.pdimg);

            final ImageView videoIcon = (ImageView) productMediaView.findViewById(R.id.videoicon);
            final ImageView removeIcon = (ImageView) productMediaView.findViewById(R.id.removeimg);

            productMediaView.findViewById(R.id.removeimg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        for (int i = 0; i < addProductVideos.getChildCount(); ++i) {
                            View vi = addProductVideos.getChildAt(i);
                            if (vi.findViewById(R.id.pdimg).getTag() == prdVid.getTag()) {
                                addProductVideos.removeViewAt((i));
                                if(ProductVideoPathArray.size() > 0)
                                ProductVideoPathArray.remove(i);
                                if (isEditProduct)
                                    ProductVideeoUrlArray.remove(i);
                                break;
                            }
                        }

                    }catch (Exception e) {
                        e.printStackTrace();

                    }}

            });

            prdVid.setVideoURI(uri);
            prdVid.seekTo(100);
            prdVid.setTag(setTag);
                addProductVideos.addView(productMediaView);
                videoIcon.setImageResource(R.drawable.video1);
                removeIcon.setTag("video");
        }
        else{
            View productMediaView = getLayoutInflater().inflate(R.layout.add_product_image, null);
            final ImageView prdImg = (ImageView) productMediaView.findViewById(R.id.pdimg);

            final ImageView videoIcon = (ImageView) productMediaView.findViewById(R.id.videoicon);
            final ImageView removeIcon = (ImageView) productMediaView.findViewById(R.id.removeimg);
            productMediaView.findViewById(R.id.removeimg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        try{
                            for (int i = 0; i < addProductImages.getChildCount(); ++i) {
                                View vi = addProductImages.getChildAt(i);
                                if (vi.findViewById(R.id.pdimg).getTag() == prdImg.getTag()) {
                                    addProductImages.removeViewAt((i));
                                    if(ProductImagePathArray.size() > 0)
                                    ProductImagePathArray.remove(i);
                                    if(isEditProduct)
                                        ProductImageUrlArray.remove(i);
                                    break;
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                }
            });

            prdImg.setImageBitmap(bitmap);
            prdImg.setTag(setTag);
            addProductImages.addView(productMediaView);
                removeIcon.setTag("image");
                videoIcon.setImageBitmap(null);

        }
    }


    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setImageInLayout(bitmap,false,null);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
    private JSONObject setProductLocation(Place place ){

        String postalCode = "",cityName = "",countryName = "",state = "";
        double lat = 0,lng = 0;
        if(place != null) {
            locationName.setText(place.getAddress());
             lat = place.getLatLng().latitude;
             lng = place.getLatLng().longitude;

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
// lat,lng, your current location
            try {
                addresses = geocoder.getFromLocation(lat, lng, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Address address : addresses) {
                if (address.getLocality() != null && address.getPostalCode() != null && address.getCountryName() != null && address.getAdminArea() != null) {
                    postalCode = address.getPostalCode();
                    cityName = address.getLocality();
                    countryName = address.getCountryName();
                    state = address.getAdminArea();
                    Log.e("locality", "" + address.getCountryName());
                    Log.e("postal code", "" + address.getPostalCode());
                    break;
                }      //  Log.e("respone",""+place.getLocale().getCountry()+" "+addresses.get(0).getAddressLine(0)+" "+addresses.get(0).getLocality()+" "+addresses.get(0).getSubLocality()+" "+""+addresses.get(0).getPostalCode());
            }
        }
        JSONObject placeObject = new JSONObject();
        try {
            placeObject.put("country",countryName);
            placeObject.put("streetAddress",cityName);
            placeObject.put("city",cityName);
            placeObject.put("state",state);
            placeObject.put("pincode",postalCode);
            placeObject.put("latitude",""+lat);
            placeObject.put("longitude",""+lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return placeObject;
    }

    private JSONObject editProductLocationRequest(String countryName,String cityName,String state,String postalCode,Double lat,Double lng ){
        JSONObject placeObject = new JSONObject();
        try {
            placeObject.put("country",countryName);
            placeObject.put("streetAddress",cityName);
            placeObject.put("city",cityName);
            placeObject.put("state",state);
            placeObject.put("pincode",postalCode);
            placeObject.put("latitude",""+lat);
            placeObject.put("longitude",""+lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return placeObject;
    }

    private JSONArray getImageArray(){

        JSONArray imageArray = new JSONArray();
        for(int i = 0; i< ProductImageUrlArray.size();i++){
            imageArray.put(ProductImageUrlArray.get(i));
        }
        return imageArray;
    }

    private String getVideoUrlsToApi(){
        for(int i = 0;i<ProductVideeoUrlArray.size();i++){
            if(i == 0)
            videoLink = ProductVideeoUrlArray.get(i);
            else
                videoLink = videoLink+","+ProductVideeoUrlArray.get(i);

        }
        return  videoLink;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(requestCode == 100){
                condition.setText(data.getStringExtra("NAME"));
                condition.setTextColor(getResources().getColor(R.color.black));
            }else if(requestCode == 200){
                locationName.setText(data.getStringExtra("NAME"));
                locationName.setTextColor(getResources().getColor(R.color.black));

            } else if(requestCode == 300) {
                returns.setText(data.getStringExtra("NAME"));
                returns.setTextColor(getResources().getColor(R.color.black));
                returnId = Integer.parseInt(data.getStringExtra("id"));
            }
            else if(requestCode == 400){
                categoryId = data.getStringExtra("CategoryId");
                category.setText(data.getStringExtra("CategoryName"));
                category.setTextColor(getResources().getColor(R.color.black));
                subCategoryId = data.getStringExtra("id");
                getSpecificationTitles();
            }

            else if(requestCode == 600){
                handlingTimeId = Integer.parseInt(data.getStringExtra("id"));
                handling_days.setText(data.getStringExtra("NAME"));
                handling_days.setTextColor(getResources().getColor(R.color.black));
            }
            else if(requestCode == 40){
                packingId = Integer.parseInt(data.getStringExtra("id"));
                packagingType.setText(data.getStringExtra("NAME"));
                packagingType.setTextColor(getResources().getColor(R.color.black));
            }
            else if(requestCode == 700){
                brand.setText(data.getStringExtra("NAME"));
                brandId = data.getStringExtra("id");
                brand.setTextColor(getResources().getColor(R.color.black));
            }else if (requestCode==WEIGHT && resultCode==RESULT_OK){
                if (data!=null){
                    wei = data.getStringExtra("Weight");
                    len = data.getStringExtra("length");
                    wid = data.getStringExtra("width");
                    hei = data.getStringExtra("height");
                    setWeightAndSize();
                    /*      String weight = wei+"lb, ";
                    String length = len+"*";
                    String width = wid+"*";
                    wtsize.setText(weight+length+width+hei);*/
                }
            }else if (requestCode==900){
               /* placeProduct = PlaceAutocomplete.getPlace(this, data);
                //setProductLocation(placeProduct);
                Log.e("Place" , placeProduct.toString());
                String address = placeProduct.getAddress().toString();
                locationName.setText(placeProduct.getAddress().toString());
                Log.e("Address" ,address);*/
                locationParameter = editProductLocationRequest(data.getStringExtra(Constants.COUNTRY),data.getStringExtra(Constants.CITY),data.getStringExtra(Constants.STATE),data.getStringExtra(Constants.PINCODE),0.0,0.0);
                locationName.setText(data.getStringExtra(Constants.ADDRESS));
            }else if(requestCode == 20){
                String specificationNames = "";
              //  valuesSpecification.setText(data.getStringExtra("NAME").substring(1));
                specificationHashMap.put(titleNameSpecification.getText().toString(),data.getStringArrayListExtra("NAME"));

/*for new added specification*/
                try {
                    JSONArray valueArray = new JSONArray(""+specificationView.getTag());
                    boolean isAbailable;
                    for(int i = 0; i< data.getStringArrayListExtra("NAME").size();i++) {
                        isAbailable = false;
                        for(int j = 0; j<valueArray.length();j++){
                            if(data.getStringArrayListExtra("NAME").get(i).equals(valueArray.get(j))){
                               isAbailable = true;
                                break;
                            }
                        }
                        if(!isAbailable){
                            if(!TextUtils.isEmpty(data.getStringArrayListExtra("NAME").get(i)))
                            valueArray.put(data.getStringArrayListExtra("NAME").get(i));
                        }
                    }
                    specificationView.setTag(valueArray.toString());
                    } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(data.getStringArrayListExtra("NAME").size()>0) {
                    for (String name : data.getStringArrayListExtra("NAME")) {
                        if(!TextUtils.isEmpty(name))
                        specificationNames = specificationNames + ", " + name;
                    }
                    valuesSpecification.setText(specificationNames.substring(1));
                }else{
                    valuesSpecification.setText("");
                }
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("error", status.getStatusMessage());
            }else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                capturedVideo(data,1);
                Toast.makeText(this, "Video saved to:" +
                        data.getData(), Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
                Toast.makeText(this, "User cancelled the video capture.",
                        Toast.LENGTH_LONG).show();

            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            } else if(requestCode == REQUEST_CAMERA){
                onCaptureImageResult(data);
            }else if(requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data);
                /*  if (data != null) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();

                            Bitmap bm=null;
                            if (data != null) {
                                try {
                                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            ProductImagePathArray.add(getRealPathFromURI(AddProductActivity.this,uri));
                            setTag = setTag + 1;
                            setImageInLayout(bm,false,uri);
                        }
                    }

                }*/
//                onSelectFromGalleryResult(data);
            }else if(requestCode == SELECT_VIDEO){
               // onSelectFromGalleryResult(data);
                if (requestCode == SELECT_VIDEO)
                {
                    System.out.println("SELECT_VIDEO");
                    capturedVideo(data,2);

                    }
                }else if(resultCode == 101){
                paymentType.setText(data.getStringExtra(Constants.PAYMENT_MODE));
            }else if(resultCode == 888){
                Model shippingModel = new Model(data.getStringExtra(Constants.SHIPPING_SELECTION));
                packagingType.setText(shippingModel.getShippingDays());
                shippingCost = ""+shippingModel.getShippingCost();
                shippingDays = shippingModel.getShippingDays();
                shippingType = shippingModel.getShippingType();
                isshippingFree = shippingModel.isShippingfree();
            }
            }
        }
    private void capturedVideo(Intent data,int type) {
        Uri selectedVideoUri = data.getData();
        Log.e("videouri", "" + selectedVideoUri);
        // System.out.println("SELECT_VIDEO Path : " + getPath(AddProductActivity.this,selectedImageUri));
        // uploadVideo(selectedPath);
        Log.e("video_before_compress", getPath(AddProductActivity.this, selectedVideoUri));
        //videoPath = getPath(AddProductActivity.this,selectedVideoUri);

        if (selectedVideoUri != null && type==2) {
            Cursor cursor = getContentResolver().query(selectedVideoUri, null, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {

                    String displayName = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.i("display name", "Display Name: " + displayName);

                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    String size = null;
                    if (!cursor.isNull(sizeIndex)) {
                        size = cursor.getString(sizeIndex);
                    } else {
                        size = "Unknown";
                    }
                    Log.i("size", "Size: " + size);

                    tempVideoFile = FileUtils.saveTempFile(displayName, this, selectedVideoUri);
                    //editText.setText(tempFile.getPath());

                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(tempVideoFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    setTag = setTag + 1;
                    setImageInLayout(thumb, true,selectedVideoUri);
                    new VideoCompressor().execute();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }else if(type==1 && selectedVideoUri!=null){
            setImageInLayout(null, true,selectedVideoUri);
        }
    }


    class VideoCompressor extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = C.getProgressDialog(AddProductActivity.this);
            Log.d("startvideoCompress","Start video compression");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(tempVideoFile.getPath());
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            pd.dismiss();
            if(compressed){
                Log.d("compressSuccess","Compression successfully!");
                ProductVideoPathArray.add(MediaController.getInstance().getCompressVideoPath());
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
