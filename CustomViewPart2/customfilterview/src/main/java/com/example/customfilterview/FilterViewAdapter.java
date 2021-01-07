package com.example.customfilterview;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
        RelativeLayout.LayoutParams contentViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        contentViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textContentView.setLayoutParams(contentViewLayoutParams);

        textContentView.setOnClickListener(v -> {
            //TODO 角色定位
            //事件源 具体的被观察者 这里通知其他观察者事件
            notifyContentItemClick(v);
        });

        return textContentView;
    }
}
