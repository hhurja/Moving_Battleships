package movingbattleship.org.focus;

import org.junit.Test;

import Model.AppIconGenerator;
import Model.AppProcessChecker;
import Model.DatabaseHelper;
import Model.FocusModel;
import Model.Profile;
import Model.Schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by HunterHurja on 10/26/17.
 */


public class ScheduleUnitTest {

    private FocusModel fm;
    private Schedule s0;
    private Profile p0;

    public void setUp(){
        s0 = new Schedule(-1, "test_sched_0", true);
        p0 = new Profile(-1, "test_prof_0");

        fm = FocusModel.getInstance();
        fm.createNewSchedule("test_sched_1");
        fm.createNewProfile("test_prof_1");

    }

    public void cleanup(){
        // TODO write a function that clears everything
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
    public void constructor_Test() throws Exception {
        /**
         * tests the two constructors for schedule which are used
         * tests creating a schedule by calling the instantiation method from focusmodel
         */
        setUp();
        assertEquals(s0.getScheduleName(), s0.getScheduleName());
        assertEquals("test_sched_1", fm.getSchedule("test_sched_1").getScheduleName());
        assertEquals(-1, s0.getScheduleID());
        assertEquals(false, s0.isVisible());
        cleanup();
    }

    @Test
    public void changeNameTest(){
        /**
         * tests the ability to change the name of a schedule once it is created
         */
        setUp();
        s0.setScheduleName("new_name");
        assertEquals("new_name", s0.getScheduleName());
        cleanup();

    }

    @Test
    public void changeNameToBlankTest(){
        setUp();
        s0.setScheduleName("");
        assertNotEquals("", s0.getScheduleName());
        cleanup();
    }

    @Test
    public void addProfileToScheduleTest(){
        setUp();
        s0.addProfile(p0);
        fm.addProfileToSchedule(p0.getProfileName(), s0.getScheduleName());
        assertEquals(p0.getProfileName(), s0.getProfiles().get(0).getProfileName());
        assertEquals(1, fm.getAllSchedules().size());
        assertEquals("test_prof_1", fm.getAllProfiles().get(0).getProfileName());

        cleanup();
    }


//    - [ ] Constructor
//- [ ] Change name
//- [ ] schedule name should not be empty
//- [ ] add profile(s) to schedule
//- [ ] remove profile(s) from schedule
//- [ ] edit timerange of schedule
//- [ ] set timerange, test if in range (set time range to start in 30 seconds, last for 30)
//- [ ] add/clear timerange
//- [ ] repeating
//- [ ] delete schedule
//    [ ] maybe also tests to show the objects created are the same
}
