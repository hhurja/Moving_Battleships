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
import android.widget.ListAdapter;
import android.widget.ListView;

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
    private Button fab_schedule;
    private Button fab_plus;
    private Button fab_done;
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

        //String[] names = new String[packages.size()];
        int count = 0;
        for (ApplicationInfo packageInfo : packages) {
            //if (!FocusModel.getAppNameFromPackage(getApplicationContext(), packageInfo.packageName).substring(0,))
            //names[count] = FocusModel.getAppNameFromPackage(getApplicationContext(), packageInfo.packageName);
            nameToPackage.put(FocusModel.getAppNameFromPackage(getApplicationContext(), packageInfo.packageName), packageInfo.packageName);
            //count ++;
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
                                System.out.println(getBaseContext() + " : " + nameToPackage.get(name) + " : " + name);
                                focusModel.removeAppFromProfile(getBaseContext(), nameToPackage.get(name), profile.getProfileName());

                                //focusModel.getSchedule(scheduleName).removeProfile(focusModel.getSchedule(scheduleName).getScheduleID());
                                //ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, names);
                                //ListView lv = (ListView)findViewById(R.id.profilesListView);
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

        //List<App> applications = profile.getApps();
        ArrayList<App> apps = profile.getApps();
        String[] profileNames = new String[apps.size()];
        for (int i = 0; i < apps.size(); i++) {
            profileNames[i] = apps.get(i).getAppName();
        }
        /*int count = 0;
        for (App app : applications) {
            names[count] = app.getAppName();
            count ++;
        } */
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, names);
        ListAdapter adapter = new EditProfileListAdapter(getBaseContext(), profileNames, nameToPackage);
        mListView.setAdapter(adapter);

        fab_schedule = (Button) findViewById(R.id.fab_submit);
        fab_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("fab submit clicked");
                fab_schedule.setBackgroundColor(Color.GREEN);
                fab_done.setBackgroundColor(Color.GRAY);
                profile.activate();
                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(intent);
            }
        });

        fab_plus = (Button) findViewById(R.id.fab_plus);
        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("fab plus clicked");
                Intent intent = new Intent(getApplicationContext(), AppChooser.class);
                startActivity(intent);

            }
        });

        fab_done = (Button) findViewById(R.id.fab_done);
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_done.setBackgroundColor(Color.RED);
                fab_schedule.setBackgroundColor(Color.GRAY);
                profile.deactivate();
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteProfile);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //focusModel.re
                onBackPressed();
            }
        });



        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        // set up list view to see
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.layout.activity_edit_profile_view, menu);
        return true;
    } */
    /**
     * A placeholder fragment containing a simple view.
     */
    //public static class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    //private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
        /* public static MainActivity.PlaceholderFragment newInstance(int sectionNumber) {
            MainActivity.PlaceholderFragment fragment = new MainActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            AppListViewController alvc = new AppListViewController();

            //return alvc.onCreateView(inflater, container, savedInstanceState, getActivity(), getContext(), hm, mContext);
            return alvc.onCreateView(inflater, container, savedInstanceState, getActivity(), getContext());
        } */
    //}

}
