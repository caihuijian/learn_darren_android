package com.example.bezierview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by Cai Huijian on 2021/1/10.
 */
class BezierView extends View {
    PointF mFixPoint;
    PointF mFingerPoint;
    Paint mPaint;
    float mFixPointRadius = 10;//固定圆初始半径
    float mFixPointChangedRadius;//挪动手指后的固定圆半径
    float mFingerPointRadius = 10;


    public BezierView(Context context) {
        this(context, null);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
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
        if (mFixPointChangedRadius > 5) {//当手指离固定圆太远 不绘制固定圆
            canvas.drawCircle(mFixPoint.x, mFixPoint.y, mFixPointChangedRadius, mPaint);
        }

        //绘制跟随手指的圆
        canvas.drawCircle(mFingerPoint.x, mFingerPoint.y, mFingerPointRadius, mPaint);
    }

    //计算两点之间的距离
    double distanceOfPoints(PointF point1, PointF point2) {
        //勾股定理
        return Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFixPoint = new PointF();
                mFingerPoint = new PointF();
                mFixPoint.x = event.getX();
                mFixPoint.y = event.getY();
                mFingerPoint.x = event.getX();
                mFingerPoint.y = event.getY();
            case MotionEvent.ACTION_MOVE:
                mFingerPoint.x = event.getX();
                mFingerPoint.y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        //根据手指的移动 计算两点之间的距离 根据距离改变mFixPoint的半径
        mFixPointChangedRadius = calculateFixPointRadius(distanceOfPoints(mFingerPoint, mFixPoint));
        invalidate();
        return true;
    }

    private float calculateFixPointRadius(double distanceOfPoints) {
        return mFixPointRadius - (float) distanceOfPoints / 25f;//除数（20f）越大，代表半径随距离变化的程度越小
    }
}
