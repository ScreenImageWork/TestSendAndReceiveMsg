package com.zonekey.mobileteach_lib.view.drawpaint.bean.breakresult;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on  2017/11/8 14:58:48.
 *
 * @Desc 记录贝塞尔打断结果的bean
 */

public class BezierResult {
    public static final int BREAK_FROM_HEAD = 1;    //从头部打断
    public static final int BREAK_FROM_FOOT = 2;    //从脚部打断
    public static final int BREAK_FROM_MIDDLE = 3;  //从中间打断

    private int breakType;
    private ArrayList<BezierPoint> list;

    public int getBreakType() {
        return breakType;
    }

    public void setBreakType(int breakType) {
        this.breakType = breakType;
    }

    public ArrayList<BezierPoint> getList() {
        return list;
    }

    public void setList(ArrayList<BezierPoint> list) {
        this.list = list;
    }
}
