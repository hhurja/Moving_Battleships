package movingbattleship.org.focus;

import org.junit.Before;
import org.junit.Test;

import Model.App;

import static org.junit.Assert.assertEquals;

/**
 * Created by aaronrschrock on 10/25/17.
 */

public class AppUnitTest {

    /**
     * App Test Cases
     */

    private App a;

    @Before
    public void setUpForEachTest() {
        a = new App(0, "Maps", "com.google.android.apps.maps");
    }

    @Test
    //Tests if an app gets activated correctly
    public void app_activate() throws Exception {
        assertEquals(a.isBlocked(), false);
        a.blockApp(0);
        assertEquals(a.isBlocked(), true);
    }

    @Test
    //Tests if an app gets deactivated correctly
    public void app_deactivate() throws Exception {
        a.blockApp(0);
        a.unblockApp(0);
        assertEquals(a.isBlocked(), false);
    }

    @Test
    //Tests the functionality of an app being activated and deactivated by multiple different profiles
    public void app_activated_by_mult_profiles() throws Exception {
        a.blockApp(0); //block app from profile 0
        a.blockApp(1); //block app from profile 1
        assertEquals(a.isBlocked(), true);
        a.unblockApp(1);
        assertEquals(a.isBlocked(), true);
        a.unblockApp(0);
        assertEquals(a.isBlocked(), false);
    }
}
