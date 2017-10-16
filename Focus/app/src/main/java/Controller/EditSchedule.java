package Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import Model.FocusModel;
import Model.Schedule;
import movingbattleship.org.focus.R;

public class EditSchedule extends AppCompatActivity {

    private FocusModel focusModel;
    private Schedule schedule;
    private boolean isActive;

    public static ArrayList<String> names = new ArrayList<String>(){{
        add("Dating Apps");
        add("Hunter's List");
        add("Social Media");
        add("Dinosaurs");
    }};

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedules);
        isActive = false;

        focusModel = FocusModel.getInstance();

        String name = ( schedule != null ) ? schedule.getScheduleName() : "Awesome Study Session";
        String day = "Tuesday"; //TODO
        String time = "2:00 pm - 4:00 pm"; //TODO

        TextView scheduleNameTextView = (TextView) findViewById(R.id.name);
        TextView scheduleDayTextView = (TextView) findViewById(R.id.day);
        TextView scheduleTimeTextView = (TextView) findViewById(R.id.time);
        scheduleNameTextView.setText(name);
        scheduleDayTextView.setText(day);
        scheduleTimeTextView.setText(time);

        Button addProfileButton = (Button) findViewById(R.id.addProfileButton);
        Button toggleOnOff = (Button) findViewById(R.id.toggleOnOffButton);
        toggleOnOff.setBackgroundColor(isActive ? Color.RED : Color.GREEN);
        toggleOnOff.setText(isActive ? "Turn Schedule Off" : "Turn Schedule On");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        ListView lv = (ListView)findViewById(R.id.profilesListView);
        lv.setAdapter(adapter);

        addProfileButton.setOnClickListener(
                new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Add Profile to Schedule");


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
                                EditSchedule.names.add(input.getText().toString());
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
                }
        );

        toggleOnOff.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("On/Off"); // just check to see if this tap is working / list view works
                        //TODO: Turn schedule on or off
                        isActive = !isActive;
                        Button toggleOnOff = (Button) findViewById(R.id.toggleOnOffButton);
                        toggleOnOff.setBackgroundColor(isActive ? Color.RED : Color.GREEN);
                        toggleOnOff.setText(isActive ? "Turn Schedule Off" : "Turn Schedule On");
                    }
                }
        );
    }
}
