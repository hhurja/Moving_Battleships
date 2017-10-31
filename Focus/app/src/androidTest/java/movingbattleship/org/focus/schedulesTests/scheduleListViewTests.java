package movingbattleship.org.focus.schedulesTests;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import Controller.MainActivity;
import Model.FocusModel;
import Model.Profile;
import movingbattleship.org.focus.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setTime;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static movingbattleship.org.focus.profilesAndScheduleCreation.withIndex;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by adammoffitt on 10/25/17.
 */

public class scheduleListViewTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        onView(allOf(withText("Schedules"), isDescendantOfA(withId(R.id.tabs))))
                .perform(click())
                .check(matches(isDisplayed()));
    }

    // 7.1. A user can create new schedules
    @Test
    public void ensureCreateScheduleWorks() {

        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Social Media");

        onView(allOf(withText("Schedules"), isDescendantOfA(withId(R.id.tabs))))
                .perform(click())
                .check(matches(isDisplayed()));

        // set name
        onView(withId(R.id.addScheduleButton))
                .perform(click());
        onView(withId(R.id.createScheduleNameId)).perform(typeText("TestScheduleName"), closeSoftKeyboard());
        onView(withText("Next")).perform(click());

        //set repeat
        onView(withId(R.id.repeatSwitch)).perform(click());
        onView(withText("Next")).perform(click());

        onView(withText("Social Media")).perform(click());
        onView(withText("Next")).perform(click());

        // choose days
        onView(withText("Monday")).perform(click());
        onView(withText("Wednesday")).perform(click());
        onView(withText("Friday")).perform(click());

        onView(withText("Next")).perform(click());
        // add time ranges
        onView(withIndex(withClassName(containsString("TimePicker")), 0)).perform(setTime(9, 36));
        onView(withIndex(withClassName(containsString("TimePicker")), 1)).perform(setTime(11, 36));
        onView(withText("Create Schedule!")).perform(click());
        onView(withId(R.id.name)).check(matches(withText("TestScheduleName")));
    }


    // 4.4. A schedule should have a convenient view for the week
    @Test
    public void checkWeekView() {
        onView(withText("Schedules")).perform(click());
        onView(withId(R.id.addScheduleButton)).perform(click());
        pause();
        onView(withId(R.id.createScheduleNameId)).perform(typeText("testingWeekView"), closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        onView(withText("Next")).perform(click());
        onView(withText("Social Media")).perform(click());
        onView(withText("Next")).perform(click());
        onView(withText("Wednesday")).perform(click());
        onView(withText("Friday")).perform(click());
        onView(withText("Next")).perform(click());
        onView(withIndex(withClassName(containsString("TimePicker")), 0)).perform(setTime(13, 0));
        onView(withIndex(withClassName(containsString("TimePicker")), 1)).perform(setTime(16, 30));
        onView(withText("Create Schedule!")).perform(click());
        onView(withText("Calendar View")).perform(click());
        pause();
        onView(withId(R.id.action_week_view)).check(matches(isDisplayed()));
        pause();
    }

        // 4.6. For each day of the week a schedule should have a list of profiles that will be activated during that day

    @Test
    public void checkDayofWeekListActivatedProfiles() {
        FocusModel focusModel = FocusModel.getInstance();
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Wednesday");
        days.add("Friday");

        Profile p1 = new Profile(1234,"testProfile1");
        Profile p2 = new Profile(12345,"testProfile2");
        Profile p3 = new Profile(12346,"testProfile3");

        focusModel.createNewSchedule("testingWeekView");
        if( focusModel.getSchedule("testingWeekView") != null) {
            focusModel.getSchedule("testingWeekView").addTimeRange(days, 13, 0, 16, 30);
            focusModel.getSchedule("testingWeekView").addProfile(p1);
            focusModel.getSchedule("testingWeekView").addProfile(p2);
        }

        onView(withId(R.id.calendarActionButton)).perform(ViewActions.click());
        onView(withId(R.id.action_week_view)).perform(ViewActions.click());
        onView(withId(R.id.action_week_view)).check(matches(isDisplayed()));
        pause();

    }


    // 4.5. A schedule should have a convenient view for the day
    @Test
    public void checkDayView() {
        FocusModel focusModel = FocusModel.getInstance();
        ArrayList<String> days = new ArrayList<>();
        days.add("Sunday");
        days.add("Monday");
        Profile p1 = new Profile(234,"testProfile1");
        Profile p2 = new Profile(2345,"testProfile2");
        focusModel.createNewSchedule("testingDayView");
        if( focusModel.getSchedule("testingDayView") != null) {
            focusModel.getSchedule("testingDayView").addTimeRange(days, 7, 0, 10, 30);
            focusModel.getSchedule("testingDayView").addProfile(p1);
            focusModel.getSchedule("testingDayView").addProfile(p2);
        }
        onView(withId(R.id.calendarActionButton)).perform(ViewActions.click());
        onView(withId(R.id.action_day_view)).perform(ViewActions.click());
        onView(withId(R.id.action_day_view)).check(matches(isDisplayed()));
    }

    public void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
