package Controller;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aaronrschrock on 10/15/17.
 */

public class FocusNotificationListenerService extends NotificationListenerService {
    private int Unique_Integer_Number = 0;
    Context context;



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

        //TODO don't hard code and isntead check if blocked
        if (sbn.getPackageName().equals("com.gogii.textplus")) {
            //Cancel the notification if it should be blocked
            System.out.println("NOTIFICATION");
            System.out.println(sbn.getNotification().tickerText);
            cancelNotification(sbn.getKey());
        }

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
