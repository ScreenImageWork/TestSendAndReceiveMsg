package com.zonekey.mobileteach_lib.view.drawpaint.bean.breakresult;

import android.graphics.PointF;
import android.util.Log;

import com.zonekey.mobileteach_lib.view.drawpaint.util.PaintMathUtils;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by xu.wang
 * Date on  2017/11/15 17:15:41.
 *
 * @Desc
 */

public class Bezier4 {
    private PointF startPoint;
    private PointF controlPoint;
    private PointF endPoint;
    private int width;

    public Bezier4(PointF startP, PointF ctrlP, PointF endP) {
        this(startP, ctrlP, endP, 0);
    }

    public Bezier4(PointF startP, PointF ctrlP, PointF endP, int strokeWidth) {
        this.startPoint = startP;
        this.controlPoint = ctrlP;
        this.endPoint = endP;
        this.width = strokeWidth;
    }

    public boolean isHit(PointF point, float er) {
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

    public BezierResult breakBezier(PointF point, float er) {
        LinkedList<String> pointList = new LinkedList<>();

//        Circle eraser = new Circle(point, er + width / 2, 1);

        Log.e("CAH", "startPoint:" + startPoint + " endPoint:" + endPoint);

        PointF topPoint = new PointF();
        topPoint.x = (float) (0.25 * startPoint.x + 0.5 * controlPoint.x + 0.25 * endPoint.x);
        topPoint.y = (float) (0.25 * startPoint.y + 0.5 * controlPoint.y + 0.25 * endPoint.y);


        float squareLeft = point.x - width - er;
        float squareRight = point.x + width + er;
        float squareTop = point.y - width - er;
        float squareBottom = point.y + width + er;

        for (float x = squareLeft; x < squareRight; x++) {
            float t1 = 0, t2 = 0;
            float y1, y2;
            float a = startPoint.x - 2 * controlPoint.x + endPoint.x;
            float b = 2 * controlPoint.x - 2 * startPoint.x;
            float c = startPoint.x - x;

            PointF point1 = null;
            PointF point2 = null;

            if (a == 0) {
                t1 = -c / b;
                y1 = (float) (Math.pow((1 - t1), 2) * startPoint.y + 2 * t1 * (1 - t1) * controlPoint.y + Math.pow(t1, 2) * endPoint.y);
                point1 = new PointF(x, y1);
            } else {
                t1 = (float) ((-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
                t2 = (float) ((-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
                if ((t1 < 1 || t1 == 1) && (t1 > 0 || t1 == 0)) {
                    y1 = (float) (Math.pow((1 - t1), 2) * startPoint.y + 2 * t1 * (1 - t1) * controlPoint.y + Math.pow(t1, 2) * endPoint.y);
                    point1 = new PointF(x, y1);
                }

                if ((t2 < 1 || t2 == 1) && (t2 > 0 || t2 == 0)) {
                    y2 = (float) (Math.pow((1 - t2), 2) * startPoint.y + 2 * t2 * (1 - t2) * controlPoint.y + Math.pow(t2, 2) * endPoint.y);
                    point2 = new PointF(x, y2);
                }

            }

            float distance;
            if (point1 != null) {
                distance = PaintMathUtils.getDistance(point, point1);
                if (isHit(point1, er) && (Math.abs(distance - (width / 2 + er)) < 1) && distance > er) {
                    String pointStr = t1 + "," + point1.x + "," + point1.y + "," + PaintMathUtils.getDistance(point1, point);
                    pointList.add(pointStr);
                }
            }

            if (point2 != null) {
                distance = PaintMathUtils.getDistance(point, point2);
                if (isHit(point2, er) && (Math.abs(distance - (width / 2 + er)) < 1) && distance > er) {
                    String pointStr = t2 + "," + point2.x + "," + point2.y + "," + PaintMathUtils.getDistance(point2, point);
                    pointList.add(pointStr);
                }
            }

        }


        for (float y = squareTop; y < squareBottom; y++) {
            float t1, t2 = 0;
            float x1, x2;
            float a = startPoint.y - 2 * controlPoint.y + endPoint.y;
            float b = 2 * controlPoint.y - 2 * startPoint.y;
            float c = startPoint.y - y;

            PointF point1 = null, point2 = null;

            if (a == 0) {
                t1 = -c / b;
                x1 = (float) (Math.pow((1 - t1), 2) * startPoint.x + 2 * t1 * (1 - t1) * controlPoint.x + Math.pow(t1, 2) * endPoint.x);
                point1 = new PointF(x1, y);
            } else {
                t1 = (float) ((-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
                t2 = (float) ((-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
                if ((t1 < 1 || t1 == 1) && (t1 > 0 || t1 == 0)) {
                    x1 = (float) (Math.pow((1 - t1), 2) * startPoint.x + 2 * t1 * (1 - t1) * controlPoint.x + Math.pow(t1, 2) * endPoint.x);
                    point1 = new PointF(x1, y);
                }
                if ((t2 < 1 || t2 == 1) && (t2 > 0 || t2 == 0)) {
                    x2 = (float) (Math.pow((1 - t2), 2) * startPoint.x + 2 * t2 * (1 - t2) * controlPoint.x + Math.pow(t2, 2) * endPoint.x);
                    point2 = new PointF(x2, y);
                }

            }


            float distance;
            if (point1 != null) {
                distance = PaintMathUtils.getDistance(point, point1);
                if (isHit(point1, er) && (Math.abs(distance - (width / 2 + er)) < 1) && distance > er) {
                    String pointStr = t1 + "," + point1.x + "," + point1.y + "," + PaintMathUtils.getDistance(point1, point);
                    pointList.add(pointStr);
                }
            }

            if (point2 != null) {
                distance = PaintMathUtils.getDistance(point, point2);
                if (isHit(point2, er) && (Math.abs(distance - (width / 2 + er)) < 1) && distance > er) {
                    String pointStr = t2 + "," + point2.x + "," + point2.y + "," + PaintMathUtils.getDistance(point2, point);
                    pointList.add(pointStr);
                }
            }
        }


        if (pointList.size() > 0) {
            if ((PaintMathUtils.isInCircle(startPoint, (int) er, point) && !PaintMathUtils.isInCircle(endPoint, (int) er, point)) ||
                    (!PaintMathUtils.isInCircle(startPoint, (int) er, point) && PaintMathUtils.isInCircle(endPoint, (int) er, point))) {//起始点打断
                String[] pointInfo = pointList.get(0).split(",");
                float t = Float.parseFloat(pointInfo[0]);
                PointF point1 = new PointF(Float.parseFloat(pointInfo[1]), Float.parseFloat(pointInfo[2]));
                float d = Float.parseFloat(pointInfo[3]);

                for (int i = 1; i < pointList.size(); i++) {
                    pointInfo = pointList.get(i).split(",");
                    if (Float.parseFloat(pointInfo[3]) > d) {
                        t = Float.parseFloat(pointInfo[0]);
                        point1 = new PointF(Float.parseFloat(pointInfo[1]), Float.parseFloat(pointInfo[2]));
                        d = Float.parseFloat(pointInfo[3]);

                    }
                }

                PointF cp = new PointF();
                if (PaintMathUtils.isInCircle(point, (int) er, startPoint) && !PaintMathUtils.isInCircle(point, (int) er, endPoint)) {
                    cp.x = (1 - t) * controlPoint.x + t * endPoint.x;
                    cp.y = (1 - t) * controlPoint.y + t * endPoint.y;
                    BezierResult bezierResult = new BezierResult();
                    bezierResult.setBreakType(BezierResult.BREAK_FROM_HEAD);
                    bezierResult.setList(new ArrayList<BezierPoint>());
                    bezierResult.getList().add(new BezierPoint(point1, endPoint, cp));
                    return bezierResult;
                } else {
                    cp.x = (1 - t) * startPoint.x + t * controlPoint.x;
                    cp.y = (1 - t) * startPoint.y + t * controlPoint.y;

                    BezierResult bezierResult = new BezierResult();
                    bezierResult.setBreakType(BezierResult.BREAK_FROM_FOOT);
                    bezierResult.setList(new ArrayList<BezierPoint>());
                    bezierResult.getList().add(new BezierPoint(startPoint, point1, cp));
                    return bezierResult;
                }
            } else if (pointList.size() > 1) {
                String[] result1 = new String[4];
                String[] result2 = new String[4];

                float distance = 0;
                for (int k = 0; k < pointList.size() - 1; k++) {
                    PointF ePoint1 = new PointF(Float.parseFloat(pointList.get(k).split(",")[1]), Float.parseFloat(pointList.get(k).split(",")[2]));
                    for (int kk = k + 1; kk < pointList.size(); kk++) {
                        PointF ePoint2 = new PointF(Float.parseFloat(pointList.get(kk).split(",")[1]), Float.parseFloat(pointList.get(kk).split(",")[2]));
                        float d = PaintMathUtils.getDistance(ePoint1, ePoint2);
                        if (d > distance) {
                            distance = d;
                            result1 = pointList.get(k).split(",");
                            result2 = pointList.get(kk).split(",");
                        }
                    }
                }

                if (result1[0] != null && result2[0] != null) {
                    String[] pointInfo1, pointInfo2;
                    float t1 = Float.parseFloat(result1[0]);
                    float t2 = Float.parseFloat(result2[0]);

                    PointF point1;
                    PointF point2;

                    if (t1 < t2) {
                        pointInfo1 = result1;
                        pointInfo2 = result2;
                    } else {
                        pointInfo1 = result2;
                        pointInfo2 = result1;
                    }


                    t1 = Float.parseFloat(pointInfo1[0]);
                    point1 = new PointF(Float.parseFloat(pointInfo1[1]), Float.parseFloat(pointInfo1[2]));

                    t2 = Float.parseFloat(pointInfo2[0]);
                    point2 = new PointF(Float.parseFloat(pointInfo2[1]), Float.parseFloat(pointInfo2[2]));


                    PointF cp1 = new PointF();
                    cp1.x = (1 - t1) * startPoint.x + t1 * controlPoint.x;
                    cp1.y = (1 - t1) * startPoint.y + t1 * controlPoint.y;

                    PointF cp2 = new PointF();
                    cp2.x = (1 - t2) * controlPoint.x + t2 * endPoint.x;
                    cp2.y = (1 - t2) * controlPoint.y + t2 * endPoint.y;
                    BezierResult bezierResult = new BezierResult();
                    bezierResult.setBreakType(BezierResult.BREAK_FROM_MIDDLE);
                    bezierResult.setList(new ArrayList<BezierPoint>());
                    bezierResult.getList().add(new BezierPoint(startPoint, point1, cp1));
                    bezierResult.getList().add(new BezierPoint(point2, endPoint, cp2));
                    return bezierResult;

                }


            }

        }

        return null;
    }

    public PointF getPointF(PointF point, float er, float startXVal, float endXVal, float startYVal, float endYVal) {
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


    public static Bezier2.Triangle getTriangle(PointF startPoint, PointF controlPoint, PointF endPoint, float width) {
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

        return new Bezier2.Triangle(startPoint_, endPoint_, topPoint_);

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
