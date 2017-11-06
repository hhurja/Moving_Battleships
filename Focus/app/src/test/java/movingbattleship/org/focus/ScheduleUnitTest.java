package movingbattleship.org.focus;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import Model.AppIconGenerator;
import Model.AppProcessChecker;
import Model.DatabaseHelper;
import Model.FocusModel;
import Model.Profile;
import Model.Schedule;
import Model.TimeRange;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by HunterHurja on 10/26/17.
 */


public class ScheduleUnitTest {

    private FocusModel fm;
    private Schedule s0;
    private Profile p0;
    private ArrayList<String> al;

    public void setUp() {
        s0 = new Schedule(-1, "test_sched_0", true);
        p0 = new Profile(-1, "test_prof_0");

        fm = FocusModel.getInstance();
        fm.createNewSchedule("test_sched_1");
        fm.createNewProfile("test_prof_1");

    }

    private void setUpTimeRanges() {
        setUp();
        al = new ArrayList<>();
        al.add("sunday");
        al.add("monday");
        al.add("tuesday");
        al.add("wednesday");
        al.add("thursday");
        al.add("friday");
        al.add("saturday");
    }

    public void cleanup() {
        // TODO write a function that clears everything
        while (fm.getAllSchedules().size() > 0) {
            fm.removeSchedule(fm.getAllSchedules().get(0).getScheduleID());
        }
        while (fm.getAllProfiles().size() > 0) {
            fm.removeProfile(fm.getAllProfiles().get(0).getProfileName());
        }
        while (fm.getAllProfiles().size() > 0) {
            fm.removeApp(fm.getAllApps().get(0).getAppID());
        }
        fm.resetAllCreated();
        s0 = null;
        p0 = null;
        al = null;
    }

    @Test
    public void constructorTest() throws Exception {
        /**
         * tests the two schedule constructors which are used
         * tests creating a schedule by calling the instantiation method from focusmodel
         */
        setUp();
        assertEquals("test_sched_0", s0.getScheduleName());
        assertEquals("test_sched_1", fm.getSchedule("test_sched_1").getScheduleName());
        assertEquals(-1, s0.getScheduleID());
        assertEquals(0, fm.getSchedule("test_sched_1").getScheduleID());
        assertEquals(false, s0.isVisible());
        cleanup();
    }

    @Test
    public void changeNameTest() {
        /**
         * tests the ability to change the name of a schedule once it is created
         */
        setUp();
        s0.setScheduleName("new_name");
        assertEquals("new_name", s0.getScheduleName());
        fm.changeScheduleName("test_sched_1", "test_sched_2");
        assertEquals("test_sched_2", fm.getAllSchedules().get(0).getScheduleName());
        cleanup();

    }

    @Test
    public void changeNameToBlankTest() {
        setUp();
        s0.setScheduleName("");
        assertNotEquals("", s0.getScheduleName());
        assertEquals("test_sched_0", s0.getScheduleName());

        fm.changeScheduleName("test_sched_1", "");
        assertNotEquals("", fm.getAllSchedules().get(0).getScheduleName());
        assertEquals("test_sched_1", fm.getAllSchedules().get(0).getScheduleName());
        cleanup();
    }

    @Test
    public void duplicateNameTest() {
        setUp();
        fm.createNewSchedule("test_sched_1");
        assertEquals(1, fm.getAllSchedules().size());

        fm.createNewSchedule("test_sched_2");
        assertEquals(2, fm.getAllSchedules().size());

        fm.changeScheduleName("test_sched_2", "test_sched_1");
        assertNotNull(fm.getSchedule("test_sched_2"));
        cleanup();
    }

    @Test
    public void createScheduleWithBlankNameTest() {
        setUp();
        assertEquals(1, fm.getAllSchedules().size());
        fm.createNewSchedule("");
        assertEquals(1, fm.getAllSchedules().size());
        cleanup();
    }

