package Controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import movingbattleship.org.focus.R;

public class schedulesListViewController extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, Activity activity, Context context, Context c) {
        //View view = inflater.inflate(R.layout.schedules_table_view, container, false);
        View view = inflater.inflate(R.layout.schedules_list_view_fragment, container, false);
        String[] schedules = {"Schedule 1", "Schedule 2", "Schedule 3", "Schedule 4"};
        ListView schedulesListView = (ListView) view.findViewById(R.id.schedulesListView);
        System.out.println("Schedules List view iz..." + (ListView) view.findViewById(R.id.schedulesListView));
        ListAdapter schedulesAdapter = new schedulesListAdapter (context, schedules);
        schedulesListView.setAdapter(schedulesAdapter);
        return view;
    }
}