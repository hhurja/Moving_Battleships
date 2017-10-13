package Controller;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import movingbattleship.org.focus.R;
import movingbattleship.org.focus.*;

/**
 * Created by adammoffitt on 10/9/17.
 */

public class profilesListAdapter extends ArrayAdapter<String>{

    public profilesListAdapter(@NonNull Context context, String[] profileNames) {
        super(context, R.layout.profile_row ,profileNames);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
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
        appImage1.setImageResource(R.drawable.facebook);
        appImage2.setImageResource(R.drawable.instagram);
        appImage3.setImageResource(R.drawable.snapchat);

        return profilesView;
    }
}
