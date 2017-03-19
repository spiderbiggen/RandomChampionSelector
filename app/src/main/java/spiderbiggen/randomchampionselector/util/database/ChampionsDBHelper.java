package spiderbiggen.randomchampionselector.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import spiderbiggen.randomchampionselector.champion.Champion;
import spiderbiggen.randomchampionselector.champion.ChampionAttackType;
import spiderbiggen.randomchampionselector.champion.ChampionResource;
import spiderbiggen.randomchampionselector.champion.ChampionRole;
import spiderbiggen.randomchampionselector.util.database.ChampionContract.ChampionEntry;

/**
 * Created by Stefan Breetveld on 18-3-2017.
 * Part of RandomChampionSelector.
 */

public class ChampionsDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "champions.db";
    private boolean outdated = false;

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ChampionEntry.TABLE_NAME + " (" +
                    ChampionEntry._ID + " INTEGER PRIMARY KEY," +
                    ChampionEntry.COLUMN_NAME_NAME + " TEXT," +
                    ChampionEntry.COLUMN_NAME_ROLE + " TEXT," +
                    ChampionEntry.COLUMN_NAME_HEALTH + " INTEGER," +
                    ChampionEntry.COLUMN_NAME_RESOURCE + " INTEGER," +
                    ChampionEntry.COLUMN_NAME_RESOURCE_TYPE + " TEXT," +
                    ChampionEntry.COLUMN_NAME_ATTACK_TYPE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ChampionEntry.TABLE_NAME;

    public ChampionsDBHelper(Context context) {
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

    /**
     * Add a champion to the database.
     * @param champion The {@link Champion} that should be added to the database.
     * @return the _ID of the resulting database entry.
     */
    public long addChampion(Champion champion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChampionEntry.COLUMN_NAME_NAME, champion.getName());
        values.put(ChampionEntry.COLUMN_NAME_ROLE, champion.getRole().toString());
        values.put(ChampionEntry.COLUMN_NAME_HEALTH, champion.getHealth());
        values.put(ChampionEntry.COLUMN_NAME_RESOURCE, champion.getResource());
        values.put(ChampionEntry.COLUMN_NAME_RESOURCE_TYPE, champion.getResourceType().toString());
        values.put(ChampionEntry.COLUMN_NAME_ATTACK_TYPE, champion.getAttackType().toString());

        return db.insert(ChampionEntry.TABLE_NAME, null, values);
    }

    /**
     * Retrieve a List of all {@link Champion}s that are in the database.
     * @return {@link List<Champion>}
     */
    public List<Champion> getChampions(String requestedRole) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ChampionEntry.COLUMN_NAME_NAME,
                ChampionEntry.COLUMN_NAME_ROLE
        };

        String selection = ChampionEntry.COLUMN_NAME_ROLE + " LIKE ?";
        String[] selectionArgs = { requestedRole };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = ChampionEntry.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                ChampionEntry.TABLE_NAME,   // The table to query
                projection,                 // The columns to return
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                sortOrder                   // The sort order
        );

        List<Champion> champions = new ArrayList<>();

        while(cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            ChampionEntry.COLUMN_NAME_NAME
                    )
            );
            ChampionRole role = ChampionRole.valueOf(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    ChampionEntry.COLUMN_NAME_ROLE
                            )
                    ).toUpperCase()
            );

            champions.add(new Champion(name, role, 0, 0, null, null));
        }
        cursor.close();
        return champions;
    }

    public Champion getRandomChampion(String requestedRole, Champion champion) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ChampionEntry.COLUMN_NAME_NAME,
                ChampionEntry.COLUMN_NAME_ROLE,
                ChampionEntry.COLUMN_NAME_HEALTH,
                ChampionEntry.COLUMN_NAME_RESOURCE,
                ChampionEntry.COLUMN_NAME_RESOURCE_TYPE,
                ChampionEntry.COLUMN_NAME_ATTACK_TYPE
        };

        String selection = ChampionEntry.COLUMN_NAME_ROLE + " LIKE ?";
        String[] selectionArgs = { requestedRole };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = "RANDOM()";

        Cursor cursor = db.query(
                ChampionEntry.TABLE_NAME,   // The table to query
                projection,                 // The columns to return
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                sortOrder                  // The sort order
        );
        Champion rngChampion = null;
        if (rngChampion != null) {
            while (rngChampion.equals(champion) && cursor.moveToNext()) {
                rngChampion = cursorToChampion(cursor);
            }
        } else {
            if(cursor.moveToFirst()) {
                rngChampion = cursorToChampion(cursor);
            }
        }
        cursor.close();
        return rngChampion;
    }

    public Champion getChampion(String championName) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ChampionEntry.COLUMN_NAME_NAME,
                ChampionEntry.COLUMN_NAME_ROLE,
                ChampionEntry.COLUMN_NAME_HEALTH,
                ChampionEntry.COLUMN_NAME_RESOURCE,
                ChampionEntry.COLUMN_NAME_RESOURCE_TYPE,
                ChampionEntry.COLUMN_NAME_ATTACK_TYPE
        };

        String selection = ChampionEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { championName };

        // How you want the results sorted in the resulting Cursor

        Cursor cursor = db.query(
                ChampionEntry.TABLE_NAME,   // The table to query
                projection,                 // The columns to return
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                null                        // The sort order
        );
        Champion champion = null;
        if (cursor.moveToFirst()){
            champion = cursorToChampion(cursor);
        }
        cursor.close();
        return champion;
    }

    public int getMaxHealth() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ChampionEntry.COLUMN_NAME_HEALTH
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = ChampionEntry.COLUMN_NAME_HEALTH + " DESC";

        Cursor cursor = db.query(
                ChampionEntry.TABLE_NAME,   // The table to query
                projection,                 // The columns to return
                null,                  // The columns for the WHERE clause
                null,              // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                sortOrder,                       // The sort order
                "1"
        );
        int max = 0;
        if(cursor.moveToFirst()) {
            max = cursor.getInt(cursor.getColumnIndex(ChampionEntry.COLUMN_NAME_HEALTH));
        }
        cursor.close();
        return max;
    }

    public int getMinHealth() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ChampionEntry.COLUMN_NAME_HEALTH
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = ChampionEntry.COLUMN_NAME_HEALTH + " ASC";

        Cursor cursor = db.query(
                ChampionEntry.TABLE_NAME,   // The table to query
                projection,                 // The columns to return
                null,                  // The columns for the WHERE clause
                null,              // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                sortOrder,                       // The sort order
                "1"
        );
        int min = 0;
        if(cursor.moveToFirst()) {
            min = cursor.getInt(cursor.getColumnIndex(ChampionEntry.COLUMN_NAME_HEALTH));
        }
        cursor.close();
        return min;
    }

    private Champion cursorToChampion(Cursor cursor) {
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(
                        ChampionEntry.COLUMN_NAME_NAME
                )
        );
        ChampionRole role = ChampionRole.valueOf(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                ChampionEntry.COLUMN_NAME_ROLE
                        )
                ).toUpperCase()
        );
        int health = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                        ChampionEntry.COLUMN_NAME_HEALTH
                )
        );
        int resource = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                        ChampionEntry.COLUMN_NAME_RESOURCE
                )
        );
        ChampionResource resourceType = ChampionResource.valueOf(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                ChampionEntry.COLUMN_NAME_RESOURCE_TYPE
                        )
                ).toUpperCase()
        );
        ChampionAttackType attackType = ChampionAttackType.valueOf(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                ChampionEntry.COLUMN_NAME_ATTACK_TYPE
                        )
                ).toUpperCase()
        );
        return new Champion(name, role, health, resource, resourceType, attackType);
    }

    public boolean isEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String count = "SELECT count(*) FROM " + ChampionEntry.TABLE_NAME;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        mcursor.close();
        return icount == 0;
    }
}
