package com.example.chj.customratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class CustomRatingBar extends View {
    private static final String TAG = "CustomRatingBar";
    private Paint starPaint;
    private Bitmap starPressed, starNormal;
    private int starNumberSum = 0, starGap = 0, currentPressedStarIndex = -1;
    private TouchListener touchListener;


    public CustomRatingBar(Context context) {
        this(context, null);
    }

    public CustomRatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);

    }

    private void init(AttributeSet attrs, Context context) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomRatingBar);
        int normalStarRes = array.getResourceId(R.styleable.CustomRatingBar_normalStar, -1);
        int pressedStarRes = array.getResourceId(R.styleable.CustomRatingBar_pressedStar, -1);
        starGap = (int) array.getDimension(R.styleable.CustomRatingBar_starGap, dp2px(10));
        if (normalStarRes == -1 || pressedStarRes == -1) {
            throw new RuntimeException("请指定资源id pressedStar normalStar！ ");
        }
        starPressed = BitmapFactory.decodeResource(getResources(), pressedStarRes);
        starNormal = BitmapFactory.decodeResource(getResources(), normalStarRes);
        starNumberSum = array.getInt(R.styleable.CustomRatingBar_starNumber, 2);
        starPaint = new Paint();
        starPaint.setAntiAlias(true);//抗锯齿
        starPaint.setDither(true);//防抖动
        array.recycle();
    }

    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = starPressed.getHeight() + getPaddingBottom() + getPaddingTop();
        int width = starPressed.getWidth() * starNumberSum + starGap * (starNumberSum - 1) + getPaddingStart() + getPaddingEnd();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < starNumberSum; i++) {
            int startX = getPaddingStart() + (starNormal.getWidth() + starGap) * i;
            int startY = getPaddingTop();
            if (i <= currentPressedStarIndex) {
                canvas.drawBitmap(starPressed, startX, startY, starPaint);
            } else {
                canvas.drawBitmap(starNormal, startX, startY, starPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG, "onTouchEvent: " + event.getAction() + " event.getX() " + event.getX() + " event.getY() " + event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //判断Y坐标落在有效区间
                if (event.getY() < getPaddingTop() || event.getY() > (starNormal.getHeight() + getPaddingTop())) {
                    return true;
                }
                //判断X坐标落在有效区间
                if (event.getX() < getPaddingStart() || event.getX() > (starNormal.getWidth() * starNumberSum + starGap * (starNumberSum - 1) + getPaddingStart())) {
                    return true;
                }
                //判断落在第几个星星
                int starNum = (int) ((event.getX() - getPaddingStart()) / (starNormal.getWidth() + starGap));
                if (currentPressedStarIndex != starNum) {
                    Log.d(TAG, "onTouchEvent: starNum" + starNum);
                    currentPressedStarIndex = starNum;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touchListener != null) {
                    touchListener.actionUp(currentPressedStarIndex);
                }
                break;
        }
        return true;//不消费事件
    }

    public void setTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
    }

    interface TouchListener {
        void actionUp(int currentStarIndex);
    }
}