    @Test
    public void addProfileToScheduleTest() {
        setUp();
        s0.addProfile(p0);
        fm.addProfileToSchedule("test_prof_1", "test_sched_1");
        assertEquals(p0.getProfileName(), s0.getProfiles().get(0).getProfileName());
        assertEquals(1, fm.getAllSchedules().get(0).getProfiles().size());
        assertEquals("test_prof_1", fm.getAllProfiles().get(0).getProfileName());
        assertSame(fm.getAllProfiles().get(0), fm.getAllSchedules().get(0).getProfiles().get(0));


        cleanup();
    }

    @Test
    public void addFakeProfileTest() {
        setUp();
        //To make sure that profiles arent added incorrectly
        fm.addProfileToSchedule("fake_profile", "test_sched_1");
        assertEquals(0, fm.getAllSchedules().get(0).getProfiles().size());
        cleanup();
    }

    @Test
    public void addDuplicateProfileTest() {
        setUp();
        fm.addProfileToSchedule("test_prof_1", "test_sched_1");
        fm.addProfileToSchedule("test_prof_1", "test_sched_1");
        assertEquals(1, fm.getAllSchedules().get(0).getProfiles().size());
        cleanup();
    }

    @Test
    public void removeProfileFromScheduleTest() {
        setUp();
        s0.addProfile(p0);
        assertEquals(1, s0.getProfiles().size());
        s0.removeProfile(p0.getProfileID());
        assertEquals(0, s0.getProfiles().size());

        fm.addProfileToSchedule("test_prof_1", "test_sched_1");
        fm.removeProfileFromSchedule("test_prof_1", "test_sched_1");
        assertEquals(0, fm.getSchedule("test_sched_1").getProfiles().size());

        cleanup();
    }

    @Test
    public void addTimeRangeTest() {
        setUpTimeRanges();

        s0.addTimeRange(al, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.MINUTE + 1);
        assertEquals(1, s0.getTimeRanges().size());
        cleanup();
    }

    @Test
    public void deleteTimeRangeTest() {
        //TODO Im not sure how to remove a timerange
        setUpTimeRanges();

        s0.addTimeRange(al, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.MINUTE + 1);
        s0.clearTimeRanges();
        assertEquals(0, s0.getTimeRanges().size());

        cleanup();
    }

    @Test
    public void setActiveTimeRangeTest() {
        setUpTimeRanges();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);

        fm.addProfileToSchedule("test_prof_1", "test_sched_1");
        fm.getAllSchedules().get(0).addTimeRange(al, hour, min-10, hour, min + 1);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(fm.getAllSchedules().get(0).isInTimeRange());
        assertTrue(fm.getProfile("test_prof_1").isActivated());

//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        assertFalse(fm.getAllSchedules().get(0).isInTimeRange());
//        assertFalse(fm.getProfile("test_prof_1").isActivated());


        cleanup();
    }

    @Test
    public void setInactiveTimeRangeTest() {
        setUpTimeRanges();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        fm.addProfileToSchedule("test_prof_1", "test_sched_1");
        fm.getAllSchedules().get(0).addTimeRange(al, hour + 1, min, hour + 1, min + 15);
        assertFalse(fm.getAllSchedules().get(0).isInTimeRange());
        assertFalse(fm.getProfile("test_prof_1").isActivated());

        cleanup();
    }

    @Test
    public void repeatingTest() {
        //TODO how do we show that schedules are repeating?
        setUpTimeRanges();
        s0.setRepeat(true);
        assertTrue(s0.getRepeat());
        cleanup();
    }

    @Test
    public void deleteScheduleTest() {
        setUp();
        assertEquals(1, fm.getAllSchedules().size());
        fm.removeSchedule(fm.getIdFromName("Schedule", "test_sched_1"));
        assertEquals(0, fm.getAllSchedules().size());
        assertFalse(fm.getProfile("test_prof_1").isActivated());


        cleanup();
    }

}
