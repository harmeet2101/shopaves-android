/**
 * PhotoSorterView.java
 * 
 * (c) Luke Hutchison (luke.hutch@mit.edu)
 * 
 * TODO: Add OpenGL acceleration.
 * 
 * --
 * 
 * Released under the MIT license (but please notify me if you use this code, so that I can give your project credit at
 * http://code.google.com/p/android-multitouch-controller ).
 * 
 * MIT license: http://www.opensource.org/licenses/MIT
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.shop.shopaves.Constant;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shop.shopaves.Interface.PublishCallBack;
import com.shop.shopaves.Util.Model;

import java.util.ArrayList;

public class PhotoSortrView extends View implements MultiTouchController.MultiTouchObjectCanvas<PhotoSortrView.Img> {
	private ArrayList<Img> mImages = new ArrayList<Img>();
	private ArrayList<ImgItemProperties> mPropertiesImage = new ArrayList<ImgItemProperties>();

	private float minxf,minyf,maxxf,maxyf;
	private Img selectImg;
	private boolean isLoadFirst = false;
	private int canvascount = 0;
	private  Context ctx;

	private ArrayList<imagePositionatView> imagePositionatViews = new ArrayList<>();
	// --
	private GestureDetector gestureDetector;

	private MultiTouchController<Img> multiTouchController = new MultiTouchController<Img>(this);

	private PublishCallBack publishCallBack;
	// --

	private Bitmap imageBitmap;
	private int mImagesPosition;
	private MultiTouchController.PointInfo currTouchPoint = new MultiTouchController.PointInfo();

	private boolean mShowDebugInfo = true;

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	// --
	private Model imageLoadModel;

	private Paint mLinePaintTouchPointCircle = new Paint();

	// ---------------------------------------------------------------------------------------------------

	public PhotoSortrView(Context context,PublishCallBack publishCallBack) {
		this(context, null,publishCallBack);
	}

	public PhotoSortrView(Context context, AttributeSet attrs,PublishCallBack publishCallBack) {
		this(context, attrs, 0,publishCallBack);
	}

	public PhotoSortrView(Context context, AttributeSet attrs, int defStyle,PublishCallBack publishCallBack) {
		super(context, attrs, defStyle);
		this.publishCallBack = publishCallBack;
		init(context);
	}

	private void init(Context context) {
		ctx = context;
		gestureDetector = new GestureDetector(context, new SingleTapConfirm());
		Resources res = context.getResources();
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		setBackgroundColor(Color.TRANSPARENT);
	}

	/** Called by activity's onResume() method to load the images */
	public void loadImages(Context context) {
		Resources res = context.getResources();
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).load(res);
	}

	/** Called by activity's onPause() method to free memory used for loading the images */
	public void unloadImages() {
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).unload();
	}

	// ---------------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).draw(canvas);
		//if (mShowDebugInfo)
		//	drawMultitouchDebugMarks(canvas);
	}

	// ---------------------------------------------------------------------------------------------------

	public void trackballClicked() {
		mUIMode = (mUIMode + 1) % 3;
		invalidate();
	}
