package Controller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.Button;
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
    //this will store the hashmap of the application packagename with corresponding icon
    public HashMap<String, Bitmap> icons;

    public EditProfileListAdapter(@NonNull Context context, String[] profileNames) {
        super(context, R.layout.edit_profile_row, profileNames);
    }
    /*public profilesListAdapter(@NonNull Context context, String[] profileNames) {
        super(context, R.layout.profile_row ,profileNames);
    } */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater profilesInflator = LayoutInflater.from(getContext());

        View applicationsView = profilesInflator.inflate(R.layout.edit_profile_row, parent, false);
        // parse name from profile name
        name = getItem(position);
        TextView profileNameTextView = (TextView) applicationsView.findViewById(R.id.profileName);
        // of apps on that specific profile to be blocked
        ImageView appImage1 = (ImageView) applicationsView.findViewById(R.id.image1);

        appImage1.setImageResource(R.drawable.facebook);

        profileNameTextView.setText(name);

        Button button = (Button) applicationsView.findViewById(R.id.remove_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;
                // remove from profile

            }
        });

        //using the application icon from the hashmap for exampe
        //appImage1.setImageBitmap(icons.get("com.google.android.apps.maps"));
        return applicationsView;
    }
}