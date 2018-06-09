package com.zonekey.mobileteach_lib.view.drawpaint.util;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.zonekey.mobileteach_lib.view.drawpaint.bean.shape.LineInfo;
import com.zonekey.mobileteach_lib.view.drawpaint.bean.breakresult.Bezier;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on 2017/8/22 16:25
 * 擦除区域或者某一笔的工具类
 */

public class DeleteUtils {
    /**
     * 触摸时获得某一笔id的方法
     *
     * @param touchX      触摸x坐标
     * @param touchY      触摸y坐标
     * @param transPointF 转化坐标系
     * @param mPaintLines 所有绘制信息的集合
     * @return 如果检测到该Path与点击区域相交, 则返回该path的id, 否则返回null
     */
    public static String getTouchLineId(float touchX, float touchY, TransPointF transPointF, ArrayList<LineInfo> mPaintLines) {
        if (mPaintLines.size() < 1) return null;
        PointF touchP = transPointF.display2Logic(touchX, touchY);
        for (int i = mPaintLines.size() - 1; i >= 0; i--) {
            LineInfo lineInfo = mPaintLines.get(i);
            if (lineInfo.getIsDelete() == 1) continue;
            ArrayList<PointF> mPointFLists = lineInfo.getPointLists();
            for (int j = 0; j < mPointFLists.size() - 1; j++) {
                PointF startP = mPointFLists.get(j);
                PointF endP = mPointFLists.get(j + 1);
                PointF controlP = PaintMathUtils.getBesPoint(startP, endP);
                if (Bezier.isHit(touchP, 4, startP, controlP, endP, lineInfo.getStrokeWidth())) {
                    return lineInfo.getLineId();
                }
            }
        }
        return null;

    }

    /**
     * 删除区域内包含的所有曲线
     */

    public static ArrayList<LineInfo> deleteArea(float downX, float downY, float moveX, float moveY, ArrayList<LineInfo> mPaintLines, TransPointF transPointF) {
        if (downX == -1 || downY == -1 || moveX == -1 || moveY == -1) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        PointF startF = transPointF.display2Logic(downX, downY);
        PointF endF = transPointF.display2Logic(moveX, moveY);
        RectF rectF = PathFactory.createRectF(startF, endF);
        ArrayList<LineInfo> temp = new ArrayList<>();
        for (int i = mPaintLines.size() - 1; i >= 0; i--) {
            LineInfo lineInfo = mPaintLines.get(i);
            if (lineInfo.getIsDelete() == 1) continue;
            Path path = getLineInfoPath(lineInfo);
            if (path == null) continue;
            RectF bounds = new RectF();
            path.computeBounds(bounds, true);
            if (rectF.contains(bounds)) {
                sb.append(lineInfo.getLineId()).append(",");
                temp.add(lineInfo);
                mPaintLines.get(i).setIsDelete(1);
            }
        }
        return (temp == null || temp.size() == 0) ? null : temp;
    }

    /**
     * 获得某个类的形状
     *
     * @param lineInfo
     * @return
     */
    private static Path getLineInfoPath(LineInfo lineInfo) {
        if (lineInfo == null) return null;
        ArrayList<PointF> currentPointLists = lineInfo.getPointLists();
        if (currentPointLists.size() < 2) {
            return null;
        }
        Path path = new Path();
        PointF preP = currentPointLists.get(0);
        PointF ctrlP = currentPointLists.get(1);
        switch (lineInfo.getType()) {
            case 0:
                path = PathFactory.createBezier(lineInfo, null);
                break;
            case 5:
                if (preP == null || ctrlP == null) return null;
                path = PathFactory.createAllShiZhiGe(preP.x, preP.y, ctrlP.x, ctrlP.y);
                break;
            case 6:
                if (preP == null || ctrlP == null) return null;
                path = PathFactory.createAllMiZhiGe(preP.x, preP.y, ctrlP.x, ctrlP.y);
                break;
            case 7:
                if (preP == null || ctrlP == null) return null;
                path = PathFactory.creatAllSiXianGe(preP.x, preP.y, ctrlP.x, ctrlP.y);
                break;
            case 8:
                path = PathFactory.createPolyLine(lineInfo, null);
                break;
        }
        return path;
    }
}
