package com.zonekey.mobileteach_lib.net.client;

import android.util.Log;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.interf.OnTcpSendMessageListner;
import com.zonekey.mobileteach_lib.net.bean.EncodeV2;
import com.zonekey.mobileteach_lib.net.util.ProcessMsgUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by xu.wang
 * Date on  2017/12/21 16:23:55.
 *
 * @Desc
 */

public class RequestTcp extends Thread {
    private short mainCmd;
    private short subCmd;
    private short userData;
    private ProcessMsgUtil processMsgUtil;
    private String ip;
    private int port;
    private String sendBody;
    private Socket mSocket;
    private OnTcpSendMessageListner listner;
    private int connectSoTime;

    public RequestTcp(String ip, int port, short mainCmd, short subCmd, short userData, String sendBody, int connectSoTime, OnTcpSendMessageListner listener) {
        this.ip = ip;
        this.port = port;
        this.mainCmd = mainCmd;
        this.subCmd = subCmd;
        this.userData = userData;
        this.sendBody = sendBody;
        this.listner = listener;
        this.connectSoTime = connectSoTime;
    }

    @Override
    public void run() {
        try {
            initialSendMessage(mainCmd, subCmd, userData, sendBody, listner, connectSoTime);
        } catch (final Exception e) {
            Log.e("TcpUtil", e.toString());
            if (MobileTeach.handler != null) {
                MobileTeach.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listner != null) {
                            listner.error(e);
                        }
                    }
                });
            }
        }
    }

    /**
     * 发送消息
     *
     * @param mainCmd             主命令
     * @param subCmd              子命令
     * @param sendBody            发送消息
     * @param mSendMessageListner 消息回调监听
     * @param connectSoTime       超时时间
     * @throws Exception
     */
    private void initialSendMessage(short mainCmd, short subCmd, short userData, String sendBody, OnTcpSendMessageListner mSendMessageListner, int connectSoTime) throws Exception {
        if (processMsgUtil == null) {
            processMsgUtil = new ProcessMsgUtil();
        }
        mSocket = new Socket();
        mSocket.setReuseAddress(true);
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        mSocket.connect(socketAddress, connectSoTime);
        mSocket.setSoTimeout(10000);    //此方法意为tcp连接成功后is.read阻塞多长时间
        OutputStream outputStream = mSocket.getOutputStream();
        EncodeV2 encodeV2 = new EncodeV2(mainCmd, subCmd, 0, userData, sendBody);
        byte[] sendContent = encodeV2.buildSendContent();
//        byte[] temp = processMsgUtil.getSendMsg(mainCmd, subCmd, 0,userData,sendBody);
//        if (!TextUtils.isEmpty(sendBody)) {
//            outputStream.write(temp);    //向os写入消息内容
//            outputStream.write(sendBody.getBytes());    //向os写入消息内容
//        } else {
//            outputStream.write(temp);
//        }
        outputStream.write(sendContent);
        outputStream.flush();
        processMsgUtil.processAcceptConnection(mSocket, mSendMessageListner); // 接收服务器端数据,并回调listener
        mSocket.close();
    }


    public void shutdown() {
        if (mSocket != null && (!mSocket.isClosed())) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.interrupt();
    }
}
