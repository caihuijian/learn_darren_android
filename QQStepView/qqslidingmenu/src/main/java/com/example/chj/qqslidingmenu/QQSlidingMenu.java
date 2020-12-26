package com.example.chj.qqslidingmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.example.chj.util.ScreenUtil;

public class QQSlidingMenu extends HorizontalScrollView {//普通ScrollView是上下滑动 因此继承HorizontalScrollView
    private int mMenuRightGap;
    private int mMenuWidth;//菜单宽度=屏幕宽度-空白gap的宽度
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private View mShadowView;
    private boolean isMenuOpen = false;
    private boolean isIntercept = false;
    //手势处理类 使用这个类需要
    //1.一个监听者
    //2.接受触摸事件（onTouchEvent）
    private GestureDetector mGestureDetector;
    private int mScreenWidth;

    public QQSlidingMenu(Context context) {
        this(context, null);
    }

    public QQSlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQSlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDataFromAttr(attrs, context);
        mGestureDetector = new GestureDetector(context, mGestureListener);
    }

    private void initDataFromAttr(AttributeSet attrs, Context context) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.QQSlidingMenu);
        mMenuRightGap = (int) array.getDimension(R.styleable.QQSlidingMenu_menuGap, 0);
        mScreenWidth = ScreenUtil.getScreenWidth(context);
        mMenuWidth = mScreenWidth - mMenuRightGap;
        array.recycle();
    }

    /**
     * 除非在layout_main_content和layout_menu中指定固定宽高 否则使用两个布局的界面宽度无法超过屏幕大小
     * 在xml指定宽高也不是不可以 但是适配会比较麻烦 这里采用这种方式指定宽高
     * <p>
     * 菜单宽度=屏幕宽度-菜单右侧屏幕宽度
     * 主体内容宽度=屏幕宽度
     * onFinishInflate 调用时机 setContentView中在rInflate中解析xml完毕之后会addView addView之后调用该方法
     */
    /**
     * TODO 思考 这段话的功能类似测量 为什么不写在onMeasure?
     * 猜测：已知问题：如果写在onMeasure 左右滑动有问题
     * 因为继承自HorizontalScrollView HorizontalScrollView已经实现了HorizontalScrollView的onMeasure方法
     * 思考自己为什么要自定义View 比如本文 是为了处理侧滑 并同时放两个layout
     * 个人觉得 自定义view如果不是继承View和ViewGroup 不要轻易覆盖onMeasure onLayout onDraw方法,除非你对继承的View的这些代码十分了解
     * 否则很容易出问题（比如本文 继承了HorizontalScrollView 覆盖onMeasure方法就会遇到左右滑动有问题 猜测就是HorizontalScrollView的onMeasure方法做了特殊处理）
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取KGSlidingMenu的第零个子view 也就是LinearLayout
        //虽然用findViewById()更快 但是这样更能清楚的知道各个view直接的层级关系 便于学习
        ViewGroup container = (ViewGroup) getChildAt(0);
        if (container.getChildCount() != 2) {
            throw new RuntimeException("KGSlidingMenu子节点的子view必须是2个！！");
        }
        mShadowView = container.findViewById(R.id.shadowView);
        mShadowView.setAlpha(0f);
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
        mContent.setOnClickListener(new OnClickListener() {//测试屏蔽点击事件
            @Override
            public void onClick(View v) {
                Toast.makeText(mContent.getContext(), "主体被点击", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //处理事件拦截 菜单打开时 右侧空白部分不能点击
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        isIntercept = false;
        if (isMenuOpen && ev.getX() > mMenuWidth) {//注意 此处只是屏蔽了子view（主体mContent）的事件 但是整个容器的事件还会继续处理
            //因此 closeMenu和onTouchEvent中抬起手时判断位置 打开菜单的逻辑冲突 因此需要标志位isIntercept
            closeMenu();
            isIntercept = true;
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isIntercept) {//菜单以外区域事件被屏蔽 不处理任何事件
            isIntercept = false;
            return true;
        }

        if (mGestureDetector.onTouchEvent(ev)) {//返回值来自onFling
            //如果被手势处理类消费了 不再继续执行后续的事件
            return true;
        }

        //菜单关闭时getScrollX是菜单的宽度 菜单打开时getScrollX是0
        //因此在手抬起的时候判断菜单右边缘位置 0<=getScrollX()<mMenuWidth/2 打开菜单
        //mMenuWidth/2 <=getScrollX()<=mMenuWidth 关闭菜单
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (getScrollX() > mMenuWidth / 2) {
                    closeMenu();
                } else {
                    openMenu();
                }
                //return false 很重要 不能走到super.onTouchEvent
                //openMenu closeMenu调用了smoothScrollTo方法 smoothScrollTo最终调用了OverScroller的startScroll方法
                //而父类ACTION_UP中 会根据离开时的速度 基于view一个滑动的惯性滑动 调用的是OverScroller的fling方法
                //因此两者会相互影响 因此需要屏蔽父类的调用 return false;
                return false;

        }
        return super.onTouchEvent(ev);
    }

    //默认关闭菜单
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //TODO 为什么要判断changed
        scrollTo(mMenuWidth, 0);
    }

    /**
     * This is called in response to an internal scroll in this view (i.e., the
     * view scrolled its own contents). This is typically as a result of
     * {@link #scrollBy(int, int)} or {@link #scrollTo(int, int)} having been
     * called.
     *
     * @param l    Current horizontal scroll origin.
     * @param t    Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {//想象不了alpha值如何计算 就打印log然后思考逻辑
        super.onScrollChanged(l, t, oldl, oldt);
        //在滑动onScroll的同时调用setTranslationX 让view看起来好像没有滑动 以达到抽屉效果
        //比如手指向左滑动 view整体向左滑动 但是view又调用了setTranslationX向右转移 看起来好像没有移动
        //这看起来像在一个矩形容器中心放一个球，这时容器向左移动，同时球以相同的速度向右移动，那么球在空间的绝对位置不变 看起来球好像没有移动
        mMenu.setTranslationX(l * 0.8f);
//
//        //根据滑动x的距离调整左侧菜单的透明度
//        float minAlpha = 0.3f;
//        float currentMenuAlpha = (mMenuWidth - l) / (float) mMenuWidth * (1 - minAlpha) + minAlpha;
//
//        //根据滑动x的距离调整右侧内容的透明度
//        float currentContentAlpha = l / (float) mMenuWidth * (1 - minAlpha) + minAlpha;
//        mMenu.setAlpha(currentMenuAlpha);
//        mContent.setAlpha(currentContentAlpha);
//
//        //根据滑动x的距离判断右侧内容缩放的大小 算法和透明度类似
//        float minScale = 0.8f;
//        float currentContentScale = l / (float) mMenuWidth * (1 - minScale) + minScale;
//        //缩放api默认以view的正中心为支点进行缩放 将缩放中心点移动到view的左侧中间位置
//        mContent.setPivotX(0);
//        mContent.setPivotY(ScreenUtil.getScreenHeight(mContent.getContext())/2);
//        mContent.setScaleX(currentContentScale);
//        mContent.setScaleY(currentContentScale);

        float minAlpha = 0f;
        float shadowAlpha = (mMenuWidth - l) / (float) mMenuWidth * (1 - minAlpha) + minAlpha;
        mShadowView.setAlpha(shadowAlpha);
    }

    private void closeMenu() {
        isMenuOpen = false;
        smoothScrollTo(mMenuWidth, 0);
    }

    private void openMenu() {
        isMenuOpen = true;
        smoothScrollTo(0, 0);
    }

    //处理快速滑动fling
    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //向左快速滑动 关闭菜单
            if (velocityX < 0 && isMenuOpen) {
                closeMenu();
                return true;
            }
            //向右快速滑动 即使没有达到指定的x（在onTouchEvent中） 也会打开菜单
            if (velocityX > 0 && !isMenuOpen) {
                openMenu();
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };
}
