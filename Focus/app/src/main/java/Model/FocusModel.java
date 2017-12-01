package Model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;
import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import Controller.DialogActivity;
import Controller.profilesListViewController;
import Controller.schedulesListViewController;

/**
 * Created by aaronrschrock on 10/6/17.
 */

/**
 * FocusModel is the class that contains profiles, schedules, and the main functionality
 */

public class FocusModel extends Thread { // implements EasyPermissions.PermissionCallbacks
    //Main ArrayLists holding all objects
    private ArrayList<Schedule> schedules;
    private ArrayList<Profile> profiles;
    private ArrayList<App> apps;
    //Maps each each object to the others that hold it
    private HashMap<Integer, HashSet<Integer>> profiles_to_schedules; //scheduleID to list of profileIDs
    private HashMap<Integer, HashSet<Integer>> apps_to_profiles; //profileID to list of apps

    private int numProfilesCreated;
    private int numSchedulesCreated;
    private int numAppsCreated;

    private AppProcessChecker apc;
    private Profile currProf;
    private HashMap<String, Bitmap> iconMap;
    public ArrayList<TimeRange> events;
    public profilesListViewController plvc;
    public schedulesListViewController slvc;
    public Stack<Vector<String>> suggestedProfiles;
    public static boolean colorSchemeCheck = false;
    public static int color = 0;
    //this is a map that has profiles to the number of instances its been turned on
    HashMap<Profile, Long> profileUsage = new HashMap<Profile, Long>();

    private DatabaseHelper mDatabaseHelper;

    //Instance for Singleton Class
    private static FocusModel instance= null;
    private String operation;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = CalendarScopes.all().toArray(new String[CalendarScopes.all().size()]);
    
    static GoogleAccountCredential mCredential;

    /**
     * Singleton instance functions and constructor
     */

    protected FocusModel() {
        //Exists only to defeate instantiation
        numProfilesCreated = 0;
        numSchedulesCreated = 0;
        numAppsCreated = 0;
        currProf = null;
        apc = null;
        iconMap = null;
        schedules = new ArrayList<>();
        profiles = new ArrayList<>();
        apps = new ArrayList<>();


        profiles_to_schedules = new HashMap<>();
        apps_to_profiles = new HashMap<>();
        events = new ArrayList<>();

        createSuggestedProfiles();

        this.start();
    }

    protected FocusModel(AppProcessChecker apc, HashMap<String, Bitmap> iconMap) {
        //Exists only to defeate instantiation
        numProfilesCreated = 0;
        numSchedulesCreated = 0;
        numAppsCreated = 0;
        currProf = null;
        schedules = new ArrayList<>();
        profiles = new ArrayList<>();
        apps = new ArrayList<>();

        this.apc = apc;
        this.iconMap = iconMap;

        profiles_to_schedules = new HashMap<>();
        apps_to_profiles = new HashMap<>();
        events = new ArrayList<>();

        createSuggestedProfiles();

        this.start();
    }

