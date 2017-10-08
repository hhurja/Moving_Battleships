package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by aaronrschrock on 10/6/17.
 */

public class Schedule {
    private int id;
    private String name;
    HashMap<Integer, TimeRange> profileSchedule = new HashMap<Integer, TimeRange>();
    ArrayList<Profile> profiles = new ArrayList<Profile>();

    public Schedule(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getScheduleID(){
        return id;
    }

    public String getScheduleName(){
        return name;
    }

    public void setScheduleName(String name){
        this.name = name;
    }

    public HashMap<Integer,TimeRange> getProfileSchedule(){
        return profileSchedule;
    }

    public void addProfile(Profile p){
        profiles.add(p);
    }

    public void removeProfile(int profileID){
        for(Profile p: profiles){
            if(p.getProfileID() == profileID) profiles.remove(p);
        }
    }

    public ArrayList<Profile> getProfiles(){
        return profiles;
    }

    public HashSet<Integer> getProfileIDs(){
        HashSet<Integer> returnSet = new HashSet<Integer>();
        for(Profile p: profiles){
            returnSet.add(p.getProfileID());
        }
        return returnSet;
    }
}
