package demo.clienta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.MobileTeachApi;
import com.zonekey.mobileteach_lib.interf.OnTcpSendMessageListner;
import com.zonekey.mobileteach_lib.net.bean.ReceiverInfo;
import com.zonekey.mobileteach_lib.net.client.TcpUtil;
import com.zonekey.mobileteach_lib.net.client.UdpUtil;
import com.zonekey.mobileteach_lib.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private EditText et_send;
    private Button btn_send_udp, btn_send_tcp;
    private TextView tv_receive;
    private static final String ip = "192.168.12.135";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialView();
        initialData();
    }

    private void initialView() {
        et_send = findViewById(R.id.et_send_msg);
        btn_send_udp = findViewById(R.id.btn_send_udp);
        btn_send_tcp = findViewById(R.id.btn_send_tcp);
        tv_receive = findViewById(R.id.tv_show_receive_msg);
    }


    private void initialData() {
        EventBus.getDefault().register(this);

        btn_send_udp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sendBody = et_send.getText().toString();
                if (TextUtils.isEmpty(sendBody)) {
                    tv_receive.setText("请输入发送内容");
                    return;
                }
                UdpUtil udpUtil = new UdpUtil();
//                UdpUtil udpUtil1 = new UdpUtil(ip,port);    //可以指定Ip和Port;
                udpUtil.sendMessage(MobileTeachApi.RECORD.MAIN_CMD,
                        MobileTeachApi.RECORD.Command_HostUpdated, sendBody);
            }
        });

        btn_send_tcp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sendBody = et_send.getText().toString();
                if (TextUtils.isEmpty(sendBody)) {
                    tv_receive.setText("请输入发送内容");
                    return;
                }
//                TcpUtil tcpUtil = new TcpUtil(ip,port); //可指定ip和Port
                TcpUtil.getInstance().sendMessage(MobileTeachApi.RECORD.MAIN_CMD,
                        MobileTeachApi.RECORD.Command_HostUpdated, sendBody, new OnTcpSendMessageListner() {
                            @Override
                            public void success(short mainCmd, short subCmd, byte appCodeCmd, String body) {
                                ToastUtil.showToast("发送成功");
                            }

                            @Override
                            public void error(Exception e) {
                                ToastUtil.showToast("发送失败" + e.toString());
                            }
                        });
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)    //使用EventBus接收收到的消息
    public void acceptMsg(ReceiverInfo receiverInfo) {
        if (receiverInfo.getMainCmd() == MobileTeachApi.RECORD.MAIN_CMD &&
                receiverInfo.getSubCmd() == MobileTeachApi.RECORD.Command_HostUpdated) {
            StringBuffer showContent = new StringBuffer();
            showContent.append("消息来自Ip = " + receiverInfo.getReceiverAddress().getIp());
            showContent.append("  Port = " + receiverInfo.getReceiverAddress().getPort());
            showContent.append("  是 " + (receiverInfo.getReceiverAddress().getType() == 1 ? "UDP消息" : "TCP消息"));
            showContent.append(" 消息内容= " +receiverInfo.getData());
            tv_receive.setText(showContent.toString());
        } else {
            ToastUtil.showToast("收到未知类型消息");
            tv_receive.setText("收到未知类型消息");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
