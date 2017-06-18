package com.shop.shopaves.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.shop.shopaves.Util.Model;

import java.util.ArrayList;
import java.util.List;

public class PhotoSortrView extends View implements MultiTouchController.MultiTouchObjectCanvas<MultiTouchEntity> {
    private ArrayList<ImageProp> mImages = new ArrayList();
    private MultiTouchController<MultiTouchEntity> multiTouchController = new MultiTouchController(this);
    private MultiTouchController.PointInfo currTouchPoint = new MultiTouchController.PointInfo();
    private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;
    private int mUIMode = UI_MODE_ROTATE;
    private static final float SCREEN_MARGIN = 100;
    private Context context;

    private int displayWidth, displayHeight;

    public PhotoSortrView(Context context) {
        this(context, null);
        this.context = context;
    }

    public PhotoSortrView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public PhotoSortrView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        this.context = context;
    }

    private void init(Context context) {
        Resources res = context.getResources();
        setBackgroundColor(Color.TRANSPARENT);
        DisplayMetrics metrics = res.getDisplayMetrics();
        this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
                .max(metrics.widthPixels, metrics.heightPixels) : Math.min(
                metrics.widthPixels, metrics.heightPixels);
        this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
                .min(metrics.widthPixels, metrics.heightPixels) : Math.max(
                metrics.widthPixels, metrics.heightPixels);
    }


	/*public void loadImages(Context context, int resourceId) {
        Resources res = context.getResources();
		ImageEntity e = new ImageEntity(resourceId, res);
		mImages.add(e);
		float cx = SCREEN_MARGIN
				+ (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
		float cy = SCREEN_MARGIN
				+ (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));
		mImages.get(mImages.size() - 1).load(context, cx, cy);
		invalidate();
	}*/


    public void loadImages(Context context, Bitmap bitmap,String prop[]) {
        Resources res = context.getResources();
        ImageEntity e = new ImageEntity(mImages.size(), bitmap, res);
        ImageProp imp = new ImageProp();


//        imp.setProductId((long) m.getProductId());
//        imp.setId((long) m.getId());
//        imp.setEditProductImage(m.getEditProductImage());
//        imp.setProductImage(m.getProductImage());

        if (prop != null && prop.length > 9){
            e.mScaleX = Float.parseFloat(prop[0]);
//        e.mAngle = Float.parseFloat(prop[1]);
            e.mAngle = Float.parseFloat(prop[2]);
            e.mScaleY = Float.parseFloat(prop[3]);
//        e.mAngle = Float.parseFloat(prop[4]);
//        e.mAngle = Float.parseFloat(prop[5]);
            e.mCenterX = Float.parseFloat(prop[6]);
            e.mCenterY = Float.parseFloat(prop[7]);
            e.mWidth = Integer.parseInt(prop[8]);
            e.mHeight = Integer.parseInt(prop[9]);
//        e.mAngle = Float.parseFloat(prop[10]);
//        e.mAngle = Float.parseFloat(prop[11]);
        }

        imp.setEntity(e);
        imp.setFlag(false);
        mImages.add(imp);
        float cx = SCREEN_MARGIN + (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
        float cy = SCREEN_MARGIN + (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));
        mImages.get(mImages.size() - 1).getEntity().load(context, cx, cy);
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int n = mImages.size();
        for (int i = 0; i < n; i++)
            mImages.get(i).getEntity().draw(canvas);
    }

    public List<ImageProp> getItems(){
        return mImages;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return multiTouchController.onTouchEvent(event);
    }

    /**
     * Get the image that is under the single-touch point, or return null
     * (canceling the drag op) if none
     */
    public MultiTouchEntity getDraggableObjectAtPoint(MultiTouchController.PointInfo pt) {
        float x = pt.getX(), y = pt.getY();
        int n = mImages.size();
        for (int i = n - 1; i >= 0; i--) {
            ImageEntity im = (ImageEntity) mImages.get(i).getEntity();
            if (im.containsPoint(x, y))
                return im;
        }
        return null;
    }

    /**
     * Select an object for dragging. Called whenever an object is found to be
     * under the point (non-null is returned by getDraggableObjectAtPoint()) and
     * a drag operation is starting. Called with null when drag op ends.
     */
    public void selectObject(MultiTouchEntity img, MultiTouchController.PointInfo touchPoint) {
        currTouchPoint.set(touchPoint);
        if (img != null) {
            // Move image to the top of the stack when selected
            ImageProp imp = mImages.get(img.zIndex);
            mImages.remove(imp);
            mImages.add(imp);
        } else {
            // Called with img == null when drag stops.
        }
        invalidate();
    }

    /**
     * Get the current position and scale of the selected image. Called whenever
     * a drag starts or is reset.
     */
    public void getPositionAndScale(MultiTouchEntity img,
                                    MultiTouchController.PositionAndScale objPosAndScaleOut) {
        objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(),
                (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
                (img.getScaleX() + img.getScaleY()) / 2,
                (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(),
                img.getScaleY(), (mUIMode & UI_MODE_ROTATE) != 0,
                img.getAngle());
    }

    /**
     * Set the position and scale of the dragged/stretched image.
     */
    public boolean setPositionAndScale(MultiTouchEntity img, MultiTouchController.PositionAndScale newImgPosAndScale, MultiTouchController.PointInfo touchPoint) {
        currTouchPoint.set(touchPoint);
        boolean ok = ((ImageEntity) img).setPos(newImgPosAndScale);
        if (ok)
            invalidate();
        return ok;
    }

    public boolean pointInObjectGrabArea(MultiTouchController.PointInfo pt, MultiTouchEntity img) {
        return false;
    }

    public class ImageProp {
        private long productId;
        private MultiTouchEntity entity;
        private boolean flag;
        private String editProductImage;
        private long id;
        private String productImage;

        public ImageProp() {
        }

        public long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public MultiTouchEntity getEntity() {
            return entity;
        }

        public void setEntity(MultiTouchEntity entity) {
            this.entity = entity;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public String getEditProductImage() {
            return editProductImage;
        }

        public void setEditProductImage(String editProductImage) {
            this.editProductImage = editProductImage;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getProductImage() {
            return productImage;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        display.getMetrics(metrics);
//        int width = metrics.widthPixels;
//        super.onMeasure(width, width);
//    }
}
