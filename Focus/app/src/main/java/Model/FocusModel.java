package Model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static movingbattleship.org.focus.R.drawable.schedule;

/**
 * Created by aaronrschrock on 10/6/17.
 */

/**
 * FocusModel is the class that contains profiles, schedules, and the main functionality
 */

public class FocusModel extends Thread{
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

    private DatabaseHelper mDatabaseHelper;

    //Instance for Singleton Class
    private static FocusModel instance= null;

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
        } else {
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
        } else {
            schedules.add(new Schedule(numSchedulesCreated, scheduleName));
            profiles_to_schedules.put(numSchedulesCreated, new HashSet<Integer>());
            numSchedulesCreated++;
        }
    }

    public void createNewSchedule (String scheduleName, ArrayList<String> days, int startHour, int startMinute,
                         int endHour, int endMinute){

        if (alreadyExists("Schedule", scheduleName)) {
            System.out.println("Attempted to create a Schedule that already exists. "
                    + "Do something about this -- " + scheduleName);
        } else {
            Schedule newSched = new Schedule(numSchedulesCreated, Long.toString(System.currentTimeMillis()));
            schedules.add(newSched);
            newSched.addProfile(currProf);
            newSched.addTimeRange(days, startHour, startMinute, endHour, endMinute);
            System.out.println("ASDFASDFASDFAFF "+ newSched);
            profiles_to_schedules.put(numSchedulesCreated, new HashSet<Integer>());

            numSchedulesCreated++;
        }
    }
    public void createNewSchedule (String scheduleName, ArrayList<String> days, int startHour, int startMinute,
                                   int endHour, int endMinute, boolean invis){
        System.out.println("CREATING A NEW SCHEDULE");
        if (alreadyExists("Schedule", scheduleName)) {
            System.out.println("Attempted to create a Schedule that already exists. "
                    + "Do something about this -- " + scheduleName);
        } else {
            Schedule newSched = new Schedule(numSchedulesCreated, Long.toString(System.currentTimeMillis()), invis);
            schedules.add(newSched);
            newSched.addProfile(currProf);
            newSched.addTimeRange(days, startHour, startMinute, endHour, endMinute);
            System.out.println("ASDFASDFASDFAFF "+ newSched);
            profiles_to_schedules.put(numSchedulesCreated, new HashSet<Integer>());

            numSchedulesCreated++;
        }
    }

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

    public void addProfileToSchedule(String profileName, String scheduleName) {
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
                currSched.addProfile(currProf);
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
        }

    }

    private void updateWithSchedules() {
        for (Schedule s : instance.schedules) {
            if (s.isInTimeRange()) {
                System.out.println("BLOCKING FOR SCHEDULE: " + s.getScheduleName());
                s.blockProfiles();
            } else {
                System.out.println("NOT BLOCKING FOR SCHEDULE: " + s.getScheduleName());
                s.unblockProfiles();
            }
        }
    }

    public static String getAppNameFromPackage(Context context, String packageName){

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
        dbHelper.writeAllData(this);
        dbHelper.printAllTables();
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

    public ArrayList<Integer> endOfTimer(String profName){
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
        return hm;
    }
}
