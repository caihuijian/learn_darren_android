package com.example.customstatusbar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

/**
 * 本节内容
 * 状态栏设置颜色 获取高度 设置全屏
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //StatusBarUtil.setStatusBarColor(this, Color.RED);
        StatusBarUtil.setStatusBarTranslucent(this);//5.0以上调用该方法还需要将主题设置为NoActionBar
    }
}
