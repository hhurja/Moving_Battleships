package Model;

/**
 * Created by aaronrschrock on 10/6/17.
 */


import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import Controller.MainActivity;
import movingbattleship.org.focus.R;

import static movingbattleship.org.focus.R.id.profileName;

/**
 * App is a class that stores the name of an application
 * and if the app is blocked or not
 */

public class App {
    private String name;
    private int id;
    private boolean blocked;
    private HashSet<Integer> blockedProfileIDs;
    private String packageName;
    private ArrayList<Notification> notifications;

    /**
     * Constructors
     */
    public App(int id, String name, boolean blocked, String packageName) {
        this.id = id;
        this.name = name;
        this.blocked = blocked;
        this.packageName = packageName;

        blockedProfileIDs = new HashSet<>();
        notifications = new ArrayList<>();
    }

    public App(int id, String name, String packageName) {
        this.id = id;
        this.name = name;
        blocked = false;
        this.packageName = packageName;

        blockedProfileIDs = new HashSet<>();
        notifications = new ArrayList<>();
    }

    /**
     *
     * Getter and Setter functions
     *
     */

    public String getAppName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void blockApp(Integer profileID) {
        blocked = true;
        if(!blockedProfileIDs.contains(profileID)) blockedProfileIDs.add(profileID);
    }

    public int getAppID(){
        return id;
    }

    public void unblockApp(Integer profileID) {
        if (blockedProfileIDs.contains(profileID)) {
            blockedProfileIDs.remove(profileID);
        }

        if (blockedProfileIDs.isEmpty()) {
            blocked = false;

            //send all stored notifications
            for (Notification n : notifications) {
                sendNotification(n);

            }
            notifications.clear();
        }
    }

    public boolean isBlocked(){
        return blocked;
    }

    public String getPackageName(){
        return packageName;
    }

    public void addNotification(Notification n) {
        notifications.add(n);
    }

    /**
     *
     * Sends a notification
     */
    @TargetApi(Build.VERSION_CODES.O)
    public void sendNotification(Notification n) {
        NotificationManager notificationManager = (NotificationManager) MainActivity.mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        android.app.Notification notification = new android.app.Notification.Builder(MainActivity.mContext)
                .setSmallIcon(R.drawable.check)
                .setContentTitle(name + " recieved a notification while blocked")
                .setContentText(n.getTickerText())
                .setChannelId(MainActivity.CHANNEL_ID).build();

        notificationManager.notify(MainActivity.NotificationID++, notification);
        System.out.println("Notification should be sent");
    }


}
