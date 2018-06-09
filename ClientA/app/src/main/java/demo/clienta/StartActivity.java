package demo.clienta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.EventLog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.MobileTeachApi;
import com.zonekey.mobileteach_lib.net.bean.ReceiverInfo;
import com.zonekey.mobileteach_lib.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by：xu.wang on 2018/6/9 16:09
 */

public class StartActivity extends AppCompatActivity {
    private static final String TAG_IP = "tag_ip";
    private EditText et;
    private TextView tv_show;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initialView();
    }

    private void initialView() {
        EventBus.getDefault().register(this);
        et = findViewById(R.id.et_set_ip);
        tv_show = findViewById(R.id.tv_start_show);

        findViewById(R.id.tv_set_ip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = et.getText().toString();
                if (TextUtils.isEmpty(ip)) {
                    ToastUtil.showToast("还没有设置Ip");
                    return;
                }
                // TODO 发送前必须先初始化Ip和Port
                MobileTeach.setPcInfo(ip, MobileTeach.local_udp_port, MobileTeach.local_tcp_port, MobileTeach.local_file_port);
                ToastUtil.showToast("初始化成功");
            }
        });
        findViewById(R.id.tv_test_send_nmsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)    //使用EventBus接收收到的消息
    public void acceptMsg(ReceiverInfo receiverInfo) {
        //主指令和子指令可以直接定义
        if (receiverInfo.getMainCmd() == MobileTeachApi.RECORD.MAIN_CMD &&
                receiverInfo.getSubCmd() == MobileTeachApi.RECORD.Command_HostUpdated) {
            StringBuffer showContent = new StringBuffer();
            showContent.append("消息来自Ip = " + receiverInfo.getReceiverAddress().getIp());
            showContent.append("  Port = " + receiverInfo.getReceiverAddress().getPort());
            showContent.append("  是 " + (receiverInfo.getReceiverAddress().getType() == 1 ? "UDP消息" : "TCP消息"));
            showContent.append(" 消息内容= " + receiverInfo.getData());
            tv_show.setText(tv_show.getText() + "\n" + showContent.toString());
        } else {
            ToastUtil.showToast("收到未知类型消息");
            tv_show.setText("收到未知类型消息");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
