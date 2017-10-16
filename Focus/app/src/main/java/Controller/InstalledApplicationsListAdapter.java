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
    private String name = "";
    //this will store the hashmap of the application packagename with corresponding icon
    public HashMap<String, Bitmap> icons;

    public InstalledApplicationsListAdapter(@NonNull Context context, String[] profileNames, List<ApplicationInfo> packages, Profile profile) {
        super(context, R.layout.application_chooser_row, profileNames);
        this.profile = profile;
    }
    /*public profilesListAdapter(@NonNull Context context, String[] profileNames) {
        super(context, R.layout.profile_row ,profileNames);
    } */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        System.out.println("getting view");
        LayoutInflater profilesInflator = LayoutInflater.from(getContext());

        View applicationsView = profilesInflator.inflate(R.layout.application_chooser_row, parent, false);
        // parse name from profile name
        name = getItem(position);
        TextView profileNameTextView = (TextView) applicationsView.findViewById(R.id.profileName);
        // of apps on that specific profile to be blocked
        ImageView appImage1 = (ImageView) applicationsView.findViewById(R.id.image1);

        profileNameTextView.setText(name);

        CheckBox cb = (CheckBox) applicationsView.findViewById(R.id.checkBox3);
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {
                    //App app = new App(1, name, false);
                    //add application to profile
                    //profile.addApp(app);
                }
            }

            });

        //SHABINA TODO: populate with actual image
        //appImage1.setImageResource(R.drawable.facebook);

        //using the application icon from the hashmap for exampe
        //appImage1.setImageBitmap(icons.get("com.google.android.apps.maps"));
        return applicationsView;
    }
}