package Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by aaronrschrock on 10/6/17.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * FocusModel is the class that contains profiles, schedules, and the main functionality
 */

public class FocusModel {
    //Main ArrayLists holding all objects
    private ArrayList<Schedule> schedules;
    private ArrayList<Profile> profiles;
    private ArrayList<App> apps;

    //Maps each each object to the others that hold it
    private HashMap<Integer, HashSet<Integer>> profiles_to_schedules; //scheduleID to list of profileIDs
    private HashMap<Integer, HashSet<Integer>> apps_to_profiles;

    private int numProfilesCreated;
    private int numSchedulesCreated;
    private int numAppsCreated;

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
        schedules = new ArrayList<>();
        profiles = new ArrayList<>();
        apps = new ArrayList<>();

        profiles_to_schedules = new HashMap<>();
        apps_to_profiles = new HashMap<>();
    }

    public static FocusModel getInstance() {
        if (instance == null) {
            instance = new FocusModel();
        }
        return instance;
    }


    /**
     *
     * Getter Functions
     */
    public ArrayList <Profile> getAllProfiles() {
        return profiles;
    }

    public ArrayList <Schedule> getSchedules() {
        return schedules;
    }

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

    public void removeProfile(int profileID) {
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
                    break;
                }
            }

            //remove schedule from profiles_to_schedules map
            profiles_to_schedules.remove(scheduleID);
        } else {
            System.out.println("Error: Tried to remove Schedule that does not exist. SchedID: " + scheduleID);
        }
    }

    public void createNewApp(String appName) {
    /*	Create new schedule
    	 * if it already exists, output error message to console
    	 * otherwise, create the app with the input name and the next available id
    	 * add this app to the arraylist of schedules in focusmodel
    	 * increment numappscreated
	 */
        if (alreadyExists("App", appName)) {
            System.out.println("Attempted to create a App that already exists. "
                    + "Do something about this -- " + appName);
        } else {
            apps.add(new App(numAppsCreated, appName));
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

    public void addProfileToSchedule(int profileID, int scheduleID) {

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

    public void addAppToProfile(int appID, int profileID) {
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

        //make sure that the profile and app both exist
        if (currProf != null && currApp != null) {
            //if the profile does not already have this app
            if (!currProf.getAppIDs().contains(currApp.getAppID())) {
                currProf.addApp(currApp);
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

    }

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
}