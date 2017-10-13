package Model;

/**
 * Created by aaronrschrock on 10/6/17.
 */

import java.sql.Time;
import java.util.ArrayList;

public class TimeRange {

    private Time startTime;
    private Time endTime;
    private boolean repeat;
    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;

    public TimeRange(Time st, Time et) {
        setStartTime(st);
        setEndTime(et);
        repeat = false;
        sunday = false;
        monday = false;
        tuesday = false;
        wednesday = false;
        thursday = false;
        friday = false;
        saturday = false;
    }

    public boolean isInRange(Time t) {
        return true;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public Time getTimeRemaining() {
        return Time.valueOf("5");
    }

    public void setStartTime(Time st) {
        startTime = st;
    }

    public void setEndTime(Time et) {
        endTime = et;
    }

    public void setRepeat(Boolean b) {
        repeat = b;
    }

    public void setDays(ArrayList<String> al) {

    }

    public void printRanges(){
        System.out.println("Starts: "+ startTime + ", Ends: " + endTime);
        if(sunday) System.out.print("Sunday, ");
        if(monday) System.out.print("Monday, ");
        if(tuesday) System.out.print("Tuesday, ");
        if(wednesday) System.out.print("Wednesday, ");
        if(thursday) System.out.print("Thursday, ");
        if(friday) System.out.print("Friday, ");
        if(saturday) System.out.print("Saturday, ");
        System.out.print("Repeating: "+ repeat);
        System.out.println("");


    }
}
