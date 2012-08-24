package br.com.thiagorosa.pingpongmadness.tutorial.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

    // LOGS
    private static final String TAG = "[PPMT]";
    private static final String TAG_NAME = "[DatabaseAdapter] ";

    // CONSTANTS
    public static final int STAR_NONE = 0;
    public static final int STAR_HALF = 1;
    public static final int STAR_FULL = 2;
    public static final int TYPE_TUTORIAL = 1;
    public static final int TYPE_LEVEL = 2;

    // DATABASE
    public static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper mDbHelper = null;
    private static SQLiteDatabase mDb = null;

    // LEVELS
    public static final String LEVEL_ID = "_id";					// unique id
    public static final String LEVEL_TYPE = "level_type";			// type - tutorial or normal
    public static final String LEVEL_SHOTS = "level_shots";			// total shots
    public static final String LEVEL_NAME = "level_name";			// name

    // RECORDS
    public static final String RECORD_ID = "_id";					// unique id
    public static final String RECORD_LEVEL = "level_id";			// level id
    public static final String RECORD_SCORE = "record_score";		// score
    public static final String RECORD_MISSES = "record_misses";     // total misses

    // DATABASE TABLES
    public static final String DATABASE_LEVELS_TABLE = "levels";
    public static final String DATABASE_RECORDS_TABLE = "records";

    // DATABASE CREATES
    private static final String DATABASE_LEVELS_CREATE = "CREATE TABLE " + DATABASE_LEVELS_TABLE + " (" + LEVEL_ID + " integer primary key, " + LEVEL_TYPE + " int not null, " + LEVEL_SHOTS
            + " int not null, " + LEVEL_NAME + " text not null)";
    private static final String DATABASE_RECORDS_CREATE = "CREATE TABLE " + DATABASE_RECORDS_TABLE + " (" + RECORD_ID + " integer primary key autoincrement, " + RECORD_LEVEL + " long not null, "
            + RECORD_SCORE + " long not null, " + RECORD_MISSES + " long not null)";

    // DATABASE QUERIES
    private static final String QUERY_STAR_MISSES = "SELECT " + RECORD_MISSES + " FROM " + DATABASE_RECORDS_TABLE + " WHERE " + LEVEL_TYPE + " = ?";

    /*******************************************************************************************
     *******************************************************************************************/

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, TAG_NAME + "onCreate - creating tables");
            db.execSQL(DATABASE_RECORDS_CREATE);

            Log.d(TAG, TAG_NAME + "onCreate - populating tables");
            createLevels(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, TAG_NAME + "onUpgrade - upgrading from version " + oldVersion + " to " + newVersion);
            createLevels(db);
        }

        private void createLevels(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_LEVELS_TABLE);
            db.execSQL(DATABASE_LEVELS_CREATE);

            // VERSION 001
            createLevel(db, 1, TYPE_TUTORIAL, 37, "Tutorial 1");
        }

        private static long createLevel(SQLiteDatabase db, int id, int type, int shots, String name) {
            Log.d(TAG, TAG_NAME + "createLevel(" + id + "," + type + "," + shots + "," + name + ")");

            ContentValues initialValues = new ContentValues();
            initialValues.put(LEVEL_ID, id);
            initialValues.put(LEVEL_TYPE, type);
            initialValues.put(LEVEL_SHOTS, shots);
            initialValues.put(LEVEL_NAME, name);

            return db.insert(DATABASE_LEVELS_TABLE, null, initialValues);
        }

    }

    private static void openDatabase(Context context) {
        try {
            if (mDb == null || mDb.isOpen() == false) {
                //Log.d(TAG, TAG_NAME+"openDatabase");
                mDbHelper = new DatabaseHelper(context);
                mDb = mDbHelper.getWritableDatabase();
            }
        }
        catch (SQLiteFullException e) {
            Log.e(TAG, TAG_NAME + "openDatabase - exception / sqlitefull");
        }
    }

    // fetch all levels for this type
    public static Cursor fetchLevelsByType(Context context, int type) {
        Log.d(TAG, TAG_NAME + "fetchLevelsByType(" + type + ")");
        openDatabase(context);

        return mDb.query(DATABASE_LEVELS_TABLE, new String[] { LEVEL_ID, LEVEL_TYPE, LEVEL_SHOTS, LEVEL_NAME }, LEVEL_TYPE + "=" + type, null, null, null, LEVEL_TYPE + ", " + LEVEL_NAME);
    }

    // create a record for this level, with this score and miss values
    public static long createRecord(Context context, long level, long score, long miss) {
        Log.d(TAG, TAG_NAME + "createRecord(" + level + "," + score + "," + miss + ")");
        openDatabase(context);

        ContentValues initialValues = new ContentValues();
        initialValues.put(RECORD_LEVEL, level);
        initialValues.put(RECORD_SCORE, score);
        initialValues.put(RECORD_MISSES, miss);

        return mDb.insert(DATABASE_RECORDS_TABLE, null, initialValues);
    }

    // fetch the record info for this level
    public static ContentValues fetchRecord(Context context, String level_id) {
        //Log.d(TAG, TAG_NAME + "fetchRecord(" + level_id + ")");
        openDatabase(context);

        ContentValues recordInfo = new ContentValues();
        Cursor cursor = mDb.query(true, DATABASE_RECORDS_TABLE, new String[] { RECORD_ID, RECORD_LEVEL, RECORD_SCORE, RECORD_MISSES }, RECORD_LEVEL + "=" + level_id, null, null, null, RECORD_SCORE
                + " DESC", null);

        if (cursor != null) {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                recordInfo.put(RECORD_ID, cursor.getString(cursor.getColumnIndexOrThrow(RECORD_ID)));
                recordInfo.put(RECORD_LEVEL, cursor.getString(cursor.getColumnIndexOrThrow(RECORD_LEVEL)));
                recordInfo.put(RECORD_SCORE, cursor.getString(cursor.getColumnIndexOrThrow(RECORD_SCORE)));
                recordInfo.put(RECORD_MISSES, cursor.getString(cursor.getColumnIndexOrThrow(RECORD_MISSES)));
            }
            cursor.close();
        }

        return recordInfo;
    }

    // fetch the star value for tutorial mode
    public static int fetchTutorialStar(Context context) {
        return fetchStar(context, TYPE_TUTORIAL);
    }

    // fetch the star value for level mode
    public static int fetchLevelStar(Context context) {
        return fetchStar(context, TYPE_LEVEL);
    }

    // fetch the star value for this type
    private static int fetchStar(Context context, int type) {
        openDatabase(context);

        Cursor levels = fetchLevelsByType(context, type);
        Cursor misses = mDb.rawQuery(QUERY_STAR_MISSES, new String[] { "" + type });

        long totalMisses = 0;
        int total = 0;
        int star = STAR_NONE;

        // get the total number of levels
        if (levels != null) {
            total = levels.getCount();
            levels.close();
        }

        // check how many misses happened
        if (misses != null) {
            if (misses.getCount() == total) {
                misses.moveToFirst();
                do {
                    totalMisses += misses.getInt(0);
                } while (misses.moveToNext());

                star = STAR_HALF;
                if (totalMisses == 0) {
                    star = STAR_FULL;
                }
            }
            else {
                if (misses.getCount() > 0) {
                    star = STAR_HALF;
                }
            }
            misses.close();
        }

        //Log.d(TAG, TAG_NAME + "getStar(" + type + ") - " + star);
        return star;
    }

}