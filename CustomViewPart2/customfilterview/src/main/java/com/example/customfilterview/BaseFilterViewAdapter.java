package com.example.customfilterview;

import android.database.DataSetObserver;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Cai Huijian on 2021/1/5.
 */
abstract class BaseFilterViewAdapter {

//    protected final ArrayList<T> mObservers = new ArrayList<T>();
//
//    //参考BaseAdapter
//    public void registerDataSetObserver(DataSetObserver observer) {
//        mDataSetObservable.registerObserver(observer);
//    }
//
//    public void unregisterDataSetObserver(DataSetObserver observer) {
//        mDataSetObservable.unregisterObserver(observer);
//    }

    // 获取总共有几个tab页
    public abstract int getCount();

    // 获取当前tab的TabView
    public abstract View getTabView(int position);

    // 获取当前tab的菜单内容
    public abstract View getContentView(int position);
}
