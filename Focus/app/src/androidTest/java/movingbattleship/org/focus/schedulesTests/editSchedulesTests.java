package movingbattleship.org.focus.schedulesTests;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import Controller.EditSchedule;
import Model.FocusModel;
import movingbattleship.org.focus.R;

import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static junit.framework.Assert.assertEquals;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 *
 *
 *
 *
 */
@RunWith(AndroidJUnit4.class)
public class editSchedulesTests {

        @Rule
        public ActivityTestRule<EditSchedule> mActivityRule =
                new ActivityTestRule<>(EditSchedule.class);


        // 4.1. A schedule should have a name
        @Test
        public void ensureScheduleName() {

            FocusModel focusModel = FocusModel.getInstance();
            focusModel.createNewSchedule("Awesome Study Session");

            Intent i = new Intent();
            i.putExtra("scheduleName", "Awesome Study Session");
            mActivityRule.launchActivity(i);

            onView(withId(R.id.name)).check(matches((isDisplayed())));
            onView(withId(R.id.name)).check(matches(withText("Awesome Study Session")));
            assertEquals(focusModel.getSchedule("Awesome Study Session").getScheduleName(), "Awesome Study Session");
        }


        // 4.2. A schedule can include any of the existing profiles
        @Test
        public void checkShowProfiles() {

            FocusModel focusModel = FocusModel.getInstance();
            focusModel.createNewProfile("Test1");
            focusModel.createNewProfile("Test2");
            focusModel.createNewProfile("Test3");

            Intent i = new Intent();
            i.putExtra("scheduleName", "Awesome Study Session");
            mActivityRule.launchActivity(i);

            onView(withId(R.id.addProfileButton)).perform(click());
            onView(withText("Test1")).check(matches(isDisplayed()));
            onView(withText("Test2")).check(matches(isDisplayed()));
            onView(withText("Test3")).check(matches(isDisplayed()));
        }


        // 4.3. A Schedule should span a week


        // 4.7. A schedule should have an On/Off option to repeat a schedule weekly or not
        @Test
        public void checkRepeatSchedules() {

            FocusModel focusModel = FocusModel.getInstance();
            focusModel.createNewSchedule("Awesome Study Session");
            focusModel.getSchedule("Awesome Study Session").setRepeat(true);
            Intent i = new Intent();
            i.putExtra("scheduleName", "Awesome Study Session");
            mActivityRule.launchActivity(i);

            onView(withId(R.id.simple_switch)).check(matches(isDisplayed()));
            onView(withId(R.id.simple_switch)).check(matches(isChecked()));
            assertEquals((boolean)focusModel.getSchedule("Awesome Study Session").getRepeat(), true);

        }

        // 7.2 A user can edit the schedule name
        @Test
        public void checkEditScheduleName() {

            Intent i = new Intent();
            i.putExtra("scheduleName", "Awesome Study Session");
            mActivityRule.launchActivity(i);

            onView(withId(R.id.name)).perform(click());
            onView(withId(R.id.editScheduleNameId)).check(matches(isDisplayed()));
            onView(withId(R.id.editScheduleNameId)).perform(typeText("Changed Name"));
            onView(withText("Change")).inRoot(isDialog())
                    .check(matches(isDisplayed()))
                    .perform(click());

            // confirm name has been changed
            onView(allOf(withText("Changed Name"), withId(R.id.name))).check(matches(isDisplayed()));
        }

        // 7.3 A user can add profiles to certain days and/or times
        @Test
        public void checkAddProfilesToCertainDays() {

            FocusModel focusModel = FocusModel.getInstance();
            focusModel.createNewProfile("Test1");
            focusModel.createNewProfile("Test2");
            focusModel.createNewSchedule("Awesome Study Session");

            Intent i = new Intent();
            i.putExtra("scheduleName", "Awesome Study Session");
            mActivityRule.launchActivity(i);

            onView(withId(R.id.addProfileButton)).perform(click());

            // make sure checkboxes of profiles are listed
            onView(withText("Test1")).check(matches(isDisplayed()));
            onView(withText("Test2")).check(matches(isDisplayed()));
            onView(withText("Test1")).perform(click());
            onView(withText("Test2")).perform(click());

            onView(withText("Add")).perform(click());

            // make sure profile names now displayed in list of profiles for schedule
            onView(withText("Test1")).check(matches(isDisplayed()));
            onView(withText("Test2")).check(matches(isDisplayed()));

        }

        // 7.4 A user can remove profiles from certain days / times
        @Test
        public void checkRemoveProfilesFromCertainDays() {

            FocusModel focusModel = FocusModel.getInstance();
            focusModel.createNewProfile("Test1");
            focusModel.createNewProfile("Test2");

            focusModel.createNewSchedule("Awesome Study Session");
            focusModel.getSchedule("Awesome Study Session").addProfile(focusModel.getProfile("Test1"));
            focusModel.getSchedule("Awesome Study Session").addProfile(focusModel.getProfile("Test2"));

            Intent i = new Intent();
            i.putExtra("scheduleName", "Awesome Study Session");
            mActivityRule.launchActivity(i);

            onView(withText("Test1")).perform(click());

            onView(withText("Remove")).perform(click());

            // make sure profile name test1 now not displayed in list of profiles for schedule
            // and is not in list of profiles
            onView(withText("Test1")).check(doesNotExist());
        }

}
