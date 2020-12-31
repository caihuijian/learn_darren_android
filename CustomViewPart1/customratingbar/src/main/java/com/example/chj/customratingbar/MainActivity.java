package com.example.chj.customratingbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CustomRatingBar.TouchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomRatingBar customRatingBar = findViewById(R.id.customRatingBar);
        customRatingBar.setTouchListener(this);
    }

    @Override
    public void actionUp(int currentStarIndex) {
        Toast.makeText(this, "按下了第" + (currentStarIndex + 1) + "个星星", Toast.LENGTH_SHORT).show();
    }
}
