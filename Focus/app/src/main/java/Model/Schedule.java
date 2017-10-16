package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.*;
import static android.R.attr.id;

/**
 * Created by aaronrschrock on 10/6/17.
 */

public class Schedule {
    ArrayList<String> daysOfWeek = new ArrayList<String>();
    Calendar startTime;
    Calendar endTime;
    String name = "";
    ArrayList<Profile> profiles = new ArrayList<Profile>();
    Boolean activated;
    Boolean repeat;

    public Schedule(){
    }
    public Schedule(String name, ArrayList<String> daysOfWeek, Calendar startTime, Calendar endTime){
        this.name = name;
        this.daysOfWeek = daysOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        activated = true;
        repeat = false;
    }
    public void setRepeat(Boolean b) {
        repeat = b;
    }
    public void setActivated(Boolean b) {
        activated = b;
    }
    public Boolean getRepeat() {
        return repeat;
    }
    public Boolean getActivated() {
        return activated;
    }
    public void setDaysOfWeek(ArrayList<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
    public ArrayList<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public String getScheduleName(){

        return name;
    }

    public void setScheduleName(String name) {

        this.name = name;
    }

    public void addProfile(Profile p){
    	/*	Adds a profile to this schedule
    	 	* adds given profile to the arraylist of profiles
    	 */
        if (!profiles.contains(p)) {
            profiles.add(p);
        }
    }

    public void removeProfile(Profile p){
        for (int i = 0; i < profiles.size(); i++) {
            profiles.remove(p);
        }
    }

    public ArrayList<Profile> getProfiles()
    {
        return profiles;
    }


    public void setTimeRange(Calendar start, Calendar end){
        startTime = start;
        endTime = end;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public String getTimeRange() {
        StringBuilder sb = new StringBuilder("");
        String start = startTime.HOUR + ":" + startTime.MINUTE;
        String end = endTime.HOUR + ":" + endTime.MINUTE;
        sb.append(start + "  to  " + end + " on ");
        for (int i = 0; i < daysOfWeek.size(); i++) {
            sb.append(daysOfWeek.get(i) + ".. ");
        }
        String response = sb.toString();
        return response;
    }

}
