package com.example.customfilterview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Cai Huijian on 2021/1/5.
 */
class FilterViewAdapter extends BaseFilterViewAdapter {
    private Context mContext;

    public FilterViewAdapter(Context context) {
        this.mContext = context;
    }

    String[] tabTexts = {"美食", "生活", "鬼畜", "动漫区", "其他"};

    @Override
    public int getCount() {
        return tabTexts.length;
    }

    @Override
    public View getTabView(int position) {
        TextView textTabView = new TextView(mContext);
        textTabView.setPadding(10,10,10,10);
        textTabView.setGravity(Gravity.CENTER);
        textTabView.setText(tabTexts[position]);
        return textTabView;
    }

    @Override
    public View getContentView(int position) {
        TextView textContentView = new TextView(mContext);
        textContentView.setGravity(Gravity.CENTER);
        textContentView.setText(tabTexts[position]);
        return textContentView;
    }
}
