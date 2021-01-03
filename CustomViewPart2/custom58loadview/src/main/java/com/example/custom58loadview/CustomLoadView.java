package com.example.custom58loadview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by hjcai on 2021/1/3.
 */
public class CustomLoadView extends RelativeLayout {
    private static final long ANIMATOR_DURATION = 1000;
    private ShapeView mShapeView;
    private ImageView mShadowView;
    private int mShapeTransactionY;//他的值为ShapeView的margin bottom


    public CustomLoadView(Context context) {
        this(context, null);
    }

    public CustomLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.load_view, this);
        //上面的这句等价于下面的两句
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        layoutInflater.inflate(R.layout.load_view,this,true);

        //初始化各个部件 用于后期动画
        mShapeView = findViewById(R.id.shape_view);
        mShadowView = findViewById(R.id.image_shadow);
        mShapeTransactionY = ((MarginLayoutParams) mShapeView.getLayoutParams()).bottomMargin;

        post(new Runnable() {
            @Override
            public void run() {
                // onResume 之后View绘制流程执行完毕之后（View的绘制流程源码分析那一章）
                startFullDownAnimate();
            }
        });
        // onCreate() 方法中执行 ，布局文件解析 反射创建实例
    }

    //和下落动画相反
    private void startJumpUpAnimate() {
        //关于形状的动画
        // 1.下落动画+上抛动画
        // 这里是弹起// Y值越大 越在下方
        ObjectAnimator shapeTransactionAnimate = ObjectAnimator.ofFloat(mShapeView, "translationY", mShapeTransactionY, 0);

        // 2.差值器，动画速率的问题，下落的时候如果是小球在真实生活中，下落的速度应该是越来越快，上抛的速度应该是越来越慢
        // 这里是下落
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

        // 3.下落的时候改变形状
        // mShapeView.exchange();

        // 4.旋转动画，正方形 180 圆形 三角120
        float rotationValue = -180;
        switch (mShapeView.getCurrentShape()) {
            case ShapeView.ShapeType.SQUARE:
                rotationValue = -180;
                break;
            case ShapeView.ShapeType.TRIANGLE:
                rotationValue = -120;
                break;
        }
        ObjectAnimator shapeRotateAnimate = ObjectAnimator.ofFloat(mShapeView, "rotation", rotationValue);
        ObjectAnimator shadowScaleAnimate = ObjectAnimator.ofFloat(mShadowView, "scaleX", 0.3f, 1);//从0.3增加到原有宽度


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(ANIMATOR_DURATION);
        animatorSet.setInterpolator(decelerateInterpolator);
        animatorSet.playTogether(shapeTransactionAnimate, shapeRotateAnimate, shadowScaleAnimate);
        animatorSet.start();

        //当动画下落完之后就下落了
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 下落完之后就上抛了
                startFullDownAnimate();
            }
        });
    }

    private void startFullDownAnimate() {
        // 关于阴影的动画 下落位移的时候配合中间阴影缩小，上抛的时候配合中间阴影放大
        //mShadowView.setScaleX(); //里面的参数的定义可以拿出来直接作为ObjectAnimator.ofFloat的第二个参数 表示动画的类型

        //关于形状的动画
        // 1.下落动画+上抛动画
        // 这里是从高处落下 // Y值越大 越在下方
        ObjectAnimator shapeTransactionAnimate = ObjectAnimator.ofFloat(mShapeView, "translationY", 0, mShapeTransactionY);

        // 2.差值器，动画速率的问题，下落的时候如果是小球在真实生活中，下落的速度应该是越来越快，上抛的速度应该是越来越慢
        // 这里是下落
        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();

        // 3.下落的时候改变形状
        mShapeView.exchange();

        // 4.旋转动画，正方形 180 圆形 三角120
        float rotationValue = 0;
        switch (mShapeView.getCurrentShape()) {
            case ShapeView.ShapeType.SQUARE:
                rotationValue = 180;
                break;
            case ShapeView.ShapeType.TRIANGLE:
                rotationValue = 120;
                break;
        }
        ObjectAnimator shapeRotateAnimate = ObjectAnimator.ofFloat(mShapeView, "rotation", rotationValue);
        ObjectAnimator shadowScaleAnimate = ObjectAnimator.ofFloat(mShadowView, "scaleX", 1, 0.3f);//从原有宽度缩短到0.3


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(ANIMATOR_DURATION);
        animatorSet.setInterpolator(accelerateInterpolator);
        animatorSet.playTogether(shapeTransactionAnimate, shapeRotateAnimate, shadowScaleAnimate);
        // 先执行 translationAnimator 接着执行 scaleAnimator
        // animatorSet.playSequentially(translationAnimator,scaleAnimator);
        animatorSet.start();

        //当动画下落完之后就上抛了
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 下落完之后就上抛了
                startJumpUpAnimate();
            }
        });
    }

    //加载完毕调用 释放动画资源
    public void loadComplete() {
        mShapeView.clearAnimation();
        mShadowView.clearAnimation();
        // 把LoadingView从父布局移除
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);// 从父布局移除
            removeAllViews();// 移除自己所有的View
        }
    }
}
