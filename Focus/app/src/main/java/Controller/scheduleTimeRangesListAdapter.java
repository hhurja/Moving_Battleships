package Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.ArrayList;

import Model.Profile;
import Model.TimeRange;

/**
 * Created by adammoffitt on 11/6/17.
 */

public class scheduleTimeRangesListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<TimeRange> timeRanges;

        public scheduleTimeRangesListAdapter(Context context, ArrayList<TimeRange> tr) {
            this.context = context;
            this.timeRanges = tr;
        }

        @Override
        public int getCount() {
            return timeRanges.size();
        }

        @Override
        public Object getItem(int position) {
            return timeRanges.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TwoLineListItem twoLineListItem;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                twoLineListItem = (TwoLineListItem) inflater.inflate(
                        android.R.layout.simple_list_item_2, null);
            } else {
                twoLineListItem = (TwoLineListItem) convertView;
            }

            TextView text1 = twoLineListItem.getText1();
            TextView text2 = twoLineListItem.getText2();

            text1.setText(getDaysString(timeRanges.get(position).getDays()));
            String profiles = "";
            for (Profile p : timeRanges.get(position).getProfiles()) {
                profiles += p.getProfileName();
            }

            text2.setText(profiles);

            return twoLineListItem;
        }

    private String getDaysString(ArrayList<Integer> days) {
        String daysString = "";

        System.out.println(days);

        for (int i = 0; i < days.size(); i++) {
            if(days.get(i) == 1) daysString += "Su";
            if(days.get(i) == 2) daysString += "Mo";
            if(days.get(i) == 3) daysString += "Tu";
            if(days.get(i) == 4) daysString += "We";
            if(days.get(i) == 5) daysString += "Th";
            if(days.get(i) == 6) daysString += "Fr";
            if(days.get(i) == 7) daysString += "Sa";

            if(i < days.size()-1){
                daysString += ", ";
            }
        }

        return daysString;
    }
}