    @Override
    public void run(){
        while (true){
            if (instance != null) {
                instance.updateActivatedProfiles();
                instance.updateWithSchedules();
            }
//            System.out.println("CHECKING FOR NULL: "+apc+" "+instance);
            if(apc != null) {
                apc.blockApplication(instance.getBlockedApps());
                //Updates the ration time for any apps currently set to be ration blocked
                checkForRation(apc.getForegroundedApp());
            }
            //sleeps this thread for 2 seconds on the loop
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static FocusModel getInstance() {
        if (instance == null) {
            instance = new FocusModel();
        }
        return instance;
    }

    public static FocusModel getInstance(AppProcessChecker apc, HashMap<String, Bitmap> iconMap) {
        if (instance == null) {
            instance = new FocusModel(apc, iconMap);
        }
        return instance;
    }

    /**
     *
     * Getter Functions
     */

    public ArrayList<App> getAllApps(){
        return apps;
    }

    public ArrayList<Schedule> getAllSchedules(){
        return schedules;
    }

    public int getNumProfilesCreated(){
        return numProfilesCreated;
    }

    public int getNumAppsCreated(){
        return numAppsCreated;
    }

    public int getNumSchedulesCreated(){
        return numSchedulesCreated;
    }

    public ArrayList <Profile> getAllProfiles() {
        return profiles;
    }

    public ArrayList <Schedule> getSchedules() {
        return schedules;
    }

    public HashMap<String, Bitmap> getIconMap(){
        return iconMap;
    }

    public void setIconMap(HashMap<String, Bitmap> i){
        iconMap = i;
    }

    public Profile getProfile(String profileName) {
        /**
         * Returns the Profile if it exists
         * If it doesn't exist return null
         */
        for (Profile p : profiles) {
            if (p.getProfileName().equals(profileName)) {
                return p;
            }
        }

        return null;
    }

    public Profile getProfile(int profileID) {
        /**
         * Returns the Profile if it exists
         * If it doesn't exist return null
         */
        for (Profile p : profiles) {
            if (p.getProfileID() == profileID) {
                return p;
            }
        }

        return null;
    }

    public Schedule getSchedule(int scheduleID) {
        /**
         * Returns the Schedule if it exists
         * If it doesn't exist return null
         */
        for (Schedule s : schedules) {
            if (s.getScheduleID() == scheduleID) {
                return s;
            }
        }
        return null;
    }

    public Schedule getSchedule(String scheduleName) {
        /**
         * Returns the Schedule if it exists
         * If it doesn't exist return null
         */
        for (Schedule s : schedules) {
            if (s.getScheduleName().equals(scheduleName)) {
                return s;
            }
        }
        return null;
    }

    public Profile getCurrentProfile(){
        return currProf;
    }

    /**
     *
     * Profile Functions
     */
    public void createNewProfile(String profileName) {
    	/*	Create new profile
	    	 * if it already exists, output error message to console
	    	 * otherwise, create the profile with the input name and the next available id
	    	 * add this profile to the arraylist of profiles in focusmodel
	    	 * increment numprofilescreated
    	 */
        if (alreadyExists("Profile", profileName)) {
            System.out.println("Attempted to create a profile that already exists. "
                    + "Do something about this -- " + profileName);
        }
        else if (profileName.equals("")) {
            //
        }
        else {
            profiles.add(new Profile(numProfilesCreated, profileName));
            apps_to_profiles.put(numProfilesCreated, new HashSet<Integer>());
            numProfilesCreated++;
        }
    }

    public void removeProfile(String profileName) {
        int profileID = getIdFromName("Profile", profileName);
        /*	Remove profile from FocusModel
         	* first checks for existence of the profile
         	* remove the profile from profiles array in FocusModel
         	* remove it from any schedule that has the profile
         	* edit the profile-schedule map
         	* remove it from the app-profile map
         	* if it does not exists, outputs an error
		*/

        if(getProfile(profileID).isActivated()){
            getProfile(profileID).deactivate();
        }
        if (alreadyExists("Profile", Integer.toString(profileID))) {
            //remove profile from arraylist in focusmodel
            for (Profile p : profiles) {
                if (p.getProfileID().equals(profileID)) {
                    profiles.remove(p);
                    break;
                }
            }

            //remove profile from any schedule that contains it
            for (Schedule s : schedules) {
                if (s.getProfileIDs().contains(profileID)) {
                    if(s.invisible){
                        removeSchedule(s.getScheduleID());
                    }
                    s.removeProfile(profileID);
                }
            }

            //edit the profile to schedule map
            for (HashSet<Integer> val : profiles_to_schedules.values()) {
                if (val.contains(profileID)) val.remove(profileID);
            }

            //edit the app to profile map
            apps_to_profiles.remove(profileID);
        } else {
            System.out.println("Error: Tried to remove profile that does not exist. ProfID: " + profileID);
        }
    }

    public void activateProfile(int profileID) {
          /*	Blocks profile from FocusModel
         	* first checks for existence of the profile
         	* If it exists then set it to be blocked
		*/
        if (alreadyExists("Profile", Integer.toString(profileID))) {
            //Find and activate profile from arraylist in focusmodel
            for (Profile p : profiles) {
                if (p.getProfileID().equals(profileID)) {
                    p.activate();
                    break;
                }
            }
        }
    }

    public void activateProfile(String profileName) {
          /*	Blocks profile from FocusModel
         	* first checks for existence of the profile
         	* If it exists then set it to be blocked
		*/
        for (Profile p : profiles) {
            if (p.getProfileName().equals(profileName)) {
                p.activate();
                break;
            }
        }
    }

    public void deactivateProfile(int profileID) {
        /*	Unblocks profile from FocusModel
         	* first checks for existence of the profile
         	* If it exists then unblock it
		*/
        if (alreadyExists("Profile", Integer.toString(profileID))) {
            //Find and activate profile from arraylist in focusmodel
            for (Profile p : profiles) {
                if (p.getProfileID().equals(profileID)) {
                    p.deactivate();
                    break;
                }
            }
        }
    }

    public void deactivateProfile(String profileName) {
          /*	Unblocks profile from FocusModel
         	* first checks for existence of the profile
         	* If it exists then unblock it
		*/
        for (Profile p : profiles) {
            if (p.getProfileName().equals(profileName)) {
                p.deactivate();
                break;
            }
        }
    }

    //TODO
    //Input Context with getApplicationContext()
    //So function call is addAppToProfile(getApplicationContext, appName, profileName);
    public void addAppToProfile(Context context, String packageName, String profileName) {
        String appName = getAppNameFromPackage(context, packageName);
        int appID = getIdFromName("App", appName);
        int profileID = getIdFromName("Profile", profileName);
        App currApp = null;
        Profile currProf = null;

        //locate the schedule and profile we are working with
        for (Profile p : profiles) {
            if (p.getProfileID().equals(profileID)) {
                currProf = p;
                break;
            }
        }
        for (App a : apps) {
            if (a.getAppID() == appID) {
                currApp = a;
                break;
            }
        }
        if(appID == -1){
            currApp = new App(numAppsCreated, appName, packageName);
            apps.add(currApp);
            numAppsCreated++;
        }

        //make sure that the profile and app both exist
        //System.out.println("appname:" + appName + " profName: " + profileName);
        //System.out.println(currApp +" "+ currProf);
        if (currProf != null && currApp != null) {
            //if the profile does not already have this app
            if (!currProf.getAppIDs().contains(currApp.getAppID())) {
                //System.out.println("BACKEND: adding new app to profile" + currProf.getApps().size());
                currProf.addApp(currApp);
                //System.out.println("BACKEND: added new app to profile" + currProf.getApps().size());
            }

            //if this scheduleID is not already in the map
            if (!apps_to_profiles.containsKey(profileID)) {
                //adds a scheduleID as key and an arraylist containing the profileId as the value
                apps_to_profiles.put(currProf.getProfileID(), new HashSet<Integer>(currApp.getAppID()));
            } else {
                //adds profileID to the list of IDs associated with a schedule if it is not already there
                if (!apps_to_profiles.get(currProf.getProfileID()).contains(currApp.getAppID())) {
                    apps_to_profiles.get(currProf.getProfileID()).add(currApp.getAppID());
                }
            }
        }
        //System.out.println("BACKEND: num apps in profile: "+currProf.getApps().size());
    }

    //Input Context with getApplicationContext()
    //So function call is removeAppFromProfile(getApplicationContext, packageName, profileName);
    public void removeAppFromProfile(Context context, String packageName, String profileName){
        int appID = getIdFromName("App", getAppNameFromPackage(context, packageName));
        int profileID = getIdFromName("Profile", profileName);

        //if app is in profile, remove from that profile
        for(App a: currProf.getApps()){
            if(a.getAppID() == appID){
                currProf.removeApp(appID);
                break;
            }
        }

        //if the schedule is in map and the app is too, remove the app from arraylist associated with profile
//        if(apps_to_profiles.get(profileID) != null){
        if(apps_to_profiles.get(profileID).contains(appID)) {
            apps_to_profiles.get(profileID).remove(appID);
        }
//        }
    }

    public ArrayList<App> getAppsFromProfile(int profileID) {
        /**
         * Returns the list of apps in a profile
         */

        for (Profile p : profiles) {
            if (p.getProfileID() == profileID) {
                return p.getApps();
            }
        }

        return null;
    }

    public ArrayList<App> getAppsFromProfile(String ProfileName) {
        /**
         * Returns the list of apps in a profile
         */

        for (Profile p : profiles) {
            if (p.getProfileName().equals(ProfileName)) {
                return p.getApps();
            }
        }

        return null;
    }

    public void setCurrentProfile(String profName){
        for(Profile p: profiles){
            if (p.getProfileName().equals(profName)){
                currProf = p;
                return;
            }
        }
        //System.out.println("Error in setCurrentProfile: could not find profile name");
    }

    /**
     *
     * Schedule Functions
     */

    public void createNewSchedule(String scheduleName) {
    /*	Create new schedule
    	 * if it already exists, output error message to console
    	 * otherwise, create the schedule with the input name and the next available id
    	 * add this schedule to the arraylist of schedules in focusmodel
    	 * increment numschedulescreated
	 */
        if (alreadyExists("Schedule", scheduleName)) {
            System.out.println("Attempted to create a Schedule that already exists. "
                    + "Do something about this -- " + scheduleName);
        } else if(scheduleName.equals("")){
            System.out.println("Attempted to create a Schedule with a blank name. "
                    + "Do something about this");
        }
        else {
            schedules.add(new Schedule(numSchedulesCreated, scheduleName));
            profiles_to_schedules.put(numSchedulesCreated, new HashSet<Integer>());
            numSchedulesCreated++;
        }
    }

//    public void createNewSchedule (String scheduleName, ArrayList<String> days, int startHour, int startMinute,
//                         int endHour, int endMinute){
//
//        if (alreadyExists("Schedule", scheduleName)) {
//            System.out.println("Attempted to create a Schedule that already exists. "
//                    + "Do something about this -- " + scheduleName);
//        } else {
//            Schedule newSched = new Schedule(numSchedulesCreated, Long.toString(System.currentTimeMillis()));
//            schedules.add(newSched);
//            newSched.addProfile(currProf);
//            newSched.addTimeRange(days, startHour, startMinute, endHour, endMinute);
//            System.out.println("ASDFASDFASDFAFF "+ newSched);
//            profiles_to_schedules.put(numSchedulesCreated, new HashSet<Integer>());
//
//            numSchedulesCreated++;
//        }
//    }
//    public void createNewSchedule (String scheduleName, ArrayList<String> days, int startHour, int startMinute,
//                                   int endHour, int endMinute, boolean invis){
//        System.out.println("CREATING A NEW SCHEDULE");
//        if (alreadyExists("Schedule", scheduleName)) {
//            System.out.println("Attempted to create a Schedule that already exists. "
//                    + "Do something about this -- " + scheduleName);
//        } else {
//            Schedule newSched = new Schedule(numSchedulesCreated, Long.toString(System.currentTimeMillis()), invis);
//            schedules.add(newSched);
//            newSched.addProfile(currProf);
//            newSched.addTimeRange(days, startHour, startMinute, endHour, endMinute);
//            System.out.println("ASDFASDFASDFAFF "+ newSched);
//            profiles_to_schedules.put(numSchedulesCreated, new HashSet<Integer>());
//
//            numSchedulesCreated++;
//        }
//    }

    public void removeSchedule(int scheduleID) {
	    /*	Remove schedule from FocusModel
	     	* first checks for existence of the schedule
	     	* remove the schedule from schedules array in FocusModel
	     	* remove it from the profile-schedule map
	     	* if it does not exist, outputs an error
		*/
        if (alreadyExists("Schedule", Integer.toString(scheduleID))) {
            //remove schedule from arraylist
            for (Schedule s : schedules) {
                if (s.getScheduleID() == scheduleID) {
                    schedules.remove(s);
                    s.scheduleDeleted();
                    break;
                }
            }

            //remove schedule from profiles_to_schedules map
            profiles_to_schedules.remove(scheduleID);
        } else {
            System.out.println("Error: Tried to remove Schedule that does not exist. SchedID: " + scheduleID);
        }
    }

    public void removeProfileFromSchedule(String profileName, String scheduleName){
        int scheduleID = getIdFromName("Schedule", scheduleName);
        int profileID = getIdFromName("Profile", profileName);

        Schedule currSched = null;

        for (Schedule s : schedules) {
            if (s.getScheduleID() == scheduleID) {
                currSched = s;
                break;
            }
        }

        //if app is in profile, remove from that profile
        for(Profile p: currSched.getProfiles()){
            if(p.getProfileID() == profileID){
                currSched.removeProfile(profileID);
                break;
            }
        }

        //if the schedule is in map and the app is too, remove the app from arraylist associated with profile
//        if(apps_to_profiles.get(profileID) != null){
        if(profiles_to_schedules.get(scheduleID).contains(profileID)) {
            profiles_to_schedules.get(scheduleID).remove(profileID);
        }
//        }
    }

    public void addProfileToSchedule(String profileName, String scheduleName, TimeRange timeRange) {
        int profileID = getIdFromName("Profile", profileName);
        int scheduleID = getIdFromName("Schedule", scheduleName);
        Profile currProf = null;
        Schedule currSched = null;

        //locate the schedule and profile we are working with
        for (Profile p : profiles) {
            if (p.getProfileID() == profileID) {
                currProf = p;
                break;
            }
        }
        for (Schedule s : schedules) {
            if (s.getScheduleID() == scheduleID) {
                currSched = s;
                break;
            }
        }

        //make sure that the profile and schedule both exist
        if (currProf != null && currSched != null) {
            //if the schedule does not already have this profile
            if (!currSched.getProfileIDs().contains(currProf.getProfileID())) {
                currSched.addProfile(currProf, timeRange);
            } else {
                System.out.println("found currprof in the set");
            }

            //if this scheduleID is not already in the map
            if (!profiles_to_schedules.containsKey(scheduleID)) {
                //adds a scheduleID as key and an arraylist containing the profileId as the value
                profiles_to_schedules.put(currSched.getScheduleID(), new HashSet<Integer>(currProf.getProfileID()));
            } else {
                //adds profileID to the list of IDs associated with a schedule if it is not already there
                if (!profiles_to_schedules.get(currSched.getScheduleID()).contains(currProf.getProfileID())) {
                    profiles_to_schedules.get(currSched.getScheduleID()).add(currProf.getProfileID());
                }
            }
        } else {
            System.out.println("Error in addProfileToSchedule -- either prof or sched reutrn null");
            System.out.println("CurrProf: " + currProf + " CurrSched: " + currSched);
        }
    }

    public void activateSchedule(int scheduleID) {
          /*	Blocks profile from FocusModel
         	* first checks for existence of the profile
         	* If it exists then set it to be blocked
		*/
        if (alreadyExists("Schedule", Integer.toString(scheduleID))) {
            //Find and activate profile from arraylist in focusmodel
            for (Schedule s : schedules) {
                if (s.getScheduleID() == scheduleID) {
                    s.setActivated(true);
                    break;
                }
            }
        }
    }

    public void activateSchedule(String scheduleName) {
          /*	Blocks profile from FocusModel
         	* first checks for existence of the profile
         	* If it exists then set it to be blocked
		*/
        for (Schedule s : schedules) {
            if (s.getScheduleName().equals(scheduleName)) {
                s.setActivated(true);
                break;
            }
        }
    }

    public void deactivateSchedule(int scheduleID) {
        /*	Unblocks profile from FocusModel
         	* first checks for existence of the profile
         	* If it exists then unblock it
		*/
        if (alreadyExists("Schedule", Integer.toString(scheduleID))) {
            //Find and activate profile from arraylist in focusmodel
            for (Schedule s : schedules) {
                if (s.getScheduleID() == scheduleID) {
                    s.setActivated(false);
                    break;
                }
            }
        }
    }

    public void deactivateSchedule(String scheduleName) {
          /*	Unblocks profile from FocusModel
         	* first checks for existence of the profile
         	* If it exists then unblock it
		*/
        for (Schedule s : schedules) {
            if (s.getScheduleName().equals(scheduleName)) {
                s.setActivated(false);
                break;
            }
        }
    }

    /**
     *
     * App Functions
     */
    //Input Context with getApplicationContext()
    //So function call is createNewApp(getApplicationContext, packageName);
    public void createNewApp(Context context, String packageName) {
    /*	Create new schedule
    	 * if it already exists, output error message to console
    	 * otherwise, create the app with the input name and the next available id
    	 * add this app to the arraylist of schedules in focusmodel
    	 * increment numappscreated
	 */

        String appName = getAppNameFromPackage(context, packageName);
        if (alreadyExists("App", appName)) {
            System.out.println("Attempted to create a App that already exists. "
                    + "Do something about this -- " + appName);
        } else {
            apps.add(new App(numAppsCreated, appName, packageName));
            numAppsCreated++;
        }
    }

    public void removeApp(int appID) {
	    /*	Remove app from FocusModel
	     	* first checks for existence of the app
	     	* remove the app from apps array in FocusModel
	     	* remove the app from any profile that has it
	     	* remove it from the app-profile map
	     	* if it does not exist, outputs an error
		*/
        if (alreadyExists("App", Integer.toString(appID))) {
            //remove app from FocusModel
            for (App a : apps) {
                if (a.getAppID() == appID) {
                    apps.remove(a);
                    break;
                }
            }

            //remove app from any profile that contains it
            for (Profile p : profiles) {
                if (p.getAppIDs().contains(appID)) {
                    p.removeApp(appID);
                }
            }

            //remove app from the map
            for (HashSet<Integer> val : apps_to_profiles.values()) {
                if (val.contains(appID)) val.remove(appID);
            }
        } else {
            System.out.println("Error: Tried to remove App that does not exist. SchedID: " + appID);
        }
    }

    public HashSet<String> getBlockedApps(){
        HashSet<String> blockedSet = new HashSet<>();
        for(App a: apps) {
            if (a.isBlocked()) {
                blockedSet.add(a.getPackageName());
            }
        }
        return blockedSet;
    }

    public int getIdFromName(String type, String name){
        if (type.equals("Profile")){
            for(Profile p: profiles){
                if(p.getProfileName().equals(name)) return p.getProfileID();
            }
//            System.out.println("Error in getIdFromName: Did not find profile name");
            return -1;
        } else if(type.equals("App")){
            for(App a: apps){
                if(a.getAppName().equals(name)) return a.getAppID();
            }
            System.out.println("Error in getIdFromName: Did not find app name");
            return -1;
        }else if(type.equals("Schedule")){
            for(Schedule s: schedules){
                if(s.getScheduleName().equals(name)) return s.getScheduleID();
            }
            System.out.println("Error in getIdFromName: Did not find schedule name");
            return -1;
        }

        return 0;
    }

    public void addNotification(String packageName, Notification notification) {
        /**
         * Adds a notification to the corresponding app
         */
        for (App a : apps) {
            if (a.getPackageName().equals(packageName)) {
                a.addNotification(notification);
            }
        }
    }

    /**
     *
     * Helper Functions
     */

    private boolean alreadyExists(String type, String name) {
    	/*	Returns true if a profile, schedule, or app exists
    	 	* for each of profile, app, or schedule
    	 	* loop through every instance in the arraylist of that type
    	 	* if the name of id is found then return true else false
    	 */
        if (type.equals("Profile")) {
            for (Profile p : profiles) {
                if (p.getProfileName().equals(name) ||
                        Integer.toString(p.getProfileID()).equals(name)) {
//					System.out.println(p.getProfileName()+ " "+ Integer.toString(p.getProfileID()));
                    return true;
                }
            }
            return false;
        } else if (type.equals("App")) {
            for (App a : apps) {
                if (a.getAppName().equals(name) ||
                        Integer.toString(a.getAppID()).equals(name)) {
//					System.out.println(p.getProfileName()+ " "+ Integer.toString(p.getProfileID()));
                    return true;
                }
            }
            return false;
        } else if (type.equals("Schedule")) {
            for (Schedule s : schedules) {
                if (s.getScheduleName().equals(name) ||
                        Integer.toString(s.getScheduleID()).equals(name)) {
//					System.out.println(p.getProfileName()+ " "+ Integer.toString(p.getProfileID()));
                    return true;
                }
            }
            return false;
        } else {
            System.out.println("Incorrect input for function alreadyExists(String type, "
                    + "String name) in class FocusModel");
            return true;
        }
    }

    private void updateActivatedProfiles() {
        for (Profile p : instance.profiles) {
            p.updateActivation();
            long activationTime = p.getActivationTime();
            profileUsage.put(p, activationTime);
        }

    }
    //returns the profileUsage hashmap
    public HashMap<Profile, Long> getProfileUsage() {
        return profileUsage;
    }

    private void updateWithSchedules() {
        for (Schedule s : instance.schedules) {
            if (s.blockRanges()){
                System.out.println("BLOCKING FOR SCHEDULE: " + s.getScheduleName());
            }
//            if (s.isInTimeRange()) {
//                System.out.println("BLOCKING FOR SCHEDULE: " + s.getScheduleName());
//                s.blockProfiles();
//            else {
//                System.out.println("NOT BLOCKING FOR SCHEDULE: " + s.getScheduleName());
//                s.unblockProfiles();
//            }
        }
    }

    public void addTimeRangeToSchedule(TimeRange tr, String scheduleName, ArrayList<Profile> p){
        Schedule currSched = null;
        for(Schedule s: schedules){
            if (s.getScheduleName().equals(scheduleName)){
                currSched = s;
                break;
            }
        }
        currSched.addTimeRange(tr, p);

    }

    public static String getAppNameFromPackage(Context context, String packageName){
        //Check if context is null, if so simply return the packageName
        if (context == null) {
            return packageName;
        }

        //Get App Name
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (final NameNotFoundException e) {}
        final String appName = (String)((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");

        return appName;
    }

    public void createDatabase(DatabaseHelper dbHelper){
        System.out.println("*****************************************");
//        mDatabaseHelper = new DatabaseHelper(context);

//        Boolean insertData = dbHelper.addDataToIdTable(Integer.toString(numProfilesCreated),
//                Integer.toString(numAppsCreated), Integer.toString(numSchedulesCreated));
//
//
//
//        if(insertData){
//            System.out.println("Data Successfully input");
//        }else{
//            System.out.println("Data Did not enter db");
//        }
//        dbHelper.writeAllData(this);
        //dbHelper.printAllTables();
    }

    public boolean appInProfile(String profName, String appName){
        int profileID = getIdFromName("Profile", profName);
        int appID = getIdFromName("App", appName);

        if(profileID == -1 || appID == -1){
            System.out.println("Error in appInProfile function in fm: appname or profilename not found"+profName+" "+appName);
            return false;
        }
        return apps_to_profiles.get(profileID).contains(appID);


    }

    public long remainingTime(String profName){
        System.out.println("in remaining time function");
        //Returns a pair of integers
        //first is the latest hour that this profile will be engaged
        //second is the latest minute of that hour
        ArrayList<Integer> hm = new ArrayList<>();
        int profileID = getIdFromName("Profile", profName);

        String lastDay = "";
        int lastHour = -1;
        int lastMinute = -1;



        for(Map.Entry<Integer, HashSet<Integer>> entry: profiles_to_schedules.entrySet()){
            hm.clear();
            int currScheduleID = entry.getKey();
            HashSet<Integer> currProfs = entry.getValue();
            Schedule currSchedule = null;

            if(currProfs.contains(profileID)){
                for(Schedule s: schedules){
                    if (currScheduleID == s.getScheduleID()) {
                        currSchedule = s;
                    }
                }
                if(currSchedule != null){
                    if(currSchedule.isInTimeRange()){
                        hm = currSchedule.getLatestHM();
                    }
                }
            }
            if(hm != null && hm.size() > 1 && hm.get(0) >= lastHour){
                lastHour = hm.get(0);
                if(hm.get(1) > lastMinute) lastMinute = hm.get(1);
            }
        }
        hm.clear();
        hm.add(lastHour);
        hm.add(lastMinute);

        int hours = hm.get(0);
        int minutes = hm.get(1);

        if (hours == -1 && minutes == -1) {
            return getProfile(profileID).getTimeRemaining();
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, hours);
        c.set(Calendar.MINUTE, minutes);

        long rt = c.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

        long prt = getProfile(profileID).getTimeRemaining();



        System.out.println("REMAINING TIME IS ..." + rt);

        if (rt > prt) {
            return rt;
        }
        return prt;


    }

    public void changeScheduleName(String n1, String n2) {
        for(Schedule s: schedules){
            if(s.getScheduleName().equals(n1) && !n2.equals("") && !alreadyExists("Schedule", n2)){
                s.setScheduleName(n2);
            }
        }
    }

    public void resetAllCreated() {
        numSchedulesCreated = 0;
        numAppsCreated = 0;
        numProfilesCreated = 0;
    }

    public void checkForRation(String packageName) {
        for (int i = 0; i < profiles.size(); i++) {
            //if a profile is set to be rationed check if it contains the current app
            if (profiles.get(i).isRationed && profiles.get(i).rationTime > 0) {
                //check if any of the apps in the profile is the current app open
                for (int j = 0; j < profiles.get(i).getApps().size(); j++) {
                    //if it is the current app and the profile is set to be rationed, then updated RationTime
                    if (profiles.get(i).getApps().get(j).getPackageName().equalsIgnoreCase(packageName)) {
                        profiles.get(i).rationTime -= 2;
                        if (profiles.get(i).rationTime <= 0) {
                            profiles.get(i).activate();
                            profiles.get(i).addTimeToEndOfDay();
                            Intent intent = new Intent(apc.mainActivity, DialogActivity.class);
                            apc.mainActivity.startActivity(intent);
                        }
                    }
                }
            }
        }
    }

    public void rationProfile(String profileName, int hours, int minutes) {
        for (Profile p : profiles) {
            if (p.getProfileName().equals(profileName)) {
                p.rationProfile(hours, minutes);
                break;
            }
        }
    }

    public void unRationProfile(String profileName) {
        for (Profile p : profiles) {
            if (p.getProfileName().equals(profileName)) {
                p.unRationProfile();
                break;
            }
        }
    }

    public void createSuggestedProfiles() {
        suggestedProfiles = new Stack<>();

        Vector<String> profile2 = new Vector<>();
        profile2.add("Entertainment");
        profile2.add("com.google.android.youtube");
        profile2.add("com.google.android.videos");
        profile2.add("com.google.android.apps.photos");
        profile2.add("com.google.android.music");

        Vector<String> profile3 = new Vector<>();
        profile3.add("Google");
        profile3.add("com.google.android.youtube");
        profile3.add("com.google.android.apps.maps");
        profile3.add("com.google.android.calendar");
        profile3.add("com.android.chrome");

        Vector<String> profile1 = new Vector<>();
        profile1.add("Phone Services");
        profile1.add("com.android.phone");
        profile1.add("com.google.android.apps.messaging");

        Vector<String> profile4 = new Vector<>();
        profile4.add("Photo and Video");
        profile4.add("com.android.camera2");
        profile4.add("com.google.android.videos");
        profile4.add("com.google.android.apps.photos");

        suggestedProfiles.add(profile4);
        suggestedProfiles.add(profile1);
        suggestedProfiles.add(profile3);
        suggestedProfiles.add(profile2);
    }

    public void addEvent(String eventName, int day, int startHour, int startMinute, int endHour, int endMinute) {
        events.add(new TimeRange(eventName, day, startHour, startMinute, endHour, endMinute));
    }

    public TimeRange getEvent(String eventName) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).eventName == eventName) {
                return events.get(i);
            }
        }
        return null;
    }

