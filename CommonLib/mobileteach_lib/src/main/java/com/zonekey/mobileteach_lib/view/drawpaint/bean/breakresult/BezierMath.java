package com.zonekey.mobileteach_lib.view.drawpaint.bean.breakresult;

import android.graphics.PointF;

/**
 * Created by xu.wang
 * Date on  2017/11/8 11:53:11.
 *
 * @Desc 记录Bezier信息的java bean
 */

public class BezierMath {
    private float t = -1;    //贝塞尔公式中的t
    private PointF bezierP;   //控制点
    private float distance; //距离

    public BezierMath(float t, PointF bezierP, float distance) {
        this.t = t;
        this.bezierP = bezierP;
        this.distance = distance;
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    public PointF getBezierP() {
        return bezierP;
    }

    public void setBezierP(PointF bezierP) {
        this.bezierP = bezierP;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
