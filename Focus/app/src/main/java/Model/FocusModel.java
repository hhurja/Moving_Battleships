package Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by aaronrschrock on 10/6/17.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * FocusModel is the class that contains profiles, schedules, and the main fucntionality
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

    public FocusModel(){
        numProfilesCreated = 0;
        numSchedulesCreated = 0;
        numAppsCreated = 0;
        schedules = new ArrayList<>();
        profiles = new ArrayList<>();
        apps = new ArrayList<>();

        profiles_to_schedules = new HashMap<>();
        apps_to_profiles = new HashMap<>();

    }

    public void createNewProfile(String profileName){
        profiles.add(new Profile(numProfilesCreated, profileName));
        apps_to_profiles.put(numProfilesCreated, new HashSet<Integer>());
        numProfilesCreated++;
        return;
    }

    public void removeProfile(int profileID){
        //remove profile from FocusModel
        for(Profile p: profiles){
            if(p.getProfileID().equals(profileID)) profiles.remove(p);
        }

        //remove profile from any schedule that contains it
        for(Schedule s: schedules){
            if(s.getProfileIDs().contains(profileID)){
                s.removeProfile(profileID);
            }
        }

        //edit the profile to schedule map
        for (HashSet<Integer> val : profiles_to_schedules.values()){
            if(val.contains(profileID)) val.remove(profileID);
        }

        //edit the app to profile map
        apps_to_profiles.remove(profileID);

        return;
    }

    public void createNewSchedule(String scheduleName){
        profiles.add(new Profile(numSchedulesCreated, scheduleName));
        profiles_to_schedules.put(numSchedulesCreated, new HashSet<Integer>());
        numSchedulesCreated++;
        return;
    }

    public void removeSchedule(int scheduleID){
        //remove schedule from arraylist
        for(Schedule s: schedules){
            if(s.getScheduleID() == scheduleID) schedules.remove(s);
        }

        //remove schedule from profiles_to_schedules map
        profiles_to_schedules.remove(scheduleID);

        return;
    }

    public void createNewApp(String appName){
        apps.add(new App(numAppsCreated, appName));
        numAppsCreated++;

        return;
    }

    public void removeApp(int appID){
        //remove app from FocusModel
        for(App a: apps){
            if (a.getAppID() == appID) apps.remove(a);
            break;
        }

        //remove app from any profile that contains it
        for(Profile p: profiles){
            if(p.getAppIDs().contains(appID)){
                p.removeApp(appID);
            }
        }

        //remove app from the map
        for (HashSet<Integer> val : apps_to_profiles.values()){
            if(val.contains(appID)) val.remove(appID);
            break;
        }
        return;
    }

    public void addProfileToSchedule(int profileID, int scheduleID){

        Profile currProf = null;
        Schedule currSched = null;

        //locate the schedule and profile we are working with
        for (Profile p: profiles){
            if (p.getProfileID().equals(profileID)){
                currProf = p;
            }
        }
        for (Schedule s: schedules){
            if (s.getScheduleID() == scheduleID){
                currSched = s;
            }
        }

        //make sure that the profile and schedule both exist
        if(currProf != null && currSched != null){
            //if the schedule does not already have this profile
            if(!currSched.getProfileIDs().contains(currProf.getProfileID())){
                currSched.addProfile(currProf);
            }

            //if this scheduleID is not already in the map
            if(!profiles_to_schedules.containsKey(scheduleID)){
                //adds a scheduleID as key and an arraylist containing the profileId as the value
                profiles_to_schedules.put(currSched.getScheduleID(), new HashSet<Integer>(currProf.getProfileID()));
            }else{
                //adds profileID to the list of IDs associated with a schedule if it is not already there
                if(!profiles_to_schedules.get(currSched.getScheduleID()).contains(currProf.getProfileID())) {
                    profiles_to_schedules.get(currSched.getScheduleID()).add(currProf.getProfileID());
                }
            }
        }

        return;
    }

    public void addAppToProfile(int appID, int profileID){
        App currApp = null;
        Profile currProf = null;

        //locate the schedule and profile we are working with
        for (Profile p: profiles){
            if (p.getProfileID().equals(profileID)){
                currProf = p;
            }
        }
        for (App a: apps){
            if (a.getAppID() == appID){
                currApp = a;
            }
        }

        //make sure that the profile and app both exist
        if(currProf != null && currApp != null){
            //if the profile does not already have this app
            if(!currProf.getAppIDs().contains(currApp.getAppID())){
                currProf.addApp(currApp);
            }

            //if this scheduleID is not already in the map
            if(!profiles_to_schedules.containsKey(profileID)){
                //adds a scheduleID as key and an arraylist containing the profileId as the value
                profiles_to_schedules.put(currProf.getProfileID(), new HashSet<Integer>(currApp.getAppID()));
            }else{
                //adds profileID to the list of IDs associated with a schedule if it is not already there
                if(!profiles_to_schedules.get(currProf.getProfileID()).contains(currApp.getAppID())) {
                    profiles_to_schedules.get(currProf.getProfileID()).add(currApp.getAppID());
                }
            }
        }
    return;

    }
}

