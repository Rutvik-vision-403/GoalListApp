package com.example.goallist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.Adapters.MainGoalList;
import com.example.goallist.CallBackClasses.MainGoalMoveCallBack;
import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.MasterGoal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    // TODO : refactor this project with AOSP method
    // TODO : Show data correct when delete optimize it

    // get all data list of data from MasterGoal Table
    public final List<MasterGoal> masterGoals = new ArrayList<>();
    MainGoalList masterGoalAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Main activity recycler view for list of goals
        RecyclerView recyclerViewForListOfGoalTitle = findViewById(R.id.recyclerView);

        DatabaseHelper userDatabase = new DatabaseHelper(this);
        masterGoals.addAll(userDatabase.getAllGoalAndDateFromMasterTable());

        recyclerViewForListOfGoalTitle.setLayoutManager(new LinearLayoutManager(this));

        // Adapter for recycler view data
        masterGoalAdapter = new MainGoalList(masterGoals, this);
        recyclerViewForListOfGoalTitle.setAdapter(masterGoalAdapter);

        // Drag and drop feature inside recycler view
        ItemTouchHelper.Callback dragDropListener = new MainGoalMoveCallBack(this, masterGoalAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragDropListener);
        itemTouchHelper.attachToRecyclerView(recyclerViewForListOfGoalTitle);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_new_goal, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.filterGoal).getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Filter Goals");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                masterGoalAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                masterGoalAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.addNewItemInGoalList) {
            Intent gotoAddGoalActivity = new Intent(MainActivity.this, AddGoal.class);
            startActivity(gotoAddGoalActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    // Show notification daily morning at 9 am, temporarily not used this method for testing purpose
    public void showNotificationDailyMorning() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);

        Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()){
            searchView.setIconified(true);
        }else {
            super.onBackPressed();
        }

    }
}