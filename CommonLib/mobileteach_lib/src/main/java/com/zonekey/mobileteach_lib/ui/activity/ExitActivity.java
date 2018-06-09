package com.zonekey.mobileteach_lib.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.MobileTeachConfig;
import com.zonekey.mobileteach_lib.R;

/**
 * Created by Administrator on 2017/1/10.
 */
public class ExitActivity extends Activity {
    private LinearLayout root_exit;
    private Button btn_sure;
    private TextView tv_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        int width = 0;
        if (MobileTeach.CurrentApp == MobileTeachConfig.App_Teachmaster) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            width = screenWidth * 2 / 3;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            width = screenWidth / 2;
        }

        setContentView(R.layout.activity_exit);
        String detail = getIntent().getStringExtra("qpp_quit");
        root_exit = (LinearLayout) findViewById(R.id.root_exit);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) root_exit.getLayoutParams();
        layoutParams.width = width;

        root_exit.setLayoutParams(layoutParams);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_msg.setText(detail + "，将返回登录界面");
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExitActivity.this, MobileTeach.LoginActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (MobileTeach.mListener != null) {
                    MobileTeach.mListener.toLoginActivity();
                }
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {

    }
}
