package Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

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

    private static final String TIMERANGE_TABLE_NAME = "timerange_table";
    private static final String trCol0 = "START_HOUR";
    private static final String trCol1 = "START_MINUTE";
    private static final String trCol2 = "END_HOUR";
    private static final String trCol3 = "END_MINUTE";
    private static final String trCol4 = "REPEAT";
    private static final String trCol5 = "SUNDAY";
    private static final String trCol6 = "MONDAY";
    private static final String trCol7 = "TUESDAY";
    private static final String trCol8 = "WEDNESDAY";
    private static final String trCol9 = "THURSDAY";
    private static final String trCol10 = "FRIDAY";
    private static final String trCol11 = "SATURDAY";
    private static final String trCol12 = "FK_SCHEDULE_ID";

    private static final String A2P_TABLE_NAME = "a2p_table";
    private static final String a2pCol0 = "APP_ID";
    private static final String a2pCol1 = "PROFILE_ID";

    private static final String P2S_TABLE_NAME = "p2s_table";
    private static final String p2sCol0 = "PROFILE_ID";
    private static final String p2sCol1 = "SCHEDULE_ID";

    private static final String BP2A_TABLE_NAME = "BP2A_table";
    private static final String bp2aCol0 = "PROFILE_ID";
    private static final String bp2aCol1 = "APP_ID";


    private static final List<String> table_names = new ArrayList<>();

    public DatabaseHelper(Context context){
        super(context, ID_TABLE_NAME, null, 1);
        table_names.add(ID_TABLE_NAME);
        table_names.add(APPS_TABLE_NAME);
        table_names.add(SCHEDULES_TABLE_NAME);
        table_names.add(PROFILES_TABLE_NAME);
        table_names.add(TIMERANGE_TABLE_NAME);
        table_names.add(A2P_TABLE_NAME);
        table_names.add(P2S_TABLE_NAME);
        table_names.add(BP2A_TABLE_NAME);

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
//        System.out.println("DELETING TABLES");
//        for(String table_name: table_names){
//            System.out.println("DELETING TABLE: "+ table_name);
//            db.execSQL("DROP TABLE IF EXISTS " + ID_TABLE_NAME);
//        }
        // query to obtain the names of all tables in your database
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

// iterate over the result set, adding every table name to a list
        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }

