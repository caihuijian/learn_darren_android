package com.example.likesview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private LikesLayout mLikesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLikesLayout = findViewById(R.id.likeLayout);
    }

    public void like(View view) {
        mLikesLayout.addLikes();
    }
}