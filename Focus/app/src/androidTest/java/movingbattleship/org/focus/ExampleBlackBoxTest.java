package movingbattleship.org.focus;



import static org.hamcrest.core.AllOf.allOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.matchers.JUnitMatchers.*;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.core.deps.guava.base.Strings;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.filters.LargeTest;
import static android.support.test.espresso.Espresso.onView;

import Controller.MainActivity;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.Espresso;

/**
 * Created by aaronrschrock on 10/27/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleBlackBoxTest {

    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void initValidString() {
        //Do Nothing for this test
    }

    @Test
    public void changeText_sameActivity() throws InterruptedException {
        //Click on add profile button
        Espresso.onView(withId(R.id.addProfileButton)).perform(click());
        //pause
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void addSchedule() throws InterruptedException {
        //Click on add profile button
        Espresso.onView(withText("Schedules")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.addScheduleButton)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Another test"));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

