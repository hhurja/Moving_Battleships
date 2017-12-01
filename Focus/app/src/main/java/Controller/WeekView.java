package Controller;

import android.content.Intent;
import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
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
            int color = s.getColor();
            for (TimeRange t : s.getTimeRanges()) {
                for (Integer i : t.getDays()) {
                    Calendar startTime = Calendar.getInstance();
                    //if (startTime.get(Calendar.DAY_OF_WEEK) < i) {
                    //    startTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) +((7-startTime.get(Calendar.DAY_OF_WEEK))+i));
                    //} else {
                        startTime.set(Calendar.DAY_OF_WEEK, i);
                    //}
                    if ( i < Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                        startTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH)+7);
                    }
                    startTime.set(Calendar.HOUR_OF_DAY,t.getStartHour());
                    startTime.set(Calendar.MINUTE,t.getStartMinute());
                    startTime.set(Calendar.MONTH, newMonth-1);
                    startTime.set(Calendar.YEAR, newYear);
                    System.out.println("Start time: " + startTime);
                    System.out.println("Get Start time: " + startTime.getTime());

                    Calendar endTime = (Calendar) startTime.clone();
                    //if (endTime.get(Calendar.DAY_OF_WEEK) < i) {
                      //  endTime.set(Calendar.DAY_OF_MONTH, endTime.get(Calendar.DAY_OF_MONTH) +((7-endTime.get(Calendar.DAY_OF_WEEK))+i));
                    //} else {
                    //endTime.set(Calendar.DAY_OF_WEEK, i);
                    //}
                    endTime.set(Calendar.HOUR_OF_DAY,t.getEndHour());
                    endTime.set(Calendar.MINUTE,t.getEndMinute());
                    System.out.println("End time: " + endTime);
                    System.out.println("Get End time: " + endTime.getTime());
                    String profNames = (t.getProfiles().size() > 1 ? (t.getProfiles().get(0).getProfileName() + "...") : (t.getProfiles().get(0).getProfileName()));
                    WeekViewEvent event = new WeekViewEvent(1, profNames, startTime, endTime);
                    event.setColor(color);
                    events.add(event);
                }
            }
        }

        HashSet checkDuplicates = new HashSet();
        for (TimeRange t : focusModel.getEvents()) {
            for (Integer i : t.getDays()) {
                if (!checkDuplicates.contains(t.getEventName())) {
                    Calendar startTime = Calendar.getInstance();
                    //if (startTime.get(Calendar.DAY_OF_WEEK) < i) {
                    //    startTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) +((7-startTime.get(Calendar.DAY_OF_WEEK))+i));
                    //} else {
                    startTime.set(Calendar.DAY_OF_WEEK, i);
                    //}
                    if (i < Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                        startTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) + 7);
                    }
                    startTime.set(Calendar.HOUR_OF_DAY, t.getStartHour());
                    startTime.set(Calendar.MINUTE, t.getStartMinute());
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);
                    //System.out.println("Get Start time: " + startTime.getTime());

                    Calendar endTime = (Calendar) startTime.clone();
                    //if (endTime.get(Calendar.DAY_OF_WEEK) < i) {
                    //  endTime.set(Calendar.DAY_OF_MONTH, endTime.get(Calendar.DAY_OF_MONTH) +((7-endTime.get(Calendar.DAY_OF_WEEK))+i));
                    //} else {
                    //endTime.set(Calendar.DAY_OF_WEEK, i);
                    //}
                    endTime.set(Calendar.HOUR_OF_DAY, t.getEndHour());
                    endTime.set(Calendar.MINUTE, t.getEndMinute());
                    //System.out.println("Get End time: " + endTime.getTime());
                    WeekViewEvent event = new WeekViewEvent(1, t.getEventName(), startTime, endTime);
                    event.setColor(Color.BLUE);
                    System.out.println(event.toString());
                    events.add(event);
                    checkDuplicates.add(t.getEventName());
                }
            }
        }

        return events;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, schedulesListViewController.class);
        this.startActivity(intent);
    }
}
