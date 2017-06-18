package com.shop.shopaves.Activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.Constant.ImgItemProperties;
import com.shop.shopaves.Constant.PhotoSortrView;
import com.shop.shopaves.Dialog.SetProductCollectionDialog;
import com.shop.shopaves.Interface.ImageEditingCallBack;
import com.shop.shopaves.Interface.PublishCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.MultipartRequest;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PublishProductActivity extends AppCompatActivity {

    private RelativeLayout background_img;
    private Bitmap backgroundBitmap;
    private ImageView imgUsers;
    private ProgressDialog pd;
    private String COLLECTION_ID;
    private PhotoSortrView photoSorter;
    private AppStore aps;
    private ArrayList<ProductItems> productItemsArrayList;
    private  int TAG_VALUE = 0;
    private String productDetails;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    private ArrayList<String> productImageArrayList = new ArrayList<>();
    private ArrayList<Model> productModel = new ArrayList<>();
    private boolean isPublish = false;
    private Button publishClick;
    private String backgroundImagePath = "";
    private String backgroundImageUrl = "";
    private String collectionImageUrl = "";
    private int type = 1;
    private ImageView backImg;
    private boolean isBackground = true;
    private Button publishButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product);
        background_img = (RelativeLayout) findViewById(R.id.background);
        backImg = (ImageView)findViewById(R.id.bacbg);
        TextView additem_text = (TextView) findViewById(R.id.additem_text);
        publishClick = (Button)findViewById(R.id.publish);
        publishButton = (Button)findViewById(R.id.publish);
        additem_text.setTypeface(C.getSegoeprFontTypeface(this));
        C.applyTypeface(C.getParentView(findViewById(R.id.publishproductact)), C.getHelveticaNeueFontTypeface(PublishProductActivity.this));

        photoSorter = new PhotoSortrView(this,publishCallBack);
        photoSorter.invalidate();
        productItemsArrayList = new ArrayList<>();
        aps = new AppStore(this);
        COLLECTION_ID = ""+getIntent().getIntExtra("ID",0);
        if(getIntent().getStringExtra("productDetail") != null)
            productDetails = getIntent().getStringExtra("productDetail");
        isPublish = getIntent().getBooleanExtra("isPublish",false);

        if(isPublish)
            publishClick.setText("UNPUBLISH");
       /* findViewById(R.id.und0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.onClickUndo();
            }
        });

        findViewById(R.id.redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.onClickRedo();
            }
        });*/

        findViewById(R.id.addbackground).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
            }
        });

        findViewById(R.id.back_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        findViewById(R.id.addditem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivityForResult(new Intent(PublishProductActivity.this, CategoriesActivity.class), 203);
                startActivityForResult(new Intent(PublishProductActivity.this, SelectItemActivity.class).putExtra("CONTENT_NAME", Constants.EDIT_COLLECTION), 203);
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = C.getProgressDialog(PublishProductActivity.this);


                if(getStringImage(C.getBitmap(background_img))!=null){
                    UploadImage(C.saveChart(PublishProductActivity.this,getResizedBitmap(C.getBitmap(background_img),500,500),background_img.getMeasuredHeight(),background_img.getMeasuredWidth()).toString());

                }
                makeCollectionSaveRequest();
            }
        });
        background_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (background_img.getBackground() != null)
                    new SetProductCollectionDialog(PublishProductActivity.this, backgroundBitmap).show();
                else {
                    Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
                }*/
                if (background_img.getBackground() == null){
                   /* Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 50);*/
                    selectImage();
                }
            }
        });

        findViewById(R.id.bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    /*Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 50);*/
                selectImage();
            }
        });

        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(publishButton.getAlpha() == 1)
                    makeCollectionPublishRequest();
            }
        });

        if(!TextUtils.isEmpty(productDetails)){
            publishButton.setAlpha(1);
            Model model = new Model(productDetails);
            Model dataValue = new Model(model.getData());
            PicassoCache.getPicassoInstance(PublishProductActivity.this).load(C.ASSET_URL+dataValue.getBackBgData()).into(targetBg);
            backgroundImageUrl = dataValue.getBackBgData();
            Model prdArray[] =  model.getProductArray();
            for(Model product : prdArray){
                productModel.add(product);
                productImageArrayList.add(product.getEditProductImage());
                productItemsArrayList.add(new ProductItems(""+product.getId(),product.getEditProductImage(),""+product.getProductId()));
            }
            loadImage(0);
        }
    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(PublishProductActivity.this);
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
        startActivityForResult(intent, Constants.REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.SELECT_FILE);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //  thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
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
        Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
        background_img.setBackgroundDrawable(drawable);

        backgroundImagePath = destination.getPath();
       /* ProductImagePathArray.add(destination.getPath());
        setImageInLayout(thumbnail,false);*/
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
        backgroundImagePath = getPath(PublishProductActivity.this,selectedImage);

        Drawable drawable = new BitmapDrawable(getResources(), bm);
        background_img.setBackgroundDrawable(drawable);
        //ProductImagePathArray.add(getPath(AddProductActivity.this,selectedImage));
        //setTag = setTag + 1;
        // setImageInLayout(bm,false);
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

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }
    private void loadImage(int position){
        if(productImageArrayList.size()>0)
            PicassoCache.getPicassoInstance(PublishProductActivity.this).load(C.ASSET_URL+productImageArrayList.get(position)).into(target);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void makeCollectionSaveRequest(){
        pd = C.getProgressDialog(this);
        JSONObject editCollection = new JSONObject();
        try {
            editCollection.put("collectionId",COLLECTION_ID);
            editCollection.put("colletionImage",collectionImageUrl);
            editCollection.put("collectionBg",backgroundImageUrl);
            editCollection.put("products",getPublishArray());
            Net.makeRequest(C.APP_URL+ ApiName.ADD_PRODUCT_ON_COLLECTION_API,editCollection.toString(),collection_add_response,publish_error);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeCollectionPublishRequest(){
        pd = C.getProgressDialog(PublishProductActivity.this);
        Map<String, String> map = new HashMap<>();
        map.put("collectionId",COLLECTION_ID);
        if(isPublish)
            map.put("isPublish","false");
        else
            map.put("isPublish","true");
        Net.makeRequestParams(C.APP_URL + ApiName.PUBLISH_COLLECTION_API, map, collection_publish_response, publish_error);
    }

    private JSONArray getPublishArray(){
        JSONArray array = new JSONArray();
        for(int i = 0; i< productItemsArrayList.size(); i++){
            ProductItems item = productItemsArrayList.get(i);
            JSONObject editProduct = new JSONObject();

            try {
                editProduct.put("editProductImage", item.selectedProductImage);
                editProduct.put("productImage", "");
                editProduct.put("imageProperties", getProperties(i).toString());
                editProduct.put("productId", item.productId);
                editProduct.put("id", item.Id);
                array.put(editProduct);
            }catch (Exception e){}
        }

        return  array;
    }

    private JSONObject getProperties(int position){
        ImgItemProperties imgp  = photoSorter.getAllImageProperties().get(position);
        JSONObject obj = new JSONObject();
        try {
            obj.put("scalex",""+ imgp.scalex);
            obj.put("angle",""+imgp.angle);
            obj.put("angle",""+imgp.angle);
            obj.put("scaley",""+imgp.scaley);
            obj.put("centerx",""+imgp.centerx);
            obj.put("centery",""+imgp.centery);
            obj.put("tx","0.000000");
            obj.put("ty","0.000000");
            obj.put("minx",""+imgp.minx);
            obj.put("miny",""+imgp.miny);
            obj.put("maxx",""+imgp.maxx);
            obj.put("maxy",""+imgp.maxy);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return obj;
    }


//
//    private JSONObject getProperties(int position){
//        ImgItemProperties imgp  = photoSorter.getAllImageProperties().get(position);
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("scalex",""+ imgp.scalex);
//            obj.put("scaley",""+imgp.scaley);
//            obj.put("centerx",""+imgp.centerx);
//            obj.put("centery",""+imgp.scaley);
//            obj.put("minx",""+imgp.minx);
//            obj.put("miny",""+imgp.miny);
//            obj.put("maxx",""+imgp.maxx);
//            obj.put("maxy",""+imgp.maxy);
//            obj.put("angle",""+imgp.angle);
//
//        } catch (JSONException e1) {
//            e1.printStackTrace();
//        }
//        return obj;
//    }

    Response.Listener<JSONObject> collection_add_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("loginn_response", jsonObject.toString());
            Model model = new Model();
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Toast.makeText(PublishProductActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
                publishButton.setAlpha(1);
            }
             Intent i =new Intent(PublishProductActivity.this,CollectionDetailActivity.class).putExtra("collectionId",Integer.parseInt(COLLECTION_ID));
             startActivity(i);
            finish();
        }
    };


    Response.Listener<JSONObject> collection_publish_response = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            pd.dismiss();
            Log.e("loginn_response", jsonObject.toString());
            Model model = new Model(jsonObject);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                Toast.makeText(PublishProductActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
                isPublish = !isPublish;
                if(publishClick.getText().toString().equals("PUBLISH"))
                    startActivity(new Intent(PublishProductActivity.this,CollectionDetailActivity.class).putExtra(Constants.COLLECTION_ID,Integer.parseInt(COLLECTION_ID)));
                    // publishClick.setText("UNPUBLISH");
                else
                    publishClick.setText("PUBLISH");
            }
        }
    };

    Response.ErrorListener publish_error = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(PublishProductActivity.this,""+ VolleyErrors.setError(volleyError), Toast.LENGTH_SHORT).show();
        }
    };

    private void addView(final Bitmap bitmap,Model model){

        imgUsers = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams imgvwDimens;
        imgvwDimens = new LinearLayout.LayoutParams(330, 330);
        Matrix m = imgUsers.getImageMatrix();
        Matrix matrix = new Matrix();
        matrix.setValues(new float[] {1.302234f, 0.000000f, 0f, 0.000000f, 1.302234f, 0f, 0F, 0F, 1F});
        findViewById(R.id.additem_text).setVisibility(View.GONE);
        findViewById(R.id.arrowOfAddImage).setVisibility(View.GONE);
        imgUsers.setImageMatrix(m);

        imgUsers.setX(260.380513f);
        imgUsers.setY(130.213570f);

        imgUsers.setLayoutParams(imgvwDimens);
        if(bitmap != null){
            imgUsers.setImageBitmap(bitmap);
            TAG_VALUE = TAG_VALUE+1;
            photoSorter.addImages(PublishProductActivity.this,imgUsers.getDrawable(),model,TAG_VALUE);
        }
        if(background_img.getChildCount() > 0){
            background_img.removeAllViews();
            background_img.addView(photoSorter);
        }else
            background_img.addView(photoSorter);
        backgroundBitmap = bitmap;

//        a => xx
//        b => yx
//        c => xy
//        d => yy
//        tx => x0
//        ty => y0
//        "imageProperties": "0.798357,0.000000,0.000000,0.798357,0.000000,0.000000,166.983570,145.344380,0.000000,0.000000",
//1.302234,0.099093,-0.099093,1.302234,0.000000,0.000000,260.380513,130.213570,-0.000000,0.000000"

        Matrix m1 = imgUsers.getImageMatrix();
        Matrix matrix1 = new Matrix();
        matrix1.setValues(new float[] {1.302234f, 0.000000f, 0f, 0.000000f, 1.302234f, 0f, 0F, 0F, 1F});
        imgUsers.setImageMatrix(m1);

        imgUsers.setX(260.380513f);
        imgUsers.setY(130.213570f);

    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            addView(bitmap,productModel.get(bitmapArrayList.size()));
            bitmapArrayList.add(bitmap);
            if(productImageArrayList.size()>bitmapArrayList.size())
                loadImage(bitmapArrayList.size());

           /* else
                for(Bitmap bitmapImage : bitmapArrayList){
                    addView(bitmapImage,"");
                }*/
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private Target targetBg = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            background_img.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            type = 2;
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
    //Mulitpart request for image upload
    private void UploadImage(final String path) {
        new AsyncTask<Void,Void,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                return path;
            }

            @Override
            protected void onPostExecute(String image) {
                super.onPostExecute(image);
                if (!TextUtils.isEmpty(image)) {
                    MultipartRequest multipartRequest = new MultipartRequest(PublishProductActivity.this, C.APP_URL+"uploadFile",image, r_upload, e);
                    RequestQueue requestQueue = Volley.newRequestQueue(PublishProductActivity.this);
                    multipartRequest.setShouldCache(false);
                    requestQueue.add(multipartRequest);
                    Log.e("image===",multipartRequest.toString());
                }else {
                    pd.dismiss();
                }
            }
        }.execute();
    }
    //Mulitpart request for image upload
    private void UploadBackgroundImage(final String path) {
        new AsyncTask<Void,Void,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                return path;
            }

            @Override
            protected void onPostExecute(String image) {
                super.onPostExecute(image);
                if (!TextUtils.isEmpty(image)) {
                    MultipartRequest multipartRequest = new MultipartRequest(PublishProductActivity.this, C.APP_URL+"uploadFile",image, uploadBackground, e);
                    RequestQueue requestQueue = Volley.newRequestQueue(PublishProductActivity.this);
                    multipartRequest.setShouldCache(false);
                    requestQueue.add(multipartRequest);
                    Log.e("image===",multipartRequest.toString());
                }else {
                    pd.dismiss();
                }
            }
        }.execute();
    }

    class ProductItems{
        public String selectedProductImage;
        public String productId;
        public String Id;

        public ProductItems(String Id,String selectedProductImage, String productId) {
            this.selectedProductImage = selectedProductImage;
            this.productId = productId;
            this.Id = Id;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data != null){
            switch (requestCode) {
                case 50:data.getDataString();
                    if (resultCode == RESULT_OK) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(PublishProductActivity.this.getContentResolver(), Uri.parse(data.getDataString()));
                            backgroundBitmap = bitmap;

                            if(bitmap != null){
                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                background_img.setBackgroundDrawable(drawable);
                                // backImg.setImageDrawable(drawable);
                               /* Uri selectedImage = data.getData();
                                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String path = cursor.getString(columnIndex);
                                imagepath = path;
                                cursor.close();*/
                                Uri selectedImage = data.getData();
                                String wholeID = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    wholeID = DocumentsContract.getDocumentId(selectedImage);


                                    // Split at colon, use second item in the array
                                    String id = wholeID.split(":")[1];

                                    String[] column = { MediaStore.Images.Media.DATA };

                                    // where id is equal to
                                    String sel = MediaStore.Images.Media._ID + "=?";

                                    Cursor cursor = getContentResolver().
                                            query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    column, sel, new String[]{ id }, null);

                                    String filePath = "";

                                    int columnIndex = cursor.getColumnIndex(column[0]);

                                    if (cursor.moveToFirst()) {
                                        filePath = cursor.getString(columnIndex);
                                    }
                                    backgroundImagePath = filePath;
                                    cursor.close();
                                }else{
                                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                                    cursor.moveToFirst();
                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                    String path = cursor.getString(columnIndex);
                                    backgroundImagePath = path;
                                    cursor.close();
                                }
                            }

                        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
// TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    }
                case 203:
                    publishButton.setAlpha(0.3f);
                    addView(StringToBitMap(aps.getData(Constants.PRODUCT_BITMAP)),null);
                    productItemsArrayList.add(new ProductItems("",aps.getData(Constants.PRODUCT_IMAGE_URL),aps.getData(Constants.PRODUCT_ID)));
                    aps.setData(Constants.PRODUCT_BITMAP,"");
                    aps.setData(Constants.PRODUCT_IMAGE_URL,"");
                    aps.setData(Constants.PRODUCT_ID,"");

                    break;
                case Constants.REQUEST_CAMERA:
                    onCaptureImageResult(data);
                    break;
                case Constants.SELECT_FILE:
                    onSelectFromGalleryResult(data);
                    break;
                    /*else if(requestCode == REQUEST_CAMERA){
                onCaptureImageResult(data);
            }else if(requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data);
            }*/
            }
        }else if(requestCode == 203){
            publishButton.setAlpha(0.3f);
            addView(StringToBitMap(aps.getData(Constants.PRODUCT_BITMAP)),null);
            productItemsArrayList.add(new ProductItems("",aps.getData(Constants.PRODUCT_IMAGE_URL),aps.getData(Constants.PRODUCT_ID)));
            aps.setData(Constants.PRODUCT_BITMAP,"");
            aps.setData(Constants.PRODUCT_IMAGE_URL,"");
            aps.setData(Constants.PRODUCT_ID,"");
        }
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        photoSorter.loadImages(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        photoSorter.unloadImages();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            photoSorter.trackballClicked();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Response.Listener<JSONObject> uploadBackground = new Response.Listener<JSONObject>(){
        @Override
        public void onResponse(JSONObject response) {
            pd.dismiss();
            Model model = new Model(response);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                backgroundImageUrl = model.getFileName();
                makeCollectionSaveRequest();
                Toast.makeText(PublishProductActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    };
    Response.Listener<JSONObject> r_upload = new Response.Listener<JSONObject>(){
        @Override
        public void onResponse(JSONObject response) {
            pd.dismiss();
            Model model = new Model(response);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                collectionImageUrl = model.getFileName();

                if(TextUtils.isEmpty(backgroundImagePath))
                    makeCollectionSaveRequest();
                else
                    UploadBackgroundImage(backgroundImagePath);

                Toast.makeText(PublishProductActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    };
    Response.ErrorListener e = new Response.ErrorListener(){

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            pd.dismiss();
            Toast.makeText(PublishProductActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
        }
    };

    PublishCallBack publishCallBack = new PublishCallBack() {
        @Override
        public void onPublishCallBack(Bitmap bitmap,int position) {
            new SetProductCollectionDialog(PublishProductActivity.this,bitmap,position, imageEditingCallBack).show();
        }
    };

    ImageEditingCallBack imageEditingCallBack = new ImageEditingCallBack() {
        @Override
        public void onRemoveImageCallBack() {

            photoSorter.removeCollectionImages();
            background_img.removeAllViews();
        }

        @Override
        public void onForwardImageBitmapCallBack() {

            photoSorter.goForward();
        }

        @Override
        public void onBackwardImageBitmapCallBack() {
            photoSorter.goBackward();
        }

        @Override
        public void onSetEditedImageBitmapCallBack(int position, Bitmap drawable) {

            //photoSorter.addImages(PublishProductActivity.this,drawable,null,TAG_VALUE+1);
            // addView(drawable,null);
            photoSorter.editImageAtPosition(position,drawable);
        }


    };

}
