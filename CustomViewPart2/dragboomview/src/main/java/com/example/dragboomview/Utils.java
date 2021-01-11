package com.example.dragboomview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Cai Huijian on 2021/1/11.
 */
class Utils {
    //2.1 创建截图
    public static Bitmap getBitmapByView(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static float dp2px(float dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    //获取状态栏的高度
    public static int getStatusBarHeight1(Context context) {
        // 插件式换肤有讲到：怎么获取资源的-->类似反射 先获取资源id，根据id获取资源高度
        Resources resources = context.getResources();
        int statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android");
        Log.e("TAG", statusBarHeightId + " -> " + resources.getDimensionPixelOffset(statusBarHeightId));
        return resources.getDimensionPixelSize(statusBarHeightId);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static float getStatusBarHeight(Context context) {
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
