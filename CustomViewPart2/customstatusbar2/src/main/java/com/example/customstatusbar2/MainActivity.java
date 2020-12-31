package com.example.customstatusbar2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 本节内容
 * 状态栏渐隐效果
 * 原理： 根据ImageView的高度修改状态栏alpha值
 */
public class MainActivity extends AppCompatActivity {

    ScrollView mScrollView;
    ImageView mImageView;
    int mImageViewHeight;
    int mHeaderViewHeight;
    View mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScrollView = findViewById(R.id.scrollView);
        mImageView = findViewById(R.id.imageView);
        mHeader = findViewById(R.id.header);
        mHeader.post(new Runnable() {
            @Override
            public void run() {
                mHeaderViewHeight = mHeader.getMeasuredHeight();
            }
        });
        mHeader.getBackground().setAlpha(0);
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mImageViewHeight = mImageView.getMeasuredHeight();
            }
        });
        mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (mImageViewHeight == 0) {
                    return;
                }
                float alpha = (float) scrollY / (mImageViewHeight - mHeaderViewHeight);
                alpha = alpha * 255;
                if (alpha > 255) {
                    alpha = 255;
                }
                if (alpha < 0) {
                    alpha = 0;
                }
                Log.d("TAG", "onScrollChange: alpha " + alpha);
                //background setAlpha和view setAlpha的取值范围不同 这里要*255
                mHeader.getBackground().setAlpha((int) alpha);
            }
        });
    }
}
