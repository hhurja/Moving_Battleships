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
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AllOf.allOf;

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


    // 6.2. Users can edit an existing profile to change the name, add applications to the
    //blocked list or remove applications from the blocked list
    @Test
    public void ChangeProfileName() throws InterruptedException {

        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("A Profile");
        focusModel.setCurrentProfile("A Profile");

        Intent i = new Intent();
        i.putExtra("name", "A Profile");
        mActivityRule.launchActivity(i);

        // change profile name
        Espresso.onView(withId(R.id.name)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("Fun Profile"));
        Espresso.onView(withText("Change")).perform(click());
        // make sure name change worked
        Espresso.onView(withId(R.id.name)).check(matches(withText("Fun Profile")));




        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void CancelChangeProfileName() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Awesome Profile");
        focusModel.setCurrentProfile("Awesome Profile");

        Intent i = new Intent();
        i.putExtra("name", "Awesome Profile");
        mActivityRule.launchActivity(i);

        // don't change profile name
        Espresso.onView(withId(R.id.name)).perform(ViewActions.click());
        Espresso.onView(allOf(withClassName(containsString("EditText")))).perform(replaceText("FakeOut Profile"));
        Espresso.onView(withText("Cancel")).perform(click());
        // make sure name was not changed
        Espresso.onView(withId(R.id.name)).check(matches(withText("Awesome Profile")));
    }

    // 6.2. Users can edit an existing profile to change the name, add applications to the
    //blocked list or remove applications from the blocked list
    // 2.2. A profile should have a list of the apps to block
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

    // 2.3. A profile should have an On/Off option to make the profile active/passive
    @Test
    public void ProfileHasOnOffToggle() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Test Profile");
        focusModel.setCurrentProfile("Test Profile");

        Intent i = new Intent();
        i.putExtra("name", "Test Profile");
        mActivityRule.launchActivity(i);

        // start blocking apps
        Espresso.onView(withText("Start Blocking This Profile")).check(matches(isDisplayed())).perform(click());

        Espresso.onView(withHint("Minutes")).perform(replaceText("15"));
        onView(withText("Create")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        // stop blocking apps
        Espresso.onView(withText("Stop Blocking This Profile")).check(matches(isDisplayed())).perform(click());

    }

    // 2.4. Individual profile can be activated for a specific amount of time
    // 3.1. The system should have means to set the amount of time for the profile to be
    // active in minutes and hours.
    @Test
    public void ProfileActivatedSpecificTimeAmount() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Practice Profile");
        focusModel.setCurrentProfile("Practice Profile");

        Intent i = new Intent();
        i.putExtra("name", "Practice Profile");
        mActivityRule.launchActivity(i);

        // start blocking apps
        Espresso.onView(withText("Start Blocking This Profile")).check(matches(isDisplayed())).perform(click());

        Espresso.onView(withHint("Hours")).perform(replaceText("1"));
        Espresso.onView(withHint("Minutes")).perform(replaceText("15"));
        onView(withText("Create")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
    }

    // 3.2. Minimum amount of time allowed to set is 10 minutes
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

    // 3.3. Maximum amount of time allowed to set is 10 hours
    @Test
    public void BlockMoreThanTenHours() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Another Profile");
        focusModel.setCurrentProfile("Another Profile");

        Intent i = new Intent();
        i.putExtra("name", "Another Profile");
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

    // 2.7. It should be possible to deactivate an active profile at any moment(the system
    // should not have any restrictions for deactivating the profile)
    @Test
    public void DeactivateActiveProfile() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Active Profile");
        focusModel.setCurrentProfile("Active Profile");
        focusModel.getCurrentProfile().activate();


        Intent i = new Intent();
        i.putExtra("name", "Active Profile");
        mActivityRule.launchActivity(i);

        // stop blocking apps
        Espresso.onView(withText("Stop Blocking This Profile")).check(matches(isDisplayed())).perform(click());

        // make sure profile is inactive
        Espresso.onView(withText("Start Blocking This Profile")).check(matches(isDisplayed()));

    }

    /// 6.3. Users can delete a profile. This should delete it from all the schedules that have
    // that profile
    @Test
    public void DeleteProfile() throws InterruptedException {
        FocusModel focusModel = FocusModel.getInstance();
        focusModel.createNewProfile("Profile will be deleted :(");
        focusModel.setCurrentProfile("Profile will be deleted :(");


        Intent i = new Intent();
        i.putExtra("name", "Profile will be deleted :(");
        mActivityRule.launchActivity(i);

        // delete profile
        Espresso.onView(withId(R.id.deleteProfile)).check(matches(isDisplayed())).perform(click());
    }

}
