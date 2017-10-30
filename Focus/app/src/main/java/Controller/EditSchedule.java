package Controller;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

import Model.FocusModel;
import Model.Profile;
import Model.Schedule;
import Model.TimeRange;
import movingbattleship.org.focus.R;

@TargetApi(23)
public class EditSchedule extends AppCompatActivity {

    private FocusModel focusModel;
    private Schedule schedule;
    private boolean isRepeat;
    private String scheduleName;
    private static View myView;
    private ArrayAdapter<String> listAdapter;

    public static ArrayList<String> names = new ArrayList<String>();

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedules);
        isRepeat = false;
        focusModel = FocusModel.getInstance();

        // get the contact info from the contact activity so the user can see the selected contact info
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            scheduleName = extras.getString("scheduleName");
            System.out.println(scheduleName);
            schedule = focusModel.getSchedule(scheduleName);
            if ( schedule != null ) {
                isRepeat = schedule.getRepeat();
            }
        }

        if (focusModel.getSchedule(scheduleName) != null) {
            if( focusModel.getSchedule(scheduleName).getProfiles() != null ) {
                System.out.println("A: " + focusModel.getSchedule(scheduleName).getProfiles().size());
            }
            for (Profile p : focusModel.getSchedule(scheduleName).getProfiles()) {
                if (p != null) {
                    if (!names.contains(p.getProfileName())) {
                        names.add(p.getProfileName());
                    }
                }
            }
        }

        String name = ( schedule != null ) ? schedule.getScheduleName() : "Awesome Study Session";

        TextView scheduleNameTextView = (TextView) findViewById(R.id.name);
        scheduleNameTextView.setText(name);

        setDateAndTimeTable();

        Button addProfileButton = (Button) findViewById(R.id.addProfileButton);
        Switch toggleOnOff = (Switch) findViewById(R.id.simple_switch);
        toggleOnOff.setChecked(schedule.getRepeat());

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        ListView lv = (ListView)findViewById(R.id.profilesListView);
        lv.setAdapter(listAdapter);

        lv.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final View v = view;
                        final String name = String.valueOf(parent.getItemAtPosition(position));
                        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Remove Profile from Schedule?");

                        // Set up the buttons
                        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               focusModel.removeProfileFromSchedule(name, scheduleName);
                                System.out.println("Names pre: " + name);
                                names.remove(name);
                                System.out.println("Names: " + names.toString());
                                ListView lv = (ListView)findViewById(R.id.profilesListView);
                                listAdapter.notifyDataSetChanged();
                                lv.setAdapter(listAdapter);
                                lv.refreshDrawableState();
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
        );
        addProfileButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Choose profiles to add to schedule");
                        LinearLayout layout = new LinearLayout(v.getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final ArrayList<CheckBox> profilesCheckBoxes = new ArrayList<>();
                        final View view = v;
                        for (Profile p : focusModel.getAllProfiles()) {
                            CheckBox cb = new CheckBox(v.getContext());
                            cb.setText(p.getProfileName());
                            profilesCheckBoxes.add(cb);
                            layout.addView(cb);
                        }
                        builder.setView(layout);
                        // Set up the buttons
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<String> profiles = new ArrayList<String>();

                                for (CheckBox cb : profilesCheckBoxes) {
                                    if(cb.isChecked()) {
                                        profiles.add(cb.getText().toString());
                                    }
                                }
                                for (int i = 0; i < profiles.size(); i++) {
                                    if (!names.contains(profiles.get(i))) {
                                        names.add(profiles.get(i));
                                    }
                                }
                                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, names);
                                ListView lv = (ListView)findViewById(R.id.profilesListView);
                                lv.setAdapter(listAdapter);

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
        );

        toggleOnOff.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: Turn schedule on or off
                        isRepeat = !isRepeat;
                        Switch toggleOnOff = (Switch) findViewById(R.id.simple_switch);
                        if (focusModel.getSchedule(scheduleName) != null ) {
                            focusModel.getSchedule(scheduleName).setRepeat(isRepeat);
                        }
                    }
                }
        );
    }

    public void onClick(View v) {
        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Edit Profile name");

        // Set up the input
        final EditText input = new EditText(v.getContext());
        input.setId(R.id.editScheduleNameId);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Add profile with that name to schedule
                if (focusModel.getSchedule(scheduleName) != null ) {
                    focusModel.getSchedule(scheduleName).setScheduleName(input.getText().toString());
                }
                scheduleName = input.getText().toString();
                TextView scheduleNameTextView = (TextView) findViewById(R.id.name);
                scheduleNameTextView.setText(scheduleName);
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

    void getDays(View v, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Edit Schedule Time Ranges");
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final View tempView = v;
        final String n = name;
        final TextView errorMessage = new TextView(v.getContext());
        errorMessage.setVisibility(View.GONE);
        errorMessage.setTextColor(Color.RED);
        errorMessage.setText("Choose schedule days");

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
                    getTimes(tempView, n, days);
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

        myView = v;
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
        builder.setNeutralButton("Add another time range",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        getDays(EditSchedule.myView, name);
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
                if (tpStart.getHour()>tpEnd.getHour()){
                    if ((24-tpStart.getHour())+tpEnd.getHour() > 10) {
                        wantToCloseDialog = false;
                    }
                }
                else if(tpEnd.getHour()-tpStart.getHour() > 10) {
                    wantToCloseDialog = false;
                }
                else if (tpEnd.getHour()-tpStart.getHour() == 10) {
                    if (tpEnd.getMinute()-tpStart.getMinute() > 0) {
                        wantToCloseDialog = false;
                    }
                    else {
                        wantToCloseDialog = true;
                    }
                }
                else if (tpEnd.getHour()-tpStart.getHour() == 0) {
                    if (tpEnd.getMinute()-tpStart.getMinute() < 10) {
                        wantToCloseDialog = false;
                    }
                    else {
                        wantToCloseDialog = true;
                    }
                }
                else if (tpEnd.getHour()-tpStart.getHour() == 1) {
                    if (((60-tpStart.getMinute())+tpEnd.getMinute()) < 10) {
                        wantToCloseDialog = false;
                    }
                    else {
                        wantToCloseDialog = true;
                    }
                }

                if (wantToCloseDialog) {

                    focusModel.createNewSchedule(name);
                    if( focusModel.getSchedule(name) != null) {
                        focusModel.getSchedule(name).addTimeRange(days, tpStart.getHour(), tpStart.getMinute(), tpEnd.getHour(), tpEnd.getMinute());
                        setDateAndTimeTable();
                    }

                    getDays(EditSchedule.myView, name);
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

                if (tpStart.getHour()>tpEnd.getHour()){
                    if ((24-tpStart.getHour())+tpEnd.getHour() > 10) {
                        wantToCloseDialog = false;
                    }
                }
                else if(tpEnd.getHour()-tpStart.getHour() > 10) {
                    wantToCloseDialog = false;
                }
                else if (tpEnd.getHour()-tpStart.getHour() == 10) {
                    if (tpEnd.getMinute()-tpStart.getMinute() > 0) {
                        wantToCloseDialog = false;
                    }
                    else {
                        wantToCloseDialog = true;
                    }
                }
                else if (tpEnd.getHour()-tpStart.getHour() == 0) {
                    if (tpEnd.getMinute()-tpStart.getMinute() < 10) {
                        wantToCloseDialog = false;
                    }
                    else {
                        wantToCloseDialog = true;
                    }
                }
                else if (tpEnd.getHour()-tpStart.getHour() == 1) {
                    if (((60-tpStart.getMinute())+tpEnd.getMinute()) < 10) {
                        wantToCloseDialog = false;
                    }
                    else {
                        wantToCloseDialog = true;
                    }
                }

                if (wantToCloseDialog) {

                    focusModel.createNewSchedule(name);
                    if( focusModel.getSchedule(name) != null) {
                        focusModel.getSchedule(name).addTimeRange(days, tpStart.getHour(), tpStart.getMinute(), tpEnd.getHour(), tpEnd.getMinute());
                        setDateAndTimeTable();
                    }

                    dialog.dismiss();
                } else {
                    errorMessage.setVisibility(View.VISIBLE);
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            }
        });
    }

    void setDateAndTimeTable() {
        TableLayout daysAndTimesTable = (TableLayout) findViewById(R.id.datesAndTimesTableLayout);
        daysAndTimesTable.removeAllViews();
        if (schedule != null) {
            for (TimeRange t : schedule.getTimeRanges()) {
                System.out.println(schedule.getTimeRanges());
                String days = getDaysString(t.getDays());
                String times = t.getTime();
                System.out.println(days + " " + times);

                TextView daysTextView = new TextView(getApplicationContext());
                daysTextView.setText(days);
                daysTextView.setPadding(10, 10, 10, 10);
                TextView timesTextView = new TextView(getApplicationContext());
                timesTextView.setText(times);
                timesTextView.setPadding(10, 10, 10, 10);
                timesTextView.setGravity(Gravity.RIGHT);
                timesTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                TableRow dtRow = new TableRow(getApplicationContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                dtRow.setLayoutParams(lp);
                dtRow.addView(daysTextView);
                dtRow.addView(timesTextView);
                daysAndTimesTable.addView(dtRow);
            }
        }
        daysAndTimesTable.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                focusModel.getSchedule(scheduleName).clearTimeRanges();
                getDays(v, scheduleName);
                setDateAndTimeTable();
            }
        });
    }

    //the following lines cause the app the crash. It works without.
    /*
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), schedulesListViewController.class);
        startActivity(i);
    }*/
}
