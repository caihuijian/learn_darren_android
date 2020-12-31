package com.example.chj.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class QQStepView extends View {
    private int mOuterColor = Color.BLUE;
    private int mInnerColor = Color.GREEN;
    private int mBorderWidth = 1;
    private int mStepTextSize = 5;
    private int mStepTextColor = Color.GRAY;
    private Paint mOuterArcPaint, mInnerArcPaint, mTextPaint;
    private float mStepMax = 8000;
    private float mStepCurrent = 2000;


    //step3 创建java 文件并准备好构造方法
    public QQStepView(Context context) {
        this(context, null);
    }

    public QQStepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //step4 获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.QQStepView);
        mInnerColor = array.getColor(R.styleable.QQStepView_innerColor, mInnerColor);
        mOuterColor = array.getColor(R.styleable.QQStepView_outerColor, mOuterColor);
        mStepTextColor = array.getColor(R.styleable.QQStepView_stepTextColor, mStepTextColor);
        mBorderWidth = (int) array.getDimension(R.styleable.QQStepView_borderWidth, mBorderWidth);
        mStepTextSize = array.getDimensionPixelSize(R.styleable.QQStepView_stepTextSize, mStepTextSize);
        array.recycle();

        mOuterArcPaint = new Paint();
        mOuterArcPaint.setAntiAlias(true);//抗锯齿
        mOuterArcPaint.setColor(mOuterColor);
        mOuterArcPaint.setStrokeWidth(mBorderWidth);
        mOuterArcPaint.setStyle(Paint.Style.STROKE);//画空心弧
        mOuterArcPaint.setStrokeCap(Paint.Cap.ROUND);//收口（帽子）为圆形 而不是直角

        mInnerArcPaint = new Paint();
        mInnerArcPaint.setAntiAlias(true);//抗锯齿
        mInnerArcPaint.setColor(mInnerColor);
        mInnerArcPaint.setStrokeWidth(mBorderWidth);
        mInnerArcPaint.setStyle(Paint.Style.STROKE);//画空心弧
        mInnerArcPaint.setStrokeCap(Paint.Cap.ROUND);//收口（帽子）为圆形 而不是直角

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);//抗锯齿
        mTextPaint.setColor(mStepTextColor);
        mTextPaint.setTextSize(mStepTextSize);
    }

    //step5 编写onMeasure方法 决定控件宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果xml中定义的宽高任意一个是wrap content 给控件一个最小宽高
        int smallerValue = 50;
        if (MeasureSpec.AT_MOST != MeasureSpec.getMode(widthMeasureSpec) && MeasureSpec.AT_MOST != MeasureSpec.getMode(heightMeasureSpec)) {
            //TODO 疑问 如果使用match parent出现的效果不对 不是正方形 但是断点看 应该宽高设置一致了。。。
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            //宽高取最小值 确保是个正方形
            smallerValue = Math.min(width, height);
        }
        setMeasuredDimension(smallerValue, smallerValue);
    }

    //step6 编写onDraw方法 画外圆弧 内圆弧 文字
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //step6.1 绘制外圆弧
        //边缘没有显示完整 是因为描边有宽度 需要减去描边宽度的一半 视频中的计算有些复杂了 不够直接
        RectF rectF = new RectF(mOuterArcPaint.getStrokeWidth() / 2, mOuterArcPaint.getStrokeWidth() / 2, getWidth() - mOuterArcPaint.getStrokeWidth() / 2, getHeight() - mOuterArcPaint.getStrokeWidth() / 2);
        //参数 绘制区域 开始绘制的角度 画笔扫过后绘制的角度 是否封闭 画笔
        canvas.drawArc(rectF, 135, 270, false, mOuterArcPaint);

        if (mStepMax == 0) {
            return;
        }
        //step6.2 绘制内圆弧（当前步数占总步数的比例==内弧的角度占外弧角度的比例）
        float innerArcSweepAngle = (mStepCurrent / mStepMax) * 270;//270是外弧的角度
        canvas.drawArc(rectF, 135, innerArcSweepAngle, false, mInnerArcPaint);

        //step6.3 绘制文字
        String mStepCurrentStr = (int) mStepCurrent + "";

        //计算文字绘制的左边起始点：（控件的宽度-文字的宽度）/2
        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(mStepCurrentStr, 0, mStepCurrentStr.length(), textBounds);//用于计算text的宽度
        float textStartX = (getWidth() - textBounds.width()) / 2.0f;

        //计算文字绘制的baseline
        Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
        //int baseLine = Math.abs((fontMetricsInt.bottom - fontMetricsInt.top - getHeight()) / 2 + fontMetricsInt.top);
        int baseLine = getHeight() / 2 - fontMetricsInt.bottom / 2 - fontMetricsInt.top / 2;

        //使用方法drawText(@NonNull String text, float x, float y, @NonNull Paint paint)
        //含义 文本 左边的绘制起始点 baseline 画笔
        canvas.drawText(mStepCurrentStr, textStartX, baseLine, mTextPaint);
    }

    public synchronized void setStepMax(int stepMax) {
        this.mStepMax = stepMax;
    }

    public synchronized void setStepCurrent(int stepCurrent) {
        this.mStepCurrent = stepCurrent;
        //不断绘制
        invalidate();
    }
}
