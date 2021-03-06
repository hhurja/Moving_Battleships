package Model;

import android.content.Context;
import android.graphics.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import Controller.MainActivity;

/**
 * Created by aaronrschrock on 10/6/17.
 */

public class Schedule {
    private int id;
    private String name;
    //    private HashMap<TimeRange, ArrayList<Profile>> profileSchedule;
    ArrayList<TimeRange> timeRanges;
    //    TimeRange timeRange;
    ArrayList<Profile> profiles;
    Boolean activated;
    Boolean repeat;
    Boolean blocked;
    Boolean invisible;
    Boolean alreadyPrompted = false;
    Boolean isInRange;
    HashMap<Calendar, String> holidays = new HashMap<Calendar, String> ();
    int color;
    Boolean today_is_holiday = false;
    public Schedule(int id, String name) {
        this.id = id;
        this.name = name;
        activated = true;
        repeat = false;
        blocked = false;
        invisible = false;
        isInRange = false;
        Random rnd = new Random();
        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        profiles = new ArrayList<>();
        timeRanges = new ArrayList<>();
        updateHolidays();
    }

    public void updateHolidays() {
        try {
            Context c = MainActivity.mContext;
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(c.getAssets().open("holidays.txt")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String holidayName = parts[1];
                String holidayDate = parts[0];
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = sdf.parse(holidayDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    holidays.put(cal, holidayName);
                }
                catch (ParseException pe) {
                    System.out.println("Date conversion not working");
                }
            }
        }
        catch (IOException ioe) {
            System.out.println("reading text file not working");
        }
    }

    public Schedule(int id, String scheduleName, ArrayList<String> days){
        this.id = id;
        this.name = scheduleName;
        activated = true;
        repeat = false;
        invisible = false;
        isInRange = false;
        Random rnd = new Random();
        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

//        profileSchedule = new HashMap<>();
        profiles = new ArrayList<>();
        timeRanges = new ArrayList<>();
    }

    public Schedule(TimeRange tr){
        this.id = id; //TODO
        this.name = tr.getEventName();
        activated = true;
        repeat = tr.isRepeating();;
        invisible = false;
        isInRange = false;
        Random rnd = new Random();
        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        profiles = tr.getProfiles();
        timeRanges = new ArrayList<>();
        timeRanges.add(tr);
    }

    public Schedule(int id, String scheduleName, boolean invis){
        this.id = id;
        this.name = scheduleName;
        activated = true;
        repeat = false;
        invisible = invis;
        isInRange = false;
        Random rnd = new Random();
        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

//        profileSchedule = new HashMap<>();
        profiles = new ArrayList<>();
        timeRanges = new ArrayList<>();
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
        if(!name.equals("")) this.name = name;
    }

    public void addProfile(Profile p, TimeRange tr){
        System.out.println("THIS SCHEDULE IS: " + getScheduleName() + " AND THE ID IS " + id);
        /*	Adds a profile to this schedule
    	    * checks if this profile has already been added
    	 	* adds given profile to the arraylist of profiles
    	 */

        for(Profile prof: profiles){
            if(prof.getProfileName().equals(p.getProfileName())){
                System.out.println("Error in AddProfile in Schedule.Java -- Profile already exists");
                return;
            }
        }
        tr.addProfile(p);
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
        for(TimeRange tr: timeRanges){
            if(tr.hasProfile(profileID)){
                tr.removeProfile(profileID);
            }
        }
    }

    public ArrayList<Profile> getProfiles(){
        return profiles;
    }

    public Profile getProfileFromId(int id) {
        // takes in id, returns profile associated with that id
        // if no profile associated, return null
        // returning null shouldn't happen, but yolo
        for (Profile profile : profiles) {
            if (profile.getProfileID() == id) {
                return profile;
            }
        }
        return null;
    }

