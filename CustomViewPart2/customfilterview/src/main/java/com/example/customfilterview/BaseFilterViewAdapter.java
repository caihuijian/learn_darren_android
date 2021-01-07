package com.example.customfilterview;

import android.database.DataSetObserver;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Cai Huijian on 2021/1/5.
 */
abstract class BaseFilterViewAdapter {

    //角色定位 抽象的被观察者 参考ListView的BaseAdapter
    //开始 抽象的被观察者
    protected final ArrayList<ContentClickObserver> mObservers = new ArrayList<>();

    public void registerObserver(ContentClickObserver observer) {
        mObservers.add(observer);
    }

    public void unregisterObserver(ContentClickObserver observer) {
        mObservers.remove(observer);
    }

    public void notifyContentItemClick(View view) {
        for (ContentClickObserver observer : mObservers) {
            observer.contentItemClick(view);
        }
    }
    //结束 抽象的被观察者


    // 获取总共有几个tab页
    public abstract int getCount();

    // 获取当前tab的TabView
    public abstract View getTabView(int position);

    // 获取当前tab的菜单内容
    public abstract View getContentView(int position);
}
