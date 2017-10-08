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
}
