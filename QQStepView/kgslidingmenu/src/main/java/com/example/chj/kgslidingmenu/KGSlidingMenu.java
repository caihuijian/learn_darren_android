package com.example.chj.kgslidingmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.example.chj.util.ScreenUtil;

public class KGSlidingMenu extends HorizontalScrollView {//普通ScrollView是上下滑动 因此继承HorizontalScrollView
    private int mMenuRightGap;
    private int mMenuWidth;//菜单宽度=屏幕宽度-空白gap的宽度
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;
    private int mScreenHeight;

    public KGSlidingMenu(Context context) {
        this(context, null);
    }

    public KGSlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KGSlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDataFromAttr(attrs, context);
    }

    private void initDataFromAttr(AttributeSet attrs, Context context) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.KGSlidingMenu);
        mMenuRightGap = (int) array.getDimension(R.styleable.KGSlidingMenu_menuGap, 0);
        mScreenWidth = ScreenUtil.getScreenWidth(context);
        mScreenHeight = ScreenUtil.getScreenHeight(context);
        mMenuWidth = mScreenWidth - mMenuRightGap;
        array.recycle();
    }

    /**
     * 除非在layout_main_content和layout_menu中指定固定宽高 否则使用两个布局的界面宽度无法超过屏幕大小
     * 指定宽高也不是不可以 但是适配会比较麻烦 这里采用测量的方式指定宽高
     * <p>
     * 菜单宽度=屏幕宽度-菜单右侧屏幕宽度
     * 主体内容宽度=屏幕宽度
     */
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        //获取KGSlidingMenu的第零个子view 也就是LinearLayout
//        //虽然用findViewById()更快 但是这样更能清楚的知道各个view直接的层级关系 便于学习
//        ViewGroup container = (ViewGroup) getChildAt(0);
//        if (container.getChildCount() != 2) {
//            throw new RuntimeException("KGSlidingMenu子节点的子view必须是2个！！");
//        }
//        //获得menu节点并指宽度
//        mMenu = (ViewGroup) container.getChildAt(0);
//        ViewGroup.LayoutParams tempLayoutParams = mMenu.getLayoutParams();
//        tempLayoutParams.width = mMenuWidth;
//        mMenu.setLayoutParams(tempLayoutParams);
//
//        //获得content节点并指定宽度
//        mContent = (ViewGroup) container.getChildAt(1);
//        tempLayoutParams = mContent.getLayoutParams();
//        tempLayoutParams.width = mMenuWidth + mMenuRightGap;// mMenuWidth + mMenuRightGap 就是屏幕的宽度
//        mContent.setLayoutParams(tempLayoutParams);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(mMenuWidth + mMenuRightGap + mMenuWidth, height);
//    }
    @Override
    protected void onFinishInflate() {//TODO 什么时候调用 为什么在onMeasure测量无效？？？
        super.onFinishInflate();
        //获取KGSlidingMenu的第零个子view 也就是LinearLayout
        //虽然用findViewById()更快 但是这样更能清楚的知道各个view直接的层级关系 便于学习
        ViewGroup container = (ViewGroup) getChildAt(0);
        if (container.getChildCount() != 2) {
            throw new RuntimeException("KGSlidingMenu子节点的子view必须是2个！！");
        }
        //获得menu节点并指宽度
        mMenu = (ViewGroup) container.getChildAt(0);
        ViewGroup.LayoutParams tempLayoutParams = mMenu.getLayoutParams();
        tempLayoutParams.width = mMenuWidth;
        mMenu.setLayoutParams(tempLayoutParams);

        //获得content节点并指定宽度
        mContent = (ViewGroup) container.getChildAt(1);
        tempLayoutParams = mContent.getLayoutParams();
        tempLayoutParams.width = mMenuWidth + mMenuRightGap;// mMenuWidth + mMenuRightGap 就是屏幕的宽度
        mContent.setLayoutParams(tempLayoutParams);
        //setMeasuredDimension(mScreenWidth*2-mMenuRightGap, mScreenHeight);
    }
}
