package com.shop.shopaves.Activity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.MyApp;
import com.shop.shopaves.Util.PicassoCache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpDetailActivity extends AppCompatActivity {
    static final int DATE_DIALOG_ID = 0;
    private TextView setDoB;
    private String DOB;
    private int REQUEST_CAMERA = 100;
    private int SELECT_FILE = 200;
    private CircleImageView profileImg;
    private String imageUrl;
    private String accessToken;
    private String TYPE ="0";
    private String APP_ID = "";
    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    DatePickerDialog da;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);
        setDoB = (TextView) findViewById(R.id.set_dob);
        final EditText email = (EditText) findViewById(R.id.enteremail);
        final EditText username = (EditText) findViewById(R.id.enterusername);
        final EditText phone_number = (EditText) findViewById(R.id.enterphonenumber);
        final EditText password = (EditText) findViewById(R.id.password);
        TextView welcomeName = (TextView)findViewById(R.id.name);
        profileImg = (CircleImageView) findViewById(R.id.addphoto_imgvw);
        C.applyTypeface(C.getParentView(findViewById(R.id.detailsignup)), C.getHelveticaNeueFontTypeface(SignUpDetailActivity.this));
        MyApp.getInstance().trackScreenView("Email signup screen");
        if (getIntent() != null) {
            if (getIntent().getStringExtra("NAME") != null) {
                username.setText(getIntent().getStringExtra("NAME"));
                welcomeName.setText(getIntent().getStringExtra("NAME"));
            }
            if (getIntent().getStringExtra("ACCESS_TOKEN") != null)
                accessToken = getIntent().getStringExtra("ACCESS_TOKEN");
            if (getIntent().getStringExtra("TYPE") != null)
                TYPE = getIntent().getStringExtra("TYPE");
            if (getIntent().getStringExtra("APP_ID") != null)
                APP_ID = getIntent().getStringExtra("APP_ID");
            if (getIntent().getStringExtra("IMAGE_URL") != null) {
                imageUrl = getIntent().getStringExtra("IMAGE_URL");
                if(!TextUtils.isEmpty(imageUrl))
                PicassoCache.getPicassoInstance(SignUpDetailActivity.this).load(imageUrl).placeholder(R.drawable.female).into(profileImg);
            }
            if(getIntent().getStringExtra("SOCIAL_EMAIL") != null){
                email.setText(getIntent().getStringExtra("SOCIAL_EMAIL"));
            }


            da = new DatePickerDialog(this, mDateSetListener,
                    mYear, mMonth, mDay);
            da.getDatePicker().setMaxDate(c.getTimeInMillis());


            findViewById(R.id.back_click).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (TextUtils.isEmpty(email.getText().toString()))
                        Toast.makeText(SignUpDetailActivity.this, "Please enter email", Toast.LENGTH_LONG).show();
                    else if (!C.isValidEmail(email.getText().toString()))
                        Toast.makeText(SignUpDetailActivity.this, "Please enter valid email", Toast.LENGTH_LONG).show();
                    else if (TextUtils.isEmpty(username.getText().toString()))
                        Toast.makeText(SignUpDetailActivity.this, "Please enter username", Toast.LENGTH_LONG).show();
                    else if (TextUtils.isEmpty(phone_number.getText().toString()))
                        Toast.makeText(SignUpDetailActivity.this, "Please enter phone number", Toast.LENGTH_LONG).show();
                    else if (TextUtils.isEmpty(password.getText().toString()))
                        Toast.makeText(SignUpDetailActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
                    else if (password.length() < 4 || password.length() >= 16) {
                        Toast.makeText(SignUpDetailActivity.this, "password length must be less-than 16 and greater-than 4", Toast.LENGTH_LONG).show();
                    } else if (TextUtils.isEmpty(setDoB.getText().toString()))
                        Toast.makeText(SignUpDetailActivity.this, "Please enter date of birth", Toast.LENGTH_LONG).show();
                    else {
                        Bundle b = new Bundle();
                        b.putString("EMAIL", email.getText().toString());
                        b.putString("USERNAME", username.getText().toString());
                        b.putString("PHONE_NUMBER", phone_number.getText().toString());
                        b.putString("PASSWORD", password.getText().toString());
                        b.putString("DOB", DOB);
                        b.putString("IMAGE", imageUrl);
                        b.putString("ACCESS_TOKEN", accessToken);
                        b.putString("TYPE", TYPE);
                        b.putString("APP_ID", APP_ID);

                        Intent intent = new Intent(getApplication(), AddressActivity.class);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
            });

            findViewById(R.id.calendr).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    da.show();
                }
            });
            profileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onBackPressed();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpDetailActivity.this);
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
        imageUrl =  getPath(SignUpDetailActivity.this,selectedImage);
        profileImg.setImageBitmap(bm);
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

       // Uri selectedImage = data.getData();
        imageUrl = destination.getPath();
        //imageUrl =  getPath(SignUpDetailActivity.this,selectedImage);

        profileImg.setImageBitmap(thumbnail);
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
}
