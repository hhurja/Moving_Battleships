package Model;

/**
 * Created by aaronrschrock on 10/6/17.
 */

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.PreferencesFactory;
import java.util.Date;


public class TimeRange {

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean repeat;
    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private HashMap<Date, Date> dates;
    public String eventName;

    private ArrayList<Profile> profiles;

    private HashMap<Integer, Boolean> dayMap;

    public TimeRange(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        repeat = false;
        sunday = false;
        monday = false;
        tuesday = false;
        wednesday = false;
        thursday = false;
        friday = false;
        saturday = false;
        dayMap = new HashMap<>();

        dayMap.put(1, sunday);
        dayMap.put(2, monday);
        dayMap.put(3, tuesday);
        dayMap.put(4, wednesday);
        dayMap.put(5, thursday);
        dayMap.put(6, friday);
        dayMap.put(7, saturday);

        profiles = new ArrayList<>();
    }

    public TimeRange(ArrayList<String> days, int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        repeat = false;
        sunday = false;
        monday = false;
        tuesday = false;
        wednesday = false;
        thursday = false;
        friday = false;
        saturday = false;
        dates = new HashMap<Date, Date>();

        dayMap = new HashMap<>();

        addDays(days);
        dayMap.put(1, sunday);
        dayMap.put(2, monday);
        dayMap.put(3, tuesday);
        dayMap.put(4, wednesday);
        dayMap.put(5, thursday);
        dayMap.put(6, friday);
        dayMap.put(7, saturday);

        profiles = new ArrayList<>();
        eventName = "";
    }

    public TimeRange(ArrayList<String> days, int startHour, int startMinute, int endHour, int endMinute, ArrayList<Profile> profiles) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        repeat = false;
        sunday = false;
        monday = false;
        tuesday = false;
        wednesday = false;
        thursday = false;
        friday = false;
        saturday = false;
        dates = new HashMap<Date, Date>();

        dayMap = new HashMap<>();

        addDays(days);
        dayMap.put(1, sunday);
        dayMap.put(2, monday);
        dayMap.put(3, tuesday);
        dayMap.put(4, wednesday);
        dayMap.put(5, thursday);
        dayMap.put(6, friday);
        dayMap.put(7, saturday);

        this.profiles = profiles;
        eventName = "";
    }

    public TimeRange(ArrayList<String> days, int startHour, int startMinute, int endHour, int endMinute, String eventName) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        repeat = false;
        sunday = false;
        monday = false;
        tuesday = false;
        wednesday = false;
        thursday = false;
        friday = false;
        saturday = false;
        dates = new HashMap<Date, Date>();

        dayMap = new HashMap<>();

        addDays(days);
        dayMap.put(1, sunday);
        dayMap.put(2, monday);
        dayMap.put(3, tuesday);
        dayMap.put(4, wednesday);
        dayMap.put(5, thursday);
        dayMap.put(6, friday);
        dayMap.put(7, saturday);

        profiles = new ArrayList<>();
        this.eventName = eventName;
    }

