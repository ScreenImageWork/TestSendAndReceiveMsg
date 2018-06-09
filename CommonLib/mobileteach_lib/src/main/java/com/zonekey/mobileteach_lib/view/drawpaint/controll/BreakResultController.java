package com.zonekey.mobileteach_lib.view.drawpaint.controll;

import android.graphics.PointF;

import com.zonekey.mobileteach_lib.util.Build32Code;
import com.zonekey.mobileteach_lib.util.LogUtil;
import com.zonekey.mobileteach_lib.view.drawpaint.bean.breakresult.Bezier2;
import com.zonekey.mobileteach_lib.view.drawpaint.bean.breakresult.BezierPoint;
import com.zonekey.mobileteach_lib.view.drawpaint.bean.breakresult.BezierResult;
import com.zonekey.mobileteach_lib.view.drawpaint.bean.shape.LineInfo;
import com.zonekey.mobileteach_lib.view.drawpaint.util.TransPointF;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on  2017/11/6 10:41:03.
 *
 * @Desc 用于处理打断图形的controller
 */

public class BreakResultController {

    private final String TAG = "BreakResultController";

    /**
     * @param touchX      移动的x点
     * @param touchY      移动的y点
     * @param transPointF 转换坐标系
     * @param lists       所有线条的集合
     * @return
     * @Desc 如果被打断 则返回true,否则返回false  ,返回true的话会刷新整个View
     */
    public boolean onPointAdd(float touchX, float touchY, TransPointF transPointF, ArrayList<LineInfo> lists) {
        for (int i = 0; i < lists.size(); i++) {    //遍历每一条直线
            if (lists.get(i).getIsDelete() == 1) continue; //标记已删除的线条就不用参加运算了
            if (bezierIsHit(lists.get(i), transPointF.display2Origin(touchX, touchY), lists, i, transPointF)) {  //线被碰撞
                return true;
            }
        }
//        LogUtil.e(TAG, "atomInter =" + Bezier.count.get());
        return false;
    }

