package com.zonekey.mobileteach_lib;

import android.os.Environment;

/**
 * Created by xu.wang
 * Date on 2017/6/1 13:48
 */

public class MobileTeachConfig {
    //----------------------记录当前app是--------------------------
    public final static int App_Teachmaster = 1;      //智客助手
    public final static int App_MobileTeach_Teacher = 2;  //智客互动老师端
    public final static int App_MobileTeach_Student = 3;  //智客互动学生端
    public final static int App_WiseClass_Teacher = 1;    //智课堂教师端
    public final static int App_WiseClass_Student = 5;    ///智课堂学生端
    //-----------智客助手--------------------------------
    public final static int teachmaster_file_port = 10998;
    public final static int teachmaster_udp_port = 10999;
    public final static int teachmaster_tcp_port = 11000;
    public final static String teachmaster_dir = Environment
            .getExternalStorageDirectory() + "/zonekey/teachmaster/"; // 项目的根路径;
    //----------智客互动教师端-------------------------
    public final static int teacher_file_port = 11998;
    public final static int teacher_udp_port = 11999;
    public final static int teacher_tcp_port = 12000;
    public final static String teacher_dir = Environment
            .getExternalStorageDirectory() + "/zonekey/mobileteach/teacher/"; // 项目的根路径;
    //-----------智客互动学生端-----------------------
    public final static int stu_file_port = 12998;
    public final static int stu_udp_port = 12999;
    public final static int stu_tcp_port = 13000;
    public final static String student_dir = Environment
            .getExternalStorageDirectory() + "/zonekey/mobileteach/student/"; // 项目的根路径;
    //-----------智课堂教师端-------------------------------------------------
    public final static int wiseclass_teacher_file_port = 10998;
    public final static int wiseclass_teacher_udp_port = 10999;
    public final static int wiselass_teacher_tcp_port = 11000;
    public final static String wiseclass_teacher_dir = Environment.getExternalStorageDirectory()
            + "/zonekey/wiseclass/teacher/";
    //-----------智课堂学生端-------------------------------------------------
    public final static int wiseclass_student_file_port = 10998;
    public final static int wiseclass_student_udp_port = 10999;
    public final static int wiselass_student_tcp_port = 11000;
    public final static String wiseclass_student_dir = Environment.getExternalStorageDirectory()
            + "/zonekey/wiseclass/teacher/";
}
