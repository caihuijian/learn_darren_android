package com.example.likesview;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by hjcai on 2021/1/13.
 */
class BezierTypeEvaluator implements TypeEvaluator<PointF> {
    /**
     * This function returns the result of linearly interpolating the start and end values, with
     * <code>fraction</code> representing the proportion between the start and end values. The
     * calculation is a simple parametric calculation:
     * 注意下面的这段描述 计算结果result = x0 + t * (x1 - x0)
     * 其中x0代表起始值 x1代表终点值 t代表比值
     * <code>result = x0 + t * (x1 - x0)</code>,
     * where <code>x0</code> is <code>startValue</code>, <code>x1</code> is <code>endValue</code>,
     * and <code>t</code> is <code>fraction</code>.
     *
     * @param fraction   The fraction from the starting to the ending values
     *                   从起点到终点的比值
     * @param startValue The start value.
     *                   起始点的值
     * @param endValue   The end value.
     *                   终点的值
     * @return A linear interpolation between the start and end values, given the
     * <code>fraction</code> parameter.
     * 此函数的意义是计算某个比值fraction时 中间点的状态（位置） 其中fraction的区间为[0,1] 比如fraction也可以理解为百分比,为0.5表示一半的意思
     */
    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
//        float pointX = startValue.x + (endValue.x - startValue.x) * fraction;
//        float pointY = startValue.y + (endValue.y - startValue.y) * fraction;
//        return new PointF(pointX, pointY);
        return null;
    }
}
