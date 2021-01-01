package com.example.goallist.Database;

public class MasterGoal {

    public static final String TABLE_NAME = "master_goal_table_1";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GOAL = "goal_title";
    public static final String COLUMN_END_DATE = "end_date";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_GOAL + " TEXT,"
                    + COLUMN_END_DATE + " TEXT"
                    + " )";

    private int id;
    private String goalTitle;
    private String endDate;

    public MasterGoal(){

    }

    public MasterGoal(int id,String goalTitle,String endDate){

        this.id = id;
        this.goalTitle = goalTitle;
        this.endDate = endDate;

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoalTitle() {
        return goalTitle;
    }

    public void setGoalTitle(String goalTitle) {
        this.goalTitle = goalTitle;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
