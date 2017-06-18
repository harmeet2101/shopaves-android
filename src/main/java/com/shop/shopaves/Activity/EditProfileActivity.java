package com.shop.shopaves.Activity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.MultipartRequest;
import com.shop.shopaves.network.Net;
import com.shop.shopaves.network.VolleyErrors;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements Response.ErrorListener,Response.Listener<JSONObject>{
    static final int DATE_DIALOG_ID = 0;
    private EditText setDoB;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String DOB;
    private ProgressDialog pd;
    private AppStore aps;
    private EditText firstName,lastName,emailId,userName,phoneNumber,dob;
    private int REQUEST_CAMERA = 100;
    private int SELECT_FILE = 200;
    private String imageLocalPath = "";
    private String imageRequestUrl = "";
    private CircleImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setDoB = (EditText)findViewById(R.id.dateofbirth);
        firstName = (EditText)findViewById(R.id.firstname);
        lastName = (EditText)findViewById(R.id.lastname);
        emailId = (EditText)findViewById(R.id.email);
        userName = (EditText)findViewById(R.id.username);
        phoneNumber = (EditText)findViewById(R.id.phonenumber);
        dob = (EditText)findViewById(R.id.dateofbirth);
        userImage = (CircleImageView)findViewById(R.id.addphoto_imgvw);
        aps = new AppStore(this);
        C.applyTypeface(C.getParentView(findViewById(R.id.editprofile)), C.getHelveticaNeueFontTypeface(EditProfileActivity.this));

        findViewById(R.id.cal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });


        findViewById(R.id.changepassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this,ChangePasswordActivity.class));
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValidate()){
                    if(!TextUtils.isEmpty(imageLocalPath))
                        UploadImage(imageLocalPath);
                    else
                        makeEditProfileRequest();
                }
            }
        });

        findViewById(R.id.changephoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        findViewById(R.id.back_collectn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setInfo();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }

    private void setInfo(){
        Model profileInfo = new Model(aps.getData(Constants.LOGIN_INFO));
        Model data = new Model(profileInfo.getData());
        userName.setText(data.getName());
        emailId.setText(data.getEmailId());
        phoneNumber.setText(data.getMobileNumber());
        dob.setText(data.getDateOfBirth());
        firstName.setText(data.getFirstName());
        lastName.setText(data.getLastName());
        PicassoCache.getPicassoInstance(EditProfileActivity.this).load(C.ASSET_URL+data.getImageUrl()).placeholder(R.drawable.female).into(userImage);
    }
    private boolean isValidate(){
        if(TextUtils.isEmpty(firstName.getText().toString())) {
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(lastName.getText().toString())) {
            Toast.makeText(this, "Please enter last name", Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(emailId.getText().toString())) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(userName.getText().toString())) {
            Toast.makeText(this, "Please enter user name", Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(phoneNumber.getText().toString())) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_LONG).show();
            return false;
        }else if(TextUtils.isEmpty(dob.getText().toString())) {
            Toast.makeText(this, "Please enter date of birth", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void makeEditProfileRequest(){
        pd = C.getProgressDialog(this);
        JSONObject map = new JSONObject();
        try {
            map.put("id",aps.getData(Constants.USER_ID));
            map.put("firstName",firstName.getText().toString());
            map.put("lastName",lastName.getText().toString());
            map.put("name",userName.getText().toString());
            map.put("mobileNumber",phoneNumber.getText().toString());
            map.put("dob",dob.getText().toString());
            map.put("image",imageRequestUrl);
            Net.makeRequest(C.APP_URL+ ApiName.EDIT_PROFILE_API,map.toString(),this,this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Mulitpart request for image upload
    private void UploadImage(final String path) {
        new AsyncTask<Void,Void,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = C.getProgressDialog(EditProfileActivity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                return path;
            }

            @Override
            protected void onPostExecute(String image) {
                super.onPostExecute(image);
                if (!TextUtils.isEmpty(image)) {
                    MultipartRequest multipartRequest = new MultipartRequest(EditProfileActivity.this, C.APP_URL+"uploadFile",image, r_upload, EditProfileActivity.this);
                    RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
                    multipartRequest.setShouldCache(false);
                    requestQueue.add(multipartRequest);
                    Log.e("image===",multipartRequest.toString());
                }else {
                    pd.dismiss();
                }
            }
        }.execute();
    }

    private void updateDisplay() {
        setDoB.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mDay).append("/")
                        .append(mMonth + 1).append("/")
                        .append(mYear).append(" "));
        DOB = new StringBuilder()
                // Month is 0 based so add 1
                .append(mDay).append("/")
                .append(mMonth + 1).append("/")
                .append(mYear).append(" ").toString();
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {

        pd.dismiss();
        Toast.makeText(EditProfileActivity.this,""+ VolleyErrors.setError(volleyError),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        pd.dismiss();
        Log.e("editProfileResponse",jsonObject.toString());
        Model model = new Model(jsonObject.toString());
        Toast.makeText(EditProfileActivity.this,model.getMessage(),Toast.LENGTH_LONG).show();
        if(model.getStatus().equals(Constants.SUCCESS_CODE)){
            aps.setData(Constants.LOGIN_INFO,jsonObject.toString());
            setResult(200);
            finish();
        }
    }

    Response.Listener<JSONObject> r_upload = new Response.Listener<JSONObject>(){
        @Override
        public void onResponse(JSONObject response) {
            pd.dismiss();
            Model model = new Model(response);
            if(model.getStatus().equals(Constants.SUCCESS_CODE)){
                //Toast.makeText(AddProductActivity.this,""+model.getMessage(),Toast.LENGTH_LONG).show();
               // ProductImageUrlArray.add(model.getFileName());

                imageRequestUrl = model.getFileName();
                    makeEditProfileRequest();

            }
        }
    };


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

        // Uri selectedImage = data.getData();
        imageLocalPath = destination.getPath();
        //imageUrl =  getPath(SignUpDetailActivity.this,selectedImage);

        userImage.setImageBitmap(thumbnail);
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
        imageLocalPath =  getPath(EditProfileActivity.this,selectedImage);
        userImage.setImageBitmap(bm);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data!=null){
            if(requestCode == REQUEST_CAMERA){
                onCaptureImageResult(data);
            }else if(requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data);
            }
        }
    }
}
