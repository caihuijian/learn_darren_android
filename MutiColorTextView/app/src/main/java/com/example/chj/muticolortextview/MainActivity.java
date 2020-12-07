package com.example.chj.muticolortextview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private MultiColorTextView multiColorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        multiColorTextView = findViewById(R.id.multi_tv);
    }

    public void leftToRight(View view) {
        multiColorTextView.setCurrentDirection(MultiColorTextView.Direction.LEFT_TO_RIGHT);
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0,1);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float mCurrentProgress = (float) animation.getAnimatedValue();
                multiColorTextView.setCurrentProgress(mCurrentProgress);
            }
        });
        valueAnimator.start();
    }

    public void rightToLeft(View view) {
        multiColorTextView.setCurrentDirection(MultiColorTextView.Direction.RIGHT_TO_LEFT);
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0,1);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float mCurrentProgress = (float) animation.getAnimatedValue();
                multiColorTextView.setCurrentProgress(mCurrentProgress);
            }
        });
        valueAnimator.start();
    }
}
