package Controller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.*;
import android.widget.CompoundButton;
import android.widget.CompoundButton.*;
import android.view.View;
import android.widget.CheckBox;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import movingbattleship.org.focus.R;
import movingbattleship.org.focus.*;
import Model.*;

/**
 * Created by adammoffitt on 10/9/17.
 */

public class EditProfileListAdapter extends ArrayAdapter<String>{
    private Profile profile;
    private String name = "";
    private FocusModel focusModel;
    private View applicationsView;
    //this will store the hashmap of the application packagename with corresponding icon
    public HashMap<String, Bitmap> icons;
    private HashMap<Button, String> buttonToName = new HashMap <Button, String> ();
    private HashMap<String, String> nameToPackage;

    public EditProfileListAdapter(@NonNull Context context, String[] profileNames, HashMap<String, String> nameToPackage) {
        super(context, R.layout.edit_profile_row, profileNames);
        this.nameToPackage = nameToPackage;
        focusModel = FocusModel.getInstance();
        profile = focusModel.getCurrentProfile();
    }
    /*public profilesListAdapter(@NonNull Context context, String[] profileNames) {
        super(context, R.layout.profile_row ,profileNames);
    } */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater profilesInflator = LayoutInflater.from(getContext());

         applicationsView = profilesInflator.inflate(R.layout.edit_profile_row, parent, false);
        // parse name from profile name
        name = getItem(position);
        TextView profileNameTextView = (TextView) applicationsView.findViewById(R.id.profileName);
        // of apps on that specific profile to be blocked
        ImageView appImage1 = (ImageView) applicationsView.findViewById(R.id.image1);

        appImage1.setImageResource(R.drawable.facebook);



        profileNameTextView.setText(name);

        Button button = (Button) applicationsView.findViewById(R.id.remove_button);
        buttonToName.put(button, name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;
                String str = nameToPackage.get(buttonToName.get((Button) view));
                System.out.println("string in remove from profile is : " + str);
                /*profilesListViewController.profilesAdapter = new profilesListAdapter(profilesListViewController.context, profileNames, icons);
                profilesListViewController.profilesListView.setAdapter(profilesAdapter);
                profilesListViewController.profilesListView.invalidate(); */
                // remove from profile
                view.setVisibility(ListView.INVISIBLE);
                focusModel.removeAppFromProfile(applicationsView.getContext(), str, profile.getProfileName());
                //EditProfileListAdapter.notifyDataSetChanged();
                view.setVisibility(ListView.VISIBLE);
            }
        });

        //using the application icon from the hashmap for exampe
        System.out.println("name is " + nameToPackage.get(name));
        System.out.println((focusModel.getIconMap()).get(nameToPackage.get(name)));
        //appImage1.setImageBitmap((focusModel.getIconMap()).get(name));
        appImage1.setImageBitmap((focusModel.getIconMap()).get(nameToPackage.get(name)));
        return applicationsView;
    }
}