package com.zonekey.mobileteach_lib.net.client;

import android.text.TextUtils;
import android.util.Log;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.net.UdpSocketInstance;
import com.zonekey.mobileteach_lib.net.bean.EncodeV2;
import com.zonekey.mobileteach_lib.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by xu.wang
 * Date on 2016/11/16 10:55
 * Modify on 2017/05/02
 */
public class UdpUtil {
    public static final String TAG = "UdpUtil";
    private int port;
    private String ip;
    //--------------后续版本扩充使用------------------


    public UdpUtil(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    public UdpUtil() {
        this.ip = MobileTeach.pc_ip;
        this.port = MobileTeach.pc_udp_port;
    }

    /**
     * 发送不需要获得回应的UDP指令
     */
    public void sendMessage(short mainCommand, short subCommand) {
        sendMessage(mainCommand, subCommand, null);
    }

    public void sendMessage(final short mainCommand, final short subCommand, final String sendBody) {
        if (TextUtils.isEmpty(ip) || port == 0) return;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    send(mainCommand, subCommand, sendBody);
                } catch (Exception e) {
                    Log.e(TAG, e.toString() + "");
                }
            }
        }.start();
    }

    private synchronized void send(short mainCmd, short subCmd, String sendBody) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        EncodeV2 encodeV2 = new EncodeV2(mainCmd,subCmd,MobileTeach.local_udp_port,(byte)0x00,sendBody);
        byte[] sendContent = encodeV2.buildSendContent();
        //定义用来发送数据的DatagramPacket实例
        DatagramPacket dp_send = new DatagramPacket(sendContent, sendContent.length, inetAddress, port);
        UdpSocketInstance.getInstance().getDatagramSocket().send(dp_send);
    }
}
