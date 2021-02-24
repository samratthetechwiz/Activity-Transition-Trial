package com.example.activitytransitiontrial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private final static String TAG = "DETECT TRANSITION";
    //Database version.
    //Note: Increase the database version every-time you make changes to your table structure.
    private static final int DATABASE_VERSION = 6;

    //Database Name
    private static final String DATABASE_NAME = "activityDetails";

    //You will declare all your table names here.
    private static final String TABLE_ACTIVITY = "activity";

    private static final String KEY_ID = "_ID";
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_TRANSITION = "transition";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_DATE = "date";
    private static final String KEY_DAY_OF_WEEK = "dayOfWeek";
    private static final String KEY_BATTERY_PERCENTAGE_START = "batteryPercentageStart";
    private static final String KEY_BATTERY_PERCENTAGE_END = "batteryPercentageEnd";
    private static final String KEY_BATTERY_PERCENTAGE_CONSUMED = "batteryPercentageConsumed";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACTIVITY_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_ACTIVITY + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ACTIVITY + " TEXT, "
                + KEY_TRANSITION + " TEXT, "
                + KEY_START_TIME + " TEXT, "
                + KEY_END_TIME + " TEXT, "
                + KEY_DURATION + " TEXT, "
                + KEY_DATE + " TEXT, "
                + KEY_DAY_OF_WEEK + " TEXT, "
                + KEY_BATTERY_PERCENTAGE_START + " TEXT, "
                + KEY_BATTERY_PERCENTAGE_END + " TEXT, "
                + KEY_BATTERY_PERCENTAGE_CONSUMED + " TEXT, "
                + KEY_LATITUDE + " TEXT, "
                + KEY_LONGITUDE + " TEXT" + ")";

        //Create table query executed in sqlite
        db.execSQL(CREATE_ACTIVITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This method will be called only if there is change in DATABASE_VERSION.

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);

        // Create tables again
        onCreate(db);
    }

    // Add New Activity
    public void addActivity(ActivityModel activity) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d(TAG,"Inside addActivity");
        //Content values use KEY-VALUE pair concept
        ContentValues values = new ContentValues();
        values.put(KEY_ACTIVITY, activity.getActivity());
        values.put(KEY_TRANSITION, activity.getTransition());
        values.put(KEY_START_TIME, activity.getStartTime());
        values.put(KEY_END_TIME, activity.getEndTime());
        values.put(KEY_DURATION, activity.getDuration());
        values.put(KEY_DATE, activity.getDate());
        values.put(KEY_DAY_OF_WEEK, activity.dayOfWeek);
        values.put(KEY_BATTERY_PERCENTAGE_START, activity.getBatteryPercentageStart());
        values.put(KEY_BATTERY_PERCENTAGE_END, activity.getBatteryPercentageEnd());
        values.put(KEY_BATTERY_PERCENTAGE_CONSUMED, activity.getBatteryPercentageConsumed());
        values.put(KEY_LATITUDE, activity.getLatitude());
        values.put(KEY_LONGITUDE, activity.getLongitude());
        Log.d(TAG,"Got Values");

        long res = db.insert(TABLE_ACTIVITY, null, values);
        Log.d(TAG,"Insert Successful " + String.valueOf(res));
        db.close();
    }

    // Getting single activity details through ID
    /*public ActivityModel getActivity(int activity) {

        SQLiteDatabase db = this.getReadableDatabase();


        //You can browse to the query method to know more about the arguments.
        Cursor cursor = db.query(TABLE_ACTIVITY,
                new String[] { KEY_ACTIVITY, KEY_START_TIME, KEY_DATE, KEY_DAY_OF_WEEK, KEY_BATTERY_PERCENTAGE,
                                KEY_LATITUDE, KEY_LONGITUDE},
                KEY_ID + "=?",
                new String[] { String.valueOf(_ID) },
                null,
                null,
                null,
                null);

        if (cursor != null)
            cursor.moveToFirst();

        ActivityModel activity = new ActivityModel(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2));

        //Return Activity
        return activity;
    }*/

    // Getting All Activities
    public List<ActivityModel> getAllActivities() {
        List<ActivityModel> activityList = new ArrayList<ActivityModel>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ActivityModel activity = new ActivityModel(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getDouble(8),
                        cursor.getDouble(9),
                        cursor.getDouble(10),
                        cursor.getDouble(11),
                        cursor.getDouble(12));

                activityList.add(activity);
            } while (cursor.moveToNext());
        }

        // return activity list
        return activityList;
    }

    // Updating single student
    public int updateActivity(int prevID, ActivityModel activity) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d(TAG, "PrevID : " + String.valueOf(prevID));
        ContentValues values = new ContentValues();
        values.put(KEY_END_TIME, activity.getEndTime());
        values.put(KEY_DURATION, activity.getDuration());
        values.put(KEY_BATTERY_PERCENTAGE_END, activity.getBatteryPercentageEnd());
        values.put(KEY_BATTERY_PERCENTAGE_CONSUMED, activity.getBatteryPercentageConsumed());

        // updating student row
        return db.update(TABLE_ACTIVITY,
                values,
                KEY_ID + " = ?",
                new String[] { String.valueOf(prevID)});

    }
}
