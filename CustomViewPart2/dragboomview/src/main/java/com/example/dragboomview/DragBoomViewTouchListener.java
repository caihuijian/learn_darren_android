package com.example.dragboomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
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
                //2.按下的时候将原先的View隐藏
                mOriginView.setVisibility(View.INVISIBLE);
                //2.2将创建的截图和DragView一起显示到界面
                Bitmap bitmap = Utils.getBitmapByView(mOriginView);
                mDrageView.setCaptureView(bitmap);
                mWindowManager.addView(mDrageView,mParams);
                break;
        }
        return false;
    }
}
