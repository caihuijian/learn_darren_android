package com.example.chj.taglayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TagLayout extends ViewGroup {
    private static final String TAG = "TagLayout";
    List<List> childViewsInLines = new ArrayList<>();
    List<View> oneLineViews = new ArrayList<>();

    public TagLayout(Context context) {
        this(context, null);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        childViewsInLines.clear();
        oneLineViews.clear();
        //根据源码 先测量一遍所有子view 以获取子view的宽高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        getLayoutParams();
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = 0;

        int totalChildNum = getChildCount();
        int currentLineWidth = 0;
        int currentChildHeight;//包括child margin
        int currentChildWidth;//包括child margin
        for (int i = 0; i < totalChildNum; i++) {
            View currentChild = getChildAt(i);
            if(currentChild.getVisibility() == GONE){
                continue;
            }
            currentChildWidth = getChildWidthIncludeMargin(currentChild);
            currentChildHeight = getChildHeightIncludeMargin(currentChild);
            //计算是否需要换行
            if (currentLineWidth + currentChildWidth > parentWidth) {
                //需要换行 //TODO 考虑高度不一样
                currentLineWidth = 0;//重置当前行宽度的累计值
                parentHeight += currentChildHeight;//高度累加 //TODO 暂且使用最后一个view的高度作为此行高度
                childViewsInLines.add(oneLineViews);//记录一行的view
                oneLineViews = new ArrayList<>();//为下一行view记录做准备
                oneLineViews.add(currentChild);//不要忘了当前这个view
            } else {
                //此行记录长度增加
                currentLineWidth += currentChildWidth;//当前行宽度的累计值增加
                oneLineViews.add(currentChild);//当前行view增加
            }
            //最后一个view即使宽度没有达到换行 仍然需要累计高度 作为新的一行
            if (i == getChildCount() - 1) {
                parentHeight += currentChildHeight;//高度累加
                childViewsInLines.add(oneLineViews);//记录一行的view
            }
        }
        //Log.d(TAG, "onMeasure: parentWidth" + parentWidth + " parentHeight " + parentHeight);
        setMeasuredDimension(parentWidth, parentHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineStartX=0;
        int lineStartY=0;
        int currentChildWidth;
        int currentChildHeight=0;
        for (List lineViews : childViewsInLines){

            for (Object view : lineViews){
                View currentChild = (View) view;
                if(currentChild.getVisibility() == GONE){
                    continue;
                }
                currentChildWidth = getChildWidthIncludeMargin(currentChild);
                currentChildHeight = getChildHeightIncludeMargin(currentChild);
                currentChild.layout(lineStartX,lineStartY,lineStartX+currentChildWidth,lineStartY+currentChildHeight);
                Log.d(TAG, "onLayout: "+"lineStartX->"+lineStartX+"lineStartY->"+lineStartY+"currentChildWidth->"+currentChildWidth+"+currentChildHeight->"+currentChildHeight);
                lineStartX +=currentChildWidth;
            }
            lineStartX = 0;//换行 起始绘制点x重置
            lineStartY+=currentChildHeight;////换行 高度累加
        }
    }

    private int getChildHeightIncludeMargin(View currentChild) {
        MarginLayoutParams currentChildLayout = null;
        if (currentChild.getLayoutParams() instanceof MarginLayoutParams) {
            currentChildLayout = (MarginLayoutParams) currentChild.getLayoutParams();
        }
        return currentChild.getMeasuredHeight() + (currentChildLayout == null ? 0 : (currentChildLayout.topMargin + currentChildLayout.bottomMargin));
    }

    private int getChildWidthIncludeMargin(View currentChild) {
        MarginLayoutParams currentChildLayout = null;
        if (currentChild.getLayoutParams() instanceof MarginLayoutParams) {
            currentChildLayout = (MarginLayoutParams) currentChild.getLayoutParams();
        }
        return currentChild.getMeasuredWidth() + (currentChildLayout == null ? 0 : (currentChildLayout.leftMargin + currentChildLayout.rightMargin));
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {//会影响子view getLayoutParams是否可以转型为MarginLayoutParams
        return new MarginLayoutParams(getContext(),attrs);
    }
}
