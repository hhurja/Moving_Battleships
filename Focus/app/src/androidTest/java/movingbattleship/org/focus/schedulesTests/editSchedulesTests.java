package movingbattleship.org.focus.schedulesTests;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.runner.RunWith;

import Controller.EditSchedule;

/**
 * Created by adammoffitt on 10/25/17.
 */
@RunWith(AndroidJUnit4.class)
public class editSchedulesTests {

        @Rule
        public ActivityTestRule<EditSchedule> mActivityRule =
                new ActivityTestRule<>(EditSchedule.class);

/*
        // 4.1. A schedule should have a name
        @Test
        public void ensureScheduleName() {
            focusModel.createNewSchedule(name);
            // Type text and then press the button.
            onView(withId(R.id.name)).check(View);
        }
        // 4.2. A schedule can include any of the existing profiles
        @Test
        public void changeText_newActivity() {
            // Type text and then press the button.
            onView(withId(R.id.inputField)).perform(typeText("NewText"),
                    closeSoftKeyboard());
            onView(withId(R.id.switchActivity)).perform(click());

            // This view is in a different Activity, no need to tell Espresso.
            onView(withId(R.id.resultView)).check(matches(withText("NewText")));
        }
        */

        // 4.3. A Schedule should span a week

        // 4.4. A schedule should have a convenient view for the week

        // 4.5. A schedule should have a convenient view for the day

        // 4.6. For each day of the week a schedule should have a list of profiles that will be activated during that day

        // 4.7. A schedule should have an On/Off option to repeat a schedule weekly or not

        // 7.2 A user can edit the schedule name
        // 7.3 A user can add profiles to certain days and/or times
        // 7.4 A user can remove profiles from certain days / times

}
