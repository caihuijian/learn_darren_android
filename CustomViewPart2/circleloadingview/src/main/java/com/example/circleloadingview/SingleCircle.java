package com.example.circleloadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by Cai Huijian on 2021/1/9.
 */
class SingleCircle extends View {
    private Paint mPaint;
    private int mRadius;
    private int mColor;

    public SingleCircle(Context context) {
        this(context, null);
    }

    public SingleCircle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //onMeasure onLayout onDraw的执行顺序 此时可以知道测量宽高
        mRadius = Math.min(getMeasuredHeight(), getMeasuredWidth()) / 2;
        canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, Util.dip2px(mRadius, getContext()), mPaint);
    }

    public void changeColor(int color) {
        mPaint.setColor(color);
        mColor = color;
        invalidate();
    }

    public int getColor() {
        return mColor;
    }

}
