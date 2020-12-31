package com.example.lockpatternview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class LockPatternView extends View {
    private Point[][] mPoints;
    private boolean hasInit = false;
    private Paint mPaintNormal, mPaintPressed, mPaintError;//这里就不搞那么多画笔了 内外圈一样的颜色
    private int mRadiusOut, mRadiusInner;
    private ArrayList<Point> mPressedPoints = new ArrayList<>();
    private PassListener mPassListener;
    private float mFingerX = -1;
    private float mFingerY = -1;
    private boolean mNeedIntercept = false;

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
            initPoints();//初始化圆 以及位置
            intPaint();//初始化画笔
        }
        drawCircle(canvas);
        drawLine(canvas);
        drawLine2Finger(canvas);
    }

    private void drawLine2Finger(Canvas canvas) {
        //注意如果选中的圆数目为0 不需要绘制
        if (mPressedPoints.size() == 0) {
            return;
        }
        //手指离开屏幕 直接return
        if (mFingerX < 0 || mFingerY < 0) {
            return;
        }

        //手指在内圈 直接return
        if (isFingerInCircle(mFingerX, mFingerY, mPressedPoints.get(mPressedPoints.size() - 1).positionX, mPressedPoints.get(mPressedPoints.size() - 1).positionY)) {
            return;
        }

        double x = mPressedPoints.get(mPressedPoints.size() - 1).positionX - mFingerX;
        double y = mPressedPoints.get(mPressedPoints.size() - 1).positionY - mFingerY;
        double centerDistance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //dx = radius/两圆心距离*x
        double dx = mRadiusInner / centerDistance * x;
        //int dy =  radius/两圆心距离*y
        double dy = mRadiusInner / centerDistance * y;
        canvas.drawLine((float) (mPressedPoints.get(mPressedPoints.size() - 1).positionX - dx), (float) (mPressedPoints.get(mPressedPoints.size() - 1).positionY - dy), mFingerX, mFingerY, mPaintPressed);
    }

    private void drawLine(Canvas canvas) {
        if (mPressedPoints.size() < 2) {
            return;
        }
        for (int i = 1; i < mPressedPoints.size(); i++) {
            //绘制前一个圆和当前的圆的连线
            //注意符号 不要调用绝对值函数了 因为手势可以从左上向右下 也可以反过来，这时是+dx dy还是-dx dy值得考虑
            double x = mPressedPoints.get(i - 1).positionX - mPressedPoints.get(i).positionX;
            double y = mPressedPoints.get(i - 1).positionY - mPressedPoints.get(i).positionY;
            double centerDistance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            //dx = radius/两圆心距离*x
            double dx = mRadiusInner / centerDistance * x;
            //int dy =  radius/两圆心距离*y
            double dy = mRadiusInner / centerDistance * y;
            if (mPressedPoints.get(i).status == PointStatus.NORMAL) {
                return;
            }
            canvas.drawLine((float) (mPressedPoints.get(i - 1).positionX - dx), (float) (mPressedPoints.get(i - 1).positionY - dy), (float) (mPressedPoints.get(i).positionX + dx), (float) (mPressedPoints.get(i).positionY + dy),
                    mPressedPoints.get(i).status == PointStatus.PRESSED ? mPaintPressed : mPaintError);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mNeedIntercept) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFingerX = event.getX();
                mFingerY = event.getY();
                traversingCheckInPoints(mFingerX, mFingerY);
                return true;
            case MotionEvent.ACTION_MOVE:
                mFingerX = event.getX();
                mFingerY = event.getY();
                traversingCheckInPoints(mFingerX, mFingerY);
                break;
            case MotionEvent.ACTION_UP:
                mNeedIntercept = true;
                mFingerX = -1;
                mFingerY = -1;
                //checkPassWord(); 检查密码应该交由外部比较
                StringBuilder passBuffer = new StringBuilder();//拼凑密码
                for (Point point : mPressedPoints) {
                    passBuffer.append(point.index);
                }
                mPassListener.notifyPass(passBuffer.toString());
                //抬起手指后两秒无法操作

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mNeedIntercept = false;
                    }
                }, 2000);
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    //不应该在控件内部判断密码 维持控件的纯洁度
//    private void checkPassWord() {
//        mNeedIntercept = true;
//        clearStatusDelay(2000);
//        //如果密码太短 弹出提示 并将所有点状态跟新为error 设置屏幕无法触碰 两秒后清空状态
//        if (mPressedPoints.size() <= 4) {
//            Toast.makeText(getContext(), "密码太短！", Toast.LENGTH_SHORT).show();
//            for (Point point : mPressedPoints) {
//                point.setStatus(PointStatus.ERROR);
//            }
//            return;
//        }
//
//        StringBuffer passBuffer = new StringBuffer();//拼凑密码
//        for (Point point : mPressedPoints) {
//            passBuffer.append(point.index);
//        }
//        //如果密码正确 弹出提示 设置屏幕无法触碰 两秒之后清空状态
//        Log.d("TAG", "checkPassWord: mPassWord " + mPassWord + " passBuffer " + passBuffer);
//        if (passBuffer.toString().equals(mPassWord)) {
//            Toast.makeText(getContext(), "密码正确！", Toast.LENGTH_SHORT).show();
//        } else {//如果密码错误 弹出提示 并将所有点状态跟新为error 设置屏幕无法触碰 两秒后清空状态
//            Toast.makeText(getContext(), "密码错误！", Toast.LENGTH_SHORT).show();
//            for (Point point : mPressedPoints) {
//                point.setStatus(PointStatus.ERROR);
//            }
//        }
//    }

    private void drawCircle(Canvas canvas) {
        for (Point[] pointArr : mPoints) {
            for (Point point : pointArr) {
                switch (point.getStatus()) {
                    case PointStatus.NORMAL:
                        canvas.drawCircle(point.positionX, point.positionY, mRadiusOut, mPaintNormal);
                        canvas.drawCircle(point.positionX, point.positionY, mRadiusInner, mPaintNormal);
                        break;
                    case PointStatus.PRESSED:
                        canvas.drawCircle(point.positionX, point.positionY, mRadiusOut, mPaintPressed);
                        canvas.drawCircle(point.positionX, point.positionY, mRadiusInner, mPaintPressed);
                        break;
                    case PointStatus.ERROR:
                        canvas.drawCircle(point.positionX, point.positionY, mRadiusOut, mPaintError);
                        canvas.drawCircle(point.positionX, point.positionY, mRadiusInner, mPaintError);
                        break;
                }
            }
        }
    }

    private void intPaint() {
        mRadiusOut = getMeasuredWidth() / 10;
        mRadiusInner = mRadiusOut / 4;
        mPaintNormal = getPaintByColor(0xFFFFFFFF);
        mPaintPressed = getPaintByColor(0xFFCCFFFF);
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
        mPoints[0][0] = new Point(getMeasuredWidth() / 5, getMeasuredWidth() / 5, 0, PointStatus.NORMAL);
        mPoints[0][1] = new Point(getMeasuredWidth() * 2 / 4, getMeasuredWidth() / 5, 1, PointStatus.NORMAL);
        mPoints[0][2] = new Point(getMeasuredWidth() * 4 / 5, getMeasuredWidth() / 5, 2, PointStatus.NORMAL);
        //第二行
        mPoints[1][0] = new Point(getMeasuredWidth() / 5, getMeasuredWidth() / 2, 3, PointStatus.NORMAL);
        mPoints[1][1] = new Point(getMeasuredWidth() * 2 / 4, getMeasuredWidth() / 2, 4, PointStatus.NORMAL);
        mPoints[1][2] = new Point(getMeasuredWidth() * 4 / 5, getMeasuredWidth() / 2, 5, PointStatus.NORMAL);
        //第三行
        mPoints[2][0] = new Point(getMeasuredWidth() / 5, getMeasuredWidth() * 4 / 5, 6, PointStatus.NORMAL);
        mPoints[2][1] = new Point(getMeasuredWidth() * 2 / 4, getMeasuredWidth() * 4 / 5, 7, PointStatus.NORMAL);
        mPoints[2][2] = new Point(getMeasuredWidth() * 4 / 5, getMeasuredWidth() * 4 / 5, 8, PointStatus.NORMAL);
    }

    void traversingCheckInPoints(float fingerX, float fingerY) {
        for (Point[] pointArr : mPoints) {
            for (Point point : pointArr) {
                if (isFingerInCircle(fingerX, fingerY, point.positionX, point.positionY)) {
                    point.setStatus(PointStatus.PRESSED);
                    if (!mPressedPoints.contains(point)) {
                        mPressedPoints.add(point);
                    }
                    //Log.d("TAG", "traversingCheckInPoints: in!!");
                    return;
                }
            }
        }
    }

    boolean isFingerInCircle(float fingerX, float fingerY, int circleX, int circleY) {//初中知识 判断点到圆心的距离
        //Log.d("TAG", "traversingCheckInPoints: fingerX->"+fingerX+" fingerY "+fingerY+" circleX "+circleX+" circleY "+circleY);
        float dx = Math.abs(fingerX - circleX);
        float dy = Math.abs(fingerY - circleY);
        return Math.sqrt(dx * dx + dy * dy) - mRadiusOut < 0;
    }

    public void setPointsStatus(@PointStatus int pointsStatus) {
        for (LockPatternView.Point point : mPressedPoints) {
            point.setStatus(pointsStatus);
        }
        invalidate();
    }

    public void resetLockPatternView() {
        setPointsStatus(PointStatus.NORMAL);
        mPressedPoints.clear();
    }

    public void setPassListener(PassListener mPassListener) {
        this.mPassListener = mPassListener;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PointStatus.NORMAL, PointStatus.PRESSED, PointStatus.ERROR})
    public @interface PointStatus {
        int NORMAL = 0;
        int PRESSED = 1;
        int ERROR = 2;
    }

    interface PassListener {
        void notifyPass(String pass);
    }

    static class Point {
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

        int getStatus() {
            return status;
        }

        void setStatus(@PointStatus int status) {
            this.status = status;
        }
    }
}
