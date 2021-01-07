package com.example.customfilterview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by Cai Huijian on 2021/1/5.
 */
class CustomFilterView extends RelativeLayout implements View.OnClickListener {

    private static final long ANIMATION_DURATION = 300;
    private static final String TAG = "CustomFilterView";
    private LinearLayout mContainerTab;
    private RelativeLayout mContainerContent;
    private View mShadowView;
    private BaseFilterViewAdapter mFilterViewAdapter;
    private int mCurrentTabIndex = 0;//当前所处Tab的位置
    private boolean isAnimating = false;
    private FilterViewAdapterContentClickObserver mObserver;


    public CustomFilterView(Context context) {
        this(context, null);
    }

    public CustomFilterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //还是感觉使用xml创建的方式更加清晰 使用代码添加布局适合用在添加一两个View的情况 一旦View的个数增加 逻辑也变得复杂，层次不够清晰 容易出bug
        inflate(getContext(), R.layout.layout_filter, this);
        mContainerTab = findViewById(R.id.container_tab);
        mContainerContent = findViewById(R.id.container_content);
        mShadowView = findViewById(R.id.view_shadow);
    }

    //CustomFilterView相当于ListView
    public void setFilterViewAdapter(BaseFilterViewAdapter filterViewAdapter) {
        //参考ListView的setAdapter
        if (mFilterViewAdapter != null && mObserver != null) {
            mFilterViewAdapter.unregisterObserver(mObserver);
        }
        this.mFilterViewAdapter = filterViewAdapter;
        mObserver = new FilterViewAdapterContentClickObserver();
        //Adapter是具体的观察者（调用registerObserver的对象是观察者）
        mFilterViewAdapter.registerObserver(mObserver);


        //获取当前有几个tab页
        int count = mFilterViewAdapter.getCount();
        for (int i = 0; i < count; i++) {
            //显示tabView部分
            TextView tabView = (TextView) mFilterViewAdapter.getTabView(i);
            LinearLayout.LayoutParams tabViewLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            tabViewLayoutParams.weight = 1;
            tabView.setLayoutParams(tabViewLayoutParams);
            tabView.setTextColor(Color.BLACK);
            mContainerTab.addView(tabView);
            //给tabView的每个item增加点击动作
            tabView.setOnClickListener(this);

            //显示content部分 只显示第一页
            if (i == 0) {
                View contentView = mFilterViewAdapter.getContentView(i);
                mContainerContent.addView(contentView);
            }
        }
        //给第零页设置为选中状态
        ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setTextColor(Color.RED);
        ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setBackgroundColor(Color.GRAY);
        mShadowView.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置阴影区域为整个view的1/5
        LayoutParams mShadowViewLayoutParams = (LayoutParams) mShadowView.getLayoutParams();
        mShadowViewLayoutParams.height = MeasureSpec.getSize(heightMeasureSpec) / 5;
        mShadowView.setLayoutParams(mShadowViewLayoutParams);
    }

    @Override
    public void onClick(View v) {
        //轮巡tab页 看看是不是点击了tab页
        int tabCount = mFilterViewAdapter.getCount();
        for (int i = 0; i < tabCount; i++) {
            Log.d(TAG, "onClick: " + isAnimating);
            //点击了某一个tab页
            if (mContainerTab.getChildAt(i) == v) {
                //点击了当前显示的tab页 关闭主体内容 切换tab页颜色
                if (mContainerTab.getChildAt(mCurrentTabIndex) == v) {
                    closeContent();
                } else if (mCurrentTabIndex == -1) {//当前没有显示的tab页
                    openContent(i);
                } else {//点击了非当前显示的tab页  tab页text的颜色需要变化（注意新旧的当前页面都要变化） 内容需要变化
                    //旧tab状态更新
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setTextColor(Color.BLACK);
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setBackgroundColor(Color.WHITE);
                    mCurrentTabIndex = i;
                    //新tab页状态更新
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setTextColor(Color.RED);
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setBackgroundColor(Color.GRAY);
                    mContainerContent.removeAllViews();
                    mContainerContent.addView(mFilterViewAdapter.getContentView(i));
                }
            }
        }

        //如果点击了shadowView 同样关闭主体部分
        if (v == mShadowView) {
            closeContent();
        }
    }

    private void closeContent() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setTextColor(Color.BLACK);
        ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setBackgroundColor(Color.WHITE);
        mContainerContent.removeAllViews();
        mCurrentTabIndex = -1;
        //添加旧页面关闭动画
        ObjectAnimator transactionAnimateInY = ObjectAnimator.ofFloat(mContainerContent, "translationY", 0, -mContainerContent.getMeasuredHeight());
        ObjectAnimator alphaAnimate = ObjectAnimator.ofFloat(mShadowView, "alpha", 1f, 0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(ANIMATION_DURATION);
        //顺序播放动画
        animatorSet.playTogether(transactionAnimateInY, alphaAnimate);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mContainerContent.removeAllViews();
                mShadowView.setVisibility(GONE);
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void openContent(int tabIndex) {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        //新tab页状态更新
        mCurrentTabIndex = tabIndex;
        ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setTextColor(Color.RED);
        ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setBackgroundColor(Color.GRAY);
        mContainerContent.addView(mFilterViewAdapter.getContentView(tabIndex));
        //添加新页面打开动画
        //设置主体位置的位移动画和阴影透明度动画
        mShadowView.setVisibility(VISIBLE);
        ObjectAnimator transactionAnimateInY = ObjectAnimator.ofFloat(mContainerContent, "translationY", -mContainerContent.getMeasuredHeight(), 0);
        ObjectAnimator alphaAnimate = ObjectAnimator.ofFloat(mShadowView, "alpha", 0, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(ANIMATION_DURATION);
        //顺序播放动画
        animatorSet.playTogether(transactionAnimateInY, alphaAnimate);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    //角色定位 抽象观察者的实现者 类比ListView中的DataSetObservable
    class FilterViewAdapterContentClickObserver extends ContentClickObserver {

        @Override
        void contentItemClick(View view) {//因为demo的content是一个textView 这里的view没有实际意义
            closeContent();
        }
    }
}
