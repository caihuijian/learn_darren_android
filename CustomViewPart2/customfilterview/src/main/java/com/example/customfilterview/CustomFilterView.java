package com.example.customfilterview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

/**
 * Created by Cai Huijian on 2021/1/5.
 */
class CustomFilterView extends RelativeLayout {

    private LinearLayout mContainerTab;
    private RelativeLayout mContainerContent;
    private View mShadowView;
    private BaseFilterViewAdapter mFilterViewAdapter;


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
            View tabView = mFilterViewAdapter.getTabView(i);
            LinearLayout.LayoutParams tabViewLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            tabViewLayoutParams.weight = 1;
            tabView.setLayoutParams(tabViewLayoutParams);
            mContainerTab.addView(tabView);

            //显示content部分 只显示第一页
            if (i == 0) {
                View contentView = mFilterViewAdapter.getContentView(i);
                LayoutParams contentViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                contentViewLayoutParams.addRule(CENTER_IN_PARENT);
                contentView.setLayoutParams(contentViewLayoutParams);
                mContainerContent.addView(contentView);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置阴影区域为整个view的1/5
        LayoutParams mShadowViewLayoutParams = (LayoutParams) mShadowView.getLayoutParams();
        mShadowViewLayoutParams.height = MeasureSpec.getSize(heightMeasureSpec)/5;
        mShadowView.setLayoutParams(mShadowViewLayoutParams);
    }
}
