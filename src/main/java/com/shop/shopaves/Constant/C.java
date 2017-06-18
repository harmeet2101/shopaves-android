package com.shop.shopaves.Constant;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shop.shopaves.Activity.HomeActivity;
import com.shop.shopaves.Activity.LoginActivity;
import com.shop.shopaves.Activity.ProductDetailsActivity;
import com.shop.shopaves.Interface.ValueCallBack;
import com.shop.shopaves.R;
import com.shop.shopaves.Util.ApiName;
import com.shop.shopaves.Util.Model;
import com.shop.shopaves.Util.PicassoCache;
import com.shop.shopaves.dataModel.AppStore;
import com.shop.shopaves.network.Net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 9/28/2016.
 */

public class C  {

    //public  static String APP_URL = "http://54.169.58.190:8080/ShopavesApi-0.1.0/shopaves/";
    public  static String APP_URL = "http://52.220.225.211:8080/ShopavesApi-0.1.0/shopaves/";
    public static String ASSET_URL = "https://s3-ap-southeast-1.amazonaws.com/shopaves/";

    public static String instagram_Client_id = "e9ee179307de4b8c84676424ff5721c9";
    public static String instagram_secret_id = "a71c30cb4716462bb3a7efec76e29d8e";
    public static final String CALLBACK_URL = "https://amsyt.com";
    public static String GCM_REGISTERED_ID = "";
    public static String API_TYPE = "";
    public static String PRODUCT_INFO = "";
    public static String SEARCH_TYPE="";
    public static  boolean IS_TOP_FILTER = false;
    public static Typeface getHelveticaNeueFontTypeface(Context ctx) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(), "font/HelveticaNeue.ttf");
        return font;
    }

    public static Typeface getGeekriotFontTypeface(Context ctx) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(), "font/geekriotTBS.ttf");
        return font;
    }

    public static Typeface getSegoeprFontTypeface(Context ctx) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(), "font/segoepr.ttf");
        return font;
    }


    //	getting the parent of a view
    public static ViewGroup getParentView(View v) {
        ViewGroup vg = null;
        if (v != null) {
            vg = (ViewGroup) v.getRootView();
        }
        return vg;

    }
    public static String getFirstLetterCaps(String name){
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static void shareContent(Context ctx,String name,String imageUrl){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(imageUrl);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, name);
        ctx.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }
    public static void shareContentExp(Context ctx,String name,Uri imageUrl){

       /* Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //Target whatsapp:
        shareIntent.setPackage("com.whatsapp");
        //Add text and then Image URI
        shareIntent.putExtra(Intent.EXTRA_TEXT, name);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUrl);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            ctx.startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx,"Whatsapp have not been installed.",Toast.LENGTH_SHORT);
        }*/


        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        //sharingIntent.setType("text/html");
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUrl);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, name);
        ctx.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
  /*  public static ProgressDialog getProgressDialog(Context ctx){

        ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();
        return  pd;
    }*/
    public static ProgressDialog getProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
    }


    //	apply typeface for all child views tetxview,button,edittext.
    public static void applyTypeface(ViewGroup v, Typeface f) {

        if (v != null) {

            int vgCount = v.getChildCount();
            for (int i = 0; i < vgCount; i++) {
                if (v.getChildAt(i) == null)
                    continue;
                if (v.getChildAt(i) instanceof ViewGroup) {
                    applyTypeface((ViewGroup) v.getChildAt(i), f);
                } else {
                    View view = v.getChildAt(i);
                    if (view instanceof TextView) {
                        ((TextView) (view)).setTypeface(f);
                    } else if (view instanceof Button) {
                        ((Button) (view)).setTypeface(f);
                    } else if (view instanceof EditText) {
                        ((EditText) (view)).setTypeface(f);
                    }
                }
            }
        }
    }

    private static final String EMAIL_PATTERN ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static boolean isValidEmail(String hex) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public static String saveToSDCard(Bitmap bt){
        File galleryFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "ShopAves");
        if (!galleryFile.exists()) {
            if (!galleryFile.mkdirs()) {
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",  Locale.getDefault()).format(new Date());

        File  mediaFile = new File(galleryFile.getPath() + File.separator + "IMG_" + timeStamp + ".png");
        FileOutputStream out = null;
        String path = "";
        try {
            out = new FileOutputStream(mediaFile);
            bt.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mediaFile.getAbsolutePath();
    }
    public static String getBase64Image(Bitmap bitmap){
        String b64Image = null;
        if (bitmap != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            byte[] bitmapdata = stream.toByteArray();
            b64Image = bitmapdata == null ? null : Base64.encodeToString(bitmapdata, Base64.DEFAULT);
        }

        return b64Image;
    }

/*

    public static File getImageUrl(){
        File folder = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Webe");

        if (!folder.exists()) {
           folder.mkdirs();
        }

		*//*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());*//*

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.ENGLISH).format(new Date());

        File file = new File(folder.getPath() + File.separator + "/" + timeStamp + ".jpg");

        return file;
    }*/

				public static String getEncodedString(String value){
                    try {
                        return !TextUtils.isEmpty(value) ? URLEncoder.encode(value, "utf-8") : "";
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return "";
                }

    public static File saveChart(Context ctx, Bitmap getbitmap, float height, float width) {
        File folder = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Shopaves");

        if (!folder.exists()) {
            folder.mkdirs();
        }

		/*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());*/

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.ENGLISH).format(new Date());

        File file = new File(folder.getPath() + File.separator + "/" + timeStamp + ".png");

        boolean success = false;

        if (!file.exists()) {
            try {
                success = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream ostream = null;

        try {

            ostream = new FileOutputStream(file);

            System.out.println(ostream);

            Bitmap well = getbitmap;
            Bitmap save = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            Canvas now = new Canvas(save);
            now.drawRect(new Rect(0, 0, (int) width, (int) height), paint);
            now.drawBitmap(well,
                    new Rect(0, 0, well.getWidth(), well.getHeight()),
                    new Rect(0, 0, (int) width, (int) height), null);

            if (save == null) {
                System.out.println("NULL bitmap save\n");
            }
            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);

//saved to gallery

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "");
            values.put(MediaStore.Images.Media.DESCRIPTION, "");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
            values.put("_data", file.getAbsolutePath());

            ContentResolver cr = ctx.getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }


		/*catch (NullPointerException e){

			success = false;

			e.printStackTrace();
			//Toast.makeText(getApplicationContext(), "Null error", Toast.LENGTH_SHORT).show();
		}*/ catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		/*catch (FileNotFoundException e){

			success = false;
			e.printStackTrace();
			// Toast.makeText(getApplicationContext(), "File error", Toast.LENGTH_SHORT).show();
		}
		catch (IOException e){

			success = false;
			e.printStackTrace();
			// Toast.makeText(getApplicationContext(), "IO error", Toast.LENGTH_SHORT).show();
		}*/

        return file;
    }

    public static String getImageUrl(String imageUrl){
         return imageUrl.startsWith("https") ? imageUrl : C.ASSET_URL+imageUrl;
    }

    public static Bitmap getBitmap(RelativeLayout layout) {

        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
       /* //Bitmap bmp = Bitmap.createBitmap(layout.getDrawingCache());
       *//**//* Bitmap b = Bitmap.createBitmap(
                120, 120, Bitmap.Config.ARGB_8888);
        layout.setDrawingCacheEnabled(false);
*//**//**/
        Bitmap b = layout.getDrawingCache();

       /* Bitmap b = Bitmap.createBitmap( layout.getLayoutParams().width, layout.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        layout.layout(layout.getLeft(), layout.getTop(), layout.getRight(), layout.getBottom());
        layout.draw(c);*/

        return b;
    }

    public static String getTitleCase(String input){
        String[] words = input.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                try {
                    sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
                }catch (Exception e){

                }

            }
        }
        return sb.toString();
    }

    public static String formateValue(Double due) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat formatter = (DecimalFormat) nf;
        //DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();
        //DecimalFormat formatter = null;
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setCurrencySymbol(""); // Don't use null.
        //symbols.setDecimalSeparator('.');
        //symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        String value = formatter.format(due);
        //value.trim();
        return value;
    }

    public static String FormatterValue(float due) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat formatter = (DecimalFormat) nf;
        //DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();
        //DecimalFormat formatter = null;
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setCurrencySymbol(""); // Don't use null.
        //symbols.setDecimalSeparator('.');
        //symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        String value = formatter.format(due);
        //value.trim();
        return value;
    }

    public static View setProductView(final Context ctx,Model productModel){
        final AppStore aps = new AppStore(ctx);
        View fashion_trends_view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.single_product_hori, null);
        final TextView productName = (TextView) fashion_trends_view.findViewById(R.id.product_name);
        TextView productPrice = (TextView) fashion_trends_view.findViewById(R.id.price);
        TextView productDiscount = (TextView) fashion_trends_view.findViewById(R.id.cross_price);
        TextView offDiscount = (TextView) fashion_trends_view.findViewById(R.id.offdiscount);
        ImageView prdImageView = (ImageView)fashion_trends_view.findViewById(R.id.product_img);
        final ImageView like = (ImageView)fashion_trends_view.findViewById(R.id.wishlike);
        RelativeLayout offerdiscount=(RelativeLayout)fashion_trends_view.findViewById(R.id.offerdiscount);
        TextView offdis=(TextView)fashion_trends_view.findViewById(R.id.offdis);
        LinearLayout offDiscountView = (LinearLayout)fashion_trends_view.findViewById(R.id.off);
        RelativeLayout specialOffer = (RelativeLayout)fashion_trends_view.findViewById(R.id.spcloffr);
        final RelativeLayout likeClick = (RelativeLayout)fashion_trends_view.findViewById(R.id.clicklike);
        RatingBar productRate = (RatingBar)fashion_trends_view.findViewById(R.id.rate);
        productRate.setStepSize(0.5f);
        TextView rateCount = (TextView)fashion_trends_view.findViewById(R.id.ratecount);
        productRate.setRating(productModel.getAverageRating());
        Log.e("productrate",""+productModel.getRateCount());
        fashion_trends_view.findViewById(R.id.rateview).setVisibility(productModel.getRateCount() > 0 ? View.VISIBLE : View.INVISIBLE );
        rateCount.setText("("+""+productModel.getRateCount()+")");
        productName.setText(C.getTitleCase(productModel.getProductName()));
        if(!TextUtils.isEmpty(""+productModel.getProductId()))
        productName.setTag("" + productModel.getProductId());
        else if(!TextUtils.isEmpty(""+productModel.getId()))
            productName.setTag("" + productModel.getId());

      if(!TextUtils.isEmpty(""+productModel.getProductId()))
          likeClick.setTag(productModel.getProductId());
        else if(!TextUtils.isEmpty(""+productModel.getId()))
      likeClick.setTag(productModel.getId());
        //likeClick.setTag(productModel.getId());

        if(productModel.getProductImage() != null)
        PicassoCache.getPicassoInstance(ctx).load(C.getImageUrl(productModel.getProductImage())).into(prdImageView);
        else if(productModel.getImage() != null)
            PicassoCache.getPicassoInstance(ctx).load(C.getImageUrl(productModel.getImage())).into(prdImageView);

        like.setImageResource(productModel.getProductLikeStatus() == 1 ? R.drawable.itemlike : R.drawable.itemunlike);
        like.setTag(productModel.getProductLikeStatus() == 1 ? "1" : "2");

      /*  productPrice.setText("$" + ((Integer.parseInt(price)-((Integer.parseInt(price) * Integer.parseInt(discount) / 100)))));
            productDiscount.setText("$"+price);
            offDiscount.setText("$"+discount);

*/
      productDiscount.setPaintFlags(productDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        productPrice.setText("$ " + C.FormatterValue(Float.parseFloat(productModel.getDiscount())));

       // productDiscount.setText("$ "+ C.FormatterValue(Float.parseFloat(productModel.getPrice())));
        if(Float.parseFloat(productModel.getPrice())>0) {
            offdis.setText("" + C.FormatterValue((100 - (Float.parseFloat(productModel.getDiscount()) * 100) / Float.parseFloat(productModel.getPrice())))  + "% off");
            offerdiscount.setVisibility((100 - (Float.parseFloat(productModel.getDiscount()) * 100) / Float.parseFloat(productModel.getPrice())) <= 0 ? View.INVISIBLE : View.VISIBLE);

            if((100 - (Float.parseFloat(productModel.getDiscount()) * 100) / Float.parseFloat(productModel.getPrice())) > 50)
                specialOffer.setVisibility(View.VISIBLE);
            else
                specialOffer.setVisibility(View.GONE);
        }
        fashion_trends_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView prd = (TextView) v.findViewById(R.id.product_name);
                ctx.startActivity(new Intent(ctx,ProductDetailsActivity.class).putExtra("productId",""+prd.getTag()));
            }
        });
        likeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(aps.getData(Constants.USER_ID))){
                       // makeProductLikeRequest(""+likeClick.getTag(),"2");
                       final ProgressDialog pd = C.getProgressDialog(ctx);
                        Map<String, String> map = new HashMap<>();
                        map.put("productId",""+productName.getTag());
                        map.put("status",like.getTag().equals("1") ? "2" : "1");
                        map.put("userId",aps.getData(Constants.USER_ID));
                        Net.makeRequestParams(C.APP_URL + ApiName.LIKE_PRODUCT_API, map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                pd.dismiss();
                                Model model = new Model(jsonObject);
                                if (model.getStatus().equals(Constants.SUCCESS_CODE)) {
                                    Log.e("productLikeStatus", jsonObject.toString());

                                    if (model.getCurrentLikeStatus() == 1) {
                                        like.setImageResource(R.drawable.itemlike);
                                        like.setTag("1");
                                        // likeProductIcon.setImageResource(R.drawable.itemlike);
                                        // likeProductIcon.setTag("1");
                                    } else {
                                        like.setImageResource(R.drawable.itemunlike);
                                        //  likeProductIcon.setImageResource(R.drawable.itemunlike);
                                        like.setTag("2");
                                    }
                                }            // makeCollectionDetailRequest();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                pd.dismiss();
                            }
                        });

                }else
                    ctx.startActivity(new Intent(ctx, LoginActivity.class));
                //   C.setLoginMessage(getActivity());
            }
        });

        return fashion_trends_view;
    }

    public static Bitmap getBitmapForCanvas(RelativeLayout layout){
        Bitmap b = Bitmap.createBitmap( layout.getLayoutParams().width, layout.getLayoutParams().height, Bitmap.Config.ARGB_8888);
       // Bitmap b = Bitmap.createBitmap( 100, 100, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        layout.layout(layout.getLeft(), layout.getTop(), layout.getRight(), layout.getBottom());
        layout.draw(c);
        return b;
    }

    public static void customDialog(Context ctx, final ValueCallBack valueCallBack){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
        alertDialog.setTitle("Specification");
        alertDialog.setMessage("Enter your specification");

        final EditText input = new EditText(ctx);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,0,10,0);

        input.setLayoutParams(lp);

        alertDialog.setView(input);
       // alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        valueCallBack.callBack(input.getText().toString());
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    public static  void setLoginMessage(Context context){
        Toast.makeText(context, "you have to login first for this operation.", Toast.LENGTH_SHORT).show();
    }
    public  static final int PERMISSION_REQUEST_CODE = 100;

    public static boolean checkPermission(Context ctx){
        int result = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static  void requestPermission(Context ctx){
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) ctx, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST_CODE);
        }
    }//VEnXFulqSqE9grAbcPyQEG8kEtk=

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputPattern = "dd MMM, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getDateFormat(String date) {
        long smsTimeInMilis = 0;
        TimeZone tz = TimeZone.getTimeZone(TimeZone.getDefault().getID());// Quoted "Z" to indicate UTC, no timezone offset
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

