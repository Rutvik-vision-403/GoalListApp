package com.example.goallist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.AddGoalList.AdapterForFetchData;
import com.example.goallist.AddGoalList.AddGoalListAdapter;
import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.SubGoal;

import java.util.ArrayList;
import java.util.List;

import static com.example.goallist.AddGoalList.ShowGoalListInMainActivityAdapter.isFromEditButton;
import static com.example.goallist.AddGoalList.ShowGoalListInMainActivityAdapter.isIsFromEditButtonForSave;


public class AddGoal extends AppCompatActivity {

    private RecyclerView addGoalList;

    private AddGoalListAdapter addGoalItem;
    private AdapterForFetchData adapterForFetchData;
    private EditText goalTitle;
    private TextView dateView;
    private CardView datePick;
    private CardView addNewSubGoal;
    private LinearLayout addGoalLayout;

    final DatabaseHelper databaseHelper = new DatabaseHelper(this);

    List<SubGoal> selectedSubGoals = new ArrayList<>();
    List<SubGoal> addNewItemInArray = new ArrayList<>();

    public static int idOfMasterGoal;
    public static int idOfMasterGoalIndex;
    public static boolean forUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        Intent getIndex = getIntent();
        idOfMasterGoal = getIndex.getIntExtra("Current master id", 0);
        idOfMasterGoalIndex = getIndex.getIntExtra("Current Master Index ID",0);


        selectedSubGoals = databaseHelper.getSubGoalWithMasterId(idOfMasterGoal);
        addNewItemInArray = databaseHelper.returnEmptySubGoals();

        goalTitle = findViewById(R.id.goalTitle);
        addGoalList = findViewById(R.id.addGoalList);
        addNewSubGoal = findViewById(R.id.addNewGoal);
        datePick = findViewById(R.id.setDate);
        dateView = findViewById(R.id.showDateTextView);
        addGoalLayout = findViewById(R.id.linearLayoutAddGoal);
        addGoalList.setLayoutManager(new LinearLayoutManager(this));


       addGoalList.getRecycledViewPool().setMaxRecycledViews(1, 0);

        if (isFromEditButton) {

            String tempDate = databaseHelper.getMasterGoal(idOfMasterGoalIndex).getEndDate();
            getSupportActionBar().setTitle("Update Goal");
            if (tempDate != null) {
                dateView.setText(tempDate);
            } else {
                dateView.setText("Select Date");
            }

        } else {
            getSupportActionBar().setTitle("Add Goal");
        }

        datePick.setOnClickListener(view -> {
            SetDateCalenderFragment setDateCalenderFragment = new SetDateCalenderFragment(AddGoal.this, dateView);
            setDateCalenderFragment.show(getSupportFragmentManager(), "Set Goal Date");
        });

