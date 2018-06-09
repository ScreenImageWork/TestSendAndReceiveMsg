package com.zonekey.mobileteach_lib.view.drawpaint.bean.breakresult;

import android.graphics.PointF;
import android.graphics.RectF;

import com.zonekey.mobileteach_lib.view.drawpaint.util.PaintMathUtils;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on  2017/11/7 10:59:30.
 *
 * @Desc 打断使用的Bean
 */

public class Bezier2 {
    private PointF startP;
    private PointF ctrlP;
    private PointF endP;
    private int strokeWidth;

    public Bezier2(PointF startP, PointF ctrlP, PointF endP) {
        this(startP, ctrlP, endP, 0);
    }

    public Bezier2(PointF startP, PointF ctrlP, PointF endP, int strokeWidth) {
        this.startP = startP;
        this.ctrlP = ctrlP;
        this.endP = endP;
        this.strokeWidth = strokeWidth;
    }

    /**
     * @param point      触摸点
     * @param er         触摸点得半径
     * @param startPoint 起始点
     * @param startPoint 控制点
     * @param endPoint   结束点
     * @param width      线宽
     * @return
     * @Desc 判断当前线条与触摸点是否碰撞
     */
    public static boolean isHit(PointF point, float er, PointF startPoint, PointF controlPoint, PointF endPoint, int width) {
        float left = Math.min(startPoint.x, endPoint.x);
        float right = Math.max(startPoint.x, endPoint.x);
        float top = Math.min(startPoint.y, endPoint.y);
        float bottom = Math.max(startPoint.y, endPoint.y);
        float squareLeft = point.x - width - er;
        float squareRight = point.x + width + er;
        float squareTop = point.y - width - er;
        float squareBottom = point.y + width + er;
        float startX = -1, endX = -1;
        float startY = -1, endY = -1;
        if ((squareLeft < left || squareLeft == left) && (squareRight > left && (squareRight < right || squareRight == right))) {
            startX = left;
            endX = squareRight;
        } else if ((squareLeft > left) && (squareRight < right || squareRight == right)) {
            startX = squareLeft;
            endX = squareRight;
        } else if ((squareLeft > left && (squareLeft < right || squareLeft == right)) && squareRight > right) {
            startX = squareLeft;
            endX = right;
        }
        if ((squareTop < top || squareTop == top) && (squareBottom > top && (squareBottom < bottom || squareBottom == bottom))) {
            startY = top;
            endY = squareBottom;
        } else if (squareTop > top && (squareBottom < bottom || squareBottom == bottom)) {
            startY = squareTop;
            endY = squareBottom;
        } else if ((squareTop > top && (squareTop < bottom || squareTop == bottom)) && squareBottom > bottom) {
            startY = squareTop;
            endY = bottom;
        }
        if ((startX != -1 && endX != -1) || (startY != -1 && endY != -1)) {
            return getPoint(point, er, startX, endX, startY, endY, startPoint, controlPoint, endPoint, width) == null ? false : true;
        }
        if (er == 0) {
            return (PaintMathUtils.getDistance(point, startPoint) <= width / 2) || (PaintMathUtils.getDistance(point, endPoint) <= width / 2);
        } else {
            return (PaintMathUtils.getDistance(point, startPoint) < (width / 2 + er)) || (PaintMathUtils.getDistance(point, endPoint) < (width / 2 + er));
        }
    }

