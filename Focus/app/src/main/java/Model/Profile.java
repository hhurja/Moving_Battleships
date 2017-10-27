package Model;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import Controller.MainActivity;

import static android.R.attr.button;
import static android.R.attr.x;
import static movingbattleship.org.focus.R.id.textView;

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
    private HashSet<Integer> scheduleIDs; //TODO do we need this?
    private boolean activated;
    private boolean activatedByProfile;
    private boolean activatedBySchedule;
    private boolean onOffSwitch;
    public String time;
    public Calendar finishBlocking;
    public Boolean blockedFromProfiles;
    public TextView textView;
    public TextView listView;
    public Button button;
    /**
     * Constructors
     */
    public Profile(Integer profileID, String profileName) {
        this.profileID = profileID;
        this.profileName = profileName;
        activated = false;
        apps = new ArrayList<>();
        onOffSwitch = true;
        time = "";
        blockedFromProfiles = false;
        scheduleIDs = new HashSet<Integer>();
    }

    /**
     * Getters and Setters
     */

    public void addScheduleID(int id) {
        scheduleIDs.add(id);
    }
    public void removeScheduleID(int id) {
        scheduleIDs.remove(Integer.valueOf(id));
    }
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
        if (activated == true) {
            app.blockApp(profileID);
        }
    }

    public void removeApp(int appID) {
        for(App a: apps){
            if(a.getAppID() == appID){
                apps.remove(a);
                if (activated == true) {
                    a.unblockApp(profileID);
                }
                return;
            }
        }
        return;
    }

    /**
     * Blocking functions
     */

    public void activate() {
        activated = true;
        blockProfile();
    }

    public void deactivate() {
        time = "";
        activated = false;
        unblockProfile();
    }



    public void blockProfile() {
        //only display notification if switched from nonactive to active
        if (activated == false && scheduleIDs.isEmpty()) {
            createNotificationForActive();
        }
        activated = true;
        for (int i = 0; i < apps.size(); i++) {
            apps.get(i).blockApp(profileID);
        }
    }

    public void unblockProfile() {
        System.out.println(scheduleIDs.size());
        for (Integer i : scheduleIDs) {
            System.out.println(i);
        }
        //only display notification if switched from active to nonactive
        if (activated == true && scheduleIDs.isEmpty()) {
            createNotificationForDeactive();
            activated = false;
            for (int i = 0; i < apps.size(); i++) {
                apps.get(i).unblockApp(profileID);
            }
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

    public void turnOFF(){
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

    public void addTime(int min, int hour) {
        finishBlocking = Calendar.getInstance();
        int addMinutes = (hour*60) + min;
        finishBlocking.add(Calendar.MINUTE, addMinutes);
        String HH = Integer.toString(finishBlocking.get(Calendar.HOUR));
        String MM = "";
        if (finishBlocking.get(Calendar.MINUTE) < 10) {
            MM = "0" + Integer.toString(finishBlocking.get(Calendar.MINUTE));
        }
        else{
            MM = Integer.toString(finishBlocking.get(Calendar.MINUTE));
        }
        time = HH + ":" + MM;
        System.out.println("Time blocking will finish: " + time);
    }

    public void getView(TextView tw, Button b) {
        textView = tw;
        button = b;
    }
    public void getListView(TextView listV) {
        listView = listV;
    }
    public void updateActivation() {
        Calendar now = Calendar.getInstance();
        //if the profile should stop being actived
        if (finishBlocking != null) {
            if (finishBlocking.equals(now) || finishBlocking.before(now)) {
                deactivate();
                textView.setVisibility(TextView.INVISIBLE);
                listView.setVisibility(TextView.INVISIBLE);
                button.setText("Start Blocking This Profile");
                button.setBackgroundColor(Color.GREEN);
            }
        }
    }

}
