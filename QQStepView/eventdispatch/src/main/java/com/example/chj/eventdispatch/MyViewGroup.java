package com.example.chj.eventdispatch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MyViewGroup extends LinearLayout {
    private static final String TAG = "MyViewGroup";

    public MyViewGroup(Context context) {
        super(context);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent:" + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent:" + event.getAction());
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent:" + ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        measureChildren(MeasureSpec.AT_MOST,MeasureSpec.AT_MOST);
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            getChildAt(i).layout(0, 0, getChildAt(i).getMeasuredWidth(),getChildAt(i).getMeasuredHeight());
//        }
//
//    }
}
