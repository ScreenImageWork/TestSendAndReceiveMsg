package com.zonekey.mobileteach_lib.net.bean;

import android.text.TextUtils;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.MobileTeachApi;
import com.zonekey.mobileteach_lib.net.util.ProcessMsgUtil;
import com.zonekey.mobileteach_lib.util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * Created by xu.wang
 * Date on  2017/11/28 10:27:39.
 *
 * @Desc
 */

public class EncodeV2 {
    private short mainCmd;
    private short subCmd;
    private int port;   //tcp的短连接该位置写0;
    private short userData;
    private String sendBody;

    public EncodeV2(short mainCmd, short subCmd, int port, short userData) {
        this.mainCmd = mainCmd;
        this.subCmd = subCmd;
        this.port = port;
        this.userData = userData;
    }

    public EncodeV2(short mainCmd, short subCmd, int port, short userData, String sendBody) {
        this.mainCmd = mainCmd;
        this.subCmd = subCmd;
        this.port = port;
        this.userData = userData;
        this.sendBody = sendBody;
    }

    public byte[] buildSendContent() {
        int bodyLength;
        ByteBuffer bb;
        if (TextUtils.isEmpty(sendBody)) {
            bodyLength = 0;
            bb = ByteBuffer.allocate(19);
        } else {
            bodyLength = sendBody.getBytes().length;
            bb = ByteBuffer.allocate(19 + bodyLength);
        }
        bb.put(MobileTeachApi.encodeVersion1); //编码版本1     0
        bb.put(MobileTeachApi.encodeVersion2_v1);   //编码版本2     1
        bb.put(MobileTeach.AppCode);     //app指令      2
        bb.put(MobileTeachApi.MACHINE_TYPE);    //机器类型 3
        bb.put(ByteUtil.short2Bytes(mainCmd));  //4-5   主指令
        bb.put(ByteUtil.short2Bytes(subCmd));   //6-7   子指令
        bb.put(ByteUtil.int2Bytes(port));        //8-11 端口号
        bb.put(ByteUtil.int2Bytes(bodyLength));  //12 -16位,数据长度
        bb.put(ByteUtil.short2Bytes(userData));   //17 _18位
        byte[] tempb = bb.array();
        bb.put(ByteUtil.getCheckCode(tempb));
        if (bodyLength != 0) {
            bb.put(sendBody.getBytes());
        }
        return bb.array();
    }

}
