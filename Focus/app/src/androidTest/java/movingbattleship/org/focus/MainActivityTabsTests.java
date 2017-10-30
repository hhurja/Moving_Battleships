package movingbattleship.org.focus;

/**
 * Created by adammoffitt on 10/28/17.
 */

import android.support.annotation.UiThread;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import Controller.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNull.notNullValue;


@RunWith(AndroidJUnit4.class)
public class MainActivityTabsTests {
    private MainActivity mActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void swipePage() {
        onView(withId(R.id.container))
                .check(matches(isDisplayed()));

        onView(withId(R.id.container))
                .perform(swipeLeft());
    }

    @Test
    public void checkTabLayoutDisplayed() {
        onView(withId(R.id.tabs))
                .perform(click())
                .check(matches(isDisplayed()));
    }

    @Test
    @UiThread
    public void checkTabSwitch() {
        // I'd like to switch to a tab (schedules) and check that the switch happened
        onView(allOf(withText("Schedules"), isDescendantOfA(withId(R.id.tabs))))
                .perform(click())
                .check(matches(isDisplayed()));
    }
}