package Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import Model.FocusModel;
import Model.Profile;
import movingbattleship.org.focus.R;

/**
 * Created by adammoffitt on 10/9/17.
 */

public class InstalledApplicationsListAdapter extends ArrayAdapter<String>{
    private Profile profile;
    private FocusModel focusModel;
    private String name = "";
    static View applicationsView;
    static ViewGroup parent;
    private HashMap <CheckBox, String> buttonToName = new HashMap <CheckBox, String>();
    private HashMap<String, String> nameToPackage;
    private CheckBox addToProfileCB;
    private boolean allChecked;

    public InstalledApplicationsListAdapter(@NonNull Context context, String[] profileNames, HashMap <String, String> nameToPackage,
                                            boolean allChecked) {
        super(context, R.layout.application_chooser_row, profileNames);
        this.nameToPackage = nameToPackage;
        this.focusModel = FocusModel.getInstance();
        this.profile = focusModel.getCurrentProfile();
        this.allChecked = allChecked;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater profilesInflator = LayoutInflater.from(getContext());
        this.parent = parent;

        applicationsView = profilesInflator.inflate(R.layout.application_chooser_row, parent, false);
        // parse name from profile name
        name = getItem(position);
        TextView profileNameTextView = (TextView) applicationsView.findViewById(R.id.profileName);

        ImageView appImage1 = (ImageView) applicationsView.findViewById(R.id.image1);
        // populate image icon with actual application image
        if (focusModel != null && focusModel.getIconMap() != null) {
            appImage1.setImageBitmap((focusModel.getIconMap()).get(nameToPackage.get(name)));
        }

        profileNameTextView.setText(name);


        addToProfileCB = (CheckBox) applicationsView.findViewById(R.id.checkBox3);
        buttonToName.put(addToProfileCB, name);
        if (profile.FindAppInProfile(name) || allChecked) {
            addToProfileCB.setChecked(true);
        }
        addToProfileCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                String str = nameToPackage.get(buttonToName.get((CheckBox) buttonView));
                if (isChecked) {
                    //add application to profile
                    focusModel.addAppToProfile(applicationsView.getContext(), str, profile.getProfileName());
                } else {
                    // remove from profile
                    focusModel.removeAppFromProfile(applicationsView.getContext(), str, profile.getProfileName());
                }
            }

        });

        return applicationsView;
    }
}
