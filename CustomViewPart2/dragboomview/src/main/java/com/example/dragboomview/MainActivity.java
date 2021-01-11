package com.example.dragboomview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView =  findViewById(R.id.textView);
        DragBoomView.attachToView(textView);
        //TODO 截图被放在了那一层？？？
        //TODO DragBoomViewTouchListener 和DragBoomView 职责不清
    }
}