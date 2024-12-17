package com.transcode.smartcity101p2.services;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.orhanobut.hawk.Hawk;
import com.transcode.smartcity101p2.model.Const;
import com.transcode.smartcity101p2.model.LoginResponse;

public class NotificationListener101Service extends NotificationListenerService {

    /*
        These are the package names of the apps. for which we want to
        listen the notifications
     */
    private static final class ApplicationPackageNames {
        public static final String SMART_101_PACK_NAME = "com.transcode.smartcity101p2";
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    public static final class InterceptedNotificationCode {
        public static final int SMART_101_CODE = 1;
        public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        int notificationCode = matchNotificationCode(sbn);

        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
            if (!Hawk.isBuilt()) {
                Hawk.init(getBaseContext()).build();
            }
            LoginResponse loginResponse = Hawk.get(Const.KEY_LOGIN_DATA);
            if (loginResponse != null) {
                String citizen_id = "";
                try {
                    citizen_id = loginResponse.getAuthority_info().getCitizenId();
                } catch (Exception ex) {

                }
                UpdateNotification u = new UpdateNotification();
                u.updateEmergency(citizen_id, getBaseContext());
                u.updateComplain(citizen_id, getBaseContext());
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if (packageName.equals(ApplicationPackageNames.SMART_101_PACK_NAME)) {
            return (InterceptedNotificationCode.SMART_101_CODE);
        } else {
            return (InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }
}
