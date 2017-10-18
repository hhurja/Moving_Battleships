package Controller;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.FocusModel;
import Model.Profile;
import movingbattleship.org.focus.R;

/**
 * Created by Ruth on 10/14/17.
 */

public class AppChooser extends AppCompatActivity {
    private static Profile profile;
    private FocusModel focusModel;
    private ListView mListView;
    private Button fab_done;
    List<ApplicationInfo> packages = new ArrayList<>();
    HashMap<String, String> nameToPackage = new HashMap <String, String> ();

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_chooser);

        focusModel = FocusModel.getInstance();

        mListView = (ListView) findViewById(R.id.InstalledAppsListView);



        final PackageManager pm = getPackageManager();
//get a list of installed apps.
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        /*for (ApplicationInfo packageInfo : packages) {
            System.out.println("Installed package :" + packageInfo.packageName);
            System.out.println("Source dir : " + packageInfo.sourceDir);
            System.out.println("Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        } */

        String[] names = new String[packages.size()];
        int count = 0;
        for (ApplicationInfo packageInfo : packages) {
            //if (!FocusModel.getAppNameFromPackage(getApplicationContext(), packageInfo.packageName).substring(0,))
            String appName = FocusModel.getAppNameFromPackage(getApplicationContext(), packageInfo.packageName);
            System.out.println("app name is " + appName + " from " + packageInfo.packageName);
            String checkName = "";
            if (appName.length() > 10) {
                checkName = appName.substring(0, 11);
            }
            if (!checkName.equals("com.android")) {
                names[count] = appName;
                nameToPackage.put(appName, packageInfo.packageName);
                count ++;
            }

        }
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, names);

        ListAdapter adapter = new InstalledApplicationsListAdapter(mListView.getContext(), names, nameToPackage);
        mListView.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.add_applications);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;
                EditProfile ep = new EditProfile();
                System.out.println("rendering new EditProfile");
                //ep.setProfile(profile);
                //System.out.println("name of current profile is " + AppChooser.profile.getProfileName());
                Intent intent = new Intent(getApplicationContext(), ep.getClass());
                startActivity(intent);
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
