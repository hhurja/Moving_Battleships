package Model;

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
    private int scheduleCounter;
    private int profileCounter;

    /**
     * Constructors
     */

    public FocusModel() {
        scheduleCounter = 0;
        profileCounter = 0;
    }

    /**
     * Getters and Setters
     */

    public void createNewProfile(String name) {

    }

    public void removeProfile(Integer profileID) {

    }

    public void createNewSchedule(String name) {

    }

    public void removeSchedule(Integer scheduleID) {

    }

}

