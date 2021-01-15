package com.example.rotateloadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * Created by hjcai on 2021/1/14.
 * RotateLoadingView 存在三种动画状态 MergeAnimationStatus ExtendAnimationStatus RotateAnimationStatus
 * 三种动画状态的控制状态如下
 * <p>
 * 创建RotateLoadingView之后 RotateLoadingView自动进入RotateAnimationStatus状态 该状态要由外部打破 否则持续执行
 * 当外部加载完毕 调用loadComplete方法，RotateLoadingView取消RotateAnimationStatus状态  进入MergeAnimationStatus状态
 * MergeAnimationStatus动画执行完毕进入ExtendAnimationStatus状态 ExtendAnimationStatus执行完毕 将当前RotateLoadingView隐藏
 */
class RotateLoadingView extends View {
    // 动画时长
    private static final int ANIMATION_DURATION = 1500;
    // 当前动画状态
    AnimateStatus mCurrentAnimateStatus;
    // 绘制各种圆 背景的画笔
    Paint mPaint;
    // 是否已经初始化
    boolean mInitialized = false;
    // 小圆的几个颜色
    int[] mColors;
    // 每个小圆对应的角度（将2π分割为n份 n代表颜色的数目） 用于表示各个小圆初始的位置
    float mPerAngle;
    // 旋转经过的角度 结合每个圆对应的角度来表示旋转时各个小圆的位置
    float mRotatedAngle = 0;
    // view的宽高
    int mViewHeight, mViewWidth;
    // 大圆的半径
    float mBigCircleRadius;
    // 小圆的半径
    float mSmallCircleRadius;
    // 绘制各种状态时候的小圆的中心点
    int mCenterX, mCenterY;
    // 扩展动画透明洞洞的半径
    float mHoleRadius = 0;
    // View对角线的一半
    private float mDiagonalDist;

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
        int color5 = Color.parseColor("#00FF00");
        mColors = new int[]{color0, color1, color2, color3, color4, color5};
        mPerAngle = (float) (Math.PI * 2 / mColors.length);
        mInitialized = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurrentAnimateStatus == null) {
            // 初始状态为RotateAnimationStatus
            mCurrentAnimateStatus = new RotateAnimationStatus();
        }
        // 各个状态进行各自的绘制
        mCurrentAnimateStatus.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 初始化依赖获取view宽高的变量
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mBigCircleRadius = Math.min(mViewHeight, mViewWidth) / 4;
        mSmallCircleRadius = mBigCircleRadius / 8;
        mCenterX = mViewWidth / 2;
        mCenterY = mViewHeight / 2;
        mDiagonalDist = (float) Math.sqrt(mCenterX * mCenterX + mCenterY * mCenterY);
    }

    // 当数据加载完毕 调用该方法 来停掉旋转动画并开启聚合动画
    public void loadComplete() {
        if (mCurrentAnimateStatus == null) {
            return;
        }
        mCurrentAnimateStatus.cancel();
        // 从RotateAnimationStatus进入MergeAnimationStatus
        mCurrentAnimateStatus = new MergeAnimationStatus();
    }

    //复用方法 RotateAnimationStatus和MergeAnimationStatus可以公用该方法
    private void drawSmallCircle(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (int i = 0; i < mColors.length; i++) {
            mPaint.setColor(mColors[i]);
            double currentAngle = mPerAngle * i + mRotatedAngle;
            canvas.drawCircle((float) (mCenterX + Math.sin(currentAngle) * mBigCircleRadius), (float) (mCenterY - Math.cos(currentAngle) * mBigCircleRadius), mSmallCircleRadius, mPaint);
        }
    }

    //旋转动画 本质是ValueAnimator改变mRotatedAngle 旋转角 然后在onDraw方法不停绘制
    class RotateAnimationStatus extends AnimateStatus {

        ValueAnimator rotateAnimator;

        public RotateAnimationStatus() {

            //想一想 逆时针旋转如何实现
            rotateAnimator = ValueAnimator.ofFloat(0, (float) (Math.PI * 2));
            rotateAnimator.setDuration(ANIMATION_DURATION);
            //默认的插值器走走停停 使用匀速的插值器替换
            rotateAnimator.setInterpolator(new LinearInterpolator());
            rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
            rotateAnimator.addUpdateListener(animation -> {
                mRotatedAngle = (float) animation.getAnimatedValue();
                invalidate();
            });
            rotateAnimator.start();
        }

        @Override
        void draw(Canvas canvas) {
            drawSmallCircle(canvas);
        }

        @Override
        void cancel() {
            rotateAnimator.cancel();
        }

        @Override
        void pause() {
            rotateAnimator.pause();
        }

        @Override
        void resume() {
            rotateAnimator.resume();
        }

    }

    //聚合动画 本质是ValueAnimator改变mBigCircleRadius 大圆半径 然后在onDraw方法不停绘制
    class MergeAnimationStatus extends AnimateStatus {
        private final ValueAnimator mValueAnimator;

        public MergeAnimationStatus() {

            //大圆半径从大变小
            mValueAnimator = ValueAnimator.ofFloat(mBigCircleRadius, 0);
            mValueAnimator.setDuration(ANIMATION_DURATION / 2);
            mValueAnimator.addUpdateListener(animation -> {
                mBigCircleRadius = (float) animation.getAnimatedValue();// 最大半径到 0
                // 重新绘制
                invalidate();
            });
            // 开始的时候向后然后向前甩
            mValueAnimator.setInterpolator(new AnticipateInterpolator(3f));
            // 等聚合完毕画展开
            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentAnimateStatus = new ExtendAnimationStatus();//从MergeAnimationStatus进入ExtendAnimationStatus
                    Log.e("TAG", "MergeAnimationStatus: ");
                }
            });
            mValueAnimator.start();
        }

        @Override
        void draw(Canvas canvas) {
            drawSmallCircle(canvas);
        }

        @Override
        void cancel() {
            mValueAnimator.cancel();
        }

        @Override
        void pause() {
            mValueAnimator.pause();
        }

        @Override
        void resume() {
            mValueAnimator.resume();
        }
    }

    //扩散动画 本质是利用mPaint绘制空心圆 ValueAnimator改变画笔的粗细和半径 然后在onDraw方法不停绘制
    class ExtendAnimationStatus extends AnimateStatus {
        private final ValueAnimator mAnimator;

        public ExtendAnimationStatus() {
            mAnimator = ValueAnimator.ofFloat(0, (float) Math.sqrt(mCenterX * mCenterX + mCenterY * mCenterY));//透明圆的半径从0到View对角线的一半
            mAnimator.setDuration(ANIMATION_DURATION);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            mAnimator.addUpdateListener(animation -> {
                mHoleRadius = (float) animation.getAnimatedValue(); // 0 - 对角线的一半
                invalidate();
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    //最后的动画执行完毕 释放资源
                    mCurrentAnimateStatus = null;
                    RotateLoadingView.this.setVisibility(View.GONE);
                    Log.e("TAG", "ExtendAnimationStatus: ");
                }
            });
            mAnimator.start();
        }

        @Override
        void draw(Canvas canvas) {
            // 画笔的宽度
            float strokeWidth = mDiagonalDist - mHoleRadius;
            // 直觉上会使用mHoleRadius作为半径 但是mHoleRadius从0变到mDiagonalDist
            // 相反的strokeWidth从mDiagonalDist变到0
            // 因此当使用mHoleRadius作为半径时 一开始的画笔很粗 即使我们空心圆的半径为0 也会画成实心圆 因为画笔很粗
            // 真实的半径（绘制的半径）=透明的半径+strokeWidth/2 重点！！！
            // 当strokeWidth/2>代码设置的绘制的半径时 我们看不到空心的部分
            float radius = strokeWidth / 2 + mHoleRadius;
            mPaint.setStrokeWidth(strokeWidth);
            canvas.drawCircle(mCenterX, mCenterY, radius, mPaint);
        }

        @Override
        void cancel() {
            mAnimator.cancel();
        }

        @Override
        void pause() {
            mAnimator.pause();
        }

        @Override
        void resume() {
            mAnimator.resume();
        }
    }

    public AnimateStatus getCurrentAnimateStatus() {
        return mCurrentAnimateStatus;
    }

    abstract class AnimateStatus {
        abstract void draw(Canvas canvas);

        abstract void cancel();

        abstract void pause();

        abstract void resume();
    }
}
