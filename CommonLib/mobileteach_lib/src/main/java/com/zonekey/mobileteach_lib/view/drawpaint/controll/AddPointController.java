package com.zonekey.mobileteach_lib.view.drawpaint.controll;

import android.graphics.PointF;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.MobileTeachApi;
import com.zonekey.mobileteach_lib.MobileTeachConfig;
import com.zonekey.mobileteach_lib.util.Build32Code;
import com.zonekey.mobileteach_lib.util.LogUtil;
import com.zonekey.mobileteach_lib.view.drawpaint.PaintConfig;
import com.zonekey.mobileteach_lib.view.drawpaint.PaintAttacher;
import com.zonekey.mobileteach_lib.view.drawpaint.bean.shape.LineInfo;
import com.zonekey.mobileteach_lib.view.drawpaint.util.ColorUtil;
import com.zonekey.mobileteach_lib.view.drawpaint.util.PaintMathUtils;
import com.zonekey.mobileteach_lib.view.drawpaint.util.TransPointF;

/**
 * Created by xu.wang
 * Date on  2017/11/8 17:40:19.
 *
 * @Desc 根据触摸事件, 按逻辑增加点到list的控制类
 */

public class AddPointController {
    private final String TAG = "AddPointController";
    private int currentColor; //记录画笔颜色
    private int currentPaintSize; //记录画笔粗细
    private PaintAttacher mAttacher;
    private int currentPoint = 1;   //当前是本次绘制的第几个点

    public AddPointController(PaintAttacher attacher, int currentColor, int currentPaintSize) {
        this.mAttacher = attacher;
        this.currentColor = currentColor;
        this.currentPaintSize = currentPaintSize;
    }

