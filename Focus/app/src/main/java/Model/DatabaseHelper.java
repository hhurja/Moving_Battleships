package Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HunterHurja on 10/17/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "id_table";
    private static final String Col0 = "ID";
    private static final String Col1 = "numProfilesCreated";
    private static final String Col2 = "numAppsCreated";
    private static final String Col3 = "numSchedulesCreated";


    public DatabaseHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        System.out.println("Creating table");
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT), " +
                Col0 + ", "+ Col1 + ", " + Col2 + ", " + Col3;
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqldb, int i, int j){

    }

    public boolean addData(String c1, String c2, String c3){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Col1, c1);
        cv.put(Col2, c2);
        cv.put(Col3, c3);


        System.out.println("Adding item: " + c1+" "+c2+" "+c3 + " to table: " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, cv);

        if(result == -1) return false;

        return true;

    }


}
