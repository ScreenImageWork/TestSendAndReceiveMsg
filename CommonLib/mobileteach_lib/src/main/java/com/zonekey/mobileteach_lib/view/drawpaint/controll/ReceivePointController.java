package com.zonekey.mobileteach_lib.view.drawpaint.controll;

import android.graphics.PointF;
import android.text.TextUtils;

import com.zonekey.mobileteach_lib.MobileTeachApi;
import com.zonekey.mobileteach_lib.net.bean.ReceiverInfo;
import com.zonekey.mobileteach_lib.util.LogUtil;
import com.zonekey.mobileteach_lib.view.drawpaint.bean.shape.LineInfo;
import com.zonekey.mobileteach_lib.view.drawpaint.interf.IReceivePointController;
import com.zonekey.mobileteach_lib.view.drawpaint.interf.OnRefreshListner;
import com.zonekey.mobileteach_lib.view.drawpaint.util.ColorUtil;
import com.zonekey.mobileteach_lib.view.drawpaint.util.TransPointF;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by xu.wang
 * Date on 2017/9/7 11:02
 * 控制接收远程的点
 */

public class ReceivePointController implements IReceivePointController {
    private ArrayList<LineInfo> mReceiveLists = new ArrayList<>();   //收到远程的lineInfo
    private static final int NEW_SHAPE = 1;
    private static final int ADD_POINT = 2;
    private static final int END_DRAW = 3;

    private OnRefreshListner mListener;

    public ReceivePointController(OnRefreshListner listener) {
        this.mListener = listener;
        Collections.synchronizedList(mReceiveLists);
    }


    //接收增加新的LineInfo指令...
    @Override
    public void newShape(ReceiverInfo receiverInfo, TransPointF transPointF, ArrayList<LineInfo> lists) {
        String[] splits = receiverInfo.getData().split("\\|");
        if (splits.length != 5) {
            LogUtil.e("PaintImageView", "收到增加新图形的数据类型不正确" + receiverInfo.getData());
            return;
        }
        LineInfo lineInfo = new LineInfo();
        lineInfo.setLineId(splits[1]);      //     直线的id
        lineInfo.setColor(ColorUtil.convertStringToColor(splits[2]));  //颜色
        lineInfo.setStrokeWidth(Integer.parseInt(splits[3]));   //线的粗细


        switch (splits[0]) { //第一项数据类型
            case MobileTeachApi.PresentControl.QuadraticBezier:
                lineInfo.setType(0);            //增加贝塞尔
                newShapeBezier(lineInfo, splits[4], receiverInfo.getData());
                break;
            case MobileTeachApi.PresentControl.PAINT_TIANZIGE:
                lineInfo.setType(5);            //增加田字格
                newShapeOther(lineInfo, splits[4], receiverInfo.getData());
                break;
            case MobileTeachApi.PresentControl.PAINT_MIZIGE:
                lineInfo.setType(6);            //增加米字格
                newShapeOther(lineInfo, splits[4], receiverInfo.getData());
                break;
            case MobileTeachApi.PresentControl.PAINT_SiXianGE:
                lineInfo.setType(7);            //增加类型
                newShapeOther(lineInfo, splits[4], receiverInfo.getData());
                break;
        }
        LogUtil.writeLog("PaintImageView", "NewShape " + receiverInfo.getData());
        mReceiveLists.add(lineInfo);
    }

    private void newShapeBezier(LineInfo lineInfo, String pointString, String detail) {
        if (TextUtils.isEmpty(pointString)) {
            LogUtil.writeLog("PaintImageView", "new shape accept first PointF point x y error" + detail);
            return;
        }
        String[] split_temp = pointString.split(",");
        if (split_temp.length != 6) {
            LogUtil.writeLog("PaintImageView", "new shape accept first PointF point x y error" + detail);
            return;
        }
        try {
            lineInfo.getPointLists().add(new PointF(Float.parseFloat(split_temp[0]), Float.parseFloat(split_temp[1])));
            lineInfo.getPointLists().add(new PointF(Float.parseFloat(split_temp[2]), Float.parseFloat(split_temp[3])));
            lineInfo.getPointLists().add(new PointF(Float.parseFloat(split_temp[4]), Float.parseFloat(split_temp[5])));
        } catch (Exception e) {
            LogUtil.writeLog("PaintImageView", "new shape accept first PointF point x y error" + detail);
        }

    }

    private void newShapeOther(LineInfo lineInfo, String pointString, String detail) {
        if (TextUtils.isEmpty(pointString)) {
            LogUtil.writeLog("PaintImageView", "new shape accept first PointF point x y error" + detail);
            return;
        }
        String[] split_temp = pointString.split(",");
        if (split_temp.length != 2) {
            LogUtil.writeLog("PaintImageView", "new shape accept first PointF point x y error" + detail);
            return;
        }
        lineInfo.getPointLists().add(0,
                new PointF(Float.parseFloat(split_temp[0]), Float.parseFloat(split_temp[1])));
    }