    public void scheduleDeleted() {
        unblockProfiles();
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

    private void blockProfiles(ArrayList<Profile> profilesToBlock, ArrayList<Profile> profilesNotBlocked){
        for(Profile p: profilesToBlock) {
            if (p.isOn()){
                System.out.println("BLOCKING FOR PROFILE: "+ p.getProfileName());
                p.blockProfile();
                p.addScheduleID(id);
            }else{
                System.out.println("NOT BLOCKING FOR PROFILE: "+ p.getProfileName());
                p.unblockProfile();
                p.removeScheduleID(id);
            }
            if (!isInRange){
                p.turnOn();
                isInRange = true;
            }
        }
        blocked = true;
    }

    public void unblockProfiles(){
        for(Profile p: profiles){
            p.removeScheduleID(id);
            p.unblockProfile();
        }
        blocked = false;
    }

    public Boolean isActive(){
        return activated;
    }

    public boolean isInTimeRange(){
        for(TimeRange tr: timeRanges){
            if (tr.inRange()){
                return true;
            }

        }
        isInRange = false;
        return false;
    }

    public void addTimeRange(ArrayList<String> days, int startHour, int startMinute, int endHour, int endMinute){
        timeRanges.add(new TimeRange(days, startHour, startMinute, endHour, endMinute));
    }

    public void addTimeRange(ArrayList<String> days, int startHour, int startMinute, int endHour, int endMinute, ArrayList<Profile> profiles){
        timeRanges.add(new TimeRange(days, startHour, startMinute, endHour, endMinute, profiles));
    }

    public void addTimeRange(TimeRange tr, ArrayList<Profile> p){
        tr.addProfiles(p);
        timeRanges.add(tr);
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

    public void clearTimeRanges(){
        //TODO update this
        timeRanges.clear();
    }

    public void setColor(int i){
        color = i;
    }

    public boolean isVisible(){
        return !invisible;
    }
    public boolean blockRanges() {
        /*
        boolean hasBlocked = false;
        ArrayList<Profile> profilesToBlock = new ArrayList<>();
        ArrayList<Profile> profilesNotBlocked = new ArrayList<>();
        for(TimeRange tr: timeRanges){
            if (tr.inRange()){
                hasBlocked = true;
                for(Profile p: tr.getProfiles()){
                    if(!profilesToBlock.contains(p)){
                        profilesToBlock.add(p);
                    }
                }
            }else{
                for(Profile p: tr.getProfiles()){
                    if(!profilesNotBlocked.contains(p)){
                        profilesNotBlocked.add(p);
                    }
                }
            }
        }
        if (hasBlocked){
            blockProfiles(profilesToBlock, profilesNotBlocked);
            isInRange = true;
            return true;
        }
        isInRange = false;
        return false;
        */

        isInRange = false;
        for(TimeRange tr: timeRanges){
            String holiday_name = "";
            //if it is the same day and holiday blocking has not been prompted yet
            //then ask the user to choose if it should be blocked and block accordingly
            if (tr.inRange()) {
                Calendar now = Calendar.getInstance();
                for (Calendar key : holidays.keySet()) {
                    today_is_holiday = now.get(Calendar.YEAR) == key.get(Calendar.YEAR) &&
                            now.get(Calendar.DAY_OF_YEAR) == key.get(Calendar.DAY_OF_YEAR);
                    if (today_is_holiday) {
                        break;
                    }
                }
                if (today_is_holiday) {
                    if (alreadyPrompted == false) {
                        MainActivity.promptHolidayBlocking();
                        alreadyPrompted = true;
                    }
                    for(Profile p: tr.getProfiles()){
                        p.unblockProfile();
                        p.removeScheduleID(id);
                    }
                }
                else {
                    isInRange = true;
                    for (Profile p : tr.getProfiles()) {
                        p.blockProfile();
                        p.addScheduleID(id);
                    }
                }
            }
            else{
                for(Profile p: tr.getProfiles()){
                    p.unblockProfile();
                    p.removeScheduleID(id);
                }
            }

        }
        return isInRange;
    }

    public int getColor() {
        return color;
    }
}
