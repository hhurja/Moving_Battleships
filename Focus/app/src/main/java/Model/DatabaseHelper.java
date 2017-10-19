package Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HunterHurja on 10/17/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;

    private static final String ID_TABLE_NAME = "id_table";
    private static final String idCol0 = "ID";
    private static final String idCol1 = "numProfilesCreated";
    private static final String idCol2 = "numAppsCreated";
    private static final String idCol3 = "numSchedulesCreated";

    private static final String PROFILES_TABLE_NAME = "prof_table";
    private static final String profCol0 = "PROFILE_ID";
    private static final String profCol1 = "PROFILE_NAME";
    private static final String profCol2 = "ON_OFF_SWITCH";
    private static final String profCol3 = "ACTIVATED";

    private static final String APPS_TABLE_NAME = "app_table";
    private static final String appCol0 = "APP_ID";
    private static final String appCol1 = "APP_NAME";
    private static final String appCol2 = "BLOCKED";
    private static final String appCol3 = "PACKAGE_NAME";

    private static final String SCHEDULES_TABLE_NAME = "schedule_table";
    private static final String schedCol0 = "SCHEDULE_ID";
    private static final String schedCol1 = "SCHEDULE_NAME";
    private static final String schedCol2 = "BLOCKED";
    private static final String schedCol3 = "INVISIBLE";
    private static final String schedCol4 = "REPEAT";

    private static final List<String> table_names = new ArrayList<>();



    public DatabaseHelper(Context context){
        super(context, ID_TABLE_NAME, null, 1);
        table_names.add(ID_TABLE_NAME);
        table_names.add(APPS_TABLE_NAME);
        table_names.add(SCHEDULES_TABLE_NAME);
        table_names.add(PROFILES_TABLE_NAME);

    }

    @Override
    public void onCreate(SQLiteDatabase db){
        System.out.println("Creating table");
        String createTable = "CREATE TABLE IF NOT EXISTS " + ID_TABLE_NAME  + " (" +
                idCol0 + " INTEGER PRIMARY KEY, "+ idCol1 + " INTEGER, " + idCol2 + " INTEGER, " + idCol3 + " INTEGER)";
        System.out.println(createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int j){
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ID_TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public boolean addDataToIdTable(String c1, String c2, String c3){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(idCol1, c1);
        cv.put(idCol2, c2);
        cv.put(idCol3, c3);


        System.out.println("Adding item: " + c1+" "+c2+" "+c3 + " to table: " + ID_TABLE_NAME);

        long result = db.insert(ID_TABLE_NAME, null, cv);

        if(result == -1) return false;

        return true;

    }

    public void deleteAllTables(SQLiteDatabase db){
        for(String table_name: table_names){
            db.execSQL("DROP TABLE IF EXISTS " + ID_TABLE_NAME);
        }
    }

    public boolean writeAllData(FocusModel fm){
        SQLiteDatabase db = this.getWritableDatabase();
        deleteAllTables(db);
        createTables(db);

        boolean idQuery = makeIdQuery(db, fm.getNumProfilesCreated(), fm.getNumAppsCreated(), fm.getNumSchedulesCreated());
        boolean profQuery = makeProfileQuery(db, fm.getAllProfiles());
        boolean appQuery = makeAppQuery(db, fm.getAllApps());
        boolean scheduleQuery = makeScheduleQuery(db, fm.getAllSchedules());

        if(!idQuery){ System.out.println("IdQUERY returned false"); }
        if(!profQuery){ System.out.println("profQUERY returned false"); }
        if(!appQuery){ System.out.println("appQUERY returned false"); }
        if(!scheduleQuery){ System.out.println("ScheduleQuery returned false"); }
        return true;
    }

    public boolean makeScheduleQuery(SQLiteDatabase db, ArrayList<Schedule> schedules){
        boolean correct = true;
        for(Schedule s: schedules) {
            int blocked = 0;
            int repeat = 0;
            int invisible = 0;
            if(s.blocked) blocked = 1;
            if(s.repeat) repeat = 1;
            if(s.invisible) invisible = 1;

            ContentValues cv = new ContentValues();
            cv.put(schedCol0, Integer.toString(s.getScheduleID()));
            cv.put(schedCol1, s.getScheduleName());
            cv.put(schedCol2, Integer.toString(blocked));
            cv.put(schedCol3, Integer.toString(invisible));
            cv.put(schedCol4, Integer.toString(repeat));
            long result = db.insert(PROFILES_TABLE_NAME, null, cv);
            if ( result == -1 ) correct = false;
        }

        return correct;
    }

    public boolean makeAppQuery(SQLiteDatabase db, ArrayList<App> apps){
        boolean correct = true;
        for(App a: apps) {
            int blocked = 0;
            if(a.isBlocked()) blocked = 1;

            ContentValues cv = new ContentValues();
            cv.put(appCol0, Integer.toString(a.getAppID()));
            cv.put(appCol1, a.getAppName());
            cv.put(appCol2, Integer.toString(blocked));
            cv.put(appCol3, a.getPackageName());
            long result = db.insert(PROFILES_TABLE_NAME, null, cv);
            if ( result == -1 ) correct = false;
        }
        return correct;
    }

    public boolean makeIdQuery(SQLiteDatabase db, int c1, int c2, int c3){
        ContentValues cv = new ContentValues();
        cv.put(idCol1, Integer.toString(c1));
        cv.put(idCol2, Integer.toString(c2));
        cv.put(idCol3, Integer.toString(c3));



        long result = db.insert(ID_TABLE_NAME, null, cv);
        if(result == -1) return false;
        return true;
    }

    public void createTables(SQLiteDatabase db){
        String createTablesQuery = "";
        ArrayList<String> queries = new ArrayList<>();

        queries.add("CREATE TABLE IF NOT EXISTS " + ID_TABLE_NAME  + " (" +
                idCol0 + " INTEGER PRIMARY KEY, "+ idCol1 + " INTEGER, " + idCol2 + " INTEGER, " + idCol3 + " INTEGER)");

        queries.add("CREATE TABLE IF NOT EXISTS " + PROFILES_TABLE_NAME  + " (" +
                profCol0 + " INTEGER PRIMARY KEY, "+ profCol1 + " TEXT, " + profCol2 + " INTEGER, " + profCol3 + " INTEGER)");

        queries.add("CREATE TABLE IF NOT EXISTS " + SCHEDULES_TABLE_NAME  + " (" +
                schedCol0 + " INTEGER PRIMARY KEY, "+ schedCol1 + " TEXT, " + schedCol2 + " INTEGER, " +
                schedCol3 + " INTEGER, " + schedCol4 + " INTEGER)");

        queries.add("CREATE TABLE IF NOT EXISTS " + APPS_TABLE_NAME  + " (" +
                appCol0 + " INTEGER PRIMARY KEY, "+ appCol1 + " TEXT, " + appCol2 + " INTEGER, " + appCol3 + " TEXT)");


        for(String q: queries){
            db.execSQL(q);
        }
    }

    public boolean makeProfileQuery(SQLiteDatabase db, ArrayList<Profile> profiles){
        boolean correct = true;
        for(Profile p: profiles) {
            int on_off = 0;
            int activated = 0;
            if(p.isOn()) on_off = 1;
            if(p.isActivated()) activated = 1;

            ContentValues cv = new ContentValues();
            cv.put(profCol0, Integer.toString(p.getProfileID()));
            cv.put(profCol1, p.getProfileName());
            cv.put(profCol2, Integer.toString(on_off));
            cv.put(profCol3, Integer.toString(activated));
            long result = db.insert(PROFILES_TABLE_NAME, null, cv);
            if ( result == -1 ) correct = false;
        }

        return correct;
    }

    public String getTableAsString(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }


}
