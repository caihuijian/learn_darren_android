package com.example.circleloadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by Cai Huijian on 2021/1/9.
 */
class CircleLoadingView extends RelativeLayout {
    private static final String TAG = "CircleLoadingView";
    private SingleCircle mCircleLeft, mCircleCenter, mCircleRight;
    private float mAnimateDistance;
    private int mAnimateDuration = 500;
    AnimatorSet mAnimatorSet4Out;
    AnimatorSet mAnimatorSet4In;


    public CircleLoadingView(Context context) {
        this(context, null);
    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAnimateDistance = Util.dip2px(80, context);
        inflate(context, R.layout.layout_circle_loading_view, this);
        mCircleLeft = findViewById(R.id.circle_left);
        mCircleCenter = findViewById(R.id.circle_center);
        mCircleRight = findViewById(R.id.circle_right);
        mCircleLeft.changeColor(Color.RED);
        mCircleCenter.changeColor(Color.GREEN);
        mCircleRight.changeColor(Color.BLUE);
        post(this::startOutAnimate);
    }


    //往外跑
    private void startOutAnimate() {
        if (mAnimatorSet4Out == null) {
            ObjectAnimator translationLeftOut = ObjectAnimator.ofFloat(mCircleLeft, "translationX", 0, -mAnimateDistance);
            ObjectAnimator translationRightOut = ObjectAnimator.ofFloat(mCircleRight, "translationX", 0, mAnimateDistance);
            mAnimatorSet4Out = new AnimatorSet();
            mAnimatorSet4Out.setInterpolator(new DecelerateInterpolator(2f));//2f表示比原来的动画更明显 减速动画
            mAnimatorSet4Out.playTogether(translationLeftOut, translationRightOut);
            mAnimatorSet4Out.setDuration(mAnimateDuration);
            mAnimatorSet4Out.start();
            mAnimatorSet4Out.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    startInAnimate();//往里跑
                }
            });
        }
        mAnimatorSet4Out.start();
    }

    //往里跑
    private void startInAnimate() {
        if (mAnimatorSet4In == null) {
            ObjectAnimator translationLeftIn = ObjectAnimator.ofFloat(mCircleLeft, "translationX", -mAnimateDistance, 0);
            ObjectAnimator translationRightIn = ObjectAnimator.ofFloat(mCircleRight, "translationX", mAnimateDistance, 0);
            mAnimatorSet4In = new AnimatorSet();
            mAnimatorSet4In.setInterpolator(new AccelerateInterpolator(2f));//2f表示比原来的动画更明显 加速动画
            mAnimatorSet4In.playTogether(translationLeftIn, translationRightIn);
            mAnimatorSet4In.setDuration(mAnimateDuration);
            mAnimatorSet4In.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    exchangeColor();//交换颜色
                    startOutAnimate();//往外跑
                }
            });
        }
        mAnimatorSet4In.start();
    }

    //点集中到中间 交换颜色
    private void exchangeColor() {
        int leftColor = mCircleLeft.getColor();
        int centerColor = mCircleCenter.getColor();
        int rightColor = mCircleRight.getColor();
        mCircleCenter.changeColor(leftColor);
        mCircleRight.changeColor(centerColor);
        mCircleLeft.changeColor(rightColor);
    }

    //释放资源
    public void loadingComplete() {
        mAnimatorSet4Out.cancel();
        mAnimatorSet4In.cancel();
        removeAllViews();
    }
}
