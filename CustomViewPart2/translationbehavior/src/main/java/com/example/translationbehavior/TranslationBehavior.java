package com.example.translationbehavior;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Created by hjcai on 2021/1/1.
 */
public class TranslationBehavior extends CoordinatorLayout.Behavior {
    /**
     * 报错
     * Caused by: android.view.InflateException: Binary XML file line #38: Could not inflate Behavior subclass com.example.translationbehavior.TranslationBehavior
     * Caused by: java.lang.RuntimeException: Could not inflate Behavior subclass com.example.translationbehavior.TranslationBehavior
     * at androidx.coordinatorlayout.widget.CoordinatorLayout.parseBehavior(CoordinatorLayout.java:622)
     * at androidx.coordinatorlayout.widget.CoordinatorLayout$LayoutParams.<init>(CoordinatorLayout.java:2805)
     * 在parseBehavior中断点 发现执行clazz.getConstructor(CONSTRUCTOR_PARAMS);时报错
     * 跟踪CONSTRUCTOR_PARAMS
     * static final Class<?>[] CONSTRUCTOR_PARAMS = new Class<?>[] {
     * Context.class,
     * AttributeSet.class
     * };
     * 发现我们需要构造函数 且必须是public的
     */
    public TranslationBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * 当CoordinatorLayout的子view尝试滚动的时候被调用
     * <p>
     * 当返回值为true的时候表明 coordinatorLayout 充当nested scroll parent 处理这次滑动，
     * 需要注意的是只有从此方法返回true的行为才会接收后续嵌套的滚动事件
     * （如：onNestedPreScroll、onNestedScroll等）
     *
     * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
     *                          associated with
     * @param child             the child view of the CoordinatorLayout this Behavior is associated with
     * @param directTargetChild the child view of the CoordinatorLayout that either is or
     *                          contains the target of the nested scroll operation
     * @param target            the descendant view of the CoordinatorLayout initiating the nested scroll
     *                          <p>
     *                          axes是非常重要的参数 可以判断横向竖向滑动
     * @param axes              the axes that this nested scroll applies to. See
     *                          {@link ViewCompat#SCROLL_AXIS_HORIZONTAL},
     *                          {@link ViewCompat#SCROLL_AXIS_VERTICAL}
     * @param type              the type of input which cause this scroll event
     * @return true if the Behavior wishes to accept this nested scroll
     * @see NestedScrollingParent2#onStartNestedScroll(View, View, int, int)
     */

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
        //只在竖向scroll发生的时候接受后续滑动事件
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    //滑动发生时调用
    private boolean isAnimating = false;

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        Log.e("TAG", "dyConsumed -> " + dyConsumed + " dyUnconsumed -> " + dyUnconsumed);
        child.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        if (!isAnimating) {
            floatingButtonAnimate(dyConsumed, child);
        }

    }

    private void floatingButtonAnimate(int dyConsumed, View child) {
        if (dyConsumed > 0) {
            //如果是向上滑动 隐藏ActionBar
            int translationY = ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).bottomMargin + child.getMeasuredHeight();
            child.animate().translationY(translationY).setDuration(500).start();
        } else if (dyConsumed < 0) {
            // 如果是向下滑动 显示出ActionBar
            child.animate().translationY(0).setDuration(500).start();
        }
    }
}
