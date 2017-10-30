package movingbattleship.org.focus;

import org.junit.Before;
import org.junit.Test;

import Controller.EditSchedule;
import Model.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by aaronrschrock on 10/25/17.
 */

public class ProfileUnitTest {

    private App a;
    private App a2;
    private Profile p;
    private Profile p2;
    private FocusModel fm;

    @Before
    public void setUpForEachTest() {
        p = new Profile(0, "TestProfile");
        p2 = new Profile(1, "TestProfil2");
        a = new App(0, "Maps", "com.google.android.apps.maps");
        a2 = new App(1, "TextPlus", "com.gogii.textplus");
        fm = FocusModel.getInstance();
    }

    @Test
    //Test changing the profile name
    public void change_profile_name() throws Exception {
        assertEquals(p.getProfileName(), "TestProfile");
        p.setProfileName("NewName");
        assertEquals(p.getProfileName(), "NewName");
    }

    @Test
    //Test adding apps to a profile
    public void add_apps_to_profile() throws Exception {
        assertEquals(p.getApps().size(), 0);
        p.addApp(a);
        assertEquals(p.getApps().size(), 1);
        p.addApp(a2);
        assertEquals(p.getApps().size(), 2);

        assertEquals(p.getApps().get(0).getAppName(), a.getAppName());
        assertEquals(p.getApps().get(1).getAppName(), a2.getAppName());
    }

    @Test
    //Tests REmoving apps from a profile
    public void remove_apps_from_profile() throws Exception {
        p.addApp(a);
        p.addApp(a2);
        p.removeApp(a.getAppID());

        assertEquals(p.getApps().size(), 1);
        assertEquals(p.getApps().get(0).getAppName(), a2.getAppName());

        p.removeApp(a2.getAppID());
        assertEquals(p.getApps().size(), 0);
    }

    @Test
    //Test activating and deactivating a profile
    public void activate_profile() throws Exception {
        assertEquals(p.isActivated(), false);
        assertEquals(a.isBlocked(), false);
        assertEquals(a2.isBlocked(), false);

        p.addApp(a);
        p.addApp(a2);
        p.activate();

        assertEquals(p.isActivated(), true);
        assertEquals(a.isBlocked(), true);
        assertEquals(a2.isBlocked(), true);

        p.deactivate();

        assertEquals(p.isActivated(), false);
        assertEquals(a.isBlocked(), false);
        assertEquals(a2.isBlocked(), false);
    }

    @Test
    //Test activating and deactivating mulitple profiles
    public void activate_multiple_profiles() throws Exception {
        assertEquals(p.isActivated(), false);
        assertEquals(p2.isActivated(), false);
        assertEquals(a.isBlocked(), false);
        assertEquals(a2.isBlocked(), false);

        p.addApp(a);
        p.addApp(a2);
        p.activate();
        p2.addApp(a);
        p2.addApp(a2);
        p2.activate();

        assertEquals(p.isActivated(), true);
        assertEquals(p2.isActivated(), true);
        assertEquals(a.isBlocked(), true);
        assertEquals(a2.isBlocked(), true);

        p.deactivate();

        assertEquals(p.isActivated(), false);
        assertEquals(p2.isActivated(), true);
        assertEquals(a.isBlocked(), true);
        assertEquals(a2.isBlocked(), true);

        p2.deactivate();
        assertEquals(p.isActivated(), false);
        assertEquals(p2.isActivated(), false);
        assertEquals(a.isBlocked(), false);
        assertEquals(a2.isBlocked(), false);
    }

    @Test
    //Delete profile that has activated apps
    public void delete_profile() {
        fm.createNewProfile("DeleteProfile");
        fm.addAppToProfile(null, "com.google.android.apps.maps", "DeleteProfile");
        fm.addAppToProfile(null, "com.gogii.textplus", "DeleteProfile");

        fm.activateProfile("DeleteProfile");
        assertEquals(fm.getAllProfiles().get(0).isActivated(), true);
        assertEquals(fm.getAllProfiles().size(), 1);
        assertEquals(fm.getAllApps().size(), 2);

        fm.removeProfile("DeleteProfile");
        assertEquals(fm.getAllProfiles().size(), 0);
        assertEquals(fm.getAllApps().size(), 2);

        //check that apps are no longer active after profile deleted
        assertEquals(fm.getAllApps().get(0).isBlocked(), false);
        assertEquals(fm.getAllApps().get(1).isBlocked(), false);
        assertEquals(fm.getBlockedApps().size(), 0);
    }
}