// call DROP TABLE on every table name
        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            db.execSQL(dropQuery);
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
        boolean timerangeQuery = makeTimerangeQuery(db, fm.getAllSchedules());
        boolean a2pQuery = makeA2PQuery(db, fm.getAllProfiles());
        boolean p2sQuery = makeP2SQuery(db, fm.getAllSchedules());
        boolean bp2aQuery = makeBP2AQuery(db, fm.getAllApps());

        if(!idQuery){ System.out.println("IdQUERY returned false"); }
        if(!profQuery){ System.out.println("profQUERY returned false"); }
        if(!appQuery){ System.out.println("appQUERY returned false"); }
        if(!scheduleQuery){ System.out.println("ScheduleQuery returned false"); }
        if(!timerangeQuery){ System.out.println("timeRangeQUERY returned false"); }
        if(!a2pQuery){ System.out.println("a2pQUERY returned false"); }
        if(!p2sQuery){ System.out.println("p2sQUERY returned false"); }
        if(!bp2aQuery){ System.out.println("bp2aQUERY returned false"); }
        return true;
    }

    public boolean makeTimerangeQuery(SQLiteDatabase db, ArrayList<Schedule> schedules){
        boolean correct = true;
        for(Schedule s: schedules) {
            for(TimeRange tr: s.getTimeRanges()) {
                int repeat = 0;
                int sunday = 0;
                int monday = 0;
                int tuesday = 0;
                int wednesday = 0;
                int thursday = 0;
                int friday = 0;
                int saturday = 0;
                if(tr.isRepeating()) repeat = 1;
                for(int day: tr.getDays()){
                    if(day == 1) sunday = 1;
                    if(day == 2) monday = 1;
                    if(day == 3) tuesday = 1;
                    if(day == 4) wednesday = 1;
                    if(day == 5) thursday = 1;
                    if(day == 6) friday = 1;
                    if(day == 7) saturday = 1;
                }

                ContentValues cv = new ContentValues();

                cv.put(trCol0, Integer.toString(tr.getStartHour()));
                cv.put(trCol1, Integer.toString(tr.getStartMinute()));
                cv.put(trCol2, Integer.toString(tr.getEndHour()));
                cv.put(trCol3, Integer.toString(tr.getEndMinute()));

                cv.put(trCol4, Integer.toString(repeat));
                cv.put(trCol5, Integer.toString(sunday));
                cv.put(trCol6, Integer.toString(monday));
                cv.put(trCol7, Integer.toString(tuesday));
                cv.put(trCol8, Integer.toString(wednesday));
                cv.put(trCol9, Integer.toString(thursday));
                cv.put(trCol10, Integer.toString(friday));
                cv.put(trCol11, Integer.toString(saturday));
                cv.put(trCol12, Integer.toString(s.getScheduleID()));


                long result = db.insert(TIMERANGE_TABLE_NAME, null, cv);
                if (result == -1) correct = false;
            }
        }
        return correct;
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
            long result = db.insert(SCHEDULES_TABLE_NAME, null, cv);
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
            long result = db.insert(APPS_TABLE_NAME, null, cv);
            if ( result == -1 ) correct = false;
        }
        return correct;
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

    public boolean makeIdQuery(SQLiteDatabase db, int c1, int c2, int c3){
        ContentValues cv = new ContentValues();
        cv.put(idCol1, Integer.toString(c1));
        cv.put(idCol2, Integer.toString(c2));
        cv.put(idCol3, Integer.toString(c3));



        long result = db.insert(ID_TABLE_NAME, null, cv);
        if(result == -1) return false;
        return true;
    }

    public boolean makeA2PQuery(SQLiteDatabase db, ArrayList<Profile> profiles){
        boolean correct = true;
        for(Profile p: profiles) {
            int profID = p.getProfileID();
            for(App a: p.getApps()){
                ContentValues cv = new ContentValues();
//                System.out.println("APPID = "+ a.getAppID() + " PROFILEID = " + profID);
                cv.put(a2pCol0, Integer.toString(a.getAppID()));
                cv.put(a2pCol1, Integer.toString(profID));
                long result = db.insert(A2P_TABLE_NAME, null, cv);
                if ( result == -1 ) correct = false;
            }
        }

        return correct;
    }

    public boolean makeP2SQuery(SQLiteDatabase db, ArrayList<Schedule> schedules){
        boolean correct = true;
        for(Schedule s: schedules) {
            int schedID = s.getScheduleID();
            for(Profile p: s.getProfiles()){
                ContentValues cv = new ContentValues();
//                System.out.println("APPID = "+ a.getAppID() + " PROFILEID = " + profID);
                cv.put(p2sCol0, Integer.toString(p.getProfileID()));
                cv.put(a2pCol1, Integer.toString(schedID));
                long result = db.insert(P2S_TABLE_NAME, null, cv);
                if ( result == -1 ) correct = false;
            }
        }

        return correct;
    }

    public boolean makeBP2AQuery(SQLiteDatabase db, ArrayList<App> apps){
        boolean correct = true;
        for(App a: apps) {
            int appID = a.getAppID();
            for(Integer pID: a.getBlockedProfileIDs()){
                ContentValues cv = new ContentValues();
//                System.out.println("APPID = "+ a.getAppID() + " PROFILEID = " + profID);
                cv.put(p2sCol0, Integer.toString(pID));
                cv.put(a2pCol1, Integer.toString(appID));
                long result = db.insert(P2S_TABLE_NAME, null, cv);
                if ( result == -1 ) correct = false;
            }
        }

        return correct;
    }

    public void createTables(SQLiteDatabase db){
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

        queries.add("CREATE TABLE IF NOT EXISTS " + TIMERANGE_TABLE_NAME + " (" + trCol0 + " INTEGER, "+
                trCol1 + " INTEGER, " + trCol2 + " INTEGER, " + trCol3 + " INTEGER, " + trCol4 + " INTEGER, " +
                trCol5 + " INTEGER, " + trCol6 + " INTEGER, " + trCol7 + " INTEGER, " + trCol8 + " INTEGER, " +
                trCol9 + "INTEGER, " + trCol10 + " INTEGER, " + trCol11 + " INTEGER, " + trCol12 + " INTEGER)");

        queries.add("CREATE TABLE IF NOT EXISTS " + A2P_TABLE_NAME  + " (" +
                "id INTEGER PRIMARY KEY, " + a2pCol0 + " INTEGER, " + a2pCol1 + " INTEGER)");

        queries.add("CREATE TABLE IF NOT EXISTS " + P2S_TABLE_NAME  + " (" +
                "id INTEGER PRIMARY KEY, " + p2sCol0 + " INTEGER, " + p2sCol1 + " INTEGER)");


        queries.add("CREATE TABLE IF NOT EXISTS " + BP2A_TABLE_NAME  + " (" +
                "id INTEGER PRIMARY KEY, " + bp2aCol0 + " INTEGER, " + bp2aCol1 + " INTEGER)");

        for(String q: queries){
//            System.out.println(q);
            db.execSQL(q);
        }
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

    public void printAllTables(){
        for(String s: table_names){
            System.out.println(getTableAsString(s));
            System.out.println("*****************************");
        }
    }
}
