package com.zonekey.mobileteach_lib.net.server;

import android.os.Handler;
import android.util.Log;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.interf.OnTcpSendMessageListner;
import com.zonekey.mobileteach_lib.net.SocketConstant;
import com.zonekey.mobileteach_lib.net.bean.EncodeV2;
import com.zonekey.mobileteach_lib.net.bean.ReceiverAddress;
import com.zonekey.mobileteach_lib.util.LogUtil;

import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by xu.wang
 * Date on 2017/6/15 11:34
 * 接收Tcp消息的Server
 */

public class TcpServer extends BaseServer {
    private String ip;
    private int tempPort = 0;

    public TcpServer(Handler mHandler) {
        super(mHandler);
        TAG = "TcpServer";
        this.port = MobileTeach.local_tcp_port;
    }

    @Override
    protected void acceptSocketMsg(Socket socket) {
        try {
            this.ip = socket.getInetAddress().toString();
            this.tempPort = socket.getLocalPort();
            processMsgUtil.processAcceptConnection(socket, new MyListener());
            OutputStream outputStream = socket.getOutputStream();
            String msg = "true";
            EncodeV2 encodeV2 = new EncodeV2((short)0x00,(short) 0x00,MobileTeach.local_tcp_port,(byte)0x00,msg);
            outputStream.write(encodeV2.buildSendContent());
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            showLog("Tcp msg accept exception " + e);
            LogUtil.writeLog(TAG, "Tcp msg accept exception " + e);
        }
    }

    class MyListener implements OnTcpSendMessageListner {

        @Override
        public void success(short mainCmd, short subCmd, byte appCodeCmd, String body) {
            showLog("收到消息" + body);
            sendData2Manager(mainCmd, subCmd, appCodeCmd, body, new ReceiverAddress(ip, port, 2), SocketConstant.TCP_COMMAND);
        }

        @Override
        public void error(Exception e) {
            Log.e(TAG, "回调异常" + e.toString());
        }
    }
}
