package Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by aaronrschrock on 10/6/17.
 */


import java.util.ArrayList;

/**
 * FocusModel is the class that contains profiles, schedules, and the main fucntionality
 */

public class FocusModel {
    private ArrayList<Schedule> schedules;
    private ArrayList<Profile> profiles;

    private ArrayList<App> apps;
    private int numProfiles;
    private int numSchedules;
    private int numApps;

    public FocusModel(){
        numProfiles = 0;
        numSchedules = 0;
        numApps = 0;
        schedules = new ArrayList<>();
        profiles = new ArrayList<>();
        apps = new ArrayList<>();

    }

    public void createNewProfile(String profileName){
        profiles.add(new Profile(numProfiles, profileName));
        numProfiles++;
    }

    public void removeProfile(int profileID){

    }

    public void createNewSchedule(String scheduleName){

    }

    public void removeSchedule(int scheduleID){

    }

    public void createNewApp(String appName){
        apps.add(new App(numApps, appName));
        numApps++;
    }

    public void removeApp(String appName){

    };
}

