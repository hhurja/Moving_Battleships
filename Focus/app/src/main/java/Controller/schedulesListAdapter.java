package Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import Model.Schedule;
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
        TextView scheduleDayTextView = (TextView) schedulesView.findViewById(R.id.day);
        TextView scheduleTimeTextView = (TextView) schedulesView.findViewById(R.id.time);
        scheduleNameTextView.setText(s.getScheduleName());
        scheduleDayTextView.setText("Monday");
        scheduleTimeTextView.setText("2:00 pm - 4:00 pm");
        // access your linear layout
        LinearLayout schedulesLinearLayout = (LinearLayout)schedulesView.findViewById(R.id.profilesLinearLayout);
        String[] names = {"Dating Apps", "Hunter's List", "Social Media", "Dinosaurs"};

        if (schedulesLinearLayout != null) {
            for (int i = 0; i < names.length; i++) {
                TextView textView = new TextView(schedulesView.getContext());
                textView.setText(names[i]);
                schedulesLinearLayout.addView(textView);
            }
        }

        return schedulesView;
    }
}