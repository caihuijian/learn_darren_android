package com.example.customfilterview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by Cai Huijian on 2021/1/5.
 */
class CustomFilterView extends RelativeLayout implements View.OnClickListener {

    private LinearLayout mContainerTab;
    private RelativeLayout mContainerContent;
    private View mShadowView;
    private BaseFilterViewAdapter mFilterViewAdapter;
    private int mCurrentTabIndex = 0;//当前所处Tab的位置


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

    public void setFilterViewAdapter(BaseFilterViewAdapter filterViewAdapter) {
        this.mFilterViewAdapter = filterViewAdapter;
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
            //点击了某一个tab页
            if (mContainerTab.getChildAt(i) == v) {
                //点击了当前显示的tab页 关闭主体内容 切换tab页颜色
                if (mContainerTab.getChildAt(mCurrentTabIndex) == v) {
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setTextColor(Color.BLACK);
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setBackgroundColor(Color.WHITE);
                    mContainerContent.removeAllViews();
                } else {//点击了非当前显示的tab页  tab页text的颜色需要变化（注意新旧的当前页面都要变化） 内容需要变化
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setTextColor(Color.BLACK);
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setBackgroundColor(Color.WHITE);
                    mCurrentTabIndex = i;
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setTextColor(Color.RED);
                    ((TextView) mContainerTab.getChildAt(mCurrentTabIndex)).setBackgroundColor(Color.GRAY);
                    mContainerContent.removeAllViews();
                    mContainerContent.addView(mFilterViewAdapter.getContentView(i));
                }
            }
        }
    }
}
