package com.example.chj.muticolortextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class MultiColorTextView extends TextView {
    private Paint mOriginPaint;
    private Paint mChangePaint;
    //绘制进度
    private float mCurrentProgress = 0.0f;
    //绘制方向
    private Direction mCurrentDirection = Direction.LEFT_TO_RIGHT;

    public enum Direction {
        RIGHT_TO_LEFT, LEFT_TO_RIGHT
    }

    public MultiColorTextView(Context context) {
        this(context, null);
    }

    public MultiColorTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiColorTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context, attrs);
    }

    //1.1读取xml的自定义属性 初始化画笔
    private void initPaint(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MultiColorTextView);
        int originPaintColor = array.getColor(R.styleable.MultiColorTextView_originColor, Color.GREEN);
        int changePaintColor = array.getColor(R.styleable.MultiColorTextView_changeColor, Color.GREEN);
        mOriginPaint = getPaintByColor(originPaintColor);
        mChangePaint = getPaintByColor(changePaintColor);
        array.recycle();
    }

    private Paint getPaintByColor(int originPaintColor) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        paint.setTextSize(getTextSize());//获取父类TextView定义的textSize
        paint.setDither(true);//防抖动
        paint.setColor(originPaintColor);
        return paint;
    }

    //2 实现一行文字两种颜色 思路 利用canvas.clipRect的api 不断裁切 坐标用一个画笔绘制 右边是另一个画笔绘制
    @Override
    protected void onDraw(Canvas canvas) {
        //不要使用TextView的onDraw方法
        //super.onDraw(canvas);


        //根据mCurrentProgress 计算两个画笔绘制的临界点
        int middle = (int) (getWidth() * mCurrentProgress);

//        //抽成方法start
//        //裁切区域 保留以下区域
//        //canvas.clipRect(0,0,80,getHeight());
//        canvas.save();
//        Rect rect = new Rect(0, 0, middle, getHeight());//裁剪区域 只绘制0-middle 左半区域
//        canvas.clipRect(rect);
//
//        //获取文字 计算位置 绘制文字
//        String text = getText().toString();
//        //获取text宽度
//        Rect bounds = new Rect();
//        mOriginPaint.getTextBounds(text, 0, text.length(), bounds);
//        //计算绘制origin的x坐标
//        int originStartX = (getWidth() - bounds.width()) / 2;
//
//        //计算绘制origin的y坐标（baseline）
//        Paint.FontMetrics fontMetrics = mOriginPaint.getFontMetrics();
//        float baseline = (getHeight()-fontMetrics.top-fontMetrics.bottom) / 2;
//        canvas.drawText(text, originStartX, baseline, mOriginPaint);
//        canvas.restore();//canvas.save();保存画布  canvas.restore();释放画布 如果不调用 无法进行再次裁切 他们成对调用
//
//        canvas.save();
//        rect = new Rect(middle, 0, getWidth(), getHeight());//裁剪区域 只绘制middle-getWidth 右半区域
//        canvas.clipRect(rect);
//        canvas.drawText(text, originStartX, baseline, mChangePaint);
//        canvas.restore();
//        //抽成方法end

        //将绘制左边和绘制右边的部分抽成一个方法 调用两次 与上面注释部分等价
        if (Direction.LEFT_TO_RIGHT == mCurrentDirection) {
            clipRectThenDrawText(canvas, mChangePaint, 0, middle);//只绘制0-middle 左半区域
            clipRectThenDrawText(canvas, mOriginPaint, middle, getWidth());//裁剪区域 只绘制middle-getWidth 右半区域
        } else {
            clipRectThenDrawText(canvas, mChangePaint, getWidth()-middle, getWidth());//只绘制右区域
            clipRectThenDrawText(canvas, mOriginPaint, 0, getWidth()-middle);//裁剪区域 只绘制左半区域
        }

    }

    private void clipRectThenDrawText(Canvas canvas, Paint paint, int startClip, int endClip) {
        canvas.save();
        Rect rect = new Rect(startClip, 0, endClip, getHeight());//裁剪区域 只绘制这个区域
        canvas.clipRect(rect);

        //获取文字 计算位置 绘制文字
        String text = getText().toString();
        //获取text宽度
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        //计算绘制origin的x坐标
        int originStartX = (getWidth() - bounds.width()) / 2;

        //计算绘制origin的y坐标（baseline）
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float baseline = (getHeight() - fontMetrics.top - fontMetrics.bottom) / 2;
        canvas.drawText(text, originStartX, baseline, paint);
        canvas.restore();//canvas.save();保存画布  canvas.restore();释放画布 如果不调用 无法进行再次裁切 他们成对调用
    }

    public void setCurrentProgress(float mCurrentProgress) {
        this.mCurrentProgress = mCurrentProgress;
        invalidate();
    }

    public void setCurrentDirection(Direction direction) {
        this.mCurrentDirection = direction;
    }
}

