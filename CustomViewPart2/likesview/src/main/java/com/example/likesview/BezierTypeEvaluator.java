package com.example.likesview;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by hjcai on 2021/1/13.
 * 用于产生三阶贝塞尔曲线
 */
class BezierTypeEvaluator implements TypeEvaluator<PointF> {
    private final PointF mControlPoint1;
    private final PointF mControlPoint2;


    public BezierTypeEvaluator(PointF controlPoint1, PointF controlPoint2) {
        mControlPoint1 = controlPoint1;
        mControlPoint2 = controlPoint2;
    }

    /**
     * This function returns the result of linearly interpolating the start and end values, with
     * <code>fraction</code> representing the proportion between the start and end values. The
     * calculation is a simple parametric calculation:
     * 注意下面的这段描述 计算结果result = x0 + fraction * (x1 - x0)
     * 其中x0代表起始值 x1代表终点值 t代表比值
     * <code>result = x0 + fraction * (x1 - x0)</code>,
     * where <code>x0</code> is <code>startPoint</code>, <code>x1</code> is <code>endPoint</code>,
     * and <code>fraction</code> is <code>fraction</code>.
     *
     * @param fraction   The fraction from the starting to the ending values
     *                   从起点到终点的比值
     * @param startPoint The start value.
     *                   起始点的值
     * @param endPoint   The end value.
     *                   终点的值
     * @return A linear interpolation between the start and end values, given the
     * <code>fraction</code> parameter.
     * 此函数的意义是计算某个比值fraction时 中间点的状态（位置） 其中fraction的区间为[0,1] 比如fraction也可以理解为百分比,为0.5表示一半的意思
     * <p>
     * <p>
     * 以上的解释为通用解释
     * 对于求三阶贝塞尔曲线的本方法而言
     * 该方法用于计算在特定 起点，终点，控制点1，控制点2时，fraction从0变化到1，返回的PointF连接起来将构成三阶贝塞尔曲线
     * 至于计算方法 直接套用百度百科的三阶贝塞尔曲线的公式即可 fraction即时百科中的t
     */
    @Override
    public PointF evaluate(float fraction, PointF startPoint, PointF endPoint) {
        PointF pointOnBezier = new PointF();
        pointOnBezier.x = (float) (Math.pow((1 - fraction), 3) * startPoint.x + 3 * mControlPoint1.x * fraction * Math.pow(1 - fraction, 2) + 3 * mControlPoint2.x * fraction * fraction * (1 - fraction) + endPoint.x * Math.pow(fraction, 3));
        pointOnBezier.y = (float) (Math.pow((1 - fraction), 3) * startPoint.y + 3 * mControlPoint1.y * fraction * Math.pow(1 - fraction, 2) + 3 * mControlPoint2.y * fraction * fraction * (1 - fraction) + endPoint.y * Math.pow(fraction, 3));
        return pointOnBezier;
    }
}
