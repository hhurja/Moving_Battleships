package Controller;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Model.FocusModel;
import Model.Profile;
import movingbattleship.org.focus.R;

public class profilesListViewController extends Fragment {
    //this hashmap stores the application and their corresponding icons
    public static HashMap<String, Bitmap> icons;
    private static Context mContext;
    FocusModel focusModel;
    private static View rootView;
    private static ListAdapter profilesAdapter;
    private static ListView profilesListView;
    private static  LayoutInflater inflater;
    private static  ViewGroup container;
    private static Bundle savedInstanceState;
    private static Activity activity;
    private static Context context;
    FloatingActionButton fb;
    public HashMap<Profile, Long> profileUsage = new HashMap<Profile, Long>();
    private String [] profileNames;

    public TimerClass timerInstance;

    public class TimerClass extends CountDownTimer {

        public TimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            profileUsage = focusModel.getProfileUsage();
//            String timerStr = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
//                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
//                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
//                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
//                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
//            );
            //synchronized(profilesAdapter.this) {
            //updating scroll position
            // save index and top position
            int index = profilesListView.getFirstVisiblePosition();
            View v = profilesListView.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - profilesListView.getPaddingTop());

            ArrayList<Profile> profiles = focusModel.getAllProfiles();
            profileNames = new String[profiles.size()];
            for (int i = 0; i < profiles.size(); i++) {
                profileNames[i] = profiles.get(i).getProfileName();
            }
            Collections.sort(java.util.Arrays.asList(profileNames),new Comparator<String>()
                    {
                        @Override
                        public int compare(String prof1,String prof2)
                        {
                            long p1Time = 0;
                            long p2Time = 0;

                            ArrayList<Profile> profiles = focusModel.getAllProfiles();
                            for (int i = 0; i < profiles.size(); i++) {
                                if (prof1 == profiles.get(i).getProfileName()) {
                                    p1Time = profiles.get(i).getActivationTime();
                                    break;
                                }
                            }
                            for (int i = 0; i < profiles.size(); i++) {
                                if (prof2 == profiles.get(i).getProfileName()) {
                                    p2Time = profiles.get(i).getActivationTime();
                                    break;
                                }
                            }
                            if (p1Time > p2Time) {
                                return -1;
                            } else if (p1Time < p2Time){
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    });


            profilesAdapter = new profilesListAdapter(profilesListViewController.context, profileNames, focusModel.getIconMap());
            if (profilesListView != null && profilesAdapter != null) {
                profilesListView.setAdapter(profilesAdapter);
                profilesListView.setSelectionFromTop(index, top);
            }

            //}
        }

        @Override
        public void onFinish() {

        }
    }

    public void refreshData() {
        ((BaseAdapter)profilesListView.getAdapter()).notifyDataSetChanged();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, Activity activity, Context context, HashMap<String, Bitmap> hm, Context c) {
        System.out.println("in on create view");
        this.inflater = inflater;
        this.container = container;
        this.savedInstanceState = savedInstanceState;
        this.activity = activity;
        this.context = context;


        focusModel = FocusModel.getInstance();
        System.out.println("profiles size in create view is " + focusModel.getAllProfiles().size());
        icons = hm;
        //System.out.println("here");
        //System.out.println("List view iz..." );
        mContext = c;
        rootView = inflater.inflate(R.layout.profiles_list_view_fragment, container, false);

        this.fb = (FloatingActionButton) rootView.findViewById(R.id.addProfileButton);
        fb.show();
        //System.out.println(this.getContext());
        //System.out.println("here 2");
        //  RUTH TODO: replace with actual names of profiles
        // need access to FocusModel, which can access list of profiles



        ArrayList<Profile> profiles = focusModel.getAllProfiles();
        profileNames = new String[profiles.size()];
        for (int i = 0; i < profiles.size(); i++) {
            profileNames[i] = profiles.get(i).getProfileName();
        }




        // pass profileNames into profileListAdapter constructor

        //
        //String[] names = {"Dating Apps", "Hunter's List", "Social Media", "Dinosaurs"};
         profilesListView = (ListView) rootView.findViewById(R.id.profilesListView);
        //System.out.println("List view iz..." + (ListView) rootView.findViewById(R.id.profilesListView));
        //System.out.println("here 3");
        profilesAdapter = new profilesListAdapter(context, profileNames, icons);
        /*if (profilesListView != null && activity != null) {
             ArrayAdapter <String> listViewAdapter = new ArrayAdapter<String> (activity,
                     android.R.layout.simple_list_item_1, android.R.id.text1, names);
            profilesListView.setAdapter(listViewAdapter);
         } */



        //System.out.println("here 5");

        if (profilesListView != null) {
            profilesListView.setAdapter(profilesAdapter);
            timerInstance = new TimerClass(1000000, 1000);
            timerInstance.start();
            System.out.println("made timer");
            profilesListView.setOnItemClickListener(

                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String name = String.valueOf(parent.getItemAtPosition(position));
                            System.out.println(name); // just check to see if this tap is working / list view works
                            // set current profile
                            focusModel.setCurrentProfile(name);
                            Intent intent = new Intent(profilesListViewController.mContext, EditProfile.class);
                            //ep.setProfile(p);
                            profilesListViewController.mContext.startActivity(intent);
                        }
                    }

            );
        }


        fb.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Create Profile");
                        ScrollView sv = new ScrollView(v.getContext());
                        LinearLayout layout = new LinearLayout(v.getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final EditText titleBox = new EditText(v.getContext());
                        titleBox.setHint("Title");
                        titleBox.setInputType(InputType.TYPE_CLASS_TEXT);
                        layout.addView(titleBox);
                        sv.addView(layout);
                        builder.setView(sv);

                        // Set up the buttons
                        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: Add new profile with name in text box
                                System.out.println("profiles size is " + focusModel.getAllProfiles().size());
                                focusModel.createNewProfile(titleBox.getText().toString());
                                /*rootView.invalidate();
                                profilesListViewController plvc = new profilesListViewController();
                                rootView = plvc.onCreateView(profilesListViewController.inflater, profilesListViewController.container,
                                        profilesListViewController.savedInstanceState, profilesListViewController.activity, profilesListViewController.context,
                                        profilesListViewController.icons, profilesListViewController.mContext);
                                rootView.invalidate(); */
                                ArrayList<Profile> profiles = focusModel.getAllProfiles();
                                String[] profileNames = new String[profiles.size()];
                                for (int i = 0; i < profiles.size(); i++) {
                                    profileNames[i] = profiles.get(i).getProfileName();
                                }
                                profilesListViewController.profilesAdapter = new profilesListAdapter(profilesListViewController.context, profileNames, icons);
                                profilesListViewController.profilesListView.setAdapter(profilesAdapter);
                                profilesListViewController.profilesListView.invalidate();
                                System.out.println("profiles size after update is " + focusModel.getAllProfiles().size());
                                //Intent intent = new Intent(profilesListViewController.context, MainActivity.class);
                                //startActivity(intent);
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
        return rootView;
    }
}
