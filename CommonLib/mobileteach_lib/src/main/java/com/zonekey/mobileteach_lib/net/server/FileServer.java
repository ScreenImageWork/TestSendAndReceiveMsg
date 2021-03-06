package com.zonekey.mobileteach_lib.net.server;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.MobileTeachApi;
import com.zonekey.mobileteach_lib.interf.OnFileDownLoadListener;
import com.zonekey.mobileteach_lib.net.SocketConstant;
import com.zonekey.mobileteach_lib.net.bean.ReceiverAddress;
import com.zonekey.mobileteach_lib.net.client.OperationFile;
import com.zonekey.mobileteach_lib.util.ByteUtil;
import com.zonekey.mobileteach_lib.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by xu.wang
 * Date on 2016/11/22 09:36
 * 接收文件的server
 */
public class FileServer extends BaseServer {
    private HashMap<String, OnFileDownLoadListener> mDownLoadMap = new HashMap<>();
    private ArrayList<OperationFile> mLists = new ArrayList<>();

    public FileServer(Handler mHandler) {
        super(mHandler);
        TAG = "FileServer";
        this.port = MobileTeach.local_file_port;
        Collections.synchronizedList(mLists);
    }

    @Override
    protected void acceptSocketMsg(Socket socket) {
        try {
            InputStream is = socket.getInputStream();
            byte[] buff = new byte[1];
            is.read(buff);
            if (buff[0] == MobileTeachApi.TransferVersion_v0) {
                v0forFile(is, socket);
            } else if (buff[0] == MobileTeachApi.TransferVersion_v1) {
                v1ForFile(is, socket);
            } else if (buff[0] == MobileTeachApi.File.REQUEST_FILE) {   //兼容智课助手
                otherAcceptFile(is, socket);
            } else {
                Log.e("FileServer", "网路版本指令不正确....");

            }
        } catch (Exception e) {
            showLog("接受文件异常" + e.toString());
            LogUtil.writeLog(TAG, "接受文件异常" + e.toString());
        }
    }

    private void otherAcceptFile(InputStream is, Socket socket) throws IOException {
        byte[] buff = new byte[3];
        is.read(buff);
        byte sub_command = buff[0];
        buff = new byte[4];
        is.read(buff);
        int fileNameSize = ByteUtil.bytesToInt(buff);
        buff = new byte[4];
        is.read(buff);
        int filesize = ByteUtil.bytesToInt(buff);
        buff = new byte[fileNameSize];
        is.read(buff);
        String fileName = new String(buff).toString();
        String substring = fileName.substring(1, fileName.length());
        String[] split = substring.split("\\\\");
        String parent = split[0];
        String name = split[1];
        File file = new File(MobileTeach.localCacheDir + parent);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file_receive = new File(file, name);
        showLog("开始接受文件" + file_receive.getName());
        LogUtil.writeLog(TAG, "开始接受文件" + file_receive.getName());
        if (file_receive.exists() && file_receive.isFile() &&
                (TextUtils.equals(name, file_receive.getName())) &&
                (file_receive.length() == filesize)) {
            showLog("已经存在文件" + file_receive.getName() + "名字相同,长度相同,复用缓存");
            LogUtil.writeLog(TAG, "已经存在文件" + file_receive.getName() + "名字相同,长度相同,复用缓存");
        } else {
            if (!file_receive.exists()) {
                file_receive.createNewFile();
            }
            OperationFile operationFile = new OperationFile(is, file_receive, filesize, mDownLoadMap, mLists);
            operationFile.saveFile();
        }
        is.close();
        sendData2Manager((short) 0x00, MobileTeachApi.File.REQUEST_FILE, (byte) 0x00, file_receive.getPath(),
                new ReceiverAddress(socket.getInetAddress().toString(), socket.getLocalPort(), 3), SocketConstant.FILE_COMMAND);
    }


