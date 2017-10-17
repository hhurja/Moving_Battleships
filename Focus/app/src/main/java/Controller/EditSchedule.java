package Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import Model.FocusModel;
import Model.Schedule;
import Model.Profile;
import movingbattleship.org.focus.R;

public class EditSchedule extends AppCompatActivity {

    private FocusModel focusModel;
    private Schedule schedule;
    private boolean isActive;
    private String scheduleName;

    public static ArrayList<String> names = new ArrayList<String>();

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
        System.out.println("Schedule Set: " + this.schedule);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedules);
        isActive = false;
        focusModel = FocusModel.getInstance();

        // get the contact info from the contact activity so the user can see the selected contact info
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            scheduleName = extras.getString("scheduleName");
            schedule = focusModel.getSchedule(scheduleName);
            System.out.println("Does this extra thing work: " + scheduleName);
        }

        for ( Profile p : focusModel.getSchedule(scheduleName).getProfiles() ) {
            if(p!=null) {
                names.add(p.getProfileName());
            }
        }

        System.out.println("Schedule: " + schedule);
        String name = ( schedule != null ) ? schedule.getScheduleName() : "Awesome Study Session";
        String day = "Tuesday"; //TODO
        String time = "2:00 pm - 4:00 pm"; //TODO

        TextView scheduleNameTextView = (TextView) findViewById(R.id.name);
        TextView scheduleTimeTextView = (TextView) findViewById(R.id.time);
        scheduleNameTextView.setText(name);
        scheduleTimeTextView.setText(time);

        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("Monday");
        tempList.add("Thursday");
        tempList.add("Friday"); //schedule.getDays()

        GridView gridview = (GridView) findViewById(R.id.daysGrid);
        ArrayAdapter<String> gridAdapter = new ArrayAdapter<>(gridview.getContext(),
                android.R.layout.simple_list_item_1, tempList);
        gridview.setAdapter(gridAdapter);

        Button addProfileButton = (Button) findViewById(R.id.addProfileButton);
        Button toggleOnOff = (Button) findViewById(R.id.toggleOnOffButton);
        toggleOnOff.setBackgroundColor(isActive ? Color.RED : Color.GREEN);
        toggleOnOff.setText(isActive ? "Turn Schedule Off" : "Turn Schedule On");

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        ListView lv = (ListView)findViewById(R.id.profilesListView);
        lv.setAdapter(listAdapter);

        lv.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final View v = view;
                        String name = String.valueOf(parent.getItemAtPosition(position));
                        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Remove Profile from Schedule?");

                        // Set up the buttons
                        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Add profile with that name to schedule
                                focusModel.getSchedule(scheduleName).removeProfile(focusModel.getSchedule(scheduleName).getScheduleID());
                                ArrayList<String> namesList = new ArrayList<>();
                                for (Profile p : focusModel.getSchedule(scheduleName).getProfiles()) {
                                    if ( p!= null ) {
                                        namesList.add(p.getProfileName());
                                    }
                                }
                                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, namesList);
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

        addProfileButton.setOnClickListener(
                new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Add Profile to Schedule");
                        final View view = v;

                        // Set up the input
                        final EditText input = new EditText(v.getContext());
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Add profile with that name to schedule
                                focusModel.getSchedule(scheduleName).addProfile(focusModel.getProfile(input.getText().toString()));
                                ArrayList<String> namesList = new ArrayList<>();
                                for (Profile p : focusModel.getSchedule(scheduleName).getProfiles()) {
                                    if(p!=null) {
                                        namesList.add(p.getProfileName());
                                    }
                                }
                                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, namesList);
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
                        isActive = !isActive;
                        Button toggleOnOff = (Button) findViewById(R.id.toggleOnOffButton);
                        focusModel.getSchedule(scheduleName).setActivated(isActive);
                        toggleOnOff.setBackgroundColor(isActive ? Color.RED : Color.GREEN);
                        toggleOnOff.setText(isActive ? "Turn Schedule Off" : "Turn Schedule On");
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
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Add profile with that name to schedule
                focusModel.getSchedule(scheduleName).setScheduleName(input.getText().toString());
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
}