//        "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        try {
            Date mDate = sdf.parse(date);
            smsTimeInMilis = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(tz);
        Calendar smsTime = Calendar.getInstance(tz);
        smsTime.setTimeInMillis(smsTimeInMilis+19800000);

        Calendar now = Calendar.getInstance();
        return DateFormat.format("dd MMM, yyyy", smsTime).toString();

       /* final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd MMM, yyyy hh:mm aa";
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
        }else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == -1  ){
            return "Tomorrow " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMM, yyyy", smsTime).toString();
        }*/
    }
    public static void hideSoftKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), 0);
    }
    public static String getDateFormatInMonth(String date){
        long smsTimeInMilis = 0;
        TimeZone tz = TimeZone.getTimeZone(TimeZone.getDefault().getID());// Quoted "Z" to indicate UTC, no timezone offset
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

//        "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        try {
            Date mDate = sdf.parse(date);
            smsTimeInMilis = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(tz);
        Calendar smsTime = Calendar.getInstance(tz);
        smsTime.setTimeInMillis(smsTimeInMilis+19800000);

        Calendar now = Calendar.getInstance();
        return DateFormat.format("EEEE, dd MMM, yyyy - hh:mm aa", smsTime).toString();

    }

    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            Log.d("Exception parsing",e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
   /* public static String parseDate(String timeAtMiliseconds) {
        if (timeAtMiliseconds.equalsIgnoreCase("")) {
            return "";
        }
        //API.log("Day Ago "+dayago);
        String result = "now";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dataSot = formatter.format(new Date());
        Calendar calendar = Calendar.getInstance();

        long dayagolong = Long.valueOf(timeAtMiliseconds) * 1000;
        calendar.setTimeInMillis(dayagolong);
        String agoformater = formatter.format(calendar.getTime());

        Date CurrentDate = null;
        Date CreateDate = null;

        try {
            CurrentDate = formatter.parse(dataSot);
            CreateDate = formatter.parse(agoformater);

            long different = Math.abs(CurrentDate.getTime() - CreateDate.getTime());

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            different = different % secondsInMilli;
            if (elapsedDays == 0) {
                if (elapsedHours == 0) {
                    if (elapsedMinutes == 0) {
                        if (elapsedSeconds < 0) {
                            return "0" + " s";
                        } else {
                            if (elapsedDays > 0 && elapsedSeconds < 59) {
                                return "now";
                            }
                        }
                    } else {
                        return String.valueOf(elapsedMinutes) + "m ago";
                    }
                } else {
                    return String.valueOf(elapsedHours) + "h ago";
                }

            } else {
                if (elapsedDays <= 29) {
                    return String.valueOf(elapsedDays) + "d ago";
                }
                if (elapsedDays > 29 && elapsedDays <= 58) {
                    return "1Mth ago";
                }
                if (elapsedDays > 58 && elapsedDays <= 87) {
                    return "2Mth ago";
                }
                if (elapsedDays > 87 && elapsedDays <= 116) {
                    return "3Mth ago";
                }
                if (elapsedDays > 116 && elapsedDays <= 145) {
                    return "4Mth ago";
                }
                if (elapsedDays > 145 && elapsedDays <= 174) {
                    return "5Mth ago";
                }
                if (elapsedDays > 174 && elapsedDays <= 203) {
                    return "6Mth ago";
                }
                if (elapsedDays > 203 && elapsedDays <= 232) {
                    return "7Mth ago";
                }
                if (elapsedDays > 232 && elapsedDays <= 261) {
                    return "8Mth ago";
                }
                if (elapsedDays > 261 && elapsedDays <= 290) {
                    return "9Mth ago";
                }
                if (elapsedDays > 290 && elapsedDays <= 319) {
                    return "10Mth ago";
                }
                if (elapsedDays > 319 && elapsedDays <= 348) {
                    return "11Mth ago";
                }
                if (elapsedDays > 348 && elapsedDays <= 360) {
                    return "12Mth ago";
                }

                if (elapsedDays > 360 && elapsedDays <= 720) {
                    return "1 year ago";
                }

                if (elapsedDays > 720) {
                    SimpleDateFormat formatterYear = new SimpleDateFormat("MM/dd/yyyy");
                    Calendar calendarYear = Calendar.getInstance();
                    calendarYear.setTimeInMillis(dayagolong);
                    return formatterYear.format(calendarYear.getTime()) + "";
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }*/


    public static String getDayTomorrowDateFormat(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }

    public static String parseDate(long t){
        //long secondsAgo = 1000-3600000;
        PrettyTime prettyTime = new PrettyTime();
       return  prettyTime.format(new Date(t));
    }

    public static void setLocale(String lang,Context ctx) {
        Locale myLocale = new Locale(lang);
        Resources res = ctx.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(ctx, HomeActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(refresh);
    }
}
