package com.example.dragboomview;

import android.graphics.Bitmap;
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
}