        if (isFromEditButton) {
            adapterForFetchData = new AdapterForFetchData(selectedSubGoals, addGoalList, this);
            addGoalList.setAdapter(adapterForFetchData);
            addNewSubGoal.setVisibility(View.VISIBLE);
            addGoalLayout.setFocusableInTouchMode(true);
            addGoalLayout.setFocusable(true);

            adapterForFetchData.notifyDataSetChanged();
            ItemTouchHelper.Callback dragDropListener= new SubGoalMoveCallBack(this,adapterForFetchData);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragDropListener);
            itemTouchHelper.attachToRecyclerView(addGoalList);

        } else {
            addGoalItem = new AddGoalListAdapter(addNewItemInArray, addGoalList, this);
            addGoalList.setAdapter(addGoalItem);
            addNewSubGoal.setVisibility(View.VISIBLE);

            ItemTouchHelper.Callback dragDropListener= new SubGoalMoveCallBack(this,addGoalItem);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragDropListener);
            itemTouchHelper.attachToRecyclerView(addGoalList);
        }

        setDataOnAllView();

    }

    public void setDataOnAllView() {

        if (isFromEditButton) {
            goalTitle.setText(databaseHelper.getMasterGoal(idOfMasterGoalIndex).getGoalTitle());
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
        if (R.id.saveGoal == item.getItemId()) {

            if (isIsFromEditButtonForSave) {

                databaseHelper.updateDateInMasterGoal(AddGoal.idOfMasterGoalIndex, dateView.getText().toString());

                if (!goalTitle.getText().toString().isEmpty()) {
                    databaseHelper.updateGoalTitle(databaseHelper.getMasterGoal(idOfMasterGoalIndex), goalTitle.getText().toString());
                    databaseHelper.deleteAllSubGoals(idOfMasterGoal);

                    for (int insert = 0; insert < selectedSubGoals.size(); insert++) {

                        EditText editText = addGoalList.findViewHolderForAdapterPosition(insert).itemView.findViewById(R.id.addNewItem);
                        if (editText.getText().toString().isEmpty())
                            continue;
                        databaseHelper.insertSubGoals(editText.getText().toString(), idOfMasterGoal);

                    }
                    isIsFromEditButtonForSave = false;
                    forUpdate = false;
                    Intent gotoMainActivity = new Intent(this, MainActivity.class);
                    gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(gotoMainActivity);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).
                            setMessage("Please Enter Title").
                            setPositiveButton("OK", (dialogInterface, i) -> {
                            }).create();

                    alertDialog.show();
                }
            } else {

                // Check Date validation
                if ((!goalTitle.getText().toString().isEmpty())) {

                    if ((!dateView.getText().toString().equals("Select Date"))) {

                        long id = databaseHelper.insertGoal(goalTitle.getText().toString(), dateView.getText().toString());
                        databaseHelper.updateRecyclerPosition(id);

                        for (int iterate = 0; iterate < addGoalItem.getItemCount(); iterate++) {

                            EditText editText = addGoalList.findViewHolderForAdapterPosition(iterate).itemView.findViewById(R.id.addNewItem);
                            if (editText.getText().toString().isEmpty())
                                continue;
                            databaseHelper.insertSubGoals(editText.getText().toString(), id);
                        }

                        Intent gotoMainActivity = new Intent(this, MainActivity.class);
                        gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(gotoMainActivity);

                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(this).
                                setMessage("Date is Not Selected!").
                                setPositiveButton("OK", (dialogInterface, i) -> {
                                }).create();

                        alertDialog.show();
                    }


                } // Check Title validation
                else if ((!dateView.getText().toString().equals("Select Date"))) {

                    if ((!goalTitle.getText().toString().isEmpty())) {

                        long id = databaseHelper.insertGoal(goalTitle.getText().toString(), dateView.getText().toString());
                        databaseHelper.updateRecyclerPosition(id);

                        for (int iterate = 0; iterate < addGoalItem.getItemCount(); iterate++) {

                            EditText editText = addGoalList.findViewHolderForAdapterPosition(iterate).itemView.findViewById(R.id.addNewItem);
                            if (editText.getText().toString().isEmpty())
                                continue;
                            databaseHelper.insertSubGoals(editText.getText().toString(), id);
                        }
                        Intent gotoMainActivity = new Intent(this, MainActivity.class);
                        gotoMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(gotoMainActivity);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(this).
                                setMessage("Please Enter Title").
                                setPositiveButton("OK", (dialogInterface, i) -> {
                                }).create();

                        alertDialog.show();
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).
                            setMessage("Enter Title And Date First!").
                            setPositiveButton("OK", (dialogInterface, i) -> {
                            }).create();
                    alertDialog.show();
                }
            }
        }
        return true;
    }

    public void addNewGoalInList(View view) {

        if (forUpdate) {
            if (selectedSubGoals.size() == 0) {
                selectedSubGoals.add(new SubGoal());
                adapterForFetchData.notifyDataSetChanged();
            } else if (selectedSubGoals.get((selectedSubGoals.size() - 1)).getSubGoal() != null) {
                selectedSubGoals.add(new SubGoal());
                adapterForFetchData.notifyDataSetChanged();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).
                        setMessage("Please Enter Field First").
                        setPositiveButton("OK", (dialogInterface, i) -> {
                        }).setCancelable(false).create();
                alertDialog.show();
            }

        } else {
            if (addNewItemInArray.size() == 0) {
                addNewItemInArray.add(new SubGoal());
                addGoalItem.notifyDataSetChanged();
            } else if (addNewItemInArray.get((addNewItemInArray.size() - 1)).getSubGoal() != null) {
                addNewItemInArray.add(new SubGoal());
                addGoalItem.notifyDataSetChanged();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).
                        setMessage("Please Enter Field First").
                        setPositiveButton("OK", (dialogInterface, i) -> {
                        }).setCancelable(false).create();
                alertDialog.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        isIsFromEditButtonForSave = false;
        forUpdate = false;
    }
}