//    public Time getStartTime() {
//        return startTime;
//    }
//
//    public Time getEndTime() {
//        return endTime;
//    }

    public void addDays(ArrayList<String> days){
        for(String S: days){
//            System.out.println("PRINTING DAYS: " + S);
            if(S.toLowerCase().equals("sunday")) sunday = true;
            if(S.toLowerCase().equals("monday")) monday = true;
            if(S.toLowerCase().equals("tuesday")) tuesday = true;
            if(S.toLowerCase().equals("wednesday")) wednesday = true;
            if(S.toLowerCase().equals("thursday")) thursday = true;
            if(S.toLowerCase().equals("friday")) friday = true;
            if(S.toLowerCase().equals("saturday")) saturday = true;
        }
        //add to the dates hashmap
        updateDateHM();
    }
    public void updateDateHM() {
        Date now = Calendar.getInstance().getTime();
        Calendar today = Calendar.getInstance();
        int day = today.get(Calendar.DAY_OF_WEEK);
        if (sunday == true) {
            Calendar start = today;
            Calendar end = today;
            if (day == Calendar.SUNDAY) {
                System.out.println("Start Hour" + startHour);
                System.out.println("End Hour: " + endHour);
                //set the startDate
                start.set(Calendar.HOUR_OF_DAY, startHour);
                start.set(Calendar.MINUTE, startMinute);
                start.set(Calendar.SECOND, 0);
                Date startDate = start.getTime();
                //set the endDate
                end.set(Calendar.HOUR_OF_DAY, endHour);
                end.set(Calendar.MINUTE, endMinute);
                end.set(Calendar.SECOND, 0);
                Date endDate = end.getTime();
                //add start date and end date to hashmap
                System.out.println(startDate);
                System.out.println(endDate);
                dates.put(startDate, endDate);
            }
            else {
                Date startDate = today.getTime();
                Date endDate = today.getTime();
                startDate.setDate(startDate.getDate() + (7 + 0 - startDate.getDay()) % 7);
                startDate.setHours(startHour);
                startDate.setMinutes(startMinute);
                startDate.setSeconds(0);
                endDate.setDate(startDate.getDate() + (7 + 0 - startDate.getDay()) % 7);
                endDate.setHours(endHour);
                endDate.setMinutes(endMinute);
                System.out.println("Starting time: " + startDate);
                System.out.println("Ending time: " + endDate);
                dates.put(startDate, endDate);
            }
        }
        if (monday == true) {
            Calendar start = today;
            Calendar end = today;
            if (day == Calendar.MONDAY) {
                //set the startDate
                start.set(Calendar.HOUR_OF_DAY, startHour);
                start.set(Calendar.MINUTE, startMinute);
                start.set(Calendar.SECOND, 0);
                Date startDate = start.getTime();
                //set the endDate
                end.set(Calendar.HOUR_OF_DAY, endHour);
                end.set(Calendar.MINUTE, endMinute);
                end.set(Calendar.SECOND, 0);
                Date endDate = end.getTime();
                //add start date and end date to hashmap
                System.out.println(startDate);
                System.out.println(endDate);
                dates.put(startDate, endDate);
            }
            else {
                Date startDate = today.getTime();
                Date endDate = today.getTime();
                startDate.setDate(startDate.getDate() + (7 + 1 - startDate.getDay()) % 7);
                startDate.setHours(startHour);
                startDate.setMinutes(startMinute);
                startDate.setSeconds(0);
                endDate.setDate(startDate.getDate() + (7 + 1 - startDate.getDay()) % 7);
                endDate.setHours(endHour);
                endDate.setMinutes(endMinute);
                dates.put(startDate, endDate);
            }
        }
        if (tuesday == true) {
            Calendar start = today;
            Calendar end = today;
            if (day == Calendar.TUESDAY) {
                //set the startDate
                start.set(Calendar.HOUR_OF_DAY, startHour);
                start.set(Calendar.MINUTE, startMinute);
                start.set(Calendar.SECOND, 0);
                Date startDate = start.getTime();
                //set the endDate
                end.set(Calendar.HOUR_OF_DAY, endHour);
                end.set(Calendar.MINUTE, endMinute);
                end.set(Calendar.SECOND, 0);
                Date endDate = end.getTime();
                //add start date and end date to hashmap
                System.out.println(startDate);
                System.out.println(endDate);
                dates.put(startDate, endDate);
            }
            else {
                Date startDate = today.getTime();
                Date endDate = today.getTime();
                startDate.setDate(startDate.getDate() + (7 + 2 - startDate.getDay()) % 7);
                startDate.setHours(startHour);
                startDate.setMinutes(startMinute);
                startDate.setSeconds(0);
                endDate.setDate(startDate.getDate() + (7 + 2 - startDate.getDay()) % 7);
                endDate.setHours(endHour);
                endDate.setMinutes(endMinute);
                dates.put(startDate, endDate);
            }
        }
        if (wednesday == true) {
            Calendar start = today;
            Calendar end = today;
            if (day == Calendar.WEDNESDAY) {
                //set the startDate
                start.set(Calendar.HOUR_OF_DAY, startHour);
                start.set(Calendar.MINUTE, startMinute);
                start.set(Calendar.SECOND, 0);
                Date startDate = start.getTime();
                //set the endDate
                end.set(Calendar.HOUR_OF_DAY, endHour);
                end.set(Calendar.MINUTE, endMinute);
                end.set(Calendar.SECOND, 0);
                Date endDate = end.getTime();
                //add start date and end date to hashmap
                System.out.println(startDate);
                System.out.println(endDate);
                dates.put(startDate, endDate);
            }
            else {
                Date startDate = today.getTime();
                Date endDate = today.getTime();
                startDate.setDate(startDate.getDate() + (7 + 3 - startDate.getDay()) % 7);
                startDate.setHours(startHour);
                startDate.setMinutes(startMinute);
                startDate.setSeconds(0);
                endDate.setDate(startDate.getDate() + (7 + 3 - startDate.getDay()) % 7);
                endDate.setHours(endHour);
                endDate.setMinutes(endMinute);
                dates.put(startDate, endDate);
            }
        }
        if (thursday == true) {
            Calendar start = today;
            Calendar end = today;
            if (day == Calendar.THURSDAY) {
                //set the startDate
                start.set(Calendar.HOUR_OF_DAY, startHour);
                start.set(Calendar.MINUTE, startMinute);
                start.set(Calendar.SECOND, 0);
                Date startDate = start.getTime();
                //set the endDate
                end.set(Calendar.HOUR_OF_DAY, endHour);
                end.set(Calendar.MINUTE, endMinute);
                end.set(Calendar.SECOND, 0);
                Date endDate = end.getTime();
                //add start date and end date to hashmap
                System.out.println(startDate);
                System.out.println(endDate);
                dates.put(startDate, endDate);
            }
            else {
                Date startDate = today.getTime();
                Date endDate = today.getTime();
                startDate.setDate(startDate.getDate() + (7 + 4 - startDate.getDay()) % 7);
                startDate.setHours(startHour);
                startDate.setMinutes(startMinute);
                startDate.setSeconds(0);
                endDate.setDate(startDate.getDate() + (7 + 4 - startDate.getDay()) % 7);
                endDate.setHours(endHour);
                endDate.setMinutes(endMinute);
                dates.put(startDate, endDate);
            }
        }
        if (friday == true) {
            Calendar start = today;
            Calendar end = today;
            if (day == Calendar.FRIDAY) {
                //set the startDate
                start.set(Calendar.HOUR_OF_DAY, startHour);
                start.set(Calendar.MINUTE, startMinute);
                start.set(Calendar.SECOND, 0);
                Date startDate = start.getTime();
                //set the endDate
                end.set(Calendar.HOUR_OF_DAY, endHour);
                end.set(Calendar.MINUTE, endMinute);
                end.set(Calendar.SECOND, 0);
                Date endDate = end.getTime();
                //add start date and end date to hashmap
                System.out.println(startDate);
                System.out.println(endDate);
                dates.put(startDate, endDate);
            }
            else {
                Date startDate = today.getTime();
                Date endDate = today.getTime();
                startDate.setDate(startDate.getDate() + (7 + 5 - startDate.getDay()) % 7);
                startDate.setHours(startHour);
                startDate.setMinutes(startMinute);
                startDate.setSeconds(0);
                endDate.setDate(startDate.getDate() + (7 + 5 - startDate.getDay()) % 7);
                endDate.setHours(endHour);
                endDate.setMinutes(endMinute);
                dates.put(startDate, endDate);
            }
        }
        if (saturday == true) {
            Calendar start = today;
            Calendar end = today;
            if (day == Calendar.SATURDAY) {
                //set the startDate
                start.set(Calendar.HOUR_OF_DAY, startHour);
                start.set(Calendar.MINUTE, startMinute);
                start.set(Calendar.SECOND, 0);
                Date startDate = start.getTime();
                //set the endDate
                end.set(Calendar.HOUR_OF_DAY, endHour);
                end.set(Calendar.MINUTE, endMinute);
                end.set(Calendar.SECOND, 0);
                Date endDate = end.getTime();
                //add start date and end date to hashmap
                System.out.println(startDate);
                System.out.println(endDate);
                dates.put(startDate, endDate);
            }
            else {
                Date startDate = today.getTime();
                Date endDate = today.getTime();
                startDate.setDate(startDate.getDate() + (7 + 6 - startDate.getDay()) % 7);
                startDate.setHours(startHour);
                startDate.setMinutes(startMinute);
                startDate.setSeconds(0);
                endDate.setDate(startDate.getDate() + (7 + 6 - startDate.getDay()) % 7);
                endDate.setHours(endHour);
                endDate.setMinutes(endMinute);
                dates.put(startDate, endDate);
            }
        }
    }

    public HashMap<Date, Date> getDates() {
        return dates;
    }

    public boolean isRepeating(){
        return repeat;
    }

    public Time getTimeRemaining() {
        return Time.valueOf("5");
    }

    public void setRepeat(Boolean b) {
        repeat = b;
    }

