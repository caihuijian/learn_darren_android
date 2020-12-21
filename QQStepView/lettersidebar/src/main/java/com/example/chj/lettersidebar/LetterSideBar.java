package com.example.chj.lettersidebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class LetterSideBar extends View {
    private static final String TAG = "LetterSideBar";
    String mLetters[] = new String[]{
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z", "#"};
    private Paint mTextPaint, mFocusTextPaint;
    private int currentTouchIndex = -1;

    public LetterSideBar(Context context) {
        this(context, null);
    }

    public LetterSideBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterSideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextPaint = initPaint(Color.BLUE);
        mFocusTextPaint = initPaint(Color.RED);
    }

    private Paint initPaint(int color) {
        //省略自定义属性
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextSize(sp2px(20));
        return paint;
    }

    private float sp2px(int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private Rect bounds = new Rect();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxLetterWidth = 0;
        int height = MeasureSpec.getSize(heightMeasureSpec);
        for (String letter : mLetters) {//此处有改动 因为每个字母的宽度不等 所以我觉得不能以字母A的宽度作为控件宽度 而应该取最大的字母的宽度
            mTextPaint.getTextBounds(letter, 0, 1, bounds);
            maxLetterWidth = Math.max(maxLetterWidth, bounds.width());
        }

        setMeasuredDimension(maxLetterWidth + getPaddingEnd() + getPaddingStart(), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int itemHeight = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / mLetters.length;
        Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
        int baseLine = (itemHeight - fontMetricsInt.bottom - fontMetricsInt.top) / 2;

        int startX;//每个字母开始绘制的x点
        int startY;//;//每个字母开始绘制的y起点
        for (int i = 0; i < mLetters.length; i++) {
            mTextPaint.getTextBounds(mLetters[i], 0, 1, bounds);
            int currentTextWidth = bounds.width();
            startX = (getMeasuredWidth() - currentTextWidth) / 2;
            startY = getPaddingTop() + i * itemHeight + baseLine;
            if (currentTouchIndex == i) {
                canvas.drawText(mLetters[i], startX, startY, mFocusTextPaint);
            } else {
                canvas.drawText(mLetters[i], startX, startY, mTextPaint);
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //计算落在那个字母
                int itemHeight = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / mLetters.length;
                int index = (int) ((event.getY() - getPaddingTop()) / itemHeight);
                if (index < 0 || index > 26) {
                    return true;
                }
                if (currentTouchIndex != index) {//优化 只有index发生变化才通知更新UI
                    currentTouchIndex = index;
                    mLetterSideBarTouchListener.onTouch(mLetters[index], true);
                    invalidate();
                }

                Log.d(TAG, "onTouchEvent: " + mLetters[index]);
                break;
            case MotionEvent.ACTION_UP:
                //TODO 优化 松开的时候 高亮字母变色
                mLetterSideBarTouchListener.onTouch("", false);
                break;
        }
        return true;//view 消费事件
    }

    private LetterSideBarTouchListener mLetterSideBarTouchListener;

    void setLetterSideBarListener(LetterSideBarTouchListener listener) {
        mLetterSideBarTouchListener = listener;
    }

    interface LetterSideBarTouchListener {
        void onTouch(String letter, boolean isDown);
    }
}
