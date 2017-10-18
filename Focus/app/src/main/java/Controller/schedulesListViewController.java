package Controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

import Model.FocusModel;
import Model.Profile;
import Model.Schedule;
import movingbattleship.org.focus.R;

@TargetApi(23)
public class schedulesListViewController extends Fragment {

    public static Context mContext;
    private static View myView;
    private ListView schedulesListView;
    FocusModel focusModel = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, Activity activity, Context context, Context c) {

        mContext = c;
        focusModel = FocusModel.getInstance();

        View view = inflater.inflate(R.layout.schedules_list_view_fragment, container, false);
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

    void getAll(View v) {
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
    void getName(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Enter new schedule name:");

        myView = v;
        // Set up the input
        final EditText input = new EditText(v.getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Add profile with that name to schedule
                getRepeat(schedulesListViewController.myView, input.getText().toString());
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
        builder.setTitle("Enter new schedule name:");

        final Switch mySwitch = new Switch(v.getContext());

        builder.setView(mySwitch);

        // Set up the buttons
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Add profile with that name to schedule
                getProfiles(schedulesListViewController.myView, mySwitch.isChecked(), name);
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

    void getDays(View v, boolean r, String name, ArrayList<String> p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Choose days schedule");
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final String n = name;
        final ArrayList<String> profiles = p;
        final boolean repeat = r;
        final TextView errorMessage = new TextView(v.getContext());
        errorMessage.setVisibility(View.GONE);
        errorMessage.setTextColor(Color.RED);
        errorMessage.setText("Please check at least one day");

        final CheckBox m = new CheckBox(v.getContext());
        m.setText("Monday");
        m.setChecked(true);
        final CheckBox tu = new CheckBox(v.getContext());
        tu.setText("Tuesday");
        final CheckBox w = new CheckBox(v.getContext());
        w.setText("Wednesday");
        final CheckBox th = new CheckBox(v.getContext());
        th.setText("Thursday");
        final CheckBox f = new CheckBox(v.getContext());
        f.setText("Friday");
        final CheckBox sa = new CheckBox(v.getContext());
        sa.setText("Saturday");
        final CheckBox su = new CheckBox(v.getContext());
        su.setText("Sunday");

        layout.addView(errorMessage);
        layout.addView(m);
        layout.addView(tu);
        layout.addView(w);
        layout.addView(th);
        layout.addView(f);
        layout.addView(sa);
        layout.addView(su);

        builder.setView(layout);
        // Set up the buttons
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: set days ???
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ArrayList<String> days = new ArrayList<String>();
                if(m.isChecked()) {
                    days.add("Monday");
                } if(tu.isChecked()) {
                    days.add("Tuesday");
                 } if(w.isChecked()) {
                    days.add("Wednesday");
                } if(th.isChecked()) {
                    days.add("Thursday");
                 } if(f.isChecked()) {
                    days.add("Friday");
                 } if(sa.isChecked()) {
                    days.add("Saturday");
                 } if(su.isChecked()) {
                    days.add("Sunday");
                }
                Boolean wantToCloseDialog = !days.isEmpty();
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog) {
                    System.out.println("DAYS: " + days);
                    getTimes(schedulesListViewController.myView, repeat, n, days, profiles);
                    dialog.dismiss();
                }
                else {
                    errorMessage.setVisibility(View.VISIBLE);
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            }
        });
    }

    void getProfiles(View v, boolean r, String n) {
        final String names = n;
        final boolean repeat = r;
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Choose profiles to add to schedule");
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final ArrayList<CheckBox> profilesCheckBoxes = new ArrayList<>();

        for (Profile p : focusModel.getAllProfiles()) {
            CheckBox cb = new CheckBox(v.getContext());
            cb.setText(p.getProfileName());
            profilesCheckBoxes.add(cb);
            layout.addView(cb);
        }
        builder.setView(layout);
        // Set up the buttons
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> profiles = new ArrayList<String>();

                for (CheckBox cb : profilesCheckBoxes) {
                    if(cb.isChecked()) {
                        profiles.add(cb.getText().toString());
                    }
                }
                getDays(schedulesListViewController.myView, repeat, names, profiles);
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

    void getTimes(View v, boolean r, String n, ArrayList<String> d, ArrayList<String> p) {

        final String name = n;
        final boolean repeat = r;
        final ArrayList<String> days = d;
        final ArrayList<String> profiles = p;
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Set Time Range");

        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView errorMessage = new TextView(v.getContext());
        errorMessage.setText("Please enter a valid time range (Less than 10 hrs and Greater than 10 mins)");
        errorMessage.setVisibility(View.GONE);
        errorMessage.setTextColor(Color.RED);

        final TimePicker tpStart = new TimePicker(new ContextThemeWrapper(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        tpStart.setLayoutMode(1);
        tpStart.setScaleY((float) .8);

        TextView to = new TextView(v.getContext());
        to.setText("TO");
        to.setGravity(Gravity.CENTER);

        final TimePicker tpEnd = new TimePicker(new ContextThemeWrapper(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        tpEnd.setLayoutMode(2);
        tpEnd.setScaleY((float) .8);

        layout.addView(errorMessage);
        layout.addView(tpStart);
        layout.addView(to);
        layout.addView(tpEnd);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Create Schedule!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // Do nothing?
            }
        });
        builder.setNeutralButton("Add another time range",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        getDays(schedulesListViewController.myView, repeat, name, profiles);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean wantToCloseDialog = true;
                //Do stuff, possibly set wantToCloseDialog to true then...

                int start = tpStart.getHour() * 60 + tpStart.getMinute();
                int end = tpEnd.getHour() * 60 + tpEnd.getMinute();

                if ( end - start > 600) {
                    wantToCloseDialog = false;
                }
                if (end < start) {
                    wantToCloseDialog = false;
                }
                if (end - start < 10) {
                    wantToCloseDialog = false;
                }

                if (wantToCloseDialog) {

                    focusModel.createNewSchedule(name);
                    if( focusModel.getSchedule(name) != null) {
                        focusModel.getSchedule(name).addTimeRange(days, tpStart.getHour(), tpStart.getMinute(), tpEnd.getHour(), tpEnd.getMinute());
                        ((BaseAdapter)schedulesListView.getAdapter()).notifyDataSetChanged();
                    }

                    for(String p : profiles){
                        if (focusModel.getProfile(p) != null) {
                            focusModel.getSchedule(name).addProfile(focusModel.getProfile(p));
                        }
                    }

                    getDays(schedulesListViewController.myView, repeat, name, profiles);
                    dialog.dismiss();
                } else {
                    errorMessage.setVisibility(View.VISIBLE);
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            }
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean wantToCloseDialog = true;
                //Do stuff, possibly set wantToCloseDialog to true then...

                int start = tpStart.getHour() * 60 + tpStart.getMinute();
                int end = tpEnd.getHour() * 60 + tpEnd.getMinute();

                if ( end - start > 600) {
                    wantToCloseDialog = false;
                }
                if (end < start) {
                    wantToCloseDialog = false;
                }
                if (end - start < 10) {
                    wantToCloseDialog = false;
                }

                if (wantToCloseDialog) {

                    focusModel.createNewSchedule(name);
                    if( focusModel.getSchedule(name) != null) {
                        focusModel.getSchedule(name).addTimeRange(days, tpStart.getHour(), tpStart.getMinute(), tpEnd.getHour(), tpEnd.getMinute());
                        ((BaseAdapter)schedulesListView.getAdapter()).notifyDataSetChanged();
                    }

                    for(String p : profiles){
                        if (focusModel.getProfile(p) != null) {
                            focusModel.getSchedule(name).addProfile(focusModel.getProfile(p));
                        }
                    }

                    dialog.dismiss();
                } else {
                    errorMessage.setVisibility(View.VISIBLE);
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            }
        });
    }
}