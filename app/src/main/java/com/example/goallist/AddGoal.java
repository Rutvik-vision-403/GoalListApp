package com.example.goallist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.AddGoalList.AdapterForFetchData;
import com.example.goallist.AddGoalList.AddGoalList;
import com.example.goallist.AddGoalList.ShowGoalListInMainActivity;
import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.MasterGoal;
import com.example.goallist.Database.SubGoal;

import java.util.ArrayList;
import java.util.List;

import static com.example.goallist.AddGoalList.ShowGoalListInMainActivity.isFromEditButton;

public class AddGoal extends AppCompatActivity {

    RecyclerView addGoalList;
    ArrayList<Integer> addNewItemInArray = new ArrayList<>();
    AddGoalList addGoalItem;
    AdapterForFetchData adapterForFetchData;
    EditText goalTitle;


    List<MasterGoal> masterGoals = new ArrayList<>();
    List<SubGoal> subGoals = new ArrayList<>();
    List<SubGoal> selectedSubGoals = new ArrayList<>();
    DatabaseHelper databaseHelper;
    int indexData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        Intent getIndex = getIntent();
        indexData = getIndex.getIntExtra(ShowGoalListInMainActivity.CURRENT_SELECTED_GOAL_INDEX_TAG, 0);

        Intent getSubGoalData = getIntent();
        selectedSubGoals = (List<SubGoal>)getSubGoalData.getSerializableExtra(ShowGoalListInMainActivity.LIST_OF_ALL_SUB_GOALS_WITH_PERTICULAR_INDEX);

        goalTitle = findViewById(R.id.goalTitle);
        addGoalList = findViewById(R.id.addGoalList);
        addGoalList.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        masterGoals.addAll(databaseHelper.getAllGoalAndDateFromMasterTable());
        subGoals.addAll(databaseHelper.getAllSubGoalFromSubGoalTable());

        if (isFromEditButton){
            adapterForFetchData = new AdapterForFetchData(selectedSubGoals, this);
            addGoalList.setAdapter(adapterForFetchData);
        }else {
            addGoalItem = new AddGoalList(addNewItemInArray, this);
            addGoalList.setAdapter(addGoalItem);
        }

        setDataOnAllView();

    }

    public void setDataOnAllView() {

        if (ShowGoalListInMainActivity.isFromEditButton) {
            goalTitle.setText(masterGoals.get(indexData).getGoalTitle());
            isFromEditButton = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_goal_calender_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.setDate == item.getItemId()) {
            SetDateCalenderFragment setDateCalenderFragment = new SetDateCalenderFragment();
            setDateCalenderFragment.show(getSupportFragmentManager(), "Set Goal Date");
        } else if (R.id.savaGoal == item.getItemId()) {

            long id = databaseHelper.insertGoal(goalTitle.getText().toString(), "1-Jan-2021");

            MasterGoal masterGoal = databaseHelper.getMasterGoal(id);
            masterGoals.add(0, masterGoal);
            // insert goal title here

            for (int iterate = 0; iterate < addGoalItem.getItemCount(); iterate++) {

                EditText editText = addGoalList.findViewHolderForAdapterPosition(iterate).itemView.findViewById(R.id.addNewItem);

                long idSubGoalInserted = databaseHelper.insertSubGoals(editText.getText().toString(), id);

                SubGoal subGoal = databaseHelper.getSubGoal(idSubGoalInserted);
                subGoals.add(0, subGoal);
                // insert sub goal here
            }

            Intent gotoMainActivity = new Intent(this, MainActivity.class);
            gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gotoMainActivity);

        }
        return true;
    }

    public void addNewGoalInList(View view) {

        addNewItemInArray.add(1);
        addGoalItem.notifyDataSetChanged();


    }
}