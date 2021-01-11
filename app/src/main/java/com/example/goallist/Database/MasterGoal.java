package com.example.goallist.Database;

public class MasterGoal {

    public static final String TABLE_NAME = "master_goal_table";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GOAL = "goal_title";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String RECYCLER_POSITION = "recycler_position";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_GOAL + " TEXT,"
                    + COLUMN_END_DATE + " TEXT,"
                    + RECYCLER_POSITION + " INTEGER"
                    + " )";

    private int id;
    private String goalTitle;
    private String endDate;
    private int recyclerPosition;

    public MasterGoal(){

    }

    public MasterGoal(int id,String goalTitle,String endDate, int recyclerPosition){

        this.id = id;
        this.goalTitle = goalTitle;
        this.endDate = endDate;
        this.recyclerPosition = recyclerPosition;

    }
    public int getId() {
        return id;
    }

    public int getRecyclerPosition(){
        return recyclerPosition;
    }

    public void setRecyclerPosition(int recyclerPosition){
        this.recyclerPosition = recyclerPosition;
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
