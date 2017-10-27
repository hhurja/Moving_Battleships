package movingbattleship.org.focus.schedulesTests;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import Controller.MainActivity;
import movingbattleship.org.focus.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by adammoffitt on 10/25/17.
 */

public class scheduleListViewTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    // 7.1. A user can create new schedules
    @Test
    public void ensureCreateScheduleWorks() {

        Espresso.onView(withId(R.id.schedulesTab)).perform(click());

        onView(withId(R.id.addScheduleButton))
                .perform(click());
        //onView(withId(12345)).perform(typeText("TestScheduleName"), closeSoftKeyboard());
    }

    // 4.2. A schedule can include any of the existing profiles

}
