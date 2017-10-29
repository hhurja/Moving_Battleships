package movingbattleship.org.focus;

import org.junit.Test;

import Model.FocusModel;

import static junit.framework.Assert.assertEquals;

/**
 * Created by HunterHurja on 10/29/17.
 */

public class FocusModelUnitTest {

    private FocusModel fm;

    public void setUp(){
        fm = FocusModel.getInstance();
    }

    public void cleanUp(){
        while(fm.getAllSchedules().size() > 0){
            fm.removeSchedule(fm.getAllSchedules().get(0).getScheduleID());
        }
        while(fm.getAllProfiles().size() > 0){
            fm.removeProfile(fm.getAllProfiles().get(0).getProfileName());
        }
        while(fm.getAllProfiles().size() > 0){
            fm.removeApp(fm.getAllApps().get(0).getAppID());
        }
    }

    @Test
    public void numSchedulesTest(){
        /**
         * Tests to make sure that numSchedules increments correctly
         */
        setUp();
        assertEquals(0, fm.getNumSchedulesCreated());
        fm.createNewSchedule("Sched_1");
        assertEquals(1, fm.getNumSchedulesCreated());
        fm.createNewSchedule("Sched_2");
        assertEquals(2, fm.getNumSchedulesCreated());
        fm.removeSchedule(1);
        assertEquals(2, fm.getNumSchedulesCreated());
        cleanUp();
    }

    @Test
    public void numAppsTest(){
        /**
         * Tests to make sure that numApps increments correctly
         */
        //TODO do I even need this?
        setUp();
        cleanUp();

    }

    @Test
    public void numProfilesTest(){
        /**
         * Tests to make sure that numProfiles increments correctly
         */
        setUp();
        assertEquals(0, fm.getNumProfilesCreated());
        fm.createNewProfile("prof_1");
        assertEquals(1, fm.getNumProfilesCreated());
        fm.createNewProfile("prof_2");
        assertEquals(2, fm.getNumProfilesCreated());
        fm.removeProfile("prof_1");
        assertEquals(2, fm.getNumProfilesCreated());
        cleanUp();

    }

    @Test
    public void getBlockedAppsTest(){

    }


//            FocusModel
//- [ ] numapps, numschedules, numprofiles increment/decrement correctly
//- [ ] getblockedapps
//- [ ] removeProfileFromSchedule
//- [ ] addProfileToSchedule
//- [ ] already exists
//- [ ] updateWithSchedules
}
