package Controller;


import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import movingbattleship.org.focus.R;
import movingbattleship.org.focus.*;

public class profilesListViewController extends Fragment {
    //this hashmap stores the application and their corresponding icons
    public HashMap<String, Bitmap> icons;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, Activity activity, Context context, HashMap<String, Bitmap> hm) {
        icons = hm;
        //System.out.println("here");
         //System.out.println("List view iz..." );
         View rootView = inflater.inflate(R.layout.profiles_list_view_fragment, container, false);
         //System.out.println(this.getContext());
         //System.out.println("here 2");
         //  RUTH TODO: replace with actual names of profiles
         // need access to FocusModel, which can access list of profiles
        /*
        ArrayList <Profile> profiles = focusModel.getProfiles().getSize()
        String [] profileNames = new String [profiles];
        for (int i = 0; i < profiles.size(); i++) {
            profileNames[i] = profiles.get(i).getName();
        }
        */
         // pass profileNames into profileListAdapter constructor

         //
         String[] names = {"Dating Apps", "Hunter's List", "Social Media", "Dinosaurs"};
         ListView profilesListView = (ListView) rootView.findViewById(R.id.profilesListView);
        System.out.println("List view iz..." + (ListView) rootView.findViewById(R.id.profilesListView));
         //System.out.println("here 3");
        ListAdapter profilesAdapter = new profilesListAdapter (context, names, icons);
        /*if (profilesListView != null && activity != null) {
             ArrayAdapter <String> listViewAdapter = new ArrayAdapter<String> (activity,
                     android.R.layout.simple_list_item_1, android.R.id.text1, names);
            profilesListView.setAdapter(listViewAdapter);
         } */

         //System.out.println("here 4");

         //System.out.println("here 5");

         if ( profilesListView != null ) {
             profilesListView.setAdapter(profilesAdapter);
             profilesListView.setOnItemClickListener(

                     new AdapterView.OnItemClickListener() {
                         @Override
                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                             String name = String.valueOf(parent.getItemAtPosition(position));
                             System.out.println(name); // just check to see if this tap is working / list view works
                             // TODO: open up actual profile
                         }
                     }

             );
         }
         return rootView;
     }
}
