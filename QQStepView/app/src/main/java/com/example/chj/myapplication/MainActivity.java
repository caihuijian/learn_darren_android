package com.example.chj.myapplication;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final QQStepView qqStepView = findViewById(R.id.myQQStepView);
        qqStepView.setStepMax(20000);

        //step7 添加属性动画
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0,10799);
        valueAnimator.setDuration(1500);
        valueAnimator.setInterpolator(new DecelerateInterpolator());//插值器 用于调整动画播放速度 先快后慢
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentStep = (float) animation.getAnimatedValue();
                qqStepView.setStepCurrent((int) currentStep);
            }
        });
        valueAnimator.start();
    }

}
