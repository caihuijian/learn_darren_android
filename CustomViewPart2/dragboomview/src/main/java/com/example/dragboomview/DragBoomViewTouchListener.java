package com.example.dragboomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Cai Huijian on 2021/1/11.
 */
class DragBoomViewTouchListener implements View.OnTouchListener {
    private DragBoomView mDragView;
    private WindowManager mWindowManager;
    private Context mContext;
    private View mOriginView;
    private WindowManager.LayoutParams mParams;

    // 爆炸帧动画
    private FrameLayout mBombFrame;
    private ImageView mBombImage;
    private DragViewDisappearListener mDisappearListener;

    public DragBoomViewTouchListener(View mOriginView, DragViewDisappearListener disappearListener) {
        this.mOriginView = mOriginView;
        this.mContext = mOriginView.getContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDragView = new DragBoomView(mContext);
        mDragView.setDragViewTouchListener(this);
        mParams = new WindowManager.LayoutParams();
        // 背景要透明
        mParams.format = PixelFormat.TRANSPARENT;

        //爆炸动画初始化
        mBombFrame = new FrameLayout(mContext);
        mBombImage = new ImageView(mContext);
        mBombImage.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mBombFrame.addView(mBombImage);
        this.mDisappearListener = disappearListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int[] location = new int[2];
                mOriginView.getLocationOnScreen(location);
                //3.1 遇到问题originView的位置不对，手指点的位置不对
                //location是view左上角相对屏幕的位置，需要确保固定圆在mOriginView的中心就要加上view宽高的一半，同时还要减去图状态栏高度
                mDragView.initPoints(location[0] + (mOriginView.getMeasuredWidth() / 2), location[1] + (mOriginView.getMeasuredHeight() / 2) - Utils.getStatusBarHeight(mContext));

                Bitmap bitmap = Utils.getBitmapByView(mOriginView);
                mDragView.setCaptureView(bitmap);
                mWindowManager.addView(mDragView, mParams);
                //2.按下的时候将原先的View隐藏
                mOriginView.setVisibility(View.INVISIBLE);
            case MotionEvent.ACTION_MOVE://3.移动的时候 不停绘制贝塞尔曲线以及截图
                //这里传的点和down的点不统一 我认为不太好
                mDragView.updatePosition(event.getRawX(), event.getRawY() - Utils.getStatusBarHeight(mContext));
                break;
            case MotionEvent.ACTION_UP:
                //4.对手指抬起做监听
                mDragView.dealWithActionUp();
                break;
        }
        return true;
    }

    public void reset() {
        // 把创建的贝塞尔曲线以及截图删除
        mWindowManager.removeView(mDragView);
        // 把原来的View显示
        mOriginView.setVisibility(View.VISIBLE);
    }

    //帧动画
    public void dismiss(PointF pointF) {
        // 移除截图的View
        mWindowManager.removeView(mDragView);
        // 要在 mWindowManager 添加一个爆炸动画
        mWindowManager.addView(mBombFrame, mParams);
        mBombImage.setBackgroundResource(R.drawable.anim_bubble_pop);

        AnimationDrawable drawable = (AnimationDrawable) mBombImage.getBackground();
        mBombImage.setX(pointF.x - drawable.getIntrinsicWidth() / 2);
        mBombImage.setY(pointF.y - drawable.getIntrinsicHeight() / 2);

        drawable.start();
        // 等它执行完之后我要移除掉这个 爆炸动画也就是 mBombFrame
        mBombImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWindowManager.removeView(mBombFrame);
                // 通知一下外面该消失
                if (mDisappearListener != null) {
                    mDisappearListener.viewDismiss(mOriginView);
                }
            }
        }, getAnimationDrawableTime(drawable));
    }

    private long getAnimationDrawableTime(AnimationDrawable drawable) {
        int numberOfFrames = drawable.getNumberOfFrames();
        long time = 0;
        for (int i = 0; i < numberOfFrames; i++) {
            time += drawable.getDuration(i);
        }
        return time;
    }

    public interface DragViewDisappearListener {
        void viewDismiss(View view);
    }
}
