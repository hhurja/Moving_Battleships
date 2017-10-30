package movingbattleship.org.focus.schedulesTests;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
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
public class addSchedulesTest {
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
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Distracting Videos"));
        Espresso.onView(withText("Create")).perform(click());
        Espresso.onView(withText("Distracting Videos")).perform(click());
        Espresso.onView(withText("Add Application")).perform(click());
        onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(0)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        Espresso.onView(withText("Add Applications")).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());

    }
    @Test
    public void test2_createSchedule() {
        onView(withText("Schedules")).perform(click());
        onView(withId(R.id.addScheduleButton))
                .perform(click());
        pause();
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Schedule"));
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
        pause();
        pause();
        pause();
    }
    @Test
    public void test3_createAnotherProfile() {
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
    }
    @Test
    public void test4_createAnotherSchedule() {
        onView(withText("Schedules")).perform(click());
        onView(withId(R.id.addScheduleButton))
                .perform(click());
        pause();
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Another schedule"));
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Next")).perform(click());
        pause();
        Espresso.onView(withText("Distracting Wallpaper")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        Espresso.onView(withText("Tuesday")).perform(click());
        Espresso.onView(withText("Next")).perform(click());
        onView(withIndex(withClassName(containsString("TimePicker")), 0)).perform(setTime(15, 36));
        onView(withIndex(withClassName(containsString("TimePicker")), 1)).perform(setTime(19, 36));
        pause();
        Espresso.onView(withText("Create Schedule!")).perform(click());
        pause();
        pause();
        pause();
    }

    public void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
