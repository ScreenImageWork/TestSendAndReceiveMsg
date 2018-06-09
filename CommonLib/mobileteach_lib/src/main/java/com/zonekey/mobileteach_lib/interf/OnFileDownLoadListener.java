package com.zonekey.mobileteach_lib.interf;

/**
 * Created by xu.wang
 * Date on  2018/1/2 17:01:38.
 *
 * @Desc
 */

public interface OnFileDownLoadListener {
    void downloadFileProgress(int progress, String name);
    void error(Exception e);
}