    //往LineInfo增加新的点...
    @Override
    public void addPoint(ReceiverInfo receiverInfo, TransPointF transPointF, ArrayList<LineInfo> lists) {
        if (mReceiveLists.size() < 1) {
            LogUtil.writeLog("PaintImageView Add Point", "数据总量不正确" + receiverInfo.getData());
            return;
        }
        String[] splits = receiverInfo.getData().split("\\|");
        if (splits.length != 3) {
            LogUtil.writeLog("PaintImageView Add Point", "收到追加点的信息不正确");
            return;
        }
        //遍历查看集合中有没有这个lineinfo的id,没有就扔掉这个点
        int index = -1;
        int type = -1;
        for (int i = 0; i < mReceiveLists.size(); i++) {
            if (TextUtils.equals(mReceiveLists.get(i).getLineId(), splits[0])) {
                index = i;
                type = mReceiveLists.get(i).getType();
                break;
            }
        }
        if (index == -1) {
            LogUtil.writeLog("PaintImageView Add Point", "接受点的list中没有这个id" + splits[0]);
            return;
        }
        switch (type) {
            case 5:
            case 6:
            case 7:
                LineInfo lineInfo = mReceiveLists.get(index);
                mListener.refresh(addPointOther(lineInfo, splits[2]));
                break;
            case 0:
                LineInfo tempInfo = mReceiveLists.get(index);
                int indexId = Integer.parseInt(splits[1]);  // 电脑端发来的index是从1开始的...
                mListener.refresh(addPointBezier(tempInfo, indexId, splits[2], receiverInfo.getData()));
                break;
        }
    }

    private boolean addPointBezier(LineInfo tempInfo, int indexId, String split, String detail) {
        String[] split_temp = split.split(",");
        if (split_temp.length != 6) {
            LogUtil.writeLog("PaintImageView", "AddPoint Bezier=" + detail);
            return false;
        }
        if (tempInfo.getPointLists().size() < indexId) {
            tempInfo.getPointLists().add(new PointF(Float.parseFloat(split_temp[0]), Float.parseFloat(split_temp[1])));
            tempInfo.getPointLists().add(new PointF(Float.parseFloat(split_temp[2]), Float.parseFloat(split_temp[3])));
            tempInfo.getPointLists().add(new PointF(Float.parseFloat(split_temp[4]), Float.parseFloat(split_temp[5])));
            LogUtil.writeLog("PaintImageView", "the index is right AddPoint Bezier=" + detail);
            return true;
        } else if (tempInfo.getPointLists().size() > indexId) {
            tempInfo.getPointLists().add(indexId - 1, new PointF(Float.parseFloat(split_temp[4]), Float.parseFloat(split_temp[5])));
            tempInfo.getPointLists().add(indexId - 1, new PointF(Float.parseFloat(split_temp[2]), Float.parseFloat(split_temp[3])));
            tempInfo.getPointLists().add(indexId - 1, new PointF(Float.parseFloat(split_temp[0]), Float.parseFloat(split_temp[1])));
            LogUtil.writeLog("PaintImageView", "sort and insert pos AddPoint Bezier=" + (indexId - 1) + detail);
            return true;
        } else {
            LogUtil.writeLog("PaintImageView", "收到重复点,不处理");
            return false;
        }
    }

    private boolean addPointOther(LineInfo lineInfo, String pointDetail) {
        String[] split_temp = pointDetail.split(",");
        if (split_temp.length != 2) {
            LogUtil.writeLog("PaintImageView Add Point", "收到追加点的信息不正确");
            return false;
        }
        if (lineInfo.getPointLists().size() >= 2) {
            lineInfo.getPointLists().set(1, new PointF(Float.parseFloat(split_temp[0]), Float.parseFloat(split_temp[1])));
        } else {
            lineInfo.getPointLists().add(1, new PointF(Float.parseFloat(split_temp[0]), Float.parseFloat(split_temp[1])));
        }
        return true;
    }


    //结束LineInfo的绘制.
    @Override
    public void endDrawing(ReceiverInfo receiverInfo, TransPointF transPointF, ArrayList<LineInfo> lists) {
        String lineId = receiverInfo.getData();
        for (int i = 0; i < mReceiveLists.size(); i++) {
            if (TextUtils.equals(lineId, mReceiveLists.get(i).getLineId())) {
                lists.add(mReceiveLists.get(i));  //向当前记录LineInfo的lists里记录当前一条线的信息
                mReceiveLists.remove(i);    //从receivereLists的缓存区中删除.
                break;
            }
        }
        mListener.refresh(true);
    }

    @Override
    public ArrayList<LineInfo> getReceiveList() {
        return mReceiveLists;
    }

}
