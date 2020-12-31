package com.example.customstatusbar2;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by hjcai on 2020/12/31.
 */
public class StatusBarUtil {
    //设置Activity的状态栏颜色 注意是状态栏
    public static void setStatusBarColor(Activity activity, int color) {
        // 5.0 以上 直接调用系统提供的方法 setStatusBarColor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }


        // 4.4 - 5.0 之间 大致思路 采用一个技巧，首先把他弄成全屏，在状态栏的部分加一个布局
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 首先把他弄成全屏（），在状态栏的部分加一个布局
            // activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//这个标志位完全全屏了 都没有状态栏了
            // FLAG_TRANSLUCENT_STATUS标志位 电量 时间 网络状态 都还在
            // 1 将Window设置为全屏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // 在状态栏的部分加一个布局 setContentView 源码分析，自己加一个布局 (高度是状态栏的高度)
            // 2 先准备一个高度与statusBar相等的view 并设置好颜色
            View view = new View(activity);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
            view.setLayoutParams(params);
            view.setBackgroundColor(color);


            // DecorView是一个 FrameLayout 布局 , 会加载一个系统的布局（LinearLayout） ,
            // 在系统布局中会有一个 id 为 android.R.id.content 这布局是（RelativeLayout）

            //  http://www.jianshu.com/p/531d1168b3ee
            // 3 将第二步准备好的view添加到decor view
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(view);

            // 4 获取activity中setContentView布局的根布局 contentView 并给他一个padding
            // 出现布局被view遮挡的情况 解决方案一
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            contentView.setPadding(0, getStatusBarHeight(activity), 0, 0);
            // 创建的view最好不要加在mainActivity的布局 而是加在DecorView

            // 出现布局被view遮挡的情况 解决方案二可以在xml中添加 android:fitsSystemWindows="true" 但是每个布局都要写
            // 出现布局被view遮挡的情况 解决方案三
            // View activityView = contentView.getChildAt(0);
            // activityView.setFitsSystemWindows(true);
            // 这样只要设置一次即可
            // 出现布局被view遮挡的情况 解决方案四
            // activityView.setPadding(0,getStatusBarHeight(activity),0,0);
            // 方案一和方案四类似 只不过一个padding加在content的上面 一个padding加在了content的第一个子view上
            // 不过方案一更优 因为方案四会导致我们在自己的布局文件最外层容器设置padding无效

            // 整体逻辑：由外到内的布局分别是 1 PhoneWindow 2 DecorView(FrameLayout) 3 系统布局（LinearLayout 有个叫android.R.id.content的占位符的FrameLayout） 4 我们创建的布局加载在android.R.id.content的位置
            // 比如R.layout.screen_simple.xml是一个系统布局 是一个LinearLayout
            // 1 将PhoneWindow 设置为全屏
            // 2 将准备好的view放到DecorView（FrameLayout）的尾部 对于FrameLayout来说就是最上层
            // 3 由于FrameLayout不会自动将其他view挤到正确的位置 因此我们需要给处于FrameLayout中的系统布局设置一个padding或者fitsSystemWindows 将之前添加的view露出来
        }
    }

    //获取状态栏的高度
    private static int getStatusBarHeight(Activity activity) {
        // 插件式换肤有讲到：怎么获取资源的-->类似反射 先获取资源id，根据id获取资源高度
        Resources resources = activity.getResources();
        int statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android");
        Log.e("TAG", statusBarHeightId + " -> " + resources.getDimensionPixelOffset(statusBarHeightId));
        return resources.getDimensionPixelOffset(statusBarHeightId);
    }

    //设置activity全屏（5.0以上调用该方法还需要将主题设置为NoActionBar）
    public static void setStatusBarTranslucent(Activity activity) {
        // 5.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        // 用上面的方法中的第一步即可
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
