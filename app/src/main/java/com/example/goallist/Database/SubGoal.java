package com.example.goallist.Database;

import java.io.Serializable;

public class SubGoal implements Serializable {

    public static final String TABLE_NAME = "sub_goal_table_1";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GOAL = "sub_goal_title";
    public static final String MASTER_GOAL_ID = "master_goal_id";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_GOAL + " TEXT,"
                    + MASTER_GOAL_ID + " INTEGER"
                    + " )";

    private int id;
    private String subGoal;
    private int masterId;

    public SubGoal(){

    }

    public SubGoal(int id,String subGoal,int masterId){
        this.id = id;
        this.subGoal = subGoal;
        this.masterId = masterId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubGoal() {
        return subGoal;
    }

    public void setSubGoal(String subGoal) {
        this.subGoal = subGoal;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }
}
