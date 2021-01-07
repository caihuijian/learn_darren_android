package com.example.customfilterview;

import android.database.DataSetObserver;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Cai Huijian on 2021/1/5.
 */
public interface BaseFilterViewAdapter {

    //角色定位 抽象的被观察者 参考ListView的Adapter.java
    void registerObserver(ContentClickObserver observer);

    void unregisterObserver(ContentClickObserver observer);

    void notifyContentItemClick(View view);


    // 获取总共有几个tab页
    abstract int getCount();

    // 获取当前tab的TabView
    abstract View getTabView(int position);

    // 获取当前tab的菜单内容
    abstract View getContentView(int position);
}