/*
	private void drawMultitouchDebugMarks(Canvas canvas) {
		if (currTouchPoint.isDown()) {
			float[] xs = currTouchPoint.getXs();
			float[] ys = currTouchPoint.getYs();
			float[] pressures = currTouchPoint.getPressures();
			int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
			for (int i = 0; i < numPoints; i++)
				canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80, mLinePaintTouchPointCircle);
			if (numPoints == 2)
				canvas.drawLine(xs[0], ys[0], xs[1], ys[1], mLinePaintTouchPointCircle);
		}
	}*/

	// ---------------------------------------------------------------------------------------------------

	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(gestureDetector.onTouchEvent(event)){
			//Toast.makeText(context,"display", Toast.LENGTH_LONG).show();
			publishCallBack.onPublishCallBack(imageBitmap,mImagesPosition);
			return true;
		}else
		return multiTouchController.onTouchEvent(event);
	}

	/** Get the image that is under the single-touch point, or return null (canceling the drag op) if none */
	public Img getDraggableObjectAtPoint(MultiTouchController.PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = mImages.size();
		for (int i = n - 1; i >= 0; i--) {
			Img im = mImages.get(i);
			if (im.containsPoint(x, y))
				return im;
		}
		return null;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be under the point (non-null is returned by getDraggableObjectAtPoint())
	 * and a drag operation is starting. Called with null when drag op ends.
	 */
	public void selectObject(Img img, MultiTouchController.PointInfo touchPoint) {
		if(((BitmapDrawable)img.getDrawable()).getBitmap() != null){
			imageBitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
		}

		for(int i = 0; i<mImages.size();i++){
			if(mImages.get(i) == img){
				mImagesPosition = i;
			}
		}
		currTouchPoint.set(touchPoint);
		if (img != null) {
			// Move image to the top of the stack when selectedImg
			selectImg = img;
			/*mImages.remove(img);
			mImages.add(img);*/
		} else {
			// Called with img == null when drag stops.
		}
		invalidate();
		imageProperties(img);
	}


	public ArrayList<ImgItemProperties> imageProperties(Img img){
		boolean canAdded = false;
		if(mPropertiesImage.size() > 0){
			for(ImgItemProperties imgItemProperties : mPropertiesImage){
			//	ImgItemProperties imgItemProperties = mPropertiesImage.get(i);

				if(imgItemProperties.Id == img.id){
					canAdded = false;
					break;
				}
				else
					canAdded = true;
				/*else
				canAdded = false;*/

					//mPropertiesImage.add(new ImgItemProperties(img.id,""+img.getScaleY(),""+img.getScaleX(),""+img.getCenterX(),""+img.getCenterY(),""+img.getAngle()));
			}
			if(canAdded)
				mPropertiesImage.add(new ImgItemProperties(img.id,""+img.getScaleY(),""+img.getScaleX(),""+img.getCenterX(),""+img.getCenterY(),""+img.minX,""+img.minY,""+img.maxX,""+img.maxY,""+img.angle));
		else{
				if(mImagesPosition < mPropertiesImage.size()) {
					mPropertiesImage.remove(mImagesPosition);
					mPropertiesImage.add(mImagesPosition, new ImgItemProperties(img.id, "" + img.getScaleY(), "" + img.getScaleX(), "" + img.getCenterX(), "" + img.getCenterY(), "" + img.minX, "" + img.minY, "" + img.maxX, "" + img.maxY, "" + img.angle));
				}
			}
		}else{
			mPropertiesImage.add(new ImgItemProperties(img.id,""+img.getScaleY(),""+img.getScaleX(),""+img.getCenterX(),""+img.getCenterY(),""+img.minX,""+img.minY,""+img.maxX,""+img.maxY,""+img.angle));
		}
		return mPropertiesImage;
	}

	public ArrayList<ImgItemProperties> getAllImageProperties(){
		return  this.mPropertiesImage;
	}

	/** Get the current position and scale of the selected image. Called whenever a drag starts or is reset. */
	public void getPositionAndScale(Img img, MultiTouchController.PositionAndScale objPosAndScaleOut) {
		// FIXME affine-izem (and fix the fact that the anisotropic_scale part requires averaging the two scale factors)
		objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(img.getScaleX() + img.getScaleY()) / 2, (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(), img.getScaleY(),
				(mUIMode & UI_MODE_ROTATE) != 0, img.getAngle());
	}

	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(Img img, MultiTouchController.PositionAndScale newImgPosAndScale, MultiTouchController.PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		boolean ok = img.setPos(newImgPosAndScale);
		if (ok)
			invalidate();
		return ok;
	}
	// ----------------------------------------------------------------------------------------------

	class Img {
		private Drawable resId;
        private Drawable drawable;
		private int id;
        private boolean firstLoad;
        private int width, height, displayWidth, displayHeight;
        private float centerX, centerY, scaleX, scaleY, angle;
        private float minX, maxX, minY, maxY;
        private static final float SCREEN_MARGIN = 100;
		private   Model imagePositionModel;

		public Img(Drawable resId, Resources res,Model imagePosition,int id) {
			this.resId = resId;
			this.firstLoad = true;
			this.id = id;
			this.imagePositionModel = imagePosition;
			getMetrics(res);
		}

		private void getMetrics(Resources res) {
			DisplayMetrics metrics = res.getDisplayMetrics();
			// The DisplayMetrics don't seem to always be updated on screen rotate, so we hard code a portrait
			// screen orientation for the non-rotated screen here...
			// this.displayWidth = metrics.widthPixels;
			// this.displayHeight = metrics.heightPixels;
			this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels,
					metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
			this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels,
					metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);
		}

		/** Called by activity's onResume() method to load the images */
		public void load(Resources res) {
			getMetrics(res);
			this.drawable = resId;
			this.width = drawable.getIntrinsicWidth();
			this.height = drawable.getIntrinsicHeight();

				//	this.drawable = res.getDrawable(resId);

				float cx, cy, sx, sy;
				if (firstLoad) {
					/*cx = SCREEN_MARGIN + (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
					cy = SCREEN_MARGIN + (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));
					*/
					/*float sc = (float) (Math.max(displayWidth, displayHeight) / (float) Math.max(width, height) * Math.random() * 0.3 + 0.2);
					sx = sy = sc;*/
					if(imagePositionModel!=null){
						Model model = new Model(imagePositionModel.getImageProperties());
						cx = Float.parseFloat(model.getCenterX());
						cy = Float.parseFloat(model.getCenterY());
						sx = Float.parseFloat(model.getScaleX());
						sy = Float.parseFloat(model.getScaleY());
						this.minX = Float.parseFloat(model.getMinx());
						this.minY = Float.parseFloat(model.getMiny());
						this.maxX = Float.parseFloat(model.getMaxx());
						this.maxY = Float.parseFloat(model.getMaxy());
						setPos(cx, cy, sx, sy, Float.parseFloat(model.getAngle()));
					}
					else{
						cx = SCREEN_MARGIN + (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
					cy = SCREEN_MARGIN + (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));

						cx = width/2;
						cy = height/2;
					float sc = (float) (Math.max(displayWidth, displayHeight) / (float) Math.max(width, height) * Math.random() * 0.3 + 0.2);
					sx = sy = sc;
						setPos(cx, cy, sx, sy, 0.0f);
					//	firstLoad = false;
					}
					//firstLoad = false;
				} else {
					// Reuse position and scale information if it is available
					// FIXME this doesn't actually work because the whole activity is torn down and re-created on rotate
					cx = this.centerX;
					cy = this.centerY;
					sx = this.scaleX;
					sy = this.scaleY;
					// Make sure the image is not off the screen after a screen rotation
					if (this.maxX < SCREEN_MARGIN)
						cx = SCREEN_MARGIN;
					else if (this.minX > displayWidth - SCREEN_MARGIN)
						cx = displayWidth - SCREEN_MARGIN;
					if (this.maxY > SCREEN_MARGIN)
						cy = SCREEN_MARGIN;
					else if (this.minY > displayHeight - SCREEN_MARGIN)
						cy = displayHeight - SCREEN_MARGIN;
					setPos(cx, cy, sx, sy, 0.0f);
				}
				//setPos(cx, cy, sx, sy, 0.0f);
				//cX-> 250.03954 CY-> 336.54092 sX-> 0.5565003 sY-> 0.5565003 angle-> -0.6756139 minX-> 79.19394 minY-> 165.69533 maxX-> 420.88513 maxY-> 507.38654
		}

		/** Called by activity's onPause() method to free memory used for loading the images */
		public void unload() {
			this.drawable = null;
		}

		/** Set the position and scale of an image in screen coordinates */
		public boolean setPos(MultiTouchController.PositionAndScale newImgPosAndScale) {
			firstLoad = false;

			return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale
					.getScaleX() : newImgPosAndScale.getScale(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleY()
					: newImgPosAndScale.getScale(), newImgPosAndScale.getAngle());
			// FIXME: anisotropic scaling jumps when axis-snapping
			// FIXME: affine-ize
			// return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(), newImgPosAndScale.getScaleAnisotropicX(),
			// newImgPosAndScale.getScaleAnisotropicY(), 0.0f);
		}

		/** Set the position and scale of an image in screen coordinates */
		private boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle) {

			float ws = (displayWidth / 2) * scaleX, hs = (displayHeight / 2) * scaleY;
			float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX + ws, newMaxY = centerY + hs;
			if (newMinX > displayWidth - SCREEN_MARGIN || newMaxX < SCREEN_MARGIN || newMinY > displayHeight - SCREEN_MARGIN
					|| newMaxY < SCREEN_MARGIN)
				return false;
			this.centerX = centerX;
			this.centerY = centerY;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.angle = angle;
			if(!firstLoad && imagePositionModel!= null){
				this.minX = newMinX;
				this.minY = newMinY;
				this.maxX = newMaxX;
				this.maxY = newMaxY;
			}else if(imagePositionModel== null){
				this.minX = newMinX;
				this.minY = newMinY;
				this.maxX = newMaxX;
				this.maxY = newMaxY;
			}

			return true;
		}

		/** Return whether or not the given screen coords are inside this image */
		public boolean containsPoint(float scrnX, float scrnY) {
			// FIXME: need to correctly account for image rotation
			return (scrnX >= minX && scrnX <= maxX && scrnY >= minY && scrnY <= maxY);
		}

		public void draw(Canvas canvas) {
			canvas.save();

			float dx = (maxX + minX) / 2;
			float dy = (maxY + minY) / 2;
			drawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			canvas.translate(dx, dy);
			canvas.rotate(angle * 180.0f / (float) Math.PI);
			canvas.translate(-dx, -dy);
			drawable.draw(canvas);
			Log.e("points"," cX-> "+this.centerX+" CY-> "+
			this.centerY+" sX-> "+
			this.scaleX+" sY-> "+
			this.scaleY+" angle-> "+
			this.angle+" minX-> "+
			this.minX+" minY-> "+
			this.minY+" maxX-> "+
			this.maxX+" maxY-> "+
			this.maxY);
			Log.e("getImageWidth",""+getWidth());
			Log.e("getImageHeight",""+getHeight());
			getImgMatrix();
			canvas.restore();
		}
		public void getImgMatrix(){
			ImageView im =new  ImageView(getContext());
			im.setImageDrawable(drawable);
			Matrix matrix =  im.getImageMatrix();
			float[] values = new float[9];
			matrix.getValues(values);

			Log.e("matrixvalue","  "+values[0]+"  "+values[1]+"  "+values[2]+"  "+values[3]+"  "+values[4]+"  "+values[5]+"  "+values[6]+"  "+values[7]+"  "+values[8]);

		}
		public Drawable getDrawable() {
			return drawable;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public float getCenterX() {
			return centerX;
		}

		public float getCenterY() {
			return centerY;
		}

		public float getScaleX() {
			return scaleX;
		}

		public float getScaleY() {
			return scaleY;
		}

		public float getAngle() {
			return angle;
		}

		// FIXME: these need to be updated for rotation
		public float getMinX() {
			return minX;
		}

		public float getMaxX() {
			return maxX;
		}

		public float getMinY() {
			return minY;
		}

		public float getMaxY() {
			return maxY;
		}
	}

	public void addImages(Context context,Drawable resimg,Model imagePositions,int id){
		Resources res = context.getResources();
		mImages.add(new Img(resimg, res,imagePositions,id));
		imageLoadModel  = imagePositions;
		//unloadImages();
		loadImages(context);
		//invalidate();

	}

	private class imagePositionatView{
		String minx,miny,maxx,maxy,id;

		public imagePositionatView(String id,String minx, String miny, String maxx, String maxy) {
			this.minx = minx;
			this.miny = miny;
			this.maxx = maxx;
			this.maxy = maxy;
			this.id = id;
		}
	}

	private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			return true;
		}
	}

	public void removeCollectionImages(){
		mImages.remove(selectImg);
		invalidate();
	}
	public void goForward(){
		mImages.remove(selectImg);
		mImages.add(selectImg);
		invalidate();
	}

	public void goBackward(){
		mImages.remove(selectImg);
		mImages.add(0,selectImg);
		invalidate();
	}

	public void editImageAtPosition(int position,Bitmap bitmap){
		Drawable d = new BitmapDrawable(ctx.getResources(),bitmap);
		mImages.get(position).drawable = d;
		invalidate();
	/*	mImages.remove(position);
		addImages(ctx,d,null,4);
		//firstLoad = false;
		invalidate();*/
	}

}
