package com.example.rotateloadingview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    RotateLoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 模拟获取后台数据完毕
        loadingView = findViewById(R.id.loading_view);
        new Handler().postDelayed(() -> loadingView.loadComplete(), 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RotateLoadingView.AnimateStatus status = loadingView.getCurrentAnimateStatus();
        if (status != null) {
            status.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RotateLoadingView.AnimateStatus status = loadingView.getCurrentAnimateStatus();
        if (status != null) {
            status.resume();
        }
    }
}