    private void v1ForFile(InputStream is, Socket socket) throws Exception {
        byte[] buff = new byte[1];
        is.read(buff);
        byte appCode = buff[0];     //发送者的ApllicationCode   1
        buff = new byte[1];
        is.read(buff);              //读取machineCode;      2
        buff = new byte[2];
        is.read(buff);
        short mainCmd = ByteUtil.bytesToShort(buff);    //主指令 3-4
        buff = new byte[2];
        is.read(buff);
        short subCmd = ByteUtil.bytesToShort(buff);    //子指令 5- 6
        buff = new byte[4];
        is.read(buff);
        int fileNameSize = ByteUtil.bytesToInt(buff);   //文件名的长度 7 - 10;
        buff = new byte[4];
        is.read(buff);
        int filesize = ByteUtil.bytesToInt(buff);       //文件长度 11 - 14;
        buff = new byte[fileNameSize];
        is.read(buff);
        String fileName = new String(buff).toString();
        if (fileName.startsWith("\\")) {
            fileName = fileName.substring(1, fileName.length());
        }
        String[] split = fileName.split("\\\\");
        if (split.length <= 1) {
            socket.close();
            throw new Exception("file name error 文件名中必须有反斜杠,请参考<<通信命令文档>>" + fileName);
        }
        String parent = split[0];
        String name = split[1];
        File file = new File(MobileTeach.localCacheDir + parent);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file_receive = new File(file, name);
        if (file_receive.exists() && file_receive.isFile() &&
                (TextUtils.equals(name, file_receive.getName())) &&
                (file_receive.length() == filesize)) {
            showLog("已经存在文件" + file_receive.getName() + "名字相同,长度相同");
        } else {
            if (!file_receive.exists()) {
                file_receive.createNewFile();
            }
            OperationFile operationFile = new OperationFile(is, file_receive, filesize, mDownLoadMap, mLists);
            operationFile.saveFile();
        }
        is.close();
        sendData2Manager(mainCmd, subCmd, appCode, file_receive.getPath(), new ReceiverAddress(socket.getInetAddress().toString(), socket.getLocalPort(), 3), SocketConstant.FILE_COMMAND);
    }

    private void v0forFile(InputStream is, Socket socket) throws Exception {
        byte[] buff = new byte[4];
        is.read(buff);
        buff = new byte[4];
        is.read(buff);
        int fileNameSize = ByteUtil.bytesToInt(buff);
        buff = new byte[4];
        is.read(buff);
        int filesize = ByteUtil.bytesToInt(buff);
        buff = new byte[fileNameSize];
        is.read(buff);
        String fileName = new String(buff).toString();
        if (fileName.startsWith("\\")) {
            fileName = fileName.substring(1, fileName.length());
        }
        String[] split = fileName.split("\\\\");
        if (split.length <= 1) {
            socket.close();
            throw new Exception("file name error 文件名中必须有反斜杠,请参考<<通信命令文档>>" + fileName);
        }
        String parent = split[0];
        String name = split[1];
        File file = new File(MobileTeach.localCacheDir + parent);
        if (!file.exists()) file.mkdirs();
        File file_receive = new File(file, name);
        if (file_receive.exists() && file_receive.isFile() &&
                (TextUtils.equals(name, file_receive.getName())) &&
                (file_receive.length() == filesize)) {
            showLog("已经存在文件" + file_receive.getName() + "名字相同,长度相同");
        } else {
            if (!file_receive.exists()) file_receive.createNewFile();
            OperationFile operationFile = new OperationFile(is, file_receive, filesize, mDownLoadMap, mLists);
            operationFile.saveFile();
        }
        is.close();
        sendData2Manager((byte) 0x00, MobileTeachApi.File.REQUEST_FILE, (byte) 0x00, file_receive.getPath(),
                new ReceiverAddress(socket.getInetAddress().toString(), socket.getLocalPort(), 3), SocketConstant.FILE_COMMAND);
    }


    public void registerFileListener(String name, OnFileDownLoadListener listener) {
        if (TextUtils.isEmpty(name) || listener == null) {
            LogUtil.e(TAG, "registerFileListener exception : name or listener == null");
            return;
        }
        if (mDownLoadMap.containsKey(name)) {
            LogUtil.e(TAG, "已经包含name = " + name);
            return;
        }
        mDownLoadMap.put(name, listener);
        for (int i = 0; i < mLists.size(); i++) {
            OperationFile operationFile = mLists.get(i);
            operationFile.changeMap();
        }
    }

    public void unRegisterFileListener(String name) {
        mDownLoadMap.remove(name);
    }
}
