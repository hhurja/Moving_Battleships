package Controller;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import movingbattleship.org.focus.R;

/**
 * Created by Ruth on 10/11/17.
 */

public class ProfilesListView extends ListView {
    public ProfilesListView(Context context) {
        super(context);
        setEmptyView(findViewById(R.layout.profiles_list_view_fragment));
    }
}
