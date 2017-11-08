package Controller;

import android.annotation.TargetApi;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import Model.FocusModel;
import Model.Schedule;
import movingbattleship.org.focus.R;

@TargetApi(23)
public class schedulesListViewController extends Fragment {

    public static Context mContext;
    private static View myView;
    private ListView schedulesListView;
    FocusModel focusModel = null;
    private View masterView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, Activity activity, Context context, Context c) {

        mContext = c;
        focusModel = FocusModel.getInstance();

        View view = inflater.inflate(R.layout.schedules_list_view_fragment, container, false);
        masterView = view;
        TextView emptyMessage = new TextView(view.getContext());
        emptyMessage.setText("No schedules. Add a schedule to get Focused!");

        ArrayList<Schedule> schedules = focusModel.getSchedules();
        if (schedules.isEmpty()) {
            //TODO: show error
        }
        schedulesListView = (ListView) view.findViewById(R.id.schedulesListView);
        ListAdapter schedulesAdapter = new schedulesListAdapter (context, schedules);

        schedulesListView.setAdapter(schedulesAdapter);
        schedulesListView.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Schedule temp = (Schedule)parent.getItemAtPosition(position);
                        String name = temp.getScheduleName();
                        Intent intent = new Intent(schedulesListViewController.mContext, EditSchedule.class);
                        intent.putExtra("scheduleName", name);
                        schedulesListViewController.mContext.startActivity(intent);
                    }
                }
        );

        FloatingActionButton fb = (FloatingActionButton) view.findViewById(R.id.addScheduleButton);
        fb.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getName(v);
                    }
                });

        FloatingActionButton fbRemove = (FloatingActionButton) view.findViewById(R.id.deleteScheduleButton);
        fbRemove.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Enter schedule name to remove");

                        myView = v;
                        // Set up the input
                        final EditText input = new EditText(v.getContext());
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Schedule s = focusModel.getSchedule(input.getText().toString());
                                if ( s != null ) {
                                    focusModel.removeSchedule(s.getScheduleID());
                                    ((BaseAdapter)schedulesListView.getAdapter()).notifyDataSetChanged();
                                }

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

        Button fbCalendar = (Button) view.findViewById(R.id.calendarActionButton);
        fbCalendar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(schedulesListViewController.mContext, WeekView.class);
                        //intent.putExtra("scheduleName", name);
                        schedulesListViewController.mContext.startActivity(intent);
                    }
                });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        schedulesListView = (ListView) masterView.findViewById(R.id.schedulesListView);
        ((BaseAdapter)schedulesListView.getAdapter()).notifyDataSetChanged();
    }

    @SuppressWarnings("ResourceType")
    void getName(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Enter new schedule name:");

        myView = v;
        // Set up the input
        final EditText input = new EditText(v.getContext());
        input.setId(R.id.createScheduleNameId);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().length() == 0) {
                    dialog.cancel();
                }
                else {
                    // Add profile with that name to schedule
                    getRepeat(schedulesListViewController.myView, input.getText().toString());
                }

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

    void getRepeat(View v, String n){
        final String name = n;
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Repeat schedule?");

        final Switch mySwitch = new Switch(v.getContext());
        mySwitch.setId(R.id.repeatSwitch);
        builder.setView(mySwitch);

        // Set up the buttons
        builder.setPositiveButton("Create!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Add profile with that name to schedule
                focusModel.createNewSchedule(name);
                focusModel.getSchedule(name).setRepeat(mySwitch.isChecked());
                schedulesListView = (ListView) masterView.findViewById(R.id.schedulesListView);
                ((BaseAdapter)schedulesListView.getAdapter()).notifyDataSetChanged();
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
}