package com.example.lockpatternview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    String passWord = "123456";
    LockPatternView mLockPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLockPattern = findViewById(R.id.lockPattern);
        mLockPattern.setPassWord(passWord);
    }
}
