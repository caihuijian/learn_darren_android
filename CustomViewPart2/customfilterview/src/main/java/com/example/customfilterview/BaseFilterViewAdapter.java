package com.example.customfilterview;

import android.view.View;

/**
 * Created by Cai Huijian on 2021/1/5.
 */
abstract class BaseFilterViewAdapter {
    // 获取总共有几个tab页
    public abstract int getCount();

    // 获取当前tab的TabView
    public abstract View getTabView(int position);

    // 获取当前tab的菜单内容
    public abstract View getContentView(int position);
}
