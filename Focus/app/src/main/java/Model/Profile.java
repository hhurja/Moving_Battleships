package Model;

import java.util.ArrayList;

/**
 * Created by aaronrschrock on 10/6/17.
 */

/**
 * Profile is a class that resembles a profile
 * Each profile contains a list of apps within it,
 * and keeps track of teh apps that should be blocked
 */

public class Profile {

    private Integer profileID;
    private String profileName;
    private ArrayList<App> apps;
    private ArrayList<Integer> scheduleIDs; //TODO do we need this?
    private boolean activated;

    /**
     * Constructors
     */
    public Profile(Integer profileID, String profileName) {
        this.profileID = profileID;
        this.profileName = profileName;
        activated = false;
    }

    /**
     * Getters and Setters
     */

    public Integer getProfileID() {
        return profileID;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public ArrayList<App> getApps() {
        return apps;
    }

    public boolean isActivated() {
        return activated;
    }

    public void addApp(App app) {
        apps.add(app);
    }

    public void removeApp(App app) {
        if(!apps.contains(app)) {
            return;
        }

        //loop through the profile's apps and delete app
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).getName().equals(app.getName())) {
                apps.remove(i);
                return;
            }
        }
    }

    /**
     * Blocking functions
     */

    public void activate() {
        activated = true;
        blockProfile();
    }

    public void deactivate() {
        activated = false;
        unblockProfile();
    }

    public void blockProfile() {
        for (int i = 0; i < apps.size(); i++) {
            apps.get(i).blockApp();
        }
    }

    public void unblockProfile() {
        for (int i = 0; i < apps.size(); i++) {
            //TODO Check if app is blocked by another profile, if so don't unblock

            apps.get(i).unblockApp();
        }
    }

    /**
     * TODO
    private ArrayList<App> getBlockedApps() {
        return null;
    }
    **/



}
