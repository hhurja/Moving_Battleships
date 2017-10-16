package Controller;

import android.app.Service;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager.LayoutParams;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.HashMap;

import Model.AppIconGenerator;
import Model.AppProcessChecker;
import movingbattleship.org.focus.R;
import movingbattleship.org.focus.*;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static Context mContext;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static HashMap<String, Bitmap> hm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this.getApplicationContext();

        AppIconGenerator aig = new AppIconGenerator(getPackageManager());
        hm = aig.getAppIcon();
        MainActivity m = MainActivity.this;
        Context c = getApplicationContext();
        PackageManager pm = getPackageManager();
        UsageStatsManager usm = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
        AppIconGenerator aicongen = new AppIconGenerator(getPackageManager());
        hm = aicongen.getAppIcon();
        AppProcessChecker apc = new AppProcessChecker(c, pm, usm, m);

        //profilesListViewController plvc = new profilesListViewController();
        //getSupportFragmentManager().beginTransaction().add(R.id.container, plvc).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // get profile information from backend
        // this assumes that id is the same id associated
        // with Profile class instance on backend
        //Profile profile = getProfileFromId(id);
        // display on EditProfileView
        //if (profile != null) {
        //$this->updateProfileShown(profile);
        //}

        return super.onOptionsItemSelected(item);
    }

    public static Context getAppContext(){
        return mContext;
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Profiles";
                case 1:
                    return "Schedules";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
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

            System.out.println("* WOLOLOLO - " + getArguments().getInt(ARG_SECTION_NUMBER));

            if ( getArguments().getInt(ARG_SECTION_NUMBER) == 1 ) {
                System.out.println("1 WOLOLOLO");
                profilesListViewController plvc = new profilesListViewController();
                return plvc.onCreateView(inflater, container, savedInstanceState, getActivity(), getContext(), hm, mContext);
            } else {
                System.out.println("2 WOLOLOLO");
                schedulesListViewController slvc = new schedulesListViewController();
                return slvc.onCreateView(inflater, container, savedInstanceState, getActivity(), getContext(), mContext);
            }
            // View rootView = inflater.inflate(R.layout.profiles_list_view_fragment, container, false);

            // return rootView;
        }
    }
}