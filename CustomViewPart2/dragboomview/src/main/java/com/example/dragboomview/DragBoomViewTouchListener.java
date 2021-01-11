package com.example.dragboomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Cai Huijian on 2021/1/11.
 */
class DragBoomViewTouchListener implements View.OnTouchListener {
    private DragBoomView mDrageView;
    private WindowManager mWindowManager;
    private Context mContext;
    private View mOriginView;
    private View mCaptureView;
    private WindowManager.LayoutParams mParams;

    public DragBoomViewTouchListener(View mOriginView) {
        this.mOriginView = mOriginView;
        this.mContext = mOriginView.getContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDrageView = new DragBoomView(mContext);
        mDrageView.setDragViewTouchListener(this);
        mParams = new WindowManager.LayoutParams();
        // 背景要透明
        mParams.format = PixelFormat.TRANSPARENT;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("TAG", "onTouch: " + event.getRawX() + " " + event.getRawY());
                //2.2将创建的截图和DragView一起显示到界面
                int[] location = new int[2];
                mOriginView.getLocationOnScreen(location);
                //3.1 遇到问题originView的位置不对，手指点的位置不对
                //location是view左上角相对屏幕的位置，需要确保固定圆在mOriginView的中心就要加上view宽高的一半，同时还要减去图状态栏高度
                mDrageView.initOriginView(location[0] + (mOriginView.getMeasuredWidth() >> 1), location[1] + (mOriginView.getMeasuredHeight() >> 1) - Utils.getStatusBarHeight(mContext));

                Bitmap bitmap = Utils.getBitmapByView(mOriginView);
                mDrageView.setCaptureView(bitmap);
                mDrageView.updatePosition(location[0] + (mOriginView.getMeasuredWidth() >> 1), location[1] + (mOriginView.getMeasuredHeight() >> 1) - Utils.getStatusBarHeight(mContext));
                mWindowManager.addView(mDrageView, mParams);
                //2.按下的时候将原先的View隐藏
                mOriginView.setVisibility(View.INVISIBLE);
                Log.e("TAG", "onTouch INVISIBLE: ");
                return true;
            case MotionEvent.ACTION_MOVE://3.移动的时候 不停绘制贝塞尔曲线以及截图
                mDrageView.updatePosition(event.getRawX(), event.getRawY());
                break;
        }
        return false;
    }
}
