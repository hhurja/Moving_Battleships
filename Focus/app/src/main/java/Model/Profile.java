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
import java.util.Date;
import Controller.MainActivity;

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
    //the integer that represents the # of times a profile gets activated
    public Integer activationCount = 0;
    // the total amount of minutes a profile has been blocked for
    public long activationTime = 0;
    //used to calculate the amount of time
    public Date startActivation = null;
    public Date endActivation = null;

    public boolean isRationed;
    public boolean isRationBlocked;
    public int rationTime;
    public Calendar finishRationing;

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
        isRationed = false;
        isRationBlocked = false;
        rationTime = 0;
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

    public long getActivationTime() {
        if (startActivation != null && endActivation == null) {
            return ((Calendar.getInstance().getTimeInMillis()/1000) - (startActivation.getTime()/1000));
        }
        else {
            System.out.println("ooooer here");
            return activationTime;
        }
    }

    public long getTimeRemaining() {
        if (finishBlocking == null) {
            return 0;
        }
        return finishBlocking.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

    public long getTimeRationRemaining() {
        if (finishRationing == null) {
            return 0;
        }
        return finishRationing.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
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
        if (isRationed) {
            isRationBlocked = true;
        }
        blockProfile();
    }

    public void deactivate() {
        time = "";

        if (isRationed) {
            unRationProfile();
        }
        else {
            unblockProfile();
        }
    }

    //returns the amount of times a profile is activated
    public Integer getActivationCount() {
        return activationCount;
    }


    public void blockProfile() {
        //only display notification if switched from nonactive to active
        if (activated == false && scheduleIDs.isEmpty()) {
            createNotificationForActive();
            activationCount++;
            //get the time that activation has started
            startActivation = Calendar.getInstance().getTime();

        }
        activated = true;
        for (int i = 0; i < apps.size(); i++) {
            apps.get(i).blockApp(profileID);
        }
    }

    public void unblockProfile() {
        System.out.println("unblock me please: " + scheduleIDs.size());

        for (Integer i : scheduleIDs) {
            System.out.println(i);
        }
        //only display notification if switched from active to nonactive
        if (activated == true && scheduleIDs.isEmpty()) {
            createNotificationForDeactive();
            //get the time that activation has ended
            endActivation = Calendar.getInstance().getTime();
            //calculate the amount of time that the profile has been blocked
            activationTime = activationTime + ((endActivation.getTime()/1000) - (startActivation.getTime()/1000));
            //reset start and end activation times
            startActivation = null;
            endActivation = null;
            activated = false;
            for (int i = 0; i < apps.size(); i++) {
                apps.get(i).unblockApp(profileID);
            }
        }
    }

    public void rationProfile(int hours, int minutes) {
        isRationed = true;
        rationTime += minutes * 60;
        rationTime += hours * 60 * 60;

    }

    public void unRationProfile() {
        isRationed = false;

        if (isRationBlocked) {
            unblockProfile();
            isRationBlocked = false;
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

        //If context is non null then send notification
        if (MainActivity.mContext != null) {
            NotificationManager notificationManager = (NotificationManager) MainActivity.mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            android.app.Notification notification = new android.app.Notification.Builder(MainActivity.mContext)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle(profileName + " blocked!")
                    .setContentText("Time to Focus! " + profileName + " is now blocked!")
                    .setChannelId(MainActivity.CHANNEL_ID).build();

            notificationManager.notify(MainActivity.NotificationID++, notification);
            System.out.println("Notification should be sent");
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationForDeactive() {

        //If context is non null then send notification
        if (MainActivity.mContext != null) {
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

    public void addTimeToEndOfDay() {
        finishBlocking = Calendar.getInstance();
        int hour = 24 - Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = 60 - Calendar.getInstance().get(Calendar.MINUTE);
        if (min == 60) {
            min = 0;
        }
        else {
            hour--;
        }
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

    public void addTimeToRation(int min, int hour) {
        finishRationing = Calendar.getInstance();
        int addMinutes = (hour*60) + min;
        finishRationing.add(Calendar.MINUTE, addMinutes);
        String HH = Integer.toString(finishRationing.get(Calendar.HOUR));
        String MM = "";
        if (finishRationing.get(Calendar.MINUTE) < 10) {
            MM = "0" + Integer.toString(finishRationing.get(Calendar.MINUTE));
        }
        else{
            MM = Integer.toString(finishRationing.get(Calendar.MINUTE));
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
