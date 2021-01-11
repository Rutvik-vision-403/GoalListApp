package com.example.goallist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.AddGoalList.ShowGoalListInMainActivityAdapter;
import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.MasterGoal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // TODO : problem delete notes
    public final List<MasterGoal> masterGoals = new ArrayList<>();
    DatabaseHelper userDatabase;
    RecyclerView recyclerViewForListOfGoalTitle;
    ShowGoalListInMainActivityAdapter masterGoalAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewForListOfGoalTitle = findViewById(R.id.recyclerView);

        userDatabase = new DatabaseHelper(this);
        masterGoals.addAll(userDatabase.getAllGoalAndDateFromMasterTable());

        recyclerViewForListOfGoalTitle.setLayoutManager(new LinearLayoutManager(this));
        masterGoalAdapter = new ShowGoalListInMainActivityAdapter(recyclerViewForListOfGoalTitle,masterGoals,this);
        recyclerViewForListOfGoalTitle.setAdapter(masterGoalAdapter);

        ItemTouchHelper.Callback dragDropListener= new ItemMoveCallBack(this,masterGoalAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragDropListener);
        itemTouchHelper.attachToRecyclerView(recyclerViewForListOfGoalTitle);

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

    public void showNotificationDailyMorning(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);

        Intent intent = new Intent(getApplicationContext(),MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        alarmManager.cancel(pendingIntent);
    }


}