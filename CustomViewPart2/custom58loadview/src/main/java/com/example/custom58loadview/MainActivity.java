package com.example.custom58loadview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    CustomLoadView mLoadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadView = findViewById(R.id.load_view);
        mLoadView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadView.loadComplete();
            }
        }, 3000);
    }
}
