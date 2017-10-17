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
import android.widget.CheckBox;
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

@TargetApi(23)
public class schedulesListViewController extends Fragment {

    private static Context mContext;
    private static View myView;
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
                getDays(schedulesListViewController.myView, input.getText().toString());
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

    void getDays(View v, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Choose days schedule");
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final String n = name;

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
                    getTimes(schedulesListViewController.myView, n, days);
                    dialog.dismiss();
                }
                else {
                    errorMessage.setVisibility(View.VISIBLE);
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            }
        });
    }

    void getTimes(View v, String n, ArrayList<String> d) {

        final String name = n;
        final ArrayList<String> days = d;
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
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean wantToCloseDialog = true;
                //Do stuff, possibly set wantToCloseDialog to true then...

                if (tpEnd.getHour() - tpStart.getHour() > 10 || (tpEnd.getHour() - tpStart.getHour() > 9 && tpEnd.getMinute() > tpStart.getMinute())) {
                    wantToCloseDialog = false;
                }
                if (tpEnd.getHour() < tpStart.getHour()) {
                    wantToCloseDialog = false;
                }
                if (tpEnd.getHour() * 60 + tpEnd.getMinute() - tpStart.getHour() * 60 + tpStart.getMinute() < 10) {
                    wantToCloseDialog = false;
                }

                if (wantToCloseDialog) {
                    //TODO: CREATE SCHEDULE
                    focusModel.createNewSchedule(name);
                    focusModel.getSchedule(name).addTimeRange(days,tpStart.getHour(), tpStart.getMinute(), tpEnd.getHour(), tpEnd.getMinute());

                    System.out.println("Done!");
                    dialog.dismiss();
                } else {
                    System.out.println("show error message");
                    errorMessage.setVisibility(View.VISIBLE);
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            }
        });
    }
}