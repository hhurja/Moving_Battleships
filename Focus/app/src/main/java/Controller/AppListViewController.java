package Controller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import movingbattleship.org.focus.R;

/**
 * Created by Ruth on 10/15/17.
 */

public class AppListViewController extends Fragment{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, Activity activity, Context context) {
        View rootView = inflater.inflate(R.layout.edit_profile_view_fragment, container, false);
        System.out.println("here");
        String[] names = {"App 1", "App 2", "App 3", "App 4"};
        ListView alv = (ListView) rootView.findViewById(R.id.AppListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, names);
        alv.setAdapter(adapter);
        //System.out.println("List view iz..." + (ListView) rootView.findViewById(R.id.profilesListView));
        //System.out.println("here 3");
        //ListAdapter profilesAdapter = new profilesListAdapter (context, names, icons);
        /*if (profilesListView != null && activity != null) {
             ArrayAdapter <String> listViewAdapter = new ArrayAdapter<String> (activity,
                     android.R.layout.simple_list_item_1, android.R.id.text1, names);
            profilesListView.setAdapter(listViewAdapter);
         } */

        //System.out.println("here 4");

        //System.out.println("here 5");

        /*if ( profilesListView != null ) {
            profilesListView.setAdapter(profilesAdapter);
            profilesListView.setOnItemClickListener(

                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String name = String.valueOf(parent.getItemAtPosition(position));
                            System.out.println(name); // just check to see if this tap is working / list view works
                            // TODO: open up actual profile
                            Profile p = new Profile(1, name);
                            EditProfile ep = new EditProfile();
                            Intent intent = new Intent(profilesListViewController.mContext, ep.getClass());
                            ep.setProfile(p);
                            profilesListViewController.mContext.startActivity(intent);
                        }
                    }

            );
        } */
        return rootView;
    }
}
