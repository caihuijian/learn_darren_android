package com.example.customstatusbar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //StatusBarUtil.setStatusBarColor(this, Color.RED);
        StatusBarUtil.setStatusBarTranslucent(this);//5.0以上调用该方法还需要将主题设置为NoActionBar
    }
}
