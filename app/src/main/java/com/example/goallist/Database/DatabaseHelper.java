package com.example.goallist.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "Goal_List_1";
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

                masterGoals.add(masterGoal);

            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();

        return masterGoals;

    }

    // get all sub goal from goal table
    public List<SubGoal> getAllSubGoalFromSubGoalTable() {

        List<SubGoal> subGoals = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + SubGoal.TABLE_NAME;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SubGoal subGoal = new SubGoal();
                subGoal.setId(cursor.getInt(cursor.getColumnIndex(SubGoal.COLUMN_ID)));
                subGoal.setSubGoal(cursor.getString(cursor.getColumnIndex(SubGoal.COLUMN_GOAL)));
                subGoal.setMasterId(cursor.getInt(cursor.getColumnIndex(SubGoal.MASTER_GOAL_ID)));

                subGoals.add(subGoal);

            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();

        return subGoals;
    }

    public MasterGoal getMasterGoal(long id) {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase
                .query(MasterGoal.TABLE_NAME
                        , new String[]{MasterGoal.COLUMN_ID
                                , MasterGoal.COLUMN_GOAL
                                , MasterGoal.COLUMN_END_DATE},
                        MasterGoal.COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MasterGoal masterGoal = new MasterGoal(cursor.getInt(cursor.getColumnIndex(MasterGoal.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(MasterGoal.COLUMN_GOAL)),
                cursor.getString(cursor.getColumnIndex(MasterGoal.COLUMN_END_DATE)));

        cursor.close();
        return masterGoal;
    }

    public SubGoal getSubGoal(long id) {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase
                .query(SubGoal.TABLE_NAME
                        , new String[]{SubGoal.COLUMN_ID
                                , SubGoal.COLUMN_GOAL
                                , SubGoal.MASTER_GOAL_ID},
                        SubGoal.COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        SubGoal subGoal = new SubGoal(cursor.getInt(cursor.getColumnIndex(SubGoal.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(SubGoal.COLUMN_GOAL)),
                cursor.getInt(cursor.getColumnIndex(SubGoal.MASTER_GOAL_ID)));

        cursor.close();
        return subGoal;

    }

    // return current all clicked master index data
    public List<SubGoal> getSubGoalDataWithPosition(int id) {

        List<SubGoal> subGoalData = new ArrayList<>();

        //String selectQuery = "SELECT " + SubGoal.COLUMN_GOAL + " FROM " + SubGoal.TABLE_NAME + " WHERE " + SubGoal.MASTER_GOAL_ID + " =? " + id;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(SubGoal.TABLE_NAME,new String[]{SubGoal.COLUMN_GOAL},
                SubGoal.MASTER_GOAL_ID + " =? ",
                new String[]{String.valueOf(id)},null,null,null);
       // Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SubGoal subGoal = new SubGoal();
                subGoal.setSubGoal(cursor.getString(cursor.getColumnIndex(SubGoal.COLUMN_GOAL)));
                subGoalData.add(subGoal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subGoalData;
    }

    // insert data into master goal and sub goal
    public long insertGoal(String masterGoalTitle, String masterGoalDate) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MasterGoal.COLUMN_GOAL, masterGoalTitle);
        contentValues.put(MasterGoal.COLUMN_END_DATE, masterGoalDate);
        long masterGoalInsertIndex = sqLiteDatabase.insert(MasterGoal.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        return masterGoalInsertIndex;
    }

    public long insertSubGoals(String subGoalData, long masterGoalId) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SubGoal.COLUMN_GOAL, subGoalData);
        contentValues.put(SubGoal.MASTER_GOAL_ID, masterGoalId);

        long subGoalInsertIndex = sqLiteDatabase.insert(SubGoal.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        return subGoalInsertIndex;

    }

    // update goal title
    public void updateGoalTitle(MasterGoal masterGoalTitle) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MasterGoal.COLUMN_GOAL, masterGoalTitle.getGoalTitle());
        sqLiteDatabase.update(MasterGoal.TABLE_NAME,
                contentValues, MasterGoal.COLUMN_ID + "=?"
                , new String[]{String.valueOf(masterGoalTitle.getId())});

    }

    // update sub goal data
    public void updateSubGoals(SubGoal subGoalData) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MasterGoal.COLUMN_GOAL, subGoalData.getSubGoal());
        sqLiteDatabase.update(MasterGoal.TABLE_NAME,
                contentValues, SubGoal.COLUMN_ID + "=?"
                , new String[]{String.valueOf(subGoalData.getId())});

    }

    // delete whole goal and sub goal from main activity
    public void deleteGoalAndSubGoal(MasterGoal masterGoal) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(MasterGoal.TABLE_NAME, MasterGoal.COLUMN_ID + "=?", new String[]{String.valueOf(masterGoal.getId())});
        // delete list element as well *** but delete element is for change view of recycler only
        //sqLiteDatabase.delete(SubGoal.TABLE_NAME,SubGoal.MASTER_GOAL_ID+"= ?",new String[]{String.valueOf(masterGoal.getId())});
        sqLiteDatabase.close();

    }

    // delete only sub goal
    public void deleteSubGoal(SubGoal subGoal) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(SubGoal.TABLE_NAME, SubGoal.COLUMN_ID + "=?", new String[]{String.valueOf(subGoal.getId())});
        sqLiteDatabase.close();

    }
}