    public ArrayList<TimeRange> getEvents() {
        return events;
    }

    public void importFromCSV() throws IOException {
        try {
            CSVReader csv = new CSVReader(new FileReader(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/output.csv"));

            for(String[] line: csv.readAll()){
                if(line[0].equals("app")){
                    if(line[1].equals("meta")){
                        App a = new App(Integer.parseInt(line[2]), line[3], Boolean.parseBoolean(line[4]), line[5]);
                        apps.add(a);
                    }else{
                        
                    }
                }else if (line[0].equals("prof")){
                    if(line[1].equals("scheds")){
                        currProf = null;
                        for(Profile p: profiles){
//                            System.out.println("Keep failing here: "+Integer.parseInt(line[2])+ " | " + p.getProfileID() );
                            if (p.getProfileID() == Integer.parseInt(line[2])){
                                currProf = p;
                                break;
                            }
                        }
                        for(int i = 3; i < line.length; i++) {
                            currProf.addScheduleID(Integer.parseInt(line[i]));
//                            addProfileToSchedule();
                        }
                    }else{
                        Profile p = new Profile(Integer.parseInt(line[1]), line[2]);
                        for(int i = 3; i < line.length; i++){
                            for(App a: apps){
                                if(a.getAppName().equals(line[i])) p.addApp(a);
                            }
                        }
                        profiles.add(p);
                        System.out.println("CREATING PROFILE ON IMPORT: "+p.getProfileID()+" | "+p.getProfileName());

                    }
                }else if(line[0].equals("sched")){
                    System.out.println("creating Schedule: "+ line[2]);
                    Schedule newS = new Schedule(Integer.parseInt(line[1]), line[2], Boolean.parseBoolean(line[3]));
                    newS.setActivated(Boolean.parseBoolean(line[4]));
                    newS.setColor(Integer.parseInt(line[5]));
                    schedules.add(newS);
                }else if(line[0].equals("tr")){
                    ArrayList<String> days = new ArrayList<>();
                    if(line[6].toLowerCase().equals("true")) days.add("sunday");
                    if(line[7].toLowerCase().equals("true")) days.add("monday");
                    if(line[8].toLowerCase().equals("true")) days.add("tuesday");
                    if(line[9].toLowerCase().equals("true")) days.add("wednesday");
                    if(line[10].toLowerCase().equals("true")) days.add("thursday");
                    if(line[11].toLowerCase().equals("true")) days.add("friday");
                    if(line[12].toLowerCase().equals("true")) days.add("saturday");

                    TimeRange tr = new TimeRange(days, Integer.parseInt(line[2]), Integer.parseInt(line[3]), Integer.parseInt(line[4]),
                            Integer.parseInt(line[5]));

                    ArrayList<Profile> p = new ArrayList<>();
                    for (int i = 13; i < line.length; i++){
                        Profile currProf = null;
                        for(Profile pr: profiles){
                            if (pr.getProfileID() == Integer.parseInt(line[i])){
                                currProf = pr;
                                break;
                            }
                        }
                        p.add(currProf);
                    }

                    Schedule currSched = null;
                    for (Schedule s: schedules){
                        if(s.getScheduleID() == Integer.parseInt(line[1])){
                            currSched = s;
                            break;
                        }
                    }

                    currSched.addTimeRange(tr, p);
                }


//                for(String s: line){
//                    System.out.println("READING ITEM FROM FILE: "+ s);
//                }
            }

        //TODO REFRESH
            if(plvc != null){
                plvc.refreshData();
            }
            if(slvc != null){
                slvc.refreshData();
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /* Google Calender Crap */

    /*
    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     *
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            System.out.println("1");
            acquireGooglePlayServices();
            System.out.println("2");
        } else if (mCredential.getSelectedAccountName() == null) {
            System.out.println("3");
            chooseAccount();
            System.out.println("4");
        } else if (! isDeviceOnline()) {
            System.out.println("5");
            mOutputText.setText("No network connection available.");
        } else {
            System.out.println("6");
            if (operation.equals("IMPORT")) {
                System.out.println("import");
                new MakeGetRequestTask(mCredential).execute();
            } else if (operation.equals("EXPORT")) {
                System.out.println("export");
                for (Schedule s : focusModel.getAllSchedules()) {
                    for (TimeRange tr : s.getTimeRanges()) {
                        try {
                            System.out.println(s.getScheduleName() + " : " + tr.getProfiles() + " : " + tr.getDates());
                            insertToGoogleCalendarAsync(tr, s.getScheduleName(), tr.getProfiles());
                        } catch (Exception e) {
                            System.out.println("Exception: " + e);
                        }
                    }
                }
            }
            System.out.println("7");
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     *
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            System.out.println("8");
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                System.out.println("9");
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
                System.out.println("10");
            } else {
                System.out.println("11");
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            System.out.println("12");
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    new String[]{Manifest.permission.GET_ACCOUNTS});
            System.out.println("13");
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     *
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     *
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     *
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     *
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     *
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     *
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     *
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     *
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                GoogleCalendarActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    void insertToGoogleCalendarAsync(TimeRange tr, String sn, ArrayList<Profile> p) throws IOException {
        new MakeInsertRequestTask(mCredential, tr, sn, p).execute();
    }


    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     *
    public class MakeGetRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeGetRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         *
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         *
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));

                DateTime end = event.getEnd().getDateTime();
                if (end == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    end = event.getEnd().getDate();
                }

                //arraylist of days, int starthour, startminute, endhour, endminute
                Date startDate = new Date(start.getValue());
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(startDate);
                int day = cal1.get(Calendar.DAY_OF_WEEK);
                int startHour = cal1.get(Calendar.HOUR_OF_DAY);
                int startMinute = cal1.get(Calendar.MINUTE);
                Date endDate = new Date(end.getValue());
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(endDate);
                int endHour = cal2.get(Calendar.HOUR_OF_DAY);
                int endMinute = cal2.get(Calendar.MINUTE);
                System.out.println("Adding event: " + event.getSummary() + " : " + day);
                addEvent(event.getSummary(), day, startHour, startMinute, endHour, endMinute);
            }
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
           // mOutputText.setText("");
           // mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            //System.out.println("Post get request execute");
            // mProgress.hide();
            if (output == null || output.size() == 0) {
                //mOutputText.setText("No results returned.");
            } else {
                //mOutputText.setText("Data retrieved using the Google Calendar API!\n Check your calendar view to see events");
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            FocusModel.REQUEST_AUTHORIZATION);
                } else {
                    //mOutputText.setText("The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }

    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     *
    public class MakeInsertRequestTask extends AsyncTask<Void, Void, Boolean> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;
        private TimeRange timeRange;
        private String scheduleName;
        private ArrayList<Profile> profilesList;


        MakeInsertRequestTask(GoogleAccountCredential credential, TimeRange tr, String sn, ArrayList<Profile> p) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            timeRange = tr;
            scheduleName = sn;
            profilesList = p;
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         *
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                insertToGoogleCalendar(timeRange, scheduleName, profilesList);
                return true;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
           //  mOutputText.setText("");
           // mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            //mProgress.hide();
            if (false) {
                mOutputText.setText("Sync unsuccessful");
                mProgress.hide();
            } else {
                mOutputText.setText("Focus! schedule posted to your Google Calendar!");
                mProgress.hide();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleCalendarActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }

        public void insertToGoogleCalendar(TimeRange tr, String scheduleName, ArrayList<Profile> p) throws IOException {

            String summary = "Focus! Schedule: " + scheduleName;
            String location = "Wherever you be at my boi!";
            String des = p.toString();
            for (Date d : tr.getDates().keySet()) {
                DateTime startDate = new DateTime(d);
                Event event = new Event()
                        .setSummary(summary)
                        .setLocation(location)
                        .setDescription(des);
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDate)
                        .setTimeZone("America/Los_Angeles");
                event.setStart(start);
                EventDateTime end = new EventDateTime()
                        .setDateTime(new DateTime(tr.getDates().get(d)))
                        .setTimeZone("America/Los_Angeles");
                event.setEnd(end);
                String[] recurrence = new String[]{"RRULE:FREQ=WEEKLY;UNTIL=20110701T170000Z"};
                event.setRecurrence(Arrays.asList(recurrence));
                EventReminder[] reminderOverrides = new EventReminder[]{
                        new EventReminder().setMethod("email").setMinutes(24 * 60),
                        new EventReminder().setMethod("popup").setMinutes(10),
                };
                Event.Reminders reminders = new Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(Arrays.asList(reminderOverrides));
                event.setReminders(reminders);
                String calendarId = "primary";
                //event.send
                if (mService != null) {
                    System.out.println("inserting into calendar : " + event);
                    mService.events().insert(calendarId, event).setSendNotifications(true).execute();
                }

                //TODO Event recurringEvent = service.events().insert("primary", event).execute();
            }
        }
    } /*

   /*************************************************/
}
