package Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import movingbattleship.org.focus.R;

public class schedulesListViewController extends Fragment {

    private static Context mContext;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, Activity activity, Context context, Context c) {

        mContext = c;

        View view = inflater.inflate(R.layout.schedules_list_view_fragment, container, false);
        String[] schedules = {"Schedule 1", "Schedule 2", "Schedule 3", "Schedule 4"};
        ListView schedulesListView = (ListView) view.findViewById(R.id.schedulesListView);
        System.out.println("Schedules List view iz..." + (ListView) view.findViewById(R.id.schedulesListView));
        ListAdapter schedulesAdapter = new schedulesListAdapter (context, schedules);

        schedulesListView.setAdapter(schedulesAdapter);
        schedulesListView.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = String.valueOf(parent.getItemAtPosition(position));
                        System.out.println(name); // just check to see if this tap is working / list view works
                        // TODO: open up actual profile
                        Intent intent = new Intent(schedulesListViewController.mContext, EditSchedule.class);
                        schedulesListViewController.mContext.startActivity(intent);
                    }
                }
        );
        return view;
    }
}