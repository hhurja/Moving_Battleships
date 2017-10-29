package Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import Model.FocusModel;
import Model.Profile;
import movingbattleship.org.focus.R;

/**
 * Created by adammoffitt on 10/9/17.
 */

public class EditProfileListAdapter extends ArrayAdapter<String>{
    private Profile profile;
    private String name = "";
    private FocusModel focusModel;
    private View applicationsView;
    private HashMap<String, String> nameToPackage;

    public EditProfileListAdapter(@NonNull Context context, String[] profileNames, HashMap<String, String> nameToPackage) {
        super(context, R.layout.edit_profile_row, profileNames);
        this.nameToPackage = nameToPackage;
        focusModel = FocusModel.getInstance();
        profile = focusModel.getCurrentProfile();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater profilesInflator = LayoutInflater.from(getContext());

         applicationsView = profilesInflator.inflate(R.layout.edit_profile_row, parent, false);
        // parse name from profile name
        name = getItem(position);
        TextView profileNameTextView = (TextView) applicationsView.findViewById(R.id.profileName);
        profileNameTextView.setText(name);

        ImageView appImage1 = (ImageView) applicationsView.findViewById(R.id.image1);
        // set image corresponding to application
        if (focusModel != null && focusModel.getIconMap() != null) {
            appImage1.setImageBitmap((focusModel.getIconMap()).get(nameToPackage.get(name)));
        }

        return applicationsView;
    }
}