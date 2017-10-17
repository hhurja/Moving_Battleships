package Model;

/**
 * Created by aaronrschrock on 10/6/17.
 */

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

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

        dayMap.put(1, sunday);
        dayMap.put(2, monday);
        dayMap.put(3, tuesday);
        dayMap.put(4, wednesday);
        dayMap.put(5, thursday);
        dayMap.put(6, friday);
        dayMap.put(7, saturday);
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

        dayMap.put(1, sunday);
        dayMap.put(2, monday);
        dayMap.put(3, tuesday);
        dayMap.put(4, wednesday);
        dayMap.put(5, thursday);
        dayMap.put(6, friday);
        dayMap.put(7, saturday);
        addDays(days);
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
            if(S.toLowerCase().equals("sunday")) sunday = true;
            if(S.toLowerCase().equals("monday")) monday = true;
            if(S.toLowerCase().equals("tuesday")) tuesday = true;
            if(S.toLowerCase().equals("wednesday")) wednesday = true;
            if(S.toLowerCase().equals("thursday")) thursday = true;
            if(S.toLowerCase().equals("friday")) friday = true;
            if(S.toLowerCase().equals("saturday")) saturday = true;
        }
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
        int currDay = cal.DAY_OF_WEEK;
        int currHour = cal.HOUR_OF_DAY;
        int currMinute = cal.MINUTE;

        if(dayMap.get(currDay)){
            if(currHour == startHour){
                if(currMinute >= startMinute){
                    if(currHour < endHour || (currHour == endHour && currMinute <= endMinute)) return true;
                }
            }else if(currHour >startHour){
                if(currHour < endHour || (currHour == endHour && currMinute <= endMinute)) return true;
            }
        }
        return false;
    }

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
