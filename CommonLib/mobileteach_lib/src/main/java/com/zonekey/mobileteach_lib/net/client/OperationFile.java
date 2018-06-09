package com.zonekey.mobileteach_lib.net.client;

import android.text.TextUtils;
import android.util.Log;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.interf.OnFileDownLoadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xu.wang
 * Date on  2018/1/3 09:27:34.
 *
 * @Desc 操作某一个文件的下载, 可用于提供注册回调进度
 */

public class OperationFile {
    private final static String TAG = "OperationFile";
    private boolean isDebug = false;
    private int cacheSize = 4 * 1024;
    private ArrayList<OperationFile> mLists;                    //当前所有文件下载的list
    private InputStream is;
    private File file_receive;
    private int fileSize;
    private OnFileDownLoadListener listener;                        //当前的回调
    private HashMap<String, OnFileDownLoadListener> mDownLoadMap;   //保存listener的map
    private int sum = 0;
    private int preProgress = 0;

    public OperationFile(InputStream is, final File file_receive, int filesize, HashMap<String, OnFileDownLoadListener> downloadMap, ArrayList<OperationFile> lists) {
        this.is = is;
        this.file_receive = file_receive;
        this.fileSize = filesize;
        this.mDownLoadMap = downloadMap;
        this.mLists = lists;
        mLists.add(this);
    }

    public void saveFile() {
        try {
            setListener();
            byte[] temp_byte = new byte[cacheSize];
            FileOutputStream fos = new FileOutputStream(file_receive);
            int len = 0;
            while ((len = is.read(temp_byte)) != -1) {
                fos.write(temp_byte, 0, len);
                sum += len;
                final int progress = sum * 100 / fileSize;
                if (preProgress == progress) {
                    continue;
                }
                preProgress = progress;
                showLog("sum = " + sum + " len = " + fileSize + " progress = " + progress);
                if (listener != null) {
                    MobileTeach.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.downloadFileProgress(progress, file_receive.getName());
                        }
                    });
                }
            }
            fos.close();
            showLog("文件接受完成 = " + file_receive.getName());
        } catch (final Exception e) {
            if (listener != null) {
                MobileTeach.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.error(e);
                    }
                });
            }
            showLog("save File exception = " + e.toString());
        } finally {
            mLists.remove(this);
            if (listener != null) mDownLoadMap.remove(file_receive.getName());
        }
    }

    public void changeMap() {
        setListener();
    }

    private void setListener() {
        Iterator<Map.Entry<String, OnFileDownLoadListener>> iterator = mDownLoadMap.entrySet().iterator();
        boolean isExists = false;
        if (iterator.hasNext() && !isExists) {
            Map.Entry<String, OnFileDownLoadListener> entry = iterator.next();
            if (TextUtils.equals(entry.getKey(), file_receive.getName())) {
                listener = entry.getValue();
                showLog("set listener success");
                isExists = true;
            }
        }
        if (!isExists) {
            listener = null;
        }
    }

    private void showLog(String msg) {
        if (!isDebug) return;
        Log.e(TAG, "" + msg);
    }
}
