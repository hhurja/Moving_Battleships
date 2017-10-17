package Controller;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ListAdapter;
import Model.Profile;
import android.content.pm.*;
import android.content.Intent;

import movingbattleship.org.focus.R;
import java.util.*;
import Model.*;

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
    /**
     * The {@link ViewPager} that will host the section contents.
     */

    @Override
    public void onBackPressed(){
        // new ProfilesList
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        focusModel = FocusModel.getInstance();
        profile = focusModel.getCurrentProfile();
        setContentView(R.layout.activity_edit_profile_view);

        HashMap<String, String> nameToPackage = new HashMap <String, String> ();
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
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
                System.out.println("fab done clicked");
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
