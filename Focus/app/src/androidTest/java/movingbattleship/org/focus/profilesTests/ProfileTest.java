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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 * Created by aaronrschrock on 10/27/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileTest {
    /*
Add/Edit/Delete Profile


6.3. Users can delete a profile. This should delete it from all the schedules that have
that profile


2.5. When a profile is active any notification from an application from the blocked list
should not be visible/audible
2.6. When a profile is active user should be restricted from interacting with any
application from the blocked list
*/

    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void init(){
        mActivityRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }

    // 6.1. Users can create new profiles
    @Test
    public void ensureCreateProfileWorks() {

        //Click on add profile button
        Espresso.onView(withText("Profiles")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.addProfileButton)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Sample Profile"));
        Espresso.onView(withText("Create")).perform(click());

        // make sure name matches up
        Espresso.onData(anything()).inAdapterView(withId(R.id.profilesListView)).atPosition(0)
                .onChildView(withId(R.id.profileName)).check(matches(withText("Sample Profile")));

        Espresso.onData(anything()).inAdapterView(withId(R.id.profilesListView)).atPosition(0).perform(click());
    }

    // 2.1 A profile should have a name
    @Test
    public void CheckProfileHasName() {

        //Click on add profile button
        Espresso.onView(withText("Profiles")).perform(ViewActions.click());
        Espresso.onView(withId(R.id.addProfileButton)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Sample Profile"));
        Espresso.onView(withText("Create")).perform(click());

        // make sure name matches up
        Espresso.onData(anything()).inAdapterView(withId(R.id.profilesListView)).atPosition(0)
                .onChildView(withId(R.id.profileName)).check(matches(withText("Sample Profile")));

    }


}
