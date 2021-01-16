package com.example.goallist;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.goallist.Adapters.AddNewGoal;
import com.example.goallist.Adapters.FetchData;
import com.example.goallist.CallBackClasses.ItemMoveCallbackForSubGoal;
import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.SubGoal;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.goallist.Adapters.MainGoalList.isFromEditButton;
import static com.example.goallist.Adapters.MainGoalList.isFromEditButtonForSave;


public class AddGoal extends AppCompatActivity{

    // Sub goal recycler view
    private RecyclerView addGoalList;

    // Adapters for sub goals data
    private AddNewGoal addGoalItem;
    private FetchData adapterForFetchData;

    private EditText goalTitle;
    private TextView dateView;

    final DatabaseHelper databaseHelper = new DatabaseHelper(this);

    ArrayList<SubGoal> selectedSubGoals = new ArrayList<>();
    ArrayList<SubGoal> addNewItemInArray = new ArrayList<>();

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

        CardView addNewSubGoal = findViewById(R.id.addNewGoal);
        CardView datePick = findViewById(R.id.setDate);

        dateView = findViewById(R.id.showDateTextView);
       LinearLayout addGoalLayout = findViewById(R.id.linearLayoutAddGoal);
        addGoalList.setLayoutManager(new LinearLayoutManager(this));

        // TODO : remove this method after testing if functionality never change
        //addGoalList.getRecycledViewPool().setMaxRecycledViews(1, 0);

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
            SetDateCalenderFragment setDateCalenderFragment = new SetDateCalenderFragment(dateView);
            setDateCalenderFragment.show(getSupportFragmentManager(), "Set Goal Date");
        });

        if (isFromEditButton) {
            adapterForFetchData = new FetchData(selectedSubGoals, addGoalList, this);

            addGoalLayout.setFocusableInTouchMode(true);
            addGoalLayout.setFocusable(true);

            ItemTouchHelper.Callback callback =
                    new ItemMoveCallbackForSubGoal(adapterForFetchData);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(addGoalList);

            addGoalList.setAdapter(adapterForFetchData);

//            adapterForFetchData.notifyDataSetChanged();
//            ItemTouchHelper.Callback dragDropListener= new SubGoalMoveCallBack(this,adapterForFetchData);
//            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragDropListener);
//            itemTouchHelper.attachToRecyclerView(addGoalList);

        } else {
            addGoalItem = new AddNewGoal(addNewItemInArray, addGoalList, this);

            ItemTouchHelper.Callback callback =
                    new ItemMoveCallbackForSubGoal(addGoalItem);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(addGoalList);

            addGoalList.setAdapter(addGoalItem);

//            ItemTouchHelper.Callback dragDropListener= new SubGoalMoveCallBack(this,addGoalItem);
//            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragDropListener);
//            itemTouchHelper.attachToRecyclerView(addGoalList);
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

            if (isFromEditButtonForSave) {

                databaseHelper.updateDateInMasterGoal(AddGoal.idOfMasterGoalIndex, dateView.getText().toString());

                if (!goalTitle.getText().toString().isEmpty()) {
                    databaseHelper.updateGoalTitle(databaseHelper.getMasterGoal(idOfMasterGoalIndex), goalTitle.getText().toString());
                    databaseHelper.deleteAllSubGoals(idOfMasterGoal);

                    for (int insert = 0; insert < selectedSubGoals.size(); insert++) {

                        EditText editText = Objects.requireNonNull(addGoalList.findViewHolderForAdapterPosition(insert)).itemView.findViewById(R.id.addNewItem);
                        if (editText.getText().toString().isEmpty())
                            continue;
                        databaseHelper.insertSubGoals(editText.getText().toString(), idOfMasterGoal);

                    }

                    isFromEditButtonForSave = false;
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

                            EditText editText = Objects.requireNonNull(addGoalList.findViewHolderForAdapterPosition(iterate)).itemView.findViewById(R.id.addNewItem);
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

                            EditText editText = Objects.requireNonNull(addGoalList.findViewHolderForAdapterPosition(iterate)).itemView.findViewById(R.id.addNewItem);
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

        isFromEditButtonForSave = false;
        forUpdate = false;

    }

}