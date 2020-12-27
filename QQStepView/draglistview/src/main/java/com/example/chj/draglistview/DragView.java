package com.example.chj.draglistview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

public class DragView extends FrameLayout {
    private ViewDragHelper mViewDragHelper;
    private ViewGroup frontView, behindView;
    private boolean isFolded = true;

    public DragView(@NonNull Context context) {
        this(context, null);
    }

    public DragView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = ViewDragHelper.create(this, mDragHelperCallback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //注意赋值时机 不能太靠前 太早behindView和frontView都还是空
        //Log.d("TAG", "DragView: " + this.getChildCount());
        if (this.getChildCount() != 2) {
            throw new RuntimeException("DragView 必须又2个子view");
        }
        //固然可以用findViewById来定位各个view 但这种getChildAt的方式更有利于我们了解各个view的位置
        behindView = (ViewGroup) this.getChildAt(0);
        frontView = (ViewGroup) this.getChildAt(1);
    }

    ListView getFrontListView() {
        return (ListView) frontView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);//需要给mViewDragHelper投喂事件才有效果 否则没反应
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //同理 需要给子view投喂事件 必须返回true
                return true;
        }
        return super.onTouchEvent(event);
    }

    private ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return view == frontView;//只有前面的view可以拖动
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //top代表垂直拖动的距离 dy代表垂直方向的速度还是加速度之类的？
            //Log.d("TAG", "clampViewPositionVertical: top->" + top + " dy-> " + dy);
            if (top < 0) {
                return 0;
            }
            if (top > behindView.getMeasuredHeight()) {
                return behindView.getMeasuredHeight();
            }
            return top;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // left代表水平拖动的距离 dx代表水平方向的速度还是加速度之类的？
            //Log.d("TAG", "clampViewPositionHorizontal: left->" + left + " dx-> " + dx);
            return 0;//注释掉clampViewPositionHorizontal或者强制返回0 让view无法水平拖动
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //Log.d("TAG", "onViewReleased: xvel->" + xvel + " yvel-> " + yvel);
            //Log.d("TAG", "onViewReleased: releasedChild.getTop()->" + releasedChild.getTop());
            //2选1 要么全露出后面的view 要么全遮住后面的view
            //需要结合computeScroll使用 两个都要调用刷新
            if (releasedChild.getTop() < behindView.getMeasuredHeight() / 2) {
                //全遮住后面的view 展开态
                isFolded = false;
                mViewDragHelper.settleCapturedViewAt(0, 0);
            } else {
                //全露出后面的view 折叠态
                isFolded = true;
                mViewDragHelper.settleCapturedViewAt(0, behindView.getMeasuredHeight());
            }
            invalidate();
        }

    };

    /**
     * Called by a parent to request that a child update its values for mScrollX
     * and mScrollY if necessary. This will typically be done if the child is
     * animating a scroll using a {@link android.widget.Scroller Scroller}
     * object.
     */
    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    //Ignoring pointerId=0 because ACTION_DOWN was not received for this pointer before ACTION_MOVE.
    //It likely happened because  ViewDragHelper did not receive all the events in the event stream.
    //报错 原因如下
    //DragView.onInterceptTouchEvent().DOWN -> 拦截 -> listView.onTouch() ->
    //DragView.onInterceptTouchEvent().MOVE->DragView.onTouchEvent().MOVE
    //DragView的onTouchEvent缺失了down的处理部分 mViewDragHelper.processTouchEvent(event);也就缺失了down事件
    //事件不完整
    float downY = 0f;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //展开态拦截？
//        if (!isFolded){
//            return true;
//        }
        boolean isMovingDown = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                mViewDragHelper.processTouchEvent(ev);//确保事件完整
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG", "onInterceptTouchEvent: ACTION_MOVE" + ev.getY());
                isMovingDown = ev.getY() > downY;//向下滑动
//                return isMovingDown;
        }
        Log.d("TAG", "onInterceptTouchEvent: " + ev.getY());
        if (isFolded && isMovingDown) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
