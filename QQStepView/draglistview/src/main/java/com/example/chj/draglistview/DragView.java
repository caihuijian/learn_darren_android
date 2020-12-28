package com.example.chj.draglistview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

public class DragView extends FrameLayout {
    private ViewDragHelper mViewDragHelper;
    private ViewGroup frontView, behindView;
    private boolean isMenuOpen = false;

    public DragView(@NonNull Context context) {
        this(context, null);
    }

    public DragView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //1.创建和使用ViewDragHelper
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
        //1.创建和使用ViewDragHelper
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
            return view == frontView;//2.1 后面不能拖动 只有前面的view可以拖动
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //top代表垂直拖动的距离 dy代表垂直方向的速度还是加速度之类的？
            //Log.d("TAG", "clampViewPositionVertical: top->" + top + " dy-> " + dy);
            //2.3 垂直拖动的范围最大只能是后面菜单 View 的高度
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
            return 0;//2.2 列表只能垂直拖动 注释掉clampViewPositionHorizontal或者强制返回0 让view无法水平拖动
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //Log.d("TAG", "onViewReleased: xvel->" + xvel + " yvel-> " + yvel);
            //Log.d("TAG", "onViewReleased: releasedChild.getTop()->" + releasedChild.getTop());
            //2.4 手指松开的时候两者选其一，要么打开要么关闭 2选1 要么全露出后面的view 要么全遮住后面的view
            //2.4 需要结合computeScroll使用 两个都要调用刷新
            if (releasedChild.getTop() < behindView.getMeasuredHeight() / 2) {
                //全遮住后面的view 折叠态 菜单关闭
                isMenuOpen = false;
                mViewDragHelper.settleCapturedViewAt(0, 0);
            } else {
                //全露出后面的view 展开态 菜单打开
                isMenuOpen = true;
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
    //上面这个报错 原因如下
    //DragView.onInterceptTouchEvent().DOWN -> 拦截 -> listView.onTouch() ->
    //DragView.onInterceptTouchEvent().MOVE->DragView.onTouchEvent().MOVE
    //DragView的onTouchEvent缺失了down的处理部分 mViewDragHelper.processTouchEvent(event);也就缺失了down事件
    //事件不完整
    private float mDownY = 0f;

    //3 事件的分发和拦截 需要结合canChildScrollUp使用 canChildScrollUp来自SwipeRefreshLayout的对应方法
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //菜单打开（展开态-非折叠态）父容器拦截所有事件 listView不应该自己处理事件
        if (isMenuOpen) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //让mViewDragHelper获取完整事件 否则mViewDragHelper会丢弃整个事件（只得到move事件不处理）
                mViewDragHelper.processTouchEvent(ev);
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                //菜单关闭 （折叠态）并且是向下滑动 并且listView内部不能向下滚动 父容器拦截所有事件 listView不应该自己处理事件
                if (moveY > mDownY && !canChildScrollUp()) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     * 判断View是否滚动到了最顶部,还能不能向上滚
     * 这个方法比较老了
     */
    public boolean canChildScrollUpOld() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (frontView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) frontView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(frontView, -1) || frontView.getScrollY() > 0;
            }
        } else {
            return frontView.canScrollVertically(-1);
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     * 判断View是否滚动到了最顶部,还能不能向上滚
     * 这个是API29最新的code 与canChildScrollUpOld功能相同
     */
    public boolean canChildScrollUp() {
        return frontView instanceof ListView ? ListViewCompat.canScrollList((ListView) frontView, -1) : frontView.canScrollVertically(-1);
    }
}
