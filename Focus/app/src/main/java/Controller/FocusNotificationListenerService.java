package Controller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.pm.PackageManager.NameNotFoundException;


import Model.FocusModel;
import Model.Notification;

/**
 * Created by aaronrschrock on 10/15/17.
 */

public class FocusNotificationListenerService extends NotificationListenerService {
    private int Unique_Integer_Number = 0;
    private FocusModel focusModel = null;



    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    /**
     *
     * Function runs everytime a notification is recieved
     * If the notification is from an app that should be blocked then block the notification
     * and save its contents
     * Else let the notification continue
     *
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        focusModel = FocusModel.getInstance();

        //TODO Save the notification to be displayed later
        if (focusModel.getBlockedApps().contains(sbn.getPackageName())) {
            //Cancel the notification if it should be blocked
            System.out.println("NOTIFICATION");
            System.out.println(sbn.getNotification().tickerText);
            cancelNotification(sbn.getKey());

            //Get App Name
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = packageManager.getApplicationInfo(sbn.getPackageName(), 0);
            } catch (final NameNotFoundException e) {}
            final String appName = (String)((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");

            //Get App Title
            String notificationTitle = appName + " Notification at " + getDate(sbn.getPostTime());
            //Get TickerText
            String tickerText = (String) sbn.getNotification().tickerText;
            //Make Notification
            Notification notification = new Notification(appName, notificationTitle, tickerText);

            FocusModel.addNotification(sbn.getPackageName(), notification);
        }

        /*
        //Hard Coded Example
        if (sbn.getPackageName().equals("com.gogii.textplus")) {
            //Cancel the notification if it should be blocked
            System.out.println("NOTIFICATION");
            System.out.println(sbn.getNotification().tickerText);
            cancelNotification(sbn.getKey());
        }
        */

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        //Do nothing
    }

    private String getDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat();
        return format.format(new Date(time));
    }
}
