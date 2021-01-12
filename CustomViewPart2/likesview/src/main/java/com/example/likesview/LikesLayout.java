package com.example.likesview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by Cai Huijian on 2021/1/12.
 */
class LikesLayout extends RelativeLayout {
    private static final String TAG = "LikesLayout";
    // 用于产生随机数
    private Random mRandom;
    // 存储图片资源id
    private int[] mImageResId;

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
        mImageResId = new int[]{R.drawable.star_blue, R.drawable.star_green, R.drawable.star_red};
    }

    // 1.在底部中心位置初始化图片 用上透明度变化和scale变化的动画
    public void addLike() {
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

        // 2.计算二阶贝塞尔曲线 本节难点
        return innerAnimator;
    }
}
