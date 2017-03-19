package spiderbiggen.randomchampionselector.util.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import spiderbiggen.randomchampionselector.util.database.ChampionContract.AbilityEntry;

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 */

public class AbilitiesDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "abilities.db";
    private boolean outdated = false;

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AbilityEntry.TABLE_NAME + " (" +
                    AbilityEntry._ID + " INTEGER PRIMARY KEY," +
                    AbilityEntry.COLUMN_NAME_NAME + " TEXT," +
                    AbilityEntry.COLUMN_NAME_COST + " TEXT," +
                    AbilityEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    AbilityEntry.COLUMN_NAME_COOLDOWN + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AbilityEntry.TABLE_NAME;

    public AbilitiesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
        outdated = true;
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean isEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String count = "SELECT count(*) FROM " + AbilityEntry.TABLE_NAME;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        mcursor.close();
        return icount == 0;
    }
}
