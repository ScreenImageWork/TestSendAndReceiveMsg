package com.zonekey.mobileteach_lib.view.drawpaint.bean.breakresult;

import android.graphics.PointF;

/**
 * Created by xu.wang
 * Date on  2017/11/8 15:05:47.
 *
 * @Desc 打断代码使用, 记录startP, endP, ctrlP
 */

public class BezierPoint {
    private PointF startP;
    private PointF endP;
    private PointF ctrlP;

    public BezierPoint(PointF startP, PointF endP, PointF ctrlP) {
        this.startP = startP;
        this.ctrlP = ctrlP;
        this.endP = endP;
    }

    public PointF getStartP() {
        return startP;
    }

    public void setStartP(PointF startP) {
        this.startP = startP;
    }

    public PointF getEndP() {
        return endP;
    }

    public void setEndP(PointF endP) {
        this.endP = endP;
    }

    public PointF getCtrlP() {
        return ctrlP;
    }

    public void setCtrlP(PointF ctrlP) {
        this.ctrlP = ctrlP;
    }
}
