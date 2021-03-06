package com.example.dragboomview;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        DragBoomView.attachToView(textView, view -> {
            //4.6通知到调用者，View被删除成功
            Toast.makeText(MainActivity.this, "原先的View1被删除", Toast.LENGTH_SHORT).show();
            view.setVisibility(View.GONE);
        });

        TextView textView2 = findViewById(R.id.textView2);
        DragBoomView.attachToView(textView2, view -> {
            Toast.makeText(MainActivity.this, "原先的View2被删除", Toast.LENGTH_SHORT).show();
            view.setVisibility(View.GONE);
        });

        TextView textView3 = findViewById(R.id.textView3);
        DragBoomView.attachToView(textView3, view -> {
            Toast.makeText(MainActivity.this, "原先的View3被删除", Toast.LENGTH_SHORT).show();
            view.setVisibility(View.GONE);
        });
    }
}