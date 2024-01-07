package com.sliding;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class TouchlessScrollView extends ScrollView {

    /**
     * Creates a new instance of TouchlessScrollview.
     * @param context
     */
    public TouchlessScrollView(Context context) {
        this(context, null);
    }

    /**
     * Creates a new instance of TouchlessScrollview.
     * @param context
     * @param attrs
     */
    public TouchlessScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates a new instance of TouchlessScrollview.
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TouchlessScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Save the current y scroll position for later.
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        // Do not save the current scroll position. Always store scrollY=0 and delegate
        // responsibility of saving state to the MultiShrinkScroller.
        final int scrollY = getScrollY();
        setScrollY(0);
        final Parcelable returnValue = super.onSaveInstanceState();
        setScrollY(scrollY);
        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}