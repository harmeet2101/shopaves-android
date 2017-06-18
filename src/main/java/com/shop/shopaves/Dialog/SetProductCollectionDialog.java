package com.shop.shopaves.Dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.MyView;
import com.shop.shopaves.Interface.ImageEditingCallBack;
import com.shop.shopaves.R;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.ArrayList;

/**
 * Created by amsyt005 on 20/10/16.
 */
public class SetProductCollectionDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private Bitmap bitmap;
    private HorizontalScrollView hzs;
    private LinearLayout couout_add;
    private MyView drawView;
    private RelativeLayout editView;
    private ImageEditingCallBack imageEditingCallBack;
    private ImageView set_image;
    private int position;
    private ArrayList<Integer> pixelArray = new ArrayList<>();
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    public SetProductCollectionDialog(Context context, Bitmap bitmap,int position,ImageEditingCallBack imageEditingCallBack) {
        super(context,R.style.Theme_Dialog);
        this.context = context;
        this.bitmap = bitmap;
        this.position = position;
        this.imageEditingCallBack = imageEditingCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_product_collection);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        drawView = new MyView(context);

         set_image = (ImageView)findViewById(R.id.setImage);
        final ImageView eraseImage = (ImageView)findViewById(R.id.erase);
        hzs = (HorizontalScrollView)findViewById(R.id.cout_out_scroll);
        couout_add = (LinearLayout)findViewById(R.id.addedcutout);
        editView = (RelativeLayout)findViewById(R.id.edit_view);
        set_image.setImageBitmap(bitmap);
        set_image.setTag("0");
        findViewById(R.id.clone_item).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);
        findViewById(R.id.forward).setOnClickListener(this);
        findViewById(R.id.backward).setOnClickListener(this);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eraseImage.getTag().equals("1")){
                    imageEditingCallBack.onSetEditedImageBitmapCallBack(position,createDrawableSelectedImage());
                    C.saveChart(context,createDrawableSelectedImage(),bitmap.getWidth(),bitmap.getHeight());

                }else if(set_image.getTag().equals("1")){
                    imageEditingCallBack.onSetEditedImageBitmapCallBack(position,((BitmapDrawable)set_image.getDrawable()).getBitmap());
                }
                dismiss();
            }
        });
        editView.addView(drawView);
        findViewById(R.id.couout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutOut();
            }
        });
        eraseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    drawView.setErasable(false);
            }
        });

        set_image.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){

                 int x = (int)event.getX();
                int y = (int)event.getY();
                int pixel = bitmap.getPixel(x,y);
                pixelArray.add(pixel);
                return false;
            }
        });


        findViewById(R.id.eraseundo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // drawView.setErasable(true);
                drawView.setTransparent();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.clone_item:
                cloneItem();
                break;

            case R.id.remove:
                removeItem();
                break;
            case R.id.forward:
                forwardItem();
                break;
            case R.id.backward:
                backwardItem();
                break;

        }
    }

    private void cloneItem(){
    }

    private void removeItem(){
        imageEditingCallBack.onRemoveImageCallBack();
        drawView.setErasable(false);
        dismiss();
    }

    private void forwardItem(){

        imageEditingCallBack.onForwardImageBitmapCallBack();
        dismiss();
    }

    private void backwardItem(){
        imageEditingCallBack.onBackwardImageBitmapCallBack();
        dismiss();
    }

    private void cutOut(){
        couout_add.removeAllViews();
        hzs.setVisibility(View.VISIBLE);
        final Filter fooFilter = SampleFilters.getAweStruckVibeFilter();
        final Filter fooFilter1 = SampleFilters.getNightWhisperFilter();
        final Filter fooFilter2 = SampleFilters.getStarLitFilter();
        final Filter fooFilter3 = SampleFilters.getBlueMessFilter();
        final Filter fooFilter4 = SampleFilters.getLimeStutterFilter();
        for(int i = 0; i<7;i++){
            View cout_out_item = getLayoutInflater().inflate(R.layout.cout_out_single, null);
            final ImageView coutimg = (ImageView)cout_out_item.findViewById(R.id.cout);

            if(i == 0){
              coutimg.setImageBitmap(bitmap);
               cout_out_item.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       hzs.setVisibility(View.GONE);
                       set_image.setImageBitmap(bitmap);
                   }
               });

           }else if(i == 1){
               final int c = bitmap.getPixel(100, 100);
               coutimg.setImageBitmap(replaceColor(bitmap,c,Color.BLACK));
               cout_out_item.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       hzs.setVisibility(View.GONE);
                       set_image.setImageBitmap(replaceColor(bitmap,c,Color.BLACK));
                       set_image.setTag("1");
                   }
               });
           }else if(i == 2){
                coutimg.setImageBitmap(fooFilter.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                cout_out_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hzs.setVisibility(View.GONE);
                        set_image.setImageBitmap(fooFilter.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                        set_image.setTag("1");
                    }
                });
            }else if(i == 3){
                coutimg.setImageBitmap(fooFilter1.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                cout_out_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hzs.setVisibility(View.GONE);
                        set_image.setImageBitmap(fooFilter1.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                        set_image.setTag("1");
                    }
                });
            }else if(i == 4){
                coutimg.setImageBitmap(fooFilter2.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                cout_out_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hzs.setVisibility(View.GONE);
                        set_image.setImageBitmap(fooFilter2.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                        set_image.setTag("1");
                    }
                });
            }else if(i == 5){
                coutimg.setImageBitmap(fooFilter3.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                cout_out_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hzs.setVisibility(View.GONE);
                        set_image.setImageBitmap(fooFilter3.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                        set_image.setTag("1");
                    }
                });
            }else if(i == 6){
                coutimg.setImageBitmap(fooFilter4.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                cout_out_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hzs.setVisibility(View.GONE);
                        set_image.setImageBitmap(fooFilter4.processFilter(Bitmap.createScaledBitmap(bitmap, 640, 640, false)));
                        set_image.setTag("1");
                    }
                });
            }
            couout_add.addView(cout_out_item);

            /*View cout_out_item = getLayoutInflater().inflate(R.layout.cout_out_single, null);
            ImageView coutimg = (ImageView)cout_out_item.findViewById(R.id.cout);
            coutimg.setImageBitmap(bitmap);
            cout_out_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hzs.setVisibility(View.GONE);
                }
            });
            couout_add.addView(cout_out_item);*/
        }
    }


    private Bitmap createDrawableSelectedImage(){
       // Drawable d = new BitmapDrawable(context.getResources(), C.getBitmap(editView));
        //return  replaceColor(C.getBitmapForCanvas(editView),0xFFFFFFFF,Color.BLACK);
        return  setMask();
    }

    public Bitmap replaceColor(Bitmap src,int fromColor, int targetColor) {
        if(src == null) {
            return null;
        }
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < pixels.length; ++x) {
            pixels[x] = (pixels[x] == fromColor) ? Color.TRANSPARENT : pixels[x];
        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Bitmap setMask(){
      Bitmap original = bitmap;
       // Bitmap mask = replaceColor(C.getBitmapForCanvas(editView),0xFFFFFFFF,Color.BLACK);
        Bitmap maskBitmap = C.getBitmapForCanvas(editView);
        //Bitmap mask = C.getBitmapForCanvas(editView);

        Log.e("hw values","or height"+bitmap.getHeight()+"ow width"+bitmap.getWidth()+"mask height"+maskBitmap.getHeight()+"mask width"+maskBitmap.getWidth());

        Bitmap result = Bitmap.createBitmap(maskBitmap.getWidth(), maskBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(original, 0, 0, null);
        mCanvas.drawBitmap(maskBitmap, 0, 0, paint);
        paint.setXfermode(null);
        Log.e("result bitmap h",result.getHeight()+"wid"+result.getWidth());
        return result;
    }
}
