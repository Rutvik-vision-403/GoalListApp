package com.example.goallist.CallBackClasses;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goallist.Database.DatabaseHelper;
import com.example.goallist.Database.MasterGoal;
import com.example.goallist.DragDropInterfaces.DragDropCallBacks;

import java.util.List;

import static com.example.goallist.Adapters.MainGoalList.masterGoalsWithDragData;

public class MainGoalMoveCallBack extends ItemTouchHelper.Callback {

    DragDropCallBacks itemMoveCallBack;
    Context context;
    DatabaseHelper databaseHelper;


    public MainGoalMoveCallBack(Context context, DragDropCallBacks callBack) {
        this.itemMoveCallBack = callBack;
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int movementFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

        return makeMovementFlags(movementFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

        itemMoveCallBack.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        List<MasterGoal> masterGoals = databaseHelper.getAllGoalAndDateFromMasterTable();

        for (int i = 0; i < masterGoalsWithDragData.size(); i++) {
           databaseHelper.updateMasterTableWithDragEvent(masterGoalsWithDragData.get(i).getRecyclerPosition(),masterGoalsWithDragData.get(i).getGoalTitle(),
                    masterGoalsWithDragData.get(i).getEndDate(), masterGoals.get(i).getId());

        }

    }
}
