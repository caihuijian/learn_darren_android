package com.example.lockpatternview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LockPatternView extends View {
    Point[][] mPoints;
    private boolean hasInit = false;
    private Paint mPaintNormal, mPaintPressed, mPaintError;
    private int radiusOut, radiusInner;

    public LockPatternView(Context context) {
        super(context);
    }

    public LockPatternView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LockPatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //在这里写死 采用宽高中较短的那个作为边，绘制区域为这个边长的正方形
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int smaller = Math.min(height, width);
        setMeasuredDimension(smaller, smaller);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!hasInit) {//只初始化一次
            hasInit = true;
            initPoints();//3初始化圆 以及位置
            intPaint();//3初始化画笔
        }
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        for (Point[] pointArr : mPoints) {
            for (Point point : pointArr) {
                canvas.drawCircle(point.positionX, point.positionY, radiusOut, mPaintNormal);
            }
        }
    }

    private void intPaint() {
        radiusOut = getMeasuredWidth() / 8;
        radiusInner = radiusOut / 3;
        mPaintNormal = getPaintByColor(0xFFCCFFFF);
        mPaintPressed = getPaintByColor(0xFF99FFFF);
        mPaintError = getPaintByColor(0xFFCC3333);
    }

    private Paint getPaintByColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        return paint;
    }

    private void initPoints() {
        mPoints = new Point[3][3];
        //第一行 每一行y坐标一样
        mPoints[0][0] = new Point(getMeasuredWidth() / 4, getMeasuredWidth() / 4, 0, PointStatus.NORMAL);
        mPoints[0][1] = new Point(getMeasuredWidth() * 2 / 4, getMeasuredWidth() / 4, 1, PointStatus.NORMAL);
        mPoints[0][2] = new Point(getMeasuredWidth() * 3 / 4, getMeasuredWidth() / 4, 2, PointStatus.NORMAL);
        //第二行
        mPoints[1][0] = new Point(getMeasuredWidth() / 4, getMeasuredWidth() / 2, 3, PointStatus.NORMAL);
        mPoints[1][1] = new Point(getMeasuredWidth() * 2 / 4, getMeasuredWidth() / 2, 4, PointStatus.NORMAL);
        mPoints[1][2] = new Point(getMeasuredWidth() * 3 / 4, getMeasuredWidth() / 2, 5, PointStatus.NORMAL);
        //第三行
        mPoints[2][0] = new Point(getMeasuredWidth() / 4, getMeasuredWidth() * 3 / 4, 6, PointStatus.NORMAL);
        mPoints[2][1] = new Point(getMeasuredWidth() * 2 / 4, getMeasuredWidth() * 3 / 4, 7, PointStatus.NORMAL);
        mPoints[2][2] = new Point(getMeasuredWidth() * 3 / 4, getMeasuredWidth() * 3 / 4, 8, PointStatus.NORMAL);
    }

    /**
     * 获取屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        if (context == null) {
            return -1;
        }
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        if (context == null) {
            return -1;
        }
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PointStatus.NORMAL, PointStatus.PRESSED, PointStatus.ERROR})
    public @interface PointStatus {
        int NORMAL = 0;
        int PRESSED = 1;
        int ERROR = 2;
    }

    class Point {
        int positionX;
        int positionY;
        int index;
        @PointStatus
        int status;

        Point(int positionX, int positionY, int index, @PointStatus int status) {
            this.positionX = positionX;
            this.positionY = positionY;
            this.index = index;
            this.status = status;
        }

        public void setStatus(@PointStatus int status) {
            this.status = status;
        }
    }
}