//    public void setDays(ArrayList<String> al) {
//
//    }

    public Boolean inRange(){
        Calendar cal = Calendar.getInstance();
        int currDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int currHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currMinute = Calendar.getInstance().get(Calendar.MINUTE);

        if (dayMap.get(currDay)!= null) {
            if (dayMap.get(currDay)) {
                if (currHour == startHour) {
                    if (currMinute >= startMinute) {
                        if (currHour < endHour || (currHour == endHour && currMinute <= endMinute))
                            return true;
                    }
                } else if (currHour > startHour) {
                    if (currHour < endHour || (currHour == endHour && currMinute <= endMinute))
                        return true;
                }
            }
        }
        return false;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public String getEventName() {
        return eventName;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public ArrayList<Integer> getDays() {
        ArrayList<Integer> days = new ArrayList<>();
//        Iterator it = dayMap.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            if((boolean)pair.getValue()) {
//                days.add(new Integer((int)pair.getKey()));
//            }
//            it.remove(); // avoids a ConcurrentModificationException
//        }
        for(Integer key: dayMap.keySet()){
            if(dayMap.get(key)){
                days.add(key);
            }
        }
        return days;
    }

    public String getTime() {
        String s = "";

        s += (startHour == 0 || startHour == 12 ? "12" : startHour%12) + ":" + (startMinute > 9 ? startMinute : "0"+startMinute)
                + "-" + (endHour == 0 || endHour == 12 ? "12" : endHour%12) + ":" + (endMinute > 9 ? endMinute : "0"+endMinute) + (endHour>11 ? "PM" : "AM");

        return s;
    }

    public void addProfile(Profile p){
        if(!profiles.contains(p)) profiles.add(p);
    }

    public void removeProfile(Profile p, Schedule s){
        if (profiles.contains(p)){
            p.removeScheduleID(s.getScheduleID());
            p.deactivate();
            profiles.remove(p);
        }
    }

    public void removeProfile(int profileID){
        for(Profile p: profiles){
            if (p.getProfileID() == profileID) {
                p.deactivate();
                profiles.remove(p);
                break;
            }

        }
    }

    public void removeProfile(String profileName){
        for(Profile p: profiles){
            if (p.getProfileName().equals(profileName))
                profiles.remove(p);
            break;
        }
    }

    public ArrayList<Profile> getProfiles(){
        return profiles;
    }

    public boolean hasProfile(int profileID) {
        for(Profile p: profiles){
            if(p.getProfileID() == profileID){
                return true;
            }
        }
        return false;
    }

    public void addProfiles(ArrayList<Profile> newProfiles){
        for(Profile p: newProfiles){
            if(!profiles.contains(p)){
                profiles.add(p);
            }
        }
    }

//    public void blockProfiles(){
//        for(Profile p: profiles) {
//            if (p.isOn()){
//                System.out.println("BLOCKING FOR PROFILE: "+ p.getProfileName());
//                p.blockProfile();
//                p.addScheduleID(id);
//            }else{
//                System.out.println("NOT BLOCKING FOR PROFILE: "+ p.getProfileName());
//            }
//            if (!isInRange){
//                p.turnOn();
//                isInRange = true;
//            }
//        }
//        blocked = true;
//    }
//    public void printRanges(){
//        System.out.println("Starts: "+ startTime + ", Ends: " + endTime);
//        if(sunday) System.out.print("Sunday, ");
//        if(monday) System.out.print("Monday, ");
//        if(tuesday) System.out.print("Tuesday, ");
//        if(wednesday) System.out.print("Wednesday, ");
//        if(thursday) System.out.print("Thursday, ");
//        if(friday) System.out.print("Friday, ");
//        if(saturday) System.out.print("Saturday, ");
//        System.out.print("Repeating: "+ repeat);
//        System.out.println("");
//
//
//    }
}
