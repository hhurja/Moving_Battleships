package Controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import Model.*;

import movingbattleship.org.focus.R;
import movingbattleship.org.focus.*;

/**
 * Created by adammoffitt on 10/9/17.
 */

public class profilesListAdapter extends ArrayAdapter<String>{
    //this will store the hashmap of the application packagename with corresponding icon
    public HashMap<String, Bitmap> icons;
    private FocusModel focusModel;
    private Profile profile;

    public profilesListAdapter(@NonNull Context context, String[] profileNames, HashMap<String, Bitmap> hm) {
        super(context, R.layout.profile_row, profileNames);
        icons = hm;
        System.out.println("in constructor");
        focusModel = FocusModel.getInstance();
    }
    /*public profilesListAdapter(@NonNull Context context, String[] profileNames) {
        super(context, R.layout.profile_row ,profileNames);
    } */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        System.out.println("getting view");
        LayoutInflater profilesInflator = LayoutInflater.from(getContext());

        View profilesView = profilesInflator.inflate(R.layout.profile_row, parent, false);
        // parse name from profile name
        String name = getItem(position);
        TextView profileNameTextView = (TextView) profilesView.findViewById(R.id.profileName);
        // TODO: RUTH populate first three images as + symbols by default, add up to 3 images
        // of apps on that specific profile to be blocked
        ImageView appImage1 = (ImageView) profilesView.findViewById(R.id.image1);
        ImageView appImage2 = (ImageView) profilesView.findViewById(R.id.image2);
        ImageView appImage3 = (ImageView) profilesView.findViewById(R.id.image3);

        profileNameTextView.setText(name);
        //appImage1.setImageResource(R.drawable.facebook);
        //appImage2.setImageResource(R.drawable.instagram);
        //appImage3.setImageResource(R.drawable.snapchat);

        //using the application icon from the hashmap for exampe


        //int count = 0;
        //while (count < 3 && count < profile.getApps().size()) {

        //}
        profile = focusModel.getProfile(name);
        if (profile.getApps().size() > 0) {
            String appName = profile.getApps().get(0).getPackageName();
            appImage1.setImageBitmap(focusModel.getIconMap().get(appName));
        }
        if (profile.getApps().size() > 1) {
            String appName = profile.getApps().get(1).getPackageName();
            appImage2.setImageBitmap(focusModel.getIconMap().get(appName));
        }
        if (profile.getApps().size() > 2) {
            String appName = profile.getApps().get(2).getPackageName();
            appImage3.setImageBitmap(focusModel.getIconMap().get(appName));
        }


        Switch scheduledSwitch = (Switch) profilesView.findViewById(R.id.switch1);
        if (profile.isActivated()) {
            scheduledSwitch.setChecked(true);
        } else {
            scheduledSwitch.setChecked(false);
        }
        scheduledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    profile.activate();
                } else {
                    profile.deactivate();
                }
            }
        });
        return profilesView;
    }
}
