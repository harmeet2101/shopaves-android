package com.shop.shopaves.Constant;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TableRow;
import android.widget.TextView;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by amsyt003 on 2/6/16.
 */
public class SideSelector extends LinearLayout {
    private static String TAG = SideSelector.class.getCanonicalName();

    public static char[] ALPHABET = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final int BOTTOM_PADDING = 10;

    private SectionIndexer selectionIndexer = null;
    private StickyListHeadersListView list;
//    private Paint paint;
    private String[] sections;
    private Context mContext;

    public SideSelector(Context context) {

        super(context);
        this.mContext=context;
        init();
    }

    public SideSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        init();
    }

    public SideSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext=context;
        init();
    }

    private void init() {
        /*setBackgroundColor(0x44FFFFFF);
        paint = new Paint();
        paint.setColor(0xFFA6A9AA);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);*/
    }

    public void setListView(StickyListHeadersListView _list) {
        list = _list;
        selectionIndexer = (SectionIndexer) _list.getAdapter();

        Object[] sectionsArr = selectionIndexer.getSections();
        sections = new String[sectionsArr.length];
        for (int i = 0; i < sectionsArr.length; i++) {
            sections[i] = sectionsArr[i].toString();
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int y = (int) event.getY();
        float selectedIndex = ((float) y / (float) getPaddedHeight()) * ALPHABET.length;
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                if (selectionIndexer == null) {
                    selectionIndexer = (SectionIndexer) list.getAdapter();
                }
                int position = selectionIndexer.getPositionForSection((int) selectedIndex);
                if (position == -1) {
                    return true;
                }
                list.setSelection(position);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return true;
    }

   /* protected void onDraw(Canvas canvas) {

        int viewHeight = getPaddedHeight();
        float charHeight = ((float) viewHeight) / (float) sections.length;

        float widthCenter = getMeasuredWidth() / 2;
        for (int i = 0; i < sections.length; i++) {
            canvas.drawText(String.valueOf(sections[i]), widthCenter, charHeight + (i * charHeight), paint);
        }
        super.onDraw(canvas);
    }*/


    private int getPaddedHeight() {
        return getHeight() - BOTTOM_PADDING;
    }
}
