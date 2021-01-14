package com.example.rotateloadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by hjcai on 2021/1/14.
 */
class RotateLoadingView extends View {
    // 当前动画状态
    AnimateStatus mCurrentAnimateStatus;
    // 绘制各种圆 背景的画笔
    Paint mPaint;
    // 是否已经初始化
    boolean mInitialized = false;
    // 小圆的几个颜色
    int[] mColors;
    // 每个小圆对应的角度（将2π分割为n份 n代表颜色的数目） 用于表示各个小圆初始的位置
    double mPerAngle;
    // 旋转经过的角度 结合每个圆对应的角度来表示旋转时各个小圆的位置
    double mRotatedAngle = 0;

    // view的宽高
    int mViewHeight, mViewWidth;
    // 大圆的半径
    int mBigCircleRadius;
    // 小圆的半径
    int mSmallCircleRadius;
    // 绘制各种状态时候的小圆的中心点
    int mCenterX, mCenterY;

    public RotateLoadingView(Context context) {
        this(context, null);
    }

    public RotateLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!mInitialized) {
            initParams();
        }
    }

    private void initParams() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        int color0 = Color.parseColor("#FFAAFC");
        int color1 = Color.parseColor("#FFCCCC");
        int color2 = Color.parseColor("#CCFFCC");
        int color3 = Color.parseColor("#CCCCFF");
        int color4 = Color.parseColor("#CCFFFF");
        int color5 = Color.parseColor("#FFFF00");
        mColors = new int[]{color0, color1, color2, color3, color4, color5};
        mPerAngle = Math.PI * 2 / mColors.length;
        mInitialized = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurrentAnimateStatus == null) {
            mCurrentAnimateStatus = new RotateAnimateStatus();
        }
        mCurrentAnimateStatus.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mBigCircleRadius = Math.min(mViewHeight, mViewWidth) / 4;
        mSmallCircleRadius = mBigCircleRadius / 8;
        mCenterX = mViewWidth / 2;
        mCenterY = mViewHeight / 2;
    }

    class RotateAnimateStatus extends AnimateStatus {
        @Override
        void draw(Canvas canvas) {
            for (int i = 0; i < mColors.length; i++) {
                mPaint.setColor(mColors[i]);
                double currentAngle = mPerAngle * i + mRotatedAngle;
                canvas.drawCircle((float) (mCenterX + Math.sin(currentAngle) * mBigCircleRadius), (float) (mCenterY + Math.cos(currentAngle) * mBigCircleRadius), mSmallCircleRadius, mPaint);
            }
        }
    }

    abstract class AnimateStatus {
        abstract void draw(Canvas canvas);
    }
}
