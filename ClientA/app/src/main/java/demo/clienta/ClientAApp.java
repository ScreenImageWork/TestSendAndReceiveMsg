package demo.clienta;

import android.app.Application;

import com.zonekey.mobileteach_lib.MobileTeach;
import com.zonekey.mobileteach_lib.MobileTeachConfig;
import com.zonekey.mobileteach_lib.net.AcceptMsgManager;

/**
 * Created by：xu.wang on 2018/6/9 15:40
 */

public class ClientAApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileTeach.init(this, MobileTeachConfig.App_Teachmaster);
        AcceptMsgManager.getInstance().startServer();
    }
}
