package com.example.custom58loadview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hjcai on 2021/1/3.
 */
public class ShapeView extends View {
    @ShapeType
    private int mCurrentShape = ShapeType.CIRCLE;
    private Paint mCirclePaint, mTrianglePaint, mSquarePaint;
    private Path mPath;

    public ShapeView(Context context) {
        this(context, null);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCirclePaint = initPaintByColor(Color.RED);
        mTrianglePaint = initPaintByColor(Color.YELLOW);
        mSquarePaint = initPaintByColor(Color.GRAY);
    }

    private Paint initPaintByColor(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        return paint;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ShapeType.CIRCLE, ShapeType.SQUARE, ShapeType.TRIANGLE})
    public @interface ShapeType {
        int CIRCLE = 0;
        int SQUARE = 1;
        int TRIANGLE = 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(width, height), Math.min(width, height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mCurrentShape) {
            case ShapeType.CIRCLE:
                // 画圆形
                int center = getWidth() / 2;
                canvas.drawCircle(center, center, center, mCirclePaint);
                break;
            case ShapeType.SQUARE:
                // 画正方形
                canvas.drawRect(0, 0, getWidth(), getHeight(), mSquarePaint);
                break;
            case ShapeType.TRIANGLE:
                // 画三角  Path 画路线
                if (mPath == null) {
                    mPath = new Path();
                    mPath.moveTo(getWidth() >> 1, 0);
                    //勾股定理
                    mPath.lineTo(0, (float) (Math.sqrt(3) * getWidth() / 2));
                    mPath.lineTo(getWidth(), ((float) Math.sqrt(3) * getWidth() / 2));
                    mPath.close();//直接连接到起点
                }
                canvas.drawPath(mPath, mTrianglePaint);
                break;
        }
    }

    public void exchange() {
        switch (mCurrentShape) {
            case ShapeType.CIRCLE:
                mCurrentShape = ShapeType.SQUARE;
                break;
            case ShapeType.SQUARE:
                mCurrentShape = ShapeType.TRIANGLE;
                break;
            case ShapeType.TRIANGLE:
                mCurrentShape = ShapeType.CIRCLE;
                break;
        }
        // 不断重新绘制形状
        invalidate();
    }

    public int getCurrentShape() {
        return mCurrentShape;
    }
}