    /**
     * 贝塞尔模式下储存的数据结构:
     * ---------  第1个点-------   第2个点-------        第三个点   -----向后以次类推
     * startP:      p0             p0,               (p0 + p1) /2
     * ctrlP:       p0             p0                     p1
     * endP:        p0          (p0 + p1) /2         (p1 + p2) / 2
     *
     * @param lineInfo
     * @param transPointF
     * @param downX
     * @param downY
     */
    public void actionDown(LineInfo lineInfo, TransPointF transPointF, float downX, float downY) {
        lineInfo.setLineId(Build32Code.createGUID());
        if (mAttacher.getDrawState() == PaintAttacher.DrawState.BEZIER || mAttacher.getDrawState() == PaintAttacher.DrawState.PEN_YINGGUANG) {
            if (mAttacher.getDrawState() == PaintAttacher.DrawState.BEZIER) {
                lineInfo.setColor(currentColor);
                lineInfo.setStrokeWidth(currentPaintSize);
                if (lineInfo.getPointLists().size() != 0) { //如果是第一个点的话,三个点都是原点
                    LogUtil.writeLog(TAG, "actionDown the pointList.size != 0");
                }
                PointF tempStartP;
                PointF tempCtrlP;
                PointF tempEndP;
                tempEndP = tempCtrlP = tempStartP = transPointF.display2Logic(downX, downY);
                lineInfo.getPointLists().add(tempStartP);  //startPoint,起始点
                lineInfo.getPointLists().add(tempCtrlP);  //ctrlPoint,控制点
                lineInfo.getPointLists().add(tempEndP);  //endPoint,结束点
                if (MobileTeach.CurrentApp == MobileTeachConfig.App_Teachmaster) {    //是智课助手
                    mAttacher.getPaintInfo2Pc().sendDownInfo(lineInfo.getLineId(), MobileTeachApi.PresentControl.QuadraticBezier, currentColor, currentPaintSize,
                            transPointF.display2logicX(downX), transPointF.display2logicY(downY));
                } else {
                    mAttacher.getPaintInfo2Pc().sendDownInfo(lineInfo.getLineId(), MobileTeachApi.PresentControl.QuadraticBezier, currentColor, currentPaintSize,
                            tempStartP, tempCtrlP, tempEndP);
                }
            } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.PEN_YINGGUANG) {
                int currentAlphaColor = ColorUtil.convertColorToString(currentColor, PaintConfig.mYingGuangAlhpa);
                lineInfo.setColor(currentAlphaColor);
                lineInfo.setStrokeWidth(PaintConfig.mYingGuangSize);
                PointF tempStartP;
                PointF tempCtrlP;
                PointF tempEndP;
                tempEndP = tempCtrlP = tempStartP = transPointF.display2Logic(downX, downY);
                lineInfo.getPointLists().add(tempStartP);  //startPoint,起始点
                lineInfo.getPointLists().add(tempCtrlP);  //ctrlPoint,控制点
                lineInfo.getPointLists().add(tempEndP);  //endPoint,结束点
                if (MobileTeach.CurrentApp == MobileTeachConfig.App_Teachmaster) {
                    mAttacher.getPaintInfo2Pc().sendDownInfo(lineInfo.getLineId(), MobileTeachApi.PresentControl.QuadraticBezier, currentAlphaColor, PaintConfig.mYingGuangSize,
                            transPointF.display2logicX(downX), transPointF.display2logicY(downY));
                } else {
                    mAttacher.getPaintInfo2Pc().sendDownInfo(lineInfo.getLineId(), MobileTeachApi.PresentControl.QuadraticBezier, currentAlphaColor, PaintConfig.mYingGuangSize,
                            tempStartP, tempCtrlP, tempEndP);
                }
            }

        } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.ARROW || mAttacher.getDrawState() == PaintAttacher.DrawState.ELLIPSE || mAttacher.getDrawState() == PaintAttacher.DrawState.RECTANGLE
                || mAttacher.getDrawState() == PaintAttacher.DrawState.TIANZHIGE || mAttacher.getDrawState() == PaintAttacher.DrawState.MIZIGE || mAttacher.getDrawState() == PaintAttacher.DrawState.SIXIANGE) {
            lineInfo.setColor(currentColor);
            lineInfo.setStrokeWidth(currentPaintSize);
            lineInfo.getPointLists().add(transPointF.display2Logic(downX, downY));
            String drawType = MobileTeachApi.PresentControl.PAINT_ARROW_LINE;
            switch (mAttacher.getDrawState()) {
                case ARROW:
                    lineInfo.setType(4);
                    drawType = MobileTeachApi.PresentControl.PAINT_ARROW_LINE;
                    break;
                case ELLIPSE:
                    lineInfo.setType(3);
                    drawType = MobileTeachApi.PresentControl.PAINT_ELLIPSE;
                    break;
                case RECTANGLE:
                    lineInfo.setType(2);
                    drawType = MobileTeachApi.PresentControl.PAINT_RECTANGLE;
                    break;
                case TIANZHIGE:
                    lineInfo.setType(5);
                    drawType = MobileTeachApi.PresentControl.PAINT_TIANZIGE;
                    break;
                case MIZIGE:
                    lineInfo.setType(6);
                    drawType = MobileTeachApi.PresentControl.PAINT_MIZIGE;
                    break;
                case SIXIANGE:
                    lineInfo.setType(7);
                    drawType = MobileTeachApi.PresentControl.PAINT_SiXianGE;
                    break;
            }
            mAttacher.getPaintInfo2Pc().sendDownInfo(lineInfo.getLineId(), drawType, currentColor, currentPaintSize,
                    transPointF.display2logicX(downX), transPointF.display2logicY(downY));
        } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.POLYLINE) {
            lineInfo.setType(8);
            lineInfo.setColor(currentColor);
            lineInfo.setStrokeWidth(currentPaintSize);
            mAttacher.getPaintInfo2Pc().sendDownInfo(lineInfo.getLineId(), MobileTeachApi.PresentControl.PAINT_POLY_LINE, currentColor, currentPaintSize,
                    transPointF.display2logicX(downX), transPointF.display2logicY(downY));
        }
    }

    public void actionMove(LineInfo lineInfo, TransPointF transPointF, float preX, float preY, float moveX, float moveY) {
        if (lineInfo == null) return;
        if (mAttacher.getDrawState() == PaintAttacher.DrawState.BEZIER || mAttacher.getDrawState() == PaintAttacher.DrawState.PEN_YINGGUANG) {
            PointF tempStartP;
            PointF tempCtrlP;
            PointF tempEndP;
            if (lineInfo.getPointLists().size() == 0) { //如果是第一个点的话,三个点都是原点
                tempEndP = tempCtrlP = tempStartP = transPointF.display2Logic(moveX, moveY);
            } else if (lineInfo.getPointLists().size() == 3) {
                tempCtrlP = tempStartP = lineInfo.getPointLists().get(lineInfo.getPointLists().size() - 1);
                tempEndP = PaintMathUtils.getBesPoint(tempCtrlP, transPointF.display2Logic(moveX, moveY));
            } else {
                tempStartP = lineInfo.getPointLists().get(lineInfo.getPointLists().size() - 1);
                tempCtrlP = transPointF.display2Logic(preX, preY);
                tempEndP = PaintMathUtils.getBesPoint(tempCtrlP, transPointF.display2Logic(moveX, moveY));
            }
            lineInfo.getPointLists().add(tempStartP);  //startPoint,起始点
            lineInfo.getPointLists().add(tempCtrlP);  //ctrlPoint,控制点
            lineInfo.getPointLists().add(tempEndP);  //endPoint,结束点
            if (MobileTeach.CurrentApp == MobileTeachConfig.App_Teachmaster) {
                mAttacher.getPaintInfo2Pc().sendMoveInfo(lineInfo.getLineId(), currentPoint,
                        tempCtrlP.x, tempCtrlP.y, tempEndP.x, tempEndP.y);
            } else {
                mAttacher.getPaintInfo2Pc().sendMoveInfo(lineInfo.getLineId(), currentPoint * 3, tempStartP, tempCtrlP, tempEndP);
            }

        } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.RECTANGLE || mAttacher.getDrawState() == PaintAttacher.DrawState.ARROW || mAttacher.getDrawState() == PaintAttacher.DrawState.ELLIPSE
                || mAttacher.getDrawState() == PaintAttacher.DrawState.TIANZHIGE || mAttacher.getDrawState() == PaintAttacher.DrawState.MIZIGE || mAttacher.getDrawState() == PaintAttacher.DrawState.SIXIANGE) {
            if (lineInfo.getPointLists().size() > 1) {
                lineInfo.getPointLists().set(1, mAttacher.getTransPointF().display2Logic(moveX, moveY));
            } else {
                lineInfo.getPointLists().add(mAttacher.getTransPointF().display2Logic(moveX, moveY));
            }
            mAttacher.getPaintInfo2Pc().setMoveShape(lineInfo.getLineId(), currentPoint, mAttacher.getTransPointF().display2logicX(moveX), mAttacher.getTransPointF().display2logicY(moveY));
        } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.POLYLINE) {
            lineInfo.getPointLists().add(mAttacher.getTransPointF().display2Logic(moveX, moveY));
            mAttacher.getPaintInfo2Pc().setMoveShape(lineInfo.getLineId(), currentPoint, mAttacher.getTransPointF().display2logicX(moveX), mAttacher.getTransPointF().display2logicY(moveY));
        }
        currentPoint++;
    }

    public void actionUp(LineInfo lineInfo, float upX, float upY) {
        if (MobileTeach.CurrentApp != MobileTeachConfig.App_Teachmaster)
            mAttacher.getPaintInfo2Pc().sendUpInfo(lineInfo.getLineId());
    }

    public void setColor(int color) {
        this.currentColor = color;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.currentPaintSize = strokeWidth;
    }


    public int getColor() {
        return currentColor;
    }
}
