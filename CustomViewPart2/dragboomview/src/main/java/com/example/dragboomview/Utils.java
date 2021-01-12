package com.example.dragboomview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PointF;
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

    //获取状态栏高度
    public static float getStatusBarHeight1(Context context) {
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        //没有获取到 取0
        return 0;
    }

    //按照百分比在由point1 point2组成的线段上取点
    public static PointF getPointByPercent(PointF point1, PointF point2, float percent) {
        return new PointF(evaluateValue(percent, point1.x, point2.x), evaluateValue(
                percent, point1.y, point2.y));
    }

    //Number是int float等基本数字类型的父类
    public static float evaluateValue(float percent, Number start, Number end) {
        return start.floatValue() + (end.floatValue() - start.floatValue())
                * percent;
    }
}
