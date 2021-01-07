package com.example.customfilterview;

import android.view.View;

/**
 * Created by Cai Huijian on 2021/1/7.
 */
abstract class ContentClickObserver {
    //角色定位 抽象的观察者 参考ListView的Observable
    abstract void contentItemClick(View view);
}
