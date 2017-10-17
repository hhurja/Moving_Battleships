package Controller;

import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
                    startTime.set(Calendar.MONTH,newMonth-1);
                    startTime.set(Calendar.YEAR,newYear);
                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR,t.getEndHour());
                    endTime.set(Calendar.MONTH,t.getEndMinute());
                    WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
                    event.setColor(Color.RED);
                    events.add(event);
                }
            }
        }

        return events;
    }

}
