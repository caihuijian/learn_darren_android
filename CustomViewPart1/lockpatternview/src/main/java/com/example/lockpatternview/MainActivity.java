package com.example.lockpatternview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String mPassWord = "123456";
    LockPatternView mLockPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLockPattern = findViewById(R.id.lockPattern);
        mLockPattern.setPassListener(new LockPatternView.PassListener() {
            @Override
            public void notifyPass(String pass) {
                checkPassWord(pass);
            }
        });
    }

    private void checkPassWord(String passWord) {
        //如果密码太短 弹出提示 并将所有点状态跟新为error 设置屏幕无法触碰 两秒后清空状态
        if (passWord.length() <= 4) {
            Toast.makeText(MainActivity.this, "密码太短！", Toast.LENGTH_SHORT).show();
            mLockPattern.setPointsStatus(LockPatternView.PointStatus.ERROR);
            delayResetLockPattern(2000);
            //2s后恢复正常状态
            return;
        }

        //如果密码正确 弹出提示 设置屏幕无法触碰 两秒之后清空状态
        Log.d("TAG", "checkPassWord: mPassWord " + mPassWord + " passWord " + passWord);
        if (passWord.equals(mPassWord)) {
            Toast.makeText(MainActivity.this, "密码正确！", Toast.LENGTH_SHORT).show();
            delayResetLockPattern(2000);
        } else {//如果密码错误 弹出提示 并将所有点状态跟新为error 设置屏幕无法触碰 两秒后清空状态
            Toast.makeText(MainActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
            delayResetLockPattern(2000);
        }
    }

    void delayResetLockPattern(int delayMillisecond) {
        mLockPattern.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLockPattern.resetLockPatternView();
            }
        }, delayMillisecond);
    }
}
