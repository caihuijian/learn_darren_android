package com.example.circleloadingview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleLoadingView circleLoadingView = findViewById(R.id.loadingView);
        circleLoadingView.postDelayed(circleLoadingView::loadingComplete, 1000 * 10);//10s后假装加载完毕
    }
}