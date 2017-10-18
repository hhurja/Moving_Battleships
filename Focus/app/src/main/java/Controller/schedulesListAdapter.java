package Controller;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import Model.Profile;
import Model.Schedule;
import Model.TimeRange;
import movingbattleship.org.focus.R;

/**
 * Created by adammoffitt on 10/9/17.
 */

public class schedulesListAdapter extends ArrayAdapter<Schedule>{

    public schedulesListAdapter(@NonNull Context context, ArrayList<Schedule> schedules) {

        super(context, R.layout.schedules_row, schedules);
        System.out.println("in constructor");
    }
    /*public profilesListAdapter(@NonNull Context context, String[] profileNames) {
        super(context, R.layout.profile_row ,profileNames);
    } */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        System.out.println("getting schedules view");
        LayoutInflater schedulesInflator = LayoutInflater.from(getContext());

        View schedulesView = schedulesInflator.inflate(R.layout.schedules_row, parent, false);
        // parse name from profile name
        Schedule s = getItem(position);
        TextView scheduleNameTextView = (TextView) schedulesView.findViewById(R.id.name);
        TextView scheduleActiveTextView = (TextView) schedulesView.findViewById(R.id.active);
        //TextView scheduleDayTextView = (TextView) schedulesView.findViewById(R.id.day);
        //TextView scheduleTimeTextView = (TextView) schedulesView.findViewById(R.id.time);
        scheduleNameTextView.setText(s.getScheduleName());
        scheduleActiveTextView.setText(s.isActive() ? "Active" : "Off");
        scheduleActiveTextView.setTextColor(s.isActive() ? Color.GREEN : Color.RED);
        //scheduleDayTextView.setText("Monday");
        //scheduleTimeTextView.setText("2:00 pm - 4:00 pm");

        TableLayout daysAndTimesTable = (TableLayout) schedulesView.findViewById(R.id.daysAndTimesTable);
        for (TimeRange t : s.getTimeRanges()) {
            System.out.println(s.getTimeRanges());
            String days = getDaysString(t.getDays());
            String times = t.getTime();
            System.out.println(days + " " + times);

            TextView daysTextView = new TextView(getContext());
            daysTextView.setText(days);
            TextView timesTextView = new TextView(getContext());
            timesTextView.setText(times);
            TableRow dtRow = new TableRow(getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            dtRow.setLayoutParams(lp);
            dtRow.addView(daysTextView);
            //dtRow.addView(timesTextView);
            daysAndTimesTable.addView(dtRow);
        }

        // access your linear layout
        LinearLayout schedulesLinearLayout = (LinearLayout)schedulesView.findViewById(R.id.profilesLinearLayout);
        ArrayList<String> names = new ArrayList<>();
        for (Profile p : s.getProfiles()) {
            names.add(p.getProfileName());
        }

        if (schedulesLinearLayout != null) {
            for (int i = 0; i < names.size(); i++) {
                TextView textView = new TextView(schedulesView.getContext());
                textView.setText(names.get(i));
                schedulesLinearLayout.addView(textView);
            }
        }

        return schedulesView;
    }

    private String getDaysString(ArrayList<Integer> days) {
        String daysString = "";

        for (int i = 0; i < days.size(); i++) {
            switch(days.get(i)) {
                case 1 : daysString += "Mo";
                case 2 : daysString += "Tu";
                case 3 : daysString += "We";
                case 4 : daysString += "Th";
                case 5 : daysString += "Fr";
                case 6 : daysString += "Sa";
                case 7 : daysString += "Su";
            }
            if(i < days.size()-1){
                daysString += ", ";
            }
        }

        return daysString;
    }
}