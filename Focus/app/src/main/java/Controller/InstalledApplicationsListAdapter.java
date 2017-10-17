package Controller;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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

public class InstalledApplicationsListAdapter extends ArrayAdapter<String>{
    private Profile profile;
    private FocusModel focusModel;
    private String name = "";
    static View applicationsView;
    static ViewGroup parent;
    //this will store the hashmap of the application packagename with corresponding icon
    public HashMap<String, Bitmap> icons;
    private HashMap <CheckBox, String> buttonToName = new HashMap <CheckBox, String>();
    private HashMap<String, String> nameToPackage;

    public InstalledApplicationsListAdapter(@NonNull Context context, String[] profileNames, HashMap <String, String> nameToPackage) {
        super(context, R.layout.application_chooser_row, profileNames);
        this.nameToPackage = nameToPackage;
        this.focusModel = FocusModel.getInstance();
        this.profile = focusModel.getCurrentProfile();
    }
    /*public profilesListAdapter(@NonNull Context context, String[] profileNames) {
        super(context, R.layout.profile_row ,profileNames);
    } */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        System.out.println("getting view");
        LayoutInflater profilesInflator = LayoutInflater.from(getContext());
        this.parent = parent;

        applicationsView = profilesInflator.inflate(R.layout.application_chooser_row, parent, false);
        // parse name from profile name
        name = getItem(position);
        TextView profileNameTextView = (TextView) applicationsView.findViewById(R.id.profileName);
        // of apps on that specific profile to be blocked
        ImageView appImage1 = (ImageView) applicationsView.findViewById(R.id.image1);

        profileNameTextView.setText(name);
        System.out.println("name is " + name);

        CheckBox cb = (CheckBox) applicationsView.findViewById(R.id.checkBox3);
        buttonToName.put(cb, name);
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                String str = nameToPackage.get(buttonToName.get((CheckBox) buttonView));
                if (isChecked) {
                    //add application to profile

                    System.out.println("name is " + str);
                    focusModel.addAppToProfile(applicationsView.getContext(), str, profile.getProfileName());
                    System.out.println("new size of profile apps is " + profile.getApps().size());
                } else {
                    // remove from profile
                    focusModel.removeAppFromProfile(applicationsView.getContext(), str, profile.getProfileName());
                }
            }

        });

        //SHABINA TODO: populate with actual image
        //appImage1.setImageResource(R.drawable.facebook);

        //using the application icon from the hashmap for exampe
        appImage1.setImageBitmap((focusModel.getIconMap()).get(nameToPackage.get(name)));
        return applicationsView;
    }
}
