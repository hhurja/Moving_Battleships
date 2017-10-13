package Controller;


import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import movingbattleship.org.focus.R;
import movingbattleship.org.focus.*;

public class profilesListViewController extends Fragment {
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         //System.out.println("here");
         View rootView = inflater.inflate(R.layout.profile_row, container, false);
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
         String[] names = {"Dating Apps", "Hunter's List", "Social Media", "Dinosaurs"};
         //ListAdapter profilesAdapter = new profilesListAdapter (this, names);

         //ListView profilesListView = (ListView) rootView.findViewById(R.id.profilesListView);

         //System.out.println("here 3");
         //ArrayAdapter <String> listViewAdapter = new ArrayAdapter<String> ( getActivity(),
           //      android.R.layout.simple_list_item_1, names);
         //System.out.println("here 4");
        //profilesListView.setAdapter(listViewAdapter);
         //System.out.println("here 5");
        /*
         if ( profilesListView != null ) {
             profilesListView.setAdapter(profilesAdapter);
             profilesListView.setOnItemClickListener(

                     new AdapterView.OnItemClickListener() {
                         @Override
                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                             String name = String.valueOf(parent.getItemAtPosition(position));
                             System.out.println(name); // just check to see if this tap is working / list view works
                         }
                     }

             );
         } */
         return rootView;
     }
}
