package Model;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashSet;

import Controller.MainActivity;
import movingbattleship.org.focus.R;

/**
 * Created by aaronrschrock on 10/6/17.
 */

/**
 * Profile is a class that resembles a profile
 * Each profile contains a list of apps within it,
 * and keeps track of teh apps that should be blocked
 */

public class Profile {

    private Integer profileID;
    private String profileName;
    private ArrayList<App> apps;
    private ArrayList<Integer> scheduleIDs; //TODO do we need this?
    private boolean activated;
    private boolean onOffSwitch;

    /**
     * Constructors
     */
    public Profile(Integer profileID, String profileName) {
        this.profileID = profileID;
        this.profileName = profileName;
        activated = false;
        apps = new ArrayList<>();
        onOffSwitch = true;
    }

    /**
     * Getters and Setters
     */

    public Integer getProfileID() {
        return profileID;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public ArrayList<App> getApps() {
        return apps;
    }

    public boolean isActivated() {
        return activated;
    }

    public void addApp(App app) {
        apps.add(app);
    }

    public void removeApp(int appID) {
        for(App a: apps){
            if(a.getAppID() == appID){
                apps.remove(a);
                return;
            }
        }
        return;
    }

    /**
     * Blocking functions
     */

    public void activate() {
        //only display notification if switched from nonactive to active
        if (activated == false) {
            createNotificationForActive();
        }
        activated = true;
        blockProfile();
    }

    public void deactivate() {
        //only display notification if switched from active to nonactive
        if (activated == true) {
            createNotificationForDeactive();
        }
        activated = false;
        unblockProfile();
    }

    public void blockProfile() {
        for (int i = 0; i < apps.size(); i++) {
            apps.get(i).blockApp(profileID);
        }
    }

    public void unblockProfile() {
        for (int i = 0; i < apps.size(); i++) {
            apps.get(i).unblockApp(profileID);
        }
    }

    public HashSet<Integer> getAppIDs(){
        HashSet<Integer> returnSet = new HashSet<Integer>();
        for(App a: apps){
            returnSet.add(a.getAppID());
        }
        return returnSet;
    }

    public boolean isOn(){
        return onOffSwitch;
    }

    public void turnOn(){
        onOffSwitch = true;
    }

    public void turnOfF(){
        onOffSwitch = false;
    }

    /**
     * Functions for sending notifications
     */

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationForActive() {

        NotificationManager notificationManager = (NotificationManager) MainActivity.mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        android.app.Notification notification = new android.app.Notification.Builder(MainActivity.mContext)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(profileName + " blocked!")
                .setContentText("Time to Focus! " + profileName + " is now blocked!")
                .setChannelId(MainActivity.CHANNEL_ID).build();

        notificationManager.notify(MainActivity.NotificationID++, notification);
        System.out.println("Notification should be sent");
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationForDeactive() {
        NotificationManager notificationManager = (NotificationManager) MainActivity.mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        android.app.Notification notification = new android.app.Notification.Builder(MainActivity.mContext)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(profileName + " unblocked!")
                .setContentText("Time to relax! " + profileName + " is NO longer blocked!")
                .setChannelId(MainActivity.CHANNEL_ID).build();

        notificationManager.notify(MainActivity.NotificationID++, notification);
        System.out.println("Notification should be sent");
    }

}
