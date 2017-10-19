package Controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import java.util.HashMap;
import java.util.List;

import Model.App;
import Model.FocusModel;
import Model.Profile;
import movingbattleship.org.focus.R;

/**
 * Created by Ruth on 10/14/17.
 */

public class EditProfile extends AppCompatActivity {
    private Profile profile;
    private FocusModel focusModel;
    private ListView mListView;
    private Button fab_toggle;
    private Button fab_plus;
    private TextView timerText;
    private Button fab_start;
    List<ApplicationInfo> packages = new ArrayList<>();
    private HashMap<String, String> nameToPackage = new HashMap <String, String> ();
    /**
     * The {@link ViewPager} that will host the section contents.
     */

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
                //focusModel.getSchedule(scheduleName).setScheduleName(input.getText().toString());
                //scheduleName = input.getText().toString();
                //TextView scheduleNameTextView = (TextView) findViewById(R.id.name);
                //scheduleNameTextView.setText(scheduleName);
                profile.setProfileName(input.getText().toString());
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
        profileName.setText(profile.getProfileName());

        timerText = (TextView) findViewById(R.id.timer);
        //TODO: figure out if we call isOn(), isActive(), or both
        if (profile.isActivated()) {
            //timerText.setText("Blocked until: " + profile.g)
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

        ArrayList<App> apps = profile.getApps();
        String[] profileNames = new String[apps.size()];
        for (int i = 0; i < apps.size(); i++) {
            profileNames[i] = apps.get(i).getAppName();
        }
        ListAdapter adapter = new EditProfileListAdapter(getBaseContext(), profileNames, nameToPackage);
        mListView.setAdapter(adapter);

        fab_toggle = (Button) findViewById(R.id.fab_toggle);
        // if profile is on, allow user to toggle off
        if (profile.isOn()) {
            fab_toggle.setText("Toggle Off");
        } else {
            fab_toggle.setText("Toggle On");
        }
        fab_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profile.isOn()) {
                    profile.turnOFF();
                } else {
                    profile.turnOn();
                }
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        fab_start = (Button) findViewById(R.id.startBlocking);
        fab_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


}
