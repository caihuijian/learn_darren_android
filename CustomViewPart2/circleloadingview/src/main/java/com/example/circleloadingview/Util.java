package com.example.circleloadingview;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Cai Huijian on 2021/1/9.
 */
class Util {
    public static float dip2px(int dip, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dip, context.getResources().getDisplayMetrics());
    }
}
