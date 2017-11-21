package Controller;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Model.FocusModel;
import Model.Profile;
import Model.Schedule;
import Model.TimeRange;
import movingbattleship.org.focus.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GoogleCalendarActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {

    private FocusModel focusModel;
    static GoogleAccountCredential mCredential;
    private ImageView mImageView;
    private TextView mOutputText;
    private Button mCallApiButton;
    private Button mCallApiImportButton;
    ProgressDialog mProgress;
    private String operation;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Sync with Google Calendar";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = CalendarScopes.all().toArray(new String[CalendarScopes.all().size()]);

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        focusModel = FocusModel.getInstance();
        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mImageView = new ImageView(this);
        mImageView.setMaxHeight(500);
        mImageView.setAdjustViewBounds(true);
        mImageView.setImageResource(R.drawable.google_calendar);
        activityLayout.addView(mImageView);

        operation = "IMPORT";
        mCallApiButton = new Button(this);
        mCallApiButton.setText("Export Focus! to Google Calendar");
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                operation = "EXPORT";
                getResultsFromApi();
                mCallApiButton.setEnabled(true);
            }
        });
        activityLayout.addView(mCallApiButton);

        mCallApiImportButton = new Button(this);
        mCallApiImportButton.setText("Import Google Calendar to Focus!");
        mCallApiImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiImportButton.setEnabled(false);
                mOutputText.setText("");
                operation = "IMPORT";
                getResultsFromApi();
                mCallApiImportButton.setEnabled(true);
            }
        });
        activityLayout.addView(mCallApiImportButton);

        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setMinHeight(20);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setText(
                "Click the \'" + BUTTON_TEXT +"\' button to Sync Focus! with Google Calendar!.");
        activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Connecting to Google Calendar ...");

        setContentView(activityLayout);

        ArrayList<String> scopes = new ArrayList(Arrays.asList(SCOPES));
        scopes.add("https://www.googleapis.com/auth/plus.login");
        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), scopes)
                .setBackOff(new ExponentialBackOff());
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            System.out.println("1");
            acquireGooglePlayServices();
            System.out.println("2");
        } else if (mCredential.getSelectedAccountName() == null) {
            System.out.println("3");
            chooseAccount();
            System.out.println("4");
        } else if (! isDeviceOnline()) {
            System.out.println("5");
            mOutputText.setText("No network connection available.");
        } else {
            System.out.println("6");
            if (operation.equals("IMPORT")) {
                System.out.println("import");
                new MakeGetRequestTask(mCredential).execute();
            } else if (operation.equals("EXPORT")) {
                System.out.println("export");
                for (Schedule s : focusModel.getAllSchedules()) {
                    for (TimeRange tr : s.getTimeRanges()) {
                        try {
                            System.out.println(s.getScheduleName() + " : " + tr.getProfiles() + " : " + tr.getDates());
                            insertToGoogleCalendarAsync(tr, s.getScheduleName(), tr.getProfiles());
                        } catch (Exception e) {
                            System.out.println("Exception: " + e);
                        }
                    }
                }
            }
            System.out.println("7");
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            System.out.println("8");
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                System.out.println("9");
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
                System.out.println("10");
            } else {
                System.out.println("11");
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            System.out.println("12");
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    new String[]{Manifest.permission.GET_ACCOUNTS});
            System.out.println("13");
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                GoogleCalendarActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    void insertToGoogleCalendarAsync(TimeRange tr, String sn, ArrayList<Profile> p) throws IOException {
        new MakeInsertRequestTask(mCredential, tr, sn, p).execute();
    }


    static void insertToGoogleCalendarStatic(TimeRange tr, String scheduleName, ArrayList<Profile> p) throws IOException {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        com.google.api.services.calendar.Calendar mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
        String summary = "Focus! Schedule: " + scheduleName;
        String location = "Wherever you be at my boi!";
        String des = p.toString();
        for (Date d : tr.getDates().keySet()) {
            DateTime startDate = new DateTime(d);
            Event event = new Event()
                    .setSummary(summary)
                    .setLocation(location)
                    .setDescription(des);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDate)
                    .setTimeZone("America/Los_Angeles");
            event.setStart(start);
            EventDateTime end = new EventDateTime()
                    .setDateTime(new DateTime(tr.getDates().get(d)))
                    .setTimeZone("America/Los_Angeles");
            event.setEnd(end);
            String[] recurrence = new String[]{"RRULE:FREQ=WEEKLY;UNTIL=20110701T170000Z"};
            event.setRecurrence(Arrays.asList(recurrence));
            EventReminder[] reminderOverrides = new EventReminder[]{
                    new EventReminder().setMethod("email").setMinutes(24 * 60),
                    new EventReminder().setMethod("popup").setMinutes(10),
            };
            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(Arrays.asList(reminderOverrides));
            event.setReminders(reminders);
            String calendarId = "primary";
            //event.send
            if (mService != null) {
                System.out.println("inserting into calendar");
                mService.events().insert(calendarId, event).setSendNotifications(true).execute();
            }

            //TODO Event recurringEvent = service.events().insert("primary", event).execute();
        }
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    public class MakeGetRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeGetRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));

                DateTime end = event.getEnd().getDateTime();
                if (end == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    end = event.getEnd().getDate();
                }

                //arraylist of days, int starthour, startminute, endhour, endminute
                Date startDate = new Date(start.getValue());
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(startDate);
                int day = cal1.get(Calendar.DAY_OF_WEEK);
                int startHour = cal1.get(Calendar.HOUR_OF_DAY);
                int startMinute = cal1.get(Calendar.MINUTE);
                Date endDate = new Date(end.getValue());
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(endDate);
                int endHour = cal2.get(Calendar.HOUR_OF_DAY);
                int endMinute = cal2.get(Calendar.MINUTE);
                System.out.println("Adding event: " + event.getSummary() + " : " + day);
                focusModel.addEvent(event.getSummary(), day, startHour, startMinute, endHour, endMinute);
            }
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            System.out.println("Post get request execute");
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                //output.add(0, "Data retrieved using the Google Calendar API:");
                //mOutputText.setText(TextUtils.join("\n", output));
                mOutputText.setText("Data retrieved using the Google Calendar API!\n Check your calendar view to see events");
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleCalendarActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }

    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    public class MakeInsertRequestTask extends AsyncTask<Void, Void, Boolean> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;
        private TimeRange timeRange;
        private String scheduleName;
        private ArrayList<Profile> profilesList;


        MakeInsertRequestTask(GoogleAccountCredential credential, TimeRange tr, String sn, ArrayList<Profile> p) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            timeRange = tr;
            scheduleName = sn;
            profilesList = p;
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                insertToGoogleCalendar(timeRange, scheduleName, profilesList);
                return true;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            mProgress.hide();
            if (false) {
                mOutputText.setText("Sync unsuccessful");
                mProgress.hide();
            } else {
                mOutputText.setText("Focus! schedule posted to your Google Calendar!");
                mProgress.hide();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleCalendarActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }

        public void insertToGoogleCalendar(TimeRange tr, String scheduleName, ArrayList<Profile> p) throws IOException {

            String summary = "Focus! Schedule: " + scheduleName;
            String location = "Wherever you be at my boi!";
            String des = "Blocked Profiles: ";
            for (int i = 0; i < p.size(); i++) {
                des.concat(p.get(i).getProfileName());
                if ( i < p.size()-1) {
                    des.concat(", ");
                }
            }

            for (Date d : tr.getDates().keySet()) {
                DateTime startDate = new DateTime(d);
                Event event = new Event()
                        .setSummary(summary)
                        .setLocation(location)
                        .setDescription(des);
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDate)
                        .setTimeZone("America/Los_Angeles");
                event.setStart(start);
                EventDateTime end = new EventDateTime()
                        .setDateTime(new DateTime(tr.getDates().get(d)))
                        .setTimeZone("America/Los_Angeles");
                event.setEnd(end);
                String[] recurrence = new String[]{"RRULE:FREQ=WEEKLY;UNTIL=20110701T170000Z"};
                event.setRecurrence(Arrays.asList(recurrence));
                EventReminder[] reminderOverrides = new EventReminder[]{
                        new EventReminder().setMethod("email").setMinutes(24 * 60),
                        new EventReminder().setMethod("popup").setMinutes(10),
                };
                Event.Reminders reminders = new Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(Arrays.asList(reminderOverrides));
                event.setReminders(reminders);
                String calendarId = "primary";
                //event.send
                if (mService != null) {
                    System.out.println("inserting into calendar : " + event);
                    mService.events().insert(calendarId, event).setSendNotifications(true).execute();
                }

                //TODO Event recurringEvent = service.events().insert("primary", event).execute();
            }
        }
    }
}