package Controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import Model.FocusModel;
import Model.Profile;
import movingbattleship.org.focus.R;

/**
 * Created by adammoffitt on 10/9/17.
 */

public class profilesListAdapter extends ArrayAdapter<String>{
    //this will store the hashmap of the application package name with corresponding icon
    public HashMap<String, Bitmap> icons;
    private FocusModel focusModel;
    private Profile profile;
    public TextView timerText;
    private Context context;

    public profilesListAdapter(@NonNull Context context, String[] profileNames, HashMap<String, Bitmap> hm) {
        super(context, R.layout.profile_row, profileNames);
        icons = hm;
        focusModel = FocusModel.getInstance();
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater profilesInflator = LayoutInflater.from(getContext());

        View profilesView = profilesInflator.inflate(R.layout.profile_row, parent, false);
        // parse name from profile name
        String name = getItem(position);
        TextView profileNameTextView = (TextView) profilesView.findViewById(R.id.profileName);

        profile = focusModel.getProfile(name);

        timerText = (TextView) profilesView.findViewById(R.id.timer);
        //sends the timerText to profile class so it can update automatically once a profile is blocked


        if (profile.isActivated()) {
            timerText.setVisibility(TextView.VISIBLE);
            //if (profile.isActivated()) {
            long millisUntilFinished = focusModel.remainingTime(profile.getProfileName());
                        String timerStr = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            );
                timerText.setText(timerStr);
            //}
            //timerText.setText("not changed yet");
            //System.out.println("remaining time in proflistadapter: " + focusModel.remainingTime(profile.getProfileName()));
            //timerInstance = new TimerClass(focusModel.remainingTime(profile.getProfileName()), 1000);
            //timerInstance.start();
        } else {
            timerText.setText("Profile inactive");
        }

        profile.getListView(timerText);

        ImageView appImage1 = (ImageView) profilesView.findViewById(R.id.image1);
        ImageView appImage2 = (ImageView) profilesView.findViewById(R.id.image2);
        ImageView appImage3 = (ImageView) profilesView.findViewById(R.id.image3);

        profileNameTextView.setText(name);
        profile = focusModel.getProfile(name);
        if (profile.getApps().size() > 0 && focusModel.getIconMap() != null) {
            String appName = profile.getApps().get(0).getPackageName();
            appImage1.setImageBitmap(focusModel.getIconMap().get(appName));
        }
        if (profile.getApps().size() > 1 && focusModel.getIconMap() != null) {
            String appName = profile.getApps().get(1).getPackageName();
            appImage2.setImageBitmap(focusModel.getIconMap().get(appName));
        }
        if (profile.getApps().size() > 2 && focusModel.getIconMap() != null) {
            String appName = profile.getApps().get(2).getPackageName();
            appImage3.setImageBitmap(focusModel.getIconMap().get(appName));
        }

        return profilesView;
    }
}
