package com.example.goallist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.AddGoalList.AddGoalSubTaskWhenExpand;
import com.example.goallist.AddGoalList.ShowGoalListInMainActivity;
import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.MasterGoal;
import com.example.goallist.Database.SubGoal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /*
    *   Left : delete child note when parent delete
    *        : add date in database & change date color with time
    *        : update data and check is data change or not
    *        : Solve index problem
    * */

    List<MasterGoal> masterGoals = new ArrayList<>();
    List<SubGoal> subGoals = new ArrayList<>();
    DatabaseHelper userDatabase;


    RecyclerView recyclerViewForListOfGoalTitle;
    //RecyclerView recyclerViewForSubGoals;
    ShowGoalListInMainActivity masterGoalAdapter;
    AddGoalSubTaskWhenExpand subGoalAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllViewReference();

        userDatabase = new DatabaseHelper(this);

        masterGoals.addAll(userDatabase.getAllGoalAndDateFromMasterTable());
        subGoals.addAll(userDatabase.getAllSubGoalFromSubGoalTable());


        recyclerViewForListOfGoalTitle.setLayoutManager(new LinearLayoutManager(this));
        //recyclerViewForSubGoals.setLayoutManager(new LinearLayoutManager(this));

        masterGoalAdapter = new ShowGoalListInMainActivity(masterGoals,subGoals,this);
        subGoalAdapter = new AddGoalSubTaskWhenExpand(subGoals,this);

        recyclerViewForListOfGoalTitle.setAdapter(masterGoalAdapter);
       // recyclerViewForSubGoals.setAdapter(subGoalAdapter);

    }

    private void getAllViewReference(){

        recyclerViewForListOfGoalTitle = findViewById(R.id.recyclerView);

        View layout = getLayoutInflater().inflate(R.layout.main_goal_item,null);
        //recyclerViewForSubGoals = layout.findViewById(R.id.addSubTaskHere);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_new_goal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.addNewItemInGoalList){
            Intent gotoAddGoalActivity = new Intent(MainActivity.this,AddGoal.class);
            startActivity(gotoAddGoalActivity);

        }
        return super.onOptionsItemSelected(item);
    }

}