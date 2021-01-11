package com.example.goallist.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "Goal_List_v2";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(MasterGoal.CREATE_TABLE);
        sqLiteDatabase.execSQL(SubGoal.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MasterGoal.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SubGoal.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    // get all goal and date from master table
    public List<MasterGoal> getAllGoalAndDateFromMasterTable() {


        List<MasterGoal> masterGoals = new ArrayList<>();
        String selectQuery = " SELECT * FROM " + MasterGoal.TABLE_NAME;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MasterGoal masterGoal = new MasterGoal();
                masterGoal.setId(cursor.getInt(cursor.getColumnIndex(MasterGoal.COLUMN_ID)));
                masterGoal.setGoalTitle(cursor.getString(cursor.getColumnIndex(MasterGoal.COLUMN_GOAL)));
                masterGoal.setEndDate(cursor.getString(cursor.getColumnIndex(MasterGoal.COLUMN_END_DATE)));
                masterGoal.setRecyclerPosition(cursor.getInt(cursor.getColumnIndex(MasterGoal.RECYCLER_POSITION)));

                masterGoals.add(masterGoal);

            } while (cursor.moveToNext());
        }

        cursor.close();
         //sqLiteDatabase.close();

        return masterGoals;

    }

    public List<SubGoal> getAllSubGoals() {

        List<SubGoal> subGoals = new ArrayList<>();
        String getAllData = " SELECT * FROM " + SubGoal.TABLE_NAME;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(getAllData, null);

        if (cursor.moveToFirst()) {
            do {
                SubGoal subGoal = new SubGoal();
                subGoal.setId(cursor.getInt(cursor.getColumnIndex(SubGoal.COLUMN_ID)));
                subGoal.setSubGoal(cursor.getString(cursor.getColumnIndex(SubGoal.COLUMN_GOAL)));
                subGoal.setMasterId(cursor.getInt(cursor.getColumnIndex(SubGoal.MASTER_GOAL_ID)));

                subGoals.add(subGoal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subGoals;
    }

    public MasterGoal getMasterGoal(long id) {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        // TODO : problem here adapter position always give in 1 to 4 and database table
        //  id have dif value when item delete at that time id not match
        Cursor cursor = sqLiteDatabase
                .query(MasterGoal.TABLE_NAME
                        , new String[]{MasterGoal.COLUMN_ID
                                , MasterGoal.COLUMN_GOAL
                                , MasterGoal.COLUMN_END_DATE, MasterGoal.RECYCLER_POSITION},
                        MasterGoal.COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MasterGoal masterGoal = new MasterGoal(cursor.getInt(cursor.getColumnIndex(MasterGoal.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(MasterGoal.COLUMN_GOAL)),
                cursor.getString(cursor.getColumnIndex(MasterGoal.COLUMN_END_DATE)), cursor.getInt(cursor.getColumnIndex(MasterGoal.RECYCLER_POSITION)));

        cursor.close();
         //sqLiteDatabase.close();
        return masterGoal;
    }

    public List<SubGoal> getSubGoalWithMasterId(long id) {
        List<SubGoal> subGoals = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase
                .query(SubGoal.TABLE_NAME
                        , new String[]{SubGoal.COLUMN_ID
                                , SubGoal.COLUMN_GOAL
                                , SubGoal.MASTER_GOAL_ID},
                        SubGoal.MASTER_GOAL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    SubGoal subGoal = new SubGoal();
                    subGoal.setId(cursor.getInt(cursor.getColumnIndex(SubGoal.COLUMN_ID)));
                    subGoal.setSubGoal(cursor.getString(cursor.getColumnIndex(SubGoal.COLUMN_GOAL)));
                    subGoal.setMasterId(cursor.getInt(cursor.getColumnIndex(SubGoal.MASTER_GOAL_ID)));

                    subGoals.add(subGoal);

                } while (cursor.moveToNext());
            }

        cursor.close();
        // sqLiteDatabase.close();

        return subGoals;
    }

    // insert data into master goal and sub goal
    public long insertGoal(String masterGoalTitle, String endDate) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MasterGoal.COLUMN_GOAL, masterGoalTitle);
        contentValues.put(MasterGoal.COLUMN_END_DATE, endDate);
        // sqLiteDatabase.close();
        return sqLiteDatabase.insert(MasterGoal.TABLE_NAME, null, contentValues);
    }

    public void insertSubGoals(String subGoalData, long masterGoalId) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SubGoal.COLUMN_GOAL, subGoalData);
        contentValues.put(SubGoal.MASTER_GOAL_ID, masterGoalId);

         // sqLiteDatabase.close();
        sqLiteDatabase.insert(SubGoal.TABLE_NAME, null, contentValues);

    }

    // update goal title
    public void updateGoalTitle(MasterGoal masterGoalTitle, String updatedValue) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MasterGoal.COLUMN_GOAL, updatedValue);
        sqLiteDatabase.update(MasterGoal.TABLE_NAME,
                contentValues, MasterGoal.COLUMN_ID + "=?"
                , new String[]{String.valueOf(masterGoalTitle.getId())});

       //  sqLiteDatabase.close();
    }

    public void updateRecyclerPosition(long idOfMasterGoal) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MasterGoal.RECYCLER_POSITION, idOfMasterGoal);
        sqLiteDatabase.update(MasterGoal.TABLE_NAME, contentValues, MasterGoal.COLUMN_ID + "=?", new String[]{String.valueOf(idOfMasterGoal)});


    }

    public void updateRecyclerPositionValue(int recyclerCurrentPosition, int previousPosition) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MasterGoal.RECYCLER_POSITION, recyclerCurrentPosition);
        sqLiteDatabase.update(MasterGoal.TABLE_NAME, contentValues, MasterGoal.COLUMN_ID + "=?", new String[]{String.valueOf(previousPosition)});

    }

    public void updateSubGoalMasterId(List<Integer> oldId, List<Integer> newId) {



        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        // contentValues1.put(SubGoal.MASTER_GOAL_ID,newId);

        sqLiteDatabase.update(SubGoal.TABLE_NAME, contentValues1, SubGoal.MASTER_GOAL_ID + "=?", new String[]{String.valueOf(oldId)});
    }

    public void updateDateInMasterGoal(int id, String selectedDate) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MasterGoal.COLUMN_END_DATE, selectedDate);

        sqLiteDatabase.update(MasterGoal.TABLE_NAME, contentValues, MasterGoal.COLUMN_ID + "=?", new String[]{String.valueOf(id)});

       //  sqLiteDatabase.close();
    }

    public void updateMasterTableWithDragEvent(int recyclerPosition, String goalTitle, String endDate, int prevGoalId) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MasterGoal.COLUMN_GOAL, goalTitle);
        contentValues.put(MasterGoal.COLUMN_END_DATE, endDate);
        contentValues.put(MasterGoal.RECYCLER_POSITION, recyclerPosition);

        sqLiteDatabase.update(MasterGoal.TABLE_NAME, contentValues, MasterGoal.COLUMN_ID + "=?", new String[]{String.valueOf(prevGoalId)});
       // sqLiteDatabase.close();

    }

    // delete whole goal and sub goal from main activity
    public void deleteGoalAndSubGoal(MasterGoal masterGoal) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(MasterGoal.TABLE_NAME, MasterGoal.COLUMN_ID + "=?", new String[]{String.valueOf(masterGoal.getId())});
        sqLiteDatabase.delete(SubGoal.TABLE_NAME, SubGoal.MASTER_GOAL_ID + "=?", new String[]{String.valueOf(masterGoal.getRecyclerPosition())});
        //   sqLiteDatabase.close();

    }

    public void deleteAllSubGoals(int masterId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(SubGoal.TABLE_NAME, SubGoal.MASTER_GOAL_ID + "=?", new String[]{String.valueOf(masterId)});
       //   sqLiteDatabase.close();
    }

    public List<SubGoal> returnEmptySubGoals() {
        return new ArrayList<>();
    }

    public List<SubGoal> getSubGoalWithRecyclerPosition(int rclcPosition) {

        List<SubGoal> subGoals = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(SubGoal.TABLE_NAME,
                new String[]{SubGoal.COLUMN_ID, SubGoal.COLUMN_GOAL,
                        SubGoal.MASTER_GOAL_ID}, SubGoal.MASTER_GOAL_ID + "=?", new String[]{String.valueOf(rclcPosition)}, null, null, null);

            if (cursor.moveToFirst()){
                do {
                    SubGoal subGoal = new SubGoal();
                    subGoal.setId(cursor.getInt(cursor.getColumnIndex(SubGoal.COLUMN_ID)));
                    subGoal.setSubGoal(cursor.getString(cursor.getColumnIndex(SubGoal.COLUMN_GOAL)));
                    subGoal.setMasterId(cursor.getInt(cursor.getColumnIndex(SubGoal.MASTER_GOAL_ID)));

                    subGoals.add(subGoal);
                } while (cursor.moveToNext());
            }

            cursor.close();

        return subGoals;
    }
}
