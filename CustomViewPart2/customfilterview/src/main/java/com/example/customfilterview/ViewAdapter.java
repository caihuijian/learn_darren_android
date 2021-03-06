package com.example.customfilterview;

import android.view.View;

/**
 * Created by Cai Huijian on 2021/1/5.
 */
public interface ViewAdapter {

    //角色定位 抽象的被观察者 参考ListView的Adapter.java
    void registerObserver(Observer observer);

    void unregisterObserver(Observer observer);

    void notifyContentItemClick(View view);


    // 获取总共有几个tab页
    abstract int getCount();

    // 获取当前tab的TabView
    abstract View getTabView(int position);

    // 获取当前tab的菜单内容
    abstract View getContentView(int position);
}
