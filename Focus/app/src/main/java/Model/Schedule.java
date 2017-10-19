package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.sql.Time;
import java.util.StringTokenizer;

/**
 * Created by aaronrschrock on 10/6/17.
 */

public class Schedule {
    private int id;
    private String name;
    //HashMap<Integer, ArrayList<TimeRange>> profileSchedule;
    ArrayList<TimeRange> timeRanges;
//    TimeRange timeRange;
    ArrayList<Profile> profiles;
    Boolean activated;
    Boolean repeat;
    Boolean blocked;
    Boolean invisible;


    public Schedule(int id, String name){
        this.id = id;
        this.name = name;
        activated = true;
        repeat = false;
        blocked = false;
        invisible = false;

//        profileSchedule = new HashMap<>();
        profiles = new ArrayList<Profile>();
        //timeRange = new TimeRange();
        timeRanges = new ArrayList<>();
    }

    public Schedule(int id, String scheduleName, ArrayList<String> days){
        this.id = id;
        this.name = scheduleName;
        activated = true;
        repeat = false;
        invisible = false;

//        profileSchedule = new HashMap<>();
        profiles = new ArrayList<>();
//        timeRange = new TimeRange(new Time(startHour, startMinute, 0), new Time(endHour, endMinute, 0));

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

    public int getScheduleID(){
        return id;
    }

    public String getScheduleName(){
        return name;
    }

    public void setScheduleName(String name){
        this.name = name;
    }

//    public HashMap<Integer, ArrayList<TimeRange>> getProfileSchedule(){
//        return profileSchedule;
//    }

    public void addProfile(Profile p){
    	/*	Adds a profile to this schedule
    	    * checks if this profile has already been added
    	 	* adds given profile to the arraylist of profiles
    	 */

        for(Profile prof: profiles){
            if(prof.getProfileName().equals(p.getProfileName())){
                System.out.println("Error in AddProfile in Schedule.Java -- Profile already exists");
            }
        }
        profiles.add(p);
//        profiles.add(p);
//        if(profileSchedule.containsKey(p.getProfileID())){
//            System.out.println("Error in AddProfile in Schedule.Java -- Profile already exists in hashmap");
//        }else{
////        	System.out.println("Added profile with id: "+ p.getProfileID()+ " to profileSchedule");
//            profileSchedule.put(p.getProfileID(), new ArrayList<TimeRange>() );
//        }
    }

    public void removeProfile(int profileID){
        for(Profile p: profiles){
            if(p.getProfileID() == profileID){
                profiles.remove(p);
                break;
            }
        }
    }

    public ArrayList<Profile> getProfiles(){
        return profiles;
    }

    // takes in id, returns profile associated with that id
    // if no profile associated, return null
    // returning null shouldn't happen, but yolo
    public Profile getProfileFromId(int id) {
        for (Profile profile : profiles) {
            if (profile.getProfileID() == id) {
                return profile;
            }
        }
        return null;
    }

    public HashSet<Integer> getProfileIDs(){
        HashSet<Integer> returnSet = new HashSet<Integer>();
        for(Profile p: profiles){
            returnSet.add(p.getProfileID());
        }
        return returnSet;
    }

    public ArrayList<TimeRange> getTimeRanges(){
        return timeRanges;
    }

    public void blockProfiles(){
        for(Profile p: profiles) p.blockProfile();
        blocked = true;
    }

    public void unblockProfiles(){
        for(Profile p: profiles) p.unblockProfile();
        blocked = false;
    }

    public Boolean isActive(){
        return activated;
    }

    public boolean isInTimeRange(){
        for(TimeRange tr: timeRanges){
            if (tr.inRange()) return true;
        }
        return false;
    }

    public void addTimeRange(ArrayList<String> days, int startHour, int startMinute, int endHour, int endMinute){
        timeRanges.add(new TimeRange(days, startHour, startMinute, endHour, endMinute));
    }

    public ArrayList<Integer> getLatestHM(){
        //returns an arraylist that will be 2 integers long every time
        //the first int is the latest hour that this schedule will be engaged until
        //the second int is the latest minute of that hour
        ArrayList<Integer> hm = new ArrayList<>();

        int maxHour = -1;
        int maxMinute = -1;

        for(TimeRange tr: timeRanges){
            if(tr.inRange()){
                if(tr.getEndHour() >= maxHour){
                    maxHour = tr.getEndHour();
                    if(tr.getEndMinute() > maxMinute) maxMinute = tr.getEndMinute();
                }
            }
        }
        hm.add(maxHour);
        hm.add(maxMinute);
        return hm;
    }



//    public void addTimeRangeToProfile(Profile p, TimeRange tr){
////    	System.out.println(p+ " "+ profileSchedule.get(p.getProfileID()));
////    	System.out.println(profileSchedule);
//        profileSchedule.get(p.getProfileID()).add(tr);
//    }

//    public void printTimeRanges(){
//        for(Profile p: profiles){
//
//            System.out.println(p.getProfileName()+ ":");
//            for(TimeRange tr: profileSchedule.get(p.getProfileID()))
//                tr.printRanges();
//        }
//    }
}
