package Controller;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Model.FocusModel;
import Model.Profile;
import Model.Schedule;
import Model.TimeRange;
import movingbattleship.org.focus.R;

@TargetApi(23)
public class EditSchedule extends AppCompatActivity {

    private FocusModel focusModel;
    private Schedule schedule;
    private boolean isRepeat;
    private String scheduleName;
    private static View myView;
    private scheduleTimeRangesListAdapter listAdapter;
    private ListView editSchedulesListView;
    public static ArrayList<String> names = new ArrayList<String>();
    public com.google.api.services.calendar.Calendar mService = null;
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
    /** Application name. */
    private static final String APPLICATION_NAME =
            "Google Calendar API Focus!";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/calendar-java-focus!");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                EditSchedule.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = null;// TODO: new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
    getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedules);
        isRepeat = false;
        focusModel = FocusModel.getInstance();
        editSchedulesListView = (ListView) findViewById(R.id.profilesListView);
        // get the contact info from the contact activity so the user can see the selected contact info
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            scheduleName = extras.getString("scheduleName");
            System.out.println(scheduleName);
            schedule = focusModel.getSchedule(scheduleName);
            if ( schedule != null ) {
                isRepeat = schedule.getRepeat();
            }
        }

        String name = ( schedule != null ) ? schedule.getScheduleName() : "Awesome Study Session";

        TextView scheduleNameTextView = (TextView) findViewById(R.id.name);
        scheduleNameTextView.setText(name);

        //setDateAndTimeTable();

        Button addTimeRangeButton = (Button) findViewById(R.id.addTimeRangeButton);
        Switch repeatSwitch = (Switch) findViewById(R.id.simple_switch);
        repeatSwitch.setChecked(isRepeat);

        ListView lv = (ListView)findViewById(R.id.profilesListView);
        if ( schedule != null ) {
            listAdapter = new scheduleTimeRangesListAdapter(this, schedule.getTimeRanges());
            lv.setAdapter(listAdapter);
        }

        lv.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final View v = view;
                        final int pos = position;
                        final String name = String.valueOf(parent.getItemAtPosition(position));
                        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
                        AlertDialog.Builder builder = new AlertDialog.Builder(editSchedulesListView.getContext());
                        builder.setTitle("Add / Remove Profile To / from Schedule?");

                        // Set up the buttons
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                add((TimeRange)listAdapter.getItem(pos), v);
                            }
                        });
                        builder.setNeutralButton("Remove",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        remove((TimeRange)listAdapter.getItem(pos), v);
                                    }
                                });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                }
        );
        addTimeRangeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDays(editSchedulesListView, scheduleName);
                    }
                }
        );

        repeatSwitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: Turn schedule on or off
                        isRepeat = !isRepeat;
                        Switch repeatSwitch = (Switch) findViewById(R.id.simple_switch);
                        if (focusModel.getSchedule(scheduleName) != null ) {
                            focusModel.getSchedule(scheduleName).setRepeat(isRepeat);
                        }
                    }
                }
        );
    }

    public void remove ( TimeRange tr, View v ) {

        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Choose profiles to remove from time range");
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final ArrayList<CheckBox> profilesCheckBoxes = new ArrayList<>();
        final View view = v;
        final TimeRange timerange = tr;
        System.out.println(profilesCheckBoxes);
        for (Profile p : tr.getProfiles()) {
            if ( focusModel.getSchedule(scheduleName) != null && !focusModel.getSchedule(scheduleName).getProfiles().contains(p) ) {
                CheckBox cb = new CheckBox(v.getContext());
                cb.setText(p.getProfileName());
                profilesCheckBoxes.add(cb);
                layout.addView(cb);
            }
        }
        builder.setView(layout);
        // Set up the buttons
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> profiles = new ArrayList<String>();

                for (CheckBox cb : profilesCheckBoxes) {
                    if(cb.isChecked()) {
                        profiles.add(cb.getText().toString());
                    }
                }
                for (int i = 0; i < profiles.size(); i++) {
                    timerange.removeProfile(focusModel.getProfile(profiles.get(i)), schedule);

                }

                ListView lv = (ListView)findViewById(R.id.profilesListView);
                listAdapter.notifyDataSetChanged();
                lv.setAdapter(listAdapter);
                lv.refreshDrawableState();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public void add (TimeRange tr, View v) {
        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Choose profiles to add to schedule");
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final ArrayList<CheckBox> profilesCheckBoxes = new ArrayList<>();
        final View view = v;
        final TimeRange timerange = tr;
        System.out.println(profilesCheckBoxes);
        for (Profile p : focusModel.getAllProfiles()) {
            if ( focusModel.getSchedule(scheduleName) != null && !focusModel.getSchedule(scheduleName).getProfiles().contains(p) ) {
                if ( !tr.getProfiles().contains(p) ) {
                    CheckBox cb = new CheckBox(v.getContext());
                    cb.setText(p.getProfileName());
                    profilesCheckBoxes.add(cb);
                    layout.addView(cb);
                }
            }
        }
        builder.setView(layout);
        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> profiles = new ArrayList<String>();

                for (CheckBox cb : profilesCheckBoxes) {
                    if(cb.isChecked()) {
                        profiles.add(cb.getText().toString());
                    }
                }
                for (int i = 0; i < profiles.size(); i++) {
                    timerange.addProfile(focusModel.getProfile(profiles.get(i)));
                }
                ListView lv = (ListView)findViewById(R.id.profilesListView);
                listAdapter.notifyDataSetChanged();
                lv.setAdapter(listAdapter);
                lv.refreshDrawableState();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public void onClick(View v) {
        // CITED: https://stackoverflow.com/questions/10903754/input-text-dialog-android
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Edit Profile name");

        // Set up the input
        final EditText input = new EditText(v.getContext());
        input.setId(R.id.editScheduleNameId);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Add profile with that name to schedule
                if (focusModel.getSchedule(scheduleName) != null ) {
                    focusModel.getSchedule(scheduleName).setScheduleName(input.getText().toString());
                }
                scheduleName = input.getText().toString();
                TextView scheduleNameTextView = (TextView) findViewById(R.id.name);
                scheduleNameTextView.setText(scheduleName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private String getDaysString(ArrayList<Integer> days) {
        String daysString = "";

        System.out.println(days);

        for (int i = 0; i < days.size(); i++) {
            if(days.get(i) == 1) daysString += "Su";
            if(days.get(i) == 2) daysString += "Mo";
            if(days.get(i) == 3) daysString += "Tu";
            if(days.get(i) == 4) daysString += "We";
            if(days.get(i) == 5) daysString += "Th";
            if(days.get(i) == 6) daysString += "Fr";
            if(days.get(i) == 7) daysString += "Sa";

            if(i < days.size()-1){
                daysString += ", ";
            }
        }

        return daysString;
    }

    void getDays(View v, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Add Time Range");
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final View tempView = v;
        final String n = name;
        final TextView errorMessage = new TextView(v.getContext());
        errorMessage.setVisibility(View.GONE);
        errorMessage.setTextColor(Color.RED);
        errorMessage.setText("Choose time range days");

        final CheckBox m = new CheckBox(v.getContext());
        m.setText("Monday");
        m.setChecked(true);
        final CheckBox tu = new CheckBox(v.getContext());
        tu.setText("Tuesday");
        final CheckBox w = new CheckBox(v.getContext());
        w.setText("Wednesday");
        final CheckBox th = new CheckBox(v.getContext());
        th.setText("Thursday");
        final CheckBox f = new CheckBox(v.getContext());
        f.setText("Friday");
        final CheckBox sa = new CheckBox(v.getContext());
        sa.setText("Saturday");
        final CheckBox su = new CheckBox(v.getContext());
        su.setText("Sunday");

        layout.addView(errorMessage);
        layout.addView(m);
        layout.addView(tu);
        layout.addView(w);
        layout.addView(th);
        layout.addView(f);
        layout.addView(sa);
        layout.addView(su);

        builder.setView(layout);
        // Set up the buttons
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: set days ???
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                ArrayList<String> days = new ArrayList<String>();
                if(m.isChecked()) {
                    days.add("Monday");
                } if(tu.isChecked()) {
                days.add("Tuesday");
                } if(w.isChecked()) {
                days.add("Wednesday");
                } if(th.isChecked()) {
                days.add("Thursday");
                } if(f.isChecked()) {
                days.add("Friday");
                } if(sa.isChecked()) {
                days.add("Saturday");
                } if(su.isChecked()) {
                days.add("Sunday");
            }
                Boolean wantToCloseDialog = !days.isEmpty();
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog) {
                    System.out.println("DAYS: " + days);
                    getTimes(tempView, n, days);
                    dialog.dismiss();
                }
                else {
                    errorMessage.setVisibility(View.VISIBLE);
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            }
        });
    }

    void getTimes(View v, String n, ArrayList<String> d) {

        myView = v;
        final String name = n;
        final ArrayList<String> days = d;
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Set Times");

        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView errorMessage = new TextView(v.getContext());
        errorMessage.setText("Please enter a valid time range (Less than 10 hrs and Greater than 10 mins)");
        errorMessage.setVisibility(View.GONE);
        errorMessage.setTextColor(Color.RED);

        final TimePicker tpStart = new TimePicker(new ContextThemeWrapper(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        tpStart.setLayoutMode(1);
        tpStart.setScaleY((float) .8);

        TextView to = new TextView(v.getContext());
        to.setText("TO");
        to.setGravity(Gravity.CENTER);

        final TimePicker tpEnd = new TimePicker(new ContextThemeWrapper(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        tpEnd.setLayoutMode(2);
        tpEnd.setScaleY((float) .8);

        layout.addView(errorMessage);
        layout.addView(tpStart);
        layout.addView(to);
        layout.addView(tpEnd);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Create Schedule!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing?
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean wantToCloseDialog = true;
                //Do stuff, possibly set wantToCloseDialog to true then...

                if (tpStart.getHour()>tpEnd.getHour()){
                    if ((24-tpStart.getHour())+tpEnd.getHour() > 10) {
                        wantToCloseDialog = false;
                    }
                }
                else if(tpEnd.getHour()-tpStart.getHour() > 10) {
                    wantToCloseDialog = false;
                }
                else if (tpEnd.getHour()-tpStart.getHour() == 10) {
                    if (tpEnd.getMinute()-tpStart.getMinute() > 0) {
                        wantToCloseDialog = false;
                    }
                    else {
                        wantToCloseDialog = true;
                    }
                }
                else if (tpEnd.getHour()-tpStart.getHour() == 0) {
                    if (tpEnd.getMinute()-tpStart.getMinute() < 10) {
                        wantToCloseDialog = false;
                    }
                    else {
                        wantToCloseDialog = true;
                    }
                }
                else if (tpEnd.getHour()-tpStart.getHour() == 1) {
                    if (((60-tpStart.getMinute())+tpEnd.getMinute()) < 10) {
                        wantToCloseDialog = false;
                    }
                    else {
                        wantToCloseDialog = true;
                    }
                }

                if (wantToCloseDialog) {

                    TimeRange tr = new TimeRange(days, tpStart.getHour(), tpStart.getMinute(), tpEnd.getHour(), tpEnd.getMinute());
                    getProfiles(v, name, tr);

                    dialog.dismiss();
                } else {
                    errorMessage.setVisibility(View.VISIBLE);
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            }
        });
    }

    void getProfiles(View v, String n, TimeRange tr) {
        final String scheduleName = n;
        final TimeRange timerange = tr;
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Choose profiles to add to time range");
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final ArrayList<CheckBox> profilesCheckBoxes = new ArrayList<>();

        for (Profile p : focusModel.getAllProfiles()) {
            CheckBox cb = new CheckBox(v.getContext());
            cb.setText(p.getProfileName());
            profilesCheckBoxes.add(cb);
            layout.addView(cb);
        }
        builder.setView(layout);
        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Profile> profiles = new ArrayList<Profile>();

                for (CheckBox cb : profilesCheckBoxes) {
                    if(cb.isChecked()) {
                        if( !profiles.contains(focusModel.getProfile(cb.getText().toString()))) {
                            profiles.add(focusModel.getProfile(cb.getText().toString()));
                        }
                    }
                }
                focusModel.addTimeRangeToSchedule(timerange, scheduleName, profiles);
                try {
                    insertToGoogleCalendar(timerange, scheduleName, profiles);
                } catch (IOException e) {
                    System.out.println(e);
                }
                ((BaseAdapter)editSchedulesListView.getAdapter()).notifyDataSetChanged();
            }
        });

        builder.setNeutralButton("Add another time range",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        ArrayList<Profile> profiles = new ArrayList<Profile>();
                        for (CheckBox cb : profilesCheckBoxes) {
                            if(cb.isChecked()) {
                                if( !profiles.contains(focusModel.getProfile(cb.getText().toString()))) {
                                    profiles.add(focusModel.getProfile(cb.getText().toString()));
                                }
                            }
                        }
                        focusModel.addTimeRangeToSchedule(timerange, scheduleName, profiles);

                        getDays(editSchedulesListView, scheduleName);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    void insertToGoogleCalendar(TimeRange tr, String scheduleName, ArrayList<Profile> p) throws IOException {

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
        if (mService != null)
                mService.events().insert(calendarId, event).setSendNotifications(true).execute();
            //TODO Event recurringEvent = service.events().insert("primary", event).execute();
        }
    }
    /*
    void setDateAndTimeTable() {
        TableLayout daysAndTimesTable = (TableLayout) findViewById(R.id.datesAndTimesTableLayout);
        daysAndTimesTable.removeAllViews();
        if (schedule != null) {
            for (TimeRange t : schedule.getTimeRanges()) {
                System.out.println(schedule.getTimeRanges());
                String days = getDaysString(t.getDays());
                String times = t.getTime();
                System.out.println(days + " " + times);

                TextView daysTextView = new TextView(getApplicationContext());
                daysTextView.setText(days);
                daysTextView.setPadding(10, 10, 10, 10);
                TextView timesTextView = new TextView(getApplicationContext());
                timesTextView.setText(times);
                timesTextView.setPadding(10, 10, 10, 10);
                timesTextView.setGravity(Gravity.RIGHT);
                timesTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                TableRow dtRow = new TableRow(getApplicationContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                dtRow.setLayoutParams(lp);
                dtRow.addView(daysTextView);
                dtRow.addView(timesTextView);
                daysAndTimesTable.addView(dtRow);
            }
        }
        daysAndTimesTable.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                focusModel.getSchedule(scheduleName).clearTimeRanges();
                getDays(v, scheduleName);
                setDateAndTimeTable();
            }
        });
    } */
}