    /**
     * @param point        触摸点
     * @param er           触摸点的半径
     * @param startXVal    触碰生成Rect区域的 startX
     * @param endXVal      触碰生成Rect区域的 endX
     * @param startYVal    触碰生成Rect区域的 startY
     * @param endYVal      触碰生成Rect区域的 endY
     * @param startPoint   起始点
     * @param controlPoint 控制点
     * @param endPoint     结束点
     * @param width        线宽
     * @return startX ,startY-------------------
     * |                               |
     * |                               |
     * |---------------------- endX,endY
     * @Desc 获得触摸区域与线条相交的点
     */
    private static PointF getPoint(PointF point, float er, float startXVal, float endXVal, float startYVal, float endYVal,
                                   PointF startPoint, PointF controlPoint, PointF endPoint, int width) {
        float d = width / 2 + er + 10;
        for (float x = startXVal; x < endXVal; x++) {
            float t;
            float a = (startPoint.x - 2 * controlPoint.x + endPoint.x);
            float b = 2 * controlPoint.x - 2 * startPoint.x;
            float c = startPoint.x - x;
            if (a == 0) {
                t = -c / b;
            } else {
                float t1 = (float) ((-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
                float t2 = (float) ((-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
                if ((t1 < 1 || t1 == 1) && (t1 > 0 || t1 == 0))
                    t = t1;
                else
                    t = t2;
            }
            float y = (float) (Math.pow((1 - t), 2) * startPoint.y + 2 * t * (1 - t) * controlPoint.y + Math.pow(t, 2) * endPoint.y);
            PointF p = new PointF(x, y);
            float distance = PaintMathUtils.getDistance(point, p);
            if (distance < (width / 2 + er) || distance == (width / 2 + er)) {
                return p;
            }
        }
        for (float y = startYVal; y < endYVal; y++) {
            float t;
            float a = (startPoint.y - 2 * controlPoint.y + endPoint.y);
            float b = 2 * controlPoint.y - 2 * startPoint.y;
            float c = startPoint.y - y;
            if (a == 0) {
                t = -c / b;
            } else {
                float t1 = (float) ((-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
                float t2 = (float) ((-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
                if ((t1 < 1 || t1 == 1) && (t1 > 0 || t1 == 0))
                    t = t1;
                else
                    t = t2;
            }
            float x = (float) (Math.pow((1 - t), 2) * startPoint.x + 2 * t * (1 - t) * controlPoint.x + Math.pow(t, 2) * endPoint.x);
            PointF p = new PointF(x, y);
            float distance = PaintMathUtils.getDistance(point, p);
            if (distance < (width / 2 + er) || distance == (width / 2 + er)) {
                return p;
            }
        }
        return null;
    }

    /**
     * @param touchP
     * @param touchRadius
     * @return
     * @Desc 打断线条的方法
     */
    public BezierResult breakBezier(PointF touchP, float touchRadius) {
        ArrayList<BezierMath> mathLists = new ArrayList<>();

        RectF rectF = new RectF();      //触摸点 ,能碰撞的有效区域的矩形
        rectF.left = touchP.x - strokeWidth - touchRadius;
        rectF.right = touchP.x + strokeWidth + touchRadius;
        rectF.top = touchP.y - strokeWidth - touchRadius;
        rectF.bottom = touchP.y + strokeWidth + touchRadius;

        int radius = (int) (touchRadius + strokeWidth / 2);
        Triangle triangle = getTriangle(startP, ctrlP, endP, strokeWidth);   //创建三角形
        if (PaintMathUtils.isInCircle(triangle.startPoint, radius, touchP) && PaintMathUtils.isInCircle(triangle.endPoint, radius, touchP)
                && PaintMathUtils.isInCircle(triangle.topPoint, radius, touchP)) {
            return null;
        }

        for (float reacFX = rectF.left; reacFX < rectF.right; reacFX++) {   //遍历碰撞区域内x方向的点
            float[] bezierT = PaintMathUtils.getBezierT(reacFX, startP.x, ctrlP.x, endP.x);//将rectFX 代入根据Bezier公式求T
            if (bezierT.length == 0) {
                continue;
            } else if (bezierT.length == 1) {
                BezierMath bezierMath = isAvailableBezier(bezierT[0], reacFX, touchP, touchRadius);   //根据t判断是否是正确的触摸点坐标
                if (bezierMath != null) mathLists.add(bezierMath);
            } else if (bezierT.length == 2) {
                BezierMath bezierMath1 = isAvailableBezier(bezierT[0], reacFX, touchP, touchRadius);  //根据t判断是否是正确的触摸点坐标
                if (bezierMath1 != null) mathLists.add(bezierMath1);
                BezierMath bezierMath2 = isAvailableBezier(bezierT[1], reacFX, touchP, touchRadius);  //根据t判断是否是正确的触摸点坐标
                if (bezierMath2 != null) mathLists.add(bezierMath2);
            }
        }

        for (float reacFY = rectF.top; reacFY < rectF.bottom; reacFY++) {   //遍历碰撞区域y方向的点
            float[] bezierT = PaintMathUtils.getBezierT(reacFY, startP.y, ctrlP.y, endP.y);
            if (bezierT.length == 0) {
                continue;
            } else if (bezierT.length == 1) {
                BezierMath bezierMath = isAvailableBezier(bezierT[0], reacFY, touchP, touchRadius);   //根据t判断是否是正确的触摸点坐标
                if (bezierMath != null) mathLists.add(bezierMath);
            } else if (bezierT.length == 2) {
                BezierMath bezierMath1 = isAvailableBezier(bezierT[0], reacFY, touchP, touchRadius);   //根据t判断是否是正确的触摸点坐标
                if (bezierMath1 != null) mathLists.add(bezierMath1);
                BezierMath bezierMath2 = isAvailableBezier(bezierT[1], reacFY, touchP, touchRadius);   //根据t判断是否是正确的触摸点坐标
                if (bezierMath2 != null) mathLists.add(bezierMath2);
            }
        }
        if (mathLists.size() <= 0) { //检测出了触碰区域内有点在贝塞尔曲线上
            return null;
        }
        if (PaintMathUtils.isInCircle(touchP, radius, startP) && !PaintMathUtils.isInCircle(touchP, radius, endP)) {
            return breakFromHead(mathLists.get(0)); // 起始点在碰撞区域中,结束点不在区域,从头部打断
        } else if ((!PaintMathUtils.isInCircle(touchP, radius, startP) && PaintMathUtils.isInCircle(touchP, radius, endP))) {
            return breakFromFoot(mathLists.get(0));  //结束点在触碰区域中,起始点不在打断区域,从脚部打断
        } else if (mathLists.size() > 1) {
            return breakFromMiddle(mathLists);  //从中间打断
        } else {
            return null;
        }
    }

    /**
     * @param mathLists
     * @return
     * @Desc 从中间打断
     */
    private BezierResult breakFromMiddle(ArrayList<BezierMath> mathLists) {
        BezierMath result1 = null;
        BezierMath result2 = null;
        float distance = 0;
        for (int i = 0; i < mathLists.size() - 1; i++) {
            for (int j = i + 1; j < mathLists.size(); j++) {
                float d = PaintMathUtils.getDistance(mathLists.get(i).getBezierP(), mathLists.get(j).getBezierP());
                if (d > distance) {
                    distance = d;
                    result1 = mathLists.get(i);
                    result2 = mathLists.get(j);
                }
            }
        }

        if (result1 == null || result1.getT() == -1 || result2 == null || result2.getT() == -1) {
            return null;
        }
        BezierMath pointInfo1, pointInfo2;
        float t1 = result1.getT();
        float t2 = result2.getT();

        PointF point1;
        PointF point2;
        if (t1 < t2) {
            pointInfo1 = result1;
            pointInfo2 = result2;
        } else {
            pointInfo1 = result2;
            pointInfo2 = result1;
        }
        t1 = pointInfo1.getT();
        point1 = pointInfo1.getBezierP();

        t2 = pointInfo2.getT();
        point2 = pointInfo2.getBezierP();

        PointF cp1 = new PointF();
        cp1.x = (1 - t1) * startP.x + t1 * ctrlP.x;
        cp1.y = (1 - t1) * startP.y + t1 * ctrlP.y;

        PointF cp2 = new PointF();
        cp2.x = (1 - t2) * ctrlP.x + t2 * endP.x;
        cp2.y = (1 - t2) * ctrlP.y + t2 * endP.y;
        BezierResult bezierResult = new BezierResult();
        bezierResult.setBreakType(BezierResult.BREAK_FROM_MIDDLE);
        bezierResult.setList(new ArrayList<BezierPoint>());
        bezierResult.getList().add(new BezierPoint(startP, cp1, point1));
        bezierResult.getList().add(new BezierPoint(point2, cp2, endP));
        return bezierResult;
    }

    /**
     * @param bezierMath
     * @return
     * @Desc 从脚部打断
     */

    private BezierResult breakFromFoot(BezierMath bezierMath) {
        BezierResult bezierResult = new BezierResult();
        bezierResult.setBreakType(BezierResult.BREAK_FROM_FOOT);
        bezierResult.setList(new ArrayList<BezierPoint>());
        float t = bezierMath.getT();
        PointF cp = new PointF();
        cp.x = (1 - t) * startP.x + t * ctrlP.x;
        cp.y = (1 - t) * startP.y + t * ctrlP.y;
        //  返回的startP 是startP ,ctrlP 是cp, endP是bezierP
        bezierResult.getList().add(new BezierPoint(startP, cp, bezierMath.getBezierP()));
        return bezierResult;
    }

    /**
     * @param bezierMath
     * @return
     * @Desc 从头部打断
     */
    private BezierResult breakFromHead(BezierMath bezierMath) {
        BezierResult bezierResult = new BezierResult();
        bezierResult.setBreakType(BezierResult.BREAK_FROM_HEAD);
        bezierResult.setList(new ArrayList<BezierPoint>());
        float t = bezierMath.getT();
        PointF tempCtrlP = new PointF();
        tempCtrlP.x = (1 - t) * ctrlP.x + t * endP.x;
        tempCtrlP.y = (1 - t) * ctrlP.y + t * endP.y;
        //返回的startP 是bezierP ,ctrlP 是cp, endP是endP
        bezierResult.getList().add(new BezierPoint(bezierMath.getBezierP(), tempCtrlP, endP));
        return bezierResult;
    }

    /**
     * @param t           贝塞尔公式的t
     * @param rectValue   用于检测的坐标
     * @param touchP      碰撞的中心点
     * @param touchRadius 碰撞的半径
     * @return 当前点是否在贝赛尔曲线上, 不为null返回贝塞尔计算相关的信息, 反之说明不在曲线上
     * @Desc 获得根据当前触摸点坐标得到用于计算的贝塞尔数学对象
     */
    private BezierMath isAvailableBezier(float t, float rectValue, PointF touchP, float touchRadius) {
        float y = PaintMathUtils.getBezierValue(t, startP.y, ctrlP.y, endP.y);
        PointF temp = new PointF(rectValue, y);
        float distance = PaintMathUtils.getDistance(touchP, temp);  //求当前点与触摸点中心的距离
        boolean hit = isHit(temp, touchRadius, startP, ctrlP, endP, strokeWidth);   //该点s否碰撞
        if (hit && (Math.abs(distance - (strokeWidth / 2 + touchRadius)) < 1) && distance > touchRadius) {
            return new BezierMath(t, temp, distance);
        }
        return null;
    }

    public static Triangle getTriangle(PointF startPoint, PointF controlPoint, PointF endPoint, float width) {
        PointF topPoint = new PointF();
        topPoint.x = (float) (Math.pow(0.5, 2) * startPoint.x + 2 * 0.5 * 0.5 * controlPoint.x + Math.pow(0.5, 2) * endPoint.x);
        topPoint.y = (float) (Math.pow(0.5, 2) * startPoint.y + 2 * 0.5 * 0.5 * controlPoint.y + Math.pow(0.5, 2) * endPoint.y);

        PointF centerPoint = new PointF();
        centerPoint.x = (startPoint.x + endPoint.x + topPoint.x) / 3;
        centerPoint.y = (startPoint.y + endPoint.y + topPoint.y) / 3;

        PointF startPoint_ = new PointF(), endPoint_ = new PointF(), topPoint_ = new PointF();
        float k1 = (width / 2) / (PaintMathUtils.getDistance(centerPoint, startPoint) + (width / 2));
        startPoint_.x = (startPoint.x - k1 * centerPoint.x) / (1 - k1);
        startPoint_.y = (startPoint.y - k1 * centerPoint.y) / (1 - k1);

        float k2 = (width / 2) / (PaintMathUtils.getDistance(centerPoint, endPoint) + (width / 2));
        endPoint_.x = (endPoint.x - k1 * centerPoint.x) / (1 - k2);
        endPoint_.y = (endPoint.y - k1 * centerPoint.y) / (1 - k2);

        float k3 = (width / 2) / (PaintMathUtils.getDistance(centerPoint, topPoint) + (width / 2));
        topPoint_.x = (topPoint.x - k3 * centerPoint.x) / (1 - k3);
        topPoint_.y = (topPoint.y - k3 * centerPoint.y) / (1 - k3);

        return new Triangle(startPoint_, endPoint_, topPoint_);

    }

    public static class Triangle {

        public PointF startPoint;
        public PointF endPoint;
        public PointF topPoint;

        public Triangle(PointF startPoint, PointF endPoint, PointF topPoint) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            this.topPoint = topPoint;
        }
    }

}
