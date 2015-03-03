package com.spstanchev.tvseries.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.spstanchev.tvseries.common.Constants;

/**
 * Created by Stefan on 2/8/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "TVSeries.db";

    // Table Create Statements
    // Table show create statement
    private static final String CREATE_TABLE_SHOWS = "CREATE TABLE " + Constants.TABLE_SHOWS +
            "(" + Constants.TAG_ID + " INTEGER PRIMARY KEY, " +
            Constants.TAG_URL + " TEXT, " +
            Constants.TAG_NAME + " TEXT NOT NULL, " +
            Constants.TAG_TYPE + " TEXT, " +
            Constants.TAG_STATUS + " TEXT , " +
            Constants.TAG_RUNTIME + " INTEGER , " +
            Constants.TAG_PREMIERED + " TEXT, " +
            Constants.TAG_RATING + Constants.TAG_AVERAGE + " REAL, " +
            Constants.TAG_NETWORK + Constants.TAG_ID + " INTEGER, " +
            Constants.TAG_NETWORK + Constants.TAG_NAME + " TEXT, " +
            Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_NAME + " TEXT, " +
            Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_CODE + " TEXT, " +
            Constants.TAG_NETWORK + Constants.TAG_COUNTRY + Constants.TAG_TIMEZONE + " TEXT, " +
            Constants.TAG_IMAGE + Constants.TAG_MEDIUM + " TEXT, " +
            Constants.TAG_IMAGE + Constants.TAG_ORIGINAL + " TEXT, " +
            Constants.TAG_SUMMARY + " TEXT, " +
            Constants.TAG_LINKS + Constants.TAG_SELF + Constants.TAG_HREF + " TEXT, " +
            Constants.TAG_LINKS + Constants.TAG_EPISODES + Constants.TAG_HREF + " TEXT, " +
            Constants.TAG_LINKS + Constants.TAG_CAST + Constants.TAG_HREF + " TEXT, " +
            Constants.TAG_LINKS + Constants.TAG_PREVIOUS_EPISODE + Constants.TAG_HREF + " TEXT, " +
            Constants.TAG_LINKS + Constants.TAG_NEXT_EPISODE + Constants.TAG_HREF + " TEXT" + ")";

    // Table episodes create statement
    private static final String CREATE_TABLE_EPISODES = "CREATE TABLE " + Constants.TABLE_EPISODES +
            "(" + Constants.TAG_ID + " INTEGER PRIMARY KEY, " +
            Constants.TAG_URL + " TEXT, " +
            Constants.TAG_NAME + " TEXT , " +
            Constants.TAG_SEASON + " INTEGER NOT NULL, " +
            Constants.TAG_NUMBER + " INTEGER NOT NULL, " +
            Constants.TAG_AIRDATE + " TEXT , " +
            Constants.TAG_AIRTIME + " TEXT , " +
            Constants.TAG_AIRSTAMP + " TEXT , " +
            Constants.TAG_RUNTIME + " INTEGER , " +
            Constants.TAG_IMAGE + Constants.TAG_MEDIUM + " TEXT , " +
            Constants.TAG_IMAGE + Constants.TAG_ORIGINAL + " TEXT , " +
            Constants.TAG_SUMMARY + " TEXT , " +
            Constants.TAG_WATCHED + " INTEGER NOT NULL, " +
            Constants.TAG_SHOW_ID + " INTEGER NOT NULL" + ")";

    // Table cast create statement
    private static final String CREATE_TABLE_CAST = "CREATE TABLE " + Constants.TABLE_CAST +
            "(" + Constants.TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Constants.TAG_PERSON + Constants.TAG_URL + " TEXT NOT NULL, " +
            Constants.TAG_PERSON + Constants.TAG_NAME + " TEXT NOT NULL, " +
            Constants.TAG_PERSON + Constants.TAG_IMAGE + Constants.TAG_MEDIUM + " TEXT NOT NULL, " +
            Constants.TAG_PERSON + Constants.TAG_IMAGE + Constants.TAG_ORIGINAL + " TEXT NOT NULL, " +
            Constants.TAG_CHARACTER + Constants.TAG_URL + " TEXT NOT NULL, " +
            Constants.TAG_CHARACTER + Constants.TAG_NAME + " TEXT NOT NULL, " +
            Constants.TAG_CHARACTER + Constants.TAG_IMAGE + Constants.TAG_MEDIUM + " TEXT NOT NULL, " +
            Constants.TAG_CHARACTER + Constants.TAG_IMAGE + Constants.TAG_ORIGINAL + " TEXT NOT NULL, " +
            Constants.TAG_SHOW_ID + " INTEGER NOT NULL" + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_SHOWS);
        db.execSQL(CREATE_TABLE_EPISODES);
        db.execSQL(CREATE_TABLE_CAST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ". Old data will be destroyed!");
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SHOWS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_EPISODES);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CAST);

        //crete new tables
        onCreate(db);
    }

}
