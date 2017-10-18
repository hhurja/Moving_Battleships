package Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

/**
 * Created by HunterHurja on 10/17/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;

    private static final String ID_TABLE_NAME = "id_table";
    private static final String Col0 = "ID";
    private static final String Col1 = "numProfilesCreated";
    private static final String Col2 = "numAppsCreated";
    private static final String Col3 = "numSchedulesCreated";


    public DatabaseHelper(Context context){
        super(context, ID_TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        System.out.println("Creating table");
        String createTable = "CREATE TABLE IF NOT EXISTS " + ID_TABLE_NAME  + " (" +
                Col0 + " INTEGER PRIMARY KEY, "+ Col1 + " INTEGER, " + Col2 + " INTEGER, " + Col3 + " INTEGER)";
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
        cv.put(Col1, c1);
        cv.put(Col2, c2);
        cv.put(Col3, c3);


        System.out.println("Adding item: " + c1+" "+c2+" "+c3 + " to table: " + ID_TABLE_NAME);

        long result = db.insert(ID_TABLE_NAME, null, cv);

        if(result == -1) return false;

        return true;

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
