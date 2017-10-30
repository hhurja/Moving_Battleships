package movingbattleship.org.focus.schedulesTests;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import Controller.MainActivity;
import Model.FocusModel;
import movingbattleship.org.focus.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by adammoffitt on 10/25/17.
 */

public class scheduleListViewTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        mActivityRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
        onView(withId(R.id.schedulesTab)).perform(click());
    }

    // 7.1. A user can create new schedules
    @Test
    public void ensureCreateScheduleWorks() {

        onView(withId(R.id.schedulesTab)).perform(click());

        onView(withId(R.id.addScheduleButton))
                .perform(click());
        //onView(withId(12345)).perform(typeText("TestScheduleName"), closeSoftKeyboard());
    }


    // 4.4. A schedule should have a convenient view for the week
    @Test
    public void checkWeekView() {
        FocusModel focusModel = FocusModel.getInstance();
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Wednesday");
        days.add("Friday");

        focusModel.createNewSchedule("testingWeekView");
        if( focusModel.getSchedule("testingWeekView") != null) {
            focusModel.getSchedule("testingWeekView").addTimeRange(days, 13, 0, 16, 30);
        }

        onView(withId(R.id.calendarActionButton)).perform(ViewActions.click());
        onView(withId(R.id.action_week_view)).perform(ViewActions.click());
        onView(withId(R.id.action_week_view)).check(matches(isDisplayed()));
        onView(withText("testingWeekView")).check(matches(isDisplayed()));

    }
    // 4.5. A schedule should have a convenient view for the day
    @Test
    public void checkDayView() {
        FocusModel focusModel = FocusModel.getInstance();
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Wednesday");
        days.add("Friday");
        //focusModel.createNewSchedule("testingDayView", days, 13, 0, 16, 30);
    }

    // 4.2. A schedule can include any of the existing profiles

}
