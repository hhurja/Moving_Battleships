package movingbattleship.org.focus.profilesTests;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import Controller.MainActivity;
import movingbattleship.org.focus.R;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 * Created by aaronrschrock on 10/27/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileTest {

    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void initValidString() {
        //Do Nothing for this test
    }


    @Test
    public void addProfile() throws InterruptedException {
        //Click on add profile button
        Espresso.onView(withText("Profiles")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.addProfileButton)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Sample Profile"));
        Espresso.onView(withText("Create")).perform(click());
        Espresso.onData(anything()).inAdapterView(withId(R.id.profilesListView)).atPosition(0).perform(click());
        // change profile name
        Espresso.onView(withId(R.id.name)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Fun Profile"));
        Espresso.onView(withText("Change")).perform(click());
        // don't change profile name
        Espresso.onView(withId(R.id.name)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("FakeOut Profile"));
        Espresso.onView(withText("Cancel")).perform(click());
        // add applications
        Espresso.onView(withId(R.id.fab_plus)).perform(click());
        Espresso.onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(0)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        Espresso.onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(20)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        Espresso.onView(withId(R.id.add_applications)).perform(click());
        // remove an application
        Espresso.onData(anything()).inAdapterView(withId(R.id.AppListView)).atPosition(0).perform(click());
        Espresso.onView(withText("Cancel")).perform(click());
        Espresso.onData(anything()).inAdapterView(withId(R.id.AppListView)).atPosition(1).perform(click());
        Espresso.onView(withText("Remove")).perform(click());

        // start blocking apps
        Espresso.onView(withId(R.id.startBlocking)).perform(click());

        // set time less than 10 minutes
        Espresso.onView(withHint("Minutes")).perform(replaceText("8"));
        Espresso.onView(withText("Create")).perform(click());

        // set time greater than 10 hours

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
