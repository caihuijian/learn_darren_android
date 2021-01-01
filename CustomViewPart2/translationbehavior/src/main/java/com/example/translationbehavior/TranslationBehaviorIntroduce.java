package com.example.translationbehavior;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Created by hjcai on 2021/1/1.
 */
public class TranslationBehaviorIntroduce extends FloatingActionButton.Behavior {

    /**
     * 表示是否给应用了Behavior 的View 指定一个依赖的布局，通常，当依赖的View 布局发生变化时
     * 不管被被依赖View 的顺序怎样，被依赖的View也会重新布局
     *
     * @param parent     FloatingActionButton的直接父容器应该是个CoordinatorLayout
     * @param child      绑定behavior 的View
     * @param dependency 依赖的view
     * @return dependency是指定的实例类型 返回true,否则返回false
     */
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton child, @NonNull View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
    }

    //FloatingActionButton的方法
    //当被依赖的View 状态（如：位置、大小）发生变化时，这个方法被调用
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }

    /**
     * 当CoordinatorLayout的子view尝试滚动的时候被调用
     * Called when a descendant of the CoordinatorLayout attempts to initiate a nested scroll.
     * <p>
     * 当返回值为true的时候表明 coordinatorLayout 充当nested scroll parent 处理这次滑动，
     * 需要注意的是只有从此方法返回true的行为才会接收后续嵌套的滚动事件
     * （如：onNestedPreScroll、onNestedScroll等）
     *
     * <p>Any Behavior associated with any direct child of the CoordinatorLayout may respond
     * to this event and return true to indicate that the CoordinatorLayout should act as
     * a nested scrolling parent for this scroll. Only Behaviors that return true from
     * this method will receive subsequent nested scroll events.</p>
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
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    /**
     * 嵌套滚动发生之前被调用
     * 在nested scroll child 消费掉自己的滚动距离之前，嵌套滚动每次被nested scroll child
     * 更新都会调用onNestedPreScroll。注意有个重要的参数consumed，可以修改这个数组表示你消费
     * 了多少距离。假设用户垂直滑动了100px,child 做了90px 的位移，你需要把 consumed［1］的值改成90，
     * 这样coordinatorLayout就能知道只处理剩下的10px的滚动。
     * <p>
     * Called when a nested scroll in progress is about to update, before the target has
     * consumed any of the scrolled distance.
     *
     * <p>Any Behavior associated with the direct child of the CoordinatorLayout may elect
     * to accept the nested scroll as part of {@link #onStartNestedScroll}. Each Behavior
     * that returned true will receive subsequent nested scroll events for that nested scroll.
     * </p>
     *
     * <p><code>onNestedPreScroll</code> is called each time the nested scroll is updated
     * by the nested scrolling child, before the nested scrolling child has consumed the scroll
     * distance itself. <em>Each Behavior responding to the nested scroll will receive the
     * same values.</em> The CoordinatorLayout will report as consumed the maximum number
     * of pixels in either direction that any Behavior responding to the nested scroll reported
     * as consumed.</p>
     *
     * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
     *                          associated with
     * @param child             the child view of the CoordinatorLayout this Behavior is associated with
     * @param target            the descendant view of the CoordinatorLayout performing the nested scroll
     * @param dx                the raw horizontal number of pixels that the user attempted to scroll
     *                          用户水平方向的滚动距离
     * @param dy                the raw vertical number of pixels that the user attempted to scroll
     *                          用户竖直方向的滚动距离
     * @param consumed          out parameter. consumed[0] should be set to the distance of dx that
     *                          was consumed, consumed[1] should be set to the distance of dy that
     *                          was consumed
     * @param type              the type of input which cause this scroll event
     * @see NestedScrollingParent2#onNestedPreScroll(View, int, int, int[], int)
     */
    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    /**
     * 进行嵌套滚动时被调用
     * Called when a nested scroll in progress has updated and the target has scrolled or
     * attempted to scroll.
     *
     * <p>Any Behavior associated with the direct child of the CoordinatorLayout may elect
     * to accept the nested scroll as part of {@link #onStartNestedScroll}. Each Behavior
     * that returned true will receive subsequent nested scroll events for that nested scroll.
     * </p>
     *
     * <p><code>onNestedScroll</code> is called each time the nested scroll is updated by the
     * nested scrolling child, with both consumed and unconsumed components of the scroll
     * supplied in pixels. <em>Each Behavior responding to the nested scroll will receive the
     * same values.</em>
     * </p>
     *
     * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
     *                          associated with
     * @param child             the child view of the CoordinatorLayout this Behavior is associated with
     * @param target            the descendant view of the CoordinatorLayout performing the nested scroll
     * @param dxConsumed        horizontal pixels consumed by the target's own scrolling operation
     * @param dyConsumed        vertical pixels consumed by the target's own scrolling operation
     * @param dxUnconsumed      horizontal pixels not consumed by the target's own scrolling
     *                          operation, but requested by the user
     * @param dyUnconsumed      vertical pixels not consumed by the target's own scrolling operation,
     *                          but requested by the user
     * @param type              the type of input which cause this scroll event
     * @see NestedScrollingParent2#onNestedScroll(View, int, int, int, int, int)
     */
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    /**
     * 嵌套滚动结束时被调用，这是一个清除滚动状态等的好时机
     * Called when a nested scroll has ended.
     *
     * <p>Any Behavior associated with any direct child of the CoordinatorLayout may elect
     * to accept the nested scroll as part of {@link #onStartNestedScroll}. Each Behavior
     * that returned true will receive subsequent nested scroll events for that nested scroll.
     * </p>
     *
     * <p><code>onStopNestedScroll</code> marks the end of a single nested scroll event
     * sequence. This is a good place to clean up any state related to the nested scroll.
     * </p>
     *
     * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
     *                          associated with
     * @param child             the child view of the CoordinatorLayout this Behavior is associated with
     * @param target            the descendant view of the CoordinatorLayout that initiated
     *                          the nested scroll
     * @param type              the type of input which cause this scroll event
     * @see NestedScrollingParent2#onStopNestedScroll(View, int)
     */
    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
    }


    /**
     * 用户松开手指并且会发生惯性动作之前调用，参数提供了速度信息，可以根据这些速度信息
     * 决定最终状态，比如滚动Header，是让Header处于展开状态还是折叠状态。返回true 表
     * 示消费了fling.
     * Called when a nested scrolling child is about to start a fling.
     *
     * <p>Any Behavior associated with the direct child of the CoordinatorLayout may elect
     * to accept the nested scroll as part of {@link #onStartNestedScroll}. Each Behavior
     * that returned true will receive subsequent nested scroll events for that nested scroll.
     * </p>
     *
     * <p><code>onNestedPreFling</code> is called when the current nested scrolling child view
     * detects the proper conditions for a fling, but it has not acted on it yet. A
     * Behavior can return true to indicate that it consumed the fling. If at least one
     * Behavior returns true, the fling should not be acted upon by the child.</p>
     *
     * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
     *                          associated with
     * @param child             the child view of the CoordinatorLayout this Behavior is associated with
     * @param target            the descendant view of the CoordinatorLayout performing the nested scroll
     * @param velocityX         horizontal velocity of the attempted fling
     * @param velocityY         vertical velocity of the attempted fling
     * @return true if the Behavior consumed the fling
     * @see NestedScrollingParent#onNestedPreFling(View, float, float)
     */
    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }


    //摆放子 View 的时候调用，可以重写这个方法对子View 进行重新布局
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
    }
}
