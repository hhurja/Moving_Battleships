package Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

import Model.FocusModel;
import Model.Schedule;
import movingbattleship.org.focus.R;

public class schedulesListViewController extends Fragment {

    private static Context mContext;
    FocusModel focusModel = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, Activity activity, Context context, Context c) {

        mContext = c;
        focusModel = FocusModel.getInstance();

        //test
        focusModel.createNewSchedule("Aaron");
        focusModel.createNewSchedule("Ruth");
        focusModel.createNewSchedule("Shabina");


        View view = inflater.inflate(R.layout.schedules_list_view_fragment, container, false);
        ArrayList<Schedule> schedules = focusModel.getSchedules();
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
                        Schedule s = new Schedule(0,name); //TODO: just pass in name!?
                        EditSchedule es = new EditSchedule();
                        es.setSchedule(s);
                        Intent intent = new Intent(schedulesListViewController.mContext, es.getClass());
                        schedulesListViewController.mContext.startActivity(intent);
                    }
                }
        );

        FloatingActionButton fb = (FloatingActionButton) view.findViewById(R.id.addScheduleButton);
        fb.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Create Schedule");
                        ScrollView sv = new ScrollView(v.getContext());
                        LinearLayout layout = new LinearLayout(v.getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final EditText titleBox = new EditText(v.getContext());
                        titleBox.setHint("Title");
                        titleBox.setInputType(InputType.TYPE_CLASS_TEXT);
                        layout.addView(titleBox);

                        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday" , "Friday" , "Saturday" , "Sunday"};
                        final Spinner daySpinner = new Spinner(v.getContext());
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(v.getContext(),
                                android.R.layout.simple_spinner_item, days);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        daySpinner.setAdapter(dataAdapter);

                        layout.addView(daySpinner);

                        TimePicker tpStart = new TimePicker(v.getContext());
                        tpStart.setLayoutMode(1);
                        tpStart.setScaleY((float).5);
                        tpStart.setScaleX((float).5);

                        TextView to = new TextView(v.getContext());
                        to.setText("TO");
                        
                        TimePicker tpEnd = new TimePicker(v.getContext());
                        tpEnd.setLayoutMode(2);
                        tpEnd.setScaleY((float).5);
                        tpEnd.setScaleX((float).5);

                        layout.addView(tpStart);
                        layout.addView(to);
                        layout.addView(tpEnd);
                        sv.addView(layout);
                        builder.setView(sv);

                        // Set up the buttons
                        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Add profile with that name to schedule
                                EditSchedule.names.add(titleBox.getText().toString());
                                // TODO
                                //focusModel.getSchedule().addProfile(focusModel.getProfile());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });
        return view;
    }
}