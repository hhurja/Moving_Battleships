package movingbattleship.org.focus.profilesTests;


import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import Controller.EditProfile;
import Model.FocusModel;
import movingbattleship.org.focus.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.hamcrest.Matchers.anything;

/**
 * Created by aaronrschrock on 10/27/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditProfileTest {

    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<EditProfile> mActivityRule =
            new ActivityTestRule<>(EditProfile.class);

    @Before
    public void init(){
        mActivityRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }


    @Test
    public void ChangeProfileName() throws InterruptedException {

        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Sample Profile");
        focusModel.setCurrentProfile("Sample Profile");

        Intent i = new Intent();
        i.putExtra("name", "Sample Profile");
        mActivityRule.launchActivity(i);

        // change profile name
        Espresso.onView(withId(R.id.name)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Fun Profile"));
        Espresso.onView(withText("Change")).perform(click());
        // make sure name change worked
        Espresso.onView(withId(R.id.name)).check(matches(withText("Fun Profile")));



        /*
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } */
    }

    @Test
    public void CancelChangeProfileName() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Sample Profile");
        focusModel.setCurrentProfile("Sample Profile");

        Intent i = new Intent();
        i.putExtra("name", "Sample Profile");
        mActivityRule.launchActivity(i);

        // don't change profile name
        Espresso.onView(withId(R.id.name)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("FakeOut Profile"));
        Espresso.onView(withText("Cancel")).perform(click());
        // make sure name was not changed
        Espresso.onView(withId(R.id.name)).check(matches(withText("Sample Profile")));
    }

    @Test
    public void AddApplicationToProfile() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Sample Profile");
        focusModel.setCurrentProfile("Sample Profile");

        Intent i = new Intent();
        i.putExtra("name", "Sample Profile");
        mActivityRule.launchActivity(i);

        // add applications
        Espresso.onView(withId(R.id.fab_plus)).perform(click());
        Espresso.onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(0)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        Espresso.onData(anything()).inAdapterView(withId(R.id.InstalledAppsListView)).atPosition(1)
                .onChildView(withId(R.id.checkBox3)).perform(click());
        Espresso.onView(withId(R.id.add_applications)).perform(click());
        onView(withText("YouTube")).check(matches(isDisplayed()));

        // remove an application
        Espresso.onData(anything()).inAdapterView(withId(R.id.AppListView)).atPosition(1).perform(click());
        Espresso.onView(withText("Cancel")).perform(click());
        Espresso.onData(anything()).inAdapterView(withId(R.id.AppListView)).atPosition(0).perform(click());
        Espresso.onView(withText("Remove")).perform(click());

    }

    @Test
    public void BlockLessThanTenMinutes() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Sample Profile");
        focusModel.setCurrentProfile("Sample Profile");

        Intent i = new Intent();
        i.putExtra("name", "Sample Profile");
        mActivityRule.launchActivity(i);

        // start blocking apps
        Espresso.onView(withId(R.id.startBlocking)).perform(click());

        // set time less than 10 minutes
        Espresso.onView(withHint("Minutes")).perform(replaceText("8"));
        onView(withText("Create")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Okay")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void BlockMoreThanTenHours() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Sample Profile");
        focusModel.setCurrentProfile("Sample Profile");

        Intent i = new Intent();
        i.putExtra("name", "Sample Profile");
        mActivityRule.launchActivity(i);

        // start blocking apps
        Espresso.onView(withId(R.id.startBlocking)).perform(click());

        // set time less than 10 minutes
        Espresso.onView(withHint("Hours")).perform(replaceText("12"));
        onView(withText("Create")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Okay")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
    }

}
