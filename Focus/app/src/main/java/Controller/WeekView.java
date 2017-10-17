package Controller;

import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import Model.FocusModel;
import Model.Schedule;
import Model.TimeRange;

/**
 * Created by adammoffitt on 10/17/17.
 */

public class WeekView extends BaseWeekView {

    private FocusModel focusModel;
    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        focusModel = FocusModel.getInstance();

        for (Schedule s : focusModel.getSchedules()) {
            for (TimeRange t : s.getTimeRanges()) {
                for (Integer i : t.getDays()) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.DAY_OF_WEEK, i);
                    startTime.set(Calendar.HOUR_OF_DAY,t.getStartHour());
                    startTime.set(Calendar.MINUTE,t.getStartMinute());
                    startTime.set(Calendar.MONTH, newMonth-1);
                    startTime.set(Calendar.YEAR, newYear);
                    System.out.println("Start time: " + startTime);
                    System.out.println("Get Start time: " + startTime.getTime());
                    Calendar endTime = (Calendar) startTime.clone();
                    System.out.println("Say WHAT????: " + endTime.getTime());
                    endTime.set(Calendar.DAY_OF_WEEK, i);
                    endTime.set(Calendar.HOUR_OF_DAY,t.getEndHour());
                    endTime.set(Calendar.MINUTE,t.getEndMinute());
                    endTime.set(Calendar.MONTH, newMonth-1);
                    endTime.set(Calendar.YEAR, newYear);
                    System.out.println("End time: " + endTime);
                    System.out.println("Get End time: " + endTime.getTime());
                    WeekViewEvent event = new WeekViewEvent(1, s.getScheduleName(), startTime, endTime);
                    Random rnd = new Random();
                    event.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                    events.add(event);
                }
            }
        }

        return events;
    }

}
