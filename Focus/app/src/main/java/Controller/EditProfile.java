package Controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import Model.App;
import Model.FocusModel;
import Model.Profile;
import movingbattleship.org.focus.R;

import static movingbattleship.org.focus.R.id.startBlocking;
import static movingbattleship.org.focus.R.id.timer;

/**
 * Created by Ruth on 10/14/17.
 */

public class EditProfile extends AppCompatActivity {
    private Profile profile;
    private FocusModel focusModel;
    private ListView mListView;
    private Button fab_plus;
    private TextView timerText;
    private Button fab_start;
    List<ApplicationInfo> packages = new ArrayList<>();
    private HashMap<String, String> nameToPackage = new HashMap <String, String> ();
    /**
     * The {@link ViewPager} that will host the section contents.
     */

    /*public class TimerClass extends CountDownTimer {

        public TimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String timerStr = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            );
            timerText.setText("Blocked for: " + timerStr);
        }

        @Override
        public void onFinish() {

        }
    } */

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
                // change name of profile and refresh page
                profile.setProfileName(input.getText().toString());
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        focusModel = FocusModel.getInstance();
        profile = focusModel.getCurrentProfile();
        setContentView(R.layout.activity_edit_profile_view);
        final PackageManager pm = getPackageManager();
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            nameToPackage.put(FocusModel.getAppNameFromPackage(getApplicationContext(), packageInfo.packageName), packageInfo.packageName);
        }

        TextView profileName = (TextView) findViewById(R.id.name);
        String profname = ( profile != null ) ? profile.getProfileName() : "Sample Profile";
        profileName.setText(profname);

        timerText = (TextView) findViewById(timer);

        //final TimerClass timerInstance = new TimerClass(180000, 1000);

        //TODO: figure out if we call isOn(), isActive(), or both
        if (profile != null) {
            if (profile.isActivated()) {
                //ArrayList <Integer> endTime = focusModel.endOfTimer(profile.getProfileName());
                timerText.setText("Blocked for: " + "00:03:00");
                //timerInstance.start();
            } else {
                timerText.setVisibility(TextView.INVISIBLE);
            }
        } else {
            timerText.setVisibility(TextView.INVISIBLE);
        }


        mListView = (ListView) findViewById(R.id.AppListView);

        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final View v = view;
                        final String name = String.valueOf(parent.getItemAtPosition(position));
                        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Remove Application from Profile?");

                        // Set up the buttons
                        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // remove application from profile
                                focusModel.removeAppFromProfile(getBaseContext(), nameToPackage.get(name), profile.getProfileName());
                                mListView = (ListView) findViewById(R.id.AppListView);
                                ArrayList<App> apps = profile.getApps();
                                String[] profileNames = new String[apps.size()];
                                for (int i = 0; i < apps.size(); i++) {
                                    profileNames[i] = apps.get(i).getAppName();
                                }
                                ListAdapter adapter = new EditProfileListAdapter(getBaseContext(), profileNames, nameToPackage);
                                mListView.setAdapter(adapter);
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

        if (profile != null) {
            ArrayList<App> apps = profile.getApps();
            String[] profileNames = new String[apps.size()];
            for (int i = 0; i < apps.size(); i++) {
                profileNames[i] = apps.get(i).getAppName();
            }
            ListAdapter adapter = new EditProfileListAdapter(getBaseContext(), profileNames, nameToPackage);
            mListView.setAdapter(adapter);
        }


        fab_start = (Button) findViewById(startBlocking);

        //send timerText and fab_start to Profile class

        if (profile != null) {
            profile.getView(timerText, fab_start);
            if (profile.isActivated()) {
                fab_start.setBackgroundColor(Color.RED);
                fab_start.setText("Stop Blocking This Profile");
            }
            else {
                fab_start.setBackgroundColor(Color.GREEN);
                fab_start.setText("Start Blocking This Profile");
            }
        }

        fab_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(fab_start.getText());
                if (fab_start.getText().equals("Start Blocking This Profile")) {
                    final View myView = view;
                    // pop up dialogue to create new schedule
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Set Blocking time");
                    ScrollView sv = new ScrollView(view.getContext());
                    LinearLayout layout = new LinearLayout(view.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText hoursBox = new EditText(view.getContext());
                    hoursBox.setHint("Hours");
                    hoursBox.setInputType(InputType.TYPE_CLASS_TEXT);
                    layout.addView(hoursBox);

                    final EditText minutesBox = new EditText(view.getContext());
                    minutesBox.setHint("Minutes");
                    minutesBox.setInputType(InputType.TYPE_CLASS_TEXT);
                    layout.addView(minutesBox);

                    sv.addView(layout);
                    builder.setView(sv);

                    // Set up the buttons
                    builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (minutesBox.getText().toString().length() == 0 && hoursBox.getText().toString().length() == 0) {
                                showErrorMessage(myView);
                            }
                            else if (minutesBox.getText().toString().length() == 0 && hoursBox.getText().toString().length() != 0) {
                                minutesBox.setText("0");
                            }
                            else if (hoursBox.getText().toString().length() == 0 && minutesBox.getText().toString().length() != 0) {
                                hoursBox.setText("0");
                            }

                            int minuteBoxNum = Integer.parseInt(minutesBox.getText().toString());

                            int hourBoxNum = Integer.parseInt(hoursBox.getText().toString());

                            if (minuteBoxNum < 10 && hourBoxNum == 0 || hourBoxNum > 10) {
                                showErrorMessage(myView);
                                return;
                            }

                            profile.addTime(minuteBoxNum, hourBoxNum);

                            int currDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                            int currHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                            int currMinute = Calendar.getInstance().get(Calendar.MINUTE);
                            String schedName = Integer.toString(Calendar.DAY_OF_WEEK) +
                                    Integer.toString(Calendar.HOUR_OF_DAY) + Integer.toString(Calendar.MINUTE);
                            ArrayList <String> days = new ArrayList<String> ();
                            if (currDay == 1) {
                                days.add("sunday");
                            } else if (currDay == 2) {
                                days.add("monday");
                            } else if (currDay == 3) {
                                days.add("tuesday");
                            }else if (currDay == 4) {
                                days.add("wednesday");
                            }else if (currDay == 5) {
                                days.add("thursday");
                            }else if (currDay == 6) {
                                days.add("friday");
                            }else if (currDay == 7) {
                                days.add("saturday");
                            }
                            int endHour = 0;
                            int endMinute = currMinute + Integer.parseInt(minutesBox.getText().toString());
                            if (endMinute >= 60) {
                                endHour += 1;
                            }
                            endHour = currHour + Integer.parseInt(hoursBox.getText().toString());
                            if (endHour > 23) {
                                endHour = endHour - 24;
                            }
                            System.out.println("name is " + schedName + " days is " + days + " currHours is " +
                                    currHour + " currMinute is " + currMinute + " endHours is " + endHour +
                                    " endMinute is " + endMinute);
                            //focusModel.createNewSchedule(schedName, days, currHour, currMinute,
                            //endHour, endMinute, true);
                            profile.activate();
                            timerText.setVisibility(TextView.VISIBLE);
                            timerText.setText("Blocked until: " + profile.time);
                            fab_start.setText("Stop Blocking This Profile");
                            fab_start.setBackgroundColor(Color.RED);
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
                if(fab_start.getText().equals("Stop Blocking This Profile")){
                    profile.deactivate();
                    timerText.setVisibility(TextView.INVISIBLE);
                    fab_start.setText("Start Blocking This Profile");
                    fab_start.setBackgroundColor(Color.GREEN);
                }


            }
        });

        fab_plus = (Button) findViewById(R.id.fab_plus);
        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AppChooser.class);
                startActivity(intent);

            }
        });

       /* fab_done = (Button) findViewById(R.id.fab_done);
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_done.setBackgroundColor(Color.RED);
                fab_schedule.setBackgroundColor(Color.GRAY);
                profile.deactivate();
            }
        }); */

        Button deleteButton = (Button) findViewById(R.id.deleteProfile);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusModel.removeProfile(profile.getProfileName());
                focusModel.setCurrentProfile(null);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }


    void showErrorMessage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Invalid time");

        // Set up the input
        final TextView t = new TextView(view.getContext());
        t.setText("Timer must be greater than 10 minutes and less than 10 hours");
        builder.setView(t);

        // Set up the buttons
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
