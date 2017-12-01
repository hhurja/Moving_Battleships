package Controller;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Model.AppIconGenerator;
import Model.AppProcessChecker;
import Model.CSVWriterHelper;
import Model.DatabaseHelper;
import Model.FocusModel;
import movingbattleship.org.focus.R;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

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
    public static Context mContext;
    //Notification Listener Variables
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    //private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private AlertDialog enableNotificationListenerAlertDialog;
    //Variables for sending notifications
    public static int NotificationID = 0;
    public static String CHANNEL_ID = "my_channel_id";
    private TabLayout tabLayout;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static HashMap<String, Bitmap> hm;

    private DatabaseHelper mDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FocusModel.colorSchemeCheck == false) {
            //ask for color preference
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Color Theme");
            alertDialog.setMessage("Choose a color theme for your Focus! app");
            alertDialog.setButton(AlertDialog.BUTTON1, "Blue",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
                            tb.setBackgroundColor(Color.BLUE);
                            AppBarLayout abl = (AppBarLayout) findViewById(R.id.appbar);
                            abl.setBackgroundColor(Color.BLUE);;
                            FocusModel.color = Color.BLUE;
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON2, "Green",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
                            tb.setBackgroundColor(Color.GREEN);
                            AppBarLayout abl = (AppBarLayout) findViewById(R.id.appbar);
                            abl.setBackgroundColor(Color.GREEN);;
                            FocusModel.color = Color.GREEN;

                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON3, "Red",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
                            tb.setBackgroundColor(Color.RED);
                            AppBarLayout abl = (AppBarLayout) findViewById(R.id.appbar);
                            abl.setBackgroundColor(Color.RED);;
                            FocusModel.color = Color.RED;
                        }
                    });
            alertDialog.show();

        }
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setBackgroundColor(FocusModel.color);
        AppBarLayout abl = (AppBarLayout) findViewById(R.id.appbar);
        abl.setBackgroundColor(FocusModel.color);;
        FocusModel.colorSchemeCheck = true;
        mContext = this.getApplicationContext();
        // If the user did not turn the notification listener service on we prompt him to do so
        if(!isNotificationServiceEnabled()){
            System.out.println("showing notification service dialogue");
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
        //Create a channel for sending notifications
        createChannel();

        AppIconGenerator aig = new AppIconGenerator(getPackageManager());
        hm = aig.getAppIcon();
        MainActivity m = MainActivity.this;
        Context c = getApplicationContext();
        PackageManager pm = getPackageManager();
        UsageStatsManager usm = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
        AppProcessChecker apc = new AppProcessChecker(c, pm, usm, m);
        FocusModel fm = FocusModel.getInstance(apc, hm);

        /*
        String name = "ExampleProfile";
        fm.createNewProfile(name);
        fm.addAppToProfile(getApplicationContext(), "com.google.android.apps.maps", name);*/
        //fm.activateProfile(name);

//        fm.createNewSchedule("Test", new ArrayList<String>(), 15, 30, 18, 0);


        //SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();


//        ArrayList<String> blocked = new ArrayList<String>();
//        blocked.add("com.google.android.apps.maps");
//        apc.blockApplication(blocked);
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

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    protected void onStop(){
        super.onStop();
        mDatabaseHelper = new DatabaseHelper(this);
        FocusModel fm = FocusModel.getInstance();
        fm.createDatabase(mDatabaseHelper);
    }

    public static void promptHolidayBlocking() {
        Intent i = new Intent(MainActivity.mContext, HolidayBlocking.class);
        MainActivity.mContext.startActivity(i);

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
        switch (id) {
            case R.id.share_facebook:
                try {
                    String message = "Text I want to share.";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    Intent openInChooser = new Intent(share);
                    startActivity(openInChooser);
                } catch(Exception e) {
                    //e.toString();
                }
                return true;
            // ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse("https://developers.facebook.com")).build();

            // startActivity(new Intent(this, About.class));
            case R.id.google_calendar:
                Intent intent = new Intent(MainActivity.mContext, GoogleCalendarActivity.class);
                MainActivity.mContext.startActivity(intent);
                return true;
            case R.id.csvExport:
                CSVWriterHelper export = new CSVWriterHelper();
                try {
                    export.writeOut();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.csvImport:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose CSV to Import");
                LinearLayout layout = new LinearLayout(MainActivity.getAppContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                final ArrayList<RadioButton> csvCheckBoxes = new ArrayList<>();
                final RadioGroup rg = new RadioGroup(this); //create the RadioGroup
                rg.setOrientation(RadioGroup.VERTICAL);
                // android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/output.csv"
                String s = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download";
                File dir = new File(s);
                System.out.println("Path: " + dir.getAbsolutePath());
                File[] subFiles = dir.listFiles();
                if (subFiles != null)
                {
                    System.out.println("Files: " + subFiles.toString());
                    for (File file : subFiles)
                    {
                        RadioButton cb = new RadioButton(this);
                        System.out.println("file: " + file.getName());
                        cb.setText(file.getName());
                        rg.addView(cb);
                        csvCheckBoxes.add(cb);
                    }
                    layout.addView(rg);
                }

                builder.setView(layout);
                // Set up the buttons
                builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> profiles = new ArrayList<String>();

                        String fileName = csvCheckBoxes.get(rg.getCheckedRadioButtonId()-1).getText().toString();
                        System.out.println(fileName);

                        try {
                            FocusModel.getInstance().importFromCSV();
                        } catch (IOException e) {
                            e.printStackTrace();
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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        // return super.onOptionsItemSelected(item);
    }


    public static Context getAppContext(){
        return mContext;
    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if eanbled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Listener Service");
        alertDialogBuilder.setMessage("You must allow Focus! to listen to other app's notifications in order to get entire functionality.");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }

    /**
     *
     * Create a channel for sending notifications
     */
    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_id";
        CharSequence name = "Focus Channel";
        int importantce = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importantce);
        notificationManager.createNotificationChannel(mChannel);
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

            if ( getArguments().getInt(ARG_SECTION_NUMBER) == 1 ) {
                profilesListViewController plvc = new profilesListViewController();
                FocusModel.getInstance().plvc = plvc;
                return plvc.onCreateView(inflater, container, savedInstanceState, getActivity(), getContext(), hm, mContext);
            } else {
                schedulesListViewController slvc = new schedulesListViewController();
                FocusModel.getInstance().slvc = slvc;
                return slvc.onCreateView(inflater, container, savedInstanceState, getActivity(), getContext(), mContext);
            }
            // View rootView = inflater.inflate(R.layout.profiles_list_view_fragment, container, false);

            // return rootView;
        }
    }
}