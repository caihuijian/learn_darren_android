package com.example.dragboomview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by Cai Huijian on 2021/1/10.
 */
class DragBoomView extends View {
    private PointF mFixPoint;
    private PointF mFingerPoint;
    private Paint mPaint;
    private float mFixPointInitRadius = 10;//固定圆初始半径
    private float minFixPointRadius = 5;//固定圆最小半径 如果比这个更小 不进行绘制
    private float mFixPointChangedRadius;//挪动手指后的固定圆半径
    private float mFingerPointRadius = 10;
    private DragBoomViewTouchListener mDragBoomViewTouchListener;

    private Bitmap mCaptureView;//截图


    public DragBoomView(Context context) {
        this(context, null);
    }

    public DragBoomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragBoomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        mFixPointInitRadius = Utils.dp2px(mFixPointInitRadius, context);
        mFingerPointRadius = Utils.dp2px(mFingerPointRadius, context);
        minFixPointRadius = Utils.dp2px(minFixPointRadius, context);
    }

    public static void attachToView(View textView) {
        textView.setOnTouchListener(new DragBoomViewTouchListener(textView));
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mFingerPoint == null || mFixPoint == null) {
            return;
        }
        //绘制固定圆
        if (isNeedShowBezier()) {//当手指离固定圆太远 不绘制固定圆

            canvas.drawCircle(mFixPoint.x, mFixPoint.y, mFixPointChangedRadius, mPaint);
            canvas.drawPath(getBezierPath(), mPaint);
        }

        //绘制跟随手指的圆
        canvas.drawCircle(mFingerPoint.x, mFingerPoint.y, mFingerPointRadius, mPaint);

        //绘制截图
        if (mCaptureView != null) {
            canvas.drawBitmap(mCaptureView, mFingerPoint.x - mCaptureView.getWidth() / 2, mFingerPoint.y - mCaptureView.getHeight() / 2, mPaint);
        }
    }

    //计算两点之间的距离
    double distanceOfPoints(PointF point1, PointF point2) {
        //勾股定理
        return Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mFixPoint = new PointF();
//                mFingerPoint = new PointF();
//                mFixPoint.x = event.getX();
//                mFixPoint.y = event.getY();
//                mFingerPoint.x = event.getX();
//                mFingerPoint.y = event.getY();
//            case MotionEvent.ACTION_MOVE:
//                mFingerPoint.x = event.getX();
//                mFingerPoint.y = event.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        //根据手指的移动 计算两点之间的距离 根据距离改变mFixPoint的半径
//        mFixPointChangedRadius = calculateFixPointRadius(distanceOfPoints(mFingerPoint, mFixPoint));
//        invalidate();
//        return true;
//    }

    private float calculateFixPointRadius(double distanceOfPoints) {
        return mFixPointInitRadius - (float) distanceOfPoints / 25f;//除数（20f）越大，代表半径随距离变化的程度越小
    }

    private Path getBezierPath() {
        Path bezierPath = new Path();
        //计算四点坐标
        //先计算∠a ∠a = arctan（（r1.y-r2.y）/（r1.x-r2.x））
        double angleA = Math.atan((mFixPoint.y - mFingerPoint.y) / (mFixPoint.x - mFingerPoint.x));
        //计算x y以及 x' y'
        float x = (float) (Math.sin(angleA) * mFixPointChangedRadius);
        float y = (float) (Math.cos(angleA) * mFixPointChangedRadius);

        float x2 = (float) (Math.sin(angleA) * mFingerPointRadius);
        float y2 = (float) (Math.cos(angleA) * mFingerPointRadius);
        //四个点坐标分别为 A1  B1 A2 B2 他们坐标表示为
        float A1x = mFixPoint.x + x;
        float A1y = mFixPoint.y - y;

        float B1x = mFixPoint.x - x;
        float B1y = mFixPoint.y + y;

        float A2x = mFingerPoint.x + x2;
        float A2y = mFingerPoint.y - y2;

        float B2x = mFingerPoint.x - x2;
        float B2y = mFingerPoint.y + y2;

        float controlPint1x = (mFixPoint.x + mFingerPoint.x) * 0.5f;
        float controlPint1y = (mFixPoint.y + mFingerPoint.y) * 0.5f;

        //绘制路径为A1 A2 B2 B1
        bezierPath.moveTo(A1x, A1y);
        bezierPath.quadTo(controlPint1x, controlPint1y, A2x, A2y);
        bezierPath.lineTo(B2x, B2y);
        bezierPath.quadTo(controlPint1x, controlPint1y, B1x, B1y);
        bezierPath.close();
        return bezierPath;
    }

    public void setDragViewTouchListener(DragBoomViewTouchListener dragBoomViewTouchListener) {
        mDragBoomViewTouchListener = dragBoomViewTouchListener;
    }

    public void setCaptureView(Bitmap bitmap) {
        this.mCaptureView = bitmap;
    }

    public void updatePosition(float rawX, float rawY) {
        if (mFingerPoint == null) {
            mFingerPoint = new PointF();
        }
        mFingerPoint.x = rawX;
        mFingerPoint.y = rawY;
        mFixPointChangedRadius = calculateFixPointRadius(distanceOfPoints(mFingerPoint, mFixPoint));
        invalidate();
    }

    public void initPoints(float pointX, float pointY) {
        mFixPoint = new PointF(pointX, pointY);
        mFingerPoint = new PointF(pointX, pointY);
        invalidate();
    }

    private boolean isNeedShowBezier() {
        return mFixPointChangedRadius > Utils.dp2px(minFixPointRadius, getContext());
    }

    public void dealActionUp() {
        if (this.isNeedShowBezier()) { //4.1如果 抬起时距离不大，view回弹
            playBackAnimate();
        } else {//4.5如果View拖动很远 则触发消失的动作。将原先的View设置为Gone，隐藏拖动的截图，播放爆炸的帧动画，播放完毕释放资源

        }
    }

    private void playBackAnimate() {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(this, "translationX", mFingerPoint.x, mFixPoint.x);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this, "translationY", mFingerPoint.y, mFixPoint.y);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationX, translationY);
        animatorSet.setDuration(1000 * 2);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mDragBoomViewTouchListener != null) {
                    mDragBoomViewTouchListener.reset();
                }
            }
        });
    }
}
