package com.example.circleloadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

/**
 * Created by Cai Huijian on 2021/1/9.
 */
class CircleLoadingView extends RelativeLayout {
    private static final String TAG = "CircleLoadingView";
    private SingleCircle mCircleLeft, mCircleCenter, mCircleRight;
    private float mAnimateDistance;
    private int mAnimateDuration = 500;


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


    private void startOutAnimate() {
        Log.d(TAG, "startOutAnimate: ");
        ObjectAnimator translationLeftOut = ObjectAnimator.ofFloat(mCircleLeft, "translationX", 0, -mAnimateDistance);
        ObjectAnimator translationRightOut = ObjectAnimator.ofFloat(mCircleRight, "translationX", 0, mAnimateDistance);
        AnimatorSet animatorSet4Out = new AnimatorSet();
        animatorSet4Out.playTogether(translationLeftOut, translationRightOut);
        animatorSet4Out.setDuration(mAnimateDuration);
        animatorSet4Out.start();
        animatorSet4Out.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                Log.d(TAG, "startOutAnimate onAnimationEnd: ");
                startInAnimate();
            }
        });

    }

    private void startInAnimate() {
        Log.d(TAG, "startInAnimate: ");
        ObjectAnimator translationLeftIn = ObjectAnimator.ofFloat(mCircleLeft, "translationX", -mAnimateDistance, 0);
        ObjectAnimator translationRightIn = ObjectAnimator.ofFloat(mCircleRight, "translationX", mAnimateDistance, 0);
        AnimatorSet animatorSet4In = new AnimatorSet();
        animatorSet4In.resume();
        animatorSet4In.playTogether(translationLeftIn, translationRightIn);
        animatorSet4In.setDuration(mAnimateDuration);
        animatorSet4In.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                Log.d(TAG, "startInAnimate onAnimationEnd: ");
                exchangeColor();
                startOutAnimate();
            }
        });
        animatorSet4In.start();
    }

    private void exchangeColor() {
        int leftColor = mCircleLeft.getColor();
        int centerColor = mCircleCenter.getColor();
        int rightColor = mCircleRight.getColor();
        mCircleCenter.changeColor(leftColor);
        mCircleRight.changeColor(centerColor);
        mCircleLeft.changeColor(rightColor);
    }

    public void loadingComplete() {
//        mCircleLeft.
        removeAllViews();
    }
}
