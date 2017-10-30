package movingbattleship.org.focus;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static org.hamcrest.Matchers.anything;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import Controller.MainActivity;
import movingbattleship.org.focus.R;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static org.junit.matchers.JUnitMatchers.*;
import static android.support.test.espresso.contrib.PickerActions.setTime;


import static org.hamcrest.Matchers.allOf;

/**
 * Created by shabina on 10/27/17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class profilesAndScheduleCreation {
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


    @Test
    public void test1_createProfile() {
        Espresso.onView(withText("Profiles")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.addProfileButton)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText(""));
        Espresso.onView(withText("Create")).perform(click());
        Espresso.onView(withId(R.id.addProfileButton)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Distracting Videos"));
        Espresso.onView(withText("Create")).perform(click());
        Espresso.onView(withText("Distracting Videos")).perform(click());
        Espresso.onView(withText("Add Application")).perform(click());
        onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(0)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        Espresso.onView(withText("Add Applications")).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        Espresso.onView(withId(R.id.profileName)).check(matches(withText("Distracting Videos")));

    }
    @Test
    public void test2_createScheduleWithOneProfile() {
        onView(withText("Schedules")).perform(click());
        onView(withId(R.id.addScheduleButton)).perform(click());
        pause();
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText(""));
        Espresso.onView(withText("Next")).perform(click());
        onView(withId(R.id.addScheduleButton)).perform(click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Schedule w/ 1 Profile"));
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Distracting Videos")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        Espresso.onView(withText("Tuesday")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        onView(withIndex(withClassName(containsString("TimePicker")), 0)).perform(setTime(9, 36));
        onView(withIndex(withClassName(containsString("TimePicker")), 1)).perform(setTime(11, 36));
        pause();
        Espresso.onView(withText("Create Schedule!")).perform(click());
        Espresso.onView(withId(R.id.name)).check(matches(withText("Schedule w/ 1 Profile")));
        pause();
        pause();
        pause();
    }
    @Test
    public void test3_createScheduleWithMultipleProfiles() {
        Espresso.onView(withText("Profiles")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.addProfileButton)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Distracting Wallpaper"));
        Espresso.onView(withText("Create")).perform(click());
        Espresso.onView(withText("Distracting Wallpaper")).perform(click());
        pause();
        Espresso.onView(withText("Add Application")).perform(click());
        onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(2)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        pause();
        Espresso.onView(withText("Add Applications")).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        Espresso.onView(withIndex(withId(R.id.profileName),1)).check(matches(withText("Distracting Wallpaper")));

        onView(withText("Schedules")).perform(click());
        onView(withId(R.id.addScheduleButton))
                .perform(click());
        pause();
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Schedule w/ 2 Profiles"));
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Distracting Videos")).perform(click());
        Espresso.onView(withText("Distracting Wallpaper")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        Espresso.onView(withText("Wednesday")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        onView(withIndex(withClassName(containsString("TimePicker")), 0)).perform(setTime(15, 36));
        onView(withIndex(withClassName(containsString("TimePicker")), 1)).perform(setTime(19, 36));
        pause();
        Espresso.onView(withText("Create Schedule!")).perform(click());
        Espresso.onView(withIndex(withId(R.id.name),1)).check(matches(withText("Schedule w/ 2 Profiles")));
        pause();
        pause();
    }
    @Test
    public void test4_createProfileWithMultipleApplications() {
        Espresso.onView(withText("Profiles")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.addProfileButton)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Random apps"));
        Espresso.onView(withText("Create")).perform(click());
        Espresso.onView(withText("Random apps")).perform(click());
        Espresso.onView(withText("Add Application")).perform(click());
        onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(3)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(4)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(5)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        Espresso.onView(withText("Add Applications")).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        Espresso.onView(withIndex(withId(R.id.profileName),2)).check(matches(withText("Random apps")));


    }
    @Test
    public void test5_createScheduleWithIncorrectTimeRange() {
        onView(withText("Schedules")).perform(click());

        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Random Schedule!"));
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Random apps")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        Espresso.onView(withText("Thursday")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        pause();
        onView(withIndex(withClassName(containsString("TimePicker")), 0)).perform(setTime(00, 00));
        onView(withIndex(withClassName(containsString("TimePicker")), 1)).perform(setTime(11, 00));
        pause();
        Espresso.onView(withText("Create Schedule!")).perform(click());
        pause();
        onView(withIndex(withClassName(containsString("TimePicker")), 0)).perform(setTime(00, 00));
        onView(withIndex(withClassName(containsString("TimePicker")), 1)).perform(setTime(9, 00));
        pause();
        Espresso.onView(withText("Create Schedule!")).perform(click());
        pause();
        Espresso.onView(withIndex(withId(R.id.name),2)).check(matches(withText("Random Schedule!")));
    }
    @Test
    public void test6_editScheduleWithNewTimeRange() {
        onView(withText("Schedules")).perform(click());
        Espresso.onView(withText("Random Schedule!")).perform(click());
        pause();
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Random Schedule!"));
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Random apps")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        Espresso.onView(withText("Thursday")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        pause();
        onView(withIndex(withClassName(containsString("TimePicker")), 0)).perform(setTime(00, 00));
        onView(withIndex(withClassName(containsString("TimePicker")), 1)).perform(setTime(11, 00));
        pause();
        Espresso.onView(withText("Create Schedule!")).perform(click());
        pause();
        onView(withIndex(withClassName(containsString("TimePicker")), 0)).perform(setTime(00, 00));
        onView(withIndex(withClassName(containsString("TimePicker")), 1)).perform(setTime(9, 00));
        pause();
        Espresso.onView(withText("Create Schedule!")).perform(click());
        pause();
        Espresso.onView(withIndex(withId(R.id.name),2)).check(matches(withText("Random Schedule!")));
    }

    public void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
