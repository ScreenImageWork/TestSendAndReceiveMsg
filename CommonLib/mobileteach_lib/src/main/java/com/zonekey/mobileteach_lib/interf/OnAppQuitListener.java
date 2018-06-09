package com.zonekey.mobileteach_lib.interf;

/**
 * Created by xu.wang
 * Date on 2017/6/5 14:17
 */

public interface OnAppQuitListener {
    void quit(String detail);   //收到退出指令
    void toLoginActivity(); //去登录界面
}
