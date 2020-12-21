package com.example.chj.lettersidebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LetterSideBar.LetterSideBarTouchListener {
    private TextView mTv;
    private LetterSideBar mLetterSideBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv = findViewById(R.id.tv);
        mLetterSideBar = findViewById(R.id.letterSideBar);
        mLetterSideBar.setLetterSideBarListener(this);
    }

    @Override
    public void onTouch(final String letter, boolean isDown) {
        if (isDown) {
            mTv.setText(letter);
            mTv.setVisibility(View.VISIBLE);
        } else {
            mTv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTv.setText(letter);
                    mTv.setVisibility(View.GONE);
                }
            }, 200);
        }

    }
}