    /**
     * @param lineInfo    当前线条对象
     * @param touchP      触摸点
     * @param lists       所有线条的集合
     * @param position    当前线条在集合中的位置
     * @param transPointF
     * @return 返回true view会最终调用invalidate()去重新绘制所有线条,否则,不绘制
     * @Desc (假设在2这个位置打断成了线条, 则直接把原有位置2的线条remove掉, 然后add(2, lineInfo), add(3, lineInfo)),跳出循环,返回true即可
     */
    private boolean bezierIsHit(LineInfo lineInfo, PointF touchP, ArrayList<LineInfo> lists, int position, TransPointF transPointF) {
        ArrayList<PointF> pointLists = lineInfo.getPointLists();   //所有点的集合
        int width = lineInfo.getStrokeWidth();  //线宽
        for (int i = 0; i < pointLists.size(); i += 3) {
            if (i == 0 || i + 2 >= pointLists.size()) continue;
            PointF startP = transPointF.logic2Origin(pointLists.get(i));
            PointF ctrlP = transPointF.logic2Origin(pointLists.get(i + 1));
            PointF endP = transPointF.logic2Origin(pointLists.get(i + 2));
//
//            PointF startP = pointLists.get(i);
//            PointF ctrlP = pointLists.get(i + 1);
//            PointF endP = pointLists.get(i + 2);
//            Bezier bezier = new Bezier(startP, ctrlP, endP, width);
//            int state = Bezier.OHTER_BEZIER;
//            if (i < 3) state = Bezier.IS_FIRST_BEZIER;
//            if (pointLists.size() > 0 && i >= pointLists.size() - 3) state = Bezier.IS_LAST_BEZIER;
//            BezierResult bezierResult = bezier.breakBezier(touchP, 30, state);
            Bezier2 bezier = new Bezier2(startP, ctrlP, endP, width);
            BezierResult bezierResult = bezier.breakBezier(touchP, 30);
            if (bezierResult == null || bezierResult.getList() == null || bezierResult.getList().size() == 0 || bezierResult.getList().size() > 2) {
                continue; //如果不满足条件,跳过
            }
            int index = i + 3 >= pointLists.size() ? i : i + 3;
            if (bezierResult.getBreakType() == BezierResult.BREAK_FROM_HEAD) {
                for (int j = 0; j < index; j++) {   //去掉i 位置之前的所有点
                    if (j >= pointLists.size()) break;
                    pointLists.remove(j);
                }
                BezierPoint bezierPoint = bezierResult.getList().get(0);
                pointLists.add(0, transPointF.origin2Logic(bezierPoint.getStartP()));
                pointLists.add(1, transPointF.origin2Logic(bezierPoint.getCtrlP()));
                pointLists.add(2, transPointF.origin2Logic(bezierPoint.getEndP()));

//                pointLists.add(0, bezierPoint.getStartP());
//                pointLists.add(1, bezierPoint.getCtrlP());
//                pointLists.add(2, bezierPoint.getEndP());
                LogUtil.e(TAG, "break from start point" + "startP x= " + bezierPoint.getStartP().x + " y =" + bezierPoint.getStartP().y
                        + "ctrlP x= " + bezierPoint.getCtrlP().x + " y= " + bezierPoint.getCtrlP().y + "endP x= " + bezierPoint.getEndP().x
                        + "endP y = " + bezierPoint.getEndP().y);
                return true;
            } else if (bezierResult.getBreakType() == BezierResult.BREAK_FROM_FOOT) {
                for (int j = index; j < pointLists.size(); j++) {    //去掉i位置之后的所有的点
                    pointLists.remove(j);
                }
                BezierPoint bezierPoint = bezierResult.getList().get(0);
                pointLists.add(transPointF.origin2Logic(bezierPoint.getStartP()));
                pointLists.add(transPointF.origin2Logic(bezierPoint.getCtrlP()));
                pointLists.add(transPointF.origin2Logic(bezierPoint.getEndP()));

//                pointLists.add(bezierPoint.getStartP());
//                pointLists.add(bezierPoint.getCtrlP());
//                pointLists.add(bezierPoint.getEndP());
                LogUtil.e(TAG, "break from end point" + "startP x= " + bezierPoint.getStartP().x + " y =" + bezierPoint.getStartP().y
                        + "ctrlP x= " + bezierPoint.getCtrlP().x + " y= " + bezierPoint.getCtrlP().y + "endP x= " + bezierPoint.getEndP().x
                        + "endP y = " + bezierPoint.getEndP().y);
                return true;
            } else if (bezierResult.getBreakType() == BezierResult.BREAK_FROM_MIDDLE) {
                //生成第一段直线
                LineInfo lineInfo1 = new LineInfo();
                lineInfo1.setLineId(Build32Code.createGUID());
                lineInfo1.setType(0);
                lineInfo1.setColor(lineInfo.getColor());
                lineInfo1.setStrokeWidth(lineInfo.getStrokeWidth());
                ArrayList<PointF> list1 = new ArrayList<>();
                lineInfo1.setPointLists(list1);
                for (int j = 0; j < index - 3; j++) {
                    list1.add(pointLists.get(j));
                }
                BezierPoint bezierPoint1 = bezierResult.getList().get(0);
                list1.add(transPointF.origin2Logic(bezierPoint1.getStartP()));
                list1.add(transPointF.origin2Logic(bezierPoint1.getCtrlP()));
                list1.add(transPointF.origin2Logic(bezierPoint1.getEndP()));

//                list1.add(bezierPoint1.getStartP());
//                list1.add(bezierPoint1.getCtrlP());
//                list1.add(bezierPoint1.getEndP());
                //生成第二段直线
                LineInfo lineInfo2 = new LineInfo();
                lineInfo2.setLineId(Build32Code.createGUID());
                lineInfo2.setType(0);
                lineInfo2.setColor(lineInfo.getColor());
                lineInfo2.setStrokeWidth(lineInfo.getStrokeWidth());
                ArrayList<PointF> list2 = new ArrayList<>();
                lineInfo2.setPointLists(list2);

                BezierPoint bezierPoint2 = bezierResult.getList().get(1);
                list2.add(transPointF.origin2Logic(bezierPoint2.getStartP()));
                list2.add(transPointF.origin2Logic(bezierPoint2.getCtrlP()));
                list2.add(transPointF.origin2Logic(bezierPoint2.getEndP()));

//                list2.add(bezierPoint2.getStartP());
//                list2.add(bezierPoint2.getCtrlP());
//                list2.add(bezierPoint2.getEndP());

                for (int j = index + 3; j < pointLists.size(); j++) {
                    list2.add(pointLists.get(j));
                }
                lists.remove(lineInfo);
                lists.add(lineInfo1);
                lists.add(lineInfo2);
                LogUtil.e(TAG, "break from middle point1" + "startP x= " + bezierPoint1.getStartP().x + " y =" + bezierPoint1.getStartP().y
                        + "ctrlP x= " + bezierPoint1.getCtrlP().x + " y= " + bezierPoint1.getCtrlP().y + "endP x= " + bezierPoint1.getEndP().x
                        + "endP y = " + bezierPoint1.getEndP().y);
                LogUtil.e(TAG, "break from middle point2" + "startP x= " + bezierPoint2.getStartP().x + " y =" + bezierPoint2.getStartP().y
                        + "ctrlP x= " + bezierPoint2.getCtrlP().x + " y= " + bezierPoint2.getCtrlP().y + "endP x= " + bezierPoint2.getEndP().x
                        + "endP y = " + bezierPoint2.getEndP().y);
                return true;
            }
            return false;    //满足则跳出循环
        }
        return false;
    }


}
