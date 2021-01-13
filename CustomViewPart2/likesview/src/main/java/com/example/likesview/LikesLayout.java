package com.example.likesview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import androidx.core.content.ContextCompat;

/**
 * Created by Cai Huijian on 2021/1/12.
 */
class LikesLayout extends RelativeLayout {
    private static final String TAG = "LikesLayout";

    //星星的图片宽高
    private final int mDrawableWidth;
    private final int mDrawableHeight;
    // 用于产生随机数
    private Random mRandom;
    // 存储图片资源id
    private int[] mImageResId;
    //整个layout宽高
    private int mWidth;
    private int mHeight;

    private Interpolator[] mInterpolator;

    public LikesLayout(Context context) {
        this(context, null);
    }

    public LikesLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRandom = new Random();
        // 初始化资源id
        mImageResId = new int[]{R.drawable.star_blue, R.drawable.star_green, R.drawable.star_red, R.drawable.star_blue_bright, R.drawable.star_orange_light};
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.star_blue);
        //假设所有星星的大小相等（如果不等要分别计算）
        mDrawableWidth = drawable.getIntrinsicWidth();
        mDrawableHeight = drawable.getIntrinsicHeight();
        mInterpolator = new Interpolator[]{new AccelerateDecelerateInterpolator(), new AccelerateInterpolator(),
                new DecelerateInterpolator(), new LinearInterpolator()};
    }

    // 获取控件的宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public void addLikes() {
        for (int i = 0; i < 30; i++) {
            addLike();
        }
    }

    // 1.在底部中心位置初始化图片 用上透明度变化和scale变化的动画
    private void addLike() {
        // 添加一个ImageView在底部
        final ImageView likeIv = new ImageView(getContext());
        // 给一个图片资源（随机） 这里视频有点问题 比如 mRandom.nextInt(5);取值可能是是[0,5)的整数，视频里面写道 mRandom.nextInt(mImageRes.length - 1) 其实-1是不必要的
        int imageIndex = mRandom.nextInt(mImageResId.length);
        likeIv.setImageResource(mImageResId[imageIndex]);
        // 怎么添加到底部中心？利用RelativeLayout的LayoutParams
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        params.addRule(CENTER_HORIZONTAL);
        likeIv.setLayoutParams(params);
        addView(likeIv);

        AnimatorSet animator = getAnimator(likeIv);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 4.2动画结束时将添加的View删除
                removeView(likeIv);
            }
        });
        animator.start();
    }

    private AnimatorSet getAnimator(ImageView likeIv) {
        // 入场动画 添加的效果：有放大和透明度变化 （属性动画）
        // 1.1用上透明度变化和scale变化的动画
        AnimatorSet innerAnimator = new AnimatorSet();
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(likeIv, "alpha", 0.3f, 1.0f);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(likeIv, "scaleX", 0.3f, 1.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(likeIv, "scaleY", 0.3f, 1.0f);
        innerAnimator.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator);
        innerAnimator.setDuration(350);


        // 运行的路径动画  playSequentially 按循序执行
        AnimatorSet allAnimatorSet = new AnimatorSet();
        allAnimatorSet.playSequentially(innerAnimator, getBezierAnimator(likeIv));// 2.计算二阶贝塞尔曲线 本节难点
        return allAnimatorSet;
    }

    private Animator getBezierAnimator(ImageView likeIv) {
        //计算三阶贝塞尔曲线中的起点 控制点1 控制点2 终点
        //起始点在屏幕底部中间位置
        //控制点1取下半屏幕的随机点
        //控制点2取上半屏幕的随机点
        //起始点在屏幕顶部中间位置

        //所有动画计算的基准是图片的左上角
        PointF statPoint = new PointF(mWidth / 2 - mDrawableWidth / 2, mHeight - mDrawableHeight);
        PointF controlPoint1 = new PointF(mRandom.nextInt(mWidth) - mDrawableWidth, mRandom.nextInt(mHeight / 2) + mHeight / 2);
        PointF controlPoint2 = new PointF(mRandom.nextInt(mWidth) - mDrawableWidth, mRandom.nextInt(mHeight / 2));
        PointF endPoint = new PointF(mRandom.nextInt(mWidth - mDrawableWidth), 0);
        BezierTypeEvaluator evaluator = new BezierTypeEvaluator(controlPoint1, controlPoint2);
        ValueAnimator bezierAnimator = ObjectAnimator.ofObject(evaluator, statPoint, endPoint);
        // 加一些随机的差值器
        bezierAnimator.setInterpolator(mInterpolator[mRandom.nextInt(mInterpolator.length)]);
        bezierAnimator.addUpdateListener(animation -> {
            PointF pointF = (PointF) animation.getAnimatedValue();
            likeIv.setX(pointF.x);
            likeIv.setY(pointF.y);
            // 透明度变化
            float t = animation.getAnimatedFraction();
            likeIv.setAlpha(1 - t);//最小透明度0.2f
        });
        bezierAnimator.setDuration(3000);
        return bezierAnimator;
    }
}
